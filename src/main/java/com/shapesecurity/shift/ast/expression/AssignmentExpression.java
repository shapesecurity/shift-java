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
import com.shapesecurity.shift.ast.operators.Assignment;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class AssignmentExpression extends Expression {
  @NotNull
  public final Assignment operator;
  @NotNull
  public final Expression binding;
  @NotNull
  public final Expression expression;

  public AssignmentExpression(
      @NotNull Assignment operator,
      @NotNull Expression binding,
      @NotNull Expression expression) {
    super();

    this.operator = operator;
    this.binding = binding;
    this.expression = expression;
  }

  @NotNull
  public Precedence getPrecedence() {
    return Assignment.getPrecedence();
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
    return Type.AssignmentExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AssignmentExpression && this.operator.equals(((AssignmentExpression) object).operator) &&
           this.binding.equals(((AssignmentExpression) object).binding) &&
           this.expression.equals(((AssignmentExpression) object).expression);
  }

  @NotNull
  public Assignment getOperator() {
    return operator;
  }

  @NotNull
  public Expression getBinding() {
    return binding;
  }

  @NotNull
  public Expression getExpression() {
    return expression;
  }

  @NotNull
  public AssignmentExpression setOperator(@NotNull Assignment operator) {
    return new AssignmentExpression(operator, binding, expression);
  }

  @NotNull
  public AssignmentExpression setBinding(@NotNull Expression binding) {
    return new AssignmentExpression(operator, binding, expression);
  }

  @NotNull
  public AssignmentExpression setExpression(@NotNull Expression expression) {
    return new AssignmentExpression(operator, binding, expression);
  }
}
