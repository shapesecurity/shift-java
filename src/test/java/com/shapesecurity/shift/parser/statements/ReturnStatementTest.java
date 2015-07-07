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
    testScript("(function(){ return })", new FunctionExpression(Maybe.nothing(), false, NO_PARAMETERS,
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())))));

    testScript("(function(){ return; })", new FunctionExpression(Maybe.nothing(), false, NO_PARAMETERS,
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())))));

    testScript("(function(){ return x; })", new FunctionExpression(Maybe.nothing(), false, NO_PARAMETERS,
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new IdentifierExpression("x")))))));

    testScript("(function(){ return x * y })", new FunctionExpression(Maybe.nothing(), false, NO_PARAMETERS,
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(
            Maybe.just(new BinaryExpression(BinaryOperator.Mul, new IdentifierExpression("x"), new IdentifierExpression("y")))
        )))));

    testScript("_ => { return 0; }", new ArrowExpression(new FormalParameters(ImmutableList.list(
        new BindingIdentifier("_")), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
        new ReturnStatement(Maybe.just(new LiteralNumericExpression(0.0)))))));

    testScriptFailure("return;", 0, "Illegal return statement");
    testScriptFailure("{ return; }", 2, "Illegal return statement");
    testScriptFailure("if (false) { return; }", 13, "Illegal return statement");
  }
}
