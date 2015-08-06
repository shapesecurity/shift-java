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

import org.jetbrains.annotations.NotNull;

public final class NonEmptyImmutableList<T> extends ImmutableList<T> {
    @NotNull
    public final T head;

    @NotNull
    public final ImmutableList<T> tail;

    protected NonEmptyImmutableList(@NotNull T head, @NotNull final ImmutableList<T> tail) {
        super(tail.length + 1);
        this.head = head;
        this.tail = tail;
    }

    @NotNull
    public ImmutableList<T> tail() {
        return tail;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NonEmptyImmutableList)) {
            return false;
        }

        NonEmptyImmutableList<T> list = (NonEmptyImmutableList<T>) o;
        return this.head.equals(list.head) && this.tail().equals(list.tail());
    }

    @NotNull
    @Override
    public <A> A foldLeft(@NotNull F2<A, ? super T, A> f, @NotNull A init) {
        ImmutableList<T> list = this;
        while (list instanceof NonEmptyImmutableList) {
            init = f.apply(init, ((NonEmptyImmutableList<T>) list).head);
            list = ((NonEmptyImmutableList<T>) list).tail();
        }
        return init;
    }

    @NotNull
    @Override
    public <A> A foldRight(@NotNull F2<? super T, A, A> f, @NotNull A init) {
        return f.apply(this.head, this.tail().foldRight(f, init));
    }

    @NotNull
    public T reduceLeft(@NotNull F2<T, ? super T, T> f) {
        return this.tail.foldLeft(f, this.head);
    }

    @NotNull
    public T reduceRight(@NotNull F2<? super T, T, T> f) {
        return this.init().foldRight(f, this.last());
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
    public Maybe<ImmutableList<T>> maybeTail() {
        return Maybe.just(this.tail());
    }

    @NotNull
    @Override
    public Maybe<ImmutableList<T>> maybeInit() {
        if (this.tail().isEmpty()) {
            return Maybe.just(nil());
        }
        return this.tail().maybeInit().map(t -> t.cons(this.head));
    }

    @NotNull
    public final T last() {
        NonEmptyImmutableList<T> nel = this;
        while (true) {
            if (nel.tail().isEmpty()) {
                return nel.head;
            }
            nel = (NonEmptyImmutableList<T>) nel.tail();
        }
    }

    @NotNull
    public final ImmutableList<T> init() {
        if (this.tail().isEmpty()) {
            return nil();
        }
        return cons(this.head, ((NonEmptyImmutableList<T>) this.tail()).init());
    }

    @NotNull
    @Override
    public ImmutableList<T> filter(@NotNull F<T, Boolean> f) {
        return f.apply(this.head) ? cons(this.head, this.tail().filter(f)) : this.tail().filter(f);
    }

    @NotNull
    @Override
    public <B> NonEmptyImmutableList<B> map(@NotNull F<T, B> f) {
        return ImmutableList.cons(f.apply(this.head), this.tail().map(f));
    }

    @Override
    @NotNull
    public final <B> NonEmptyImmutableList<B> mapWithIndex(@NotNull F2<Integer, T, B> f) {
        int length = this.length;
        @SuppressWarnings("unchecked")
        B[] result = (B[]) new Object[length];
        ImmutableList<T> list = this;
        for (int i = 0; i < length; i++) {
            result[i] = f.apply(i, ((NonEmptyImmutableList<T>) list).head);
            list = ((NonEmptyImmutableList<T>) list).tail();
        }
        ImmutableList<B> nList = nil();
        for (int i = length - 1; i >= 0; i--) {
            nList = nList.cons(result[i]);
        }
        return (NonEmptyImmutableList<B>) nList;
    }

    @NotNull
    @Override
    public ImmutableList<T> take(int n) {
        if (n <= 0) {
            return nil();
        }
        return cons(this.head, this.tail().take(n - 1));
    }

    @NotNull
    @Override
    public ImmutableList<T> drop(int n) {
        if (n <= 0) {
            return this;
        }
        return this.tail().drop(n - 1);
    }

    @NotNull
    @Override
    public Maybe<NonEmptyImmutableList<T>> toNonEmptyList() {
        return Maybe.just(this);
    }

    @NotNull
    @Override
    public <B> Maybe<B> decons(@NotNull F2<T, ImmutableList<T>, B> f) {
        return Maybe.just(f.apply(this.head, this.tail()));
    }

    @NotNull
    @Override
    public <B, C> ImmutableList<C> zipWith(@NotNull F2<T, B, C> f, @NotNull ImmutableList<B> list) {
        if (list instanceof NonEmptyImmutableList) {
            NonEmptyImmutableList<B> nonEmptyList = (NonEmptyImmutableList<B>) list;
            return ImmutableList.cons(f.apply(this.head, nonEmptyList.head), this.tail().zipWith(f, nonEmptyList.tail()));
        }
        return nil();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NotNull
    @Override
    public <B extends T> ImmutableList<T> append(@NotNull ImmutableList<B> defaultClause) {
        return cons(this.head, this.tail().append(defaultClause));
    }

    @Override
    public boolean exists(@NotNull F<T, Boolean> f) {
        return f.apply(this.head) || this.tail().exists(f);
    }

    @NotNull
    @Override
    public Pair<ImmutableList<T>, ImmutableList<T>> span(@NotNull F<T, Boolean> f) {
        if (!f.apply(this.head)) {
            return new Pair<>(nil(), this);
        }
        Pair<ImmutableList<T>, ImmutableList<T>> s = this.tail().span(f);
        return new Pair<>(s.a.cons(this.head), s.b);
    }

    @NotNull
    @Override
    public <B> ImmutableList<B> flatMap(@NotNull F<T, ImmutableList<B>> f) {
        return f.apply(this.head).append(this.tail().flatMap(f));
    }

    @NotNull
    @Override
    public ImmutableList<T> removeAll(@NotNull F<T, Boolean> f) {
        if (f.apply(this.head)) {
            return this.tail().removeAll(f);
        }
        return cons(this.head, this.tail().removeAll(f));
    }

    @NotNull
    @Override
    public NonEmptyImmutableList<T> reverse() {
        return this.reverse(nil());
    }

    @NotNull
    @Override
    public <B, C> Pair<B, ImmutableList<C>> mapAccumL(@NotNull F2<B, T, Pair<B, C>> f, @NotNull B acc) {
        Pair<B, C> pair = f.apply(acc, this.head);
        Pair<B, ImmutableList<C>> bListPair = this.tail().mapAccumL(f, pair.a);
        return new Pair<>(bListPair.a, ImmutableList.cons(pair.b, bListPair.b));
    }

    @NotNull
    private NonEmptyImmutableList<T> reverse(@NotNull ImmutableList<T> acc) {
        if (this.tail().isEmpty()) {
            return acc.cons(this.head);
        }
        return ((NonEmptyImmutableList<T>) this.tail()).reverse(cons(this.head, acc));
    }

    @Override
    protected int calcHashCode() {
        int start = HashCodeBuilder.init();
        start = HashCodeBuilder.put(start, "List");
        start = HashCodeBuilder.put(start, head);
        return HashCodeBuilder.put(start, tail);
    }
}

