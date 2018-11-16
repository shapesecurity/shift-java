package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingWithDefault;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.Getter;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.NewExpression;
import com.shapesecurity.shift.es2017.ast.NewTargetExpression;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.Setter;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class NewTargetExpressionTest extends ParserTestCase {
    @Test
    public void testNewTargetExpression() throws JsError {
        testScript("function f() { new.target; }", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f(a = new.target){}", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("(function f(a = new.target){})", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("f")),
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("({ set m(a = new.target){} })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("m"),
                new BindingWithDefault(new BindingIdentifier("a"), new NewTargetExpression()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ m(a = new.target){} })", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("m"),
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                        new NewTargetExpression())), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())))));

        testScript("({ get m(){ new.target } })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("m"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new NewTargetExpression())))
                ))));

        testScript("function f() { new.\\u0074arget; }", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewTargetExpression())))));

        testScript("function f() { new new.target; }", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewExpression(new NewTargetExpression(), ImmutableList.empty()))))));

        testScript("function f() { new.target(); }", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new NewTargetExpression(),
                        ImmutableList.empty()))))));

        testScript("function f() { new[\"target\"]; }", new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new NewExpression(new ArrayExpression(ImmutableList.of(
                        Maybe.of(new LiteralStringExpression("target")))), ImmutableList.empty()))))));

        testScriptFailure("function f() { new.anythingElse; }", 31, "Unexpected identifier");
        testScriptFailure("function f() { new..target; }", 19, "Unexpected token \".\"");
    }
}
