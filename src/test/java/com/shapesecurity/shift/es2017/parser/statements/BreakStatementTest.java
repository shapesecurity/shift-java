package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class BreakStatementTest extends ParserTestCase {
    @Test
    public void testBreakStatement() throws JsError {
        testScript("while (true) { break }", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(
                new Block(ImmutableList.of(new BreakStatement(Maybe.empty()))))));

        testScript("done: while (true) { break done }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(
                Maybe.of("done"))))))));

        testScript("done: while (true) { break done; }", new LabeledStatement("done", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(
                Maybe.of("done"))))))));

        testScript("__proto__: while (true) { break __proto__; }", new LabeledStatement("__proto__", new WhileStatement(
                new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(
                Maybe.of("__proto__"))))))));
    }
}
