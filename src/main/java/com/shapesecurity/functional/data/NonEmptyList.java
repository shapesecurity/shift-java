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
import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.Thunk;

import org.jetbrains.annotations.NotNull;

public abstract class NonEmptyList<T> extends List<T> {
  @NotNull
  public final T head;

  protected NonEmptyList(@NotNull T head) {
    super();
    this.head = head;
  }

  @NotNull
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

  @NotNull
  @Override
  public <A> A foldLeft(@NotNull F2<A, ? super T, A> f, @NotNull A init) {
    List<T> list = this;
    while (list instanceof NonEmptyList) {
      init = f.apply(init, ((NonEmptyList<T>) list).head);
      list = ((NonEmptyList<T>) list).tail();
    }
    return init;
  }

  @NotNull
  @Override
  public <A> A foldRight(@NotNull F2<? super T, A, A> f, @NotNull A init) {
    return f.apply(this.head, this.tail().foldRight(f, init));
  }

  @NotNull
  @Override
  public Maybe<T> maybeHead() {
    return Maybe.just(this.head);
  }

  @NotNull
  @Override
  public Maybe<T> maybeLast() {
    if (this.tail().isEmpty()) {
      return Maybe.just(this.head);
    }
    return this.tail().maybeLast();
  }

  @NotNull
  @Override
  public Maybe<List<T>> maybeTail() {
    return Maybe.just(this.tail());
  }

  @NotNull
  @Override
  public Maybe<List<T>> maybeInit() {
    if (this.tail().isEmpty()) {
      return Maybe.just(List.<T>nil());
    }
    return this.tail().maybeInit().map(t -> t.cons(this.head));
  }

  @NotNull
  public final T last() {
    NonEmptyList<T> nel = this;
    while (true) {
      if (nel.tail().isEmpty()) {
        return nel.head;
      }
      nel = (NonEmptyList<T>) nel.tail();
    }
  }

  @NotNull
  public final List<T> init() {
    if (this.tail().isEmpty()) {
      return nil();
    }
    return cons(this.head, ((NonEmptyList<T>) this.tail()).init());
  }

  @NotNull
  @Override
  public List<T> filter(@NotNull F<T, Boolean> f) {
    return f.apply(this.head) ? cons(this.head, this.tail().filter(f)) : this.tail().filter(f);
  }

  @NotNull
  @Override
  public <B> NonEmptyList<B> map(@NotNull F<T, B> f) {
    return List.cons(f.apply(this.head), this.tail().map(f));
  }

  @Override
  @NotNull
  public final <B> NonEmptyList<B> mapWithIndex(@NotNull F2<Integer, T, B> f) {
    int length = this.length();
    @SuppressWarnings("unchecked")
    B[] result = (B[]) new Object[length];
    List<T> list = this;
    for (int i = 0; i < length; i++) {
      result[i] = f.apply(i, ((NonEmptyList<T>) list).head);
      list = ((NonEmptyList<T>) list).tail();
    }
    List<B> nList = nil();
    for (int i = length - 1; i >= 0; i--) {
      nList = nList.cons(result[i]);
    }
    return (NonEmptyList<B>) nList;
  }

  @NotNull
  @Override
  public List<T> take(int n) {
    if (n <= 0) {
      return nil();
    }
    return cons(this.head, this.tail().take(n - 1));
  }

  @NotNull
  @Override
  public List<T> drop(int n) {
    if (n <= 0) {
      return this;
    }
    return this.tail().drop(n - 1);
  }

  @NotNull
  @Override
  public Maybe<NonEmptyList<T>> toNonEmptyList() {
    return Maybe.just(this);
  }

  @NotNull
  @Override
  public <B> Maybe<B> decons(@NotNull F2<T, List<T>, B> f) {
    return Maybe.just(f.apply(this.head, this.tail()));
  }

  @NotNull
  @Override
  public <B, C> List<C> zipWith(@NotNull F2<T, B, C> f, @NotNull List<B> list) {
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

  @NotNull
  @Override
  public <B extends T> List<T> append(@NotNull List<B> defaultClause) {
    return cons(this.head, this.tail().append(defaultClause));
  }

  @Override
  public boolean exists(@NotNull F<T, Boolean> f) {
    return f.apply(this.head) || this.tail().exists(f);
  }

  @NotNull
  @Override
  public Pair<List<T>, List<T>> span(@NotNull F<T, Boolean> f) {
    if (!f.apply(this.head)) {
      return new Pair<>(List.<T>nil(), this);
    }
    Pair<List<T>, List<T>> s = this.tail().span(f);
    return new Pair<>(List.cons(this.head, s.a), s.b);
  }

  @NotNull
  @Override
  public <B> List<B> flatMap(@NotNull F<T, List<B>> f) {
    return f.apply(this.head).append(this.tail().flatMap(f));
  }

  @NotNull
  @Override
  public List<T> removeAll(@NotNull F<T, Boolean> f) {
    if (f.apply(this.head)) {
      return this.tail().removeAll(f);
    }
    return cons(this.head, this.tail().removeAll(f));
  }

  @NotNull
  @Override
  public NonEmptyList<T> reverse() {
    return this.reverse(List.<T>nil());
  }

  @NotNull
  @Override
  public <B, C> Pair<B, List<C>> mapAccumL(@NotNull F2<B, T, Pair<B, C>> f, @NotNull B acc) {
    Pair<B, C> pair = f.apply(acc, this.head);
    Pair<B, List<C>> bListPair = this.tail().mapAccumL(f, pair.a);
    return new Pair<>(bListPair.a, List.cons(pair.b, bListPair.b));
  }

  @NotNull
  private NonEmptyList<T> reverse(@NotNull List<T> acc) {
    if (this.tail().isEmpty()) {
      return acc.cons(this.head);
    }
    return ((NonEmptyList<T>) this.tail()).reverse(cons(this.head, acc));
  }

  static final class Eager<T> extends NonEmptyList<T> {
    @NotNull
    public final List<T> tail;

    Eager(@NotNull final T head, @NotNull final List<T> tail) {
      super(head);
      this.tail = tail;
    }

    @NotNull
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
    @NotNull
    public final Thunk<List<T>> tail;

    Lazy(@NotNull T head, @NotNull Thunk<List<T>> tail) {
      super(head);
      this.tail = tail;
    }

    @NotNull
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

