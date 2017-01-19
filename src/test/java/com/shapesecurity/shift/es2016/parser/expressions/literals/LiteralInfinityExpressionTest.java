package com.shapesecurity.shift.es2016.parser.expressions.literals;

import com.shapesecurity.shift.es2016.ast.LiteralInfinityExpression;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class LiteralInfinityExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralInfinityExpressionTest() throws JsError {
        testScript("2e308", new LiteralInfinityExpression());
    }
}
