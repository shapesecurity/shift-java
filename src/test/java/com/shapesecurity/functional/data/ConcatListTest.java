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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.shapesecurity.functional.F;

import org.junit.Test;

public class ConcatListTest {
  private static ConcatList<Integer> gen(int size) {
    return gen(0, size);
  }

  private static ConcatList<Integer> gen(int start, int size) {
    if (size == 0) {
      return ConcatList.empty();
    } else if (size == 1) {
      return ConcatList.single(start);
    } else {
      int half = size >> 1;
      return gen(start, half).append(gen(start + half, size - half));
    }
  }

  @Test
  public void simpleTest() {
    assertTrue(ConcatList.empty().isEmpty());
    assertFalse(ConcatList.single(0).isEmpty());
    ConcatList<Integer> bt = ConcatList.single(0);
    for (int i = 0; i < 10; i++) {
      bt = bt.append(bt);
    }
    assertEquals(1024, bt.length);
    assertFalse(bt.isEmpty());
    assertFalse(ConcatList.empty().append(ConcatList.single(0)).isEmpty());
    assertFalse(ConcatList.single(0).append(ConcatList.single(0)).isEmpty());
  }

  @Test
  public void findTest() {
    assertEquals(Maybe.<Integer>nothing(), ConcatList.<Integer>empty().find(F.constant(true)));
    int N = (1 << 15) - 1;
    ConcatList<Integer> list = gen(N);
    assertEquals(0, (int) list.find(F.constant(true)).just());
    assertEquals(Maybe.<Integer>nothing(), list.find(F.constant(false)));
    assertEquals(N - 1, (int) list.find(x -> x >= N - 1).just());
  }

  @Test
  public void existsTest() {
    assertFalse(ConcatList.<Integer>empty().exists(F.constant(false)));
    assertFalse(ConcatList.<Integer>empty().exists(F.constant(true)));
    int N = (1 << 15) - 1;
    ConcatList<Integer> list = gen(N);
    assertTrue(list.exists(F.constant(true)));
    assertFalse(list.exists(F.constant(false)));
    assertTrue(list.exists(x -> x >= N - 1));
    assertFalse(list.exists(x -> x >= N));
  }

  @Test
  public void monoidTest() {
    Monoid<ConcatList<Integer>> m = ConcatList.<Integer>monoid();
    assertEquals(0, m.identity().length);
    assertEquals(0, m.append(m.identity(), m.identity()).length);
    assertEquals(1, m.append(m.identity(), ConcatList.single(3)).length);
  }

  @Test
  public void foldTest() {
    ConcatList.<Integer>empty().foldLeft((x, y) -> {
      fail("not reached");
      return 0;
    }, 0);
    ConcatList.<Integer>empty().foldRight((x, y) -> {
      fail("not reached");
      return 0;
    }, 0);
    int N = (1 << 15) - 1;
    ConcatList<Integer> list = gen(N);
    list.foldLeft((result, el) -> {
      assertEquals(result, el);
      return result + 1;
    }, 0);
    list.foldRight((result, el) -> {
      assertEquals(result, el);
      return result - 1;
    }, N - 1);
  }

  @Test
  public void reverseTest() {
    assertEquals(0, ConcatList.<Integer>empty().reverse().length);
    int N = (1 << 15) - 1;
    ConcatList<Integer> list = gen(N).reverse();
    list.foldRight((result, el) -> {
      assertEquals(result, el);
      return result + 1;
    }, 0);
  }

  @Test
  public void toListTest() {
    assertEquals(0, ConcatList.<Integer>empty().toList().length);
    assertEquals(1, ConcatList.single(1).toList().length);
    assertEquals(15, gen(15).toList().length);
  }

  @Test
  public void indexUpdateTest() {
    int N = (1 << 15) - 1;
    ConcatList<Integer> list = gen(N);
    for (int i = 0; i < N; i++) {
      assertEquals(i, (int) list.index(i).just());
    }
    for (int i = 0; i < N; i++) {
      list = list.update(i, i + 1).just();
    }
    for (int i = 0; i < N; i++) {
      assertEquals(i + 1, (int) list.index(i).just());
    }
  }
}
