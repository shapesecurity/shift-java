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

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class DataProperty extends ObjectProperty {
  @Nonnull
  public final Expression value;

  public DataProperty(@Nonnull PropertyName name, @Nonnull Expression value) {
    super(name);
    this.value = value;
  }

  @Nonnull
  @Override
  public ObjectPropertyKind getKind() {
    return ObjectPropertyKind.InitProperty;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> PropertyState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.DataProperty;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof DataProperty && this.name.equals(((DataProperty) object).name) &&
           this.value.equals(((DataProperty) object).value);
  }

  @Nonnull
  public Expression getValue() {
    return value;
  }

  @Nonnull
  public DataProperty setName(@Nonnull PropertyName name) {
    return new DataProperty(name, value);
  }

  @Nonnull
  public DataProperty setValue(@Nonnull Expression value) {
    return new DataProperty(name, value);
  }
}
