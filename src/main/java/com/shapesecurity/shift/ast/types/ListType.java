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


package com.shapesecurity.shift.ast.types;

import org.jetbrains.annotations.NotNull;

public class ListType implements GenType {
  public static final ListType EXPRESSION = new ListType(Type.Expression);
  public static final ListType MAYBE_EXPRESSION = new ListType(MaybeType.EXPRESSION);
  public static final ListType STATEMENT = new ListType(Type.Statement);
  public static final ListType IDENTIFIER = new ListType(Type.Identifier);
  public static final ListType CATCHCLAUSE = new ListType(Type.CatchClause);

  @NotNull
  public final GenType elementType;

  private ListType(@NotNull GenType elementType) {
    this.elementType = elementType;
  }

  @NotNull
  @Override
  public Type rawType() {
    return Type.List;
  }

  @Override
  public boolean isAssignableFrom(@NotNull GenType type) {
    return type == this ||
           (type instanceof ListType && this.elementType.isAssignableFrom(((ListType) type).elementType)) ||
           (type instanceof NonEmptyListType &&
            this.elementType.isAssignableFrom(((NonEmptyListType) type).elementType));
  }

  @NotNull
  public static ListType from(@NotNull GenType elementType) {
    switch (elementType.rawType()) {
      case Expression:
        return EXPRESSION;
      case Maybe:
        if (((MaybeType) elementType).elementType == Type.Expression) {
          return MAYBE_EXPRESSION;
        }
      case Statement:
        return STATEMENT;
      case Identifier:
        return IDENTIFIER;
      case CatchClause:
        return CATCHCLAUSE;
    }
    return new ListType(elementType);
  }
}
