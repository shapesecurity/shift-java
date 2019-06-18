package com.shapesecurity.shift.es2018.parser.expressions.literals;

import com.shapesecurity.shift.es2018.ast.LiteralInfinityExpression;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;

import org.junit.Test;

public class LiteralInfinityExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralInfinityExpressionTest() throws JsError {
        testScript("2e308", new LiteralInfinityExpression());
    }
}
