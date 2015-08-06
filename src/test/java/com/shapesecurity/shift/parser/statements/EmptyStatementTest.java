package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.shift.ast.EmptyStatement;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class EmptyStatementTest extends ParserTestCase {
    @Test
    public void testEmptyStatement() throws JsError {
        testScript(";", new EmptyStatement());
    }
}
