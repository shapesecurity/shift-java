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
//    testScript("(\"\\\\\\\"\")", );
//    testScript("('\\\r')", );
//    testScript("('\\\r\n')", );
//    testScript("('\\\n')", );
//    testScript("('\\\u2028')", );
//    testScript("('\\\u2029')", );
//    testScript("('\u202a')", );
//    testScript("('\\0')", );
//    testScript("'use strict'; ('\\0')", );
//    testScript("'use strict'; ('\\0x')", );
//    testScript("('\\01')", );
//    testScript("('\\1')", );
//    testScript("('\\11')", );
//    testScript("('\\111')", );
//    testScript("('\\1111')", );
//    testScript("('\\2111')", );
//    testScript("('\\5111')", );
//    testScript("('\\5a')", );
//    testScript("('\\7a')", );
//    testScript("('\\a')", );
//    testScript("('\\`')", );
//    testScript("('\\u{00F8}')", );
//    testScript("('\\u{0}')", );
//    testScript("('\\u{10FFFF}')", );
//    testScript("('\\u{0000000000F8}')", );
//
//    testParseFailure("(')", "Unexpected end of input");
//    testParseFailure("('\n')", "Unexpected \"\\n\"");
//    testParseFailure("('\\x')", "Unexpected \"'\"");
//    testParseFailure("('\\u')", "Unexpected \"'\"");
//    testParseFailure("('\\8')", "Unexpected \"8\"");
//    testParseFailure("('\\9')", "Unexpected \"9\"");
//    testParseFailure("('\\x0')", "Unexpected \"0\"");
//    testParseFailure("('\u2028')", "Unexpected \"\u2028\"");
//    testParseFailure("('\u2029')", "Unexpected \"\u2029\"");
//    testParseFailure("('\\u{2028')", "Unexpected \"{\"");
//
//    // early grammar error: 11.8.4.1
//    // It is a Syntax Error if the MV of HexDigits > 1114111.
//    testParseFailure("(\"\\u{110000}\")", "Unexpected \"{\"");
//    testParseFailure("(\"\\u{FFFFFFF}\")", "Unexpected \"{\"");
  }
}
