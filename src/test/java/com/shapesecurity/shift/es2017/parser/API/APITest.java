package com.shapesecurity.shift.es2017.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation.Comment.Type;
import org.junit.Test;

public class APITest extends ParserTestCase {
    @Test
    public void testAPI() throws JsError {

    }

    @Test
    public void testScriptForComments() throws JsError {
        String source = "\n" +
                "// a comment\n" +
                "'//not a comment';\n" +
                "--> a comment\n" +
                "a + /* a comment\n" +
                "*/ <!-- a comment\n" +
                "`/*not a comment*/ ${ 0/* a template comment */ } `\n" +
                "// a comment"; // That this does not have a trailing linebreak is important.
        ParserWithLocation parser = new ParserWithLocation();
        parser.parseScript(source);

        ImmutableList<ParserWithLocation.Comment> comments = parser.getComments();

        assertTrue(comments.map(c -> source.substring(c.start.offset, c.end.offset)).equals(ImmutableList.of(
                "// a comment\n",
                "--> a comment\n",
                "/* a comment\n*/",
                "<!-- a comment\n",
                "/* a template comment */",
                "// a comment"
        )));

        assertTrue(comments.map(c -> c.type).equals(ImmutableList.of(
                Type.SingleLine,
                Type.HTMLClose,
                Type.MultiLine,
                Type.HTMLOpen,
                Type.MultiLine,
                Type.SingleLine
        )));

        assertTrue(comments.map(c -> c.text).equals(ImmutableList.of(
                " a comment",
                " a comment",
                " a comment\n",
                " a comment",
                " a template comment ",
                " a comment"
        )));
    }

    @Test
    public void testModuleForComments() throws JsError {
        String source = "\n" +
                "// a comment\n" +
                "'// not a comment';\n" +
                "a <!-- b\n"; // Note that '<!-- b' is *not* a comment.
        ParserWithLocation parser = new ParserWithLocation();
        parser.parseModule(source);

        ImmutableList<ParserWithLocation.Comment> comments = parser.getComments();

        assertTrue(comments.map(c -> source.substring(c.start.offset, c.end.offset)).equals(ImmutableList.of(
                "// a comment\n"
        )));

        assertTrue(comments.map(c -> c.type).equals(ImmutableList.of(
                Type.SingleLine
        )));

        assertTrue(comments.map(c -> c.text).equals(ImmutableList.of(
                " a comment"
        )));
    }
}
