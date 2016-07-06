/* Copyright (c) 2016 Shape Security, Inc. This source code and/or documentation is the confidential and copyrighted
 * property of Shape Security, Inc. All rights are reserved to Shape Security, Inc. The reproduction, preparation of
 * derivative works, distribution, public performance or public display of the source code and/or documentation is
 * not authorized unless expressly licensed.
 * Please contact
 * 		Shape Security, Inc., Attn: Licensing Department,
 * 		P.O. Box 772, Palo Alto, CA 94302
 * 		licensing@shapesecurity.com
 * 		650-399-0400
 * for information regarding commercial and/or trial license availability.
 */


package com.shapesecurity.shift.benchmark;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.ArrayExpression;
import com.shapesecurity.shift.ast.ExpressionStatement;
import com.shapesecurity.shift.ast.LiteralNumericExpression;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.codegen.CodeGen;
import org.junit.Test;

public class D2ABenchmark {
    @Test
    public void benchmarkD2A() {
        ImmutableList<Double> numbers = ImmutableList.nil();
        for (int i = 0; i < 100; i++) {
            numbers = numbers.cons((double) i - 50);
        }
        ArrayExpression ns =
            new ArrayExpression(numbers
                .map(LiteralNumericExpression::new)
                .map(Maybe::just));
        Script script = new Script(ImmutableList.nil(), ImmutableList.from(
            new ExpressionStatement(ns)
        ));
        for (int i = 0; i < 1000; i++) {
            CodeGen.codeGen(script);
        }
        long start = System.nanoTime();
        int n = 1000;

        for (int i = 0; i < n; i++) {
            CodeGen.codeGen(script);
        }
        long elapse = System.nanoTime() - start;
        System.out.printf("D2A: %.3fµs\n", elapse * 1e-3 / n);
    }
}
