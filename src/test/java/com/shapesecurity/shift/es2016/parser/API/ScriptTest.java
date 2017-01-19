package com.shapesecurity.shift.es2016.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class ScriptTest extends ParserTestCase {
    @Test
    public void testScript() throws JsError {
        testScript("", new Script(ImmutableList.empty(), ImmutableList.empty()));
        testScript(" ", new Script(ImmutableList.empty(), ImmutableList.empty()));
        testScriptFailure("/*", 2, "Unexpected end of input");
    }
}
