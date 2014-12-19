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

package com.shapesecurity.shift.js.ast.expression;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Function;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.ListNode;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class FunctionExpression extends PrimaryExpression implements Function {
  @Nonnull
  public final Maybe<Identifier> name;
  @Nonnull
  public final List<Identifier> parameters;
  @Nonnull
  public final FunctionBody body;

  public FunctionExpression(
      @Nonnull Maybe<Identifier> name,
      @Nonnull List<Identifier> parameters,
      @Nonnull FunctionBody body) {
    super();
    this.name = name;
    this.parameters = parameters;
    this.body = body;
  }

  public FunctionExpression(@Nonnull List<Identifier> parameters, @Nonnull FunctionBody body) {
    this(Maybe.<Identifier>nothing(), parameters, body);
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.FunctionExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof FunctionExpression && this.name.equals(((FunctionExpression) object).name) &&
        this.parameters.equals(((FunctionExpression) object).parameters) &&
        this.body.equals(((FunctionExpression) object).body);
  }

  @Nonnull
  @Override
  public List<Identifier> parameters() {
    return parameters;
  }

  @Nonnull
  public Maybe<Identifier> getName() {
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
  public FunctionExpression setName(@Nonnull Maybe<Identifier> name) {
    return new FunctionExpression(name, parameters, body);
  }

  @Nonnull
  public FunctionExpression setParameters(@Nonnull List<Identifier> parameters) {
    return new FunctionExpression(name, parameters, body);
  }

  @Nonnull
  public FunctionExpression setBody(@Nonnull FunctionBody body) {
    return new FunctionExpression(name, parameters, body);
  }
}
