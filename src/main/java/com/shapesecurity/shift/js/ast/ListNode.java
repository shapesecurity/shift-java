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

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.types.GenType;
import com.shapesecurity.shift.js.ast.types.ListType;
import com.shapesecurity.shift.js.ast.types.Type;

public class ListNode<T> extends Node {
  public final List<T> list;
  public final ListType genType;

  public ListNode(@Nonnull List<T> list, @Nonnull ListType genType) {
    this.list = list;
    this.genType = genType;
  }

  @Nonnull
  @Override
  public Type type() {
    return Type.List;
  }

  @Nonnull
  @Override
  public GenType genType() {
    return genType;
  }
}
