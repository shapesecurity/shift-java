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

package com.shapesecurity.shift.ast;

import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class CatchClause extends Node {
  @NotNull
  public final Identifier binding;
  @NotNull
  public final Block body;

  public CatchClause(@NotNull Identifier binding, @NotNull Block body) {
    super();
    this.binding = binding;
    this.body = body;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> CatchClauseState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.CatchClause;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof CatchClause && this.binding.equals(((CatchClause) object).binding) &&
           this.body.equals(((CatchClause) object).body);
  }

  @NotNull
  public Identifier getBinding() {
    return binding;
  }

  @NotNull
  public Block getBody() {
    return body;
  }

  @NotNull
  public CatchClause setBinding(@NotNull Identifier binding) {
    return new CatchClause(binding, body);
  }

  @NotNull
  public CatchClause setBody(@NotNull Block body) {
    return new CatchClause(binding, body);
  }
}
