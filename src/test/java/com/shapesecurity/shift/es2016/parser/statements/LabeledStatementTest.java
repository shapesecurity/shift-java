package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.Block;
import com.shapesecurity.shift.es2016.ast.BlockStatement;
import com.shapesecurity.shift.es2016.ast.BreakStatement;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.ForStatement;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LabeledStatement;
import com.shapesecurity.shift.es2016.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2016.ast.WhileStatement;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

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
