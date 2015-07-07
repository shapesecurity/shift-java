package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.AssignmentExpression;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.UnaryExpression;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class UnaryExpressionTest extends ParserTestCase {
  @Test
  public void testUnaryExpression() throws JsError {
    testScript("!a", new UnaryExpression(UnaryOperator.LogicalNot, new IdentifierExpression("a")));

    testScript("!(a=b)", new UnaryExpression(UnaryOperator.LogicalNot, new AssignmentExpression(
        new BindingIdentifier("a"), new IdentifierExpression("b"))));

    testScript("typeof a", new UnaryExpression(UnaryOperator.Typeof, new IdentifierExpression("a")));

    testScript("void a", new UnaryExpression(UnaryOperator.Void, new IdentifierExpression("a")));

    testScript("delete a", new UnaryExpression(UnaryOperator.Delete, new IdentifierExpression("a")));

    testScript("+a", new UnaryExpression(UnaryOperator.Plus, new IdentifierExpression("a")));

    testScript("~a", new UnaryExpression(UnaryOperator.BitNot, new IdentifierExpression("a")));

    testScript("-a", new UnaryExpression(UnaryOperator.Minus, new IdentifierExpression("a")));
  }
}
