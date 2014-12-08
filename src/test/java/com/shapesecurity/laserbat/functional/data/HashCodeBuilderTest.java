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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class HashCodeBuilderTest extends TestBase {
  @Test
  public void testSimple() {
    int i = HashCodeBuilder.init();
    assertNotEquals(HashCodeBuilder.put(i, 3), HashCodeBuilder.put(i, 4));
  }

  @Test
  public void testCollisionOnNumbers() throws IOException {
    int h = HashCodeBuilder.init();
    HashSet<Integer> hashes = new HashSet<>();
    for (int i = 0; i < 1e6; i++) {
      int a = HashCodeBuilder.put(h, i);
      assertFalse(hashes.contains(a));
      hashes.add(a);
      i++;
    }
  }

  @Test
  public void testCollision() throws IOException {
    String dict = readFile("dictionary.txt");
    String[] lines = dict.split("\n");
    HashMap<Integer, Integer> map = new HashMap<>();
    int max = 1;
    for (int i = 0; i < lines.length; i++) {
      char[] chars = lines[i].toCharArray();
      int h = HashCodeBuilder.init();
      for (int j = 0; j < chars.length; j++) {
        h = HashCodeBuilder.putChar(h, chars[j]);
      }
      int count = 0;
      if (map.containsKey(h)) {
        count = map.get(h);
      }
      if (count == max) {
        max = count + 1;
      }
      map.put(h, count + 1);
    }
    assertTrue(max < 3);
  }
}
