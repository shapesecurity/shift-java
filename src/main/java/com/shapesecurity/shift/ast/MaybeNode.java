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


package com.shapesecurity.shift.ast;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.types.GenType;
import com.shapesecurity.shift.ast.types.MaybeType;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class MaybeNode<T> extends Node {
  public final Maybe<T> maybe;
  public final MaybeType genType;

  public MaybeNode(@NotNull Maybe<T> maybe, @NotNull MaybeType genType) {
    this.maybe = maybe;
    this.genType = genType;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.Maybe;
  }

  @NotNull
  @Override
  public GenType genType() {
    return genType;
  }
}
