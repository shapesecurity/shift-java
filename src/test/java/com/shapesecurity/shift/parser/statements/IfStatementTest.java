package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;

public class IfStatementTest extends ParserTestCase {

    @Test
    public void testIfStatement() throws JsError {
        testScript("if (this) this;",
                new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.empty())
        );

        testScript("if (this) this; else this;",
                new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()),
                        Maybe.of(new ExpressionStatement(new ThisExpression())))
        );

        testScript("if (a) b;",
                new IfStatement(new IdentifierExpression("a"), new ExpressionStatement(new IdentifierExpression("b")), Maybe.empty())
        );

        testScript("if (a) b; else c;",
                new IfStatement(new IdentifierExpression("a"), new ExpressionStatement(new IdentifierExpression("b")),
                        Maybe.of(new ExpressionStatement(new IdentifierExpression("c"))))
        );

        testScript("if (morning) goodMorning();",
                new IfStatement(new IdentifierExpression("morning"),
                        new ExpressionStatement(new CallExpression(new IdentifierExpression("goodMorning"), ImmutableList.empty())),
                        Maybe.empty())
        );

        testScript("if (morning) (function(){})",
                new IfStatement(new IdentifierExpression("morning"),
                        new ExpressionStatement(new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))),
                        Maybe.empty())
        );

        testScript("if (morning) var x = 0;",
                new IfStatement(new IdentifierExpression("morning"),
                        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                                new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))
                        ))), Maybe.empty())
        );

        testScript("if (morning) goodMorning(); else goodDay();",
                new IfStatement(new IdentifierExpression("morning"),
                        new ExpressionStatement(new CallExpression(new IdentifierExpression("goodMorning"), ImmutableList.empty())),
                        Maybe.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("goodDay"), ImmutableList.empty()))))
        );
    }

}
