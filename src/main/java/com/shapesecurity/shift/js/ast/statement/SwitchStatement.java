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

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class SwitchStatement extends Statement {
  @Nonnull
  public final Expression discriminant;
  @Nonnull
  public final List<SwitchCase> cases;

  public SwitchStatement(@Nonnull Expression discriminant, @Nonnull List<SwitchCase> cases) {
    super();
    this.discriminant = discriminant;
    this.cases = cases;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState
  transform(@Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.SwitchStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatement && this.discriminant.equals(((SwitchStatement) object).discriminant) &&
        this.cases.equals(((SwitchStatement) object).cases);
  }

  @Nonnull
  public Expression getDiscriminant() {
    return discriminant;
  }

  @Nonnull
  public List<SwitchCase> getCases() {
    return cases;
  }

  @Nonnull
  public SwitchStatement setDiscriminant(@Nonnull Expression discriminant) {
    return new SwitchStatement(discriminant, cases);
  }

  @Nonnull
  public SwitchStatement setCases(@Nonnull List<SwitchCase> cases) {
    return new SwitchStatement(discriminant, cases);
  }
}
