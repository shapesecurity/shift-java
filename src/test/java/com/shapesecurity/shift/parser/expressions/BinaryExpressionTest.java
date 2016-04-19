package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class BinaryExpressionTest extends ParserTestCase {
    @Test
    public void testBinaryExpression() throws JsError {
        testScript("1+2", new BinaryExpression(new LiteralNumericExpression(1.0), BinaryOperator.Plus,
                new LiteralNumericExpression(2.0)));

        testScript("x & y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.BitwiseAnd,
                new IdentifierExpression("y")));

        testScript("x ^ y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.BitwiseXor,
                new IdentifierExpression("y")));

        testScript("x | y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.BitwiseOr,
                new IdentifierExpression("y")));

        testScript("x + y + z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Plus, new IdentifierExpression("y")), BinaryOperator.Plus, new IdentifierExpression("z")));

        testScript("x - y + z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Minus, new IdentifierExpression("y")), BinaryOperator.Plus, new IdentifierExpression("z")));

        testScript("x + y - z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Plus, new IdentifierExpression("y")), BinaryOperator.Minus, new IdentifierExpression("z")));

        testScript("x - y - z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Minus, new IdentifierExpression("y")), BinaryOperator.Minus, new IdentifierExpression("z")));

        testScript("x + y * z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Plus,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.Mul, new IdentifierExpression("z"))));

        testScript("x + y / z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Plus,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.Div, new IdentifierExpression("z"))));

        testScript("x - y % z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Minus,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.Rem, new IdentifierExpression("z"))));

        testScript("x * y * z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Mul, new IdentifierExpression("y")), BinaryOperator.Mul, new IdentifierExpression("z")));

        testScript("x * y / z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Mul, new IdentifierExpression("y")), BinaryOperator.Div, new IdentifierExpression("z")));

        testScript("x * y % z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Mul, new IdentifierExpression("y")), BinaryOperator.Rem, new IdentifierExpression("z")));

        testScript("x % y * z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Rem, new IdentifierExpression("y")), BinaryOperator.Mul, new IdentifierExpression("z")));

        testScript("x << y << z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.Left, new IdentifierExpression("y")), BinaryOperator.Left, new IdentifierExpression("z")));

        testScript("x | y | z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.BitwiseOr, new IdentifierExpression("y")), BinaryOperator.BitwiseOr, new IdentifierExpression("z")));

        testScript("x & y & z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.BitwiseAnd, new IdentifierExpression("y")), BinaryOperator.BitwiseAnd, new IdentifierExpression("z")));

        testScript("x ^ y ^ z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.BitwiseXor, new IdentifierExpression("y")), BinaryOperator.BitwiseXor, new IdentifierExpression("z")));

        testScript("x & y | z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.BitwiseAnd, new IdentifierExpression("y")), BinaryOperator.BitwiseOr, new IdentifierExpression("z")));

        testScript("x | y ^ z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.BitwiseOr,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.BitwiseXor, new IdentifierExpression("z"))));

        testScript("x | y & z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.BitwiseOr,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.BitwiseAnd, new IdentifierExpression("z"))));

        testScript("x || y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LogicalOr,
                new IdentifierExpression("y")));

        testScript("x && y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LogicalAnd,
                new IdentifierExpression("y")));

        testScript("x || y || z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.LogicalOr, new IdentifierExpression("y")), BinaryOperator.LogicalOr, new IdentifierExpression("z")));

        testScript("x && y && z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.LogicalAnd, new IdentifierExpression("y")), BinaryOperator.LogicalAnd, new IdentifierExpression("z")));

        testScript("x || y && z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LogicalOr,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.LogicalAnd,
                        new IdentifierExpression("z"))));

        testScript("x || y ^ z", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LogicalOr,
                new BinaryExpression(new IdentifierExpression("y"), BinaryOperator.BitwiseXor, new IdentifierExpression("z"))));

        testScript("x * y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Mul,
                new IdentifierExpression("y")));

        testScript("x / y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Div,
                new IdentifierExpression("y")));

        testScript("x % y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Rem,
                new IdentifierExpression("y")));

        testScript("x + y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Plus,
                new IdentifierExpression("y")));

        testScript("x - y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Minus,
                new IdentifierExpression("y")));

        testScript("x << y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Left,
                new IdentifierExpression("y")));

        testScript("x >> y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Right,
                new IdentifierExpression("y")));

        testScript("x >>> y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.UnsignedRight,
                new IdentifierExpression("y")));

        testScript("x < y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LessThan,
                new IdentifierExpression("y")));

        testScript("x > y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.GreaterThan,
                new IdentifierExpression("y")));

        testScript("x <= y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LessThanEqual,
                new IdentifierExpression("y")));

        testScript("x >= y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.GreaterThanEqual,
                new IdentifierExpression("y")));

        testScript("x in y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.In,
                new IdentifierExpression("y")));

        testScript("x instanceof y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Instanceof,
                new IdentifierExpression("y")));

        testScript("x < y < z", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.LessThan, new IdentifierExpression("y")), BinaryOperator.LessThan, new IdentifierExpression("z")));

        testScript("x == y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Equal,
                new IdentifierExpression("y")));

        testScript("x != y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.NotEqual,
                new IdentifierExpression("y")));

        testScript("x === y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.StrictEqual,
                new IdentifierExpression("y")));

        testScript("x !== y", new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.StrictNotEqual,
                new IdentifierExpression("y")));

        testScript("(a, e=0)", new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.Sequence, new AssignmentExpression(new AssignmentTargetIdentifier("e"), new LiteralNumericExpression(0.0))));
    }
}
