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
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.visitor.ReducerP;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

/**
 * There are certain locations that an identifier is not used as an expression. To reason about it,
 * see if the identifier can be
 * wrapped in multiple nested parentheses. If it can, it will be IdentifierExpression; otherwise,
 * it is just an Identifier.
 */
public final class Identifier extends Node {
  @Nonnull
  public final String name;

  public Identifier(@Nonnull String name) {
    super();
    this.name = name;
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> IdentifierState reduce(
      @Nonnull ReducerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @Nonnull final List<Branch> path) {
    return reducer.reduceIdentifier(this, path);
  }

  @Nonnull
  public <ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> IdentifierState transform(
      @Nonnull TransformerP<ProgramState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Maybe<Node> branchChild(@Nonnull Branch branch) {
    switch (branch.branchType) {
    default:
      return Maybe.<Node>nothing();
    }
  }

  @Nonnull
  @Override
  public Node replicate(@Nonnull List<? extends ReplacementChild> children) {
    return new Identifier(name);
  }

  @Override
  public boolean equals(Object object) {
    return this == object || object instanceof Identifier && this.name.equals(((Identifier) object).name);
  }
}
