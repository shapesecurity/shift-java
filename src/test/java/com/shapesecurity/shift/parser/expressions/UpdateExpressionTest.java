package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.UpdateExpression;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class UpdateExpressionTest extends Assertions {
  @Test
  public void testUpdateExpression() throws JsError {
    testScript("++a", new UpdateExpression(true, UpdateOperator.Increment, new BindingIdentifier("a")));
    testScript("--a", new UpdateExpression(true, UpdateOperator.Decrement, new BindingIdentifier("a")));

    testScript("x++", new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("x")));
    testScript("x--", new UpdateExpression(false, UpdateOperator.Decrement, new BindingIdentifier("x")));

    testScript("eval++", new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("eval")));
    testScript("eval--", new UpdateExpression(false, UpdateOperator.Decrement, new BindingIdentifier("eval")));

    testScript("arguments++", new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("arguments")));
    testScript("arguments--", new UpdateExpression(false, UpdateOperator.Decrement, new BindingIdentifier("arguments")));
  }
}
