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
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class TryFinallyStatement extends Statement {
  @Nonnull
  public final Block body;
  @Nonnull
  public final Maybe<CatchClause> catchClause;
  @Nonnull
  public final Block finalizer;

  public TryFinallyStatement(@Nonnull Block body, @Nonnull Maybe<CatchClause> catchClause, @Nonnull Block finalizer) {
    super();
    this.body = body;
    this.catchClause = catchClause;
    this.finalizer = finalizer;
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
    final List<Branch> blockPath = path.cons(new Branch(BranchType.BODY));
    final List<Branch> catchPath = path.cons(new Branch(BranchType.CATCH));
    final List<Branch> finalizerPath = path.cons(new Branch(BranchType.FINALIZER));
    return reducer.reduceTryFinallyStatement(this, path, this.body.reduce(reducer, blockPath), this.catchClause.map(
        catchClause1 -> catchClause1.reduce(reducer, catchPath)), this.finalizer.reduce(reducer, finalizerPath));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case BODY:
      return Maybe.<Node>just(this.body);
    case CATCH:
      return this.catchClause;
    case FINALIZER:
      return Maybe.just(this.finalizer);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Block block = this.body;
    Maybe<CatchClause> catchClause = this.catchClause;
    Block finalizer = this.finalizer;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case BODY:
        block = (Block) child;
        break;
      case CATCH:
        catchClause = Maybe.<CatchClause>just((CatchClause) child);
        break;
      case FINALIZER:
        finalizer = (Block) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new TryFinallyStatement(block, catchClause, finalizer);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TryFinallyStatement &&
        this.body.equals(((TryFinallyStatement) object).body) &&
        this.catchClause.equals(((TryFinallyStatement) object).catchClause) &&
        this.finalizer.equals(((TryFinallyStatement) object).finalizer);
  }
}
