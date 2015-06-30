package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class FunctionExpressionTest extends Assertions {
  @Test
  public void testFunctionExpression() throws JsError {
    testScript("(function(){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(ImmutableList.nil(),
        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function x() { y; z() });", new FunctionExpression(Maybe.just(new BindingIdentifier("x")), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new IdentifierExpression("y")), new ExpressionStatement(
            new CallExpression(new IdentifierExpression("z"), ImmutableList.nil()))))));

    testScript("(function eval() { });", new FunctionExpression(Maybe.just(new BindingIdentifier("eval")), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("(function arguments() { });", new FunctionExpression(Maybe.just(new BindingIdentifier("arguments")),
        false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("(function x(y, z) { })", new FunctionExpression(Maybe.just(new BindingIdentifier("x")), false,
        new FormalParameters(ImmutableList.list(new BindingIdentifier("y"), new BindingIdentifier("z")), Maybe.nothing()),
        new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function(a = b){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"), new IdentifierExpression("b"))),
        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function(...a){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.nil(), Maybe.just(new BindingIdentifier("a"))), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("(function(a, ...b){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new BindingIdentifier("a")), Maybe.just(new BindingIdentifier("b"))), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function({a}){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("a"),
            Maybe.nothing())))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function({a: x, a: y}){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(new StaticPropertyName("a"),
            new BindingIdentifier("x")), new BindingPropertyProperty(new StaticPropertyName("a"),
            new BindingIdentifier("y"))))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("(function([a]){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing())),
        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("(function({a = 0}){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("a"),
            Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("label: !function(){ label:; };", new LabeledStatement("label", new ExpressionStatement(
        new UnaryExpression(UnaryOperator.LogicalNot, new FunctionExpression(Maybe.nothing(), false,
            new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
            ImmutableList.list(new LabeledStatement("label", new EmptyStatement()))))))));

    testScript("(function([]){})", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(
        ImmutableList.list(new ArrayBinding(ImmutableList.nil(), Maybe.nothing())), Maybe.nothing()), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil())));

    testScriptFailure("(function(...a, b){})", 14, "Unexpected token \",\"");
    testScriptFailure("(function((a)){})", 10, "Unexpected token \"(\"");
  }
}
