package com.shapesecurity.shift.parser.expressions.literals;

import com.shapesecurity.shift.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.ast.StaticMemberExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserTestCase;

import org.junit.Test;

public class LiteralRegExpExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralRegExpExpressionTest() throws JsError {
        testScript("/a/", new LiteralRegExpExpression("a", ""));
        testScript("/\\0/", new LiteralRegExpExpression("\\0", ""));
        testScript("/\\1/u", new LiteralRegExpExpression("\\1", "u"));
        testScript("/a/;", new LiteralRegExpExpression("a", ""));
        testScript("/a/i", new LiteralRegExpExpression("a", "i"));
        testScript("/a/i;", new LiteralRegExpExpression("a", "i"));
        testScript("/[--]/", new LiteralRegExpExpression("[--]", ""));
        testScript("/[a-z]/i", new LiteralRegExpExpression("[a-z]", "i"));
        testScript("/[x-z]/i", new LiteralRegExpExpression("[x-z]", "i"));
        testScript("/[a-c]/i", new LiteralRegExpExpression("[a-c]", "i"));
        testScript("/[P QR]/i", new LiteralRegExpExpression("[P QR]", "i"));
        testScript("/[\\]/]/", new LiteralRegExpExpression("[\\]/]", ""));
        testScript("/foo\\/bar/", new LiteralRegExpExpression("foo\\/bar", ""));
        testScript("/=([^=\\s])+/g", new LiteralRegExpExpression("=([^=\\s])+", "g"));
        testScript("/(()(?:\\2)((\\4)))/;", new LiteralRegExpExpression("(()(?:\\2)((\\4)))", ""));
        testScript("/((((((((((((.))))))))))))\\12/;", new LiteralRegExpExpression("((((((((((((.))))))))))))\\12", ""));
        testScript("/\\.\\/\\\\/u", new LiteralRegExpExpression("\\.\\/\\\\", "u"));
        testScript("/\\uD834\\uDF06\\u{1d306}/u", new LiteralRegExpExpression("\\uD834\\uDF06\\u{1d306}", "u"));
        testScript("/\\uD834/u", new LiteralRegExpExpression("\\uD834", "u"));
        testScript("/\\uDF06/u", new LiteralRegExpExpression("\\uDF06", "u"));
        testScript("/[-a-]/", new LiteralRegExpExpression("[-a-]", ""));
        testScript("/[-\\-]/u", new LiteralRegExpExpression("[-\\-]", "u"));
        testScript("/[-a-b-]/", new LiteralRegExpExpression("[-a-b-]", ""));
        testScript("/[]/", new LiteralRegExpExpression("[]", ""));

        testScript("/0/g.test", new StaticMemberExpression("test", new LiteralRegExpExpression("0", "g")));

        testScript("/{/;", new LiteralRegExpExpression("{", ""));
        testScript("/}/;", new LiteralRegExpExpression("}", ""));
        testScript("/}?/u;", new LiteralRegExpExpression("}?", "u"));
        testScript("/{*/u;", new LiteralRegExpExpression("{*", "u"));
        testScript("/{}/;", new LiteralRegExpExpression("{}", ""));
        testScript("/.{.}/;", new LiteralRegExpExpression(".{.}", ""));
        testScript("/[\\w-\\s]/;", new LiteralRegExpExpression("[\\w-\\s]", ""));
        testScript("/[\\s-\\w]/;", new LiteralRegExpExpression("[\\s-\\w]", ""));
        testScript("/(?=.)*/;", new LiteralRegExpExpression("(?=.)*", ""));
        testScript("/(?!.){0,}?/;", new LiteralRegExpExpression("(?!.){0,}?", ""));
        testScript("/(?!.){0,}?/u", new LiteralRegExpExpression("(?!.){0,}?", "u"));
    }
}
