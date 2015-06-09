package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;

public class ThisExpressionTest extends Assertions {

  @Test
  public void testThisExpression() throws JsError {
    testScript("this;", new ThisExpression());
    testModule("this;", new ThisExpression());
  }
}
