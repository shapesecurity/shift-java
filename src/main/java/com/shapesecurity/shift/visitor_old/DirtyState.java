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

package com.shapesecurity.shift.visitor_old;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.Thunk;

import org.jetbrains.annotations.NotNull;

public class DirtyState<T> {
  @NotNull
  public final T node;
  public final boolean dirty;

  public DirtyState(@NotNull T node, boolean dirty) {
    this.node = node;
    this.dirty = dirty;
  }

  public static <T> DirtyState<T> clean(@NotNull T node) {
    return new DirtyState<>(node, false);
  }

  public static <T> DirtyState<T> dirty(@NotNull T node) {
    return new DirtyState<>(node, true);
  }

  public <U> DirtyState<U> bind(@NotNull F<T, DirtyState<U>> f) {
    if (this.dirty) {
      DirtyState<U> b = f.apply(this.node);
      if (b.dirty) {
        return b;
      }
      return new DirtyState<>(b.node, true);
    } else {
      return f.apply(this.node);
    }
  }

  public <U> DirtyState<Thunk<U>> bindLast(@NotNull final F<T, U> f) {
    return new DirtyState<>(Thunk.from(() -> f.apply(DirtyState.this.node)), this.dirty);
  }

  public DirtyState<T> onDirty(@NotNull F<T, DirtyState<T>> f) {
    if (this.dirty) {
      return f.apply(this.node);
    }
    return this;
  }

  @NotNull
  public DirtyState<T> setDirty() {
    if (this.dirty) {
      return this;
    }
    return new DirtyState<>(this.node, true);
  }
}
