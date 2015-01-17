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
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class ArrayExpression extends PrimaryExpression {
  @NotNull
  public final List<Maybe<Expression>> elements;

  public ArrayExpression(@NotNull List<Maybe<Expression>> elements) {
    super();
    this.elements = elements;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.ArrayExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ArrayExpression && this.elements.equals(((ArrayExpression) object).elements);
  }

  @NotNull
  public List<Maybe<Expression>> getElements() {
    return this.elements;
  }

  @NotNull
  public ArrayExpression setElements(@NotNull List<Maybe<Expression>> elements) {
    return new ArrayExpression(elements);
  }
}
