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

package com.shapesecurity.shift.js.ast.expression;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class ArrayExpression extends PrimaryExpression {
  @Nonnull
  public final List<Maybe<Expression>> elements;

  public ArrayExpression(@Nonnull List<Maybe<Expression>> elements) {
    super();
    this.elements = elements;
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    return reducer.reduceArrayExpression(this, path, this.elements.mapWithIndex((index, expression) -> expression.map(
        iExpression -> iExpression.reduce(reducer, path.cons(new Branch(BranchType.ELEMENTS, index))))));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case ELEMENTS:
      Maybe<Maybe<Expression>> maybeMaybeIExp = this.elements.index(branch.index);
      return maybeMaybeIExp.isNothing() ? Maybe.<Node>nothing() : maybeMaybeIExp.just();
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    int elementsLength = elements.length();
    Maybe<?> elementsChanges[] = new Maybe<?>[elementsLength];
    int elementsMax = -1;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case ELEMENTS:
        if (branch.index < elementsLength) {
          elementsChanges[branch.index] = Maybe.<Expression>just((Expression) child);
          elementsMax = Math.max(elementsMax, branch.index);
        }
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<Maybe<Expression>> elements = Node.replaceIndex(this.elements, elementsMax,
        (Maybe<Expression>[]) elementsChanges);
    return new ArrayExpression(elements);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ArrayExpression && this.elements.equals(((ArrayExpression) object).elements);
  }
}
