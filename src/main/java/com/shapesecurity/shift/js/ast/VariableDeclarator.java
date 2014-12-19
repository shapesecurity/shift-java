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

package com.shapesecurity.shift.js.ast;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class VariableDeclarator extends Node {
  @Nonnull
  public final Identifier binding;
  @Nonnull
  public final Maybe<Expression> init;

  public VariableDeclarator(@Nonnull Identifier binding, @Nonnull Maybe<Expression> init) {
    super();
    this.binding = binding;
    this.init = init;
  }

  public VariableDeclarator(@Nonnull Identifier binding) {
    this(binding, Maybe.<Expression>nothing());
  }

  @Nonnull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> DeclaratorState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.VariableDeclarator;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof VariableDeclarator && this.binding.equals(((VariableDeclarator) object).binding) &&
        this.init.equals(((VariableDeclarator) object).init);
  }

  @Nonnull
  public Identifier getBinding() {
    return binding;
  }

  @Nonnull
  public Maybe<Expression> getInit() {
    return init;
  }

  public VariableDeclarator setBinding(@Nonnull Identifier binding) {
    return new VariableDeclarator(binding, init);
  }

  public VariableDeclarator setInit(@Nonnull Maybe<Expression> init) {
    return new VariableDeclarator(binding, init);
  }
}
