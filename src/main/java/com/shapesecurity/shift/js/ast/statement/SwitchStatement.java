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
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class SwitchStatement extends Statement {
  @Nonnull
  public final Expression discriminant;
  @Nonnull
  public final List<SwitchCase> cases;

  public SwitchStatement(@Nonnull Expression discriminant, @Nonnull List<SwitchCase> cases) {
    super();
    this.discriminant = discriminant;
    this.cases = cases;
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState
  transform(@Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState
  reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch discriminantBranch = new Branch(BranchType.DISCRIMINANT);
    return reducer.reduceSwitchStatement(this, path, this.discriminant.reduce(reducer, path.cons(discriminantBranch)),
        this.cases.mapWithIndex((index, switchCase) -> switchCase.reduce(reducer, path.cons(new Branch(BranchType.CASES,
            index)))));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case DISCRIMINANT:
      return Maybe.<Node>just(this.discriminant);
    case CASES:
      return this.cases.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression discriminant = this.discriminant;
    int casesLength = cases.length();
    SwitchCase casesChanges[] = new SwitchCase[casesLength];
    int casesMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case DISCRIMINANT:
        discriminant = (Expression) child;
        break;
      case CASES:
        if (branch.index < casesLength) {
          casesChanges[branch.index] = (SwitchCase) child;
          casesMax = Math.max(casesMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<SwitchCase> cases = Node.replaceIndex(this.cases, casesMax, casesChanges);
    return new SwitchStatement(discriminant, cases);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.SwitchStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatement && this.discriminant.equals(((SwitchStatement) object).discriminant) &&
        this.cases.equals(((SwitchStatement) object).cases);
  }
}
