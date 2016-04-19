package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class GroupingTest extends ParserTestCase {
    @Test
    public void testGrouping() throws JsError {
        testScript("((((((((((((((((((((((((((((((((((((((((((((((((((0))))))))))))))))))))))))))))))))))))))))))))))))))",
                new LiteralNumericExpression(0.0));

        testScript("(1 + 2 ) * 3", new BinaryExpression(new BinaryExpression(new
                LiteralNumericExpression(1.0), BinaryOperator.Plus, new LiteralNumericExpression(2.0)), BinaryOperator.Mul, new LiteralNumericExpression(3.0)));

        testScript("(1) + (2  ) + 3", new BinaryExpression(new BinaryExpression(
                new LiteralNumericExpression(1.0), BinaryOperator.Plus, new LiteralNumericExpression(2.0)), BinaryOperator.Plus, new LiteralNumericExpression(3.0)));

        testScript("4 + 5 << (6)", new BinaryExpression(new BinaryExpression(
                new LiteralNumericExpression(4.0), BinaryOperator.Plus, new LiteralNumericExpression(5.0)), BinaryOperator.Left, new LiteralNumericExpression(6.0)));

        testScript("(a) + (b)", new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.Plus,
                new IdentifierExpression("b")));

        testScript("(a)", new IdentifierExpression("a"));

        testScript("((a))", new IdentifierExpression("a"));

        testScript("((a))()", new CallExpression(new IdentifierExpression("a"), ImmutableList.nil()));

        testScript("((a))((a))", new CallExpression(new IdentifierExpression("a"), ImmutableList.list(
                new IdentifierExpression("a"))));

        testScript("(a) = 0", new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("((a)) = 0", new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("void (a)", new UnaryExpression(UnaryOperator.Void, new IdentifierExpression("a")));

        testScript("(void a)", new UnaryExpression(UnaryOperator.Void, new IdentifierExpression("a")));

        testScript("(a++)", new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("a")));

        testScript("(a)++", new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("a")));

        testScript("(a)--", new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("a")));

        testScript("(a) ? (b) : (c)", new ConditionalExpression(new IdentifierExpression("a"), new IdentifierExpression("b"),
                new IdentifierExpression("c")));
    }
}
