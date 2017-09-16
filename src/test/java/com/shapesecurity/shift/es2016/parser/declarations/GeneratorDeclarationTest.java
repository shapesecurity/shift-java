package com.shapesecurity.shift.es2016.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class GeneratorDeclarationTest extends ParserTestCase {
    @Test
    public void testGeneratorDeclarationTest() throws JsError {
        testScript("function* a(){}", new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a(){yield}", new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new YieldExpression(Maybe.empty()))))));

        testScript("function* a(){yield a}", new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new YieldExpression(Maybe.of(new IdentifierExpression("a"))))))));

        testScript("function* yield(){}", new FunctionDeclaration(true, new BindingIdentifier("yield"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));


        testScript("function* hello() { yield yield z(); }", new FunctionDeclaration(true, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new YieldExpression(Maybe.of(new CallExpression(new IdentifierExpression("z"), ImmutableList.empty()))))))))));

        testScript("function* hello() { yield* z(); }", new FunctionDeclaration(true, new BindingIdentifier("hello"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new CallExpression(new IdentifierExpression("z"), ImmutableList.empty())))))));

        testScript("function* a(a=yield){}", new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(
                ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"), new YieldExpression(Maybe.empty()))),
                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a({[yield]:a}){}", new FunctionDeclaration(true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(
                        new ComputedPropertyName(new YieldExpression(Maybe.empty())), new BindingIdentifier("a"))))),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));

        testScript("function* a(){({[yield]:a}=0)}", new FunctionDeclaration(true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(
                        new AssignmentTargetPropertyProperty(new ComputedPropertyName(new YieldExpression(Maybe.empty())),
                                new AssignmentTargetIdentifier("a")))), new LiteralNumericExpression(0.0)))))));

        testScript("function* a() {} function a() {}", new Script(ImmutableList.empty(), ImmutableList.of(
                new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new FunctionDeclaration(false,
                        new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("function a() { function* a() {} function a() {} }", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("function*g() { (function*(x = yield){}); }", new FunctionDeclaration(true, new BindingIdentifier("g"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(true, Maybe.empty(),
                        new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("x"), new YieldExpression(Maybe.empty()))), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))
                )))));

        testScript("function*g() {x = { x: { x = yield } } = 0;}", new FunctionDeclaration(true, new BindingIdentifier("g"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new AssignmentExpression(new AssignmentTargetIdentifier("x"), new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(
                                new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new ObjectAssignmentTarget(ImmutableList.of(
                                        new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.of(new YieldExpression(Maybe.empty())))
                                ))))),
                                new LiteralNumericExpression(0.0)))
                )))));

        testScript("function a() { function a() {} function* a() {} }", new FunctionDeclaration(false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("function* a() { function a() {} function* a() {} }", new FunctionDeclaration(true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())),
                        new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

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
