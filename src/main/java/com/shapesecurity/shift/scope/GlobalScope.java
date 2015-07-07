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
/* // TODO figure out how this should work
  @Override
  @NotNull
  public ImmutableList<Variable> findVariables(@NotNull final Identifier identifier) {
    return super.findVariables(identifier);
  }

  @Override
  @NotNull
  public ImmutableList<Variable> findVariablesDeclaredBy(@NotNull final Identifier identifier) {
    return super.findVariablesDeclaredBy(identifier);
  }

  @Override
  @NotNull
  public ImmutableList<Variable> findVariablesReferencedBy(@NotNull final Identifier identifier) {
    return super.findVariablesReferencedBy(identifier);
  }
*/
}
