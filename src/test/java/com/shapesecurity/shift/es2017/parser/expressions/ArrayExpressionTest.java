package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.SpreadElement;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

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
        testScript("[0 , ...a = 0]", new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new SpreadElement(new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0)))))));

        testScriptFailure("[a, ...(b=c)] = 0", 14, "Invalid left-hand side in assignment");
        testScriptFailure("[0] = 0", 4, "Invalid left-hand side in assignment");
    }
}
