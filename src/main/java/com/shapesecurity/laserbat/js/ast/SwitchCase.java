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

package com.shapesecurity.laserbat.js.ast;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.path.BranchType;
import com.shapesecurity.laserbat.js.visitor.ReducerP;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class SwitchCase extends Node {
  @Nonnull
  public final Expression test;
  @Nonnull
  public final List<Statement> consequent;

  public SwitchCase(@Nonnull Expression test, @Nonnull List<Statement> consequent) {
    super();
    this.test = test;
    this.consequent = consequent;
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> SwitchCaseState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> SwitchCaseState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    final Branch testBranch = new Branch(BranchType.TEST);
    return reducer.reduceSwitchCase(this, path, this.test.reduce(reducer, path.cons(testBranch)),
        this.consequent.mapWithIndex((index, iStatement) -> iStatement.reduce(reducer, path.cons(new Branch(
            BranchType.CONSEQUENT, index)))));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case TEST:
      return Maybe.<Node>just(this.test);
    case CONSEQUENT:
      return this.consequent.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression test = this.test;
    int consequentLength = consequent.length();
    Statement consequentChanges[] = new Statement[consequentLength];
    int consequentMax = -1;
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
        if (branch.index < consequentLength) {
          consequentChanges[branch.index] = (Statement) child;
          consequentMax = Math.max(consequentMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<Statement> consequent = Node.replaceIndex(this.consequent, consequentMax, consequentChanges);
    return new SwitchCase(test, consequent);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchCase && this.test.equals(((SwitchCase) object).test) && this.consequent.equals(
        ((SwitchCase) object).consequent);
  }
}
