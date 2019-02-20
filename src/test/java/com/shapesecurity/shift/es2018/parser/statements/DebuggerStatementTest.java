package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.shift.es2018.ast.DebuggerStatement;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;

import org.junit.Test;

public class DebuggerStatementTest extends ParserTestCase {
    @Test
    public void testDebuggerStatement() throws JsError {
        testScript("debugger", new DebuggerStatement());
        testScript("debugger;", new DebuggerStatement());
    }
}
