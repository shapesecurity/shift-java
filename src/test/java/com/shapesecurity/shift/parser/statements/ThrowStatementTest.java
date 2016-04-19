package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ThrowStatementTest extends ParserTestCase {
    @Test
    public void testThrowStatement() throws JsError {
        testScript("throw this", new ThrowStatement(new ThisExpression()));

        testScript("throw x", new ThrowStatement(new IdentifierExpression("x")));

        testScript("throw x * y", new ThrowStatement(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Mul,
                new IdentifierExpression("y"))));

        testScript("throw {}", new ThrowStatement(new ObjectExpression(ImmutableList.nil())));
    }
}
