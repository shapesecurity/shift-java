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
import com.shapesecurity.shift.js.ast.Function;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class FunctionExpression extends PrimaryExpression implements Function {
  @Nonnull
  public final Maybe<Identifier> name;
  @Nonnull
  public final List<Identifier> parameters;
  @Nonnull
  public final FunctionBody body;

  public FunctionExpression(
      @Nonnull Maybe<Identifier> name,
      @Nonnull List<Identifier> parameters,
      @Nonnull FunctionBody body) {
    super();
    this.name = name;
    this.parameters = parameters;
    this.body = body;
  }

  public FunctionExpression(@Nonnull List<Identifier> parameters, @Nonnull FunctionBody body) {
    this(Maybe.<Identifier>nothing(), parameters, body);
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
    Branch bodyBranch = new Branch(BranchType.BODY);
    return reducer.reduceFunctionExpression(this, path, this.name.map(identifier -> identifier.reduce(reducer,
            path.cons(new Branch(BranchType.NAME)))), this.parameters.mapWithIndex(
            (index, identifier) -> identifier.reduce(reducer, path.cons(new Branch(BranchType.PARAMETERS, index)))),
        this.body.reduce(reducer, path.cons(bodyBranch)));
  }

  @Nonnull
  @Override
  public Maybe<? extends Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case NAME:
      return this.name;
    case PARAMETERS:
      return this.parameters.index(branch.index);
    case BODY:
      return Maybe.<Node>just(this.body);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    Maybe<Identifier> name = this.name;
    int paramsLength = this.parameters.length();
    Identifier paramsChanges[] = new Identifier[paramsLength];
    int paramsMax = -1;
    FunctionBody body = this.body;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case NAME:
        name = Maybe.just((Identifier) child);
        break;
      case PARAMETERS:
        if (branch.index < paramsLength) {
          paramsChanges[branch.index] = (Identifier) child;
          paramsMax = Math.max(paramsMax, branch.index);
        }
        break;
      case BODY:
        body = (FunctionBody) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    List<Identifier> params = Node.replaceIndex(this.parameters, paramsMax, paramsChanges);
    return new FunctionExpression(name, params, body);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof FunctionExpression && this.name.equals(((FunctionExpression) object).name) &&
        this.parameters.equals(((FunctionExpression) object).parameters) &&
        this.body.equals(((FunctionExpression) object).body);
  }

  @Nonnull
  @Override
  public List<Identifier> parameters() {
    return this.parameters;
  }
}
