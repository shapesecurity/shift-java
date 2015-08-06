package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class BinaryExpressionTest extends ParserTestCase {
    @Test
    public void testBinaryExpression() throws JsError {
        testScript("1+2", new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(1.0),
                new LiteralNumericExpression(2.0)));

        testScript("x & y", new BinaryExpression(BinaryOperator.BitwiseAnd, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x ^ y", new BinaryExpression(BinaryOperator.BitwiseXor, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x | y", new BinaryExpression(BinaryOperator.BitwiseOr, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x + y + z", new BinaryExpression(BinaryOperator.Plus, new BinaryExpression(BinaryOperator.Plus,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x - y + z", new BinaryExpression(BinaryOperator.Plus, new BinaryExpression(BinaryOperator.Minus,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x + y - z", new BinaryExpression(BinaryOperator.Minus, new BinaryExpression(BinaryOperator.Plus,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x - y - z", new BinaryExpression(BinaryOperator.Minus, new BinaryExpression(BinaryOperator.Minus,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x + y * z", new BinaryExpression(BinaryOperator.Plus, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.Mul, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x + y / z", new BinaryExpression(BinaryOperator.Plus, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.Div, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x - y % z", new BinaryExpression(BinaryOperator.Minus, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.Rem, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x * y * z", new BinaryExpression(BinaryOperator.Mul, new BinaryExpression(BinaryOperator.Mul,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x * y / z", new BinaryExpression(BinaryOperator.Div, new BinaryExpression(BinaryOperator.Mul,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x * y % z", new BinaryExpression(BinaryOperator.Rem, new BinaryExpression(BinaryOperator.Mul,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x % y * z", new BinaryExpression(BinaryOperator.Mul, new BinaryExpression(BinaryOperator.Rem,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x << y << z", new BinaryExpression(BinaryOperator.Left, new BinaryExpression(BinaryOperator.Left,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x | y | z", new BinaryExpression(BinaryOperator.BitwiseOr, new BinaryExpression(BinaryOperator.BitwiseOr,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x & y & z", new BinaryExpression(BinaryOperator.BitwiseAnd, new BinaryExpression(BinaryOperator.BitwiseAnd,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x ^ y ^ z", new BinaryExpression(BinaryOperator.BitwiseXor, new BinaryExpression(BinaryOperator.BitwiseXor,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x & y | z", new BinaryExpression(BinaryOperator.BitwiseOr, new BinaryExpression(BinaryOperator.BitwiseAnd,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x | y ^ z", new BinaryExpression(BinaryOperator.BitwiseOr, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.BitwiseXor, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x | y & z", new BinaryExpression(BinaryOperator.BitwiseOr, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.BitwiseAnd, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x || y", new BinaryExpression(BinaryOperator.LogicalOr, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x && y", new BinaryExpression(BinaryOperator.LogicalAnd, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x || y || z", new BinaryExpression(BinaryOperator.LogicalOr, new BinaryExpression(BinaryOperator.LogicalOr,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x && y && z", new BinaryExpression(BinaryOperator.LogicalAnd, new BinaryExpression(BinaryOperator.LogicalAnd,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x || y && z", new BinaryExpression(BinaryOperator.LogicalOr, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.LogicalAnd, new IdentifierExpression("y"),
                        new IdentifierExpression("z"))));

        testScript("x || y ^ z", new BinaryExpression(BinaryOperator.LogicalOr, new IdentifierExpression("x"),
                new BinaryExpression(BinaryOperator.BitwiseXor, new IdentifierExpression("y"), new IdentifierExpression("z"))));

        testScript("x * y", new BinaryExpression(BinaryOperator.Mul, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x / y", new BinaryExpression(BinaryOperator.Div, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x % y", new BinaryExpression(BinaryOperator.Rem, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x + y", new BinaryExpression(BinaryOperator.Plus, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x - y", new BinaryExpression(BinaryOperator.Minus, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x << y", new BinaryExpression(BinaryOperator.Left, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x >> y", new BinaryExpression(BinaryOperator.Right, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x >>> y", new BinaryExpression(BinaryOperator.UnsignedRight, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x < y", new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x > y", new BinaryExpression(BinaryOperator.GreaterThan, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x <= y", new BinaryExpression(BinaryOperator.LessThanEqual, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x >= y", new BinaryExpression(BinaryOperator.GreaterThanEqual, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x in y", new BinaryExpression(BinaryOperator.In, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x instanceof y", new BinaryExpression(BinaryOperator.Instanceof, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x < y < z", new BinaryExpression(BinaryOperator.LessThan, new BinaryExpression(BinaryOperator.LessThan,
                new IdentifierExpression("x"), new IdentifierExpression("y")), new IdentifierExpression("z")));

        testScript("x == y", new BinaryExpression(BinaryOperator.Equal, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x != y", new BinaryExpression(BinaryOperator.NotEqual, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x === y", new BinaryExpression(BinaryOperator.StrictEqual, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("x !== y", new BinaryExpression(BinaryOperator.StrictNotEqual, new IdentifierExpression("x"),
                new IdentifierExpression("y")));

        testScript("(a, e=0)", new BinaryExpression(BinaryOperator.Sequence, new IdentifierExpression("a"), new AssignmentExpression(new BindingIdentifier("e"), new LiteralNumericExpression(0.0))));
    }
}
