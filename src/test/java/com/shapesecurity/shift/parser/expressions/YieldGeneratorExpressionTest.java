package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class YieldGeneratorExpressionTest extends ParserTestCase {
    @Test
    public void testYieldGeneratorExpression() throws JsError {
        testScript("function*a(){yield*a}", new FunctionDeclaration(true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))))));

        testScript("function a(){yield*a}", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new BinaryExpression(
                        new IdentifierExpression("yield"), BinaryOperator.Mul, new IdentifierExpression("a")))))));

        testScriptFailure("function *a(){yield\n*a}", 2, 0, 20, "Unexpected token \"*\"");
        testScriptFailure("function *a(){yield*}", 20, "Unexpected token \"}\"");
    }
}
