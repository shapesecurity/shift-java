package com.shapesecurity.shift.es2018.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.*;
import com.shapesecurity.shift.es2018.parser.ErrorMessages;
import com.shapesecurity.shift.es2018.parser.JsError;
import org.junit.Test;

import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScript;
import static com.shapesecurity.shift.es2018.parser.ParserTestCase.testScriptFailure;

public class TrailingCommaTest {

    @Test
    public void testParams() throws JsError {
        testScript("(a,) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new LiteralNumericExpression(0)));
        testScript("async (a,) => 0", new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new LiteralNumericExpression(0)));
        testScript("function a(b,) {}", new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
        testScript("(function (a,) {})", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
        testScript("({ a (b,) {} })", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
    }

    @Test
    public void testCall() throws JsError {
//        testScript("a(b,)", new CallExpression(new IdentifierExpression("a"), ImmutableList.of(new IdentifierExpression("b"))));
//        testScript("async(a,)", new CallExpression(new IdentifierExpression("async"), ImmutableList.of(new IdentifierExpression("a"))));
        testScript("new a(b,)", new NewExpression(new IdentifierExpression("a"), ImmutableList.of(new IdentifierExpression("b"))));
        testScript("new async(a,)", new NewExpression(new IdentifierExpression("async"), ImmutableList.of(new IdentifierExpression("a"))));
    }

    @Test
    public void testFailures() {
        testScriptFailure("(,) => 0", 1, "Unexpected token \",\"");
        testScriptFailure("(a,,) => 0", 3, "Unexpected token \",\"");
        testScriptFailure("(a, ...b,) => 0", 8, ErrorMessages.INVALID_LAST_REST_PARAMETER);
        testScriptFailure("async (,) => 0", 7, "Unexpected token \",\"");
        testScriptFailure("async (a,,) => 0", 9, "Unexpected token \",\"");
        testScriptFailure("async (a, ...b,) => 0", 17, String.format(ErrorMessages.UNEXPECTED_TOKEN, "=>"));
        testScriptFailure("function a(,) {}", 11, "Unexpected token \",\"");
        testScriptFailure("function a(b,,) {}", 13, "Unexpected token \",\"");
        testScriptFailure("function a(b, ...c,) {}", 18, String.format(ErrorMessages.UNEXPECTED_TOKEN,","));
        testScriptFailure("(function (,) {})", 11, "Unexpected token \",\"");
        testScriptFailure("(function (a,,) {})", 13, "Unexpected token \",\"");
        testScriptFailure("(function (a, ...b,) {})", 18, String.format(ErrorMessages.UNEXPECTED_TOKEN,","));
        testScriptFailure("({ a (,) {} })", 6, "Unexpected token \",\"");
        testScriptFailure("({ a (b,,) {} })", 8, "Unexpected token \",\"");
        testScriptFailure("({ a (b, ...c,) {} })", 13, String.format(ErrorMessages.UNEXPECTED_TOKEN,","));
        testScriptFailure("({ set a (b,) {} })", 11, "Unexpected token \",\"");
        testScriptFailure("(a,)", 4, "Unexpected end of input");
        testScriptFailure("({a:1},)", 7, "Unexpected token \")\"");
    }

}
