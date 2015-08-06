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
        testScript("start: for (;;) break start", new LabeledStatement("start", new ForStatement(Maybe.nothing(),
                Maybe.nothing(), Maybe.nothing(), new BreakStatement(Maybe.just("start")))));

        testScript("start: while (true) break start", new LabeledStatement("start", new WhileStatement(
                new LiteralBooleanExpression(true), new BreakStatement(Maybe.just("start")))));

        testScript("__proto__: test", new LabeledStatement("__proto__", new ExpressionStatement(
                new IdentifierExpression("test"))));

        testScript("a:{break a;}", new LabeledStatement("a", new BlockStatement(new Block(ImmutableList.list(
                new BreakStatement(Maybe.just("a")))))));
    }
}
