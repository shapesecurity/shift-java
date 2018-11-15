package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.ComputedMemberExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ComputedMemberExpressionTest extends ParserTestCase {
    @Test
    public void testComputedMemberExpression() throws JsError {
        testScript("a[b, c]", new ComputedMemberExpression(new IdentifierExpression("a"), new BinaryExpression(
                new IdentifierExpression("b"), BinaryOperator.Sequence, new IdentifierExpression("c"))));

        testScript("a[b]", new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")));

        testScript("a[b] = b", new AssignmentExpression(new ComputedMemberAssignmentTarget(new IdentifierExpression("a"), new IdentifierExpression("b")), new IdentifierExpression("b")));

        testScript("(a[b]||(c[d]=e))", new BinaryExpression(new ComputedMemberExpression(
                new IdentifierExpression("a"), new IdentifierExpression("b")), BinaryOperator.LogicalOr, new AssignmentExpression(
                new ComputedMemberAssignmentTarget(new IdentifierExpression("c"), new IdentifierExpression("d")),
                new IdentifierExpression("e"))));

        testScript("a&&(b=c)&&(d=e)", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.LogicalAnd, new AssignmentExpression(new AssignmentTargetIdentifier("b"),
                new IdentifierExpression("c"))), BinaryOperator.LogicalAnd, new AssignmentExpression(new AssignmentTargetIdentifier("d"),
                new IdentifierExpression("e"))));
    }
}
