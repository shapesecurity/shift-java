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

import com.shapesecurity.laserbat.functional.Effect;
import com.shapesecurity.laserbat.functional.F;
import com.shapesecurity.laserbat.functional.F2;
import com.shapesecurity.laserbat.functional.Pair;
import com.shapesecurity.laserbat.functional.Thunk;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

/**
 * An immutable singly linked list implementation. None of the operations in {@link List} changes the list itself. Therefore you can freely share the list in
 * your system.
 * <p>
 * This is a "classical" list implementation that the list is only allowed to prepend and to remove the first element efficiently.
 * Therefore it is essentially equivalent to a stack.
 * <p>
 * It is either an empty list, or a record that contains the first element (called "head") and a list that follows(called "tail").
 * With the assumption that all the elements in the list are also immutable, the sharing of the tails is possible.
 * <p>
 * For a data structure that allows O(1) concatenation, try {@link BinaryTree}. A BinaryTree can be converted into a List in O(n) time.
 *
 * @param <A> The super type of all the elements.
 */
public abstract class List<A> implements Iterable<A> {
  private static final List<Object> NIL = new Nil<>();
  @Nonnull
  private final Thunk<Integer> hashCodeThunk = Thunk.from(this::calcHashCode);
  @Nonnull
  private final Thunk<Integer> lengthThunk = Thunk.from(this::calcLength);

  // package local
  List() { super(); }

  /**
   * Creating List from.
   *
   * @param arrayList The {@link java.util.ArrayList} to construct the {@link List} from.
   * @param <A>       The type of the elements of the list.
   * @return a new {@link List} that is comprised of all the elements in the {@link java.util.ArrayList}.
   */
  @Nonnull
  public static <A> List<A> from(@Nonnull ArrayList<A> arrayList) {
    // Manual expansion of tail recursion.
    List<A> l = nil();
    int size = arrayList.size();
    for (int i = size - 1; i >= 0; i--) {
      l = cons(arrayList.get(i), l);
    }
    return l;
  }

  /**
   * Prepends "cons" an head element to a {@link List}.
   *
   * @param head The head element to be prepended to the {@link List}.
   * @param tail The {@link List} to be prepended to.
   * @param <T>  The super type of both the element and the {@link List}
   * @return A {@link List} that is comprised of the head then the tail.
   */
  public static <T> NonEmptyList<T> cons(@Nonnull T head, @Nonnull List<T> tail) {
    return new NonEmptyList.Eager<>(head, tail);
  }

  /**
   * Prepends an head element to a {@link List} that is calculated only when needed.
   *
   * @param head The head element to be prepended to the {@link List}.
   * @param tail The thunk where the tail List can be calculated from.
   * @param <T>  The super type of both the element and the list
   * @return An List that comprises the head then the tail.
   */
  public static <T> NonEmptyList<T> cons(@Nonnull T head, @Nonnull Thunk<List<T>> tail) {
    return new NonEmptyList.Lazy<>(head, tail);
  }

  // Construction

