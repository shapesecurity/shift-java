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


package com.shapesecurity.shift.js.path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shapesecurity.shift.functional.F;
import com.shapesecurity.shift.functional.F2;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.types.GenType;

public class TypedBranch<P extends Node, C extends Node> {
  @Nonnull
  private final GenType parentType;

  @Nonnull
  private final GenType childType;

  @Nonnull
  private final F<P, C> view;

  @Nonnull
  private final F2<P, C, P> set;

  protected TypedBranch(
      @Nonnull GenType parentType,
      @Nonnull GenType childType,
      @Nonnull F<P, C> view,
      @Nonnull F2<P, C, P> set) {
    this.parentType = parentType;
    this.childType = childType;
    this.view = view;
    this.set = set;
  }

  protected boolean isValidParent(@Nonnull GenType type) {
    return this.parentType.isAssignableFrom(type);
  }

  public boolean isValidChild(@Nonnull GenType type) {
    return this.childType.isAssignableFrom(type);
  }

  public boolean isValidChild(@Nonnull GenType parentType, @Nonnull GenType type) {
    return isValidChild(type);
  }

  @Nullable
  public C view(@Nonnull P parent) {
    return this.view.apply(parent);
  }
  @Nonnull
  public P set(@Nonnull P parent, @Nonnull C child) {
    return this.set.apply(parent, child);
  }
}
