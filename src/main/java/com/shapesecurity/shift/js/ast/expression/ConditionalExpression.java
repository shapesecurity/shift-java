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

import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class ConditionalExpression extends Expression {
  @Nonnull
  public final Expression test;
  @Nonnull
  public final Expression consequent;
  @Nonnull
  public final Expression alternate;

  public ConditionalExpression(
      @Nonnull Expression test,
      @Nonnull Expression consequent,
      @Nonnull Expression alternate) {
    super();
    this.test = test;
    this.consequent = consequent;
    this.alternate = alternate;
  }

  public Precedence getPrecedence() {
    return Precedence.CONDITIONAL;
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
    return Type.ConditionalExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ConditionalExpression &&
           this.test.equals(((ConditionalExpression) object).test) &&
           this.consequent.equals(((ConditionalExpression) object).consequent) &&
           this.alternate.equals(((ConditionalExpression) object).alternate);
  }

  @Nonnull
  public Expression getTest() {
    return test;
  }

  @Nonnull
  public Expression getConsequent() {
    return consequent;
  }

  @Nonnull
  public Expression getAlternate() {
    return alternate;
  }

  @Nonnull
  public ConditionalExpression setTest(@Nonnull Expression test) {
    return new ConditionalExpression(test, consequent, alternate);
  }

  @Nonnull
  public ConditionalExpression setConsequent(@Nonnull Expression consequent) {
    return new ConditionalExpression(test, consequent, alternate);
  }

  @Nonnull
  public ConditionalExpression setAlternate(@Nonnull Expression alternate) {
    return new ConditionalExpression(test, consequent, alternate);
  }
}
