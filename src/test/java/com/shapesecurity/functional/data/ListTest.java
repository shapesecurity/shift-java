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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.shapesecurity.functional.Effect;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.TestBase;
import com.shapesecurity.functional.Thunk;

import org.junit.Test;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

public class ListTest extends TestBase {
  protected void testWithSpecialLists(@NotNull Effect<List<Integer>> f) {
    f.apply(Nil.nil());
    f.apply(List.<Integer>nil());
    f.apply(List.list(0));
    f.apply(List.list(0, 1, 2));
    f.apply(List.list(3, 2, 1));
    f.apply(LONG_LIST);
  }

  @Test
  public void testNil() {
    List<Integer> list = List.nil();
    assertTrue(list.maybeHead().isNothing());
    assertTrue(list.maybeTail().isNothing());
    assertEquals(list, list);
    assertEquals(list, List.<Integer>nil());
    assertNotEquals(list, List.cons(0, list));
  }

  @Test
  public void testList() {
    List<Integer> list = List.list();
    assertEquals(list, List.<Integer>nil());
  }

  @Test
  public void testCons() {
    testWithSpecialLists(this::testCons);
  }

  public void testCons(@NotNull List<Integer> list) {
    int a = rand();
    NonEmptyList<Integer> listP = List.cons(a, list);
    assertEquals(list.length() + 1, listP.length());
    assertEquals(a, listP.head.intValue());
    assertEquals(list, listP.tail());
    assertEquals(listP, list.cons(a));
  }

  @Test
  public void testLazyCons() {
    testWithSpecialLists(this::testLazyCons);
  }

  public void testLazyCons(List<Integer> list) {
    int a = rand();
    int b = rand();
    NonEmptyList<Integer> listP = List.cons(a, Thunk.constant((List<Integer>) List.cons(b, list)));
    assertEquals(list.length() + 2, listP.length());
    assertEquals(a, listP.head.intValue());
    assertEquals(b, listP.tail().maybeHead().just().intValue());
    assertEquals(list, listP.tail().maybeTail().just());
    assertEquals(List.cons(a, List.cons(b, list)), listP);
  }

  @Test
  public void testToArray() {
    testWithSpecialLists(this::testToArray);
  }

  private void testToArray(List<Integer> list) {
    final Integer[] a = new Integer[list.length()];
    list.mapWithIndex((i, x) -> a[i] = x);
    Integer[] a2 = new Integer[0];
    a2 = list.toArray(a2);
    assertEquals(a.length, a2.length);
    for (int i = 0; i < a.length; i++) {
      assertEquals(a[i], a2[i]);
    }
  }

  @Test
  public void testIndex() {
    List<Integer> l = List.list(0, 1, 2, 3, 4);
    assertTrue(l.index(0).just() == 0);
    assertFalse(l.index(2).just() == 0);
    assertEquals(Maybe.<Integer>nothing(), l.index(5)); //index out of range returns nothing
    assertEquals(Maybe.<Integer>nothing(), l.index(-1));
    assertEquals(Maybe.<Integer>nothing(), List.<Integer>nil().index(1));
  }

  @Test
  public void testFrom() {
    testWithSpecialLists(this::testFrom);
    testWithSpecialLists(this::testFromArray);
  }

  private void testFromArray(List<Integer> list) {
    final Integer[] array = new Integer[list.length()];
    list.mapWithIndex((i, x) -> array[i] = x);
    List<Integer> list1 = List.from(array);
    assertEquals(list, list1);
  }

  private void testFrom(List<Integer> list) {
    final ArrayList<Integer> arrList = new ArrayList<>();
    list.foreach(arrList::add);
    List<Integer> listP = List.from(arrList);
    assertEquals(list, listP);
  }

  @Test
  public void testFindMap() {
    List<Integer> list = List.list(0, 1, 2, 3, 4);
    assertTrue(list.findMap(x -> x == 2 ? Maybe.just(x - 1) : Maybe.<Integer>nothing()).just() == 1);
    assertEquals(Maybe.<Integer>nothing(), list.findMap(x -> x == 5 ? Maybe.just(x - 1) : Maybe.<Integer>nothing()));
    assertEquals(Maybe.<Integer>nothing(), List.<Integer>nil().findMap(x -> x == 5 ? Maybe.just(x - 1) :
                                                                            Maybe.<Integer>nothing()));
  }

  @Test
  public void testMaybeInit() {
    testWithSpecialLists(this::testInit);
  }

  private void testInit(List<Integer> list) {
    //if list is empty maybeInit returns nothing else returns just
    Maybe<List<Integer>> taken = list.isEmpty() ? Maybe.nothing() : Maybe.just(list.take(list.length() - 1));
    assertEquals(list.maybeInit(), taken);
  }

