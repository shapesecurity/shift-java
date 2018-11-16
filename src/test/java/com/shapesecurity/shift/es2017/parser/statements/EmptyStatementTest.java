package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class EmptyStatementTest extends ParserTestCase {
    @Test
    public void testEmptyStatement() throws JsError {
        testScript(";", new EmptyStatement());
    }
}
