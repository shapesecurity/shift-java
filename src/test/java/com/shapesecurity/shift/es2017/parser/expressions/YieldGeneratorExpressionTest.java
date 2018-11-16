package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.YieldGeneratorExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class YieldGeneratorExpressionTest extends ParserTestCase {
    @Test
    public void testYieldGeneratorExpression() throws JsError {
        testScript("function*a(){yield*a}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))))));

        testScript("function a(){yield*a}", new FunctionDeclaration(false, false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new BinaryExpression(
                        new IdentifierExpression("yield"), BinaryOperator.Mul, new IdentifierExpression("a")))))));

        testScriptFailure("function *a(){yield\n*a}", 2, 0, 20, "Unexpected token \"*\"");
        testScriptFailure("function *a(){yield*}", 20, "Unexpected token \"}\"");
    }
}
