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

import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.MaybeNode;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class IfStatement extends Statement {
  @Nonnull
  public final Expression test;
  @Nonnull
  public final Statement consequent;
  @Nonnull
  public final Maybe<Statement> alternate;

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent, @Nonnull Maybe<Statement> alternate) {
    super();
    this.test = test;
    this.consequent = consequent;
    this.alternate = alternate;
  }

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent, @Nonnull Statement alternate) {
    this(test, consequent, Maybe.just(alternate));
  }

  public IfStatement(@Nonnull Expression test, @Nonnull Statement consequent) {
    this(test, consequent, Maybe.<Statement>nothing());
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
    return Type.IfStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof IfStatement &&
        this.test.equals(((IfStatement) object).test) &&
        this.consequent.equals(((IfStatement) object).consequent) &&
        this.alternate.equals(((IfStatement) object).alternate);
  }

  @Nonnull
  public Expression getTest() {
    return test;
  }

  @Nonnull
  public Statement getConsequent() {
    return consequent;
  }

  @Nonnull
  public Maybe<Statement> getAlternate() {
    return alternate;
  }

  @Nonnull
  public IfStatement setTest(@Nonnull Expression test) {
    return new IfStatement(test, consequent, alternate);
  }

  @Nonnull
  public IfStatement setConsequent(@Nonnull Statement consequent) {
    return new IfStatement(test, consequent, alternate);
  }

  @Nonnull
  public IfStatement setAlternate(@Nonnull Maybe<Statement> alternate) {
    return new IfStatement(test, consequent, alternate);
  }


}
