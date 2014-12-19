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

import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class Getter extends AccessorProperty {
  public Getter(@Nonnull PropertyName name, @Nonnull FunctionBody body) {
    super(name, body);
  }

  @Nonnull
  @Override
  public ObjectPropertyKind getKind() {
    return ObjectPropertyKind.GetterProperty;
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
    return Type.Getter;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Getter && this.name.equals(((Getter) object).name) &&
           this.body.equals(((Getter) object).body);
  }

  @Nonnull
  public Getter setName(@Nonnull PropertyName name) {
    return new Getter(name, body);
  }

  @Nonnull
  public Getter setBody(@Nonnull FunctionBody body) {
    return new Getter(name, body);
  }
}
