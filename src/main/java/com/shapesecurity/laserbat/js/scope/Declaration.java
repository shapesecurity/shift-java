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

package com.shapesecurity.laserbat.js.scope;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.path.Branch;

import javax.annotation.Nonnull;

public class Declaration {
  /**
   * AST node representing the declaration of this node
   */
  @Nonnull
  public final Identifier node;
  @Nonnull
  public final List<Branch> path;
  /**
   * Declared Variable kind
   */
  @Nonnull
  public final Kind kind;

  public Declaration(@Nonnull Identifier node, @Nonnull List<Branch> path, @Nonnull Kind kind) {
    this.node = node;
    this.path = path;
    this.kind = kind;
  }

  @Nonnull
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

    @Nonnull
    public static Kind fromVariableDeclarationKind(@Nonnull VariableDeclaration.VariableDeclarationKind kind) {
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
