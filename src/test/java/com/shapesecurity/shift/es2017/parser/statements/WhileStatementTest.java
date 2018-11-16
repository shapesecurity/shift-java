package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class WhileStatementTest extends ParserTestCase {
    @Test
    public void testWhileStatement() throws JsError {
        testScript("while(1);", new WhileStatement(new LiteralNumericExpression(1.0), new EmptyStatement()));

        testScript("while(true);", new WhileStatement(new LiteralBooleanExpression(true), new EmptyStatement()));

        testScript("while(true) doSomething()", new WhileStatement(
                new LiteralBooleanExpression(true),
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doSomething"), ImmutableList.empty())
                )));

        testScript("while (x < 10) {x++; y--; }", new WhileStatement(new BinaryExpression(
                new IdentifierExpression("x"), BinaryOperator.LessThan, new LiteralNumericExpression(10.0)), new BlockStatement(new Block(
                ImmutableList.of(new ExpressionStatement(new UpdateExpression(false, UpdateOperator.Increment,
                        new AssignmentTargetIdentifier("x"))), new ExpressionStatement(new UpdateExpression(false, UpdateOperator.Decrement,
                        new AssignmentTargetIdentifier("y"))))
        ))));
    }
}
