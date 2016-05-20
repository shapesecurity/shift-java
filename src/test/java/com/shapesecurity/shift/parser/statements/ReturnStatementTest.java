package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ReturnStatementTest extends ParserTestCase {

    @Test
    public void testReturnStatement() throws JsError {
        testScript("(function(){ return })", new FunctionExpression(Maybe.empty(), false, NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))));

        testScript("(function(){ return; })", new FunctionExpression(Maybe.empty(), false, NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))));

        testScript("(function(){ return x; })", new FunctionExpression(Maybe.empty(), false, NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(new IdentifierExpression("x")))))));

        testScript("(function(){ return x * y })", new FunctionExpression(Maybe.empty(), false, NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new BinaryExpression(BinaryOperator.Mul, new IdentifierExpression("x"), new IdentifierExpression("y")))
                )))));

        testScript("_ => { return 0; }", new ArrowExpression(new FormalParameters(ImmutableList.of(
                new BindingIdentifier("_")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScriptFailure("return;", 0, "Illegal return statement");
        testScriptFailure("{ return; }", 2, "Illegal return statement");
        testScriptFailure("if (false) { return; }", 13, "Illegal return statement");
    }
}
