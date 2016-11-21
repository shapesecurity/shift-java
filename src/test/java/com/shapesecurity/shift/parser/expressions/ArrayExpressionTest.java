package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ArrayExpressionTest extends ParserTestCase {
    @Test
    public void testArrayExpression() throws JsError {
        testScript("[]", new ArrayExpression(ImmutableList.empty()));

        testScript("[ ]", new ArrayExpression(ImmutableList.empty()));

        testScript("[ 0 ]", new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)))));

        testScript("[ 0, ]", new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)))));

        testScript("[ ,, 0 ]", new ArrayExpression(ImmutableList.of(Maybe.empty(), Maybe.empty(),
                Maybe.of(new LiteralNumericExpression(0.0)))));

        testScript("[ 1, 2, 3, ]", new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(1.0)),
                Maybe.of(new LiteralNumericExpression(2.0)), Maybe.of(new LiteralNumericExpression(3.0)))));

        testScript("[ 1, 2,, 3, ]", new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(1.0)),
                Maybe.of(new LiteralNumericExpression(2.0)), Maybe.empty(), Maybe.of(new LiteralNumericExpression(3.0)))));

        testScript("[,,1,,,2,3,,]", new ArrayExpression(ImmutableList.of(Maybe.empty(), Maybe.empty(),
                Maybe.of(new LiteralNumericExpression(1.0)), Maybe.empty(), Maybe.empty(),
                Maybe.of(new LiteralNumericExpression(2.0)), Maybe.of(new LiteralNumericExpression(3.0)), Maybe.empty())));

        testScript("[a, ...(b=c)]", new ArrayExpression(ImmutableList.of(Maybe.of(new IdentifierExpression("a")), Maybe.of(new SpreadElement(new AssignmentExpression(new AssignmentTargetIdentifier("b"), new IdentifierExpression("c")))))));
        testScript("[,...a]", new ArrayExpression(ImmutableList.of(Maybe.empty(), Maybe.of(new SpreadElement(new IdentifierExpression("a"))))));

        testScriptFailure("[a, ...(b=c)] = 0", 14, "Invalid left-hand side in assignment");
        testScriptFailure("[0] = 0", 4, "Invalid left-hand side in assignment");
    }
}
