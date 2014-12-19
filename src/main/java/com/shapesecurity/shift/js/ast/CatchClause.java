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

import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class CatchClause extends Node {
  @Nonnull
  public final Identifier binding;
  @Nonnull
  public final Block body;

  public CatchClause(@Nonnull Identifier binding, @Nonnull Block body) {
    super();
    this.binding = binding;
    this.body = body;
  }

  @Nonnull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> CatchClauseState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.CatchClause;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof CatchClause && this.binding.equals(((CatchClause) object).binding) &&
           this.body.equals(((CatchClause) object).body);
  }

  @Nonnull
  public Identifier getBinding() {
    return binding;
  }

  @Nonnull
  public Block getBody() {
    return body;
  }

  @Nonnull
  public CatchClause setBinding(@Nonnull Identifier binding) {
    return new CatchClause(binding, body);
  }

  @Nonnull
  public CatchClause setBody(@Nonnull Block body) {
    return new CatchClause(binding, body);
  }
}
