package com.shapesecurity.shift.es2016.parser.statements;

import com.shapesecurity.shift.es2016.ast.DebuggerStatement;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class DebuggerStatementTest extends ParserTestCase {
    @Test
    public void testDebuggerStatement() throws JsError {
        testScript("debugger", new DebuggerStatement());
        testScript("debugger;", new DebuggerStatement());
    }
}
