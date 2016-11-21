package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class SwitchStatementTest extends ParserTestCase {
    @Test
    public void testSwitchStatement() throws JsError {
        testScript("switch (x) {}", new SwitchStatement(new IdentifierExpression("x"), ImmutableList.empty()));

        testScript("switch(a){case 1:}", new SwitchStatement(new IdentifierExpression("a"), ImmutableList.of(
                new SwitchCase(new LiteralNumericExpression(1.0), ImmutableList.empty()))));

        testScript("switch (answer) { case 0: hi(); break; }", new SwitchStatement(new IdentifierExpression("answer"),
                ImmutableList.of(new SwitchCase(new LiteralNumericExpression(0.0), ImmutableList.of(new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("hi"), ImmutableList.empty())), new BreakStatement(Maybe.empty()))))));

        testScript("switch (answer) { case 0: let a; }", new SwitchStatement(new IdentifierExpression("answer"),
                ImmutableList.of(new SwitchCase(new LiteralNumericExpression(0.0), ImmutableList.of(
                        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                                new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))))))));
    }
}
