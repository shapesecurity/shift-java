package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.ast.ArrowExpression;
import com.shapesecurity.shift.es2016.ast.BinaryExpression;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.FunctionExpression;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.ReturnStatement;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class ReturnStatementTest extends ParserTestCase {

    @Test
    public void testReturnStatement() throws JsError {
        testScript("(function(){ return })", new FunctionExpression(false, Maybe.empty(), NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))));

        testScript("(function(){ return; })", new FunctionExpression(false, Maybe.empty(), NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))));

        testScript("(function(){ return x; })", new FunctionExpression(false, Maybe.empty(), NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(new IdentifierExpression("x")))))));

        testScript("(function(){ return x * y })", new FunctionExpression(false, Maybe.empty(), NO_PARAMETERS,
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Mul, new IdentifierExpression("y")))
                )))));

        testScript("_ => { return 0; }", new ArrowExpression(new FormalParameters(ImmutableList.of(
                new BindingIdentifier("_")), Maybe.empty(), false), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScriptFailure("return;", 0, "Illegal return statement");
        testScriptFailure("{ return; }", 2, "Illegal return statement");
        testScriptFailure("if (false) { return; }", 13, "Illegal return statement");
    }
}
