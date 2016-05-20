package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class DoWhileStatementTest extends ParserTestCase {
    @Test
    public void testDoWhileStatement() throws JsError {
        testScript("do keep(); while (true);", new DoWhileStatement(new LiteralBooleanExpression(true),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("keep"), ImmutableList.empty()))));

        testScript("do continue; while(1);", new DoWhileStatement(new LiteralNumericExpression(1.0),
                new ContinueStatement(Maybe.empty())));

        testScript("do ; while (true)", new DoWhileStatement(new LiteralBooleanExpression(true),
                new EmptyStatement()));

        testScript("do {} while (true)", new DoWhileStatement(new LiteralBooleanExpression(true),
                new BlockStatement(new Block(ImmutableList.empty()))));

        testScript("{do ; while(false); false}", new BlockStatement(new Block(ImmutableList.of(new DoWhileStatement(
                new LiteralBooleanExpression(false), new EmptyStatement()), new ExpressionStatement(
                new LiteralBooleanExpression(false))))));

        testScript("{do ; while(false) false}", new BlockStatement(new Block(ImmutableList.of(new DoWhileStatement(
                new LiteralBooleanExpression(false), new EmptyStatement()), new ExpressionStatement(
                new LiteralBooleanExpression(false))))));
    }
}
