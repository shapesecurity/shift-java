package com.shapesecurity.shift.es2016.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class FunctionDeclarationTest extends ParserTestCase {
    @Test
    public void testFunctionDeclaration() throws JsError {
        testScript("function hello() { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                        ImmutableList.empty()))))));

        testScript("function eval() { }", new FunctionDeclaration(false, new BindingIdentifier("eval"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("function arguments() { }", new FunctionDeclaration(false, new BindingIdentifier("arguments"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())));

        testScript("function test(t, t) { }", new FunctionDeclaration(false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("t"), new BindingIdentifier("t")),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function eval() { function inner() { \"use strict\" } }", new FunctionDeclaration(
                false, new BindingIdentifier("eval"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, new BindingIdentifier("inner"),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(
                        new Directive("use strict")), ImmutableList.empty()))))));

        testScript("function hello(a) { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                ImmutableList.empty()))))));

        testScript("function hello(a, b) { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                new CallExpression(new IdentifierExpression("z"), ImmutableList.empty()))))));

        testScript("function universe(__proto__) { }", new FunctionDeclaration(false, new BindingIdentifier("universe"),
                new FormalParameters(ImmutableList.of(new BindingIdentifier("__proto__")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())));

        testScript("function test() { \"use strict\"\n + 0; }", new FunctionDeclaration(false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new BinaryExpression(
                        new LiteralStringExpression("use strict"), BinaryOperator.Plus, new LiteralNumericExpression(0.0)))))));

        testScript("function a() {} function a() {}", new Script(ImmutableList.empty(), ImmutableList.of(
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new FunctionDeclaration(false,
                        new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("function a() { function a() {} function a() {} }", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("a: function a(){}", new LabeledStatement("a", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testScript("if (0) function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new FunctionDeclaration(false,
                new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.empty()));

        testScript("if (0) function a(){} else;", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.of(
                new EmptyStatement())));

        testScript("if (0); else function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new EmptyStatement(),
                Maybe.of(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("if (0) function a(){} else function b(){}", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), Maybe.of(
                new FunctionDeclaration(false, new BindingIdentifier("b"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("try {} catch (e) { if(0) function e(){} }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new IfStatement(
                        new LiteralNumericExpression(0.0), new FunctionDeclaration(false, new BindingIdentifier("e"),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                        ImmutableList.empty())), Maybe.empty()))))));

        testScriptFailure("a: function* a(){}", 11, "Unexpected token \"*\"");
        testScriptFailure("for(;;) function a(){}", 8, "Unexpected token \"function\"");
        testScriptFailure("for(a in b) function c(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("for(a of b) function c(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("while(true) function a(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("with(true) function a(){}", 11, "Unexpected token \"function\"");
    }
}
