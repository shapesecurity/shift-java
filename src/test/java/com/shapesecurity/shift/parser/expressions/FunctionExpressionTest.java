package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class FunctionExpressionTest extends ParserTestCase {
    @Test
    public void testFunctionExpression() throws JsError {
        testScript("(function(){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(ImmutableList.empty(),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function x() { y; z() });", new FunctionExpression(Maybe.of(new BindingIdentifier("x")), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new IdentifierExpression("y")), new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("z"), ImmutableList.empty()))))));

        testScript("(function eval() { });", new FunctionExpression(Maybe.of(new BindingIdentifier("eval")), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function arguments() { });", new FunctionExpression(Maybe.of(new BindingIdentifier("arguments")),
                false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function x(y, z) { })", new FunctionExpression(Maybe.of(new BindingIdentifier("x")), false,
                new FormalParameters(ImmutableList.of(new BindingIdentifier("y"), new BindingIdentifier("z")), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function(a = b){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"), new IdentifierExpression("b"))),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function(...a){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.empty(), Maybe.of(new BindingIdentifier("a"))), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function(a, ...b){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new BindingIdentifier("a")), Maybe.of(new BindingIdentifier("b"))), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a}){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"),
                        Maybe.empty())))), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a: x, a: y}){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"),
                        new BindingIdentifier("x")), new BindingPropertyProperty(new StaticPropertyName("a"),
                        new BindingIdentifier("y"))))), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function([a]){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty())),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a = 0}){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("label: !function(){ label:; };", new LabeledStatement("label", new ExpressionStatement(
                new UnaryExpression(UnaryOperator.LogicalNot, new FunctionExpression(Maybe.empty(), false,
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                        ImmutableList.of(new LabeledStatement("label", new EmptyStatement()))))))));

        testScript("(function([]){})", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.of(new ArrayBinding(ImmutableList.empty(), Maybe.empty())), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* g(){ (function yield(){}); }", new FunctionDeclaration(new BindingIdentifier("g"), true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new FunctionExpression(Maybe.of(new BindingIdentifier("yield")), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))
        ))));

        testScript("(function*(){ (function yield(){}); });", new FunctionExpression(Maybe.empty(), true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new FunctionExpression(Maybe.of(new BindingIdentifier("yield")), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))
        ))));

        testScriptFailure("(function(...a, b){})", 14, "Unexpected token \",\"");
        testScriptFailure("(function((a)){})", 10, "Unexpected token \"(\"");

    }
}
