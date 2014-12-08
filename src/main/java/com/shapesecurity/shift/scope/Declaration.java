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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.path.Branch;

import org.jetbrains.annotations.NotNull;

public class Declaration {
  /**
   * AST node representing the declaration of this node
   */
  @NotNull
  public final Identifier node;
  @NotNull
  public final List<Branch> path;
  /**
   * Declared Variable kind
   */
  @NotNull
  public final Kind kind;

  public Declaration(@NotNull Identifier node, @NotNull List<Branch> path, @NotNull Kind kind) {
    this.node = node;
    this.path = path;
    this.kind = kind;
  }

  @NotNull
  public final List<Branch> getPath() {
    return path;
  }

  public static enum Kind {
    Var(false),
    Const(true),
    Let(true),
    FunctionName(false),
    Param(false),
    CatchParam(true);
    public final boolean isFunctionScoped;
    public final boolean isBlockScoped;

    private Kind(boolean isBlockScoped) {
      this.isFunctionScoped = !isBlockScoped;
      this.isBlockScoped = isBlockScoped;
    }

    @NotNull
    public static Kind fromVariableDeclarationKind(@NotNull VariableDeclaration.VariableDeclarationKind kind) {
      switch (kind) {
      case Var:
        return Kind.Var;
      case Const:
        return Kind.Const;
      case Let:
        return Kind.Let;
      default:
        throw new RuntimeException("not reached");
      }
    }
  }
}
