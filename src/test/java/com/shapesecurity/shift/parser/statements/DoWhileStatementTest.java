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
        testScript("do keep(); while (true);", new DoWhileStatement(
                new ExpressionStatement(new CallExpression(new IdentifierExpression("keep"), ImmutableList.empty())), new LiteralBooleanExpression(true)));

        testScript("do continue; while(1);", new DoWhileStatement(
                new ContinueStatement(Maybe.empty()), new LiteralNumericExpression(1.0)));

        testScript("do ; while (true)", new DoWhileStatement(
                new EmptyStatement(), new LiteralBooleanExpression(true)));

        testScript("do {} while (true)", new DoWhileStatement(
                new BlockStatement(new Block(ImmutableList.empty())), new LiteralBooleanExpression(true)));

        testScript("{do ; while(false); false}", new BlockStatement(new Block(ImmutableList.of(new DoWhileStatement(
                new EmptyStatement(), new LiteralBooleanExpression(false)), new ExpressionStatement(
                new LiteralBooleanExpression(false))))));

        testScript("{do ; while(false) false}", new BlockStatement(new Block(ImmutableList.of(new DoWhileStatement(
                new EmptyStatement(), new LiteralBooleanExpression(false)), new ExpressionStatement(
                new LiteralBooleanExpression(false))))));
    }
}
