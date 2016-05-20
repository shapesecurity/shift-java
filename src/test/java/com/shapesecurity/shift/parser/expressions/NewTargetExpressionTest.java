package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class NewTargetExpressionTest extends ParserTestCase {
    @Test
    public void testNewTargetExpression() throws JsError {
        testScript("function f() { new.target; }", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f(a = new.target){}", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function f(a = new.target){})", new FunctionExpression(Maybe.of(new BindingIdentifier("f")), false,
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("({ set m(a = new.target){} })", new ObjectExpression(ImmutableList.of(new Setter(
                new BindingWithDefault(new BindingIdentifier("a"), new NewTargetExpression()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("m")))));

        testScript("({ m(a = new.target){} })", new ObjectExpression(ImmutableList.of(new Method(false,
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()), new StaticPropertyName("m")))));

        testScript("({ get m(){ new.target } })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new NewTargetExpression()))),
                new StaticPropertyName("m")))));

        testScript("function f() { new.\\u0074arget; }", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f() { new new.target; }", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewExpression(new NewTargetExpression(), ImmutableList.empty()))))));

        testScript("function f() { new.target(); }", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new NewTargetExpression(),
                        ImmutableList.empty()))))));

        testScript("function f() { new[\"target\"]; }", new FunctionDeclaration(new BindingIdentifier("f"), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewExpression(new ArrayExpression(ImmutableList.of(
                        Maybe.of(new LiteralStringExpression("target")))), ImmutableList.empty()))))));

        testScriptFailure("function f() { new.anythingElse; }", 31, "Unexpected identifier");
        testScriptFailure("function f() { new..target; }", 19, "Unexpected token \".\"");
    }
}
