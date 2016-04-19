package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ClassExpressionTest extends ParserTestCase {
    @Test
    public void testClassExpression() throws JsError {
        testScript("(class {})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.nil()));

        testScript("(class extends A {})", new ClassExpression(Maybe.nothing(), Maybe.just(new IdentifierExpression("A")),
                ImmutableList.nil()));

        testScript("(class A extends A {})", new ClassExpression(Maybe.just(new BindingIdentifier("A")), Maybe.just(
                new IdentifierExpression("A")), ImmutableList.nil()));

        testScript("(class {;;;\n;\n})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.nil()));

        testScript("(class {;;;\n;a(){}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class {;;;\n;a(){}b(){}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))),
                new ClassElement(false, new Method(false, new StaticPropertyName("b"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class {set a(b) {}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Setter(new StaticPropertyName("a"), new Parameter(new BindingIdentifier("b"), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                        ImmutableList.nil()))))));

        testScript("(class {get a() {}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Getter(new StaticPropertyName("a"), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())
                        )))));

        testScript("(class {set a(b) {'use strict';}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(),
                ImmutableList.list(new ClassElement(false, new Setter(new StaticPropertyName("a"), new Parameter(new BindingIdentifier("b"), Maybe.nothing()), new FunctionBody(
                        ImmutableList.list(new Directive("use strict")), ImmutableList.nil()))))));

        testScript("(class {a(b) {'use strict';}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.list(new Parameter(new BindingIdentifier("b"), Maybe.nothing())),
                        Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use strict")), ImmutableList.nil())
                        )))));

        testScript("(class {prototype() {}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("prototype"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class {a() {}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class {3() {}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("3"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class{[3+5](){}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new ComputedPropertyName(new BinaryExpression(
                        new LiteralNumericExpression(3.0), BinaryOperator.Plus, new LiteralNumericExpression(5.0))), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class extends (a,b) {})", new ClassExpression(Maybe.nothing(), Maybe.just(new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("b"))), ImmutableList.nil()));

        testScript("var x = class extends (a,b) {};", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"),
                Maybe.just(new ClassExpression(Maybe.nothing(), Maybe.just(new BinaryExpression(
                        new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("b"))), ImmutableList.nil())))))));

        testScript("(class {static(){}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(), ImmutableList.list(
                new ClassElement(false, new Method(false, new StaticPropertyName("static"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))));

        testScript("(class {static constructor(){}})", new ClassExpression(Maybe.nothing(), Maybe.nothing(),
                ImmutableList.list(new ClassElement(true, new Method(false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.nil(),
                        Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())
                        )))));

        testScriptFailure("(class {a:0})", 9, "Only methods are allowed in classes");
        testScriptFailure("(class {a=0})", 9, "Only methods are allowed in classes");
        testScriptFailure("(class {a})", 9, "Only methods are allowed in classes");
        testScriptFailure("(class {3:0})", 9, "Only methods are allowed in classes");
        testScriptFailure("(class {[3]:0})", 11, "Only methods are allowed in classes");
        testScriptFailure("(class {)", 8, "Unexpected token \")\"");
        testScriptFailure("(class extends a,b {})", 16, "Unexpected token \",\"");
        testScriptFailure("(class extends !a {})", 15, "Unexpected token \"!\"");
        testScriptFailure("(class [a] {})", 7, "Unexpected token \"[\"");
        testScriptFailure("(class {[a,b](){}})", 10, "Unexpected token \",\"");

        // TODO
//    locationSanityTest("(class {})");
//    locationSanityTest("(class A {})");
//    locationSanityTest("(class A extends A{})");
//    locationSanityTest("(class extends A{})");
//    locationSanityTest("(class {a(){}})");
//    locationSanityTest("(class {[a](){}})");
//    locationSanityTest("(class {[a+b](){}})");
//    locationSanityTest("(class {get [a+b](){}})");
//    locationSanityTest("(class {set [a+b]([a]){}})");
//    locationSanityTest("(class {[a](){};})");
//    locationSanityTest("(class {[a](){};;})");
//    locationSanityTest("(class {static [a](){};;})");
    }
}
