package com.shapesecurity.shift.es2016.parser.expressions.literals;

import com.shapesecurity.shift.es2016.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import com.shapesecurity.shift.es2016.parser.PatternAcceptor;
import org.junit.Test;

import javax.annotation.Nonnull;

public class LiteralRegExpExpressionTest extends ParserTestCase {

    private static final String[] expectedToPass = new String[] {
            "/./",
            "/.|./",
            "/.||./",
            "/|/",
            "/|.||.|/",
            "/^$\\b\\B/",
            "/^X/",
            "/X$/",
            "/\\bX/",
            "/\\BX/",
            "/(?=t|v|X|.|$||)/",
            "/(?!t|v|X|.|$||)/",
            "/(?<=t|v|X|.|$||)/",
            "/(?<!t|v|X|.|$||)/",
            "/(?=t|v|X|.|$||)/u",
            "/(?!t|v|X|.|$||)/u",
            "/(?<=t|v|X|.|$||)/u",
            "/(?<!t|v|X|.|$||)/u",
            "/(?=t|v|X|.|$||)*/",
            "/(?!t|v|X|.|$||)*/",
            "/X*/",
            "/X+/",
            "/X?/",
            "/X*?/",
            "/X+?/",
            "/X??/",
            "/X{5}/",
            "/X{5,}/",
            "/X{5,10}/",
            "/X{5}?/",
            "/X{5,}?/",
            "/X{5,10}?/",
            "/./",
            "/\\123/",
            "/\\0/",
            "/\\0/u",
            "/\\1()/",
            "/\\1()/u",
            "/\\2()/",
            "/\\2()()/u",
            "/\\d/",
            "/\\D/",
            "/\\s/",
            "/\\S/",
            "/\\w/",
            "/\\W/",
            "/\\d/u",
            "/\\D/u",
            "/\\s/u",
            "/\\S/u",
            "/\\w/u",
            "/\\W/u",
            "/[]/",
            "/[^]/",
            "/[X]/",
            "/[^X]/",
            "/[-X]/",
            "/[^-X]/",
            "/[X-]/",
            "/[^X-]/",
            "/[0-9-a-]/",
            "/[^0-9-a-]/",
            "/[0-9-a-z]/",
            "/[^0-9-a-z]/",
            "/[0-9-a-z-]/",
            "/[^0-9-a-z-]/",
            "/[]/u",
            "/[^]/u",
            "/[X]/u",
            "/[^X]/u",
            "/[-X]/u",
            "/[^-X]/u",
            "/[X-]/u",
            "/[^X-]/u",
            "/[0-9-a-]/u",
            "/[^0-9-a-]/u",
            "/[0-9-a-z]/u",
            "/[^0-9-a-z]/u",
            "/[0-9-a-z-]/u",
            "/[^0-9-a-z-]/u",
            "/[{}[||)(()\\]?+*.$^]/",
            "/[{}[||)(()\\]?+*.$^]/u",
            "/[\\b]/",
            "/[\\b]/u",
            "/\\d]/",
            "/[\\D]/",
            "/[\\s]/",
            "/[\\S]/",
            "/[\\w]/",
            "/[\\W]/",
            "/\\f/",
            "/\\n/",
            "/\\r/",
            "/\\t/",
            "/\\v/",
            "/\\ca/",
            "/\\cZ/",
            "/\\xAA/",
            "/\\xZZ/",
            "/\\x0F/",
            "/\\u10AB/",
            "/\\u10AB/u",
            "/\\uD800/u",
            "/\\uDF00/u",
            "/\\uD800\\uDF00/u",
            "/\\u{001AD}/u",
            "/\\u{10FFFF}/u",
            "/\\u{0}/u",
            "/\\L/",
            "/\\$/",
            "/\\$/u",
            "/[\\s-X]/",
            "/{dfwfdf}/",
            "/{5.}/",
            "/{5,X}/",
            "/{5,10X}/",
            "/\\c/",
            "/[\\c]/",
            "/[\\c]/u",
            "/[\\5]/",
            "/(?:)/",
            "/(?:X)/",
            "/}*/",
            "/]*/",
            "/[\\123]/",
            "/[\\_]/",
            "/[\\1]/",
            "/[\\9]/",
            "/[\\-]/u",
            "/[\\-]/",
            "/(?<test>)\\k<test>/",
            "/\\ud800\\u1000/u",
            "/\\u{10}/u",
            "/[\\1]/",
            "/[\\7]/",
            "/[\\15]/",
            "/[\\153]/",
            "/[\\72]/"
    };

    private static final String[] expectedToFail = new String[] {
            "/(?=t|v|X|.|$||)*/u",
            "/(?!t|v|X|.|$||)*/u",
            "/(?<=t|v|X|.|$||)*/",
            "/(?<!t|v|X|.|$||)*/",
            "/(?<=t|v|X|.|$||)*/u",
            "/(?<!t|v|X|.|$||)*/u",
            "/X{10,5}/",
            "/X{10,5}?/",
            "/\\123/u",
            "/\\1/u",
            "/\\2/u",
            "/\\u{110FFFF}/u",
            "/\\L/u",
            "/[b-a]/",
            "/[\\s-X]/u",
            "/{dfwfdf}/u",
            "/{5,10}/",
            "/{5,10}/u",
            "/{5.}/u",
            "/{5,X}/u",
            "/{5,10X}/u",
            "/(?:)\\1/u",
            "/}*/u",
            "/]*/u",
            "/[\\123]/u",
            "/[\\_]/u",
            "/[\\1]/u",
            "/[\\9]/u",
            "/\\c/u",
            "/(?<\">)/",
            "/(?<test>)(?<test>)/",
            "/\\k<\">/",
            "/\\k<f>/",
            "/\\xZZ/u",
            "/\\ud800\\uZZ/u",
            "/\\uZZ/u",
            "/\\u{ZZ}/u",
            "/5{5,1G}/u"
    };

    @Test
    public void testLiteralRegExpExpressionTest() throws JsError {
        testScript("/a/", new LiteralRegExpExpression("a", false, false, false, false, false));
        testScript("/\\0/", new LiteralRegExpExpression("\\0", false, false, false, false, false));
        testScript("/\\1()/u", new LiteralRegExpExpression("\\1()", false, false, false, false, true));
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
        testScriptFailure("/}?/u;", 5, "Invalid regular expression");
        testScriptFailure("/{*/u;", 5, "Invalid regular expression");
        testScript("/{}/;", new LiteralRegExpExpression("{}", false, false, false, false, false));
        testScript("/.{.}/;", new LiteralRegExpExpression(".{.}", false, false, false, false, false));
        testScript("/[\\w-\\s]/;", new LiteralRegExpExpression("[\\w-\\s]", false, false, false, false, false));
        testScript("/[\\s-\\w]/;", new LiteralRegExpExpression("[\\s-\\w]", false, false, false, false, false));
        testScript("/(?=.)*/;", new LiteralRegExpExpression("(?=.)*", false, false, false, false, false));
        testScript("/(?!.){0,}?/;", new LiteralRegExpExpression("(?!.){0,}?", false, false, false, false, false));
        testScriptFailure("/(?!.){0,}?/u", 13, "Invalid regular expression");

        assertTrue(PatternAcceptor.acceptRegex("]", false, false, false, false, false));


        for (String regex : expectedToPass) {
            testScript(regex);
        }

        for (String regex : expectedToFail) {
            testScriptFailure(regex, "Invalid regular expression");
        }
    }
}
