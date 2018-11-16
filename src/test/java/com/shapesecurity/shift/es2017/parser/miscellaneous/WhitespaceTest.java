package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.NewExpression;
import com.shapesecurity.shift.es2017.ast.ReturnStatement;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.ContinueStatement;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.ThrowStatement;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class WhitespaceTest extends ParserTestCase {
    @Test
    public void testWhitespace() throws JsError {
        testScript("{ x\n++y }", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                new IdentifierExpression("x")), new ExpressionStatement(new UpdateExpression(true, UpdateOperator.Increment,
                new AssignmentTargetIdentifier("y")))))));

        testScript("{ x\n--y }", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                new IdentifierExpression("x")), new ExpressionStatement(new UpdateExpression(true, UpdateOperator.Decrement,
                new AssignmentTargetIdentifier("y")))))));

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

        testScript("(function(){ return\nx; })", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new ReturnStatement(Maybe.empty()), new ExpressionStatement(new IdentifierExpression("x"))))));

        testScript("(function(){ return // Comment\nx; })", new FunctionExpression(false, false, Maybe.empty(),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ReturnStatement(Maybe.empty()), new ExpressionStatement(
                        new IdentifierExpression("x"))))));

        testScript("(function(){ return/* Multiline\nComment */x; })", new FunctionExpression(false, false, Maybe.empty(),
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

        testScript("new\u0020\u0009\u000B\u000C\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\uFEFFa",
                new NewExpression(new IdentifierExpression("a"), ImmutableList.empty()));

        testScript("{0\n1\r2\u20283\u20294}", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                        new LiteralNumericExpression(0.0)), new ExpressionStatement(new LiteralNumericExpression(1.0)),
                new ExpressionStatement(new LiteralNumericExpression(2.0)), new ExpressionStatement(
                        new LiteralNumericExpression(3.0)), new ExpressionStatement(new LiteralNumericExpression(4.0))))));

        testScriptFailure("throw /* \n */ e", 2, 4, 14, "Illegal newline after throw");
        testScriptFailure("throw /* \u2028 */ e", 2, 4, 14, "Illegal newline after throw");
        testScriptFailure("throw /* \u2029 */ e", 2, 4, 14, "Illegal newline after throw");

        testScriptFailure("\u180e", 1, 0, 0, "Unexpected \"\u180e\"");

    }
}
