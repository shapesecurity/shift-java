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
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a(){yield}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new YieldExpression(Maybe.empty()))))));

        testScript("function* a(){yield a}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new YieldExpression(Maybe.of(new IdentifierExpression("a"))))))));

        testScript("function* yield(){}", new FunctionDeclaration(new BindingIdentifier("yield"), true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a(a=yield){}", new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"), new YieldExpression(Maybe.empty()))),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a({[yield]:a}){}", new FunctionDeclaration(new BindingIdentifier("a"), true,
                new FormalParameters(ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(
                        new ComputedPropertyName(new YieldExpression(Maybe.empty())), new BindingIdentifier("a"))))),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a(){({[yield]:a}=0)}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new ObjectBinding(ImmutableList.of(
                        new BindingPropertyProperty(new ComputedPropertyName(new YieldExpression(Maybe.empty())),
                                new BindingIdentifier("a")))), new LiteralNumericExpression(0.0)))))));

        testScript("function* a() {} function a() {}", new Script(ImmutableList.empty(), ImmutableList.of(
                new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new FunctionDeclaration(
                        new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("function a() { function* a() {} function a() {} }", new FunctionDeclaration(new BindingIdentifier("a"),
                false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("function*g() { (function*(x = yield){}); }", new FunctionDeclaration(new BindingIdentifier("g"), true, new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(Maybe.empty(), true,
                        new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("x"), new YieldExpression(Maybe.empty()))), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))
                )))));

        testScript("function*g() {x = { x: { x = yield } } = 0;}", new FunctionDeclaration(new BindingIdentifier("g"), true, new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new AssignmentExpression(new BindingIdentifier("x"), new AssignmentExpression(new ObjectBinding(ImmutableList.of(
                                new BindingPropertyProperty(new StaticPropertyName("x"), new ObjectBinding(ImmutableList.of(
                                        new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new YieldExpression(Maybe.empty())))
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
