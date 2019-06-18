package com.shapesecurity.shift.es2018.parser.gecko;

import com.shapesecurity.shift.es2018.parser.ErrorMessages;
import org.junit.Test;
import com.shapesecurity.shift.es2018.parser.JsError;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptSuccess;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptFailure;

public class AsyncAwaitReflectTest {

    @Test
    public void testScriptParsesSuccessfully() throws JsError {

       testScriptSuccess("async () => 1");
       testScriptSuccess("async a => 1");
       testScriptSuccess("async (a) => 1");
       testScriptSuccess("async async => 1");
       testScriptSuccess("async (async) => 1");
       testScriptSuccess("async ([a]) => 1");
       testScriptSuccess("async ([a, b]) => 1");
       testScriptSuccess("async ({a}) => 1");
       testScriptSuccess("async ({a, b}) => 1");
       
       // Expression body.
       testScriptSuccess("async a => a == b");

       // Expression body with nested async function.
       testScriptSuccess("async a => async");
       testScriptSuccess("async a => async b => c");
       testScriptSuccess("async a => async function() {}");
       testScriptSuccess("async a => async function b() {}");

       // Expression body with `await`.
       testScriptSuccess("async a => await 1");
       testScriptSuccess("async a => await await 1");
       testScriptSuccess("async a => await await await 1");

       testScriptSuccess("async a => await (async X => Y)");

       //But it can have `async` identifier as an operand.
       testScriptSuccess("async async => await async");

       // Block body.
       testScriptSuccess("async X => {yield}");

       // `yield` handling.
       testScriptSuccess("async X => yield");
       testScriptSuccess("async yield => X");
       testScriptSuccess("async yield => yield");
       testScriptSuccess("async X => {yield}");

       testScriptSuccess("async X => {yield}");
       testScriptSuccess("async yield => {X}");
       testScriptSuccess("async yield => {yield}");
       testScriptSuccess("function* g() { async X => yield }");

       // Not async functions.
       testScriptSuccess("async ()");
       testScriptSuccess("async (a)");
       testScriptSuccess("async (async)");
       testScriptSuccess("async ([a])");
       testScriptSuccess("async ([a, b])");
       testScriptSuccess("async ({a})");
       testScriptSuccess("async ({a, b})");

       // Async arrow function is assignment expression.
       testScriptSuccess("a ? async () => {1} : b");
       testScriptSuccess("a ? b : async () => {1}");

       // Await is still available as an identifier name in strict mode code.
       testScriptSuccess("function a() {'use strict'; var await = 3; }");
       testScriptSuccess("'use strict'; var await = 3");


       // Await is treated differently depending on context. Various cases.
       testScriptSuccess("var await = 3; async function a() { await 4; }");
       testScriptSuccess("async function a() { await 4; } var await = 5");
       testScriptSuccess("async function a() { function b() { return await; } }");
       testScriptSuccess("async function a() { var k = { async: 4 } }");
       testScriptSuccess("function a() { await: 4 }");

    }

    @Test
    public void testScriptHasSyntaxErrors() throws JsError{

        testScriptFailure("async ([a=await 1]) => 1", 16, ErrorMessages.UNEXPECTED_NUMBER);
        testScriptFailure("async ({a=await 1}) => 1",16, ErrorMessages.UNEXPECTED_NUMBER);

        testScriptFailure("async a => async b", 18, ErrorMessages.UNEXPECTED_EOS);
        testScriptFailure("async a => async function", 25, ErrorMessages.UNEXPECTED_EOS);
        testScriptFailure("async a => async function()", 27, ErrorMessages.UNEXPECTED_EOS);
        testScriptFailure("async a => await", 16, ErrorMessages.UNEXPECTED_EOS);
        testScriptFailure("async a => await await", 22, ErrorMessages.UNEXPECTED_EOS);

        // async property name error
        testScriptFailure("var {async async: a} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("let {async async: a} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("const {async async: a} = {}", 13, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));

        testScriptFailure("var {async async} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("let {async async} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("const {async async} = {}", 13, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));

        testScriptFailure("var {async async, } = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("let {async async, } = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("const {async async, } = {}", 13, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));

        testScriptFailure("var {async async = 0} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("let {async async = 0} = {}", 11, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));
        testScriptFailure("const {async async = 0} = {}", 13, String.format(ErrorMessages.UNEXPECTED_TOKEN, "async"));

        testScriptFailure("await 10", 6, ErrorMessages.UNEXPECTED_NUMBER);

        testScriptFailure("async(...await) => {}", 9, ErrorMessages.NO_AWAIT_IN_ASYNC_PARAMS);
        testScriptFailure("async(a, ...await) => {}", 12, ErrorMessages.NO_AWAIT_IN_ASYNC_PARAMS);
        testScriptFailure("a = async(...await) => {}) => {}",13, ErrorMessages.NO_AWAIT_IN_ASYNC_PARAMS);
        testScriptFailure("async(a = (...await) => {}) => {}",14, ErrorMessages.NO_AWAIT_IN_ASYNC_PARAMS);
        testScriptFailure("async(a = async(...await) => {}) => {}",19, ErrorMessages.NO_AWAIT_IN_ASYNC_PARAMS);
    }
}
