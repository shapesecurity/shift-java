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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class SwitchStatement extends Statement {
  @NotNull
  public final Expression discriminant;
  @NotNull
  public final List<SwitchCase> cases;

  public SwitchStatement(@NotNull Expression discriminant, @NotNull List<SwitchCase> cases) {
    super();
    this.discriminant = discriminant;
    this.cases = cases;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState
  transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.SwitchStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatement && this.discriminant.equals(((SwitchStatement) object).discriminant) &&
        this.cases.equals(((SwitchStatement) object).cases);
  }

  @NotNull
  public Expression getDiscriminant() {
    return discriminant;
  }

  @NotNull
  public List<SwitchCase> getCases() {
    return cases;
  }

  @NotNull
  public SwitchStatement setDiscriminant(@NotNull Expression discriminant) {
    return new SwitchStatement(discriminant, cases);
  }

  @NotNull
  public SwitchStatement setCases(@NotNull List<SwitchCase> cases) {
    return new SwitchStatement(discriminant, cases);
  }
}
