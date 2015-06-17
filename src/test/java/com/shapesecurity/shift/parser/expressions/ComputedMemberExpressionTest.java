package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/12/15.
 */
public class ComputedMemberExpressionTest extends Assertions {
  @Test
  public void testComputedMemberExpression() throws JsError {
    testScript("a[b, c]", new ComputedMemberExpression(new BinaryExpression(
        BinaryOperator.Sequence, new IdentifierExpression("b"), new IdentifierExpression("c")), new IdentifierExpression("a")));

    testScript("a[b]", new ComputedMemberExpression(new IdentifierExpression("b"), new IdentifierExpression("a")));

    testScript("a[b] = b",new AssignmentExpression(new ComputedMemberExpression(new IdentifierExpression("b"), new IdentifierExpression("a")), new IdentifierExpression("b")));

    testScript("(a[b]||(c[d]=e))", new BinaryExpression(BinaryOperator.LogicalOr, new ComputedMemberExpression(
        new IdentifierExpression("b"), new IdentifierExpression("a")), new AssignmentExpression(
        new ComputedMemberExpression(new IdentifierExpression("d"), new IdentifierExpression("c")),
        new IdentifierExpression("e"))));

    testScript("a&&(b=c)&&(d=e)", new BinaryExpression(BinaryOperator.LogicalAnd, new BinaryExpression(
        BinaryOperator.LogicalAnd, new IdentifierExpression("a"), new AssignmentExpression(new BindingIdentifier("b"),
        new IdentifierExpression("c"))), new AssignmentExpression(new BindingIdentifier("d"),
        new IdentifierExpression("e"))));
  }
}
