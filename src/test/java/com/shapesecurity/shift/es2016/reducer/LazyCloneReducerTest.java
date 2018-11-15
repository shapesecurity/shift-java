package com.shapesecurity.shift.es2016.reducer;

import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import junit.framework.TestCase;

import javax.annotation.Nonnull;

public class LazyCloneReducerTest extends TestCase {
    public static class IncrementReducer extends LazyReconstructingReducer {
        @Nonnull
        @Override
        public Expression reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node) {
            return new LiteralNumericExpression(node.value + 1);
        }
    }

    public void testWithoutMutation() throws JsError {
        String program = "let a = 'b'; null;";
        Script originalTree = Parser.parseScript(program);
        Script newTree = (Script) Director.reduceProgram(new IncrementReducer(), originalTree);
        assertTrue(newTree == originalTree);
    }

    public void testWithMutation() throws JsError {
        String program = "let a = 0; null;";
        Script originalTree = Parser.parseScript(program);
        Script newTree = (Script) Director.reduceProgram(new IncrementReducer(), originalTree);
        assertTrue(newTree != originalTree);
        assertEquals(Parser.parseScript("let a = 1; null;"), newTree);
        assertTrue(newTree.statements.index(1).fromJust() == originalTree.statements.index(1).fromJust());
    }
}
