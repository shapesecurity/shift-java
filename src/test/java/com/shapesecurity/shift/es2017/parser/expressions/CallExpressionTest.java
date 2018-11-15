package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.SpreadElement;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class CallExpressionTest extends ParserTestCase {
    @Test
    public void testCallExpression() throws JsError {
        testScript("a(b,c)", new CallExpression(new IdentifierExpression("a"), ImmutableList.of(new IdentifierExpression("b"), new IdentifierExpression("c"))));

        testScript("foo(bar, baz)", new CallExpression(new IdentifierExpression("foo"), ImmutableList.of(new IdentifierExpression("bar"), new IdentifierExpression("baz"))));

        testScript("(    foo  )()", new CallExpression(new IdentifierExpression("foo"), ImmutableList.empty()));

        testScript("f(...a)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(new SpreadElement(new IdentifierExpression("a")))));

        testScript("f(...a = b)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(new SpreadElement(
                new AssignmentExpression(new AssignmentTargetIdentifier("a"), new IdentifierExpression("b"))))));

        testScript("f(...a, ...b)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(new SpreadElement(
                new IdentifierExpression("a")), new SpreadElement(new IdentifierExpression("b")))));

        testScript("f(a, ...b, c)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(
                new IdentifierExpression("a"), new SpreadElement(new IdentifierExpression("b")), new IdentifierExpression("c"))));

        testScript("f(...a, b, ...c)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(
                new SpreadElement(new IdentifierExpression("a")), new IdentifierExpression("b"), new SpreadElement(
                        new IdentifierExpression("c")))));

        testScript("f(....0)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(new SpreadElement(
                new LiteralNumericExpression(0.0)))));

        testScript("f(.0)", new CallExpression(new IdentifierExpression("f"), ImmutableList.of(
                new LiteralNumericExpression(0.0))));

        testScriptFailure("f(..a)", 2, "Unexpected token \".\"");
        testScriptFailure("f(....a)", 5, "Unexpected token \".\"");
        testScriptFailure("f(... ... a)", 6, "Unexpected token \"...\"");

    }
}
