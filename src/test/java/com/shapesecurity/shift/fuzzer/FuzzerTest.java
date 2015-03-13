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


package com.shapesecurity.shift.fuzzer;

import java.util.Random;

import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.codegen.CodeGen;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.validator.Validator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FuzzerTest {
  private void test(int i, int depth) throws JsError {
    Script tree = Fuzzer.generate(new Random(i), depth);
    String text = CodeGen.codeGen(tree, true);
    assertEquals("Case " + i, 0, Validator.validate(tree).length);
    try {
      Parser.parse(text);
    } catch (JsError e) {
      System.out.println(i);
      System.out.println(e.getMessage());
      System.out.println("--------------------------");
      System.out.println(text);
      throw e;
    }
  }

  @Test
  public void testFuzzer() throws JsError {
    Random random = new Random(0);
    Script script = Fuzzer.generate(random, 0);
    assertEquals("", CodeGen.codeGen(script));
    test(10, 10);
    long start = System.nanoTime();
    int N = 10000;
    for (int i = 799; i < N; i++) {
      test(i, 10);
      if (i % 30 == 0) {
        printStats(start, i, N);
      }
    }
    printStats(start, N, N);
    System.out.println();
    System.out.println();
  }

  private void printStats(long start, int i, int N) {
    long now = System.nanoTime();
    double tpm = i / ((now - start) * 1e-6);
    System.out.printf("[%5.1f%%] Average speed %.3f tests per milli-second. %.0f tests per min.\r",
        i * 1e2 / N, tpm, tpm * 6e4);
  }
}
