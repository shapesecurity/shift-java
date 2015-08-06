package com.shapesecurity.shift.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ModuleTest extends ParserTestCase {
    @Test
    public void testModule() throws JsError {
        testModule("", new Module(ImmutableList.nil(), ImmutableList.nil()));
        testModuleFailure("/*", 2, "Unexpected end of input");
    }
}
