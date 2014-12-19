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

public class EitherType implements GenType {
  public final static EitherType VARIABLEDECLARATION_EXPRESSION =
      new EitherType(Type.VariableDeclaration, Type.Expression);

  @Nonnull
  public final GenType leftType;
  @Nonnull
  public final GenType rightType;

  private EitherType(@Nonnull GenType leftType, @Nonnull GenType rightType) {
    this.leftType = leftType;
    this.rightType = rightType;
  }

  @Nonnull
  @Override
  public Type rawType() {
    return Type.Either;
  }

  @Override
  public boolean isAssignableFrom(@Nonnull GenType type) {
    return type == this ||
           (type instanceof EitherType &&
            this.leftType.isAssignableFrom(((EitherType) type).leftType) &&
            this.rightType.isAssignableFrom(((EitherType) type).rightType));
  }

  @Nonnull
  public static EitherType from(@Nonnull GenType leftType, @Nonnull GenType rightType) {
    if (leftType == Type.VariableDeclaration && rightType == Type.Expression) {
      return VARIABLEDECLARATION_EXPRESSION;
    }
    return new EitherType(leftType, rightType);
  }
}
