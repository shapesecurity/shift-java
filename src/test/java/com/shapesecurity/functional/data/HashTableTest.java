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

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.TestBase;

import org.junit.Test;

import org.jetbrains.annotations.NotNull;

public class HashTableTest extends TestBase {
  private static final Hasher<String> BAD_HASHER = new Hasher<String>() {
    @Override
    public int hash(@NotNull String data) {
      return 0;
    }

    @Override
    public boolean eq(@NotNull String s, @NotNull String b) {
      return s.equals(b);
    }
  };
  private static final long multiplier = 0x5DEECE66DL;
  private static final long addend = 0xBL;
  private static final long mask = (1L << 48) - 1;

  @Test
  public void simpleTests() {
    HashTable<String, Integer> e = HashTable.empty();
    int N = 100000;
    for (int i = 0; i < N; i++) {
      e = e.put(Integer.toString(i), i);
    }
    HashTable<String, Integer> e1 = e.put("a", 3);
    HashTable<String, Integer> e2 = e1.put("b", 3);
    HashTable<String, Integer> e3 = e1.put("c", 3);
    HashTable<String, Integer> e4 = e1.put("a", 4);

    assertEquals(N, e.length);
    assertEquals(N + 1, e1.length);
    assertEquals(N + 2, e2.length);
    assertEquals(N + 2, e3.length);
    assertEquals(N + 1, e4.length);

    assertEquals(Maybe.<Integer>nothing(), e.get("a"));
    assertEquals(Maybe.<Integer>nothing(), e.get("b"));
    assertEquals(Maybe.<Integer>nothing(), e.get("c"));

    assertEquals(Maybe.just(3), e1.get("a"));
    assertEquals(Maybe.<Integer>nothing(), e1.get("b"));
    assertEquals(Maybe.<Integer>nothing(), e1.get("c"));

    assertEquals(Maybe.just(3), e2.get("a"));
    assertEquals(Maybe.just(3), e2.get("b"));
    assertEquals(Maybe.<Integer>nothing(), e2.get("c"));

    assertEquals(Maybe.just(3), e3.get("a"));
    assertEquals(Maybe.<Integer>nothing(), e3.get("b"));
    assertEquals(Maybe.just(3), e3.get("c"));

    assertEquals(Maybe.just(4), e4.get("a"));
    assertEquals(Maybe.<Integer>nothing(), e4.get("b"));
    assertEquals(Maybe.<Integer>nothing(), e4.get("c"));

    e4 = e4.put("a", 5);
    assertEquals(Maybe.just(5), e4.get("a"));
    assertEquals(Maybe.<Integer>nothing(), e4.get("b"));
    assertEquals(Maybe.<Integer>nothing(), e4.get("c"));
  }

  long next(long seed) {
    return (seed * multiplier + addend) & mask;
  }

  @Test
  public void deletionTest() {
    HashTable<String, Integer> e = HashTable.empty();
    int N = 100000;
    for (int i = 0; i < N; i++) {
      e = e.put(Integer.toString(i), i);
    }
    int[] shuffled = shuffle(0x12345, N);

    for (int i = 0; i < N / 2; i++) {
      String key = Integer.toString(shuffled[i]);
      assertEquals(Maybe.just(shuffled[i]), e.get(key));
      e = e.remove(key);
      assertEquals(Maybe.<Integer>nothing(), e.get(key));
      assertEquals(N - i - 1, e.length);
      assertEquals(e.length, e.remove(key).length);
    }
    for (int i = N / 2; i < N; i++) {
      String key = Integer.toString(shuffled[i]);
      assertEquals(Maybe.just(shuffled[i]), e.get(key));
    }
  }

  @Test
  public void deletionTest2() {
    HashTable<String, Integer> e = HashTable.empty(BAD_HASHER);
    int N = 1000;
    for (int i = 0; i < N; i++) {
      e = e.put(Integer.toString(i), i);
    }
    int[] shuffled = shuffle(0x12345, N);

    for (int i = 0; i < N / 2; i++) {
      String key = Integer.toString(shuffled[i]);
      assertEquals(Maybe.just(shuffled[i]), e.get(key));
      e = e.remove(key);
      assertEquals(Maybe.<Integer>nothing(), e.get(key));
      assertEquals(N - i - 1, e.length);
    }
    for (int i = N / 2; i < N; i++) {
      String key = Integer.toString(shuffled[i]);
      assertEquals(Maybe.just(shuffled[i]), e.get(key));
    }
  }

  private int[] shuffle(long seed, int n) {
    int[] shuffled = new int[n];
    for (int i = 0; i < n; i++) {
      seed = next(seed);
      int j = (int) (seed % (i + 1));
      if (j != i) {
        shuffled[i] = shuffled[j];
      }
      shuffled[j] = i;
    }
    return shuffled;
  }

  @Test
  public void traversalTests() {
    HashTable<String, Integer> e = HashTable.empty();
    int N = 10000;
    for (int i = 0; i < N; i++) {
      e = e.put(Integer.toString(i), i);
    }
    final boolean[] visited = new boolean[N];

    final int[] count = new int[1];
    assertEquals(N, e.length);
    e.entries().foreach(p -> {
      assertEquals(p.a, Integer.toString(p.b));
      assertFalse(visited[p.b]);
      visited[p.b] = true;
      count[0]++;
    });
    assertEquals(N, count[0]);
  }

