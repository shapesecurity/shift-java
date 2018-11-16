package com.shapesecurity.shift.es2017.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class LexicalDeclarationTest extends ParserTestCase {
    @Test
    public void testLexicalDeclaration() throws JsError {

        testScript("for (; false; ) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new ForStatement(Maybe.empty(), Maybe.of(new LiteralBooleanExpression(false)), Maybe.empty(),
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("for (; false; ) let\nx = 0;", new Script(ImmutableList.empty(), ImmutableList.of(new ForStatement(Maybe.empty(), Maybe.of(new LiteralBooleanExpression(false)), Maybe.empty(),
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("for (var x in null) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))), new LiteralNullExpression(),
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("for (var x in null) let\nx = 0;", new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))), new LiteralNullExpression(),
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("for (var x of []) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))), new ArrayExpression(ImmutableList.empty()),
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("for (var x of []) let\nx = 0", new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))), new ArrayExpression(ImmutableList.empty()),
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("if (false) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new IfStatement(new LiteralBooleanExpression(false),
                new ExpressionStatement(new IdentifierExpression("let")), Maybe.empty()), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("if (false) let\nx = 0", new Script(ImmutableList.empty(), ImmutableList.of(new IfStatement(new LiteralBooleanExpression(false),
                new ExpressionStatement(new IdentifierExpression("let")), Maybe.empty()), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("l: let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("l",
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("l: let\nx = 0", new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("l",
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("with ({}) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new WithStatement(new ObjectExpression(ImmutableList.empty()),
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("with ({}) let\nx = 0", new Script(ImmutableList.empty(), ImmutableList.of(new WithStatement(new ObjectExpression(ImmutableList.empty()),
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));
        testScript("while (false) let\n{}", new Script(ImmutableList.empty(), ImmutableList.of(new WhileStatement(new LiteralBooleanExpression(false),
                new ExpressionStatement(new IdentifierExpression("let"))), new BlockStatement(new Block(ImmutableList.empty())))));
        testScript("while (false) let\nx = 0", new Script(ImmutableList.empty(), ImmutableList.of(new WhileStatement(new LiteralBooleanExpression(false),
                new ExpressionStatement(new IdentifierExpression("let"))), new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("x"), new LiteralNumericExpression(0.0))))));

        testScript("let a", new VariableDeclarationStatement(new VariableDeclaration(
			VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))));

        testScript("{ let a; }", new BlockStatement(new Block(ImmutableList.of(new VariableDeclarationStatement(
                new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(
                        new BindingIdentifier("a"), Maybe.empty()))))))));

        testScript("while(true) var a", new WhileStatement(new LiteralBooleanExpression(true),
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))));

        testScriptFailure("for (; false; ) let {}", 20, "Unexpected token \"{\"");
        testScriptFailure("while(true) let a", 16, "Unexpected identifier");
        testScriptFailure("while(true) const a", 12, "Unexpected token \"const\"");
        testScriptFailure("with(true) let a", 15, "Unexpected identifier");
        testScriptFailure("with(true) class a {}", 11, "Unexpected token \"class\"");
        testScriptFailure("a: let a", 7, "Unexpected identifier");
    }
}
