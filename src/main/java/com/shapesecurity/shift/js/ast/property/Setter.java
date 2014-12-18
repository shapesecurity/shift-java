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

package com.shapesecurity.shift.js.ast.property;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.ReplacementChild;
import com.shapesecurity.shift.js.ast.Type;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;
import com.shapesecurity.shift.js.visitor.ReducerP;
import com.shapesecurity.shift.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class Setter extends AccessorProperty {
  @Nonnull
  public final Identifier parameter;

  public Setter(@Nonnull PropertyName name, @Nonnull Identifier parameter, @Nonnull FunctionBody body) {
    super(name, body);
    this.parameter = parameter;
  }

  @Nonnull
  @Override
  public ObjectPropertyKind getKind() {
    return ObjectPropertyKind.SetterProperty;
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> PropertyState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> PropertyState reduce(
      @Nonnull final ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    Branch nameBranch = new Branch(BranchType.NAME);
    Branch parameterBranch = new Branch(BranchType.PARAMETER);
    Branch bodyBranch = new Branch(BranchType.BODY);
    return reducer.reduceSetter(this, path, this.name.reduce(reducer, path.cons(nameBranch)), this.parameter.reduce(
        reducer, path.cons(parameterBranch)), this.body.reduce(reducer, path.cons(bodyBranch)));
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    case NAME:
      return Maybe.<Node>just(this.name);
    case PARAMETER:
      return Maybe.<Node>just(this.parameter);
    case BODY:
      return Maybe.<Node>just(this.body);
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    PropertyName name = this.name;
    Identifier parameter = this.parameter;
    FunctionBody body = this.body;
    while (children instanceof NonEmptyList) {
      NonEmptyList<? extends ReplacementChild> childrenNE = (NonEmptyList<? extends ReplacementChild>) children;
      ReplacementChild rc = childrenNE.head;
      Branch branch = rc.branch;
      Node child = rc.child;
      switch (branch.branchType) {
      case NAME:
        name = (PropertyName) child;
        break;
      case PARAMETER:
        parameter = (Identifier) child;
        break;
      case BODY:
        body = (FunctionBody) child;
        break;
      default:
      }
      children = childrenNE.tail();
    }
    return new Setter(name, parameter, body);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.Setter;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Setter &&
        this.name.equals(((Setter) object).name) &&
        this.parameter.equals(((Setter) object).parameter) &&
        this.body.equals(((Setter) object).body);
  }
}
