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

package com.shapesecurity.shift.ast.expression;

import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.operators.PostfixOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class PostfixExpression extends UnaryExpression {
  @NotNull
  public final PostfixOperator operator;

  public PostfixExpression(@NotNull PostfixOperator operator, @NotNull Expression operand) {
    super(operand);
    this.operator = operator;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.POSTFIX;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.PostfixExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PostfixExpression && this.operand.equals(((PostfixExpression) object).operand) &&
        this.operator.equals(((PostfixExpression) object).operator);
  }

  @NotNull
  public PostfixOperator getOperator() {
    return operator;
  }

  @NotNull
  public PostfixExpression setOperator(@NotNull PostfixOperator operator) {
    return new PostfixExpression(operator, operand);
  }

  @NotNull
  public PostfixExpression setOperand(@NotNull Expression operand) {
    return new PostfixExpression(operator, operand);
  }
}
