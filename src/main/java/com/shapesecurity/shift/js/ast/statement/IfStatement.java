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

package com.shapesecurity.shift.js.ast.statement;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class IfStatement extends Statement {
  @Nonnull
  public final Expression test;
  @Nonnull
  public final Statement consequent;
  @Nonnull
  public final Maybe<Statement> alternate;

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent, @Nonnull Maybe<Statement> alternate) {
    super();
    this.test = test;
    this.consequent = consequent;
    this.alternate = alternate;
  }

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent, @Nonnull Statement alternate) {
    this(test, consequent, Maybe.just(alternate));
  }

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent) {
    this(test, consequent, Maybe.<Statement>nothing());
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch testBranch = new Branch(BranchType.TEST);
    Branch consequentBranch = new Branch(BranchType.CONSEQUENT);
    return reducer.reduceIfStatement(this, path, this.test.reduce(reducer, path.cons(testBranch)),
        this.consequent.reduce(reducer, path.cons(consequentBranch)), this.alternate.map(iStatement -> {
          Branch alternateBranch = new Branch(BranchType.ALTERNATE);
          return iStatement.reduce(reducer, path.cons(alternateBranch));
        }));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case TEST:
      return Maybe.<Node>just(this.test);
    case CONSEQUENT:
      return Maybe.<Node>just(this.consequent);
    case ALTERNATE:
      return this.alternate;
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression test = this.test;
    Statement consequent = this.consequent;
    Maybe<Statement> alternate = this.alternate;
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
        consequent = (Statement) child;
        break;
      case ALTERNATE:
        alternate = Maybe.<Statement>just((Statement) child);
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new IfStatement(test, consequent, alternate);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.IfStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof IfStatement &&
        this.test.equals(((IfStatement) object).test) &&
        this.consequent.equals(((IfStatement) object).consequent) &&
        this.alternate.equals(((IfStatement) object).alternate);
  }
}
