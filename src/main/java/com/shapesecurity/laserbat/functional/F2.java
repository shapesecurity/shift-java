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
public interface F2<A, B, C> {
  @Nonnull
  public abstract C apply(@Nonnull A a, @Nonnull B b);

  @Nonnull
  public default F<B, C> curry(@Nonnull final A a) {
    return b -> apply(a, b);
  }

  @Nonnull
  public default F2<B, A, C> flip() {
    return (b, a) -> apply(a, b);
  }
}
