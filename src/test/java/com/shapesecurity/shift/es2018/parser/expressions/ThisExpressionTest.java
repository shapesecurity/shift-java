package com.shapesecurity.shift.es2018.parser.expressions;

import com.shapesecurity.shift.es2018.ast.ThisExpression;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;

import org.junit.Test;

public class ThisExpressionTest extends ParserTestCase {

    @Test
    public void testThisExpression() throws JsError {
        testScript("this;", new ThisExpression());
        testModule("this;", new ThisExpression());
    }
}
