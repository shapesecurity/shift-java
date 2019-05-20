package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.shift.es2018.ast.EmptyStatement;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;

import org.junit.Test;

public class EmptyStatementTest extends ParserTestCase {
    @Test
    public void testEmptyStatement() throws JsError {
        testScript(";", new EmptyStatement());
    }
}
