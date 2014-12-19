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

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class IdentifierExpression extends PrimaryExpression {
  @Nonnull
  public final Identifier identifier;

  public IdentifierExpression(@Nonnull Identifier identifier) {
    super();
    this.identifier = identifier;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.IdentifierExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof IdentifierExpression && this.identifier.equals(((IdentifierExpression) object).identifier);
  }

  @Nonnull
  public Identifier getIdentifier() {
    return identifier;
  }

  @Nonnull
  public IdentifierExpression setIdentifier(@Nonnull Identifier identifier) {
    return new IdentifierExpression(identifier);
  }
}
