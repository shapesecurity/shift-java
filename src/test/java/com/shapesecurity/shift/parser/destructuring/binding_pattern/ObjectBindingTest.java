package com.shapesecurity.shift.parser.destructuring.binding_pattern;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ObjectBindingTest extends ParserTestCase {
    @Test
    public void testObjectBinding() throws JsError {
        testScript("var {a} = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(
                        new BindingIdentifier("a"), Maybe.nothing()))), Maybe.just(new LiteralNumericExpression(0.0)))))));

        testScript("var [{a = 0}] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new ObjectBinding(
                        ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.just(
                                new LiteralNumericExpression(0.0))))))), Maybe.nothing()), Maybe.just(
                        new LiteralNumericExpression(0.0)))))));

        testScript("var [{__proto__:a, __proto__:b}] = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(
                Maybe.just(new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(new StaticPropertyName("__proto__"),
                        new BindingIdentifier("a")), new BindingPropertyProperty(new StaticPropertyName("__proto__"),
                        new BindingIdentifier("b")))))), Maybe.nothing()), Maybe.just(new LiteralNumericExpression(0.0)))))));

        testScript("var {a, x: {y: a}} = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ObjectBinding(ImmutableList.list(
                new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.nothing()), new BindingPropertyProperty(
                        new StaticPropertyName("x"), new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(
                        new StaticPropertyName("y"), new BindingIdentifier("a"))))))), Maybe.just(new LiteralNumericExpression(0.0)))))));


        testScript("var a, {x: {y: a}} = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"),
                Maybe.nothing()), new VariableDeclarator(new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(
                new StaticPropertyName("x"), new ObjectBinding(ImmutableList.list(new BindingPropertyProperty(
                new StaticPropertyName("y"), new BindingIdentifier("a"))))))), Maybe.just(new LiteralNumericExpression(0.0)))))));

        testScript("var {let, yield} = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
            ImmutableList.list(new VariableDeclarator(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(
                new BindingIdentifier("let"), Maybe.nothing()), new BindingPropertyIdentifier(
                new BindingIdentifier("yield"), Maybe.nothing()))), Maybe.just(new LiteralNumericExpression(0.0)))))));

        testScript("(a, b, [c]) => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a"),
                new BindingIdentifier("b"), new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("c"))),
                        Maybe.nothing())), Maybe.nothing()), new LiteralNumericExpression(0.0)));

        testScript("try {} catch ({e}) {}", new TryCatchStatement(new Block(ImmutableList.nil()), new CatchClause(
                new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("e"), Maybe.nothing()))),
                new Block(ImmutableList.nil()))));

        testScript("try {} catch ({e = 0}) {}", new TryCatchStatement(new Block(ImmutableList.nil()), new CatchClause(
                new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("e"), Maybe.just(
                        new LiteralNumericExpression(0.0))))), new Block(ImmutableList.nil()))));

        testScriptFailure("var {a: b.c} = 0;", 9, "Unexpected token \".\"");
        testScriptFailure("({e: a.b}) => 0", 0, "Illegal arrow function parameter list");
        testScriptFailure("function a({e: a.b}) {}", 16, "Unexpected token \".\"");
        testScriptFailure("function* a({e: a.b}) {}", 17, "Unexpected token \".\"");
        testScriptFailure("(function ({e: a.b}) {})", 16, "Unexpected token \".\"");
        testScriptFailure("(function* ({e: a.b}) {})", 17, "Unexpected token \".\"");
        testScriptFailure("({a({e: a.b}){}})", 9, "Unexpected token \".\"");
        testScriptFailure("({*a({e: a.b}){}})", 10, "Unexpected token \".\"");
        testScriptFailure("({set a({e: a.b}){}})", 13, "Unexpected token \".\"");
        testScriptFailure("try {} catch ({e: x.a}) {}", 19, "Unexpected token \".\"");
    }
}
