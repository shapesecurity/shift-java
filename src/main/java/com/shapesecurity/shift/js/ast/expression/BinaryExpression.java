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
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.ast.operators.BinaryOperator;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class BinaryExpression extends Expression {
  @Nonnull
  public final BinaryOperator operator;
  @Nonnull
  public final Expression left;
  @Nonnull
  public final Expression right;

  public BinaryExpression(@Nonnull BinaryOperator operator, @Nonnull Expression left, @Nonnull Expression right) {
    super();

    this.operator = operator;
    this.right = right;
    this.left = left;
  }

  @Nonnull
  public Precedence getPrecedence() {
    return this.operator.getPrecedence();
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
    return Type.BinaryExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BinaryExpression && this.operator.equals(((BinaryExpression) object).operator) &&
           this.left.equals(((BinaryExpression) object).left) &&
           this.right.equals(((BinaryExpression) object).right);
  }

  @Nonnull
  public BinaryOperator getOperator() {
    return operator;
  }

  @Nonnull
  public Expression getLeft() {
    return left;
  }

  @Nonnull
  public Expression getRight() {
    return right;
  }

  @Nonnull
  public BinaryExpression setOperator(@Nonnull BinaryOperator operator) {
    return new BinaryExpression(operator, left, right);
  }

  @Nonnull
  public BinaryExpression setLeft(@Nonnull Expression left) {
    return new BinaryExpression(operator, left, right);
  }

  @Nonnull
  public BinaryExpression setRight(@Nonnull Expression right) {
    return new BinaryExpression(operator, left, right);
  }
}
