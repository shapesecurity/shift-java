package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.ArrayExpression;
import com.shapesecurity.shift.ast.LiteralNumericExpression;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/11/15.
 */
public class ArrayExpressionTest extends Assertions {
  @Test
  public void testArrayExpression() throws JsError {
    testScript("[]", new ArrayExpression(ImmutableList.nil()));

    testScript("[ ]", new ArrayExpression(ImmutableList.nil()));

    testScript("[ 0 ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)))));

    testScript("[ 0, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)))));

    testScript("[ ,, 0 ]", new ArrayExpression(ImmutableList.list(Maybe.nothing(), Maybe.nothing(),
        Maybe.just(new LiteralNumericExpression(0.0)))));

    testScript("[ 1, 2, 3, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(1.0)),
        Maybe.just(new LiteralNumericExpression(2.0)), Maybe.just(new LiteralNumericExpression(3.0)))));

    testScript("[ 1, 2,, 3, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(1.0)),
        Maybe.just(new LiteralNumericExpression(2.0)), Maybe.nothing(), Maybe.just(new LiteralNumericExpression(3.0)))));

    testScript("[,,1,,,2,3,,]", new ArrayExpression(ImmutableList.list(Maybe.nothing(), Maybe.nothing(),
        Maybe.just(new LiteralNumericExpression(1.0)), Maybe.nothing(), Maybe.nothing(),
        Maybe.just(new LiteralNumericExpression(2.0)), Maybe.just(new LiteralNumericExpression(3.0)), Maybe.nothing())));

      testScriptFailure("[a, ...(b=c)] = 0", 0, "");
      testScriptFailure("[0] = 0", 0, "");

  }
}
