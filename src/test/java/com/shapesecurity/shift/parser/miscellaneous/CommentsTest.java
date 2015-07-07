package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class CommentsTest extends ParserTestCase {
  @Test
  public void testComments() throws JsError {
    testScript(" /**/");
    testScript(" /****/");
    testScript(" /**\n\r\r\n**/");
    testScript(" //\n");
    testScript("<!-- foo");
    testScript("--> comment");
    testScript("<!-- comment");
    testScript(" \t --> comment");
    testScript(" \t /* block comment */  --> comment");
    testScript("/* block comment */--> comment");
  }
}
