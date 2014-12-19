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

import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.types.GenType;
import com.shapesecurity.shift.js.ast.types.MaybeType;
import com.shapesecurity.shift.js.ast.types.Type;

public class MaybeNode<T> extends Node {
  public final Maybe<T> maybe;
  public final MaybeType genType;

  public MaybeNode(@Nonnull Maybe<T> maybe, @Nonnull MaybeType genType) {
    this.maybe = maybe;
    this.genType = genType;
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.Maybe;
  }

  @Nonnull
  @Override
  public GenType genType() {
    return genType;
  }
}
