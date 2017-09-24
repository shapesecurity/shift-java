package com.shapesecurity.shift.es2016.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import org.junit.Test;

public class AsyncFunctionDeclarationTest extends ParserTestCase {
    @Test
    public void testFunctionDeclaration() throws JsError {
        testScript("async function hello() { z(); }", new FunctionDeclaration(true, false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                        ImmutableList.empty()))))));

        testScript("async function eval() { }", new FunctionDeclaration(true, false, new BindingIdentifier("eval"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("async function arguments() { }", new FunctionDeclaration(true, false, new BindingIdentifier("arguments"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("async function test(t, t) { }", new FunctionDeclaration(true, false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("t"), new BindingIdentifier("t")),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("async function eval() { async function inner() { \"use strict\" } }", new FunctionDeclaration(
                true, false, new BindingIdentifier("eval"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(true, false, new BindingIdentifier("inner"),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(
                        new Directive("use strict")), ImmutableList.empty()))))));

        testScript("async function hello(a) { z(); }", new FunctionDeclaration(true, false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                ImmutableList.empty()))))));

        testScript("async function hello(a, b) { z(); }", new FunctionDeclaration(true, false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                new CallExpression(new IdentifierExpression("z"), ImmutableList.empty()))))));

        testScript("async function universe(__proto__) { }", new FunctionDeclaration(true, false, new BindingIdentifier("universe"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("__proto__")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("async function test() { \"use strict\"\n + 0; }", new FunctionDeclaration(true, false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new BinaryExpression(
                        new LiteralStringExpression("use strict"), BinaryOperator.Plus, new LiteralNumericExpression(0.0)))))));

        testScript("async function a() {} async function a() {}", new Script(ImmutableList.empty(), ImmutableList.of(
                new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new FunctionDeclaration(true, false,
                        new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("async function a() { function a() {} async function a() {} }", new FunctionDeclaration(true, false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("if (0) async function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new FunctionDeclaration(true, false,
                new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.empty()));

        testScript("if (0) async function a(){} else;", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.of(
                new EmptyStatement())));

        testScript("if (0); else async function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new EmptyStatement(),
                Maybe.of(new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("if (0) async function a(){} else async function b(){}", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.of(
                new FunctionDeclaration(true, false, new BindingIdentifier("b"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("try {} catch (e) { if(0) async function e(){} }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new IfStatement(
                        new LiteralNumericExpression(0.0), new FunctionDeclaration(true, false, new BindingIdentifier("e"),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                        ImmutableList.empty())), Maybe.empty()))))));
    }
}
