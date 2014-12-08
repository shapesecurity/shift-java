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

package com.shapesecurity.laserbat.functional;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface F<A, B> {
  @Nonnull
  public static <A> F<A, A> id() {
    return o -> o;
  }

  @Nonnull
  public static <A, B> F<A, B> constant(@Nonnull final B b) {
    return a -> b;
  }

  @Nonnull
  public static <A, B, C> F2<A, B, C> uncurry(@Nonnull final F<A, F<B, C>> f) {
    return (a, b) -> f.apply(a).apply(b);
  }

  @Nonnull
  public static <A, B, C> F<B, F<A, C>> flip(@Nonnull final F<A, F<B, C>> f) {
    return b -> a -> f.apply(a).apply(b);
  }

  @Nonnull
  public abstract B apply(@Nonnull A a);

  @Nonnull
  public default <C> F<C, B> compose(@Nonnull final F<C, A> f) {
    return c -> this.apply(f.apply(c));
  }
}

