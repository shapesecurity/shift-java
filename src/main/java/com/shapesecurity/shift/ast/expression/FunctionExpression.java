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

package com.shapesecurity.shift.ast.expression;

import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Function;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class FunctionExpression extends PrimaryExpression implements Function {
  @NotNull
  public final Maybe<Identifier> name;
  @NotNull
  public final List<Identifier> parameters;
  @NotNull
  public final FunctionBody body;

  public FunctionExpression(
      @NotNull Maybe<Identifier> name,
      @NotNull List<Identifier> parameters,
      @NotNull FunctionBody body) {
    super();
    this.name = name;
    this.parameters = parameters;
    this.body = body;
  }

  @NotNull
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

  @NotNull
  @Override
  public List<Identifier> parameters() {
    return this.parameters;
  }

  @NotNull
  public Maybe<Identifier> getName() {
    return this.name;
  }

  @NotNull
  public List<Identifier> getParameters() {
    return this.parameters;
  }

  @NotNull
  public FunctionBody getBody() {
    return this.body;
  }

  @NotNull
  public FunctionExpression setName(@NotNull Maybe<Identifier> name) {
    return new FunctionExpression(name, this.parameters, this.body);
  }

  @NotNull
  public FunctionExpression setParameters(@NotNull List<Identifier> parameters) {
    return new FunctionExpression(this.name, parameters, this.body);
  }

  @NotNull
  public FunctionExpression setBody(@NotNull FunctionBody body) {
    return new FunctionExpression(this.name, this.parameters, body);
  }
}
