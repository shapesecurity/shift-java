package com.shapesecurity.shift.parser.expressions.literals;

import com.shapesecurity.shift.ast.LiteralInfinityExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserTestCase;
import org.junit.Test;

public class LiteralInfinityExpressionTest extends ParserTestCase {
  @Test
  public void testLiteralInfinityExpressionTest() throws JsError {
    testScript("2e308", new LiteralInfinityExpression());
  }
}
