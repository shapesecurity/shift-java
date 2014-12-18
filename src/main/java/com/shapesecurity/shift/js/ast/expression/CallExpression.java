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
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class CallExpression extends LeftHandSideExpression {
  @Nonnull
  public final Expression callee;
  @Nonnull
  public final List<Expression> arguments;

  public CallExpression(@Nonnull Expression callee, @Nonnull List<Expression> arguments) {
    super();
    this.callee = callee;
    this.arguments = arguments;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.CALL;
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch calleeBranch = new Branch(BranchType.CALLEE);
    return reducer.reduceCallExpression(this, path, this.callee.reduce(reducer, path.cons(calleeBranch)),
        this.arguments.mapWithIndex((index, iExpression) -> iExpression.reduce(reducer, path.cons(new Branch(
            BranchType.ARGUMENTS, index)))));
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case CALLEE:
      return Maybe.<Node>just(this.callee);
    case ARGUMENTS:
      return this.arguments.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression callee = this.callee;
    int argumentsLength = arguments.length();
    Expression argumentsChanges[] = new Expression[argumentsLength];
    int argumentsMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case CALLEE:
        callee = (Expression) child;
        break;
      case ARGUMENTS:
        if (branch.index < argumentsLength) {
          argumentsChanges[branch.index] = (Expression) child;
          argumentsMax = Math.max(argumentsMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<Expression> arguments = Node.replaceIndex(this.arguments, argumentsMax, argumentsChanges);
    return new CallExpression(callee, arguments);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.CallExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof CallExpression &&
        this.callee.equals(((CallExpression) object).callee) &&
        this.arguments.equals(((CallExpression) object).arguments);
  }
}
