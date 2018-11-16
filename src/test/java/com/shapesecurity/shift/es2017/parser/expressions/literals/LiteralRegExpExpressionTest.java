package com.shapesecurity.shift.es2017.parser.expressions.literals;

import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class LiteralRegExpExpressionTest extends ParserTestCase {
    @Test
    public void testLiteralRegExpExpressionTest() throws JsError {
        testScript("/a/", new LiteralRegExpExpression("a", false, false, false, false, false));
        testScript("/\\0/", new LiteralRegExpExpression("\\0", false, false, false, false, false));
        testScript("/\\1/u", new LiteralRegExpExpression("\\1", false, false, false, false, true));
        testScript("/a/;", new LiteralRegExpExpression("a", false, false, false, false, false));
        testScript("/a/i", new LiteralRegExpExpression("a", false, true, false, false, false));
        testScript("/a/i;", new LiteralRegExpExpression("a", false, true, false, false, false));
        testScript("/[--]/", new LiteralRegExpExpression("[--]", false, false, false, false, false));
        testScript("/[a-z]/i", new LiteralRegExpExpression("[a-z]", false, true, false, false, false));
        testScript("/[x-z]/i", new LiteralRegExpExpression("[x-z]", false, true, false, false, false));
        testScript("/[a-c]/i", new LiteralRegExpExpression("[a-c]", false, true, false, false, false));
        testScript("/[P QR]/i", new LiteralRegExpExpression("[P QR]", false, true, false, false, false));
        testScript("/[\\]/]/", new LiteralRegExpExpression("[\\]/]", false, false, false, false, false));
        testScript("/foo\\/bar/", new LiteralRegExpExpression("foo\\/bar", false, false, false, false, false));
        testScript("/=([^=\\s])+/g", new LiteralRegExpExpression("=([^=\\s])+", true, false, false, false, false));
        testScript("/(()(?:\\2)((\\4)))/;", new LiteralRegExpExpression("(()(?:\\2)((\\4)))", false, false, false, false, false));
        testScript("/((((((((((((.))))))))))))\\12/;", new LiteralRegExpExpression("((((((((((((.))))))))))))\\12", false, false, false, false, false));
        testScript("/\\.\\/\\\\/u", new LiteralRegExpExpression("\\.\\/\\\\", false, false, false, false, true));
        testScript("/\\uD834\\uDF06\\u{1d306}/u", new LiteralRegExpExpression("\\uD834\\uDF06\\u{1d306}", false, false, false, false, true));
        testScript("/\\uD834/u", new LiteralRegExpExpression("\\uD834", false, false, false, false, true));
        testScript("/\\uDF06/u", new LiteralRegExpExpression("\\uDF06", false, false, false, false, true));
        testScript("/[-a-]/", new LiteralRegExpExpression("[-a-]", false, false, false, false, false));
        testScript("/[-\\-]/u", new LiteralRegExpExpression("[-\\-]", false, false, false, false, true));
        testScript("/[-a-b-]/", new LiteralRegExpExpression("[-a-b-]", false, false, false, false, false));
        testScript("/[]/", new LiteralRegExpExpression("[]", false, false, false, false, false));

        testScript("/0/g.test", new StaticMemberExpression(new LiteralRegExpExpression("0", true, false, false, false, false), "test"));

        testScript("/{/;", new LiteralRegExpExpression("{", false, false, false, false, false));
        testScript("/}/;", new LiteralRegExpExpression("}", false, false, false, false, false));
        testScript("/}?/u;", new LiteralRegExpExpression("}?", false, false, false, false, true));
        testScript("/{*/u;", new LiteralRegExpExpression("{*", false, false, false, false, true));
        testScript("/{}/;", new LiteralRegExpExpression("{}", false, false, false, false, false));
        testScript("/.{.}/;", new LiteralRegExpExpression(".{.}", false, false, false, false, false));
        testScript("/[\\w-\\s]/;", new LiteralRegExpExpression("[\\w-\\s]", false, false, false, false, false));
        testScript("/[\\s-\\w]/;", new LiteralRegExpExpression("[\\s-\\w]", false, false, false, false, false));
        testScript("/(?=.)*/;", new LiteralRegExpExpression("(?=.)*", false, false, false, false, false));
        testScript("/(?!.){0,}?/;", new LiteralRegExpExpression("(?!.){0,}?", false, false, false, false, false));
        testScript("/(?!.){0,}?/u", new LiteralRegExpExpression("(?!.){0,}?", false, false, false, false, true));
    }
}
