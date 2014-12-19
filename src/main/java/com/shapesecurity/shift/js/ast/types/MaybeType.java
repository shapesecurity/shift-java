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


package com.shapesecurity.shift.js.ast.types;

import javax.annotation.Nonnull;

public class MaybeType implements GenType {
  public static final MaybeType EXPRESSION = new MaybeType(Type.Expression);
  public static final MaybeType STATEMENT = new MaybeType(Type.Statement);
  public static final MaybeType IDENTIFIER = new MaybeType(Type.Identifier);
  public static final MaybeType CATCHCLAUSE = new MaybeType(Type.CatchClause);
  public static final MaybeType VARIABLEDECLARATION_OR_EXPRESSION =
      new MaybeType(EitherType.VARIABLEDECLARATION_EXPRESSION);

  @Nonnull
  public final GenType elementType;

  private MaybeType(@Nonnull GenType elementType) {
    this.elementType = elementType;
  }

  @Nonnull
  public static MaybeType from(@Nonnull GenType elementType) {
    if (elementType == EitherType.VARIABLEDECLARATION_EXPRESSION) {
      return VARIABLEDECLARATION_OR_EXPRESSION;
    } else if (elementType instanceof Type) {
      switch ((Type) elementType) {
      case Expression:
        return EXPRESSION;
      case Statement:
        return STATEMENT;
      case Identifier:
        return IDENTIFIER;
      case CatchClause:
        return CATCHCLAUSE;
      }
    }
    return new MaybeType(elementType);
  }

  @Nonnull
  @Override
  public Type rawType() {
    return Type.Maybe;
  }

  @Override
  public boolean isAssignableFrom(@Nonnull GenType type) {
    return type == this ||
           (type instanceof MaybeType && this.elementType.isAssignableFrom(((MaybeType) type).elementType));
  }
}
