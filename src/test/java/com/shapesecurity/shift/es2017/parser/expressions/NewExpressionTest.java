package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.NewExpression;
import com.shapesecurity.shift.es2017.ast.SpreadElement;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class NewExpressionTest extends ParserTestCase {
    @Test
    public void testNewExpression() throws JsError {
        testScript("new a(b,c)", new NewExpression(new IdentifierExpression("a"), ImmutableList.of(
                new IdentifierExpression("b"), new IdentifierExpression("c"))));

        testScript("new Button", new NewExpression(new IdentifierExpression("Button"), ImmutableList.empty()));

        testScript("new Button()", new NewExpression(new IdentifierExpression("Button"), ImmutableList.empty()));

        testScript("new Button(a)", new NewExpression(new IdentifierExpression("Button"), ImmutableList.of(
                new IdentifierExpression("a"))));

        testScript("new new foo", new NewExpression(new NewExpression(new IdentifierExpression("foo"),
                ImmutableList.empty()), ImmutableList.empty()));

        testScript("new new foo()", new NewExpression(new NewExpression(new IdentifierExpression("foo"),
                ImmutableList.empty()), ImmutableList.empty()));

        testScript("new f(...a)", new NewExpression(new IdentifierExpression("f"), ImmutableList.of(new SpreadElement(new IdentifierExpression("a")))));

        testScript("new f(...a = b)", new NewExpression(new IdentifierExpression("f"), ImmutableList.of(
                new SpreadElement(new AssignmentExpression(new AssignmentTargetIdentifier("a"), new IdentifierExpression("b"))))));

        testScript("new f(...a, ...b)", new NewExpression(new IdentifierExpression("f"), ImmutableList.of(
                new SpreadElement(new IdentifierExpression("a")), new SpreadElement(new IdentifierExpression("b")))));

        testScript("new f(a, ...b, c)", new NewExpression(new IdentifierExpression("f"), ImmutableList.of(
                new IdentifierExpression("a"), new SpreadElement(new IdentifierExpression("b")), new IdentifierExpression("c"))));

        testScript("new f(...a, b, ...c)", new NewExpression(new IdentifierExpression("f"), ImmutableList.of(
                new SpreadElement(new IdentifierExpression("a")), new IdentifierExpression("b"), new SpreadElement(
                        new IdentifierExpression("c")))));
    }
}
