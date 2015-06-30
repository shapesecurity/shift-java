package com.shapesecurity.shift.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class FunctionDeclarationTest extends Assertions {
  @Test
  public void testFunctionDeclaration() throws JsError {
    testScript("function hello() { z(); }", new FunctionDeclaration(new BindingIdentifier("hello"), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
            ImmutableList.nil()))))));

    testScript("function eval() { }", new FunctionDeclaration(new BindingIdentifier("eval"), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("function arguments() { }", new FunctionDeclaration(new BindingIdentifier("arguments"), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil())));

    testScript("function test(t, t) { }", new FunctionDeclaration(new BindingIdentifier("test"), false,
        new FormalParameters(ImmutableList.list(new BindingIdentifier("t"), new BindingIdentifier("t")),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

    testScript("function eval() { function inner() { \"use strict\" } }", new FunctionDeclaration(
        new BindingIdentifier("eval"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("inner"),
            false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(
            new Directive("use strict")), ImmutableList.nil()))))));

    testScript("function hello(a) { z(); }", new FunctionDeclaration(new BindingIdentifier("hello"), false,
        new FormalParameters(ImmutableList.list(new BindingIdentifier("a")), Maybe.nothing()), new FunctionBody(
        ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"),
        ImmutableList.nil()))))));

    testScript("function hello(a, b) { z(); }", new FunctionDeclaration(new BindingIdentifier("hello"), false,
        new FormalParameters(ImmutableList.list(new BindingIdentifier("a"), new BindingIdentifier("b")),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
        new CallExpression(new IdentifierExpression("z"), ImmutableList.nil()))))));

    testScript("function universe(__proto__) { }", new FunctionDeclaration(new BindingIdentifier("universe"), false,
        new FormalParameters(ImmutableList.list(new BindingIdentifier("__proto__")), Maybe.nothing()), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil())));

    testScript("function test() { \"use strict\"\n + 0; }", new FunctionDeclaration(new BindingIdentifier("test"), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new BinaryExpression(BinaryOperator.Plus,
            new LiteralStringExpression("\"use strict\""), new LiteralNumericExpression(0.0)))))));

    testScript("function a() {} function a() {}", new Script(ImmutableList.nil(), ImmutableList.list(
        new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), new FunctionDeclaration(
            new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
            new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

    testScript("function a() { function a() {} function a() {} }", new FunctionDeclaration(new BindingIdentifier("a"),
        false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(
            ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())),
            new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
                Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

    testScript("a: function a(){}", new LabeledStatement("a", new FunctionDeclaration(new BindingIdentifier("a"), false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil()))));

    testScript("if (0) function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new FunctionDeclaration(
        new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
        new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.nothing()));

    testScript("if (0) function a(){} else;", new IfStatement(new LiteralNumericExpression(0.0),
        new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.just(
        new EmptyStatement())));

    testScript("if (0); else function a(){}", new IfStatement(new LiteralNumericExpression(0.0), new EmptyStatement(),
        Maybe.just(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

    testScript("if (0) function a(){} else function b(){}", new IfStatement(new LiteralNumericExpression(0.0),
        new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), Maybe.just(
        new FunctionDeclaration(new BindingIdentifier("b"), false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

    testScript("try {} catch (e) { if(0) function e(){} }", new TryCatchStatement(new Block(ImmutableList.nil()),
        new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new IfStatement(
            new LiteralNumericExpression(0.0), new FunctionDeclaration(new BindingIdentifier("e"), false,
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
