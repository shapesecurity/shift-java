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

package com.shapesecurity.laserbat.js.ast.expression;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.Node;
import com.shapesecurity.laserbat.js.ast.ReplacementChild;
import com.shapesecurity.laserbat.js.ast.operators.PostfixOperator;
import com.shapesecurity.laserbat.js.ast.operators.Precedence;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.path.BranchType;
import com.shapesecurity.laserbat.js.visitor.ReducerP;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class PostfixExpression extends UnaryExpression {
  @Nonnull
  public final PostfixOperator operator;

  public PostfixExpression(@Nonnull PostfixOperator operator, @Nonnull Expression operand) {
    super(operand);
    this.operator = operator;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.POSTFIX;
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
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch operandBranch = new Branch(BranchType.OPERAND);
    return reducer.reducePostfixExpression(this, path, this.operand.reduce(reducer, path.cons(operandBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case OPERAND:
      return Maybe.<Node>just(this.operand);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression operand = this.operand;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case OPERAND:
        operand = (Expression) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new PostfixExpression(operator, operand);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PostfixExpression && this.operand.equals(((PostfixExpression) object).operand) &&
        this.operator.equals(((PostfixExpression) object).operator);
  }
}
