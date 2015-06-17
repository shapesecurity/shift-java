package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class SuperExpressionTest extends Assertions {
  @Test
  public void testSuperExpression() throws JsError {
    testScript("(class extends B { constructor() { super() } });", new ClassExpression(Maybe.nothing(), Maybe.just(
        new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil())))),
        new StaticPropertyName("constructor"))))));

    testScript("class A extends B { constructor() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil())))),
        new StaticPropertyName("constructor"))))));

    testScript("class A extends B { \"constructor\"() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil())))),
        new StaticPropertyName("\"constructor\""))))));

    testScript("class A extends B { constructor(a = super()){} }", new ClassDeclaration(new BindingIdentifier("A"),
        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"), new CallExpression(
            new Super(), ImmutableList.nil()))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil()), new StaticPropertyName("constructor"))))));

    testScript("class A extends B { constructor() { ({a: super()}); } }", new ClassDeclaration(new BindingIdentifier("A"),
        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new DataProperty(
            new CallExpression(new Super(), ImmutableList.nil()), new StaticPropertyName("a"))))))),
        new StaticPropertyName("constructor"))))));

//    testScript("class A extends B { constructor() { () => super(); } }", new ClassDeclaration(new BindingIdentifier("A"),
//        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
//        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
//        ImmutableList.list(new ExpressionStatement(new ArrowExpression(new FormalParameters(ImmutableList.nil(),
//            Maybe.nothing()), new CallExpression(new Super(), ImmutableList.nil()))))),
//        new StaticPropertyName("constructor"))))));
//
//    testScript("class A extends B { constructor() { () => { super(); } } }", new ClassDeclaration(
//        new BindingIdentifier("A"), Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false,
//        new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(
//            ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ArrowExpression(new FormalParameters(
//            ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
//            new ExpressionStatement(new CallExpression(new Super(), ImmutableList.nil())))))))),
//            new StaticPropertyName("constructor"))))));

    testScript("({ a() { super.b(); } });", new ObjectExpression(ImmutableList.list(new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new CallExpression(new StaticMemberExpression("b", new Super()),
            ImmutableList.nil())))), new StaticPropertyName("a")))));

    testScript("({ *a() { super.b = 0; } });", new ObjectExpression(ImmutableList.list(new Method(true,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new StaticMemberExpression("b", new Super()),
            new LiteralNumericExpression(0.0))))), new StaticPropertyName("a")))));

    testScript("({ get a() { super[0] = 1; } });", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new AssignmentExpression(
        new ComputedMemberExpression(new LiteralNumericExpression(0.0), new Super()),
        new LiteralNumericExpression(1.0))))), new StaticPropertyName("a")))));

//    testScript("({ set a(x) { super.b[0] = 1; } });", new ObjectExpression(ImmutableList.list(new Setter(
//        new BindingIdentifier("x"), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
//        new AssignmentExpression(new ComputedMemberExpression(new StaticMemberExpression("b", new Super()),
//            new LiteralNumericExpression(0.0)), new LiteralNumericExpression(1.0))))), new StaticPropertyName("a")))));

    testScript("(class { constructor() { super.x } });", new ClassExpression(Maybe.nothing(), Maybe.nothing(),
        ImmutableList.list(new ClassElement(false, new Method(false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
            new StaticMemberExpression("x", new Super())))), new StaticPropertyName("constructor"))))));

    testScript("class A extends B { constructor() { super.x } }", new ClassDeclaration(new BindingIdentifier("A"),
        Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false,
        new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.list(new ExpressionStatement(new StaticMemberExpression("x", new Super())))),
        new StaticPropertyName("constructor"))))));

//    testScript("class A { a() { () => super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
//        ImmutableList.list(new ClassElement(false, new Method(false, new FormalParameters(ImmutableList.nil(),
//            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
//            new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new StaticMemberExpression(
//                "b", new Super()))))), new StaticPropertyName("a"))))));

    testScript("class A { a() { new super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
        ImmutableList.list(new ClassElement(false, new Method(false, new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
            new NewExpression(new StaticMemberExpression("b", new Super()), ImmutableList.nil())))),
            new StaticPropertyName("a"))))));

    testScript("class A { a() { new super.b(); } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(),
        ImmutableList.list(new ClassElement(false, new Method(false,new FormalParameters(ImmutableList.nil(),
            Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(
            new NewExpression(new StaticMemberExpression("b", new Super()), ImmutableList.nil())))),
            new StaticPropertyName("a"))))));
//
    testScriptFailure("function f() { (super)() }", 21, "Unexpected token \"super\"");
    testScriptFailure("class A extends B { constructor() { super; } }", 41, "Unexpected token \"super\"");
    testScriptFailure("class A extends B { constructor() { (super)(); } }", 42, "Unexpected token \"super\"");
//    testScriptFailure("class A extends B { constructor() { new super(); } }", 0, "Unexpected token \"super\"");
    testScriptFailure("({ a() { (super).b(); } });", 15, "Unexpected token \"super\"");
    testScriptFailure("class A extends B { constructor() { (super).a(); } }", 42, "Unexpected token \"super\"");
  }
}
