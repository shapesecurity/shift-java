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

public class DoWhileStatement extends IterationStatement {
  @Nonnull
  public final Expression test;

  public DoWhileStatement(@Nonnull Statement body, @Nonnull Expression test) {
    super(body);
    this.test = test;
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
    return Type.DoWhileStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof DoWhileStatement && this.body.equals(((DoWhileStatement) object).body) &&
        this.test.equals(((DoWhileStatement) object).test);
  }

  @Nonnull
  public Expression getTest() {
    return test;
  }

  @Nonnull
  public DoWhileStatement setBody(@Nonnull Statement body) {
    return new DoWhileStatement(body, test);
  }

  @Nonnull
  public DoWhileStatement setTest(@Nonnull Expression test) {
    return new DoWhileStatement(body, test);
  }
}
