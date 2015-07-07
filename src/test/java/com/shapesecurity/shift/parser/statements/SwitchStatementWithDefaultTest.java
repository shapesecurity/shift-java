package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class SwitchStatementWithDefaultTest extends ParserTestCase {
  @Test
  public void testSwitchStatementWithDefault() throws JsError {
    testScript("switch(a){case 1:default:case 2:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
            ImmutableList.list(new SwitchCase(new LiteralNumericExpression(1.0), ImmutableList.nil())),
            new SwitchDefault(ImmutableList.nil()), ImmutableList.list(new SwitchCase(new LiteralNumericExpression(2.0),
            ImmutableList.nil()))));

    testScript("switch(a){case 1:default:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
            ImmutableList.list(new SwitchCase(new LiteralNumericExpression(1.0), ImmutableList.nil())),
            new SwitchDefault(ImmutableList.nil()), ImmutableList.nil()));

    testScript("switch(a){default:case 2:}", new SwitchStatementWithDefault(new IdentifierExpression("a"),
        ImmutableList.nil(), new SwitchDefault(ImmutableList.nil()), ImmutableList.list(new SwitchCase(
        new LiteralNumericExpression(2.0), ImmutableList.nil()))));

    testScript("switch (answer) { case 0: hi(); break; default: break }", new SwitchStatementWithDefault(
            new IdentifierExpression("answer"), ImmutableList.list(new SwitchCase(new LiteralNumericExpression(0.0),
            ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("hi"),
                ImmutableList.nil())), new BreakStatement(Maybe.nothing())))), new SwitchDefault(ImmutableList.list(
            new BreakStatement(Maybe.nothing()))), ImmutableList.nil()));
  }
}
