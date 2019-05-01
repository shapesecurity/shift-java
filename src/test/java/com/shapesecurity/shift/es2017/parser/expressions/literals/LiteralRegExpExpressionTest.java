package com.shapesecurity.shift.es2017.parser.expressions.literals;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import com.shapesecurity.shift.es2017.parser.PatternAcceptor;
import org.junit.Test;

public class LiteralRegExpExpressionTest extends ParserTestCase {

    private static final String[] expectedToPass = new String[] {
        "/(?!t|v|X|.|$||)*/",
        "/(?!t|v|X|.|$||)/",
        "/(?!t|v|X|.|$||)/u",
        "/(?:)/",
        "/(?:X)/",
        "/(?=t|v|X|.|$||)*/",
        "/(?=t|v|X|.|$||)/",
        "/(?=t|v|X|.|$||)/u",
        "/./",
        "/.|./",
        "/.||./",
        "/X$/",
        "/X*/",
        "/X*?/",
        "/X+/",
        "/X+?/",
        "/X?/",
        "/X??/",
        "/X{5,10}/",
        "/X{5,10}?/",
        "/X{5,}/",
        "/X{5,}?/",
        "/X{5}/",
        "/X{5}?/",
        "/[-X]/",
        "/[-X]/u",
        "/[0-9-a-]/",
        "/[0-9-a-]/u",
        "/[0-9-a-z-]/",
        "/[0-9-a-z-]/u",
        "/[0-9-a-z]/",
        "/[0-9-a-z]/u",
        "/[X-]/",
        "/[X-]/u",
        "/[X]/",
        "/[X]/u",
        "/[\\-]/",
        "/[\\-]/u",
        "/[\\1-\\2]/",
        "/[\\123]/",
        "/[\\126-\\127]/",
        "/[\\128-9]/",
        "/[\\153]/",
        "/[\\15]/",
        "/[\\1]/",
        "/[\\5]/",
        "/[\\72]/",
        "/[\\7]/",
        "/[\\9]/",
        "/[\\D]/",
        "/[\\S]/",
        "/[\\W]/",
        "/[\\\\5]/",
        "/[\\_]/",
        "/[\\b]/",
        "/[\\b]/u",
        "/[\\c0-\\c9]/",
        "/[\\c10]/",
        "/[\\c5]/",
        "/[\\c]/",
        "/[\\ca]/",
        "/[\\ca]/u",
        "/[\\s-X]/",
        "/[\\s]/",
        "/[\\u{1F4A9}-\\u{1F4AB}]/u",
        "/[\\w]/",
        "/[]/",
        "/[]/u",
        "/[^-X]/",
        "/[^-X]/u",
        "/[^0-9-a-]/",
        "/[^0-9-a-]/u",
        "/[^0-9-a-z-]/",
        "/[^0-9-a-z-]/u",
        "/[^0-9-a-z]/",
        "/[^0-9-a-z]/u",
        "/[^X-]/",
        "/[^X-]/u",
        "/[^X]/",
        "/[^X]/u",
        "/[^]/",
        "/[^]/u",
        "/[{}[||)(()\\]?+*.$^]/",
        "/[{}[||)(()\\]?+*.$^]/u",
        "/[💩-💫]/u",
        "/\\$/",
        "/\\$/u",
        "/\\0/",
        "/\\0/u",
        "/\\1()/",
        "/\\1()/u",
        "/\\123/",
        "/\\2()()/u",
        "/\\2()/",
        "/\\2/",
        "/\\BX/",
        "/\\D/",
        "/\\D/u",
        "/\\L/",
        "/\\S/",
        "/\\S/u",
        "/\\W/",
        "/\\W/u",
        "/\\bX/",
        "/\\c/",
        "/\\cZ/",
        "/\\ca/",
        "/\\d/",
        "/\\d/u",
        "/\\d]/",
        "/\\f/",
        "/\\k/",
        "/\\n/",
        "/\\r/",
        "/\\s/",
        "/\\s/u",
        "/\\t/",
        "/\\u10AB/",
        "/\\u10AB/u",
        "/\\uD800/u",
        "/\\uD800\\uDF00/u",
        "/\\uDF00/u",
        "/\\ud800\\u1000/u",
        "/\\u{001AD}/u",
        "/\\u{0}/u",
        "/\\u{10FFFF}/u",
        "/\\u{10}/u",
        "/\\v/",
        "/\\w/",
        "/\\w/u",
        "/\\x0F/",
        "/\\xAA/",
        "/\\xZZ/",
        "/]*/",
        "/^$\\b\\B/",
        "/^X/",
        "/t{5/",
        "/{5,10X}/",
        "/{5,X}/",
        "/{5.}/",
        "/{dfwfdf}/",
        "/|.||.|/",
        "/|/",
        "/}*/",
        "/[\\99-\\98]/",
        "/[\\99-\\100]/",
    };

