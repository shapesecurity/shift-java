package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

import com.shapesecurity.shift.ast.*;

public class ThisExpressionTest extends ParserTestCase {

  @Test
  public void testThisExpression() throws JsError {
    testScript("this;", new ThisExpression());
    testModule("this;", new ThisExpression());
  }
}
