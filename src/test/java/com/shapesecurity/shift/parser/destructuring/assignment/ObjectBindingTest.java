package com.shapesecurity.shift.parser.destructuring.assignment;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ObjectBindingTest extends ParserTestCase {
    @Test
    public void testObjectBinding() throws JsError {
        testScript("({x} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({x,} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({x,y} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.empty()), new BindingPropertyIdentifier(new BindingIdentifier("y"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({x,y,} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.empty()), new BindingPropertyIdentifier(new BindingIdentifier("y"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({[a]: a} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new ComputedPropertyName(new IdentifierExpression("a")), new BindingIdentifier("a")))), new LiteralNumericExpression(1.0)));
        testScript("({x = 0} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))))), new LiteralNumericExpression(1.0)));
        testScript("({x = 0,} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))))), new LiteralNumericExpression(1.0)));
        testScript("({x: y} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingIdentifier("y")))), new LiteralNumericExpression(0.0)));
        testScript("({x: y,} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingIdentifier("y")))), new LiteralNumericExpression(0.0)));
        testScript("({var: x} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("var"), new BindingIdentifier("x")))), new LiteralNumericExpression(0.0)));
        testScript("({\"x\": y} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingIdentifier("y")))), new LiteralNumericExpression(0.0)));
        testScript("({'x': y} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingIdentifier("y")))), new LiteralNumericExpression(0.0)));
        testScript("({0: y} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("0"), new BindingIdentifier("y")))), new LiteralNumericExpression(0.0)));
        testScript("({0: x, 1: x} = 0)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("0"), new BindingIdentifier("x")), new BindingPropertyProperty(new StaticPropertyName("1"), new BindingIdentifier("x")))), new LiteralNumericExpression(0.0)));
        testScript("({x: y = 0} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingWithDefault(new BindingIdentifier("y"), new LiteralNumericExpression(0.0))))), new LiteralNumericExpression(1.0)));
        testScript("({x: y = z = 0} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingWithDefault(new BindingIdentifier("y"), new AssignmentExpression(new BindingIdentifier("z"), new LiteralNumericExpression(0.0)))))), new LiteralNumericExpression(1.0)));
        testScript("({x: [y] = 0} = 1)", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("x"), new BindingWithDefault(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0))))), new LiteralNumericExpression(1.0)));
        testScript("({a:let} = 0);", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"), new BindingIdentifier("let")))), new LiteralNumericExpression(0.0)));
        testScript("({let} = 0);", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("let"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({a:yield} = 0);", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"), new BindingIdentifier("yield")))), new LiteralNumericExpression(0.0)));
        testScript("({yield} = 0);", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("yield"), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("({yield = 0} = 0);", new AssignmentExpression(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("yield"), Maybe.of(new LiteralNumericExpression(0.0))))), new LiteralNumericExpression(0.0)));
        testScript("let {a:b=c} = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"), new BindingWithDefault(new BindingIdentifier("b"), new IdentifierExpression("c"))))), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("(function*() { [...{ x = yield }] = 0; })", new FunctionExpression(Maybe.empty(), true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(
                        new ExpressionStatement(new AssignmentExpression(
                                new ArrayBinding(ImmutableList.empty(), Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new YieldExpression(Maybe.empty()))))))),
                                new LiteralNumericExpression(0.0)
                        ))
                )
        )));

        testScriptFailure("({a = 0});", 2, "Illegal property initializer");
        testScriptFailure("({a} += 0);", 5, "Invalid left-hand side in assignment");
        testScriptFailure("({a,,} = 0)", 4, "Unexpected token \",\"");
        testScriptFailure("({,a,} = 0)", 2, "Unexpected token \",\"");
        testScriptFailure("({a,,a} = 0)", 4, "Unexpected token \",\"");
        testScriptFailure("({function} = 0)", 10, "Unexpected token \"function\"");
        testScriptFailure("({a:function} = 0)", 12, "Unexpected token \"}\"");
        testScriptFailure("({a:for} = 0)", 4, "Unexpected token \"for\"");
        testScriptFailure("({'a'} = 0)", 5, "Unexpected string"); // TODO: changed error from unexpected }
        testScriptFailure("({var} = 0)", 5, "Unexpected token \"var\"");
        testScriptFailure("({a.b} = 0)", 3, "Unexpected token \".\"");
        testScriptFailure("({0} = 0)", 3, "Unexpected number"); // TODO: changed error from unexpected }
    }
}
