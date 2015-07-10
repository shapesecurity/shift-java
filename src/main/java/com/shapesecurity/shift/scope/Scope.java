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

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {

  @NotNull
  public final Node astNode;
  @NotNull
  public final HashTable<String, ImmutableList<Reference>> through;
  @NotNull
  public final ImmutableList<Scope> children;
  @NotNull
  public final Type type;
  public final boolean dynamic;
  protected final Map<String, Variable> variables = new LinkedHashMap<>();

  Scope(
      @NotNull ImmutableList<Scope> children,
      @NotNull ImmutableList<Variable> variables,
      @NotNull HashTable<String, ImmutableList<Reference>> through,
      @NotNull Type type,
      boolean isDynamic,
      @NotNull Node astNode) {
    this.children = children;
    this.through = through;
    this.type = type;
    this.astNode = astNode;

    for (Variable var : variables) {
      this.variables.put(var.name, var);
    }

    this.dynamic = isDynamic || type == Type.With;
  }

  public boolean isGlobal() {
    return (this.type == Type.Global);
  }

  @NotNull
  public final Maybe<Variable> lookupVariable(@NotNull String name) {
    return Maybe.fromNullable(this.variables.get(name));
  }

  @NotNull
  public final Collection<Variable> variables() {
    return this.variables.values();
  }

  @NotNull
  public Maybe<Variable> findVariablesDeclaredBy(@NotNull final BindingIdentifier bindingIdentifier) {
    for (Variable v : this.variables.values()) {
      if (v.declarations.exists(d -> d.node == bindingIdentifier)) {
        return Maybe.just(v);
      }
    }
    return this.children.findMap(scope -> scope.findVariablesDeclaredBy(bindingIdentifier));
  }

  @NotNull
  public Maybe<Variable> findVariablesReferencedBy(@NotNull final IdentifierExpression identifierExpression) {
    for (Variable v : this.variables.values()) {
      if (v.references.exists(p -> p.node.mapRight(ie -> ie == identifierExpression).right().orJust(false))) {
        return Maybe.just(v);
      }
    }
    return this.children.findMap(scope -> scope.findVariablesReferencedBy(identifierExpression));
  }

  @NotNull
  public Maybe<Variable> findVariablesReferencedBy(@NotNull final BindingIdentifier bindingIdentifier) {
    for (Variable v : this.variables.values()) {
      if (v.references.exists(p -> p.node.mapLeft(bi -> bi == bindingIdentifier).left().orJust(false))) {
        return Maybe.just(v);
      }
    }
    return this.children.findMap(scope -> scope.findVariablesReferencedBy(bindingIdentifier));
  }

  public enum Type {
    Global,
    Module,
    ArrowFunction,
    Function,
    FunctionName,
    Parameters,
    ParameterExpression,
    With,
    Catch,
    Block
  }
}
