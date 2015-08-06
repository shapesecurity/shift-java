package com.shapesecurity.shift.parser.expressions.literals;

import com.shapesecurity.shift.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.ast.LiteralNullExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserTestCase;

import org.junit.Test;

public class LiteralNullExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralNullExpression() throws JsError {
        testScript("null", new LiteralNullExpression());
        testScript("null;", new LiteralNullExpression());
        testScript("null\n", new LiteralNullExpression());
    }
}
