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

package com.shapesecurity.shift.ast.statement;

import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class TryCatchStatement extends Statement {
  @NotNull
  public final Block body;
  @NotNull
  public final CatchClause catchClause;

  public TryCatchStatement(@NotNull Block body, @NotNull CatchClause catchClause) {
    super();
    this.body = body;
    this.catchClause = catchClause;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.TryCatchStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TryCatchStatement &&
        this.body.equals(((TryCatchStatement) object).body) &&
        this.catchClause.equals(((TryCatchStatement) object).catchClause);
  }

  @NotNull
  public Block getBody() {
    return body;
  }

  @NotNull
  public CatchClause getCatchClause() {
    return catchClause;
  }

  @NotNull
  public TryCatchStatement setBody(@NotNull Block body) {
    return new TryCatchStatement(body, catchClause);
  }

  @NotNull
  public TryCatchStatement setCatchClause(@NotNull CatchClause catchClause) {
    return new TryCatchStatement(body, catchClause);
  }
}
