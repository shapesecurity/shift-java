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
        testScript("[]", new ArrayExpression(ImmutableList.nil()));

        testScript("[ ]", new ArrayExpression(ImmutableList.nil()));

        testScript("[ 0 ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)))));

        testScript("[ 0, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)))));

        testScript("[ ,, 0 ]", new ArrayExpression(ImmutableList.list(Maybe.nothing(), Maybe.nothing(),
                Maybe.just(new LiteralNumericExpression(0.0)))));

        testScript("[ 1, 2, 3, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(1.0)),
                Maybe.just(new LiteralNumericExpression(2.0)), Maybe.just(new LiteralNumericExpression(3.0)))));

        testScript("[ 1, 2,, 3, ]", new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(1.0)),
                Maybe.just(new LiteralNumericExpression(2.0)), Maybe.nothing(), Maybe.just(new LiteralNumericExpression(3.0)))));

        testScript("[,,1,,,2,3,,]", new ArrayExpression(ImmutableList.list(Maybe.nothing(), Maybe.nothing(),
                Maybe.just(new LiteralNumericExpression(1.0)), Maybe.nothing(), Maybe.nothing(),
                Maybe.just(new LiteralNumericExpression(2.0)), Maybe.just(new LiteralNumericExpression(3.0)), Maybe.nothing())));

        testScript("[a, ...(b=c)]", new ArrayExpression(ImmutableList.list(Maybe.just(new IdentifierExpression("a")), Maybe.just(new SpreadElement(new AssignmentExpression(new BindingIdentifier("b"), new IdentifierExpression("c")))))));
        testScript("[,...a]", new ArrayExpression(ImmutableList.list(Maybe.nothing(), Maybe.just(new SpreadElement(new IdentifierExpression("a"))))));

        testScriptFailure("[a, ...(b=c)] = 0", 14, "Invalid left-hand side in assignment");
        testScriptFailure("[0] = 0", 4, "Invalid left-hand side in assignment");
    }
}
