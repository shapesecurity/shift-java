package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebSafeCodeGenTest {
	private static void test(String expected, String source) throws JsError {
        Module module = Parser.parseModule(source);
		String code = WebSafeCodeGen.codeGen(module);
		assertEquals(expected, code);
		assertEquals(module, Parser.parseModule(code));
	}
	private static void testWithoutEq(String expected, String source) throws JsError {
		Module module = Parser.parseModule(source);
		String code = WebSafeCodeGen.codeGen(module);
		assertEquals(expected, code);
	}

	@Test
	public void testNullByte() throws JsError {
		test("(\"\\x00\")", "(\"\\0\")");
		test("(\"\\x00\")", "(\"\0\")");
		testWithoutEq("\"\\x00\"", "\"\0\"");
		testWithoutEq("/\\x00/", "/\0/");
		testWithoutEq("`\\x00`", "`\0`");
		testWithoutEq("`\\x00${0}\\x00${0}\\x00`", "`\0${0}\0${0}\0`");
		testWithoutEq("tag`\\x00`", "tag`\0`");
		testWithoutEq("tag`\\x00${0}\\x00${0}\\x00`", "tag`\0${0}\0${0}\0`");
	}

	@Test
	public void testScriptTag() throws JsError {
		test("a< script", "a<script ");
		test("a< /script/", "a</script/");
		test("a<< script", "a<<script ");
		test("a<< /script/", "a<</script/");
		test("(\"<\\x73cript \")", "(\"<script \")");
		test("(\"</\\x73cript \")", "(\"</script \")");
		testWithoutEq("\"<\\x73cript \"", "\"<script \"");
		testWithoutEq("\"</\\x73cript \"", "\"</script \"");
		testWithoutEq("/<\\x73cript/", "/<script/");
		testWithoutEq("/[</\\x73cript ]/", "/[</script ]/");
		testWithoutEq("`<\\x73cript `", "`<script `");
		testWithoutEq("`<\\x73cript ${0}<\\x73cript ${0}<\\x73cript `", "`<script ${0}<script ${0}<script `");
		testWithoutEq("`</\\x73cript `", "`</script `");
		testWithoutEq("`</\\x73cript ${0}</\\x73cript ${0}</\\x73cript `", "`</script ${0}</script ${0}</script `");
		testWithoutEq("tag`<\\x73cript `", "tag`<script `");
		testWithoutEq("tag`<\\x73cript ${0}<\\x73cript ${0}<\\x73cript `", "tag`<script ${0}<script ${0}<script `");
		testWithoutEq("tag`</\\x73cript `", "tag`</script `");
		testWithoutEq("tag`</\\x73cript ${0}</\\x73cript ${0}</\\x73cript `", "tag`</script ${0}</script ${0}</script `");
	}

	@Test
	public void testNonAscii() throws JsError {
		test("\\u03C6", "φ");
		test("\\u03C6\\u03C6\\u03C6", "φφφ");
		test("abc\\u03C6xyz", "abcφxyz");
		test("let \\u03C6", "let φ");
		test("\\u03C6=0", "φ = 0");
		test("(\"\\u03C6\")", "(\"φ\")");
		test("(\"\\uD835\\uDD42\")", "(\"\\uD835\\uDD42\")");
		test("(\"\\uD835\\uDD42\")", "(\"\uD835\uDD42\")");
		testWithoutEq("\"\\u03C6\"", "\"φ\"");
		testWithoutEq("/\\u03C6/", "/φ/");
		testWithoutEq("`\\u03C6`", "`φ`");
		testWithoutEq("`\\u03C6${0}\\u03C6${0}\\u03C6`", "`φ${0}φ${0}φ`");
		testWithoutEq("tag`\\u03C6`", "tag`φ`");
		testWithoutEq("tag`\\u03C6${0}\\u03C6${0}\\u03C6`", "tag`φ${0}φ${0}φ`");
	}
}
