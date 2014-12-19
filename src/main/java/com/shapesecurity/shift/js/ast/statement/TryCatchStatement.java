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

package com.shapesecurity.shift.js.ast.statement;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class TryCatchStatement extends Statement {
  @Nonnull
  public final Block body;
  @Nonnull
  public final CatchClause catchClause;

  public TryCatchStatement(@Nonnull Block body, @Nonnull CatchClause catchClause) {
    super();
    this.body = body;
    this.catchClause = catchClause;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
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

  @Nonnull
  public Block getBody() {
    return body;
  }

  @Nonnull
  public CatchClause getCatchClause() {
    return catchClause;
  }

  @Nonnull
  public TryCatchStatement setBody(@Nonnull Block body) {
    return new TryCatchStatement(body, catchClause);
  }

  @Nonnull
  public TryCatchStatement setCatchClause(@Nonnull CatchClause catchClause) {
    return new TryCatchStatement(body, catchClause);
  }
}
