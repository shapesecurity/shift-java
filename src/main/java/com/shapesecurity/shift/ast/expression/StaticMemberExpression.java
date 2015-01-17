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

import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class StaticMemberExpression extends MemberExpression {
  @NotNull
  public final Identifier property;

  public StaticMemberExpression(@NotNull Expression object, @NotNull Identifier property) {
    super(object);
    this.property = property;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.StaticMemberExpression;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof StaticMemberExpression &&
        this.object.equals(((StaticMemberExpression) object).object) &&
        this.property.equals(((StaticMemberExpression) object).property);
  }

  @NotNull
  public Identifier getProperty() {
    return this.property;
  }

  @NotNull
  public StaticMemberExpression setObject(@NotNull Expression object) {
    return new StaticMemberExpression(object, this.property);
  }

  @NotNull
  public StaticMemberExpression setProperty(@NotNull Identifier property) {
    return new StaticMemberExpression(this.object, property);
  }
}
