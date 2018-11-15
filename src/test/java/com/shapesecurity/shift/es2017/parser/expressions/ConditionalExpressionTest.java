package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.ConditionalExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class ConditionalExpressionTest extends ParserTestCase {
    @Test
    public void testConditionalExpression() throws JsError {
        testScript("a?b:c", new ConditionalExpression(new IdentifierExpression("a"), new IdentifierExpression("b"),
                new IdentifierExpression("c")));

        testScript("y ? 1 : 2", new ConditionalExpression(new IdentifierExpression("y"), new LiteralNumericExpression(1.0),
                new LiteralNumericExpression(2.0)));

        testScript("x && y ? 1 : 2", new ConditionalExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.LogicalAnd, new IdentifierExpression("y")), new LiteralNumericExpression(1.0),
                new LiteralNumericExpression(2.0)));

        testScript("x = (0) ? 1 : 2", new AssignmentExpression(new AssignmentTargetIdentifier("x"), new ConditionalExpression(
                new LiteralNumericExpression(0.0), new LiteralNumericExpression(1.0), new LiteralNumericExpression(2.0))));
    }
}
