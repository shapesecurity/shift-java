package com.shapesecurity.shift.es2016.parser.expressions;

import com.shapesecurity.shift.es2016.ast.ThisExpression;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class ThisExpressionTest extends ParserTestCase {

    @Test
    public void testThisExpression() throws JsError {
        testScript("this;", new ThisExpression());
        testModule("this;", new ThisExpression());
    }
}
