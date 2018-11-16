package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.ForStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ForStatementTest extends ParserTestCase {
    @Test
    public void testForStatement() throws JsError {
        testScript("for(x, y;;);", new ForStatement(
                Maybe.of(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.Sequence, new IdentifierExpression("y"))),
                Maybe.empty(),
                Maybe.empty(),
                new EmptyStatement()));

        testScript("for(x = 0;;);", new ForStatement(Maybe.of(new AssignmentExpression(new AssignmentTargetIdentifier("x"),
                new LiteralNumericExpression(0.0))), Maybe.empty(), Maybe.empty(), new EmptyStatement()));

        testScript("for(var x = 0;;);", new ForStatement(Maybe.of(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty(), Maybe.empty(),
                new EmptyStatement()));

        testScript("for(let x = 0;;);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty(), Maybe.empty(),
                new EmptyStatement()));

        testScript("for(var x = 0, y = 1;;);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.of(new LiteralNumericExpression(0.0))), new VariableDeclarator(new BindingIdentifier("y"),
                        Maybe.of(new LiteralNumericExpression(1.0)))))), Maybe.empty(), Maybe.empty(),
                new EmptyStatement()));

        testScript("for(x; x < 0;);", new ForStatement(Maybe.of(new IdentifierExpression("x")), Maybe.of(
                new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LessThan,
                        new LiteralNumericExpression(0.0))), Maybe.empty(), new EmptyStatement()));

        testScript("for(x; x < 0; x++);", new ForStatement(Maybe.of(new IdentifierExpression("x")),
                Maybe.of(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LessThan,
                        new LiteralNumericExpression(0.0))), Maybe.of(new UpdateExpression(false, UpdateOperator.Increment,
                new AssignmentTargetIdentifier("x"))), new EmptyStatement()));

        testScript("for(x; x < 0; x++) process(x);", new ForStatement(Maybe.of(new IdentifierExpression("x")),
                Maybe.of(new BinaryExpression(new IdentifierExpression("x"), BinaryOperator.LessThan,
                        new LiteralNumericExpression(0.0))),
                Maybe.of(new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("x"))),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("process"),
                        ImmutableList.of(new IdentifierExpression("x"))))));

        testScript("for(a;b;c);", new ForStatement(Maybe.of(new IdentifierExpression("a")),
                Maybe.of(new IdentifierExpression("b")), Maybe.of(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a;b;c);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))),
                Maybe.of(new IdentifierExpression("b")), Maybe.of(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a = 0;b;c);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.of(new IdentifierExpression("b")),
                Maybe.of(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a = 0;;) { let a; }", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty(), Maybe.empty(),
                new BlockStatement(new Block(ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(
                        VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.empty())))))))));

        testScript("for(;b;c);", new ForStatement(Maybe.empty(), Maybe.of(new IdentifierExpression("b")),
                Maybe.of(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(let of;;);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("of"), Maybe.empty())))), Maybe.empty(),
                Maybe.empty(), new EmptyStatement()));

        testScript("for(let a;;); let a;", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))), Maybe.empty(),
                Maybe.empty(), new EmptyStatement()));

        testScriptFailure("for({a=0};;);", 9, "Illegal property initializer"); // TODO changed index number from 4 to 9
    }
}
