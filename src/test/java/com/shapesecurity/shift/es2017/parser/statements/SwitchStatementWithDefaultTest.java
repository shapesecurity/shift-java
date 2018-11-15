package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.SwitchCase;
import com.shapesecurity.shift.es2017.ast.SwitchDefault;
import com.shapesecurity.shift.es2017.ast.SwitchStatementWithDefault;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class SwitchStatementWithDefaultTest extends ParserTestCase {
    @Test
    public void testSwitchStatementWithDefault() throws JsError {
        testScript("switch(a){case 1:default:case 2:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
                ImmutableList.of(new SwitchCase(new LiteralNumericExpression(1.0), ImmutableList.empty())),
                new SwitchDefault(ImmutableList.empty()), ImmutableList.of(new SwitchCase(new LiteralNumericExpression(2.0),
                ImmutableList.empty()))));

        testScript("switch(a){case 1:default:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
                ImmutableList.of(new SwitchCase(new LiteralNumericExpression(1.0), ImmutableList.empty())),
                new SwitchDefault(ImmutableList.empty()), ImmutableList.empty()));

        testScript("switch(a){default:case 2:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
                ImmutableList.empty(), new SwitchDefault(ImmutableList.empty()), ImmutableList.of(new SwitchCase(
                new LiteralNumericExpression(2.0), ImmutableList.empty()))));

        testScript("switch (answer) { case 0: hi(); break; default: break }", new SwitchStatementWithDefault(
                new IdentifierExpression("answer"), ImmutableList.of(new SwitchCase(new LiteralNumericExpression(0.0),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("hi"),
                        ImmutableList.empty())), new BreakStatement(Maybe.empty())))), new SwitchDefault(ImmutableList.of(
                new BreakStatement(Maybe.empty()))), ImmutableList.empty()));
    }
}
