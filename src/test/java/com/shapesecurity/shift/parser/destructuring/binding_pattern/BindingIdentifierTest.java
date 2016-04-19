package com.shapesecurity.shift.parser.destructuring.binding_pattern;

import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class BindingIdentifierTest extends ParserTestCase {
    @Test
    public void testBindingIdentifier() throws JsError {
        testScript("for(let in 0);", new ForInStatement(new AssignmentTargetIdentifier("let"), new LiteralNumericExpression(0.0),
                new EmptyStatement()));
    }
}
