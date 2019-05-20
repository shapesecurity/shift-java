package com.shapesecurity.shift.es2018.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2018.ast.Module;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

import org.junit.Test;

public class ModuleTest extends ParserTestCase {
    @Test
    public void testModule() throws JsError {
        testModule("", new Module(ImmutableList.empty(), ImmutableList.empty()));
        testModuleFailure("/*", 2, "Unexpected end of input");
    }
}
