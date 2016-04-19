package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class SuperExpressionTest extends ParserTestCase {
    @Test
    public void testSuperExpression() throws JsError {
        testScript("(class extends B { constructor() { super() } });", new ClassExpression(Maybe.nothing(), Maybe.just(
                new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil()))))
                )))));

        testScript("class A extends B { constructor() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil()))))
                )))));

        testScript("class A extends B { \"constructor\"() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil()))))
                )))));

        testScript("class A extends B { constructor(a = super()){} }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("a"),
                    Maybe.just(new CallExpression(new Super(), ImmutableList.nil())))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil()))))));

        testScript("class A extends B { constructor() { ({a: super()}); } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("a"),
                    new CallExpression(new Super(), ImmutableList.nil())))))))
                )))));

        testScript("class A extends B { constructor() { () => super(); } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new ArrowExpression(new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new CallExpression(new Super(), ImmutableList.nil())))))
                )))));

        testScript("class A extends B { constructor() { () => { super(); } } }", new ClassDeclaration(
                new BindingIdentifier("A"), Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false,
                new Method(false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(
                        ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ArrowExpression(new FormalParameters(
                        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                    new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil()))))))))
                        )))));

        testScript("({ a() { super.b(); } });", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new StaticMemberExpression(new Super(), "b"),
                        ImmutableList.nil()))))))));

        testScript("({ *a() { super.b = 0; } });", new ObjectExpression(ImmutableList.list(new Method(true, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new StaticMemberAssignmentTarget(new Super(), "b"),
                        new LiteralNumericExpression(0.0)))))))));

        testScript("({ get a() { super[0] = 1; } });", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("a"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new AssignmentExpression(
                new ComputedMemberAssignmentTarget(new Super(), new LiteralNumericExpression(0.0)),
                new LiteralNumericExpression(1.0)))))))));

        testScript("({ set a(x) { super.b[0] = 1; } });", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("a"),
                new Parameter(new BindingIdentifier("x"), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                new AssignmentExpression(new ComputedMemberAssignmentTarget(
                        new StaticMemberExpression(new Super(), "b"), new LiteralNumericExpression(0.0)), new LiteralNumericExpression(1.0)))))
                ))));

        testScript("(class { constructor() { super.x } });", new ClassExpression(Maybe.nothing(), Maybe.nothing(),
                ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                        new StaticMemberExpression(new Super(), "x")))))))));

        testScript("class A extends B { constructor() { super.x } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new StaticMemberExpression(new Super(), "x"))))
                )))));

        testScript("class A { a() { () => super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
                ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                        new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new StaticMemberExpression(
                                new Super(), "b"))))))))));

        testScript("class A { a() { new super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
                ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                        new NewExpression(new StaticMemberExpression(new Super(), "b"), ImmutableList.nil()))))
                        )))));

        testScript("class A { a() { new super.b(); } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
                ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
                        new NewExpression(new StaticMemberExpression(new Super(), "b"), ImmutableList.nil()))))
                        )))));

        testScript("({ *f() { yield super.f(); } });", new ObjectExpression(ImmutableList.list(
            new Method(true, new StaticPropertyName("f"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                new ExpressionStatement(new YieldExpression(Maybe.just(new CallExpression(new StaticMemberExpression(new Super(), "f"), ImmutableList.nil()))))
            )))
        )));

        testScriptFailure("function f() { (super)() }", 21, "Unexpected token \"super\"");
        testScriptFailure("class A extends B { constructor() { super; } }", 41, "Unexpected token \"super\"");
        testScriptFailure("class A extends B { constructor() { (super)(); } }", 42, "Unexpected token \"super\"");
        testScriptFailure("class A extends B { constructor() { new super(); } }", 45, "Unexpected token \"(\""); // TODO: changed error from unexpected super

        testScriptFailure("({ a() { (super).b(); } });", 15, "Unexpected token \"super\"");
        testScriptFailure("class A extends B { constructor() { (super).a(); } }", 42, "Unexpected token \"super\"");
    }
}
