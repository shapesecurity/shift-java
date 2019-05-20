package com.shapesecurity.shift.es2018.parser.gecko;

import com.shapesecurity.shift.es2018.parser.JsError;
import org.junit.Test;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptEarlyError;
import com.shapesecurity.shift.es2018.parser.ErrorMessages;

public class AsyncAwaitEarlyErrorsTest {

    @Test
    public void test() throws JsError {

        // If FormalParameters Contains AwaitExpression is true.
        testScriptEarlyError("async function a(k = await 3) {}", "Async function parameters must not contain await expressions");
        testScriptEarlyError("(async function(k = await 3) {})", "Async function parameters must not contain await expressions");
        testScriptEarlyError("(async function a(k = await 3) {})", "Async function parameters must not contain await expressions");

        // If BindingIdentifier is `eval` or `arguments`.
        testScriptEarlyError("\"use strict\"; async function eval() {}", String.format(ErrorMessages.INVALID_ID_BINDING_STRICT_MODE,"eval"));
        testScriptEarlyError("\"use strict\"; (async function eval() {})", String.format(ErrorMessages.INVALID_ID_BINDING_STRICT_MODE,"eval"));

        testScriptEarlyError("\"use strict\"; async function arguments() {}", String.format(ErrorMessages.INVALID_ID_BINDING_STRICT_MODE,"arguments"));
        testScriptEarlyError("\"use strict\"; (async function arguments() {})", String.format(ErrorMessages.INVALID_ID_BINDING_STRICT_MODE,"arguments"));

        // If any element of the BoundNames of FormalParameters also occurs in the
        // LexicallyDeclaredNames of AsyncFunctionBody.
        testScriptEarlyError("async function a(x) { let x; }", "Duplicate binding \"x\"");
        testScriptEarlyError("(async function(x) { let x; })", "Duplicate binding \"x\"");
        testScriptEarlyError("(async function a(x) { let x; })", "Duplicate binding \"x\"");

        // If FormalParameters contains SuperProperty is true.
        testScriptEarlyError("async function a(k = super.prop) { }", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);
        testScriptEarlyError("(async function(k = super.prop) {})", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);
        testScriptEarlyError("(async function a(k = super.prop) {})", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);

        // If AsyncFunctionBody contains SuperProperty is true.
        testScriptEarlyError("async function a() { super.prop(); }", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);
        testScriptEarlyError("(async function() { super.prop(); })", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);
        testScriptEarlyError("(async function a() { super.prop(); })", ErrorMessages.ILLEGAL_ACCESS_SUPER_MEMBER);

        // If FormalParameters contains SuperCall is true.
        testScriptEarlyError("async function a(k = super()) {}", ErrorMessages.ILLEGAL_SUPER_CALL);
        testScriptEarlyError("(async function(k = super()) {})", ErrorMessages.ILLEGAL_SUPER_CALL);
        testScriptEarlyError("(async function a(k = super()) {})", ErrorMessages.ILLEGAL_SUPER_CALL);

        // If AsyncFunctionBody contains SuperCall is true.
        testScriptEarlyError("async function a() { super(); }", ErrorMessages.ILLEGAL_SUPER_CALL);
        testScriptEarlyError("(async function() { super(); })", ErrorMessages.ILLEGAL_SUPER_CALL);
        testScriptEarlyError("(async function a() { super(); })", ErrorMessages.ILLEGAL_SUPER_CALL);

        testScriptEarlyError("async function a(k = await 3) {}", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b(k = await 3) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b(k = [await 3]) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b([k = await 3]) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b([k = [await 3]]) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b({k = await 3}) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);
        testScriptEarlyError("async function a() { async function b({k = [await 3]}) {} }", ErrorMessages.ILLEGAL_AWAIT_IN_ASYNC_PARAMS);

    }
}
