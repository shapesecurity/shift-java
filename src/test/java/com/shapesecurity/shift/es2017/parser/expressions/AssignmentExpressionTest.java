package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.CompoundAssignmentExpression;
import com.shapesecurity.shift.es2017.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class AssignmentExpressionTest extends ParserTestCase {
    @Test
    public void testAssignmentExpression() throws JsError {
        testScript("a=0;", new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("(a)=(0);", new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("x = 0", new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("eval = 0", new AssignmentExpression(new AssignmentTargetIdentifier("eval"), new LiteralNumericExpression(0.0)));

        testScript("arguments = 0", new AssignmentExpression(new AssignmentTargetIdentifier("arguments"),
                new LiteralNumericExpression(0.0)));

        testScript("x *= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignMul, new LiteralNumericExpression(0.0)));

        testScript("x **= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignExp, new LiteralNumericExpression(0.0)));

        testScript("x.x *= 0", new CompoundAssignmentExpression(
                new StaticMemberAssignmentTarget(new IdentifierExpression("x"), "x"), CompoundAssignmentOperator.AssignMul, new LiteralNumericExpression(0.0)));

        testScript("x /= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignDiv, new LiteralNumericExpression(0.0)));

        testScript("x %= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignRem, new LiteralNumericExpression(0.0)));

        testScript("x += 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignPlus, new LiteralNumericExpression(0.0)));

        testScript("x -= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignMinus, new LiteralNumericExpression(0.0)));

        testScript("x <<= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignLeftShift, new LiteralNumericExpression(0.0)));

        testScript("x >>= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignRightShift, new LiteralNumericExpression(0.0)));

        testScript("x >>>= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignUnsignedRightShift, new LiteralNumericExpression(0.0)));

        testScript("x &= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignBitAnd, new LiteralNumericExpression(0.0)));

        testScript("x ^= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignBitXor, new LiteralNumericExpression(0.0)));

        testScript("x |= 0", new CompoundAssignmentExpression(
                new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignBitOr, new LiteralNumericExpression(0.0)));

        testScript("x = (y += 0)", new AssignmentExpression(new AssignmentTargetIdentifier("x"),
                new CompoundAssignmentExpression(
                        new AssignmentTargetIdentifier("y"), CompoundAssignmentOperator.AssignPlus, new LiteralNumericExpression(0.0))));

        testScript("'use strict'; eval[0] = 0", new AssignmentExpression(new ComputedMemberAssignmentTarget(
                new IdentifierExpression("eval"), new LiteralNumericExpression(0.0)), new LiteralNumericExpression(0.0)));

        testScript("'use strict'; arguments[0] = 0", new AssignmentExpression(new ComputedMemberAssignmentTarget(
                new IdentifierExpression("arguments"), new LiteralNumericExpression(0.0)), new LiteralNumericExpression(0.0)));

        testScript("((((((((((((((((((((((((((((((((((((((((a)))))))))))))))))))))))))))))))))))))))) = 0",
                new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("((((((((((((((((((((((((((((((((((((((((a.a)))))))))))))))))))))))))))))))))))))))) = 0",
                new AssignmentExpression(new StaticMemberAssignmentTarget(new IdentifierExpression("a"), "a"),
                        new LiteralNumericExpression(0.0)));

        testScript("[0].length = 0", new AssignmentExpression(new StaticMemberAssignmentTarget(
                new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)))), "length"),
                new LiteralNumericExpression(0.0)));

        testScript("([0].length) = 0", new AssignmentExpression(new StaticMemberAssignmentTarget(
                new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)))), "length"),
                new LiteralNumericExpression(0.0)));

        testScriptFailure("({a: (b = 0)} = {})", 14, "Invalid left-hand side in assignment");
        testScriptFailure("([(a = b)] = []", 11, "Invalid left-hand side in assignment");
        testScriptFailure("(({a})=0);", 6, "Invalid left-hand side in assignment");
        testScriptFailure("(([a])=0);", 6, "Invalid left-hand side in assignment");

    }
}
