package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class LabeledStatementTest extends ParserTestCase {
    @Test
    public void testLabeledStatement() throws JsError {
        testScript("start: for (;;) break start", new LabeledStatement("start", new ForStatement(Maybe.empty(),
                Maybe.empty(), Maybe.empty(), new BreakStatement(Maybe.of("start")))));

        testScript("start: while (true) break start", new LabeledStatement("start", new WhileStatement(
                new LiteralBooleanExpression(true), new BreakStatement(Maybe.of("start")))));

        testScript("__proto__: test", new LabeledStatement("__proto__", new ExpressionStatement(
                new IdentifierExpression("test"))));

        testScript("a:{break a;}", new LabeledStatement("a", new BlockStatement(new Block(ImmutableList.of(
                new BreakStatement(Maybe.of("a")))))));
    }
}
