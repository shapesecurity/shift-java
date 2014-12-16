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
import com.shapesecurity.shift.functional.F2;

import javax.annotation.Nonnull;

public abstract class BinaryTree<T> {
  private static final Empty<Object> EMPTY = new Empty<>();
  private static BinaryTreeMonoid<Object> MONOID = new BinaryTreeMonoid<>();
  public final int length;

  protected BinaryTree(int length) {
    this.length = length;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T> BinaryTree<T> empty() {
    return (BinaryTree<T>) EMPTY;
  }

  @Nonnull
  public static <T> BinaryTree<T> single(@Nonnull T scope) {
    return new Leaf<>(scope);
  }

  @SuppressWarnings("unchecked")
  public static <T> Monoid<BinaryTree<T>> monoid() {
    return (BinaryTreeMonoid<T>) MONOID;
  }

  @Nonnull
  public final List<T> toList() {
    return this.toList(List.<T>nil());
  }

  protected abstract List<T> toList(@Nonnull List<T> acc);

  @Nonnull
  public abstract <B> B foldLeft(@Nonnull F2<B, ? super T, B> f, @Nonnull B init);

  @Nonnull
  public abstract <B> B foldRight(@Nonnull F2<? super T, B, B> f, @Nonnull B init);

  public abstract boolean isEmpty();

  @Nonnull
  public abstract BinaryTree<T> append(@Nonnull BinaryTree<? extends T> defaultClause);

  public abstract boolean exists(@Nonnull F<T, Boolean> f);

  @Nonnull
  public abstract Maybe<T> find(@Nonnull F<T, Boolean> f);

  @Nonnull
  public abstract BinaryTree<T> reverse();

  @Nonnull
  public abstract Maybe<T> index(int index);

  @Nonnull
  public abstract Maybe<BinaryTree<T>> update(int index, @Nonnull T element);

  public final static class Empty<T> extends BinaryTree<T> {
    private Empty() {
      super(0);
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> acc) {
      return acc;
    }

    @Nonnull
    @Override
    public <B> B foldLeft(@Nonnull F2<B, ? super T, B> f, @Nonnull B init) {
      return init;
    }

    @Nonnull
    @Override
    public <B> B foldRight(@Nonnull F2<? super T, B, B> f, @Nonnull B init) {
      return init;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public BinaryTree<T> append(@Nonnull BinaryTree<? extends T> defaultClause) {
      return (BinaryTree<T>) defaultClause;
    }

    @Override
    public boolean exists(@Nonnull F<T, Boolean> f) {
      return false;
    }

    @Nonnull
    @Override
    public Maybe<T> find(@Nonnull F<T, Boolean> f) {
      return Maybe.nothing();
    }

    @Nonnull
    @Override
    public BinaryTree<T> reverse() {
      return this;
    }

    @Nonnull
    @Override
    public Maybe<T> index(int index) {
      return Maybe.nothing();
    }

    @Nonnull
    @Override
    public Maybe<BinaryTree<T>> update(int index, @Nonnull T element) {
      return Maybe.nothing();
    }
  }

  public final static class Leaf<T> extends BinaryTree<T> {
    @Nonnull
    public final T data;

    private Leaf(@Nonnull T data) {
      super(1);
      this.data = data;
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> acc) {
      return acc.cons(this.data);
    }

    @Nonnull
    @Override
    public <B> B foldLeft(@Nonnull F2<B, ? super T, B> f, @Nonnull B init) {
      return f.apply(init, this.data);
    }

    @Nonnull
    @Override
    public <B> B foldRight(@Nonnull F2<? super T, B, B> f, @Nonnull B init) {
      return f.apply(this.data, init);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public BinaryTree<T> append(@Nonnull BinaryTree<? extends T> defaultClause) {
      return new Fork<>(this, (BinaryTree<T>) defaultClause);
    }

    @Override
    public boolean exists(@Nonnull F<T, Boolean> f) {
      return f.apply(this.data);
    }

    @Nonnull
    @Override
    public Maybe<T> find(@Nonnull F<T, Boolean> f) {
      if (f.apply(this.data)) {
        return Maybe.just(this.data);
      }
      return Maybe.nothing();
    }

    @Nonnull
    @Override
    public BinaryTree<T> reverse() {
      return this;
    }

    @Nonnull
    @Override
    public Maybe<T> index(int index) {
      return Maybe.iff(index == 0, this.data);
    }

    @Nonnull
    @Override
    public Maybe<BinaryTree<T>> update(int index, @Nonnull T element) {
      return index == 0 ? Maybe.just(single(element)) : Maybe.<BinaryTree<T>>nothing();
    }
  }

  public final static class Fork<T> extends BinaryTree<T> {
    @Nonnull
    public final BinaryTree<T> left, right;

    private Fork(@Nonnull BinaryTree<T> left, @Nonnull BinaryTree<T> right) {
      super(left.length + right.length);
      this.left = left;
      this.right = right;
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> acc) {
      return this.left.toList(this.right.toList(acc));
    }

    @Nonnull
    @Override
    public <B> B foldLeft(@Nonnull F2<B, ? super T, B> f, @Nonnull B init) {
      return this.right.foldLeft(f, this.left.foldLeft(f, init));
    }

    @Nonnull
    @Override
    public <B> B foldRight(@Nonnull F2<? super T, B, B> f, @Nonnull B init) {
      return this.left.foldRight(f, this.right.foldRight(f, init));
    }

    @Override
    public boolean isEmpty() {
      return this.left.isEmpty() || this.right.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public BinaryTree<T> append(@Nonnull BinaryTree<? extends T> defaultClause) {
      return new Fork<>(this, (BinaryTree<T>) defaultClause);
    }

    @Override
    public boolean exists(@Nonnull F<T, Boolean> f) {
      return this.left.exists(f) || this.right.exists(f);
    }

    @Nonnull
    @Override
    public Maybe<T> find(@Nonnull F<T, Boolean> f) {
      Maybe<T> foundLeft = this.left.find(f);
      if (foundLeft.isNothing()) {
        return this.right.find(f);
      }
      return foundLeft;
    }

    @Nonnull
    @Override
    public Fork<T> reverse() {
      return new Fork<>(this.right.reverse(), this.left.reverse());
    }

    @Nonnull
    @Override
    public Maybe<T> index(int index) {
      if (index >= this.length) {
        return Maybe.nothing();
      }
      return index < this.left.length ? this.left.index(index) : this.right.index(index - this.left.length);
    }

    @Nonnull
    @Override
    public Maybe<BinaryTree<T>> update(int index, @Nonnull T element) {
      if (index >= this.length) { return Maybe.nothing(); }
      BinaryTree<T> left = this.left;
      BinaryTree<T> right = this.right;

      if (index < this.left.length) {
        left = left.update(index, element).just();
      } else {
        right = right.update(index - this.left.length, element).just();
      }
      return Maybe.just(left.append(right));
    }
  }

  private static class BinaryTreeMonoid<T> implements Monoid<BinaryTree<T>> {
    @Nonnull
    @Override
    public BinaryTree<T> identity() {
      return new Empty<>();
    }

    @Nonnull
    @Override
    public BinaryTree<T> append(BinaryTree<T> a, BinaryTree<T> b) {
      return a.append(b);
    }
  }
}
