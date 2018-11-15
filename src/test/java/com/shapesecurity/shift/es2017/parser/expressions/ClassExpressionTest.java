package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.Getter;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ClassElement;
import com.shapesecurity.shift.es2017.ast.ClassExpression;
import com.shapesecurity.shift.es2017.ast.ComputedPropertyName;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.Setter;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class ClassExpressionTest extends ParserTestCase {
    @Test
    public void testClassExpression() throws JsError {
        testScript("(class {})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.empty()));

        testScript("(class extends A {})", new ClassExpression(Maybe.empty(), Maybe.of(new IdentifierExpression("A")),
                ImmutableList.empty()));

        testScript("(class A extends A {})", new ClassExpression(Maybe.of(new BindingIdentifier("A")), Maybe.of(
                new IdentifierExpression("A")), ImmutableList.empty()));

        testScript("(class {;;;\n;\n})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.empty()));

        testScript("(class {;;;\n;a(){}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class {;;;\n;a(){}b(){}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))),
                new ClassElement(false, new Method(false, false, new StaticPropertyName("b"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class {set a(b) {}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Setter(new StaticPropertyName("a"), new BindingIdentifier("b"), new FunctionBody(ImmutableList.empty(),
                        ImmutableList.empty()))))));

        testScript("(class {get a() {}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Getter(new StaticPropertyName("a"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                        )))));

        testScript("(class {set a(b) {'use strict';}})", new ClassExpression(Maybe.empty(), Maybe.empty(),
                ImmutableList.of(new ClassElement(false, new Setter(new StaticPropertyName("a"), new BindingIdentifier("b"), new FunctionBody(
                        ImmutableList.of(new Directive("use strict")), ImmutableList.empty()))))));

        testScript("(class {a(b) {'use strict';}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.of(new BindingIdentifier("b")),
                        Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.empty())
                        )))));

        testScript("(class {prototype() {}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("prototype"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class {a() {}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class {3() {}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("3"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class{[3+5](){}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new ComputedPropertyName(new BinaryExpression(
                        new LiteralNumericExpression(3.0), BinaryOperator.Plus, new LiteralNumericExpression(5.0))), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class extends (a,b) {})", new ClassExpression(Maybe.empty(), Maybe.of(new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("b"))), ImmutableList.empty()));

        testScript("var x = class extends (a,b) {};", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"),
                Maybe.of(new ClassExpression(Maybe.empty(), Maybe.of(new BinaryExpression(
                        new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("b"))), ImmutableList.empty())))))));

        testScript("(class {static(){}})", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(
                new ClassElement(false, new Method(false, false, new StaticPropertyName("static"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                        new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));

        testScript("(class {static constructor(){}})", new ClassExpression(Maybe.empty(), Maybe.empty(),
                ImmutableList.of(new ClassElement(true, new Method(false, false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
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
