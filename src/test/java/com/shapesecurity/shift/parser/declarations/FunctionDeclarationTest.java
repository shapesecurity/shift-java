package com.shapesecurity.shift.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class FunctionDeclarationTest extends ParserTestCase {
    @Test
    public void testFunctionDeclaration() throws JsError {
        testScript("function hello() { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                        ImmutableList.nil()))))));

        testScript("function eval() { }", new FunctionDeclaration(false, new BindingIdentifier("eval"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())));

        testScript("function arguments() { }", new FunctionDeclaration(false, new BindingIdentifier("arguments"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())));

        testScript("function test(t, t) { }", new FunctionDeclaration(false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("t"), Maybe.nothing()), new Parameter(new BindingIdentifier("t"), Maybe.nothing())),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

        testScript("function eval() { function inner() { \"use strict\" } }", new FunctionDeclaration(
                false, new BindingIdentifier("eval"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("inner"),
                        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(
                        new Directive("use strict")), ImmutableList.nil()))))));

        testScript("function hello(a) { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("a"), Maybe.nothing())), Maybe.nothing()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
                ImmutableList.nil()))))));

        testScript("function hello(a, b) { z(); }", new FunctionDeclaration(false, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("a"), Maybe.nothing()), new Parameter(new BindingIdentifier("b"), Maybe.nothing())),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                new CallExpression(new IdentifierExpression("z"), ImmutableList.nil()))))));

        testScript("function universe(__proto__) { }", new FunctionDeclaration(false, new BindingIdentifier("universe"),
                new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("__proto__"), Maybe.nothing())), Maybe.nothing()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())));

        testScript("function test() { \"use strict\"\n + 0; }", new FunctionDeclaration(false, new BindingIdentifier("test"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new BinaryExpression(
                        new LiteralStringExpression("use strict"), BinaryOperator.Plus, new LiteralNumericExpression(0.0)))))));

        testScript("function a() {} function a() {}", new Script(ImmutableList.nil(), ImmutableList.list(
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), new FunctionDeclaration(false,
                        new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("function a() { function a() {} function a() {} }", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())),
                        new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(),
                                Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("a: function a(){}", new LabeledStatement("a", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil()))));

        testScript("if (0) function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new FunctionDeclaration(false,
                new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.nothing()));

        testScript("if (0) function a(){} else;", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.just(
                new EmptyStatement())));

        testScript("if (0); else function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new EmptyStatement(),
                Maybe.just(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("if (0) function a(){} else function b(){}", new IfStatement(new LiteralNumericExpression(0.0),
                new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.just(
                new FunctionDeclaration(false, new BindingIdentifier("b"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("try {} catch (e) { if(0) function e(){} }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new IfStatement(
                        new LiteralNumericExpression(0.0), new FunctionDeclaration(false, new BindingIdentifier("e"),
                        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                        ImmutableList.nil())), Maybe.nothing()))))));

        testScriptFailure("a: function* a(){}", 11, "Unexpected token \"*\"");
        testScriptFailure("for(;;) function a(){}", 8, "Unexpected token \"function\"");
        testScriptFailure("for(a in b) function c(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("for(a of b) function c(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("while(true) function a(){}", 12, "Unexpected token \"function\"");
        testScriptFailure("with(true) function a(){}", 11, "Unexpected token \"function\"");
    }
}
