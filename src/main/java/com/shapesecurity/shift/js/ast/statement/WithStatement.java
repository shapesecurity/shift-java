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

package com.shapesecurity.shift.js.ast.statement;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class WithStatement extends Statement {
  @Nonnull
  public final Expression object;
  @Nonnull
  public final Statement body;

  public WithStatement(@Nonnull Expression object, @Nonnull Statement body) {
    super();
    this.object = object;
    this.body = body;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.WithStatement;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof WithStatement && this.object.equals(((WithStatement) obj).object) &&
        this.body.equals(((WithStatement) obj).body);
  }

  @Nonnull
  public Expression getObject() {
    return object;
  }

  @Nonnull
  public Statement getBody() {
    return body;
  }

  public WithStatement setObject(@Nonnull Expression object) {
    return new WithStatement(object, body);
  }

  public WithStatement setBody(@Nonnull Statement body) {
    return new WithStatement(object, body);
  }
}
