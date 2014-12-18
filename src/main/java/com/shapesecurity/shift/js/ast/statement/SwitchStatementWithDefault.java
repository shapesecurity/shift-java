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
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class SwitchStatementWithDefault extends Statement {
  @Nonnull
  public final Expression discriminant;
  @Nonnull
  public final List<SwitchCase> preDefaultCases;
  @Nonnull
  public final SwitchDefault defaultCase;
  @Nonnull
  public final List<SwitchCase> postDefaultCases;

  public SwitchStatementWithDefault(
      @Nonnull Expression discriminant,
      @Nonnull List<SwitchCase> preDefaultCases,
      @Nonnull SwitchDefault defaultCase,
      @Nonnull List<SwitchCase> postDefaultCases) {
    super();
    this.discriminant = discriminant;
    this.preDefaultCases = preDefaultCases;
    this.defaultCase = defaultCase;
    this.postDefaultCases = postDefaultCases;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState reduce(
      @Nonnull final ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch discriminantBranch = new Branch(BranchType.DISCRIMINANT);
    Branch defaultCaseBranch = new Branch(BranchType.DEFAULTCASE);
    return reducer.reduceSwitchStatementWithDefault(this, path, this.discriminant.reduce(reducer, path.cons(
        discriminantBranch)), this.preDefaultCases.mapWithIndex((index, switchCase) -> switchCase.reduce(reducer,
        path.cons(new Branch(BranchType.PREDEFAULTCASES, index)))), this.defaultCase.reduce(reducer, path.cons(
        defaultCaseBranch)), this.postDefaultCases.mapWithIndex((index, switchCase) -> switchCase.reduce(reducer,
        path.cons(new Branch(BranchType.POSTDEFAULTCASES, index)))));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case DISCRIMINANT:
      return Maybe.<Node>just(this.discriminant);
    case PREDEFAULTCASES:
      return this.preDefaultCases.index(branch.index);
    case DEFAULTCASE:
      return Maybe.<Node>just(this.defaultCase);
    case POSTDEFAULTCASES:
      return this.postDefaultCases.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression discriminant = this.discriminant;
    int preDefaultCasesLength = preDefaultCases.length();
    SwitchCase preDefaultCasesChanges[] = new SwitchCase[preDefaultCasesLength];
    int preDefaultCasesMax = -1;
    SwitchDefault defaultCase = this.defaultCase;
    int postDefaultCasesLength = postDefaultCases.length();
    SwitchCase postDefaultCasesChanges[] = new SwitchCase[postDefaultCasesLength];
    int postDefaultCasesMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case DISCRIMINANT:
        discriminant = (Expression) child;
        break;
      case PREDEFAULTCASES:
        if (branch.index < preDefaultCasesLength) {
          preDefaultCasesChanges[branch.index] = (SwitchCase) child;
          preDefaultCasesMax = Math.max(preDefaultCasesMax, branch.index);
        }
        break;
      case DEFAULTCASE:
        defaultCase = (SwitchDefault) child;
        break;
      case POSTDEFAULTCASES:
        if (branch.index < postDefaultCasesLength) {
          postDefaultCasesChanges[branch.index] = (SwitchCase) child;
          postDefaultCasesMax = Math.max(postDefaultCasesMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<SwitchCase> preDefaultCases = Node.replaceIndex(this.preDefaultCases, preDefaultCasesMax,
        preDefaultCasesChanges);
    List<SwitchCase> postDefaultCases = Node.replaceIndex(this.postDefaultCases, postDefaultCasesMax,
        postDefaultCasesChanges);
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.SwitchStatementWithDefault;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatementWithDefault &&
        this.discriminant.equals(((SwitchStatementWithDefault) object).discriminant) &&
        this.preDefaultCases.equals(((SwitchStatementWithDefault) object).preDefaultCases) &&
        this.defaultCase.equals(((SwitchStatementWithDefault) object).defaultCase) &&
        this.postDefaultCases.equals(((SwitchStatementWithDefault) object).postDefaultCases);
  }
}
