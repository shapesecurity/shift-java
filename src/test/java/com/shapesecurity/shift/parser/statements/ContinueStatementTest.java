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
                new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.nothing()))))));

        testScript("while (true) { continue }", new WhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.nothing()))))));

        testScript("done: while (true) { continue done }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.just("done"))))))));

        testScript("done: while (true) { continue done; }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.just("done"))))))));

        testScript("__proto__: while (true) { continue __proto__; }", new LabeledStatement("__proto__", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.just("__proto__"))))))));

        testScript("a: do continue a; while(1);", new LabeledStatement("a", new DoWhileStatement(
                new ContinueStatement(Maybe.just("a")), new LiteralNumericExpression(1.0))));

        testScript("a: while (0) { continue \n b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue \r b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue \r\n b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\r*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\n*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\r\n*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\u2028*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));

        testScript("a: while (0) { continue /*\u2029*/ b; }", new LabeledStatement("a", new WhileStatement(
                new LiteralNumericExpression(0.0), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(
                Maybe.nothing()), new ExpressionStatement(new IdentifierExpression("b"))))))));
    }
}
