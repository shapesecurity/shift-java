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
import com.shapesecurity.laserbat.js.ast.directive.UseStrictDirective;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.path.BranchType;
import com.shapesecurity.laserbat.js.visitor.ReducerP;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class FunctionBody extends Node {
  @Nonnull
  public final List<Directive> directives;
  @Nonnull
  public final List<Statement> statements;
  private final boolean isStrict;

  public FunctionBody(@Nonnull List<Directive> directives, @Nonnull List<Statement> statements) {
    super();
    this.directives = directives;
    this.statements = statements;
    this.isStrict = directives.exists(directive -> directive instanceof UseStrictDirective);
  }

  public boolean isStrict() {
    return this.isStrict;
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ProgramBodyState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ProgramBodyState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    return reducer.reduceFunctionBody(this, path, this.directives.mapWithIndex((index, iDirective) -> iDirective.reduce(
        reducer, path.cons(new Branch(BranchType.DIRECTIVES, index)))), this.statements.mapWithIndex(
        (index, iStatement) -> iStatement.reduce(reducer, path.cons(new Branch(BranchType.STATEMENTS, index)))));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case DIRECTIVES:
      return this.directives.index(branch.index);
    case STATEMENTS:
      return this.statements.index(branch.index);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    int directivesLength = directives.length();
    Directive directivesChanges[] = new Directive[directivesLength];
    int directivesMax = -1;
    int sourceElementsLength = statements.length();
    Statement sourceElementsChanges[] = new Statement[sourceElementsLength];
    int sourceElementsMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case DIRECTIVES:
        if (branch.index < directivesLength) {
          directivesChanges[branch.index] = (Directive) child;
          directivesMax = Math.max(directivesMax, branch.index);
        }
        break;
      case STATEMENTS:
        if (branch.index < sourceElementsLength) {
          sourceElementsChanges[branch.index] = (Statement) child;
          sourceElementsMax = Math.max(sourceElementsMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<Directive> directives = Node.replaceIndex(this.directives, directivesMax, directivesChanges);
    List<Statement> sourceElements = Node.replaceIndex(this.statements, sourceElementsMax, sourceElementsChanges);
    return new FunctionBody(directives, sourceElements);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof FunctionBody &&
        this.directives.equals(((FunctionBody) object).directives) &&
        this.statements.equals(((FunctionBody) object).statements) &&
        this.isStrict == ((FunctionBody) object).isStrict;
  }
}
