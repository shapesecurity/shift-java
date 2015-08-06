package com.shapesecurity.shift.parser.destructuring.binding_pattern;

import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.EmptyStatement;
import com.shapesecurity.shift.ast.ForInStatement;
import com.shapesecurity.shift.ast.LiteralNumericExpression;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class BindingIdentifierTest extends ParserTestCase {
    @Test
    public void testBindingIdentifier() throws JsError {
        testScript("for(let in 0);", new ForInStatement(new BindingIdentifier("let"), new LiteralNumericExpression(0.0),
                new EmptyStatement()));
    }
}
