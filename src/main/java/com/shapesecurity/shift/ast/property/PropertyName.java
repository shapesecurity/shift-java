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
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.utils.D2A;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public final class PropertyName extends Node {
  @NotNull
  public final String value;
  public final PropertyNameKind kind;

  private PropertyName(@NotNull String value, @NotNull PropertyNameKind kind) {
    super();
    this.value = value;
    this.kind = kind;
  }

  public PropertyName(@NotNull PropertyName node) {
    this(node.value, node.kind);
  }

  public PropertyName(@NotNull Identifier ident) {
    this(ident.name, PropertyNameKind.Identifier);
  }

  public PropertyName(@NotNull String str) {
    this(str, PropertyNameKind.String);
  }

  public PropertyName(double d) {
    this(D2A.d2a(d), PropertyNameKind.Number);
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
