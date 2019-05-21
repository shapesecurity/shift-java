package com.shapesecurity.shift.es2018.parser.gecko;

import com.shapesecurity.shift.es2018.parser.ErrorMessages;
import com.shapesecurity.shift.es2018.parser.ParserTest;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptSuccess;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptFailure;
import com.shapesecurity.shift.es2018.parser.JsError;
import org.junit.Test;


public class AsyncAwaitCoverInitTest {

    @Test
    public void test() throws JsError{

        // codeContainingCoverInitNameWithNoSyntaxError
        // CoverInitName in async arrow parameters
        testScriptSuccess("async ({a = 1}) => {}");
        testScriptSuccess("async ({a = 1}, {b = 2}) => {}");
        testScriptSuccess("async ({a = 1}, {b = 2}, {c = 3}) => {}");
        testScriptSuccess("async ({a = 1} = {}, {b = 2}, {c = 3}) => {}");
        testScriptSuccess("async ({a = 1} = {}, {b = 2} = {}, {c = 3}) => {}");
        testScriptSuccess("async ({a = 1} = {}, {b = 2} = {}, {c = 3} = {}) => {}");

        // CoverInitName nested in array destructuring.
        testScriptSuccess("async ([{a = 0}]) => {}");

        // CoverInitName nested in rest pattern.
        testScriptSuccess("async ([...[{a = 0}]]) => {}");

        // CoverInitName nested in object destructuring.
        testScriptSuccess("async ({p: {a = 0}}) => {}");


        // codeContainingCoverInitNameWithSyntaxError
        testScriptFailure("obj.async({a = 1})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("typeof async({a = 1}, {b = 2} = {}, {c = 3} = {})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NotAsync({a = 1})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NotAsync({a = 1}, {b = 2})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NoAsync({a = 1}, {b = 2}, {c = 3})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NoAsync({a = 1} = {}, {b = 2}, {c = 3})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NoAsync({a = 1} = {}, {b = 2} = {}, {c = 3})", 0, ErrorMessages.ILLEGAL_PROPERTY);
        testScriptFailure("NoAsync({a = 1}, {b = 2} = {}, {c = 3} = {})", 0, ErrorMessages.ILLEGAL_PROPERTY);
    }

}