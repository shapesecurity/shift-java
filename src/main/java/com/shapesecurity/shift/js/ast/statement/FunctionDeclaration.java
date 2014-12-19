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

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.Function;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class FunctionDeclaration extends Statement implements Function {
  @Nonnull
  public final Identifier name;
  @Nonnull
  public final List<Identifier> parameters;
  @Nonnull
  public final FunctionBody body;

  public FunctionDeclaration(
      @Nonnull Identifier name,
      @Nonnull List<Identifier> parameters,
      @Nonnull FunctionBody body) {
    super();
    this.name = name;
    this.parameters = parameters;
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
    return Type.FunctionDeclaration;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof FunctionDeclaration &&
        this.name.equals(((FunctionDeclaration) object).name) &&
        this.parameters.equals(((FunctionDeclaration) object).parameters) &&
        this.body.equals(((FunctionDeclaration) object).body);
  }

  @Nonnull
  @Override
  public List<Identifier> parameters() {
    return this.parameters;
  }

  @Nonnull
  public Identifier getName() {
    return name;
  }

  @Nonnull
  public List<Identifier> getParameters() {
    return parameters;
  }

  @Nonnull
  public FunctionBody getBody() {
    return body;
  }

  @Nonnull
  public FunctionDeclaration setName(@Nonnull Identifier name) {
    return new FunctionDeclaration(name, parameters, body);
  }

  @Nonnull
  public FunctionDeclaration setParameters(@Nonnull List<Identifier> parameters) {
    return new FunctionDeclaration(name, parameters, body);
  }

  @Nonnull
  public FunctionDeclaration setBody(@Nonnull FunctionBody body) {
    return new FunctionDeclaration(name, parameters, body);
  }
}
