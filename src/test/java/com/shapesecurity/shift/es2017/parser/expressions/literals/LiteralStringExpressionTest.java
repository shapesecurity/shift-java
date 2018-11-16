package com.shapesecurity.shift.es2017.parser.expressions.literals;

import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class LiteralStringExpressionTest extends ParserTestCase {

    @Test
    public void testLiteralStringExpression() throws JsError {
        testScript("('x')", new LiteralStringExpression("x"));
        testScript("('\\\\\\'')", new LiteralStringExpression("\\'"));
        testScript("(\"x\")", new LiteralStringExpression("x"));
        testScript("(\"\\\\\\\"\")", new LiteralStringExpression("\\\""));
        testScript("('\\\r')", new LiteralStringExpression(""));
        testScript("('\\\r\n')", new LiteralStringExpression(""));
        testScript("('\\\n')", new LiteralStringExpression(""));
        testScript("('\\\u2028')", new LiteralStringExpression(""));
        testScript("('\\\u2029')", new LiteralStringExpression(""));
        testScript("('\u202a')", new LiteralStringExpression("\u202A"));
        testScript("('\\0')", new LiteralStringExpression("\0"));
        testScript("'use strict'; ('\\0')", new LiteralStringExpression("\0"));
        testScript("'use strict'; ('\\0x')", new LiteralStringExpression("\0" + "x"));
        testScript("('\\11')", new LiteralStringExpression("\t"));
        testScript("('\\111')", new LiteralStringExpression("I"));
        testScript("('\\1111')", new LiteralStringExpression("I1"));
        testScript("('\\5111')", new LiteralStringExpression(")11"));
        testScript("('\\a')", new LiteralStringExpression("a"));
        testScript("('\\`')", new LiteralStringExpression("`"));
        testScript("('\\u{0}')", new LiteralStringExpression("\0"));
        testScript("('\\u{10FFFF}')", new LiteralStringExpression("\uDBFF\uDFFF"));

        testScript("('\\01')", new LiteralStringExpression("\u0001"));
        testScript("('\\1')", new LiteralStringExpression("\u0001"));
        testScript("('\\2111')", new LiteralStringExpression("\u00891"));
        testScript("('\\5a')", new LiteralStringExpression("\u0005a"));
        testScript("('\\7a')", new LiteralStringExpression("\u0007a"));
        testScript("('\\u{00F8}')", new LiteralStringExpression("\u00F8"));
        testScript("('\\u{0000000000F8}')", new LiteralStringExpression("\u00F8"));


        testScriptFailure("'", 1, "Unexpected end of input");
        testScriptFailure("\"", 1, "Unexpected end of input");
        testScriptFailure("(')", 3, "Unexpected end of input");
        testScriptFailure("(\")", 3, "Unexpected end of input");
        testScriptFailure("('\\x')", 4, "Unexpected \"'\"");
        testScriptFailure("('\\u')", 4, "Unexpected \"'\"");
        testScriptFailure("('\\8')", 3, "Unexpected \"8\"");
        testScriptFailure("('\\9')", 3, "Unexpected \"9\"");
        testScriptFailure("('\\x0')", 4, "Unexpected \"0\"");
        testScriptFailure("('\u2028')", 2, "Unexpected \"\\u2028\"");
        testScriptFailure("('\u2029')", 2, "Unexpected \"\\u2029\"");
        testScriptFailure("('\\u{2028')", 4, "Unexpected \"{\"");
        testScriptFailure("(\"\\u{110000}\")", 4, "Unexpected \"{\"");
        testScriptFailure("(\"\\u{FFFFFFF}\")", 4, "Unexpected \"{\"");
        testScriptFailure("'use strict'; ('\\1')", 15, "Unexpected legacy octal escape sequence: \\1");
        testScriptFailure("'use strict'; ('\\4')", 15, "Unexpected legacy octal escape sequence: \\4");
        testScriptFailure("'use strict'; ('\\11')", 15, "Unexpected legacy octal escape sequence: \\11");
        testScriptFailure("'use strict'; ('\\41')", 15, "Unexpected legacy octal escape sequence: \\41");
        testScriptFailure("'use strict'; ('\\01')", 15, "Unexpected legacy octal escape sequence: \\01");
        testScriptFailure("'use strict'; ('\\00')", 15, "Unexpected legacy octal escape sequence: \\00");
        testScriptFailure("'use strict'; ('\\001')", 15, "Unexpected legacy octal escape sequence: \\001");
        testScriptFailure("'use strict'; ('\\000')", 15, "Unexpected legacy octal escape sequence: \\000");
        testScriptFailure("'use strict'; ('\\123')", 15, "Unexpected legacy octal escape sequence: \\123");

        testScriptFailure("('\n')", 2, "Unexpected \"\\n\"");
    }
}
