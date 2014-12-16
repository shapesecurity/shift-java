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
import com.shapesecurity.shift.js.ast.operators.Assignment;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class AssignmentExpression extends Expression {
  @Nonnull
  public final Assignment operator;
  @Nonnull
  public final Expression binding;
  @Nonnull
  public final Expression expression;

  public AssignmentExpression(
      @Nonnull Assignment operator,
      @Nonnull Expression binding,
      @Nonnull Expression expression) {
    super();

    this.operator = operator;
    this.binding = binding;
    this.expression = expression;
  }

  @Nonnull
  @Override
  public Precedence getPrecedence() {
    return Assignment.getPrecedence();
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
    Branch bindingBranch = new Branch(BranchType.BINDING);
    Branch expressionBranch = new Branch(BranchType.EXPRESSION);
    return reducer.reduceAssignmentExpression(this, path, this.binding.reduce(reducer, path.cons(bindingBranch)),
        this.expression.reduce(reducer, path.cons(expressionBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case BINDING:
      return Maybe.<Node>just(this.binding);
    case EXPRESSION:
      return Maybe.<Node>just(this.expression);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression binding = this.binding;
    Expression expression = this.expression;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case BINDING:
        binding = (Expression) child;
        break;
      case EXPRESSION:
        expression = (Expression) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new AssignmentExpression(operator, binding, expression);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AssignmentExpression && this.operator.equals(((AssignmentExpression) object).operator) &&
        this.binding.equals(((AssignmentExpression) object).binding) &&
        this.expression.equals(((AssignmentExpression) object).expression);
  }
}
