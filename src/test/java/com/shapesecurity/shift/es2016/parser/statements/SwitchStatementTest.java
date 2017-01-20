package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.BreakStatement;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.SwitchCase;
import com.shapesecurity.shift.es2016.ast.SwitchStatement;
import com.shapesecurity.shift.es2016.ast.VariableDeclaration;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.VariableDeclarator;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

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
