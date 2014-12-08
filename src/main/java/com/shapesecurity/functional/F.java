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

package com.shapesecurity.functional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface F<A, B> {
  @NotNull
  public static <A> F<A, A> id() {
    return o -> o;
  }

  @NotNull
  public static <A, B> F<A, B> constant(@NotNull final B b) {
    return a -> b;
  }

  @NotNull
  public static <A, B, C> F2<A, B, C> uncurry(@NotNull final F<A, F<B, C>> f) {
    return (a, b) -> f.apply(a).apply(b);
  }

  @NotNull
  public static <A, B, C> F<B, F<A, C>> flip(@NotNull final F<A, F<B, C>> f) {
    return b -> a -> f.apply(a).apply(b);
  }

  @NotNull
  public abstract B apply(@NotNull A a);

  @NotNull
  public default <C> F<C, B> compose(@NotNull final F<C, A> f) {
    return c -> this.apply(f.apply(c));
  }
}