    private static final String[] expectedToFail = new String[] {
        "/(?!t|v|X|.|$||)*/u",
        "/(?:)\\1/u",
        "/(?<!t|v|X|.|$||)*/",
        "/(?<!t|v|X|.|$||)*/u",
        "/(?<!t|v|X|.|$||)/",
        "/(?<!t|v|X|.|$||)/u",
        "/(?<=t|v|X|.|$||)*/",
        "/(?<=t|v|X|.|$||)*/u",
        "/(?<=t|v|X|.|$||)/",
        "/(?<=t|v|X|.|$||)/u",
        "/(?<X>)(?<X>)/",
        "/(?<\\\">)/",
        "/(?<a>a)\\k/",
        "/(?<a>a)\\k</",
        "/(?<a>a)\\k<a/",
        "/(?<a>a)\\k<x>/",
        "/(?<test>)/",
        "/(?=t|v|X|.|$||)*/u",
        "/5{5,1G}/u",
        "/X{10,5}/",
        "/X{10,5}?/",
        "/[/",
        "/[\\123]/u",
        "/[\\127-\\126]/",
        "/[\\127-\\1]/",
        "/[\\1]/u",
        "/[\\2-\\1]/",
        "/[\\9]/u",
        "/[\\_]/u",
        "/[\\c10]/u",
        "/[\\c1]/u",
        "/[\\c9-\\c0]/",
        "/[\\c]/u",
        "/[\\s-X]/u",
        "/[\\u{1F4AB}-\\u{1F4A9}]/u",
        "/[b-a]/",
        "/[🌷-🌸]/",
        "/[💫-💩]/u",
        "/\\1/u",
        "/\\123/u",
        "/\\2/u",
        "/\\L/u",
        "/\\P{ASCIIII}/u",
        "/\\P{gcc=LCC}/u",
        "/\\c/u",
        "/\\k/u",
        "/\\k<X>/u",
        "/\\p{ASCIIII}/u",
        "/\\p{Ahom}/u",
        "/\\p{Script_Extensions}/u",
        "/\\p{gcc=LCC}/u",
        "/\\p{gc}/u",
        "/\\uZZ/u",
        "/\\ud800\\uZZ/u",
        "/\\u{110FFFF}/u",
        "/\\u{ZZ}/u",
        "/\\xZZ/u",
        "/]*/u",
        "/{5,10X}/u",
        "/{5,10}/",
        "/{5,10}/u",
        "/{5,X}/u",
        "/{5.}/u",
        "/{dfwfdf}/u",
        "/}*/u",
        "/[\\100-\\99]/",
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

        assertTrue(PatternAcceptor.acceptRegex("]", false));

        ImmutableList<String> failures = ImmutableList.empty();
        for (String regex : expectedToPass) {
            try {
                testScript(regex);
            } catch (JsError e) {
                failures = failures.cons(regex);
            }
        }

        if (failures.length > 0) {
            throw new RuntimeException("Regexps failed and should not have:" + failures.foldRight((str, acc) -> acc + "\n" + str, ""));
        }

        ImmutableList<String> passes = ImmutableList.empty();

        for (String regex : expectedToFail) {
            try {
                testScript(regex);
                passes = passes.cons(regex);
            } catch (JsError ignored) {
            }
        }

        if (passes.length > 0) {
            throw new RuntimeException("Regexps passed and should not have:" + passes.foldRight((str, acc) -> acc + "\n" + str, ""));
        }
    }
}
