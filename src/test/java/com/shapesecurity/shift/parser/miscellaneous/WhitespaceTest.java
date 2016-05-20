package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class WhitespaceTest extends ParserTestCase {
    @Test
    public void testWhitespace() throws JsError {
        testScript("{ x\n++y }", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                new IdentifierExpression("x")), new ExpressionStatement(new UpdateExpression(true, UpdateOperator.Increment,
                new BindingIdentifier("y")))))));

        testScript("{ x\n--y }", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                new IdentifierExpression("x")), new ExpressionStatement(new UpdateExpression(true, UpdateOperator.Decrement,
                new BindingIdentifier("y")))))));

        testScript("{ var x = 14, y = 3\nz; }", new BlockStatement(new Block(ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(14.0))),
                        new VariableDeclarator(new BindingIdentifier("y"), Maybe.of(new LiteralNumericExpression(3.0)))))),
                new ExpressionStatement(new IdentifierExpression("z"))))));

        testScript("while (true) { continue\nthere; }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("there")))))));

        testScript("while (true) { continue // Comment\nthere; }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("there")))))));

        testScript("while (true) { continue /* Multiline\nComment */there; }", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("there")))))));

        testScript("while (true) { break\nthere; }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new BreakStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("there")))))));

        testScript("while (true) { break // Comment\nthere; }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new BreakStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("there")))))));
        testScript("while (true) { break /* Multiline\nComment */there; }", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("there")))))));

        testScript("0 ;", new LiteralNumericExpression(0.0));

        testScript("(function(){ return\nx; })", new FunctionExpression(Maybe.empty(), false, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ReturnStatement(Maybe.empty()), new ExpressionStatement(new IdentifierExpression("x"))))));

        testScript("(function(){ return // Comment\nx; })", new FunctionExpression(Maybe.empty(), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ReturnStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("x"))))));

        testScript("(function(){ return/* Multiline\nComment */x; })", new FunctionExpression(Maybe.empty(), false,
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ReturnStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("x"))))));

        testScript("{ throw error\nerror; }", new BlockStatement(new Block(ImmutableList.of(new ThrowStatement(
                new IdentifierExpression("error")), new ExpressionStatement(new IdentifierExpression("error"))))));

        testScript("{ throw error// Comment\nerror; }", new BlockStatement(new Block(ImmutableList.of(new ThrowStatement(
                new IdentifierExpression("error")), new ExpressionStatement(new IdentifierExpression("error"))))));

        testScript("{ throw error/* Multiline\nComment */error; }", new BlockStatement(new Block(ImmutableList.of(
                new ThrowStatement(new IdentifierExpression("error")), new ExpressionStatement(
                        new IdentifierExpression("error"))))));

        testScript("throw /* \u202a */ e", new ThrowStatement(new IdentifierExpression("e")));

        testScript("new\u0020\u0009\u000B\u000C\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\uFEFFa",
                new NewExpression(new IdentifierExpression("a"), ImmutableList.empty()));

        testScript("{0\n1\r2\u20283\u20294}", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                        new LiteralNumericExpression(0.0)), new ExpressionStatement(new LiteralNumericExpression(1.0)),
                new ExpressionStatement(new LiteralNumericExpression(2.0)), new ExpressionStatement(
                        new LiteralNumericExpression(3.0)), new ExpressionStatement(new LiteralNumericExpression(4.0))))));

        testScriptFailure("throw /* \n */ e", 2, 4, 14, "Illegal newline after throw");
        testScriptFailure("throw /* \u2028 */ e", 2, 4, 14, "Illegal newline after throw");
        testScriptFailure("throw /* \u2029 */ e", 2, 4, 14, "Illegal newline after throw");
    }
}
