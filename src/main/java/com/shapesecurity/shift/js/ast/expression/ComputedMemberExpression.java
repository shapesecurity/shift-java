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
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class ComputedMemberExpression extends MemberExpression {
  @Nonnull
  public final Expression expression;

  public ComputedMemberExpression(@Nonnull Expression object, @Nonnull Expression expression) {
    super(object);
    this.expression = expression;
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
    return Type.ComputedMemberExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ComputedMemberExpression &&
           this.object.equals(((ComputedMemberExpression) object).object) &&
           this.expression.equals(((ComputedMemberExpression) object).expression);
  }

  @Nonnull
  public Expression getExpression() {
    return expression;
  }

  @Nonnull
  public ComputedMemberExpression setObject(@Nonnull Expression object) {
    return new ComputedMemberExpression(object, expression);
  }

  @Nonnull
  public ComputedMemberExpression setExpression(@Nonnull Expression expression) {
    return new ComputedMemberExpression(object, expression);
  }
}
