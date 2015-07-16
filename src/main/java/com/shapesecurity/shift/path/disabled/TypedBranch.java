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


package com.shapesecurity.shift.path.disabled;

import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.types.GenType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TypedBranch<P extends Node, C extends Node> {
  @NotNull
  private final GenType parentType;

  @NotNull
  private final GenType childType;

  TypedBranch(@NotNull GenType parentType, @NotNull GenType childType) {
    this.parentType = parentType;
    this.childType = childType;
  }

  boolean isValidParent(@NotNull GenType type) {
    return this.parentType.isAssignableFrom(type);
  }

  boolean isValidChild(@NotNull GenType type) {
    return this.childType.isAssignableFrom(type);
  }

  public boolean isValidChild(@NotNull GenType parentType, @NotNull GenType type) {
    return isValidChild(type);
  }

  @Nullable
  public abstract C view(@NotNull P parent);

  @NotNull
  public abstract P set(@NotNull P parent, @NotNull C child);
}