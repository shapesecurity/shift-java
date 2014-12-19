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

import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class TryFinallyStatement extends Statement {
  @Nonnull
  public final Block body;
  @Nonnull
  public final Maybe<CatchClause> catchClause;
  @Nonnull
  public final Block finalizer;

  public TryFinallyStatement(@Nonnull Block body, @Nonnull Maybe<CatchClause> catchClause, @Nonnull Block finalizer) {
    super();
    this.body = body;
    this.catchClause = catchClause;
    this.finalizer = finalizer;
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
    return Type.TryFinallyStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TryFinallyStatement &&
        this.body.equals(((TryFinallyStatement) object).body) &&
        this.catchClause.equals(((TryFinallyStatement) object).catchClause) &&
        this.finalizer.equals(((TryFinallyStatement) object).finalizer);
  }

  @Nonnull
  public Block getBody() {
    return body;
  }

  @Nonnull
  public Maybe<CatchClause> getCatchClause() {
    return catchClause;
  }

  @Nonnull
  public Block getFinalizer() {
    return finalizer;
  }

  @Nonnull
  public TryFinallyStatement setBody(@Nonnull Block body) {
    return new TryFinallyStatement(body, catchClause, finalizer);
  }

  public TryFinallyStatement setCatchClause(@Nonnull Maybe<CatchClause> catchClause) {
    return new TryFinallyStatement(body, catchClause, finalizer);
  }

  public TryFinallyStatement setFinalizer(@Nonnull Block finalizer) {
    return new TryFinallyStatement(body, catchClause, finalizer);
  }
}
