package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2016.ast.Block;
import com.shapesecurity.shift.es2016.ast.BlockStatement;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

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
