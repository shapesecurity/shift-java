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
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class ObjectExpression extends PrimaryExpression {
  @NotNull
  public final List<ObjectProperty> properties;

  public ObjectExpression(@NotNull List<ObjectProperty> properties) {
    super();
    this.properties = properties;
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
    return Type.ObjectExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ObjectExpression && this.properties.equals(((ObjectExpression) object).properties);
  }

  @NotNull
  public List<ObjectProperty> getProperties() {
    return properties;
  }

  @NotNull
  public ObjectExpression setProperties(@NotNull List<ObjectProperty> properties) {
    return new ObjectExpression(properties);
  }
}
