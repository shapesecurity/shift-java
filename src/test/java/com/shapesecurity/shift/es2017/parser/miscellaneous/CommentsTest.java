package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

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
        testScript("0/*\n*/--> a comment");
    }
}
