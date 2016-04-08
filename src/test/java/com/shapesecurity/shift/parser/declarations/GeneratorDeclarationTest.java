package com.shapesecurity.shift.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class GeneratorDeclarationTest extends ParserTestCase {
    @Test
    public void testGeneratorDeclarationTest() throws JsError {
        testScript("function* a(){}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

        testScript("function* a(){yield}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                new ExpressionStatement(new YieldExpression(Maybe.nothing()))))));

        testScript("function* a(){yield a}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                new ExpressionStatement(new YieldExpression(Maybe.just(new IdentifierExpression("a"))))))));

        testScript("function* yield(){}", new FunctionDeclaration(new BindingIdentifier("yield"), true, new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

        testScript("function* a(a=yield){}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.list(new Parameter(new BindingIdentifier("a"), Maybe.just(new YieldExpression(Maybe.nothing())))),
                Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

        testScript("function* a({[yield]:a}){}", new FunctionDeclaration(new BindingIdentifier("a"), true,
                new FormalParameters(ImmutableList.list(new Parameter(new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(
                        new ComputedPropertyName(new YieldExpression(Maybe.nothing())), new BindingIdentifier("a")))), Maybe.nothing())),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));

        testScript("function* a(){({[yield]:a}=0)}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.list(
                        new AssignmentTargetPropertyProperty(new ComputedPropertyName(new YieldExpression(Maybe.nothing())),
                                new BindingIdentifier("a")))), new LiteralNumericExpression(0.0)))))));

        testScript("function* a() {} function a() {}", new Script(ImmutableList.nil(), ImmutableList.list(
                new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())), new FunctionDeclaration(
                        new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("function a() { function* a() {} function a() {} }", new FunctionDeclaration(new BindingIdentifier("a"),
                false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())),
                        new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(),
                                Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("function*g() { (function*(x = yield){}); }", new FunctionDeclaration(new BindingIdentifier("g"), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.nothing(), true,
                        new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("x"), Maybe.just(new YieldExpression(Maybe.nothing())))), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))
                )))));

        testScript("function*g() {x = { x: { x = yield } } = 0;}", new FunctionDeclaration(new BindingIdentifier("g"), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                        new AssignmentExpression(new BindingIdentifier("x"), new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.list(
                                new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new ObjectAssignmentTarget(ImmutableList.list(
                                        new AssignmentTargetPropertyIdentifier(new BindingIdentifier("x"), Maybe.just(new YieldExpression(Maybe.nothing())))
                                ))))),
                                new LiteralNumericExpression(0.0)))
                )))));

        testScriptFailure("label: function* a(){}", 15, "Unexpected token \"*\"");
        testScriptFailure("function*g(yield){}", 11, "Unexpected token \"yield\"");
        testScriptFailure("function*g({yield}){}", 17, "Unexpected token \"yield\"");
        testScriptFailure("function*g([yield]){}", 12, "Unexpected token \"yield\"");
        testScriptFailure("function*g({a: yield}){}", 15, "Unexpected token \"yield\"");
        testScriptFailure("function*g(yield = 0){}", 11, "Unexpected token \"yield\"");
        testScriptFailure("function*g() { var yield; }", 19, "Unexpected token \"yield\"");
        testScriptFailure("function*g() { var yield = 1; }", 19, "Unexpected token \"yield\"");
        testScriptFailure("function*g() { function yield(){}; }", 24, "Unexpected token \"yield\"");
        testScriptFailure("function*g() { let yield; }", 19, "Unexpected token \"yield\"");
        testScriptFailure("function*g() { try {} catch (yield) {} }", 29, "Unexpected token \"yield\"");
    }
}
