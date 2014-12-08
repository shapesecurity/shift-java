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

public class VariableDeclaration extends Node {
  @Nonnull
  public final NonEmptyList<VariableDeclarator> declarators;
  @Nonnull
  public final VariableDeclarationKind kind;

  public VariableDeclaration(
      @Nonnull VariableDeclarationKind kind,
      @Nonnull NonEmptyList<VariableDeclarator> declarators) {
    super();
    this.declarators = declarators;
    this.kind = kind;
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> DeclarationState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> DeclarationState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    NonEmptyList<DeclaratorState> reducedDeclarators = this.declarators.mapWithIndex(
        (index, variableDeclarator) -> variableDeclarator.reduce(reducer, path.cons(new Branch(BranchType.DECLARATORS,
            index))));
    return reducer.reduceVariableDeclaration(this, path, reducedDeclarators);
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case DECLARATORS:
      return this.declarators.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    int declaratorsLength = declarators.length();
    VariableDeclarator declaratorsChanges[] = new VariableDeclarator[declaratorsLength];
    int declaratorsMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case DECLARATORS:
        if (branch.index < declaratorsLength) {
          declaratorsChanges[branch.index] = (VariableDeclarator) child;
          declaratorsMax = Math.max(declaratorsMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    NonEmptyList<VariableDeclarator> declarators = (NonEmptyList<VariableDeclarator>) Node.replaceIndex(
        this.declarators, declaratorsMax, declaratorsChanges);
    return new VariableDeclaration(this.kind, declarators);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VariableDeclaration &&
        ((VariableDeclaration) obj).declarators.equals(this.declarators) &&
        ((VariableDeclaration) obj).kind.equals(this.kind);
  }

  public static enum VariableDeclarationKind {
    Var("var"),
    Const("const"),
    Let("let");
    public final String name;

    private VariableDeclarationKind(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }
}
