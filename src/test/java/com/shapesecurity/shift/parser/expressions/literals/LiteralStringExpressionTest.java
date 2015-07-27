package com.shapesecurity.shift.parser.expressions.literals;

import com.shapesecurity.shift.ast.LiteralStringExpression;
import com.shapesecurity.shift.ast.ThisExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserTestCase;
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
    testScript("'use strict'; ('\\0x')", new LiteralStringExpression("\0x"));
    testScript("('\\11')", new LiteralStringExpression("\t"));
    testScript("('\\111')", new LiteralStringExpression("I"));
    testScript("('\\1111')", new LiteralStringExpression("I1"));
    testScript("('\\5111')", new LiteralStringExpression(")11"));
    testScript("('\\a')", new LiteralStringExpression("a"));
    testScript("('\\`')", new LiteralStringExpression("`"));
    testScript("('\\u{0}')", new LiteralStringExpression("\0"));
    testScript("('\\u{10FFFF}')", new LiteralStringExpression("\uDBFF\uDFFF"));

    testScript("('\\01')", new LiteralStringExpression("\\x01"));
    testScript("('\\1')", new LiteralStringExpression("\\x01"));
    testScript("('\\2111')", new LiteralStringExpression("\\x891"));
    testScript("('\\5a')", new LiteralStringExpression("\\x05a"));
    testScript("('\\7a')", new LiteralStringExpression("\\x07a"));
    testScript("('\\u{00F8}')", new LiteralStringExpression("\\xF8"));
    testScript("('\\u{0000000000F8}')", new LiteralStringExpression("\\xF8"));


    testScriptFailure("(')", 3, "Unexpected end of input");
    testScriptFailure("('\\x')", 4, "Unexpected \"'\"");
    testScriptFailure("('\\u')", 4, "Unexpected \"'\"");
    testScriptFailure("('\\8')", 3, "Unexpected \"8\"");
    testScriptFailure("('\\9')", 3, "Unexpected \"9\"");
    testScriptFailure("('\\x0')", 4, "Unexpected \"0\"");
    testScriptFailure("('\u2028')", 2, "Unexpected \"\u2028\"");
    testScriptFailure("('\u2029')", 2, "Unexpected \"\u2029\"");
    testScriptFailure("('\\u{2028')", 4, "Unexpected \"{\"");
    testScriptFailure("(\"\\u{110000}\")", 4, "Unexpected \"{\"");
    testScriptFailure("(\"\\u{FFFFFFF}\")", 4, "Unexpected \"{\"");

//    testScriptFailure("('\n')", 0, "Unexpected \"\\n\"");

  }
}
