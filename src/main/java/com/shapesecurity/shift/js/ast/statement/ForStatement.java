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

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class ForStatement extends IterationStatement {
  @Nonnull
  public final Maybe<Either<VariableDeclaration, Expression>> init;
  @Nonnull
  public final Maybe<Expression> test;
  @Nonnull
  public final Maybe<Expression> update;

  public ForStatement(
      @Nonnull Expression init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    this(Maybe.just(Either.right(init)), test, update, body);
  }

  public ForStatement(
      @Nonnull VariableDeclaration init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    this(Maybe.just(Either.left(init)), test, update, body);
  }

  public ForStatement(
      @Nonnull Maybe<Either<VariableDeclaration, Expression>> init,
      @Nonnull Maybe<Expression> test,
      @Nonnull Maybe<Expression> update,
      @Nonnull Statement body) {
    super(body);
    this.init = init;
    this.test = test;
    this.update = update;
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

  @Nonnull
  public Maybe<Either<VariableDeclaration, Expression>> getInit() {
    return init;
  }

  @Nonnull
  public Maybe<Expression> getTest() {
    return test;
  }

  @Nonnull
  public Maybe<Expression> getUpdate() {
    return update;
  }

  @Nonnull
  @Override
  public Statement getBody() {
    return super.getBody();
  }

  @Nonnull
  public ForStatement setInit(@Nonnull Maybe<Either<VariableDeclaration, Expression>> init) {
    return new ForStatement(init, test, update, body);
  }

  @Nonnull
  public ForStatement setTest(@Nonnull Maybe<Expression> test) {
    return new ForStatement(init, test, update, body);
  }

  @Nonnull
  public ForStatement setUpdate(@Nonnull Maybe<Expression> update) {
    return new ForStatement(init, test, update, body);
  }

  @Nonnull
  public ForStatement setBody(@Nonnull Statement body) {
    return new ForStatement(init, test, update, body);
  }
}
