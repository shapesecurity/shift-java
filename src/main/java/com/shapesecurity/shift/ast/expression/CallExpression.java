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
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class CallExpression extends LeftHandSideExpression {
  @NotNull
  public final Expression callee;
  @NotNull
  public final List<Expression> arguments;

  public CallExpression(@NotNull Expression callee, @NotNull List<Expression> arguments) {
    super();
    this.callee = callee;
    this.arguments = arguments;
  }

  @Override
  public Precedence getPrecedence() {
    return Precedence.CALL;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ExpressionState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
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

  @NotNull
  public Expression getCallee() {
    return callee;
  }

  @NotNull
  public List<Expression> getArguments() {
    return arguments;
  }

  @NotNull
  public CallExpression setCallee(@NotNull Expression callee) {
    return new CallExpression(callee, arguments);
  }

  @NotNull
  public CallExpression setArguments(@NotNull List<Expression> arguments) {
    return new CallExpression(callee, arguments);
  }

}
