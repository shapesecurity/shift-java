package com.shapesecurity.shift.es2018.parser.gecko;

import com.shapesecurity.shift.es2018.parser.JsError;
import org.junit.Test;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptSuccess;

public class AsyncAwaitMiscTest {

    @Test
    public void test() throws JsError{

        testScriptSuccess("var expr = async function foo() {\n" +
                "    return await\n" +
                "    10;\n" +
                "  };");

        testScriptSuccess("async \n" +
                "function a(){}");
    }
}
