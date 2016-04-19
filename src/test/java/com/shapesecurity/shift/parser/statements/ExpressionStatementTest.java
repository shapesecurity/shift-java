package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.shift.ast.BinaryExpression;
import com.shapesecurity.shift.ast.ExpressionStatement;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ExpressionStatementTest extends ParserTestCase {
    @Test
    public void testExpressionStatement() throws JsError {
        testScript("x", new ExpressionStatement(new IdentifierExpression("x")));

        testScript("x, y", new ExpressionStatement(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Sequence, new IdentifierExpression("y"))));

        testScript("\\u0061", new ExpressionStatement(new IdentifierExpression("a")));

        testScript("a\\u0061", new ExpressionStatement(new IdentifierExpression("aa")));

        testScript("\\u0061a", new ExpressionStatement(new IdentifierExpression("aa")));

        testScript("\\u0061a ", new ExpressionStatement(new IdentifierExpression("aa")));
    }
}
