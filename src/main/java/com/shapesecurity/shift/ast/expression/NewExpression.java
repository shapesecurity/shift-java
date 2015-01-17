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
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class NewExpression extends LeftHandSideExpression {
  @NotNull
  public final Expression callee;
  @NotNull
  public final List<Expression> arguments;

  public NewExpression(@NotNull Expression callee, @NotNull List<Expression> arguments) {
    super();
    this.callee = callee;
    this.arguments = arguments;
  }

  @Override
  public Precedence getPrecedence() {
    return this.arguments.isEmpty() ? Precedence.NEW : Precedence.MEMBER;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.NewExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof NewExpression && this.callee.equals(((NewExpression) object).callee) &&
           this.arguments.equals(((NewExpression) object).arguments);
  }

  @NotNull
  public Expression getCallee() {
    return this.callee;
  }

  @NotNull
  public List<Expression> getArguments() {
    return this.arguments;
  }

  @NotNull
  public NewExpression setCallee(@NotNull Expression callee) {
    return new NewExpression(callee, this.arguments);
  }

  @NotNull
  public NewExpression setArguments(@NotNull List<Expression> arguments) {
    return new NewExpression(this.callee, arguments);
  }
}
