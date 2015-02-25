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

package com.shapesecurity.shift.ast;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class Block extends Node {
  // ECMAScript 5 does not allow FunctionDeclarations in block statements, but no
  // implementations comply to this restriction.
  @NotNull
  public final ImmutableList<Statement> statements;

  public Block(@NotNull ImmutableList<Statement> statements) {
    super();
    this.statements = statements;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.Block;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof Block && ((Block) obj).statements.equals(this.statements);
  }

  @NotNull
  public ImmutableList<Statement> getStatements() {
    return this.statements;
  }

  @NotNull
  public Block setStatements(@NotNull ImmutableList<Statement> statements) {
    return new Block(statements);
  }
}
