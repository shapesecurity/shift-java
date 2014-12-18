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

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.ast.operators.BinaryOperator;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

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
  @Override
  public Precedence getPrecedence() {
    return this.operator.getPrecedence();
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState reduce(
      @Nonnull ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch leftBranch = new Branch(BranchType.LEFT);
    Branch rightBranch = new Branch(BranchType.RIGHT);
    return reducer.reduceBinaryExpression(this, path, this.left.reduce(reducer, path.cons(leftBranch)),
        this.right.reduce(reducer, path.cons(rightBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case LEFT:
      return Maybe.<Node>just(this.left);
    case RIGHT:
      return Maybe.<Node>just(this.right);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression left = this.left;
    Expression right = this.right;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case LEFT:
        left = (Expression) child;
        break;
      case RIGHT:
        right = (Expression) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new BinaryExpression(operator, left, right);
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
}
