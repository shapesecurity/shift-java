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

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class IfStatement extends Statement {
  @NotNull
  public final Expression test;
  @NotNull
  public final Statement consequent;
  @NotNull
  public final Maybe<Statement> alternate;

  public IfStatement(@NotNull Expression test, @NotNull Statement consequent, @NotNull Maybe<Statement> alternate) {
    super();
    this.test = test;
    this.consequent = consequent;
    this.alternate = alternate;
  }

  public IfStatement(@NotNull Expression test, @NotNull Statement consequent, @NotNull Statement alternate) {
    this(test, consequent, Maybe.just(alternate));
  }

  public IfStatement(@NotNull Expression test, @NotNull Statement consequent) {
    this(test, consequent, Maybe.nothing());
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
    return Type.IfStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof IfStatement &&
        this.test.equals(((IfStatement) object).test) &&
        this.consequent.equals(((IfStatement) object).consequent) &&
        this.alternate.equals(((IfStatement) object).alternate);
  }

  @NotNull
  public Expression getTest() {
    return test;
  }

  @NotNull
  public Statement getConsequent() {
    return consequent;
  }

  @NotNull
  public Maybe<Statement> getAlternate() {
    return alternate;
  }

  @NotNull
  public IfStatement setTest(@NotNull Expression test) {
    return new IfStatement(test, consequent, alternate);
  }

  @NotNull
  public IfStatement setConsequent(@NotNull Statement consequent) {
    return new IfStatement(test, consequent, alternate);
  }

  @NotNull
  public IfStatement setAlternate(@NotNull Maybe<Statement> alternate) {
    return new IfStatement(test, consequent, alternate);
  }


}
