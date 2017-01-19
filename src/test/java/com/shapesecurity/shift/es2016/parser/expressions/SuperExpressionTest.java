package com.shapesecurity.shift.es2016.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.ArrowExpression;
import com.shapesecurity.shift.es2016.ast.AssignmentExpression;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingWithDefault;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.ClassDeclaration;
import com.shapesecurity.shift.es2016.ast.ClassElement;
import com.shapesecurity.shift.es2016.ast.ClassExpression;
import com.shapesecurity.shift.es2016.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.DataProperty;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.Getter;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.Method;
import com.shapesecurity.shift.es2016.ast.NewExpression;
import com.shapesecurity.shift.es2016.ast.ObjectExpression;
import com.shapesecurity.shift.es2016.ast.Setter;
import com.shapesecurity.shift.es2016.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.ast.StaticPropertyName;
import com.shapesecurity.shift.es2016.ast.Super;
import com.shapesecurity.shift.es2016.ast.YieldExpression;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class SuperExpressionTest extends ParserTestCase {
    @Test
    public void testSuperExpression() throws JsError {
        testScript("(class extends B { constructor() { super() } });", new ClassExpression(Maybe.empty(), Maybe.of(
                new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.empty()))))
                )))));

        testScript("class A extends B { constructor() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.empty()))))
                )))));

        testScript("class A extends B { \"constructor\"() { super() } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new Super(), ImmutableList.empty()))))
                )))));

        testScript("class A extends B { constructor(a = super()){} }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.of(new BindingWithDefault(new BindingIdentifier("a"),
                    new CallExpression(new Super(), ImmutableList.empty()))), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))))));

        testScript("class A extends B { constructor() { ({a: super()}); } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("a"),
                    new CallExpression(new Super(), ImmutableList.empty())))))))
                )))));

        testScript("class A extends B { constructor() { () => super(); } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new ArrowExpression(new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new CallExpression(new Super(), ImmutableList.empty())))))
                )))));

        testScript("class A extends B { constructor() { () => { super(); } } }", new ClassDeclaration(
                new BindingIdentifier("A"), Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false,
                new Method(false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(
                        ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ArrowExpression(new FormalParameters(
                        ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                    new ExpressionStatement(new CallExpression(new Super(), ImmutableList.empty()))))))))
                        )))));

        testScript("({ a() { super.b(); } });", new ObjectExpression(ImmutableList.of(new Method(false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new StaticMemberExpression(new Super(), "b"),
                        ImmutableList.empty()))))))));

        testScript("({ *a() { super.b = 0; } });", new ObjectExpression(ImmutableList.of(new Method(true, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new StaticMemberAssignmentTarget(new Super(), "b"),
                        new LiteralNumericExpression(0.0)))))))));

        testScript("({ get a() { super[0] = 1; } });", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("a"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new AssignmentExpression(
                new ComputedMemberAssignmentTarget(new Super(), new LiteralNumericExpression(0.0)),
                new LiteralNumericExpression(1.0)))))))));

        testScript("({ set a(x) { super.b[0] = 1; } });", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("a"),
                new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                new AssignmentExpression(new ComputedMemberAssignmentTarget(
                        new StaticMemberExpression(new Super(), "b"), new LiteralNumericExpression(0.0)), new LiteralNumericExpression(1.0)))))
                ))));

        testScript("(class { constructor() { super.x } });", new ClassExpression(Maybe.empty(), Maybe.empty(),
                ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new StaticMemberExpression(new Super(), "x")))))))));

        testScript("class A extends B { constructor() { super.x } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("constructor"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new StaticMemberExpression(new Super(), "x"))))
                )))));

        testScript("class A { a() { () => super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.empty(),
                ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new ArrowExpression(new FormalParameters(ImmutableList.empty(), Maybe.empty()), new StaticMemberExpression(
                                new Super(), "b"))))))))));

        testScript("class A { a() { new super.b; } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.empty(),
                ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new NewExpression(new StaticMemberExpression(new Super(), "b"), ImmutableList.empty()))))
                        )))));

        testScript("class A { a() { new super.b(); } }", new ClassDeclaration(new BindingIdentifier("A"), Maybe.empty(),
                ImmutableList.of(new ClassElement(false, new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(),
                        Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new NewExpression(new StaticMemberExpression(new Super(), "b"), ImmutableList.empty()))))
                        )))));

        testScript("({ *f() { yield super.f(); } });", new ObjectExpression(ImmutableList.of(
            new Method(true, new StaticPropertyName("f"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ExpressionStatement(new YieldExpression(Maybe.of(new CallExpression(new StaticMemberExpression(new Super(), "f"), ImmutableList.empty()))))
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
