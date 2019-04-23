package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.*;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import org.junit.Test;

public class ForAwaitStatementTest extends ParserTestCase {
    @Test
    public void testForAwaitStatement() throws JsError {
        testScript("for await (var x of list) process(x);", new ForAwaitStatement(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for await(var a of b);", new ForAwaitStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for await(a of b);", new ForAwaitStatement(new AssignmentTargetIdentifier("a"), new IdentifierExpression("b"),
                new EmptyStatement()));

        testScript("for await(let [a] of b);", new ForAwaitStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(
                        new BindingIdentifier("a"))), Maybe.empty()), Maybe.empty()))), new IdentifierExpression("b"),
                new EmptyStatement()));

        testScript("for await(let of of b);", new ForAwaitStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("of"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for await(const a of b);", new ForAwaitStatement(new VariableDeclaration(VariableDeclarationKind.Const,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for await([{a=0}] of b);", new ForAwaitStatement(
                new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(
                        new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.of(new LiteralNumericExpression(0)))), Maybe.empty())
                )), Maybe.empty()),
                new IdentifierExpression("b"),
                new EmptyStatement()
        ));

        testScriptFailure("for await(;;);", 10, "Unexpected token \";\"");
        testScriptFailure("for await(let i;;);", 15, "Unexpected token \";\"");

        testScriptFailure("for await(let of 0);", 17, "Unexpected number");
        testScriptFailure("for await(this of 0);", 15, "Invalid left-hand side in for-await");

        testScriptFailure("for await(let a of b, c);", 20, "Unexpected token \",\"");
        testScriptFailure("for await(a of b, c);", 16, "Unexpected token \",\"");

        testScriptFailure("for await(var a = 0 of b);", 20, "Invalid variable declaration in for-await statement");
        testScriptFailure("for await(let a = 0 of b);", 20, "Invalid variable declaration in for-await statement");
        testScriptFailure("for await(const a = 0 of b);", 22, "Invalid variable declaration in for-await statement");
        testScriptFailure("for await(let.x of a);", 16, "Invalid left-hand side in for-await");
    }
}
