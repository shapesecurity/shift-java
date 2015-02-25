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
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.path.Branch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Scope {

  @NotNull
  public final Node astNode;
  @NotNull
  public final HashTable<String, HashTable<ImmutableList<Branch>, Reference>> through;
  @NotNull
  public final ImmutableList<Scope> children;
  @NotNull
  public final Type type;
  public final boolean dynamic;
  protected final Map<String, Variable> variables = new LinkedHashMap<>();
  @NotNull
  public final ImmutableList<Variable> blockScopedTiedVar;

  Scope(
      @NotNull ImmutableList<Scope> children,
      @NotNull ImmutableList<Variable> variables,
      @NotNull ImmutableList<Variable> blockScopedTiedVar,
      @NotNull HashTable<String, HashTable<ImmutableList<Branch>, Reference>> through,
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
  protected ImmutableList<Variable> findVariables(@NotNull final Identifier identifier) {
    ImmutableList<Variable> result = findVariablesHelper(identifier, true, true);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariables(identifier));
    }
    return result;
  }

  @NotNull
  protected ImmutableList<Variable> findVariablesDeclaredBy(@NotNull final Identifier identifier) {
    ImmutableList<Variable> result = findVariablesHelper(identifier, false, true);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariablesDeclaredBy(identifier));
    }
    return result;
  }

  @NotNull
  protected ImmutableList<Variable> findVariablesReferencedBy(@NotNull final Identifier identifier) {
    ImmutableList<Variable> result = findVariablesHelper(identifier, true, false);
    if (result.isEmpty()) {
      return this.children.bind(scope -> scope.findVariablesReferencedBy(identifier));
    }
    return result;
  }

  @NotNull
  private ImmutableList<Variable> findVariablesHelper(
      @NotNull final Identifier identifier,
      boolean lookInReferences,
      boolean lookInDeclarations) {
    for (Variable v : this.variables.values()) {
      if (lookInReferences) {
        if (v.references.find(p -> p.b.node == identifier).isJust()) {
          return ImmutableList.list(v);
        }
      }
      if (lookInDeclarations) {
        if (v.declarations.find(p -> p.b.node == identifier).isJust()) {
          return ImmutableList.list(v);
        }
      }
    }
    return ImmutableList.nil();
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
