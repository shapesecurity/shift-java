package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class YieldGeneratorExpressionTest extends Assertions {
  @Test
  public void testYieldGeneratorExpression() throws JsError {
    testScript("function*a(){yield*a}", new FunctionDeclaration(new BindingIdentifier("a"),
        true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))))));

    testScript("function a(){yield*a}", new FunctionDeclaration(new BindingIdentifier("a"),
        false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new BinaryExpression(BinaryOperator.Mul,
            new IdentifierExpression("yield"), new IdentifierExpression("a")))))));

    testScriptFailure("function *a(){yield\n*a}", 2, 0, 20, "Unexpected token \"*\"");
    testScriptFailure("function *a(){yield*}", 20, "Unexpected token \"}\"");
  }
}
