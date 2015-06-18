package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class GroupingTest extends Assertions {
  @Test
  public void testGrouping() throws JsError {
    testScript("((((((((((((((((((((((((((((((((((((((((((((((((((0))))))))))))))))))))))))))))))))))))))))))))))))))",
        new LiteralNumericExpression(0.0));

    testScript("(1 + 2 ) * 3", new BinaryExpression(BinaryOperator.Mul, new BinaryExpression(BinaryOperator.Plus, new
        LiteralNumericExpression(1.0), new LiteralNumericExpression(2.0)), new LiteralNumericExpression(3.0)));

    testScript("(1) + (2  ) + 3", new BinaryExpression(BinaryOperator.Plus, new BinaryExpression(BinaryOperator.Plus,
        new LiteralNumericExpression(1.0), new LiteralNumericExpression(2.0)), new LiteralNumericExpression(3.0)));

    testScript("4 + 5 << (6)", new BinaryExpression(BinaryOperator.Left, new BinaryExpression(BinaryOperator.Plus,
        new LiteralNumericExpression(4.0), new LiteralNumericExpression(5.0)), new LiteralNumericExpression(6.0)));

    testScript("(a) + (b)", new BinaryExpression(BinaryOperator.Plus, new IdentifierExpression("a"),
        new IdentifierExpression("b")));

    testScript("(a)", new IdentifierExpression("a"));

    testScript("((a))", new IdentifierExpression("a"));

    testScript("((a))()", new CallExpression(new IdentifierExpression("a"), ImmutableList.nil()));

    testScript("((a))((a))", new CallExpression(new IdentifierExpression("a"), ImmutableList.list(
        new IdentifierExpression("a"))));

    testScript("(a) = 0", new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)));

    testScript("((a)) = 0", new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)));

    testScript("void (a)", new UnaryExpression(UnaryOperator.Void, new IdentifierExpression("a")));

    testScript("(void a)", new UnaryExpression(UnaryOperator.Void, new IdentifierExpression("a")));

    testScript("(a++)", new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("a")));

    testScript("(a)++", new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("a")));

    testScript("(a)--", new UpdateExpression(false, UpdateOperator.Decrement, new BindingIdentifier("a")));

    testScript("(a) ? (b) : (c)", new ConditionalExpression(new IdentifierExpression("a"), new IdentifierExpression("b"),
        new IdentifierExpression("c")));
  }
}
