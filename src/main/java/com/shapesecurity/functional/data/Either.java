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

public abstract class Either<A, B> {
  // class local
  private Either() {
    super();
  }

  @NotNull
  public static <A, B> Left<A, B> left(@NotNull A a) {
    return new Left<>(a);
  }

  @NotNull
  public static <A, B> Right<A, B> right(@NotNull B b) {
    return new Right<>(b);
  }

  @NotNull
  public static <A, B extends A, C extends A> A extract(Either<B, C> e) {
    return e.either(x -> x, x -> x);
  }

  public abstract boolean isLeft();

  public final boolean isRight() {
    return !this.isLeft();
  }

  public abstract <X> X either(F<A, X> f1, F<B, X> f2);

  @NotNull
  public abstract <X, Y> Either<X, Y> map(F<A, X> f1, F<B, Y> f2);

  @NotNull
  public abstract <X> Either<X, B> mapLeft(@NotNull F<A, X> f);

  @NotNull
  public abstract <Y> Either<A, Y> mapRight(@NotNull F<B, Y> f);

  @NotNull
  public abstract Maybe<A> left();

  @NotNull
  public abstract Maybe<B> right();

  public abstract boolean eq(Either<A, B> either);

  @Override
  public abstract int hashCode();

  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(Object object) {
    return this == object || object instanceof Either && this.eq((Either<A, B>) object);
  }

  public static final class Left<A, B> extends Either<A, B> {
    @NotNull
    public final A a;

    private Left(@NotNull A a) {
      super();
      this.a = a;
    }

    @Override
    public int hashCode() {
      return this.a.hashCode();
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public <X> X either(F<A, X> f1, F<B, X> f2) {
      return f1.apply(this.a);
    }

    @NotNull
    @Override
    public <X, Y> Left<X, Y> map(F<A, X> f1, F<B, Y> f2) {
      return Either.left(f1.apply(this.a));
    }

    @NotNull
    @Override
    public <X> Left<X, B> mapLeft(@NotNull F<A, X> f) {
      return Either.left(f.apply(this.a));
    }

    @NotNull
    @Override
    public Maybe<A> left() {
      return Maybe.just(this.a);
    }

    @NotNull
    @Override
    public Maybe<B> right() {
      return Maybe.nothing();
    }

    @Override
    public boolean eq(Either<A, B> either) {
      return either instanceof Left && this.a.equals(((Left) either).a);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <C> Left<A, C> mapRight(@NotNull F<B, C> f) {
      return (Left<A, C>) this;
    }
  }

  public static final class Right<A, B> extends Either<A, B> {
    @NotNull
    public final B b;

    private Right(@NotNull B b) {
      super();
      this.b = b;
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public int hashCode() {
      return this.b.hashCode();
    }

    @Override
    public <X> X either(F<A, X> f1, F<B, X> f2) {
      return f2.apply(this.b);
    }

    @NotNull
    @Override
    public <X, Y> Either<X, Y> map(F<A, X> f1, F<B, Y> f2) {
      return Either.right(f2.apply(this.b));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <X> Right<X, B> mapLeft(@NotNull F<A, X> f) {
      return (Right<X, B>) this;
    }

    @NotNull
    @Override
    public <C> Right<A, C> mapRight(@NotNull F<B, C> f) {
      return Either.right(f.apply(this.b));
    }

    @NotNull
    @Override
    public Maybe<A> left() {
      return Maybe.nothing();
    }

    @NotNull
    @Override
    public Maybe<B> right() {
      return Maybe.just(this.b);
    }

    @Override
    public boolean eq(Either<A, B> either) {
      return either instanceof Right && this.b.equals(((Right) either).b);
    }
  }
}
