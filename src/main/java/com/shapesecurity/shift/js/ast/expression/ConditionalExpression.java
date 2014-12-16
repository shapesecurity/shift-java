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
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class ConditionalExpression extends Expression {
  @Nonnull
  public final Expression test;
  @Nonnull
  public final Expression consequent;
  @Nonnull
  public final Expression alternate;

  public ConditionalExpression(
      @Nonnull Expression test,
      @Nonnull Expression consequent,
      @Nonnull Expression alternate) {
    super();
    this.test = test;
    this.consequent = consequent;
    this.alternate = alternate;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.CONDITIONAL;
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState reduce(
      @Nonnull ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch testBranch = new Branch(BranchType.TEST);
    Branch consequentBranch = new Branch(BranchType.CONSEQUENT);
    Branch alternateBranch = new Branch(BranchType.ALTERNATE);
    return reducer.reduceConditionalExpression(this, path, this.test.reduce(reducer, path.cons(testBranch)),
        this.consequent.reduce(reducer, path.cons(consequentBranch)), this.alternate.reduce(reducer, path.cons(
            alternateBranch)));
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case TEST:
      return Maybe.<Node>just(this.test);
    case CONSEQUENT:
      return Maybe.<Node>just(this.consequent);
    case ALTERNATE:
      return Maybe.<Node>just(this.alternate);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression test = this.test;
    Expression consequent = this.consequent;
    Expression alternate = this.alternate;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case TEST:
        test = (Expression) child;
        break;
      case CONSEQUENT:
        consequent = (Expression) child;
        break;
      case ALTERNATE:
        alternate = (Expression) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new ConditionalExpression(test, consequent, alternate);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ConditionalExpression &&
        this.test.equals(((ConditionalExpression) object).test) &&
        this.consequent.equals(((ConditionalExpression) object).consequent) &&
        this.alternate.equals(((ConditionalExpression) object).alternate);
  }
}
