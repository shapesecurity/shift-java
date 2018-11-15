package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class GroupedExpressionTest extends ParserTestCase {
    @Test
    public void testGroupedExpression() throws JsError {
        testScript("(a)", new IdentifierExpression("a"));

        testScript("(0)", new LiteralNumericExpression(0.0));

        testScript("(0, a)", new BinaryExpression(new LiteralNumericExpression(0.0), BinaryOperator.Sequence,
                new IdentifierExpression("a")));

        testScript("(a, 0)", new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.Sequence,
                new LiteralNumericExpression(0.0)));

        testScript("(a, a)", new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.Sequence,
                new IdentifierExpression("a")));

        testScript("((a,a),(a,a))", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("a")), BinaryOperator.Sequence, new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.Sequence, new IdentifierExpression("a"))));

        testScript("((((((((((((((((((((((((((((((((((((((((a))))))))))))))))))))))))))))))))))))))))",
                new IdentifierExpression("a"));

        testScriptFailure("(0, {a = 0}) = 0", 0, "Invalid left-hand side in assignment");
        testScriptFailure("({a = 0})", 2, "Illegal property initializer");
        testScriptFailure("(0, {a = 0}) => 0", 0, "Illegal arrow function parameter list");
        testScriptFailure("({a = 0}, {a = 0}, 0) => 0", 19, "Unexpected number");
    }
}
