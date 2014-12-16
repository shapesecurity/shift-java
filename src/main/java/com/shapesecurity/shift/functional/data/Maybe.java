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

package com.shapesecurity.shift.functional.data;

import com.shapesecurity.shift.functional.F;
import com.shapesecurity.shift.functional.Thunk;
import com.shapesecurity.shift.functional.Unit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Maybe<A> {
  private final static Maybe<Object> NOTHING = new Nothing<>();

  // class local
  private Maybe() {
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <A> Maybe<A> nothing() {
    return (Maybe<A>) NOTHING;
  }

  @Nonnull
  public static <A> Maybe<A> just(@Nonnull A a) {
    return new Just<>(a);
  }

  @Nonnull
  public static <A> Maybe<A> fromNullable(@Nullable A a) {
    if (a == null) {
      return nothing();
    }
    return just(a);
  }

  @Nullable
  public static <A> A toNullable(@Nonnull Maybe<A> maybe) {
    if (maybe instanceof Nothing) {
      return null;
    }
    return maybe.just();
  }

  @Nonnull
  public static <A> List<A> catMaybes(@Nonnull List<Maybe<A>> l) {
    return l.foldRight((a, b) -> a.maybe(b, c -> List.<A>cons(c, b)), List.<A>nil());
  }

  @Nonnull
  public static <A, B> List<B> mapMaybe(@Nonnull final F<A, B> f, @Nonnull List<Maybe<A>> l) {
    return l.foldRight((a, b) -> a.maybe(b, v -> List.<B>cons(f.apply(v), b)), List.<B>nil());
  }

  @SuppressWarnings("BooleanParameter")
  @Nonnull
  public static <A> Maybe<A> iff(boolean test, @Nonnull A a) {
    if (test) {
      return just(a);
    }
    return nothing();
  }

  public abstract boolean eq(@Nonnull Maybe<A> maybe);

  @SuppressWarnings("unchecked")
  @Override
  public final boolean equals(Object obj) {
    return obj == this || obj instanceof Maybe && this.eq((Maybe<A>) obj);
  }

  @Override
  public abstract int hashCode();

  @Nonnull
  public abstract A just() throws NullPointerException;

  @Nonnull
  public abstract <B> B maybe(@Nonnull B def, @Nonnull F<A, B> f);

  public final void foreach(@Nonnull F<A, Unit> f) {
    map(f);
  }

  public abstract boolean isJust();

  public final boolean isNothing() {
    return !this.isJust();
  }

  @Nonnull
  public abstract List<A> toList();

  @Nonnull
  public abstract A orJust(@Nonnull A a);

  @Nonnull
  public abstract A orJustLazy(@Nonnull Thunk<A> a);

  @Nonnull
  public abstract <B> Maybe<B> map(@Nonnull F<A, B> f);

  @Nonnull
  public final <B> Maybe<B> bind(@Nonnull F<A, Maybe<B>> f) {
    return this.flatMap(f);
  }

  @Nonnull
  public abstract <B> Maybe<B> flatMap(@Nonnull F<A, Maybe<B>> f);

  private static class Just<A> extends Maybe<A> {
    @Nonnull
    private final A value;
    private Thunk<Integer> hashCodeThunk = Thunk.from(this::calcHashCode);

    private Just(@Nonnull A value) {
      super();
      this.value = value;
    }

    private int calcHashCode() {
      return HashCodeBuilder.put(this.value.hashCode(), "Just");
    }

    @Override
    public boolean eq(@Nonnull Maybe<A> maybe) {
      return maybe instanceof Just && maybe.just().equals(this.value);
    }

    @Override
    public int hashCode() {
      return this.hashCodeThunk.get();
    }

    @Nonnull
    @Override
    public A just() throws NullPointerException {
      return this.value;
    }

    @Nonnull
    @Override
    public <B> B maybe(@Nonnull B def, @Nonnull F<A, B> f) {
      return f.apply(this.value);
    }

    @Override
    public boolean isJust() {
      return true;
    }

    @Nonnull
    @Override
    public List<A> toList() {
      return List.cons(this.value, List.<A>nil());
      // return List.cons(this.value, List.nil());
    }

    @Nonnull
    @Override
    public A orJust(@Nonnull A a) {
      return this.value;
    }

    @Nonnull
    @Override
    public A orJustLazy(@Nonnull Thunk<A> a) {
      return this.value;
    }

    @Nonnull
    @Override
    public <B> Maybe<B> map(@Nonnull F<A, B> f) {
      return Maybe.just(f.apply(this.value));
    }

    @Nonnull
    @Override
    public <B> Maybe<B> flatMap(@Nonnull F<A, Maybe<B>> f) {
      return f.apply(this.value);
    }
  }

  private static class Nothing<A> extends Maybe<A> {
    private final static int HASH_CODE = HashCodeBuilder.put(HashCodeBuilder.init(), "Nothing");

    @Override
    public boolean eq(@Nonnull Maybe<A> maybe) {
      return maybe == this;
    }

    @Override
    public final int hashCode() {
      return HASH_CODE;
    }

    @Nonnull
    @Override
    public A just() throws NullPointerException {
      throw new NullPointerException("Maybe.just failed");
    }

    @Nonnull
    @Override
    public <B> B maybe(@Nonnull B def, @Nonnull F<A, B> f) {
      return def;
    }

    @Override
    public boolean isJust() {
      return false;
    }

    @Nonnull
    @Override
    public List<A> toList() {
      return List.nil();
    }

    @Nonnull
    @Override
    public A orJust(@Nonnull A a) {
      return a;
    }

    @Nonnull
    @Override
    public A orJustLazy(@Nonnull Thunk<A> a) {
      return a.get();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <B> Maybe<B> map(@Nonnull F<A, B> f) {
      return (Maybe<B>) NOTHING;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <B> Maybe<B> flatMap(@Nonnull F<A, Maybe<B>> f) {
      return (Maybe<B>) NOTHING;
    }
  }
}
