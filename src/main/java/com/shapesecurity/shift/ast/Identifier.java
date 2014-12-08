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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.path.Branch;
import com.shapesecurity.shift.visitor.ReducerP;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

/**
 * There are certain locations that an identifier is not used as an expression. To reason about it,
 * see if the identifier can be
 * wrapped in multiple nested parentheses. If it can, it will be IdentifierExpression; otherwise,
 * it is just an Identifier.
 */
public final class Identifier extends Node {
  @NotNull
  public final String name;

  public Identifier(@NotNull String name) {
    super();
    this.name = name;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> IdentifierState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.Identifier;
  }

  @Override
  public boolean equals(Object object) {
    return this == object || object instanceof Identifier && this.name.equals(((Identifier) object).name);
  }
}
