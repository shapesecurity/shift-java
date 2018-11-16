package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingPropertyIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingPropertyProperty;
import com.shapesecurity.shift.es2017.ast.BindingWithDefault;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.ObjectBinding;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.UnaryExpression;
import com.shapesecurity.shift.es2017.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class FunctionExpressionTest extends ParserTestCase {
    @Test
    public void testFunctionExpression() throws JsError {
        testScript("(function(){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function x() { y; z() });", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("x")),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new IdentifierExpression("y")), new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("z"), ImmutableList.empty()))))));

        testScript("(function eval() { });", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("eval")),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function arguments() { });", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("arguments")),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function x(y, z) { })", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("x")),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("y"), new BindingIdentifier("z")), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function(a = b){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"), new IdentifierExpression("b"))),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function(...a){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.empty(), Maybe.of(new BindingIdentifier("a"))), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function(a, ...b){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new BindingIdentifier("a")), Maybe.of(new BindingIdentifier("b"))), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a}){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"),
                        Maybe.empty())))), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a: x, a: y}){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"),
                        new BindingIdentifier("x")), new BindingPropertyProperty(new StaticPropertyName("a"),
                        new BindingIdentifier("y"))))), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function([a]){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty())),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("(function({a = 0}){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("label: !function(){ label:; };", new LabeledStatement("label", new ExpressionStatement(
                new UnaryExpression(UnaryOperator.LogicalNot, new FunctionExpression(false, false, Maybe.empty(),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                        ImmutableList.of(new LabeledStatement("label", new EmptyStatement()))))))));

        testScript("(function([]){})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.of(new ArrayBinding(ImmutableList.empty(), Maybe.empty())), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* g(){ (function yield(){}); }", new FunctionDeclaration(false, true, new BindingIdentifier("g"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("yield")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))
        ))));

        testScript("(function*(){ (function yield(){}); });", new FunctionExpression(false, true, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("yield")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))
        ))));

        testScriptFailure("(function(...a, b){})", 14, "Unexpected token \",\"");
        testScriptFailure("(function((a)){})", 10, "Unexpected token \"(\"");

    }
}
