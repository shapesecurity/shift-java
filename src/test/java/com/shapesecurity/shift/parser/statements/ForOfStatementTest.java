package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ForOfStatementTest extends ParserTestCase {
    @Test
    public void testForOfStatement() throws JsError {
        testScript("for (var x of list) process(x);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for(var a of b);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for(a of b);", new ForOfStatement(new BindingIdentifier("a"), new IdentifierExpression("b"),
                new EmptyStatement()));

        testScript("for(let [a] of b);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(
                        new BindingIdentifier("a"))), Maybe.empty()), Maybe.empty()))), new IdentifierExpression("b"),
                new EmptyStatement()));

        testScript("for(let of of b);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("of"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for(const a of b);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScriptFailure("for(let of 0);", 11, "Unexpected number");
        testScriptFailure("for(this of 0);", 9, "Invalid left-hand side in for-of");

        testScriptFailure("for(var a = 0 of b);", 14, "Invalid variable declaration in for-of statement");
        testScriptFailure("for(let a = 0 of b);", 14, "Invalid variable declaration in for-of statement");
        testScriptFailure("for(const a = 0 of b);", 16, "Invalid variable declaration in for-of statement");
    }
}
