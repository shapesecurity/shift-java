package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class WhileStatementTest extends Assertions {
  @Test
  public void testWhileStatement() throws JsError {
    testScript("while(1);", new Script(ImmutableList.nil(), ImmutableList.list(
        new WhileStatement(new LiteralNumericExpression(1.0), new EmptyStatement())
    )));

    testScript("while(true);", new Script(ImmutableList.nil(), ImmutableList.list(
        new WhileStatement(new LiteralBooleanExpression(true), new EmptyStatement())
    )));

    testScript("while(true) doSomething()", new Script(ImmutableList.nil(), ImmutableList.list(
        new WhileStatement(new LiteralBooleanExpression(true), new ExpressionStatement(new CallExpression(
            new IdentifierExpression("doSomething"), ImmutableList.nil())
        ))
    )));
  }
}
