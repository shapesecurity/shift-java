package com.shapesecurity.shift.es2016.parser.expressions.literals;

import com.shapesecurity.shift.es2016.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class LiteralNullExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralNullExpression() throws JsError {
        testScript("null", new LiteralNullExpression());
        testScript("null;", new LiteralNullExpression());
        testScript("null\n", new LiteralNullExpression());
    }
}
