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
                Maybe.just(new BinaryExpression(BinaryOperator.Sequence, new IdentifierExpression("x"), new IdentifierExpression("y"))),
                Maybe.nothing(),
                Maybe.nothing(),
                new EmptyStatement()));

        testScript("for(x = 0;;);", new ForStatement(Maybe.just(new AssignmentExpression(new BindingIdentifier("x"),
                new LiteralNumericExpression(0.0))), Maybe.nothing(), Maybe.nothing(), new EmptyStatement()));

        testScript("for(var x = 0;;);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing(), Maybe.nothing(),
                new EmptyStatement()));

        testScript("for(let x = 0;;);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing(), Maybe.nothing(),
                new EmptyStatement()));

        testScript("for(var x = 0, y = 1;;);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"),
                        Maybe.just(new LiteralNumericExpression(0.0))), new VariableDeclarator(new BindingIdentifier("y"),
                        Maybe.just(new LiteralNumericExpression(1.0)))))), Maybe.nothing(), Maybe.nothing(),
                new EmptyStatement()));

        testScript("for(x; x < 0;);", new ForStatement(Maybe.just(new IdentifierExpression("x")), Maybe.just(
                new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))), Maybe.nothing(), new EmptyStatement()));

        testScript("for(x; x < 0; x++);", new ForStatement(Maybe.just(new IdentifierExpression("x")),
                Maybe.just(new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))), Maybe.just(new UpdateExpression(false, UpdateOperator.Increment,
                new BindingIdentifier("x"))), new EmptyStatement()));

        testScript("for(x; x < 0; x++) process(x);", new ForStatement(Maybe.just(new IdentifierExpression("x")),
                Maybe.just(new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("x"),
                        new LiteralNumericExpression(0.0))),
                Maybe.just(new UpdateExpression(false, UpdateOperator.Increment, new BindingIdentifier("x"))),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("process"),
                        ImmutableList.list(new IdentifierExpression("x"))))));

        testScript("for(a;b;c);", new ForStatement(Maybe.just(new IdentifierExpression("a")),
                Maybe.just(new IdentifierExpression("b")), Maybe.just(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a;b;c);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing())))),
                Maybe.just(new IdentifierExpression("b")), Maybe.just(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a = 0;b;c);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.just(new IdentifierExpression("b")),
                Maybe.just(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(var a = 0;;) { let a; }", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing(), Maybe.nothing(),
                new BlockStatement(new Block(ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(
                        VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"),
                        Maybe.nothing())))))))));

        testScript("for(;b;c);", new ForStatement(Maybe.nothing(), Maybe.just(new IdentifierExpression("b")),
                Maybe.just(new IdentifierExpression("c")), new EmptyStatement()));

        testScript("for(let of;;);", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("of"), Maybe.nothing())))), Maybe.nothing(),
                Maybe.nothing(), new EmptyStatement()));

        testScript("for(let a;;); let a;", new ForStatement(Maybe.just(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing())))), Maybe.nothing(),
                Maybe.nothing(), new EmptyStatement()));

        testScriptFailure("for({a=0};;);", 9, "Illegal property initializer"); // TODO changed index number from 4 to 9
    }
}
