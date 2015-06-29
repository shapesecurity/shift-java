package com.shapesecurity.shift.parser;

import org.junit.Test;

/**
 * Created by u478 on 6/19/15.
 */
public class FailuresTest extends Assertions {
  @Test
  public void testFailures() throws JsError {
//    testScriptFailure("/*", 0, "Unexpected end of input");
//    testScriptFailure("/*\r", 0, "Unexpected end of input");
//    testScriptFailure("/*\r\n", 0, "Unexpected end of input");
//    testScriptFailure("/*\u2028", 0, "Unexpected end of input");
//    testScriptFailure("/*\u2029", 0, "Unexpected end of input");
//    testScriptFailure("/**", 0, "Unexpected end of input");
//    testScriptFailure("\\", 0, "Unexpected end of input");
//    testScriptFailure("\\u", 0, "Unexpected end of input");
//    testScriptFailure("\\x", 0, "Unexpected \"x\"");
//    testScriptFailure("\\o", 0, "Unexpected \"o\"");
//    testScriptFailure("\\u1", 0, "Unexpected \"1\"");
//    testScriptFailure("\\u12", 0, "Unexpected \"1\"");
//    testScriptFailure("\\u113", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\uz   ", 0, "Unexpected \"z\"");
//    testScriptFailure("a\\u1z  ", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\u11z ", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\u111z", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\", 0, "Unexpected end of input");
//    testScriptFailure("a\\u", 0, "Unexpected end of input");
//    testScriptFailure("a\\x", 0, "Unexpected \"x\"");
//    testScriptFailure("a\\o", 0, "Unexpected \"o\"");
//    testScriptFailure("a\\u1", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\u12", 0, "Unexpected \"1\"");
//    testScriptFailure("a\\u113", 0, "Unexpected \"1\"");
//    testScriptFailure("\\uD800", 0, "Unexpected end of input");
//    testScriptFailure("\\uD800x", 0, "Unexpected \"x\"");
//    testScriptFailure("\\uD800\\", 0, "Unexpected \"\\\\\"");
//    testScriptFailure("\\uD800\\u", 0, "Unexpected \"\\\\\"");
//    testScriptFailure("\\uD800\\x62", 0, "Unexpected \"\\\\\"");
//    testScriptFailure("\uD800", 0, "Unexpected end of input");
//    testScriptFailure("\uD800x", 0, "Unexpected end of input");
//    testScriptFailure("\uD800\\", 0, "Unexpected end of input");
//    testScriptFailure("\uD800\\u", 0, "Unexpected \"u\"");
//    testScriptFailure("\uD800\\x62", 0, "Unexpected \"x\"");
//    testScriptFailure("'\\03", 0, "Unexpected end of input");
//    testScriptFailure("'\\x", 0, "Unexpected end of input");
//    testScriptFailure("'\\x1", 0, "Unexpected \"1\"");
//    testScriptFailure("'\\x1   ", 0, "Unexpected \"1\"");
//    testScriptFailure("'\\x12  ", 0, "Unexpected end of input");
//    testScriptFailure("'\n", 0, "Unexpected \"\\n\"");
//    testScriptFailure("'\\", 0, "Unexpected end of input");
//    testScriptFailure("＊", 0, "Unexpected \"\uFF0A\"");
//    testScriptFailure("1.a", 0, "Unexpected \"a\"");
//    testScriptFailure("1.e", 0, "Unexpected end of input");
//    testScriptFailure("1.e+", 0, "Unexpected end of input");
//    testScriptFailure("1.e+z", 0, "Unexpected \"z\"");
//    testScriptFailure("/\\\n0", 0, "Invalid regular expression: missing /");
//    testScriptFailure("0x", 0, "Unexpected end of input");
//    testScriptFailure("0xz", 0, "Unexpected \"z\"");
//    testScriptFailure("0x1z", 0, "Unexpected \"1\"");
//    testScriptFailure("0a", 0, "Unexpected \"a\"");
//    testScriptFailure("08a", 0, "Unexpected \"a\"");
//    testScriptFailure("\u0008", 0, "Unexpected \"\\b\"");
//    testScriptFailure("{", 0, "Unexpected end of input");
//    testScriptFailure("}", 0, "Unexpected token \"}\"");
//    testScriptFailure("3ea", 0, "Unexpected \"a\"");
//    testScriptFailure("3in []", 0, "Unexpected \"i\"");
//    testScriptFailure("3e", 0, "Unexpected end of input");
//    testScriptFailure("3e+", 0, "Unexpected end of input");
//    testScriptFailure("3e-", 0, "Unexpected end of input");
//    testScriptFailure("3x", 0, "Unexpected \"x\"");
//    testScriptFailure("3x0", 0, "Unexpected \"x\"");
//    testScriptFailure("0x", 0, "Unexpected end of input");
//    testScriptFailure("01a", 0, "Unexpected \"a\"");
//    testScriptFailure("3in[]", 0, "Unexpected \"i\"");
//    testScriptFailure("0x3in[]", 0, "Unexpected \"3\""); // TODO: shouldn't this be "Unexpected \"i\""?
//    testScriptFailure("\"Hello\nWorld\"", 0, "Unexpected \"\\n\"");
//    testScriptFailure("x\\", 0, "Unexpected end of input");
//    testScriptFailure("x\\u005c", 0, "Unexpected end of input");
//    testScriptFailure("x\\u002a", 0, "Unexpected end of input");
//    testScriptFailure("a\\u", 0, "Unexpected end of input");
//    testScriptFailure("\\ua", 0, "Unexpected \"a\"");
//    testScriptFailure("/", 0, "Invalid regular expression: missing /");
//    testScriptFailure("/test", 0, "Invalid regular expression: missing /");
//    testScriptFailure("/test\n/", 0, "Invalid regular expression: missing /");
//    testScriptFailure("for((1 + 1) in list) process(x);", 0, "Invalid left-hand side in for-in");
//    testScriptFailure("[", 0, "Unexpected end of input");
//    testScriptFailure("[,", 0, "Unexpected end of input");
//    testScriptFailure("1 + {", 0, "Unexpected end of input");
//    testScriptFailure("1 + { t:t ", 0, "Unexpected end of input");
//    testScriptFailure("1 + { t:t,", 0, "Unexpected end of input");
//    testScriptFailure("var x = /\n/", 0, "Invalid regular expression: missing /");
//    testScriptFailure("var x = \"\n", 0, "Unexpected \"\\n\"");
//    testScriptFailure("var if = 0", 0, "Unexpected token \"if\"");
//    testScriptFailure("i #= 0", 0, "Unexpected \"#\"");
//    testScriptFailure("1 + (", 0, "Unexpected end of input");
//    testScriptFailure("\n\n\n{", 0, "Unexpected end of input");
//    testScriptFailure("\n/* Some multiline\ncomment */\n)", 0, "Unexpected token \")\"");
//    testScriptFailure("{ set 1 }", 0, "Unexpected number");
//    testScriptFailure("{ get 2 }", 0, "Unexpected number");
//    testScriptFailure("({ set: s(if) { } })", 0, "Unexpected token \"if\"");
//    testScriptFailure("({ set s(.) { } })", 0, "Unexpected token \".\"");
//    testScriptFailure("({ set s() { } })", 0, "Unexpected token \")\"");
//    testScriptFailure("({ set: s() { } })", 0, "Unexpected token \"{\"");
//    testScriptFailure("({ set: s(a, 0, b) { } })", 0, "Unexpected token \"{\"");
//    testScriptFailure("({ get: g(d) { } })", 0, "Unexpected token \"{\"");
//    testScriptFailure("function t(if) { }", 0, "Unexpected token \"if\"");
//    testScriptFailure("function t(true) { }", 0, "Unexpected token \"true\"");
//    testScriptFailure("function t(false) { }", 0, "Unexpected token \"false\"");
//    testScriptFailure("function t(null) { }", 0, "Unexpected token \"null\"");
//    testScriptFailure("function null() { }", 0, "Unexpected token \"null\"");
//    testScriptFailure("function true() { }", 0, "Unexpected token \"true\"");
//    testScriptFailure("function false() { }", 0, "Unexpected token \"false\"");
//    testScriptFailure("function if() { }", 0, "Unexpected token \"if\"");
//    testScriptFailure("a b;", 0, "Unexpected identifier");
//    testScriptFailure("if.a;", 0, "Unexpected token \".\"");
//    testScriptFailure("a if;", 0, "Unexpected token \"if\"");
//    testScriptFailure("a class;", 0, "Unexpected token \"class\"");
//    testScriptFailure("break 1;", 0, "Unexpected number");
//    testScriptFailure("continue 2;", 0, "Unexpected number");
//    testScriptFailure("throw", 0, "Unexpected end of input");
//    testScriptFailure("throw;", 0, "Unexpected token \";\"");
//    testScriptFailure("throw\n", 0, "Illegal newline after throw");
//    testScriptFailure("for (var i, 0, i2 in {});", 0, "Unexpected token \"in\"");
//    testScriptFailure("for ((i in {}));", 0, "Unexpected token \")\"");
//    testScriptFailure("for (i + 1 in {});", 0, "Invalid left-hand side in for-in");
//    testScriptFailure("for (+i in {});", 0, "Invalid left-hand side in for-in");
//    testScriptFailure("if(false)", 0, "Unexpected end of input");
//    testScriptFailure("if(false) doThis(); else", 0, "Unexpected end of input");
//    testScriptFailure("do", 0, "Unexpected end of input");
//    testScriptFailure("while(false)", 0, "Unexpected end of input");
//    testScriptFailure("for(;;)", 0, "Unexpected end of input");
//    testScriptFailure("with(x)", 0, "Unexpected end of input");
//    testScriptFailure("try { }", 0, "Missing catch or finally after try");
//    testScriptFailure("try {} catch (0) {} ", 0, "Unexpected number");
//    testScriptFailure("try {} catch (answer()) {} ", 0, "Unexpected token \"(\"");
//    testScriptFailure("try {} catch (-x) {} ", 0, "Unexpected token \"-\"");
//    testScriptFailure("\u203F = 10", 0, "Unexpected \"\u203F\"");
//    testScriptFailure("switch (c) { default: default: }", 0, "More than one default clause in switch statement");
//    testScriptFailure("new X().\"s\"", 0, "Unexpected string");
//    testScriptFailure("/*", 0, "Unexpected end of input");
//    testScriptFailure("/*\n\n\n", 0, "Unexpected end of input");
//    testScriptFailure("/**", 0, "Unexpected end of input");
//    testScriptFailure("/*\n\n*", 0, "Unexpected end of input");
//    testScriptFailure("/*hello", 0, "Unexpected end of input");
//    testScriptFailure("/*hello  *", 0, "Unexpected end of input");
//    testScriptFailure("\n]", 0, "Unexpected token \"]\"");
//    testScriptFailure("\r]", 0, "Unexpected token \"]\"");
//    testScriptFailure("\r\n]", 0, "Unexpected token \"]\"");
//    testScriptFailure("\n\r]", 0, "Unexpected token \"]\"");
//    testScriptFailure("//\r\n]", 0, "Unexpected token \"]\"");
//    testScriptFailure("//\n\r]", 0, "Unexpected token \"]\"");
//    testScriptFailure("/a\\\n/", 0, "Invalid regular expression: missing /");
//    testScriptFailure("//\r \n]", 0, "Unexpected token \"]\"");
//    testScriptFailure("/*\r\n*/]", 0, "Unexpected token \"]\"");
//    testScriptFailure("/*\n\r*/]", 0, "Unexpected token \"]\"");
//    testScriptFailure("/*\r \n*/]", 0, "Unexpected token \"]\"");
//    testScriptFailure("\\\\", 0, "Unexpected \"\\\\\"");
//    testScriptFailure("\\u005c", 0, "Unexpected end of input");
//    testScriptFailure("\\x", 0, "Unexpected \"x\"");
//    testScriptFailure("\\u0000", 0, "Unexpected end of input");
//    testScriptFailure("\u200C = []", 0, "Unexpected \"\u200C\"");
//    testScriptFailure("\u200D = []", 0, "Unexpected \"\u200D\"");
//    testScriptFailure("\"\\", 0, "Unexpected end of input");
//    testScriptFailure("\"\\u", 0, "Unexpected end of input");
//    testScriptFailure("try { } catch() {}", 0, "Unexpected token \")\"");
//    testScriptFailure("do { x } *", 0, "Unexpected token \"*\"");
//    testScriptFailure("var", 0, "Unexpected end of input");
//    testScriptFailure("const", 0, "Unexpected token \"const\"");
//    testScriptFailure("a enum", 0, "Unexpected identifier");
//    testScriptFailure("{ ;  ;  ", 0, "Unexpected end of input");
//    testScriptFailure("({get +:3})", 0, "Unexpected token \"+\"");
//    testScriptFailure("({get +:3})", 0, "Unexpected token \"+\"");
//    testScriptFailure("function t() { ;  ;  ", 0, "Unexpected end of input");
//    testScriptFailure("#=", 0, "Unexpected \"#\"");
//    testScriptFailure("**", 0, "Unexpected token \"*\"");
//    testScriptFailure("({a = 0});", 0, "Illegal property initializer");
//    testScriptFailure("({a: 0, 0, b = 0});", 0, "Illegal property initializer");
//    testScriptFailure("({a: b = 0, 0, c = 0});", 0, "Illegal property initializer");
//    testScriptFailure("[{a = 0}];", 0, "Illegal property initializer");
//    testScriptFailure("[+{a = 0}];", 0, "Illegal property initializer");
//    testScriptFailure("function* f() { [yield {a = 0}]; }", 0, "Illegal property initializer");
//    testScriptFailure("function* f() { [yield* {a = 0}]; }", 0, "Illegal property initializer");
//    testScriptFailure("1 / %", 0, "Unexpected token \"%\"");
  }
}
