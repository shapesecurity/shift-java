package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ContinueStatementTest extends ParserTestCase {
    @Test
    public void testContinueStatement() throws JsError {
        testScript("while (true) { continue; }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.empty()))))));

        testScript("while (true) { continue }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.empty()))))));

        testScript("done: while (true) { continue done }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.of("done"))))))));

        testScript("done: while (true) { continue done; }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.of("done"))))))));

        testScript("__proto__: while (true) { continue __proto__; }", new LabeledStatement("__proto__", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.of("__proto__"))))))));

        testScript("a: do continue a; while(1);", new LabeledStatement("a", new DoWhileStatement(
                new LiteralNumericExpression(1.0), new ContinueStatement(Maybe.of("a")))));

        testScript("a: while (0) { continue \n b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue \r b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue \r\n b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\r*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\n*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\r\n*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\u2028*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\u2029*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(
                Maybe.empty()), new ExpressionStatement(new IdentifierExpression("b"))))))));
    }
}
