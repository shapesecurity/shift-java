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
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class SwitchCase extends Node {
  @NotNull
  public final Expression test;
  @NotNull
  public final List<Statement> consequent;

  public SwitchCase(@NotNull Expression test, @NotNull List<Statement> consequent) {
    super();
    this.test = test;
    this.consequent = consequent;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> SwitchCaseState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.SwitchCase;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchCase && this.test.equals(((SwitchCase) object).test) && this.consequent.equals(
        ((SwitchCase) object).consequent);
  }

  @NotNull
  public Expression getTest() {
    return test;
  }

  @NotNull
  public List<Statement> getConsequent() {
    return consequent;
  }

  @NotNull
  public SwitchCase setTest(@NotNull Expression test) {
    return new SwitchCase(test, consequent);
  }

  @NotNull
  public SwitchCase setConsequent(@NotNull List<Statement> consequent) {
    return new SwitchCase(test, consequent);
  }
}
