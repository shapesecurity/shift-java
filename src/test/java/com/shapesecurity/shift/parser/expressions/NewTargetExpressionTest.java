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
        testScript("function f() { new.target; }", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f(a = new.target){}", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())));

        testScript("(function f(a = new.target){})", new FunctionExpression(false, Maybe.just(new BindingIdentifier("f")),
                new FormalParameters(ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())));

        testScript("({ set m(a = new.target){} })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("m"),
                new BindingWithDefault(new BindingIdentifier("a"), new NewTargetExpression()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ m(a = new.target){} })", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("m"),
                new FormalParameters(ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())))));

        testScript("({ get m(){ new.target } })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("m"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new NewTargetExpression())))
                ))));

        testScript("function f() { new.\\u0074arget; }", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f() { new new.target; }", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new NewExpression(new NewTargetExpression(), ImmutableList.nil()))))));

        testScript("function f() { new.target(); }", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new NewTargetExpression(),
                        ImmutableList.nil()))))));

        testScript("function f() { new[\"target\"]; }", new FunctionDeclaration(false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new NewExpression(new ArrayExpression(ImmutableList.list(
                        Maybe.just(new LiteralStringExpression("target")))), ImmutableList.nil()))))));

        testScriptFailure("function f() { new.anythingElse; }", 31, "Unexpected identifier");
        testScriptFailure("function f() { new..target; }", 19, "Unexpected token \".\"");
    }
}
