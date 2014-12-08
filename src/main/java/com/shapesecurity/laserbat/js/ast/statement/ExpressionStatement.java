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

package com.shapesecurity.laserbat.js.ast.statement;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.Node;
import com.shapesecurity.laserbat.js.ast.ReplacementChild;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.path.BranchType;
import com.shapesecurity.laserbat.js.visitor.ReducerP;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class ExpressionStatement extends Statement {
  @Nonnull
  public final Expression expression;

  public ExpressionStatement(@Nonnull Expression expression) {
    super();
    this.expression = expression;
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
    Branch expressionBranch = new Branch(BranchType.EXPRESSION);
    return reducer.reduceExpressionStatement(this, path, this.expression.reduce(reducer, path.cons(expressionBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case EXPRESSION:
      return Maybe.<Node>just(this.expression);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Expression expression = this.expression;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case EXPRESSION:
        expression = (Expression) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new ExpressionStatement(expression);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ExpressionStatement && this.expression.equals(((ExpressionStatement) object).expression);
  }
}