  @Test
  public void testMaybeLast() {
    testWithSpecialLists(this::testMaybeLast);
  }

  private void testMaybeLast(List<Integer> list) {
    Maybe<Integer> last = list.index(list.length() - 1);
    assertEquals(last, list.maybeLast());
  }

  @Test
  public void testMap() {
    testWithSpecialLists(this::testMap);
  }

  private void testMap(List<Integer> list) {
    Integer[] addArray = new Integer[list.length()];
    for (int i = 0; i < addArray.length; i++) {
      addArray[i] = list.index(i).just() + 1;
    }
    assertEquals(list.map(x -> x + 1), List.from(addArray));
  }

  @Test
  public void testFlatMap() {
    testWithSpecialLists(this::testFlatMap);
  }

  private void testFlatMap(List<Integer> list) {
    Integer[] dups = new Integer[list.length() * 2];
    for (int i = 0; i < dups.length / 2; i++) {
      dups[i * 2] = list.index(i).just();
      dups[i * 2 + 1] = list.index(i).just();
    }
    assertEquals(List.from(dups), list.flatMap(x -> List.list(x, x)));
  }

  // non-static

  @Test
  public void testLengthNonNegative() {
    testWithSpecialLists(this::testLengthNonNegative);
  }

  private void testLengthNonNegative(List<Integer> list) {
    assertTrue(list.length() >= 0);
  }

  @Test
  public void testEmptyNonEmptyMutualExclusivity() {
    testWithSpecialLists(ListTest.this::testEmptyNonEmptyMutualExclusivity);
  }

  private void testEmptyNonEmptyMutualExclusivity(List<Integer> list) {
    if (list.length() == 0) {
      assertTrue(list.isEmpty());
      assertFalse(list.isNotEmpty());
    } else {
      assertTrue(list.isNotEmpty());
      assertFalse(list.isEmpty());
    }
  }

  @Test
  public void testMaybeHeadMaybeTail() {
    testWithSpecialLists(ListTest.this::testMaybeHeadMaybeTail);
  }

  private void testMaybeHeadMaybeTail(List<Integer> list) {
    if (list.length() == 0) {
      assertTrue(list.maybeHead().isNothing());
      assertTrue(list.maybeTail().isNothing());
    } else {
      assertTrue(list.maybeHead().isJust());
      assertTrue(list.maybeTail().isJust());
      if (list.length() == 1) {
        assertEquals(List.<Integer>nil(), list.maybeTail().just());
      } else {
        assertNotEquals(List.<Integer>nil(), list.maybeTail().just());
      }
    }
  }

  @Test
  public void testEquals() {
    testWithSpecialLists(this::testEquals);
  }

  private void testEquals(List<Integer> list) {
    int a = rand();
    assertEquals(list, list);
    assertEquals(List.cons(a, list), List.cons(a, Thunk.constant(list)));
  }

  @Test
  public void testSpan() {
    testSpan(List.<Integer>nil(), 0, 0);
    testSpan(List.list(1, 2, 3), 3, 0);
    testSpan(List.list(10, 20, 30), 0, 3);
    testSpan(List.list(5, 10, 15), 1, 2);
  }

  private void testSpan(List<Integer> list, int lengthA, int lengthB) {
    Pair<List<Integer>, List<Integer>> s = list.span(i -> i < 10);
    assertEquals(s.a.length(), lengthA);
    assertEquals(s.b.length(), lengthB);
  }

  @Test
  public void testZipWith() {
    testWithSpecialLists(this::testZipWith);
  }

  private void testZipWith(List<Integer> list) {
    List<Integer> integers = list.zipWith((a, b) -> 0, List.<Integer>nil());
    assertEquals(0, integers.length());
    integers = list.zipWith((a, b) -> a + b, list);
    list.foldLeft((l, integer) -> {
      assertEquals(integer * 2, (int) l.maybeHead().just());
      return l.maybeTail().just();
    }, integers);
    if (list instanceof NonEmptyList) {
      NonEmptyList<Integer> nel = (NonEmptyList<Integer>) list;
      List<Integer> a = nel.zipWith((x, y) -> x + y, nel.tail());
      assertEquals(a.length(), list.length() - 1);
    }
  }

  @Test
  public void testFilter() {
    assertEquals(84, range(100).filter(i -> i > 15).length());
  }

  @Test
  public void testRemoveAll() {
    assertEquals(84, range(100).removeAll(i -> i <= 15).length());
  }

  @Test
  public void testTakeDrop() {
    assertEquals(85, range(100).drop(15).length());
    assertEquals(15, range(100).take(15).length());
  }
}
