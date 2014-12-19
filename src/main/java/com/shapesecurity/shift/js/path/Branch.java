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

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.EitherNode;
import com.shapesecurity.shift.js.ast.ListNode;
import com.shapesecurity.shift.js.ast.MaybeNode;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.types.EitherType;
import com.shapesecurity.shift.js.ast.types.GenType;
import com.shapesecurity.shift.js.ast.types.ListType;
import com.shapesecurity.shift.js.ast.types.MaybeType;

public interface Branch {
  @Nullable
  Node view(@Nonnull Node parent);

  @Nonnull
  Node set(@Nonnull Node parent, @Nonnull Node child);

  @SuppressWarnings("unchecked")
  @Nonnull
  static Node wrap(@Nonnull Object obj, @Nonnull GenType suggestedType) {
    if (obj instanceof Either) {
      return new EitherNode((Either) obj, (EitherType) suggestedType);
    } else if (obj instanceof List) {
      return new ListNode((List) obj, (ListType) suggestedType);
    } else if (obj instanceof Maybe) {
      return new MaybeNode<>((Maybe) obj, (MaybeType) suggestedType);
    } else {
      return (Node) obj;
    }
  }
}
