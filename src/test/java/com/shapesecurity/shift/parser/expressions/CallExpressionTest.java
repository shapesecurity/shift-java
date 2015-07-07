package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class CallExpressionTest extends ParserTestCase {
  @Test
  public void testCallExpression() throws JsError {
    testScript("a(b,c)", new CallExpression(new IdentifierExpression("a"), ImmutableList.list(new IdentifierExpression("b"), new IdentifierExpression("c"))));

    testScript("foo(bar, baz)", new CallExpression(new IdentifierExpression("foo"), ImmutableList.list(new IdentifierExpression("bar"), new IdentifierExpression("baz"))));

    testScript("(    foo  )()", new CallExpression(new IdentifierExpression("foo"), ImmutableList.nil()));

    testScript("f(...a)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(new SpreadElement(new IdentifierExpression("a")))));

    testScript("f(...a = b)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(new SpreadElement(
        new AssignmentExpression(new BindingIdentifier("a"), new IdentifierExpression("b"))))));

    testScript("f(...a, ...b)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(new SpreadElement(
        new IdentifierExpression("a")), new SpreadElement(new IdentifierExpression("b")))));

    testScript("f(a, ...b, c)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(
        new IdentifierExpression("a"), new SpreadElement(new IdentifierExpression("b")), new IdentifierExpression("c"))));

    testScript("f(...a, b, ...c)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(
        new SpreadElement(new IdentifierExpression("a")), new IdentifierExpression("b"), new SpreadElement(
            new IdentifierExpression("c")))));

    testScript("f(....0)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(new SpreadElement(
        new LiteralNumericExpression(0.0)))));

    testScript("f(.0)", new CallExpression(new IdentifierExpression("f"), ImmutableList.list(
        new LiteralNumericExpression(0.0))));

    testScriptFailure("f(..a)", 2, "Unexpected token \".\"");
    testScriptFailure("f(....a)", 5, "Unexpected token \".\"");
    testScriptFailure("f(... ... a)", 6, "Unexpected token \"...\"");

  }
}
