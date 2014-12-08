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

package com.shapesecurity.shift.ast.statement;

import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class DoWhileStatement extends IterationStatement {
  @NotNull
  public final Expression test;

  public DoWhileStatement(@NotNull Statement body, @NotNull Expression test) {
    super(body);
    this.test = test;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.DoWhileStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof DoWhileStatement && this.body.equals(((DoWhileStatement) object).body) &&
        this.test.equals(((DoWhileStatement) object).test);
  }

  @NotNull
  public Expression getTest() {
    return test;
  }

  @NotNull
  public DoWhileStatement setBody(@NotNull Statement body) {
    return new DoWhileStatement(body, test);
  }

  @NotNull
  public DoWhileStatement setTest(@NotNull Expression test) {
    return new DoWhileStatement(body, test);
  }
}
