package com.shapesecurity.shift.parser.expressions.literals;

import com.shapesecurity.shift.ast.LiteralStringExpression;
import com.shapesecurity.shift.ast.ThisExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserTestCase;
import org.junit.Test;

public class LiteralStringExpressionTest extends ParserTestCase {

  @Test
  public void testLiteralStringExpression() throws JsError {
    testScript("('x')", new LiteralStringExpression("x"));
    testScript("('\\\\\\'')", new LiteralStringExpression("\\'"));
    testScript("(\"x\")", new LiteralStringExpression("x"));
    // TODO: all of the other string tests
  }
}
