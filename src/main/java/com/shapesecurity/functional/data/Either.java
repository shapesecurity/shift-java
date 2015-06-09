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

package com.shapesecurity.functional.data;

import com.shapesecurity.functional.F;

import org.jetbrains.annotations.NotNull;

public final class Either<A, B> {
  private final Object data;

  private final int tag;

  // class local
  private Either(Object data, int tag) {
    super();
    this.data = data;
    this.tag = tag;
  }

  @NotNull
  public static <A, B> Either<A, B> left(@NotNull A a) {
    return new Either<>(a, 0);
  }

  @NotNull
  public static <A, B> Either<A, B> right(@NotNull B b) {
    return new Either<>(b, 1);
  }

  @NotNull
  public static <A, B extends A, C extends A> A extract(Either<B, C> e) {
    return e.either(x -> x, x -> x);
  }

  public final boolean isLeft() {
    return tag == 0;
  }

  public final boolean isRight() {
    return tag == 1;
  }

  @SuppressWarnings("unchecked")
  public <X> X either(F<A, X> f1, F<B, X> f2) {
    if (tag == 0) {
      return f1.apply((A) data);
    } else {
      return f2.apply((B) data);
    }
  }

  @NotNull
  public <X, Y> Either<X, Y> map(F<A, X> f1, F<B, Y> f2) {
    return either(a -> Either.<X, Y>left(f1.apply(a)), b -> Either.<X, Y>right(f2.apply(b)));
  }

  @NotNull
  public <X> Either<X, B> mapLeft(@NotNull F<A, X> f) {
    return map(a -> f.apply(a), b -> b);
  }

  @NotNull
  public <Y> Either<A, Y> mapRight(@NotNull F<B, Y> f) {
    return map(a -> a, b -> f.apply(b));
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public Maybe<A> left() {
    return tag == 0 ? Maybe.just((A) data) : Maybe.nothing();
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public Maybe<B> right() {
    return tag == 1 ? Maybe.just((B) data) : Maybe.nothing();
  }

  public boolean eq(Either<A, B> either) {
    return either.tag == this.tag && either.data.equals(this.data);
  }

  @Override
  public int hashCode() {
    return (0b10101010 << tag) ^ this.data.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(Object object) {
    return this == object || object instanceof Either && this.eq((Either<A, B>) object);
  }
}
