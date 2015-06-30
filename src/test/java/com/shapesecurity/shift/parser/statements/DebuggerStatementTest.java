package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.shift.ast.DebuggerStatement;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class DebuggerStatementTest extends Assertions {
  @Test
  public void testDebuggerStatement() throws JsError {
    testScript("debugger", new DebuggerStatement());
    testScript("debugger;", new DebuggerStatement());
  }
}
