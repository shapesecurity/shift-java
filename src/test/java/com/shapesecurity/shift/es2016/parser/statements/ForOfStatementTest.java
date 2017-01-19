package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.ArrayAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.ArrayBinding;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetPropertyIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.EmptyStatement;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.ForOfStatement;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.ObjectAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.VariableDeclaration;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2016.ast.VariableDeclarator;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class ForOfStatementTest extends ParserTestCase {
    @Test
    public void testForOfStatement() throws JsError {
        testScript("for (var x of list) process(x);", new ForOfStatement(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
                new IdentifierExpression("process"), ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for(var a of b);", new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))),
                new IdentifierExpression("b"), new EmptyStatement()));

        testScript("for(a of b);", new ForOfStatement(new AssignmentTargetIdentifier("a"), new IdentifierExpression("b"),
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

        testScript("for([{a=0}] of b);", new ForOfStatement(
                new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(
                        new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.of(new LiteralNumericExpression(0)))))
                )), Maybe.empty()),
                new IdentifierExpression("b"),
                new EmptyStatement()
        ));


        testScriptFailure("for(let of 0);", 11, "Unexpected number");
        testScriptFailure("for(this of 0);", 9, "Invalid left-hand side in for-of");

        testScriptFailure("for(var a = 0 of b);", 14, "Invalid variable declaration in for-of statement");
        testScriptFailure("for(let a = 0 of b);", 14, "Invalid variable declaration in for-of statement");
        testScriptFailure("for(const a = 0 of b);", 16, "Invalid variable declaration in for-of statement");
    }
}
