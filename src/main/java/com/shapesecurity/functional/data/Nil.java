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

public final class Nil<T> extends ImmutableList<T> {
    private final static int DEFAULT_HASH_CODE;

    static {
        int h = HashCodeBuilder.init();
        DEFAULT_HASH_CODE = HashCodeBuilder.put(h, "Nil");
    }

    Nil() {
        super(0);
    }

    @Override
    protected int calcHashCode() {
        return DEFAULT_HASH_CODE;
    }

    @NotNull
    @Override
    public <A> A foldLeft(@NotNull F2<A, ? super T, A> f, @NotNull A init) {
        return init;
    }

    @NotNull
    @Override
    public <A> A foldRight(@NotNull F2<? super T, A, A> f, @NotNull A init) {
        return init;
    }

    @NotNull
    @Override
    public Maybe<T> maybeHead() {
        return Maybe.nothing();
    }

    @NotNull
    @Override
    public Maybe<T> maybeLast() {
        return Maybe.nothing();
    }

    @NotNull
    @Override
    public Maybe<ImmutableList<T>> maybeTail() {
        return Maybe.nothing();
    }

    @NotNull
    @Override
    public Maybe<ImmutableList<T>> maybeInit() {
        return Maybe.nothing();
    }

    @NotNull
    @Override
    public ImmutableList<T> filter(@NotNull F<T, Boolean> f) {
        return this;
    }

    @NotNull
    @Override
    public <B> ImmutableList<B> map(@NotNull F<T, B> f) {
        return nil();
    }

    @NotNull
    @Override
    public <B> ImmutableList<B> mapWithIndex(@NotNull F2<Integer, T, B> f) {
        return nil();
    }

    @NotNull
    @Override
    public ImmutableList<T> take(int n) {
        return this;
    }

    @NotNull
    @Override
    public ImmutableList<T> drop(int n) {
        return this;
    }

    @NotNull
    @Override
    public Maybe<NonEmptyImmutableList<T>> toNonEmptyList() {
        return Maybe.nothing();
    }

    @NotNull
    @Override
    public <B> Maybe<B> decons(@NotNull F2<T, ImmutableList<T>, B> f) {
        return Maybe.nothing();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <B, C> ImmutableList<C> zipWith(@NotNull F2<T, B, C> f, @NotNull ImmutableList<B> list) {
        return (ImmutableList<C>) this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <B extends T> ImmutableList<T> append(@NotNull ImmutableList<B> defaultClause) {
        // This is safe due to erasure.
        return (ImmutableList<T>) defaultClause;
    }

    @Override
    public boolean exists(@NotNull F<T, Boolean> f) {
        return false;
    }

    @NotNull
    @Override
    public Pair<ImmutableList<T>, ImmutableList<T>> span(@NotNull F<T, Boolean> f) {
        return new Pair<>(nil(), nil());
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <B> ImmutableList<B> flatMap(@NotNull F<T, ImmutableList<B>> f) {
        return (ImmutableList<B>) this;
    }

    @NotNull
    @Override
    public ImmutableList<T> removeAll(@NotNull F<T, Boolean> f) {
        return this;
    }

    @NotNull
    @Override
    public ImmutableList<T> reverse() {
        return this;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public <B extends T> ImmutableList<T> patch(int index, int patchLength, @NotNull ImmutableList<B> replacements) {
        return (ImmutableList<T>) replacements;
    }

    @NotNull
    @Override
    public <B, C> Pair<B, ImmutableList<C>> mapAccumL(@NotNull F2<B, T, Pair<B, C>> f, @NotNull B acc) {
        return new Pair<>(acc, ImmutableList.nil());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
