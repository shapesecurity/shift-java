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
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class BinaryExpression extends Expression {
  @NotNull
  public final BinaryOperator operator;
  @NotNull
  public final Expression left;
  @NotNull
  public final Expression right;

  public BinaryExpression(@NotNull BinaryOperator operator, @NotNull Expression left, @NotNull Expression right) {
    super();

    this.operator = operator;
    this.right = right;
    this.left = left;
  }

  @NotNull
  public Precedence getPrecedence() {
    return this.operator.getPrecedence();
  }

  @NotNull
  @Override
  public Type type() {
    return Type.BinaryExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BinaryExpression && this.operator.equals(((BinaryExpression) object).operator) &&
           this.left.equals(((BinaryExpression) object).left) &&
           this.right.equals(((BinaryExpression) object).right);
  }

  @NotNull
  public BinaryOperator getOperator() {
    return this.operator;
  }

  @NotNull
  public Expression getLeft() {
    return this.left;
  }

  @NotNull
  public Expression getRight() {
    return this.right;
  }

  @NotNull
  public BinaryExpression setOperator(@NotNull BinaryOperator operator) {
    return new BinaryExpression(operator, this.left, this.right);
  }

  @NotNull
  public BinaryExpression setLeft(@NotNull Expression left) {
    return new BinaryExpression(this.operator, left, this.right);
  }

  @NotNull
  public BinaryExpression setRight(@NotNull Expression right) {
    return new BinaryExpression(this.operator, this.left, right);
  }
}
