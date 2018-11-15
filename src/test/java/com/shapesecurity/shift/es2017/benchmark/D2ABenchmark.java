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

package com.shapesecurity.shift.es2017.benchmark;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.codegen.CodeGen;
import org.junit.Test;

public class D2ABenchmark {

    private static final int WARMUP = 10000;
    private static final int MEASURE = 100;

    @Test
    public void benchmarkD2A() {
        ImmutableList<Double> numbers = ImmutableList.empty();
        for (int i = 0; i < 100; i++) {
            numbers = numbers.cons(((double) i - 50));
            if (i % 10 == 0) {
                numbers = numbers.cons(((double) i - 50 + .5));
            }
        }
        ArrayExpression ns =
            new ArrayExpression(numbers
                .map(LiteralNumericExpression::new)
                .map(Maybe::of));
        Script script = new Script(ImmutableList.empty(), ImmutableList.from(
            new ExpressionStatement(ns)
        ));
        for (int i = 0; i < WARMUP; i++) {
            CodeGen.codeGen(script);
        }
        long start = System.nanoTime();

        for (int i = 0; i < MEASURE; i++) {
            CodeGen.codeGen(script);
        }
        long elapse = System.nanoTime() - start;
        System.out.printf("D2A: %.3fµs\n", elapse * 1e-3 / MEASURE);
    }
}
