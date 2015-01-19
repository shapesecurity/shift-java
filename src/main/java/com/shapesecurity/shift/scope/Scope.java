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
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Scope {

  @NotNull
  public final Node astNode;
  @NotNull
  // TODO: immutable data structure.
  public final HashTable<String, ProjectionTree<Reference>> through;
  @NotNull
  public final List<Scope> children;
  @NotNull
  public final Type type;
  public final boolean dynamic;
  protected final Map<String, Variable> variables = new LinkedHashMap<>();
  @NotNull
  public final List<Variable> blockScopedTiedVar;

  Scope(
      @NotNull List<Scope> children,
      @NotNull List<Variable> variables,
      @NotNull List<Variable> blockScopedTiedVar,
      @NotNull HashTable<String, ProjectionTree<Reference>> through,
      @NotNull Type type,
      boolean isDynamic,
      @NotNull Node astNode) {
    this.children = children;
    this.through = through;
    this.type = type;
    this.astNode = astNode;
    this.blockScopedTiedVar = blockScopedTiedVar;

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
  protected List<Variable> findVariables(@NotNull final Identifier identifier) {
    List<Variable> result = findVariablesHelper(identifier, true, true);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariables(identifier));
    }
    return result;
  }

  @NotNull
  protected List<Variable> findVariablesDeclaredBy(@NotNull final Identifier identifier) {
    List<Variable> result = findVariablesHelper(identifier, false, true);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariablesDeclaredBy(identifier));
    }
    return result;
  }

  @NotNull
  protected List<Variable> findVariablesReferencedBy(@NotNull final Identifier identifier) {
    List<Variable> result = findVariablesHelper(identifier, true, false);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariablesReferencedBy(identifier));
    }
    return result;
  }

  @NotNull
  private List<Variable> findVariablesHelper(@NotNull final Identifier identifier, boolean lookInReferences,
                                             boolean lookInDeclarations) {
    for (Variable v : this.variables.values()) {
      if (lookInReferences) {
        for (Reference r : v.references) {
          if (r.node == identifier) {
            return List.list(v);
          }
        }
      }
      if (lookInDeclarations) {
        for (Declaration d : v.declarations) {
          if (d.node == identifier) {
            return List.list(v);
          }
        }
      }
    }
    return List.nil();
  }

  public static enum Type {
    Global,
    Function,
    FunctionName,
    With,
    Catch,
    Block
  }
}
