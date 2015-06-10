package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.shift.ast.EmptyStatement;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/10/15.
 */
public class EmptyStatementTest extends Assertions {
  @Test
  public void testEmptyStatement() throws JsError {
    testScript(";", new EmptyStatement());
  }
}
