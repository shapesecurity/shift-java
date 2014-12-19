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

package com.shapesecurity.shift.js.ast;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class Block extends Node {
  // ECMAScript 5 does not allow FunctionDeclarations in block statements, but no
  // implementations comply to this restriction.
  @Nonnull
  public final List<Statement> statements;

  public Block(@Nonnull List<Statement> statements) {
    super();
    this.statements = statements;
  }

  @Nonnull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> BlockState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.Block;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof Block && ((Block) obj).statements.equals(this.statements);
  }

  @Nonnull
  public List<Statement> getStatements() {
    return statements;
  }

  @Nonnull
  public Block setStatements(@Nonnull List<Statement> statements) {
    return new Block(statements);
  }
}
