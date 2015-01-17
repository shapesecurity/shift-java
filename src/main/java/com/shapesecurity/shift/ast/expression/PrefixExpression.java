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
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class PrefixExpression extends UnaryExpression {
  @NotNull
  public final PrefixOperator operator;

  public PrefixExpression(@NotNull PrefixOperator operator, @NotNull Expression operand) {
    super(operand);
    this.operator = operator;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.PREFIX;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.PrefixExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PrefixExpression && this.operator.equals(((PrefixExpression) object).operator) &&
        this.operand.equals(((PrefixExpression) object).operand);
  }

  @NotNull
  public PrefixExpression setOperator(@NotNull PrefixOperator operator) {
    return new PrefixExpression(operator, this.operand);
  }

  @NotNull
  public PrefixExpression setOperand(@NotNull Expression operand) {
    return new PrefixExpression(this.operator, operand);
  }
}
