package com.shapesecurity.shift.es2018.parser.destructuring.assignment;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.*;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

import org.junit.Test;

public class ObjectAssignmentTargetTest extends ParserTestCase {
    @Test
    public void testObjectAssignmentTarget() throws JsError {
        testScript("({x} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x=0, y:z} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))), new AssignmentTargetPropertyProperty(new StaticPropertyName("y"), new AssignmentTargetIdentifier("z"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x,} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x,y} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.empty()), new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("y"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x,y,} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.empty()), new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("y"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({[a]: a} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new ComputedPropertyName(new IdentifierExpression("a")), new AssignmentTargetIdentifier("a"))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({x = 0} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({x = 0,} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({x: y} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x: y,} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({var: x} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("var"), new AssignmentTargetIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({\"x\": y} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({'x': y} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({0: y} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("0"), new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({0: x, 1: x} = 0)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("0"), new AssignmentTargetIdentifier("x")), new AssignmentTargetPropertyProperty(new StaticPropertyName("1"), new AssignmentTargetIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x: y = 0} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetWithDefault(new AssignmentTargetIdentifier("y"), new LiteralNumericExpression(0.0)))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({x: y = z = 0} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetWithDefault(new AssignmentTargetIdentifier("y"), new AssignmentExpression(new AssignmentTargetIdentifier("z"), new LiteralNumericExpression(0.0))))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({x: [y] = 0} = 1)", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("x"), new AssignmentTargetWithDefault(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("y"))), Maybe.empty()), new LiteralNumericExpression(0.0)))), Maybe.empty()), new LiteralNumericExpression(1.0)));
        testScript("({a:let} = 0);", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("a"), new AssignmentTargetIdentifier("let"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({let} = 0);", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("let"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({a:yield} = 0);", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("a"), new AssignmentTargetIdentifier("yield"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({yield} = 0);", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("yield"), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({yield = 0} = 0);", new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("yield"), Maybe.of(new LiteralNumericExpression(0.0)))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("let {a:b=c} = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("a"), new BindingWithDefault(new BindingIdentifier("b"), new IdentifierExpression("c")))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("(function*() { [...{ x = yield }] = 0; })", new FunctionExpression(false, true, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(
                        new ExpressionStatement(new AssignmentExpression(
                                new ArrayAssignmentTarget(ImmutableList.empty(), Maybe.of(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("x"), Maybe.of(new YieldExpression(Maybe.empty())))), Maybe.empty()))),
                                new LiteralNumericExpression(0.0)
                        ))
                )
        )));

        testScript(
                "({d=0,f:h().a} = 0)",
                new AssignmentExpression(
                        new ObjectAssignmentTarget(
                                ImmutableList.of(
                                        new AssignmentTargetPropertyIdentifier(
                                                new AssignmentTargetIdentifier("d"),
                                                Maybe.of(new LiteralNumericExpression(0.0))
                                        ),
                                        new AssignmentTargetPropertyProperty(
                                                new StaticPropertyName("f"),
                                                new StaticMemberAssignmentTarget(
                                                        new CallExpression(
                                                                new IdentifierExpression("h"),
                                                                ImmutableList.empty()
                                                        ),
                                                        "a"
                                                )
                                        )
                                ), Maybe.empty()
                        ),
                        new LiteralNumericExpression(0.0)
                )
        );

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

    @Test
    public void testObjectAssignmentSpread() throws JsError {
        testScript("({a, ...b} = {})", new AssignmentExpression(
                new ObjectAssignmentTarget(ImmutableList.of(
                        new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.empty())
                ),
                Maybe.of(new AssignmentTargetIdentifier("b")
        )), new ObjectExpression(ImmutableList.empty())));

        testScript("({...b} = {})", new AssignmentExpression(
                new ObjectAssignmentTarget(ImmutableList.empty(), Maybe.of(new AssignmentTargetIdentifier("b"))),
                new ObjectExpression(ImmutableList.empty())
        ));


        testScriptFailure("({a, ...b, c} = {})", 14, "Invalid left-hand side in assignment");
    }
}
