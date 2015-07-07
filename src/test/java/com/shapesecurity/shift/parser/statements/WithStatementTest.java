package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class WithStatementTest extends ParserTestCase {
  @Test
  public void testWithStatement() throws JsError {
    testScript("with(1);", new WithStatement(new LiteralNumericExpression(1.0), new EmptyStatement()));

    testScript("with (x) foo", new WithStatement(new IdentifierExpression("x"), new ExpressionStatement(
        new IdentifierExpression("foo")
    )));

    testScript("with (x) foo", new WithStatement(new IdentifierExpression("x"), new ExpressionStatement(
        new IdentifierExpression("foo")
    )));

    testScript("with (x) { foo }", new WithStatement(new IdentifierExpression("x"), new BlockStatement(
        new Block(ImmutableList.list(new ExpressionStatement(new IdentifierExpression("foo"))))
    )));
  }
}