  @Test
  public void mergeTestSimple() {
    HashTable<String, Integer> t1 = HashTable.empty();
    HashTable<String, Integer> t2 = HashTable.empty();
    assertEquals(0, t1.merge(t2).length);
    t1 = t1.put("a", 1);
    assertEquals(1, t1.merge(t2).length);
    t1 = t1.put("b", 1);
    assertEquals(2, t1.merge(t2).length);
    t2 = t2.put("b", 2);
    assertEquals(2, t1.merge(t2).length);
    assertEquals(2, (int) t1.merge(t2).get("b").just());
  }

  @Test
  public void mergeTest() {
    HashTable<String, Integer> t1 = HashTable.empty();
    HashTable<String, Integer> t2 = HashTable.empty();
    int N = 10000;
    int[] shuffled = shuffle(0x12345, N);
    for (int i = 0; i < N; i += 2) {
      t1 = t1.put(Integer.toString(shuffled[i]), shuffled[i]);
      t2 = t2.put(Integer.toString(shuffled[i + 1]), shuffled[i + 1]);
    }
    HashTable<String, Integer> t = t1.merge(t2);
    assertEquals(N, t.length);
    for (int i = 0; i < N; i++) {
      assertTrue(t.get(Integer.toString(i)).isJust());
    }
  }

  @Test
  public void mergeTest2() {
    HashTable<String, Integer> t1 = HashTable.empty(BAD_HASHER);
    HashTable<String, Integer> t2 = HashTable.empty(BAD_HASHER);
    int N = 1000;
    int[] shuffled = shuffle(0x12345, N);
    for (int i = 0; i < N; i += 2) {
      t1 = t1.put(Integer.toString(shuffled[i]), shuffled[i]);
      t2 = t2.put(Integer.toString(shuffled[i + 1]), shuffled[i + 1]);
    }
    HashTable<String, Integer> t = t1.merge(t2);
    assertEquals(N, t.length);
    for (int i = 0; i < N; i++) {
      assertTrue(t.get(Integer.toString(i)).isJust());
    }
  }

  @Test
  public void getTest() {
    HashTable<String, Integer> t = HashTable.empty(BAD_HASHER);
    int N = 1000;
    for (int i = 0; i < N; i++) {
      t = t.put(Integer.toString(i), i);
    }

    for (int i = 0; i < N; i++) {
      assertEquals((Integer) i, t.get(Integer.toString(i)).just());
    }
    for (int i = 0; i < N; i++) {
      assertEquals(Maybe.<Integer>nothing(), t.get(Integer.toString(i + N)));
    }
  }

  @Test
  public void findTest() {
    HashTable<String, Integer> t = HashTable.empty();
    assertEquals(Maybe.<Pair<String, Integer>>nothing(), t.find(x -> x.a.equals("a")));
    int N = 1000;
    for (int i = 0; i < N; i++) {
      t = t.put(Integer.toString(i), i);
    }
    for (int i = 0; i < N; i++) {
      int iFinal = i;
      assertEquals(i, (int) t.find(x -> x.b == iFinal).just().b);
    }
  }

  @Test
  public void findMapTest() {
    int N = 1000;
    HashTable<String, Integer> empty = HashTable.<String, Integer>empty();
    assertEquals(Maybe.<Integer>nothing(), empty.findMap(x -> Maybe.just(0)));
    HashTable<String, Integer> t = range(0, N).foldLeft((ht, i) -> {
      return ht.put(Integer.toString(i), i);
    }, empty);

    range(0, N).foreach(i -> {
      assertEquals(Integer.toString(i), t.findMap(x -> Maybe.iff(x.b.equals(i), x.a)).just());
    });
  }

  @Test
  public void foldLeftTest() {
    HashTable.<String, Integer>empty().foldLeft((i, a) -> {
      throw new RuntimeException("not reached");
    }, 0);
    int N = 10000;
    HashTable<String, Integer> t = range(0, N).foldLeft((ht, i) -> ht.put(Integer.toString(i), i),
        HashTable.<String, Integer>empty());
    assertEquals(N * (N - 1) / 2, (int) t.foldLeft((a, i) -> a + i.b, 0));
  }

  @Test
  public void foldRightTest() {
    HashTable.<String, Integer>empty().foldRight((a, i) -> {
      throw new RuntimeException("not reached");
    }, 0);
    int N = 10000;
    HashTable<String, Integer> t = range(0, N).foldLeft((ht, i) -> ht.put(Integer.toString(i), i),
        HashTable.<String, Integer>empty());
    assertEquals(N * (N - 1) / 2, (int) t.foldRight((i, a) -> a + i.b, 0));
  }

  @Test
  public void forEachTest() {
    HashTable.<String, Integer>empty().foreach((p) -> {
      throw new RuntimeException("not reached");
    });
    int N = 10000;
    HashTable<String, Integer> t = range(0, N).foldLeft((ht, i) -> ht.put(Integer.toString(i), i),
        HashTable.<String, Integer>empty());
    int a[] = new int[1];
    t.foreach(entry -> a[0] += entry.b);
    assertEquals(N * (N - 1) / 2, a[0]);
  }

  @Test
  public void mapTest() {
    assertEquals(0, HashTable.<String, Integer>empty().map(x -> x + 1).length);
    int N = 10000;
    HashTable<String, Integer> t = range(0, N).foldLeft((ht, i) -> ht.put(Integer.toString(i), i),
        HashTable.<String, Integer>empty());
    t = t.map(x -> x + 1);
    t.foreach(pair -> {
      assertEquals(Integer.parseInt(pair.a), pair.b - 1);
    });
  }
}
