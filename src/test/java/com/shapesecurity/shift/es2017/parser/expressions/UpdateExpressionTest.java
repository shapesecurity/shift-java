package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class UpdateExpressionTest extends ParserTestCase {
    @Test
    public void testUpdateExpression() throws JsError {
        testScript("++a", new UpdateExpression(true, UpdateOperator.Increment, new AssignmentTargetIdentifier("a")));
        testScript("--a", new UpdateExpression(true, UpdateOperator.Decrement, new AssignmentTargetIdentifier("a")));

        testScript("x++", new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("x")));
        testScript("x--", new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("x")));

        testScript("eval++", new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("eval")));
        testScript("eval--", new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("eval")));

        testScript("arguments++", new UpdateExpression(false, UpdateOperator.Increment, new AssignmentTargetIdentifier("arguments")));
        testScript("arguments--", new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("arguments")));
    }
}