  @SuppressWarnings("unchecked")
  public static <T> List<T> nil() {
    return (List<T>) NIL;
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> list() {
    return (List<T>) NIL;
  }

  /**
   * A helper constructor to create a {@link NonEmptyList}.
   */
  @Nonnull
  @SafeVarargs
  public static <T> NonEmptyList<T> list(@Nonnull T head, @Nonnull T... el) {
    if (el.length == 0) {
      return cons(head, List.<T>nil());
    }
    NonEmptyList<T> l = cons(el[el.length - 1], List.<T>nil());
    for (int i = el.length - 2; i >= 0; i--) {
      l = cons(el[i], l);
    }
    return cons(head, l);
  }

  /**
   * A helper constructor to create a {@link List}.
   */
  @Nonnull
  @SafeVarargs
  public static <A> List<A> from(@Nonnull A... el) {
    if (el.length == 0) {
      return nil();
    }
    NonEmptyList<A> l = cons(el[el.length - 1], List.<A>nil());
    for (int i = el.length - 2; i >= 0; i--) {
      l = cons(el[i], l);
    }
    return l;
  }

  protected abstract int calcLength();

  protected abstract int calcHashCode();

  @Override
  public final int hashCode() {
    return this.hashCodeThunk.get();
  }

  @Override
  public Iterator<A> iterator() {

    return new Iterator<A>() {
      private List<A> curr = List.this;

      @Override
      public boolean hasNext() {
        return !this.curr.isEmpty();
      }

      @Override
      public A next() {
        if (this.curr.isEmpty()) {
          throw new NoSuchElementException();
        }
        A head = this.curr.maybeHead().just();
        this.curr = this.curr.maybeTail().just();
        return head;
      }

      @Override
      public void remove() {

      }
    };
  }

  // Methods

  /**
   * Prepend an element to the list.
   *
   * @param left The element to prepend.
   * @return A list with <code>left</code> as the first element followed by <code>this</code>.
   */
  @Nonnull
  public final NonEmptyList<A> cons(@Nonnull A left) {
    return cons(left, this);
  }

  /**
   * Classic "foldl" operation on the {@link List}.
   *
   * @param f    The function.
   * @param init The initial value.
   * @param <B>  The type of the result of the folding.
   * @return The result of the folder.
   * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)#Folds_as_structural_transformations">http://en.wikipedia.org/wiki/Fold_
   * (higher-order_function)</a>
   */
  @Nonnull
  public abstract <B> B foldLeft(@Nonnull F2<B, ? super A, B> f, @Nonnull B init);

  /**
   * Classic "foldr" operation on the {@link List}.
   *
   * @param f    The function.
   * @param init The initial value.
   * @param <B>  The type of the result of the folding.
   * @return The result of the folder.
   * @see <a href="http://en.wikipedia.org/wiki/Fold_(higher-order_function)#Folds_as_structural_transformations">http://en.wikipedia.org/wiki/Fold_
   * (higher-order_function)</a>
   */
  @Nonnull
  public abstract <B> B foldRight(@Nonnull F2<? super A, B, B> f, @Nonnull B init);

  /**
   * Returns the head of the {@link List}.
   *
   * @return Maybe.just(head of the list), or Maybe.nothing() if the list is empty.
   */
  @Nonnull
  public abstract Maybe<A> maybeHead();

  /**
   * Returns the last of the {@link List}.
   *
   * @return Maybe.just(last of the list), or Maybe.nothing() if the list is empty.
   */
  @Nonnull
  public abstract Maybe<A> maybeLast();

  /**
   * Returns the tail of the {@link List}.
   *
   * @return Maybe.just(tail of the list), or Maybe.nothing() if the list is empty.
   */
  @Nonnull
  public abstract Maybe<List<A>> maybeTail();

  /**
   * Returns the init of the {@link List}. The init of a List is defined as the rest of List removing the last element.
   *
   * @return Maybe.just(init of the list), or Maybe.nothing() if the list is empty.
   */
  @Nonnull
  public abstract Maybe<List<A>> maybeInit();

  /**
   * Returns a new list of elements when applying <code>f</code> to an element returns true.
   *
   * @param f The "predicate" function.
   * @return A new list of elements that satisfies the predicate.
   */
  @Nonnull
  public abstract List<A> filter(@Nonnull F<A, Boolean> f);

  /**
   * Applies the <code>f</code> function to each of the elements of the list and collect the result.
   * It will be a new list with the same length of the original one.
   *
   * @param f   The function to apply.
   * @param <B> The type of the new {@link List}.
   * @return The new {@link List} containing the result.
   */
  @Nonnull
  public abstract <B> List<B> map(@Nonnull F<A, B> f);

  /**
   * Applies the <code>f</code> function to each of the elements of the list and collect the result.
   * This method also provides an extra index parameter to <code>f</code> function as the first parameter.
   *
   * @param f   The function to apply.
   * @param <B> The type of the new {@link List}.
   * @return The new {@link List} containing the result.
   */
  @Nonnull
  public <B> List<B> mapWithIndex(@Nonnull F2<Integer, A, B> f) {
    return this.mapWithIndex(f, 0);
  }

  @Nonnull
  protected abstract <B> List<B> mapWithIndex(@Nonnull F2<Integer, A, B> f, int index);

  /**
   * The the first <code>n</code> elements of the list and create a new List of them. If the original
   * list contains less than <code>n</code> elements, returns a copy of the original List.
   *
   * @param n The number of elements to take.
   * @return A new list containing at most <code>n</code> elements that are the first elements of the original list.
   */
  @Nonnull
  public abstract List<A> take(int n);

  /**
   * Removes the first <code>n</code> elements of the list and return the rest of the List by reference. If the original list
   * contains less than <code>n</code> elements, returns an empty list as if it is returned by {@link #nil}.
   *
   * @param n The number of elements to skip.
   * @return A shared list containing at most <code>n</code> elements removed from the original list.
   */
  @Nonnull
  public abstract List<A> drop(int n);

  /**
   * Specialize this type to be a {@link NonEmptyList} if possible.
   *
   * @return Returns is <code>Maybe.just(this)</code> if this is indeed non-empty. Otherwise returns <code>Maybe.nothing()</code>.
   */
  @Nonnull
  public abstract Maybe<NonEmptyList<A>> toNonEmptyList();

  /**
   * Deconstruct the list in to its head and tail, and feed them into another function <code>f</code>.
   *
   * @param f   The function to receive the head and tail if they exist.
   * @param <B> The return type of <code>f</code>
   * @return If the list is an non-empty list, returns <code>Maybe.just(f(head, tail))</code>; otherwise returns <code>Maybe.nothing()</code>.
   */
  @Nonnull
  public abstract <B> Maybe<B> decons(@Nonnull F2<A, List<A>, B> f);

  /**
   * Length of the list.
   *
   * @return The length of the list.
   */
  public int length() {
    return lengthThunk.get();
  }

  /**
   * Takes another list and feeds the elements of both lists to a function at the same pace, then collects the result and forms another list.
   * Stops once either of the two lists came to an end.
   * <p>
   * Another way to visualize this operation is to imagine this operation as if it's zipping a zipper. taking two lists of things, merge them one by one,
   * and collect the results.
   *
   * @param f    The function to apply
   * @param list the other list to zip with <code>this</code>.
   * @param <B>  The type of the element of the other list.
   * @param <C>  The type of the result of the merging function.
   * @return The return type of the merging function.
   */
  @Nonnull
  public abstract <B, C> List<C> zipWith(@Nonnull F2<A, B, C> f, @Nonnull List<B> list);

  /**
   * Converts this list into an array.
   * <p>
   * Due to type erasure, the type of the resulting array has to be determined at runtime. Fortunately, you can create a zero length array and this method
   * can create an large enough array to contain all the elements. If the given array is large enough, this method will put elements in it.
   *
   * @param target The target array.
   * @return The array that contains the elements. It may or may not be the same reference of <code>target</code>.
   */
  @SuppressWarnings("unchecked")
  @Nonnull
  public final A[] toArray(@Nonnull A[] target) {
    int length = this.length();
    if (target.length < length) {
      // noinspection unchecked
      target = (A[]) Array.newInstance(target.getClass().getComponentType(), length);
    }
    List<A> l = this;
    for (int i = 0; i < length; i++) {
      target[i] = l.maybeHead().just();
      l = l.maybeTail().just();
    }
    return target;
  }

  /**
   * Runs an effect function across all the elements.
   *
   * @param f The Effect function.
   */
  public final void foreach(@Nonnull Effect<A> f) {
    // Hand expanded recursion.
    List<A> list = this;
    Maybe<A> head;
    while ((head = list.maybeHead()).isJust()) {
      f.e(head.just());
      list = list.maybeTail().just();
    }
  }

  public abstract boolean isEmpty();

  /**
   * Creates a list with the content of the current list followed by another list. If the current list is empty, simply return the second one.
   *
   * @param defaultClause The list to concatenate with. It will be reused as part of the returned list.
   * @param <B>           The type of the resulting list.
   * @return The concatenation of the two lists.
   */
  @Nonnull
  public abstract <B extends A> List<A> append(@Nonnull List<B> defaultClause);

  /**
   * Tests all the elements in the {@link List} with predicate <code>f</code> until it finds the element of reaches the end, then returns whether an element
   * has been found or not.
   *
   * @param f The predicate.
   * @return Whether an elements satisfies the predicate <code>f</code>.
   */
  public abstract boolean exists(@Nonnull F<A, Boolean> f);

  /**
   * Separates the list into a pair of lists such that 1. the concatenation of the lists is equal to <code>this</code>; 2. The first list is the longest list
   * that every element of the list fails the predicate <code>f</code>.
   *
   * @param f The predicate.
   * @return The pair.
   */
  @Nonnull
  public abstract Pair<List<A>, List<A>> span(@Nonnull F<A, Boolean> f);

  /**
   * A synonym of {@link #flatMap}.
   *
   * @param f   The function.
   * @param <B> The type of result function.
   * @return The result of bind.
   */
  @Nonnull
  public final <B> List<B> bind(@Nonnull F<A, List<B>> f) {
    return this.flatMap(f);
  }

  /**
   * Apply <code>f</code> to each element of this list to get a list of lists (of not necessarily the same type), then concatenate all these lists to get a
   * single list.
   * <p>
   * This operation can be thought of as a generalization of {@link #map} and {@link #filter}, which, instead of keeping the number of elements in the list,
   * changes the number and type of the elements in an customizable way but keeps the original order.
   * <p>
   * This operation can also be thought of as an assembly line that takes one stream of input and returns another stream of output, but not necessarily of
   * the
   * same size, type or number.
   * <p>
   * This operation is often called "bind" or ">>=" of a monad in pure functional programming context.
   *
   * @param f   The function to expand the list element.
   * @param <B> The type of the result list.
   * @return The result list.
   */
  @Nonnull
  public abstract <B> List<B> flatMap(@Nonnull F<A, List<B>> f);

  public final boolean isNotEmpty() {
    return !isEmpty();
  }

  /**
   * Tests the elements of the list with a predicate <code>f</code> and returns the first one that satisfies the predicate without testing the rest of the
   * list.
   *
   * @param f The predicate.
   * @return <code>Maybe.just(the found element)</code> if an element is found or <code>Maybe.nothing()</code> if none is found.
   */
  @Nonnull
  public final Maybe<A> find(@Nonnull F<A, Boolean> f) {
    List<A> self = this;
    while (self instanceof NonEmptyList) {
      NonEmptyList<A> selfNel = (NonEmptyList<A>) self;
      boolean result = f.apply(selfNel.head);
      if (result) {
        return Maybe.just(selfNel.head);
      }
      self = selfNel.tail();
    }
    return Maybe.nothing();
  }

  /**
   * Run <code>f</code> on each element of the list and return the result immediately if it is a
   * <code>Maybe.just</code>. Other wise return <code>Maybe.nothing()</code>
   *
   * @param f The predicate.
   * @return <code>Maybe.just(the found element)</code> if an element is found or <code>Maybe.nothing()</code> if none is found.
   */
  @Nonnull
  public final <B> Maybe<B> findMap(@Nonnull F<A, Maybe<B>> f) {
    List<A> self = this;
    while (self instanceof NonEmptyList) {
      NonEmptyList<A> selfNel = (NonEmptyList<A>) self;
      Maybe<B> result = f.apply(selfNel.head);
      if (result.isJust()) {
        return result;
      }
      self = selfNel.tail();
    }
    return Maybe.nothing();
  }

  /**
   * Creats a new list with all the elements but those satisfying the predicate.
   *
   * @param f The predicate.
   * @return A new list of filtered elements.
   */
  @Nonnull
  public abstract List<A> removeAll(@Nonnull F<A, Boolean> f);

  /**
   * Reverses the list in linear time.
   *
   * @return Reversed list.
   */
  @Nonnull
  public abstract List<A> reverse();

  /**
   * Patches the current list.
   * Patching a list first takes the first <code>index</code> elements then concatenates it with <code>replacements</code> and then concatenates it with
   * the original list dropping <code>index + patchLength</code> elements.
   * <p>
   * A visualization of this operation is to replace the <code>patchLength</code> elements in the list starting from <code>index</code> with a list of new
   * elements given by <code>replacements</code>.
   *
   * @param index        The index to start patching.
   * @param patchLength  The length to patch.
   * @param replacements The replacements of the patch.
   * @param <B>          The type of the replacements. It must be A or a subtype of A.
   * @return The patched list.
   */
  @Nonnull
  public <B extends A> List<A> patch(int index, int patchLength, @Nonnull List<B> replacements) {
    return this.take(index).append(replacements).append(this.drop(index + patchLength));
  }

  /**
   * <code>mapAccumL</code> performs {@link #map} and {@link #foldLeft} method at the same time. It is similar to {@link #foldLeft}, but instead of returning
   * the
   * new accumulation value, it also allows the user to return an extra value which will be collected and returned.
   *
   * @param f   The accumulation function.
   * @param acc The initial value of the fold part.
   * @param <B> The type of the initial value.
   * @param <C> The type of the result of map.
   * @return A pair of the accumulation value and a mapped list.
   */
  @Nonnull
  public abstract <B, C> Pair<B, List<C>> mapAccumL(@Nonnull F2<B, A, Pair<B, C>> f, @Nonnull B acc);

  /**
   * Get the <code>index</code>th element of the list. It is comparable to the <code>[]</code> operator for array but instead of returning the element, it
   * returns an <code>Maybe</code> to indicate whether the element can be found or not.
   *
   * @param index The index.
   * @return <code>Maybe.just(found element)</code>if the element can be retrieved; or <code>Maybe.nothing()</code> if index out of range().
   */
  @Nonnull
  public final Maybe<A> index(int index) {
    List<A> l = this;
    if (index < 0) {
      return Maybe.nothing();
    }
    while (index > 0) {
      if (l.isEmpty()) {
        return Maybe.nothing();
      }
      index--;
      l = l.maybeTail().just();
    }
    return l.maybeHead();
  }

  @Override
  public abstract boolean equals(Object o);
}

