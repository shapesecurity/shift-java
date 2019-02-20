package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2018.ast.Block;
import com.shapesecurity.shift.es2018.ast.BlockStatement;
import com.shapesecurity.shift.es2018.ast.CallExpression;
import com.shapesecurity.shift.es2018.ast.ExpressionStatement;
import com.shapesecurity.shift.es2018.ast.IdentifierExpression;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

import org.junit.Test;

public class BlockStatementTest extends ParserTestCase {
    @Test
    public void testBlockStatement() throws JsError {
        testScript("{ foo }", new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(
                new IdentifierExpression("foo"))))));

        testScript("{ doThis(); doThat(); }", new BlockStatement(new Block(ImmutableList.of(
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThis"), ImmutableList.empty())),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"), ImmutableList.empty()))))));

        testScript("{}", new BlockStatement(new Block(ImmutableList.empty())));
    }
}
