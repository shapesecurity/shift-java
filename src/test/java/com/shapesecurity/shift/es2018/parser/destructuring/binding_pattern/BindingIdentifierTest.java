package com.shapesecurity.shift.es2018.parser.destructuring.binding_pattern;

import com.shapesecurity.shift.es2018.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2018.ast.EmptyStatement;
import com.shapesecurity.shift.es2018.ast.ForInStatement;
import com.shapesecurity.shift.es2018.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;

import org.junit.Test;

public class BindingIdentifierTest extends ParserTestCase {
    @Test
    public void testBindingIdentifier() throws JsError {
        testScript("for(let in 0);", new ForInStatement(new AssignmentTargetIdentifier("let"), new LiteralNumericExpression(0.0),
                new EmptyStatement()));
    }
}
