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

package com.shapesecurity.shift.ast.property;

import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.utils.D2A;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public final class PropertyName extends Node {
  @NotNull
  public final String value;
  public final PropertyNameKind kind;

  public PropertyName(@NotNull PropertyName node) {
    super();
    this.value = node.value;
    this.kind = node.kind;
  }

  public PropertyName(@NotNull Identifier ident) {
    super();
    this.value = ident.name;
    this.kind = PropertyNameKind.Identifier;
  }

  public PropertyName(@NotNull LiteralStringExpression str) {
    this(str.value);
  }

  public PropertyName(@NotNull LiteralNumericExpression num) {
    this(num.value);
  }

  public PropertyName(@NotNull String str) {
    super();
    this.value = str;
    this.kind = PropertyNameKind.String;
  }

  public PropertyName(double d) {
    super();
    this.value = D2A.d2a(d);
    this.kind = PropertyNameKind.Number;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> PropertyNameState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.PropertyName;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PropertyName && this.kind.equals(((PropertyName) object).kind) &&
           this.value.equals(((PropertyName) object).value);
  }

  public static enum PropertyNameKind {
    Identifier("identifier"),
    String("string"),
    Number("number");
    @NotNull
    public final String name;

    private PropertyNameKind(@NotNull String name) {
      this.name = name;
    }
  }
}
