package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class BlockStatementTest extends ParserTestCase {
    @Test
    public void testBlockStatement() throws JsError {
        testScript("{ foo }", new BlockStatement(new Block(ImmutableList.list(new ExpressionStatement(
                new IdentifierExpression("foo"))))));

        testScript("{ doThis(); doThat(); }", new BlockStatement(new Block(ImmutableList.list(
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThis"), ImmutableList.nil())),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"), ImmutableList.nil()))))));

        testScript("{}", new BlockStatement(new Block(ImmutableList.nil())));
    }
}
