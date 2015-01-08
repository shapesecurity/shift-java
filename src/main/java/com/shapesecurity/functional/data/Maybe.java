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
import com.shapesecurity.functional.Thunk;
import com.shapesecurity.functional.Unit;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Maybe<A> {
  private final static Maybe<Object> NOTHING = new Nothing<>();

  // class local
  private Maybe() {
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <A> Maybe<A> nothing() {
    return (Maybe<A>) NOTHING;
  }

  @NotNull
  public static <A> Maybe<A> just(@NotNull A a) {
    return new Just<>(a);
  }

  @NotNull
  public static <A> Maybe<A> fromNullable(@Nullable A a) {
    if (a == null) {
      return nothing();
    }
    return just(a);
  }

  @Nullable
  public abstract A toNullable();

  @NotNull
  public static <A> List<A> catMaybes(@NotNull List<Maybe<A>> l) {
    return l.foldRight((a, b) -> a.maybe(b, c -> List.cons(c, b)), List.nil());
  }

  @NotNull
  public static <A, B> List<B> mapMaybe(@NotNull final F<A, B> f, @NotNull List<Maybe<A>> l) {
    return l.foldRight((a, b) -> a.maybe(b, v -> List.cons(f.apply(v), b)), List.nil());
  }

  @SuppressWarnings("BooleanParameter")
  @NotNull
  public static <A> Maybe<A> iff(boolean test, @NotNull A a) {
    if (test) {
      return just(a);
    }
    return nothing();
  }

  public abstract boolean eq(@NotNull Maybe<A> maybe);

  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(Object obj) {
    return obj == this || obj instanceof Maybe && this.eq((Maybe<A>) obj);
  }

  @Override
  public abstract int hashCode();

  @NotNull
  public abstract A just() throws NullPointerException;

  @NotNull
  public abstract <B> B maybe(@NotNull B def, @NotNull F<A, B> f);

  public final void foreach(@NotNull F<A, Unit> f) {
    map(f);
  }

  public abstract boolean isJust();

  public final boolean isNothing() {
    return !this.isJust();
  }

  @NotNull
  public abstract List<A> toList();

  @NotNull
  public abstract A orJust(@NotNull A a);

  @NotNull
  public abstract A orJustLazy(@NotNull Thunk<A> a);

  @NotNull
  public abstract <B> Maybe<B> map(@NotNull F<A, B> f);

  @NotNull
  public final <B> Maybe<B> bind(@NotNull F<A, Maybe<B>> f) {
    return this.flatMap(f);
  }

  @NotNull
  public abstract <B> Maybe<B> flatMap(@NotNull F<A, Maybe<B>> f);

  private static class Just<A> extends Maybe<A> {
    @NotNull
    private final A value;
    private Thunk<Integer> hashCodeThunk = Thunk.from(this::calcHashCode);

    private Just(@NotNull A value) {
      super();
      this.value = value;
    }

    private int calcHashCode() {
      return HashCodeBuilder.put(this.value.hashCode(), "Just");
    }

    @Nullable
    @Override
    public A toNullable() {
      return value;
    }

    @Override
    public boolean eq(@NotNull Maybe<A> maybe) {
      return maybe instanceof Just && maybe.just().equals(this.value);
    }

    @Override
    public int hashCode() {
      return this.hashCodeThunk.get();
    }

    @NotNull
    @Override
    public A just() throws NullPointerException {
      return this.value;
    }

    @NotNull
    @Override
    public <B> B maybe(@NotNull B def, @NotNull F<A, B> f) {
      return f.apply(this.value);
    }

    @Override
    public boolean isJust() {
      return true;
    }

    @NotNull
    @Override
    public List<A> toList() {
      return List.cons(this.value, List.nil());
    }

    @NotNull
    @Override
    public A orJust(@NotNull A a) {
      return this.value;
    }

    @NotNull
    @Override
    public A orJustLazy(@NotNull Thunk<A> a) {
      return this.value;
    }

    @NotNull
    @Override
    public <B> Maybe<B> map(@NotNull F<A, B> f) {
      return Maybe.just(f.apply(this.value));
    }

    @NotNull
    @Override
    public <B> Maybe<B> flatMap(@NotNull F<A, Maybe<B>> f) {
      return f.apply(this.value);
    }
  }

  private static class Nothing<A> extends Maybe<A> {
    private final static int HASH_CODE = HashCodeBuilder.put(HashCodeBuilder.init(), "Nothing");

    @Nullable
    @Override
    public A toNullable() {
      return null;
    }

    @Override
    public boolean eq(@NotNull Maybe<A> maybe) {
      return maybe == this;
    }

    @Override
    public final int hashCode() {
      return HASH_CODE;
    }

    @NotNull
    @Override
    public A just() throws NullPointerException {
      throw new NullPointerException("Maybe.just failed");
    }

    @NotNull
    @Override
    public <B> B maybe(@NotNull B def, @NotNull F<A, B> f) {
      return def;
    }

    @Override
    public boolean isJust() {
      return false;
    }

    @NotNull
    @Override
    public List<A> toList() {
      return List.nil();
    }

    @NotNull
    @Override
    public A orJust(@NotNull A a) {
      return a;
    }

    @NotNull
    @Override
    public A orJustLazy(@NotNull Thunk<A> a) {
      return a.get();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <B> Maybe<B> map(@NotNull F<A, B> f) {
      return (Maybe<B>) NOTHING;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <B> Maybe<B> flatMap(@NotNull F<A, Maybe<B>> f) {
      return (Maybe<B>) NOTHING;
    }
  }
}
