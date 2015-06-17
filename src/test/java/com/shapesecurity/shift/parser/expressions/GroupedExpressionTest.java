package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.BinaryExpression;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.LiteralNumericExpression;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class GroupedExpressionTest extends Assertions {
  @Test
  public void testGroupedExpression() throws JsError {
    testScript("(a)", new IdentifierExpression("a"));

    testScript("(0)", new LiteralNumericExpression(0.0));

    testScript("(0, a)", new BinaryExpression(BinaryOperator.Sequence, new LiteralNumericExpression(0.0),
        new IdentifierExpression("a")));

    testScript("(a, 0)", new BinaryExpression(BinaryOperator.Sequence, new IdentifierExpression("a"),
        new LiteralNumericExpression(0.0)));

    testScript("(a, a)", new BinaryExpression(BinaryOperator.Sequence, new IdentifierExpression("a"),
        new IdentifierExpression("a")));

    testScript("((a,a),(a,a))", new BinaryExpression(BinaryOperator.Sequence, new BinaryExpression(
        BinaryOperator.Sequence, new IdentifierExpression("a"), new IdentifierExpression("a")), new BinaryExpression(
        BinaryOperator.Sequence, new IdentifierExpression("a"), new IdentifierExpression("a"))));

    testScript("((((((((((((((((((((((((((((((((((((((((a))))))))))))))))))))))))))))))))))))))))",
        new IdentifierExpression("a"));

//    testScriptFailure("(0, {a = 0}) = 0", 0, "Invalid left-hand side in assignment");
//    testScriptFailure("({a = 0})", 0, "Illegal property initializer");
//    testScriptFailure("(0, {a = 0}) => 0", 0, "Illegal arrow function parameter list");
//    testScriptFailure("({a = 0}, {a = 0}, 0) => 0", 0, "Unexpected number");
  }
}
