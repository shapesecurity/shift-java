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

package com.shapesecurity.laserbat.functional.data;

import com.shapesecurity.laserbat.functional.F;
import com.shapesecurity.laserbat.functional.F2;
import com.shapesecurity.laserbat.functional.Pair;
import com.shapesecurity.laserbat.functional.Thunk;

import javax.annotation.Nonnull;

public abstract class NonEmptyList<T> extends List<T> {
  @Nonnull
  public final T head;

  protected NonEmptyList(@Nonnull T head) {
    super();
    this.head = head;
  }

  @Nonnull
  public abstract List<T> tail();

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NonEmptyList)) {
      return false;
    }

    NonEmptyList<T> list = (NonEmptyList<T>) o;
    return this.head.equals(list.head) && this.tail().equals(list.tail());
  }

  @Nonnull
  @Override
  public <A> A foldLeft(@Nonnull F2<A, ? super T, A> f, @Nonnull A init) {
    List<T> list = this;
    while (list instanceof NonEmptyList) {
      init = f.apply(init, ((NonEmptyList<T>) list).head);
      list = ((NonEmptyList<T>) list).tail();
    }
    return init;
  }

  @Nonnull
  @Override
  public <A> A foldRight(@Nonnull F2<? super T, A, A> f, @Nonnull A init) {
    return f.apply(this.head, this.tail().foldRight(f, init));
  }

  @Nonnull
  @Override
  public Maybe<T> maybeHead() {
    return Maybe.just(this.head);
  }

  @Nonnull
  @Override
  public Maybe<T> maybeLast() {
    if (this.tail().isEmpty()) {
      return Maybe.just(this.head);
    }
    return this.tail().maybeLast();
  }

  @Nonnull
  @Override
  public Maybe<List<T>> maybeTail() {
    return Maybe.just(this.tail());
  }

  @Nonnull
  @Override
  public Maybe<List<T>> maybeInit() {
    if (this.tail().isEmpty()) {
      return Maybe.just(List.<T>nil());
    }
    return this.tail().maybeInit().map(t -> t.cons(this.head));
  }

  @Nonnull
  public final T last() {
    NonEmptyList<T> nel = this;
    while (true) {
      if (nel.tail().isEmpty()) {
        return nel.head;
      }
      nel = (NonEmptyList<T>) nel.tail();
    }
  }

  @Nonnull
  public final List<T> init() {
    if (this.tail().isEmpty()) {
      return nil();
    }
    return cons(this.head, ((NonEmptyList<T>) this.tail()).init());
  }

  @Nonnull
  @Override
  public List<T> filter(@Nonnull F<T, Boolean> f) {
    return f.apply(this.head) ? cons(this.head, this.tail().filter(f)) : this.tail().filter(f);
  }

  @Nonnull
  @Override
  public <B> NonEmptyList<B> map(@Nonnull F<T, B> f) {
    return List.cons(f.apply(this.head), this.tail().map(f));
  }

  @Override
  @Nonnull
  public final <B> NonEmptyList<B> mapWithIndex(@Nonnull F2<Integer, T, B> f) {
    return this.mapWithIndex(f, 0);
  }

  @Nonnull
  @Override
  protected final <B> NonEmptyList<B> mapWithIndex(@Nonnull F2<Integer, T, B> f, int i) {
    return List.cons(f.apply(i, this.head), this.tail().mapWithIndex(f, i + 1));
  }

  @Nonnull
  @Override
  public List<T> take(int n) {
    if (n <= 0) {
      return nil();
    }
    return cons(this.head, this.tail().take(n - 1));
  }

  @Nonnull
  @Override
  public List<T> drop(int n) {
    if (n <= 0) {
      return this;
    }
    return this.tail().drop(n - 1);
  }

  @Nonnull
  @Override
  public Maybe<NonEmptyList<T>> toNonEmptyList() {
    return Maybe.just(this);
  }

  @Nonnull
  @Override
  public <B> Maybe<B> decons(@Nonnull F2<T, List<T>, B> f) {
    return Maybe.just(f.apply(this.head, this.tail()));
  }

  @Nonnull
  @Override
  public <B, C> List<C> zipWith(@Nonnull F2<T, B, C> f, @Nonnull List<B> list) {
    if (list instanceof NonEmptyList) {
      NonEmptyList<B> nonEmptyList = (NonEmptyList<B>) list;
      return List.cons(f.apply(this.head, nonEmptyList.head), this.tail().zipWith(f, nonEmptyList.tail()));
    }
    return nil();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Nonnull
  @Override
  public <B extends T> List<T> append(@Nonnull List<B> defaultClause) {
    return cons(this.head, this.tail().append(defaultClause));
  }

  @Override
  public boolean exists(@Nonnull F<T, Boolean> f) {
    return f.apply(this.head) || this.tail().exists(f);
  }

  @Nonnull
  @Override
  public Pair<List<T>, List<T>> span(@Nonnull F<T, Boolean> f) {
    if (!f.apply(this.head)) {
      return new Pair<>(List.<T>nil(), this);
    }
    Pair<List<T>, List<T>> s = this.tail().span(f);
    return new Pair<>(List.cons(this.head, s.a), s.b);
  }

  @Nonnull
  @Override
  public <B> List<B> flatMap(@Nonnull F<T, List<B>> f) {
    return f.apply(this.head).append(this.tail().flatMap(f));
  }

  @Nonnull
  @Override
  public List<T> removeAll(@Nonnull F<T, Boolean> f) {
    if (f.apply(this.head)) {
      return this.tail().removeAll(f);
    }
    return cons(this.head, this.tail().removeAll(f));
  }

  @Nonnull
  @Override
  public NonEmptyList<T> reverse() {
    return this.reverse(List.<T>nil());
  }

  @Nonnull
  @Override
  public <B, C> Pair<B, List<C>> mapAccumL(@Nonnull F2<B, T, Pair<B, C>> f, @Nonnull B acc) {
    Pair<B, C> pair = f.apply(acc, this.head);
    Pair<B, List<C>> bListPair = this.tail().mapAccumL(f, pair.a);
    return new Pair<>(bListPair.a, List.cons(pair.b, bListPair.b));
  }

  @Nonnull
  private NonEmptyList<T> reverse(@Nonnull List<T> acc) {
    if (this.tail().isEmpty()) {
      return acc.cons(this.head);
    }
    return ((NonEmptyList<T>) this.tail()).reverse(cons(this.head, acc));
  }

  static final class Eager<T> extends NonEmptyList<T> {
    @Nonnull
    public final List<T> tail;

    Eager(@Nonnull final T head, @Nonnull final List<T> tail) {
      super(head);
      this.tail = tail;
    }

    @Nonnull
    @Override
    public List<T> tail() {
      return this.tail;
    }

    @Override
    protected int calcLength() {
      return 1 + tail.length();
    }

    @Override
    protected int calcHashCode() {
      int start = HashCodeBuilder.init();
      start = HashCodeBuilder.put(start, "List");
      start = HashCodeBuilder.put(start, head);
      return HashCodeBuilder.put(start, tail);
    }
  }

  static final class Lazy<T> extends NonEmptyList<T> {
    @Nonnull
    public final Thunk<List<T>> tail;

    Lazy(@Nonnull T head, @Nonnull Thunk<List<T>> tail) {
      super(head);
      this.tail = tail;
    }

    @Nonnull
    @Override
    public List<T> tail() {
      return this.tail.get();
    }

    @Override
    protected int calcLength() {
      return 1 + tail.get().length();
    }

    @Override
    protected int calcHashCode() {
      int start = HashCodeBuilder.init();
      start = HashCodeBuilder.put(start, "List");
      start = HashCodeBuilder.put(start, head);
      return HashCodeBuilder.put(start, tail.get());
    }
  }
}

