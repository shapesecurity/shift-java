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


package com.shapesecurity.shift.es2017.fuzzer;

import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.parser.JsError;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Random;

public class FuzzerTest extends TestCase {
    private void test(int i, int depth) throws JsError {
        Node tree = Fuzzer.generate(new Random(i), depth);
//    String text = CodeGen.codeGen(tree, true);
//    assertEquals("Case " + i, 0, Validator.validate(tree).length);
//    try {
//      Parser.parseScript(text);
//    } catch (JsError e) {
//      System.out.println(i);
//      System.out.println(e.getMessage());
//      System.out.println("--------------------------");
//      System.out.println(text);
//      throw e;
//    }
    }

    @Test
    public void testFuzzer() throws JsError {
        Random random = new Random(1);
//    Node script = Fuzzer.generate(random, 0);
//    assertEquals("", CodeGen.codeGen(script));
        Node script = Fuzzer.generate(random, 10);
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
