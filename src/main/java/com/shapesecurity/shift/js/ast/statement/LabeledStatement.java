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

import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class LabeledStatement extends Statement {
  @Nonnull
  public final Identifier label;
  @Nonnull
  public final Statement body;

  public LabeledStatement(@Nonnull Identifier label, @Nonnull Statement body) {
    super();
    this.label = label;
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
    return Type.LabeledStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof LabeledStatement && this.label.equals(((LabeledStatement) object).label) &&
        this.body.equals(((LabeledStatement) object).body);
  }

  @Nonnull
  public Identifier getLabel() {
    return label;
  }

  @Nonnull
  public Statement getBody() {
    return body;
  }

  @Nonnull
  public LabeledStatement setLabel(@Nonnull Identifier label) {
    return new LabeledStatement(label, body);
  }

  @Nonnull
  public LabeledStatement setBody(@Nonnull Statement body) {
    return new LabeledStatement(label, body);
  }
}
