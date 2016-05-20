package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ForStatementTest extends ParserTestCase {
    @Test
    public void testForStatement() throws JsError {
        testScript("for(x, y;;);", new ForStatement(
                Maybe.of(new BinaryExpression(BinaryOperator.Sequence, new IdentifierExpression("x"), new IdentifierExpression("y"))),
                Maybe.empty(),
                Maybe.empty(),
                new EmptyStatement()));

        testScript("for(x = 0;;);", new ForStatement(Maybe.of(new AssignmentExpression(new BindingIdentifier("x"),
                new LiteralNumericExpression(0.0))), Maybe.empty(), Maybe.empty(), new EmptyStatement()));

        testScript("for(var x = 0;;);", new ForStatement(Maybe.of(new VariableDeclaration(VariableDeclarationKind.Var,
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
                new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))), Maybe.empty(), new EmptyStatement()));

        testScript("for(x; x < 0; x++);", new ForStatement(Maybe.of(new IdentifierExpression("x")),
                Maybe.of(new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))), Maybe.of(new UpdateExpression(false, UpdateOperator.Increment,
                new BindingIdentifier("x"))), new EmptyStatement()));

        testScript("for(x; x < 0; x++) process(x);", new ForStatement(Maybe.of(new IdentifierExpression("x")),
                Maybe.of(new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))),
                Maybe.of(new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("x"))),
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
