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

package com.shapesecurity.laserbat.functional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FunctionTest {
  @Test
  public void testEffect() {
    final StringBuilder result = new StringBuilder();
    Effect<Integer> effect = integer -> result.append(integer).append(";");

    assertEquals(Unit.unit, effect.apply(1));
    assertEquals(Unit.unit, effect.apply(2));
    assertEquals(Unit.unit, effect.apply(3));

    assertEquals("1;2;3;", result.toString());
  }

  @Test
  public void testF() {
    F<Integer, Integer> doubleMe = integer -> integer * 2;
    assertEquals(2, (int) doubleMe.apply(1));
    assertEquals("abc", F.id().apply("abc"));

    F<Integer, Integer> doubleDoubleMe = doubleMe.compose(doubleMe);
    assertEquals(4, (int) doubleDoubleMe.apply(1));

    F<String, Integer> c = F.constant(15);
    assertEquals(15, (int) c.apply("whatever"));

    F<String, F<String, String>> concatC = a -> b -> a + "," + b;
    assertEquals("a,b", concatC.apply("a").apply("b"));

    F2<String, String, String> concat = F.uncurry(concatC);
    assertEquals("a,b", concat.apply("a", "b"));

    F<String, F<String, String>> concatFlipped = F.flip(concatC);
    assertEquals("a,b", concatFlipped.apply("b").apply("a"));
  }

  @Test
  public void testThunk() {
    final int[] counter = new int[1];
    Thunk<Integer> t0 = Thunk.from(() -> {
      counter[0]++;
      try {
        Thread.sleep(10);
      } catch (InterruptedException ignored) {

      }
      return 1234;
    });
    for (int i = 0; i < 300; i++) {
      new Thread(() -> assertEquals(1234, (int) t0.get())).start();
    }
    assertEquals(1, counter[0]);
    assertEquals(1, (int) Thunk.from(() -> counter[0]).get());
  }

  @Test
  public void testF2() {
    F2<Integer, Integer, Integer> minus = (a, b) -> a - b;
    assertEquals(1, (int) minus.apply(3, 2));
    assertEquals(-1, (int) minus.flip().apply(3, 2));
    F<Integer, Integer> threeMinus = minus.curry(3);
    F<Integer, Integer> minusThree = minus.flip().curry(3);

    assertEquals(-3, (int) threeMinus.apply(6));
    assertEquals(-9, (int) threeMinus.apply(12));
    assertEquals(9, (int) minusThree.apply(12));
    assertEquals(-3, (int) minusThree.apply(0));
  }
}