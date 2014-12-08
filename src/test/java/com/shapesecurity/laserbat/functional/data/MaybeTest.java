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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.shapesecurity.laserbat.functional.Thunk;
import com.shapesecurity.laserbat.functional.Unit;

import org.junit.Test;

import javax.annotation.Nullable;

public class MaybeTest extends TestBase {
  final Integer notNull = 3;
  @Nullable
  final Integer nulled = null;

  @Test
  public void testFromNullable() {
    assertEquals(Maybe.just(3), Maybe.fromNullable(notNull));
    assertEquals(Maybe.<Integer>nothing(), Maybe.fromNullable(nulled));
  }

  @Test
  public void testToNullable() {
    assertEquals((Integer) 3, Maybe.toNullable(Maybe.just(notNull)));
    assertEquals(null, Maybe.toNullable(Maybe.<Integer>nothing()));
  }

  @Test
  public void testIff() {
    assertEquals(Maybe.just(3), Maybe.iff(true, notNull));
    assertEquals(Maybe.<Integer>nothing(), Maybe.iff(false, notNull));
  }

  @Test
  public void testCatMaybes() {
    new ListTest().testWithSpecialLists(this::testCatMaybes);
  }

  private void testCatMaybes(List<Integer> list) {
    assertEquals(list, Maybe.catMaybes(list.map(Maybe::just)));
  }

  @Test
  public void testMapMaybe() {
    new ListTest().testWithSpecialLists(this::testMapMaybe);
  }

  private void testMapMaybe(List<Integer> list) {
    assertEquals(list.map(x -> x + 1), Maybe.mapMaybe(x -> x + 1, list.<Maybe<Integer>>map(Maybe::just)));
  }

  @Test
  public void testBind() {
    assertEquals(Maybe.fromNullable(notNull + 1), Maybe.just(notNull).bind(x -> Maybe.just(
        x + 1))); //not fully sure why I am responsible for wrapping A into a Maybe<A>
    assertEquals(Maybe.<Integer>nothing(), Maybe.fromNullable(nulled));
  }

  @Test
  public void testForEach() {
    Maybe.fromNullable(nulled).foreach(x -> {
      fail("Maybe.forEach should not execute f on a nothing"); //should never call f
      return Unit.unit;
    });
    final int[] effect = {0};
    Maybe.fromNullable(notNull).foreach(x -> {
      effect[0] += 1;
      return Unit.unit;
    });
    assertEquals(1, effect[0]);//just should be side effected into incrementing once and only once
  }

  @Test
  public void testToList() {
    assertEquals(List.<Integer>nil(), Maybe.<Integer>nothing().toList());
    assertEquals(List.list(notNull), Maybe.just(notNull).toList());
  }

  @Test
  public void testEq() {
    assertTrue(Maybe.fromNullable(notNull).eq(Maybe.just(notNull)));
    assertFalse(Maybe.fromNullable(notNull).eq(Maybe.just(notNull + 1)));
    assertFalse(Maybe.fromNullable(notNull).eq(Maybe.<Integer>nothing()));
    assertTrue(Maybe.fromNullable(nulled).eq(Maybe.<Integer>nothing()));
    assertFalse(Maybe.fromNullable(nulled).eq(Maybe.fromNullable(notNull)));
  }

  @Test
  public void testOrJusts() {
    assertEquals(notNull, Maybe.fromNullable(notNull).orJust(notNull));
    assertEquals(notNull, Maybe.fromNullable(nulled).orJust(notNull));
    assertEquals(notNull, Maybe.fromNullable(notNull).orJustLazy(Thunk.from(() -> notNull)));
    assertEquals(notNull, Maybe.fromNullable(nulled).orJustLazy(Thunk.from(() -> notNull)));
  }

  @Test
  public void testHashCode() {
    assertEquals(Maybe.fromNullable("hash").hashCode(), Maybe.fromNullable("hash").hashCode());
    assertEquals(Maybe.fromNullable(null).hashCode(), Maybe.fromNullable(null).hashCode());
    assertNotEquals(Maybe.fromNullable("hash").hashCode(), Maybe.fromNullable(null).hashCode());
    assertNotEquals(Maybe.fromNullable(null).hashCode(), Maybe.fromNullable("hash").hashCode());
    assertNotEquals(Maybe.fromNullable("hash").hashCode(), Maybe.fromNullable("not hash").hashCode());
  }

  @Test
  public void testFlatMap() {
    assertEquals(Maybe.just(notNull + 1), Maybe.fromNullable(notNull).flatMap(x -> Maybe.fromNullable(x + 1)));
    assertEquals(Maybe.<Integer>nothing(), Maybe.fromNullable(nulled).flatMap(x -> Maybe.fromNullable(x + 1)));
  }

  @Test
  public void testIs() {
    assertTrue(Maybe.fromNullable(nulled).isNothing() && !Maybe.fromNullable(nulled).isJust());
    assertTrue(!Maybe.fromNullable(notNull).isNothing() && Maybe.fromNullable(notNull).isJust());
  }

  @Test
  public void testMaybe() {
    assertEquals(notNull, Maybe.fromNullable(notNull).maybe(1, x -> x));
    assertEquals((Integer) 1, Maybe.fromNullable(nulled).maybe(1, x -> x));
  }

  @Test
  public void testJust() {
    assertEquals(notNull, Maybe.fromNullable(notNull).just());
    try {
      Integer i = Maybe.fromNullable(nulled).just();
      fail("Did not throw NullPointerException");
    } catch (NullPointerException e) {
      //do nothing we pass
    }
  }
}
