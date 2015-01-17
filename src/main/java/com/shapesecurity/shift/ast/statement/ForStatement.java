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

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class ForStatement extends IterationStatement {
  @NotNull
  public final Maybe<Either<VariableDeclaration, Expression>> init;
  @NotNull
  public final Maybe<Expression> test;
  @NotNull
  public final Maybe<Expression> update;

  public ForStatement(
      @NotNull Maybe<Either<VariableDeclaration, Expression>> init,
      @NotNull Maybe<Expression> test,
      @NotNull Maybe<Expression> update,
      @NotNull Statement body) {
    super(body);
    this.init = init;
    this.test = test;
    this.update = update;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.ForStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ForStatement &&
        this.init.equals(((ForStatement) object).init) &&
        this.test.equals(((ForStatement) object).test) &&
        this.update.equals(((ForStatement) object).update) &&
        this.body.equals(((ForStatement) object).body);
  }

  @NotNull
  public Maybe<Either<VariableDeclaration, Expression>> getInit() {
    return this.init;
  }

  @NotNull
  public Maybe<Expression> getTest() {
    return this.test;
  }

  @NotNull
  public Maybe<Expression> getUpdate() {
    return this.update;
  }

  @NotNull
  public ForStatement setInit(@NotNull Maybe<Either<VariableDeclaration, Expression>> init) {
    return new ForStatement(init, this.test, this.update, this.body);
  }

  @NotNull
  public ForStatement setTest(@NotNull Maybe<Expression> test) {
    return new ForStatement(this.init, test, this.update, this.body);
  }

  @NotNull
  public ForStatement setUpdate(@NotNull Maybe<Expression> update) {
    return new ForStatement(this.init, this.test, update, this.body);
  }

  @NotNull
  public ForStatement setBody(@NotNull Statement body) {
    return new ForStatement(this.init, this.test, this.update, body);
  }
}
