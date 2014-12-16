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

package com.shapesecurity.shift.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class PairTest {
  @Test
  public void testPair() {
    Pair p = new Pair<>(3, 5);
    assertEquals(p, p);
    assertEquals(new Pair<>(3, 5), new Pair<>(3, 5));
    assertNotEquals(new Pair<>(3, 5), new Pair<>(3, 6));
    assertNotEquals(new Pair<>(3, 5), new Pair<>(4, 5));
    F<Integer, Integer> doubleMe = x -> x * 2;
    assertEquals(new Pair<>(3, 6), new Pair<>(3, 3).mapB(doubleMe));
    assertEquals(new Pair<>(5, 3), new Pair<>(3, 5).swap());
    assertEquals(new Pair<>(3, 3), new Pair<>(3, 3).swap());
    assertEquals(new Pair<>(5, 3).hashCode(), new Pair<>(5, 3).hashCode());
    assertNotEquals(new Pair<>(5, 3).hashCode(), new Pair<>(5, 4).hashCode());
    assertNotEquals(new Pair<>(5, 3).hashCode(), new Pair<>(6, 3).hashCode());
  }
}
