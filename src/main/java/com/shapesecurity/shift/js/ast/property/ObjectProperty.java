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

import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.visitor.TransformerP;

public abstract class ObjectProperty extends Node {
  @Nonnull
  public final PropertyName name;

  ObjectProperty(@Nonnull PropertyName name) {
    super();
    this.name = name;
  }

  @Nonnull
  public abstract <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> PropertyState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer);

  @Nonnull
  public abstract ObjectPropertyKind getKind();

  public static enum ObjectPropertyKind {
    InitProperty,
    GetterProperty,
    SetterProperty
  }

  @Nonnull
  public PropertyName getName() {
    return name;
  }
}
