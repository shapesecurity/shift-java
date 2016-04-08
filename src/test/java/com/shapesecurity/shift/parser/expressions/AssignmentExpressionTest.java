package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class AssignmentExpressionTest extends ParserTestCase {
    @Test
    public void testAssignmentExpression() throws JsError {
        testScript("a=0;", new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("(a)=(0);", new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("x = 0", new AssignmentExpression(new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("eval = 0", new AssignmentExpression(new BindingIdentifier("eval"), new LiteralNumericExpression(0.0)));

        testScript("arguments = 0", new AssignmentExpression(new BindingIdentifier("arguments"),
                new LiteralNumericExpression(0.0)));

        testScript("x *= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignMul,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x.x *= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignMul,
                new StaticMemberAssignmentTarget("x", new IdentifierExpression("x")), new LiteralNumericExpression(0.0)));

        testScript("x /= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignDiv,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x %= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignRem,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x += 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignPlus,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x -= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignMinus,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x <<= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignLeftShift,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x >>= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignRightShift,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x >>>= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignUnsignedRightShift,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x &= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignBitAnd,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x ^= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignBitXor,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x |= 0", new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignBitOr,
                new BindingIdentifier("x"), new LiteralNumericExpression(0.0)));

        testScript("x = (y += 0)", new AssignmentExpression(new BindingIdentifier("x"),
                new CompoundAssignmentExpression(CompoundAssignmentOperator.AssignPlus,
                        new BindingIdentifier("y"), new LiteralNumericExpression(0.0))));

        testScript("'use strict'; eval[0] = 0", new AssignmentExpression(new ComputedMemberAssignmentTarget(
                new LiteralNumericExpression(0.0), new IdentifierExpression("eval")), new LiteralNumericExpression(0.0)));

        testScript("'use strict'; arguments[0] = 0", new AssignmentExpression(new ComputedMemberAssignmentTarget(
                new LiteralNumericExpression(0.0), new IdentifierExpression("arguments")), new LiteralNumericExpression(0.0)));

        testScript("((((((((((((((((((((((((((((((((((((((((a)))))))))))))))))))))))))))))))))))))))) = 0",
                new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)));

        testScript("((((((((((((((((((((((((((((((((((((((((a.a)))))))))))))))))))))))))))))))))))))))) = 0",
                new AssignmentExpression(new StaticMemberAssignmentTarget("a", new IdentifierExpression("a")),
                        new LiteralNumericExpression(0.0)));

        testScript("[0].length = 0", new AssignmentExpression(new StaticMemberAssignmentTarget("length",
                new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0))))),
                new LiteralNumericExpression(0.0)));

        testScript("([0].length) = 0", new AssignmentExpression(new StaticMemberAssignmentTarget("length",
                new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0))))),
                new LiteralNumericExpression(0.0)));
    }
}
