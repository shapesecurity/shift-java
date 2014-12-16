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

package com.shapesecurity.shift.js.validator;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Monoid;

import javax.annotation.Nonnull;

public abstract class ConcatList<T> {
  // class local
  private ConcatList() {
  }

  public static <T> Monoid<ConcatList<T>> monoid() {
    return new ConcatListMonoid<>();
  }

  @Nonnull
  public static <T> ConcatList<T> nil() {
    return new Nil<>();
  }

  @Nonnull
  public static <T> ConcatList<T> single(@Nonnull T item) {
    return new Leaf<>(item);
  }

  @Nonnull
  public abstract ConcatList<T> append(@Nonnull ConcatList<T> b);

  public abstract int length();

  @Nonnull
  public final List<T> toList() {
    return this.toList(List.<T>nil());
  }

  @Nonnull
  protected abstract List<T> toList(@Nonnull List<T> tail);

  private static final class Nil<T> extends ConcatList<T> {
    @Nonnull
    @Override
    public ConcatList<T> append(@Nonnull ConcatList<T> b) {
      return b;
    }

    @Override
    public int length() {
      return 0;
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> tail) {
      return tail;
    }
  }

  private static final class Leaf<T> extends ConcatList<T> {
    @Nonnull
    private final T item;

    private Leaf(@Nonnull T item) {
      super();
      this.item = item;
    }

    @Nonnull
    @Override
    public ConcatList<T> append(@Nonnull ConcatList<T> b) {
      if (b instanceof Nil) {
        return this;
      }
      return new Concat<>(this, b);
    }

    @Override
    public int length() {
      return 1;
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> tail) {
      return List.cons(this.item, tail);
    }
  }

  private static final class Concat<T> extends ConcatList<T> {
    @Nonnull
    private final ConcatList<T> l;
    @Nonnull
    private final ConcatList<T> r;

    public Concat(@Nonnull ConcatList<T> l, @Nonnull ConcatList<T> r) {
      super();
      this.l = l;
      this.r = r;
    }

    @Nonnull
    @Override
    public ConcatList<T> append(@Nonnull ConcatList<T> b) {
      if (b instanceof Nil) {
        return this;
      }
      return new Concat<>(this, b);
    }

    @Override
    public int length() {
      return this.l.length() + this.r.length();
    }

    @Nonnull
    @Override
    protected List<T> toList(@Nonnull List<T> tail) {
      return this.l.toList(this.r.toList(tail));
    }
  }

  private static class ConcatListMonoid<T> implements Monoid<ConcatList<T>> {
    @Nonnull
    @Override
    public ConcatList<T> identity() {
      return ConcatList.nil();
    }

    @Nonnull
    @Override
    public ConcatList<T> append(ConcatList<T> a, ConcatList<T> b) {
      return a.append(b);
    }
  }
}
