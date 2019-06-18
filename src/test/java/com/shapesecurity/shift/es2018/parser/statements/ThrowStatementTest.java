package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2018.ast.BinaryExpression;
import com.shapesecurity.shift.es2018.ast.IdentifierExpression;
import com.shapesecurity.shift.es2018.ast.ObjectExpression;
import com.shapesecurity.shift.es2018.ast.ThrowStatement;
import com.shapesecurity.shift.es2018.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2018.ast.ThisExpression;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

import org.junit.Test;

public class ThrowStatementTest extends ParserTestCase {
    @Test
    public void testThrowStatement() throws JsError {
        testScript("throw this", new ThrowStatement(new ThisExpression()));

        testScript("throw x", new ThrowStatement(new IdentifierExpression("x")));

        testScript("throw x * y", new ThrowStatement(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Mul,
                new IdentifierExpression("y"))));

        testScript("throw {}", new ThrowStatement(new ObjectExpression(ImmutableList.empty())));
    }
}
