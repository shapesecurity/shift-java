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

package com.shapesecurity.functional;

import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.NonEmptyList;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public abstract class TestBase {
  private static final String BASE_PATH = System.getenv("CONFIG_DIR") == null ? "src/test/resources" : System.getenv(
      "CONFIG_DIR");

  private static Random rand = new Random();

  protected static int rand(int low, int high) {
    return rand.nextInt(Math.max(high - low, 1)) + low;
  }

  protected static int rand() {
    return rand(-100, 101);
  }

  protected static Path getPath(String path) {
    Path pathObj = Paths.get(BASE_PATH + '/' + path);
    if (Files.exists(pathObj)) {
      return pathObj;
    } else {
      try {
        return Paths.get(TestBase.class.getResource("/" + path).toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @NotNull
  protected static String readFile(@NotNull String path) throws IOException {
    byte[] encoded = Files.readAllBytes(getPath(path));
    return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
  }

  // Tests

  // static

  static protected NonEmptyList<Integer> LONG_LIST = List.list(rand());

  static {
    for (int i = 0; i < 1000; i++) {
      LONG_LIST = LONG_LIST.cons(rand());
    }
  }

  @Before
  public void setUp() {
    rand = new Random(12345L);
  }

  @NotNull
  public List<Integer> range(final int upper) {
    return range(0, upper);
  }

  @NotNull
  public List<Integer> range(final int lower, final int upper) {
    return range(lower, upper, 1);
  }

  @NotNull
  public List<Integer> range(final int lower, final int upper, final int step) {
    List<Integer> result = List.nil();
    for (int i = upper - ((upper - lower + step - 1) % step + 1); i >= lower; i -= step) {
      result = result.cons(i);
    }
    return result;
  }
}
