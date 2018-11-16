package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ForInStatementTest extends ParserTestCase {
    @Test
    public void testForInStatement() throws JsError {
        testScript("for(x in list) process(x);", new ForInStatement(new AssignmentTargetIdentifier("x"),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for (var x in list) process(x);", new ForInStatement(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for (let x in list) process(x);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for(var a in b);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for(a in b);", new ForInStatement(new AssignmentTargetIdentifier("a"), new IdentifierExpression("b"),
                new EmptyStatement()));

        testScript("for(a.b in c);", new ForInStatement(new StaticMemberAssignmentTarget(new IdentifierExpression("a"), "b"),
                new IdentifierExpression("c"), new EmptyStatement()));

        testScript("for(let of in of);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("of"), Maybe.empty()))),
                new IdentifierExpression("of"), new EmptyStatement()));

        testScript("for(const a in b);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for(let a in b, c);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new BinaryExpression(new IdentifierExpression("b"), BinaryOperator.Sequence, new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(a in b, c);", new ForInStatement(new AssignmentTargetIdentifier("a"),
                new BinaryExpression(new IdentifierExpression("b"), BinaryOperator.Sequence, new IdentifierExpression("c")),
                new EmptyStatement()));

        testScript("for([{a=0}] in b);", new ForInStatement(
                new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(
                        new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.of(new LiteralNumericExpression(0)))))
                )), Maybe.empty()),
                new IdentifierExpression("b"),
                new EmptyStatement()
        ));


        testScriptFailure("for(let a = 0 in b);", 14, "Invalid variable declaration in for-in statement");
        testScriptFailure("for(const a = 0 in b);", 16, "Invalid variable declaration in for-in statement");
        testScriptFailure("for(let ? b : c in 0);", 16, "Invalid left-hand side in for-in");

    }
}
