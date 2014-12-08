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

public class ReturnStatement extends Statement {
  @NotNull
  public final Maybe<Expression> expression;

  public ReturnStatement(@NotNull Maybe<Expression> expression) {
    super();
    this.expression = expression;
  }

  public ReturnStatement() {
    this(Maybe.<Expression>nothing());
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
    return Type.ReturnStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ReturnStatement && this.expression.equals(((ReturnStatement) object).expression);
  }

  @NotNull
  public Maybe<Expression> getExpression() {
    return expression;
  }

  @NotNull
  public ReturnStatement setExpression(@NotNull Maybe<Expression> expression) {
    return new ReturnStatement(expression);
  }
}
