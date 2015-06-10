package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/10/15.
 */
public class BreakStatementTest extends Assertions {
  @Test
  public void testBreakStatement() throws JsError {
    testScript("while (true) { break }", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(
        new Block(ImmutableList.list(new BreakStatement(Maybe.nothing()))))));

    testScript("done: while (true) { break done }", new LabeledStatement("done", new WhileStatement(
        new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(
        Maybe.just("done"))))))));

    testScript("done: while (true) { break done; }", new LabeledStatement("done", new WhileStatement(
        new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(
        Maybe.just("done"))))))));

    testScript("__proto__: while (true) { break __proto__; }", new LabeledStatement("__proto__", new WhileStatement(
        new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(
        Maybe.just("__proto__"))))))));
  }
}
