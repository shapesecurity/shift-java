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
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class CallExpression extends LeftHandSideExpression {
  @Nonnull
  public final Expression callee;
  @Nonnull
  public final List<Expression> arguments;

  public CallExpression(@Nonnull Expression callee, @Nonnull List<Expression> arguments) {
    super();
    this.callee = callee;
    this.arguments = arguments;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.CALL;
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
    return Type.CallExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof CallExpression &&
           this.callee.equals(((CallExpression) object).callee) &&
           this.arguments.equals(((CallExpression) object).arguments);
  }

  @Nonnull
  public Expression getCallee() {
    return callee;
  }

  @Nonnull
  public List<Expression> getArguments() {
    return arguments;
  }

  @Nonnull
  public CallExpression setCallee(@Nonnull Expression callee) {
    return new CallExpression(callee, arguments);
  }

  @Nonnull
  public CallExpression setArguments(@Nonnull List<Expression> arguments) {
    return new CallExpression(callee, arguments);
  }

}
