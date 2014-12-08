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

import javax.annotation.Nonnull;

public final class Nil<T> extends List<T> {
  private final static int DEFAULT_HASH_CODE;

  static {
    int h = HashCodeBuilder.init();
    DEFAULT_HASH_CODE = HashCodeBuilder.put(h, "Nil");
  }

  Nil() {
    super();
  }

  @Override
  protected int calcLength() {
    return 0;
  }

  @Override
  protected int calcHashCode() {
    return DEFAULT_HASH_CODE;
  }

  @Nonnull
  @Override
  public <A> A foldLeft(@Nonnull F2<A, ? super T, A> f, @Nonnull A init) {
    return init;
  }

  @Nonnull
  @Override
  public <A> A foldRight(@Nonnull F2<? super T, A, A> f, @Nonnull A init) {
    return init;
  }

  @Nonnull
  @Override
  public Maybe<T> maybeHead() {
    return Maybe.nothing();
  }

  @Nonnull
  @Override
  public Maybe<T> maybeLast() {
    return Maybe.nothing();
  }

  @Nonnull
  @Override
  public Maybe<List<T>> maybeTail() {
    return Maybe.nothing();
  }

  @Nonnull
  @Override
  public Maybe<List<T>> maybeInit() {
    return Maybe.nothing();
  }

  @Override
  public int length() {
    return 0;
  }

  @Nonnull
  @Override
  public List<T> filter(@Nonnull F<T, Boolean> f) {
    return this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <B> List<B> map(@Nonnull F<T, B> f) {
    return (List<B>) this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  protected final <B> List<B> mapWithIndex(@Nonnull F2<Integer, T, B> f, int index) {
    return (List<B>) this;
  }

  @Nonnull
  @Override
  public List<T> take(int n) {
    return this;
  }

  @Nonnull
  @Override
  public List<T> drop(int n) {
    return this;
  }

  @Nonnull
  @Override
  public Maybe<NonEmptyList<T>> toNonEmptyList() {
    return Maybe.nothing();
  }

  @Nonnull
  @Override
  public <B> Maybe<B> decons(@Nonnull F2<T, List<T>, B> f) {
    return Maybe.nothing();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <B, C> List<C> zipWith(@Nonnull F2<T, B, C> f, @Nonnull List<B> list) {
    return (List<C>) this;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <B extends T> List<T> append(@Nonnull List<B> defaultClause) {
    // This is safe due to erasure.
    return (List<T>) defaultClause;
  }

  @Override
  public boolean exists(@Nonnull F<T, Boolean> f) {
    return false;
  }

  @Nonnull
  @Override
  public Pair<List<T>, List<T>> span(@Nonnull F<T, Boolean> f) {
    return new Pair<>(List.<T>nil(), List.<T>nil());
    // return new P2<>(nil(), nil());
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public <B> List<B> flatMap(@Nonnull F<T, List<B>> f) {
    return (List<B>) this;
  }

  @Nonnull
  @Override
  public List<T> removeAll(@Nonnull F<T, Boolean> f) {
    return this;
  }

  @Nonnull
  @Override
  public List<T> reverse() {
    return this;
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  @Override
  public <B extends T> List<T> patch(int index, int patchLength, @Nonnull List<B> replacements) {
    return (List<T>) replacements;
  }

  @Nonnull
  @Override
  public <B, C> Pair<B, List<C>> mapAccumL(@Nonnull F2<B, T, Pair<B, C>> f, @Nonnull B acc) {
    return new Pair<>(acc, List.<C>nil());
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    return this == o;
  }
}
