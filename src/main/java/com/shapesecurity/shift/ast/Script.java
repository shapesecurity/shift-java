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

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.ReducerP;

import org.jetbrains.annotations.NotNull;

public class Script extends Node {
  @NotNull
  public final FunctionBody body;

  public Script(@NotNull FunctionBody body) {
    super();
    this.body = body;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.Script;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Script && this.body.equals(((Script) object).body);
  }

  @NotNull
  public FunctionBody getBody() {
    return this.body;
  }

  @NotNull
  public Script setBody(@NotNull FunctionBody body) {
    return new Script(body);
  }

  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  ScriptState reduce(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer) {
    return Director.reduceScript(reducer, this, ImmutableList.nil());
  }
}
