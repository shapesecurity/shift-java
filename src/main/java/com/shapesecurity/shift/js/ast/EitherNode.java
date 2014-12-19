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


package com.shapesecurity.shift.js.ast;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.js.ast.types.EitherType;
import com.shapesecurity.shift.js.ast.types.GenType;
import com.shapesecurity.shift.js.ast.types.Type;

public class EitherNode<A, B> extends Node {
  @Nonnull
  public final Either<A, B> either;
  @Nonnull
  public final EitherType genType;

  public EitherNode(@Nonnull Either<A, B> either, @Nonnull EitherType eType) {
    this.either = either;
    genType = eType;
  }

  public EitherNode(@Nonnull Either<A, B> either, @Nonnull GenType aType, @Nonnull GenType bType) {
    this.either = either;
    genType = EitherType.from(aType, bType);
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.Either;
  }

  @Nonnull
  @Override
  public GenType genType() {
    return this.genType;
  }
}
