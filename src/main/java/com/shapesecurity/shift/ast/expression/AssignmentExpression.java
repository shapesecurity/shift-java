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
import com.shapesecurity.shift.ast.operators.AssignmentOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class AssignmentExpression extends Expression {
  @NotNull
  public final AssignmentOperator operator;
  @NotNull
  public final Expression binding;
  @NotNull
  public final Expression expression;

  public AssignmentExpression(
      @NotNull AssignmentOperator operator,
      @NotNull Expression binding,
      @NotNull Expression expression) {
    super();

    this.operator = operator;
    this.binding = binding;
    this.expression = expression;
  }

  @NotNull
  public Precedence getPrecedence() {
    return AssignmentOperator.getPrecedence();
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
  public AssignmentOperator getOperator() {
    return this.operator;
  }

  @NotNull
  public Expression getBinding() {
    return this.binding;
  }

  @NotNull
  public Expression getExpression() {
    return this.expression;
  }

  @NotNull
  public AssignmentExpression setOperator(@NotNull AssignmentOperator operator) {
    return new AssignmentExpression(operator, this.binding, this.expression);
  }

  @NotNull
  public AssignmentExpression setBinding(@NotNull Expression binding) {
    return new AssignmentExpression(this.operator, binding, this.expression);
  }

  @NotNull
  public AssignmentExpression setExpression(@NotNull Expression expression) {
    return new AssignmentExpression(this.operator, this.binding, expression);
  }
}
