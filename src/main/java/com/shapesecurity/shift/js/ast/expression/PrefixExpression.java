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

package com.shapesecurity.shift.js.ast.expression;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.ast.operators.PrefixOperator;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class PrefixExpression extends UnaryExpression {
  @Nonnull
  public final PrefixOperator operator;

  public PrefixExpression(@Nonnull PrefixOperator operator, @Nonnull Expression operand) {
    super(operand);
    this.operator = operator;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.PREFIX;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.PrefixExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PrefixExpression && this.operator.equals(((PrefixExpression) object).operator) &&
        this.operand.equals(((PrefixExpression) object).operand);
  }

  @Nonnull
  public PrefixExpression setOperator(@Nonnull PrefixOperator operator) {
    return new PrefixExpression(operator, operand);
  }

  @Nonnull
  public PrefixExpression setOperand(@Nonnull Expression operand) {
    return new PrefixExpression(operator, operand);
  }
}
