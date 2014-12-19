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

package com.shapesecurity.shift.js.ast.directive;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class UnknownDirective extends Directive {
  @Nonnull
  public final String value;

  public UnknownDirective(@Nonnull CharSequence value) {
    super();
    this.value = String.valueOf(value);
  }

  @Nonnull
  @Override
  public CharSequence getContents() {
    return this.value;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> DirectiveState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.UnknownDirective;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof UnknownDirective && this.value.equals(((UnknownDirective) object).value);
  }
}
