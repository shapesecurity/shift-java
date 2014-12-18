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

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class ForStatement extends Statement {
  @Nonnull
  public final Maybe<Either<VariableDeclaration, Expression>> init;
  @Nonnull
  public final Maybe<Expression> test;
  @Nonnull
  public final Maybe<Expression> update;
  @Nonnull
  public final Statement body;

  public ForStatement(
      @Nonnull Expression init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    this(Maybe.just(Either.right(init)), test, update, body);
  }

  public ForStatement(
      @Nonnull VariableDeclaration init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    this(Maybe.just(Either.left(init)), test, update, body);
  }

  public ForStatement(
      @Nonnull Maybe<Either<VariableDeclaration, Expression>> init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    super();
    this.init = init;
    this.test = test;
    this.update = update;
    this.body = body;
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
    Branch initBranch = new Branch(BranchType.INIT);
    Branch testBranch = new Branch(BranchType.TEST);
    Branch updateBranch = new Branch(BranchType.UPDATE);
    Branch bodyBranch = new Branch(BranchType.BODY);
    return reducer.reduceForStatement(
        this,
        path,
        this.init.map(x -> x.map(
            y -> y.reduce(reducer, path.cons(initBranch)),
            e -> e.reduce(reducer, path.cons(initBranch)))),
        this.test.map(e -> e.reduce(reducer, path.cons(testBranch))),
        this.update.map(e -> e.reduce(reducer, path.cons(updateBranch))),
        this.body.reduce(reducer, path.cons(bodyBranch)));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case INIT:
      return this.init.map(Either::extract);
    case TEST:
      return this.test;
    case UPDATE:
      return this.update;
    case BODY:
      return Maybe.<Node>just(this.body);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Maybe<Either<VariableDeclaration, Expression>> init = this.init;
    Maybe<Expression> test = this.test;
    Maybe<Expression> update = this.update;
    Statement body = this.body;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case INIT:
        init = Maybe.just(child instanceof VariableDeclaration ? Either.left((VariableDeclaration) child) :
                          Either.right((Expression) child));
        break;
      case TEST:
        test = Maybe.<Expression>just((Expression) child);
        break;
      case UPDATE:
        update = Maybe.<Expression>just((Expression) child);
        break;
      case BODY:
        body = (Statement) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new ForStatement(init, test, update, body);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.ForStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ForStatement &&
        this.init.equals(((ForStatement) object).init) &&
        this.test.equals(((ForStatement) object).test) &&
        this.update.equals(((ForStatement) object).update) &&
        this.body.equals(((ForStatement) object).body);
  }
}
