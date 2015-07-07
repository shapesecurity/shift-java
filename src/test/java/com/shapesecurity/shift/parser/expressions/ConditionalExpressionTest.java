package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ConditionalExpressionTest extends ParserTestCase {
  @Test
  public void testConditionalExpression() throws JsError {
    testScript("a?b:c", new ConditionalExpression(new IdentifierExpression("a"), new IdentifierExpression("b"),
        new IdentifierExpression("c")));

    testScript("y ? 1 : 2", new ConditionalExpression(new IdentifierExpression("y"), new LiteralNumericExpression(1.0),
        new LiteralNumericExpression(2.0)));

    testScript("x && y ? 1 : 2", new ConditionalExpression(new BinaryExpression(BinaryOperator.LogicalAnd,
        new IdentifierExpression("x"), new IdentifierExpression("y")), new LiteralNumericExpression(1.0),
        new LiteralNumericExpression(2.0)));

    testScript("x = (0) ? 1 : 2", new AssignmentExpression(new BindingIdentifier("x"), new ConditionalExpression(
        new LiteralNumericExpression(0.0), new LiteralNumericExpression(1.0), new LiteralNumericExpression(2.0))));
  }
}
