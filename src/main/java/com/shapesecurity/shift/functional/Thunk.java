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

package com.shapesecurity.shift.functional;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Used for lazy evaluation.
// @FunctionalInterface
public final class Thunk<A> {
  @Nonnull
  private final Supplier<A> supplier;
  // Exception on style: private nullable.
  @Nullable
  private volatile A value = null;

  private Thunk(@Nonnull Supplier<A> supplier) {this.supplier = supplier;}

  @Nonnull
  public static <A> Thunk<A> constant(@Nonnull final A value) {
    Thunk<A> t = new Thunk<>(() -> value);
    t.value = value;
    return t;
  }

  @Nonnull
  public static <A> Thunk<A> from(@Nonnull Supplier<A> supplier) {
    return new Thunk<>(supplier);
  }

  @Nonnull
  public final A get() {
    // Double locked.
    if (this.value == null) {
      synchronized (this) {
        if (this.value == null) {
          A v = supplier.get();
          this.value = v;
          return v;
        }
      }
    }
    return this.value;
  }
}
