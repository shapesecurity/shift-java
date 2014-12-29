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
import com.shapesecurity.functional.TestBase;

import org.junit.Test;

import org.jetbrains.annotations.NotNull;

public class NonEmptyListTest extends TestBase {
  // Helpers

  private void testWithSpecialLists(@NotNull Effect<NonEmptyList<Integer>> f) {
    f.apply(List.list(0));
    f.apply(List.list(0, 1, 2));
    f.apply(List.list(3, 2, 1));
    f.apply(LONG_LIST);
  }

  private void testLengthOneGreaterThanTail(NonEmptyList<Integer> list) {
    assertEquals(list.length, 1 + list.tail().length);
  }

  // Tests

  // static

  @Test
  public void testList() {
    int a = rand();
    NonEmptyList<Integer> list = List.list(a);
    assertEquals(list.length, 1);
    assertEquals(list.head.intValue(), a);
    assertEquals(list.tail(), List.<Integer>nil());
  }

  @Test
  public void testExists() {
    NonEmptyList<Integer> list = List.list(1, 2, 3);
    assertTrue(list.exists((x) -> x == 2));
    assertFalse(list.exists((x) -> x == 4));
  }

  // non-static

  @Test
  public void testHeadTail() {
    testWithSpecialLists(this::testHeadTail);
  }

  private void testHeadTail(NonEmptyList<Integer> list) {
    int a = rand();
    assertEquals(list.maybeHead().just(), list.head);
    assertEquals(list.maybeTail().just(), list.tail());
    assertEquals(List.cons(a, list).tail(), list);
    assertEquals(Integer.valueOf(a), List.cons(a, list).head);
  }

  @Test
  public void testLengthOneGreaterThanTail() {
    testWithSpecialLists(this::testLengthOneGreaterThanTail);
  }

  @Test
  public void testEquals() {
    testWithSpecialLists(this::testEquals);
  }

  public void testEquals(NonEmptyList<Integer> list) {
    assertEquals(list, list);
    assertNotEquals(list, List.<Integer>nil());
  }

  @Test
  public void testReverse() {
    assertEquals(List.nil().reverse(), List.nil());
    assertEquals(List.list(1).reverse(), List.list(1));
    assertEquals(List.list(1, 2, 3).reverse(), List.list(3, 2, 1));
  }

  @Test
  public void testLast() {
    testWithSpecialLists(this::testLast);
  }

  private void testLast(NonEmptyList<Integer> list) {
    assertEquals(list.last(), list.index(list.length - 1).just());
  }

  @Test
  public void testInit() {
    testWithSpecialLists(this::testInit);
  }

  private void testInit(NonEmptyList<Integer> list) {
    assertEquals(list.init(), list.take(list.length - 1));
  }
}
