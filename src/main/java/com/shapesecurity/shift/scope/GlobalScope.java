/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.FunctionDeclaration;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.Node;
import org.jetbrains.annotations.NotNull;

public class GlobalScope extends Scope {
  GlobalScope(
      @NotNull ImmutableList<Scope> children,
      @NotNull ImmutableList<Variable> variables,
      @NotNull HashTable<String, ImmutableList<Reference>> through,
      @NotNull Node astNode) {
    super(children, variables, through, Type.Global, true, astNode);
    for (Pair<String, ImmutableList<Reference>> var : through.entries()) {
      this.variables.put(var.a, new Variable(var.a, var.b, ImmutableList.nil()));
    }
  }

    // these could probably be simplified, under certain assumptions, by inspecting .variables and .through instead.
    public boolean isGlobal(IdentifierExpression identifierExpression) {
      return findVariablesReferencedBy(identifierExpression).map(this::isGlobal).orJust(false);
    }

    public boolean isGlobal(BindingIdentifier bindingIdentifier) {
        return findVariablesReferencedBy(bindingIdentifier).map(this::isGlobal).orJust(false);
    }

    public boolean isGlobal(Variable variable) {
        return variables.containsValue(variable) || !isDeclared(variable);
    }

    // Because of annex B.3.3, in addition to a lexical binding (outside of scripts, which ???), functions may create a variable
    // binding for themselves. This helper gets both the (necessarily created) lexical binding and the (possible) variable binding.
    // Takes a FunctionDeclaration to ensure it is not misused, but only actually needs its binding identifier.
    // Assuming the function declaration occurs somewhere in the AST corresponding to this global scope,
    // there always will be at least one variable declared by the given function.
    // Returns (lexical, variable)
    @NotNull
    public Pair<Variable, Maybe<Variable>> findVariablesForFuncDecl(@NotNull final FunctionDeclaration func) {
        Maybe<Pair<Scope, Variable>> outerDeclaration = outermostScopeDeclaringHelper(func.name);
        assert outerDeclaration.isJust();
        Variable outerVar = outerDeclaration.just().b;
        Scope outerScope = outerDeclaration.just().a;
        Maybe<Variable> innerDeclaration = outerScope.children.findMap(scope -> scope.findVariablesDeclaredBy(func.name));
        if(innerDeclaration.isJust()) {
            return new Pair<>(innerDeclaration.just(), Maybe.just(outerVar));
        } else {
            return new Pair<>(outerVar, Maybe.nothing());
        }
    }

}
