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
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class ForInStatement extends Statement {
  @Nonnull
  public final Either<VariableDeclaration, Expression> left;
  @Nonnull
  public final Expression right;
  @Nonnull
  public final Statement body;

  public ForInStatement(@Nonnull Expression left, @Nonnull Expression right, @Nonnull Statement body) {
    super();
    this.left = Either.right(left);
    this.right = right;
    this.body = body;
  }

  public ForInStatement(@Nonnull VariableDeclaration left, @Nonnull Expression right, @Nonnull Statement body) {
    super();
    this.left = Either.left(left);
    this.right = right;
    this.body = body;
  }

  public ForInStatement(
      @Nonnull Either<VariableDeclaration, Expression> left,
      @Nonnull Expression right,
      @Nonnull Statement body) {
    super();
    this.left = left;
    this.right = right;
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
    Branch leftBranch = new Branch(BranchType.LEFT);
    Branch rightBranch = new Branch(BranchType.RIGHT);
    Branch bodyBranch = new Branch(BranchType.BODY);
    return reducer.reduceForInStatement(this, path, this.left.map(x -> x.reduce(reducer, path.cons(leftBranch)),
            x -> x.reduce(reducer, path.cons(leftBranch))), this.right.reduce(reducer, path.cons(rightBranch)),
        this.body.reduce(reducer, path.cons(bodyBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case LEFT:
      return Maybe.<Node>just(this.left.either(x -> x, x -> x));
    case RIGHT:
      return Maybe.<Node>just(this.right);
    case BODY:
      return Maybe.<Node>just(this.body);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Either<VariableDeclaration, Expression> left = this.left;
    Expression right = this.right;
    Statement body = this.body;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case LEFT:
        left = child instanceof VariableDeclaration ? Either.left((VariableDeclaration) child) : Either.right(
            (Expression) child);
        break;
      case RIGHT:
        right = (Expression) child;
        break;
      case BODY:
        body = (Statement) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new ForInStatement(left, right, body);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ForInStatement &&
        this.left.equals(((ForInStatement) object).left) &&
        this.right.equals(((ForInStatement) object).right) &&
        this.body.equals(((ForInStatement) object).body);
  }
}
