package com.shapesecurity.shift.es2017.parser.destructuring.binding_pattern;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.ArrowExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingPropertyIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingPropertyProperty;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.CatchClause;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.ObjectBinding;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.TryCatchStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ObjectBindingTest extends ParserTestCase {
    @Test
    public void testObjectBinding() throws JsError {
        testScript("var {a} = 0;", new VariableDeclarationStatement(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(
                        new BindingIdentifier("a"), Maybe.empty()))), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("var [{a = 0}] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new ObjectBinding(
                        ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.of(
                                new LiteralNumericExpression(0.0))))))), Maybe.empty()), Maybe.of(
                        new LiteralNumericExpression(0.0)))))));

        testScript("var [{__proto__:a, __proto__:b}] = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(
                Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("__proto__"),
                        new BindingIdentifier("a")), new BindingPropertyProperty(new StaticPropertyName("__proto__"),
                        new BindingIdentifier("b")))))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("var {a, x: {y: a}} = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ObjectBinding(ImmutableList.of(
                new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.empty()), new BindingPropertyProperty(
                        new StaticPropertyName("x"), new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(
                        new StaticPropertyName("y"), new BindingIdentifier("a"))))))), Maybe.of(new LiteralNumericExpression(0.0)))))));


        testScript("var a, {x: {y: a}} = 0;", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"),
                Maybe.empty()), new VariableDeclarator(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(
                new StaticPropertyName("x"), new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(
                new StaticPropertyName("y"), new BindingIdentifier("a"))))))), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("var {let, yield} = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
            ImmutableList.of(new VariableDeclarator(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(
                new BindingIdentifier("let"), Maybe.empty()), new BindingPropertyIdentifier(
                new BindingIdentifier("yield"), Maybe.empty()))), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("(a, b, [c]) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"),
                new BindingIdentifier("b"), new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("c"))),
                        Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));

        testScript("try {} catch ({e}) {}", new TryCatchStatement(new Block(ImmutableList.empty()), new CatchClause(
                new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("e"), Maybe.empty()))),
                new Block(ImmutableList.empty()))));

        testScript("try {} catch ({e = 0}) {}", new TryCatchStatement(new Block(ImmutableList.empty()), new CatchClause(
                new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("e"), Maybe.of(
                        new LiteralNumericExpression(0.0))))), new Block(ImmutableList.empty()))));

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
