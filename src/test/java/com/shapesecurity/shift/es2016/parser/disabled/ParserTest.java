///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.parser_old;
//
//import Script;
//import Statement;
//import com.shapesecurity.shift.others.TestBase;
//import JsError;
//import Parser;
//import Utils;
//import org.jetbrains.annotations.NotNull;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.Arrays;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//public class ParserTest extends TestBase {
//  public static final double NANOS_TO_SECONDS = 1e-9;
//
//  private void testLibrary(String name) throws IOException, JsError {
//    String source = readLibrary(name);
//    name = name.substring(0, name.lastIndexOf('.'));
//    Script script = Parser.parse(source);
//    String jsonString = Serializer.serialize(script);
//    String expected = jsonString;
//    if (!Files.exists(getPath("parsing/library/" + name + ".json"))) {
//      Files.createDirectories(getPath("parsing/library/" + name + ".json").getParent());
//      Files.write(getPath("parsing/library/" + name + ".json"), jsonString.getBytes(StandardCharsets.UTF_8));
//    } else {
//      expected = readFile("parsing/library/" + name + ".json");
//    }
//    assertEquals("Library " + name, expected, jsonString);
//  }
//
//  @Test
//  public void testSimple() throws JsError {
//    Script node = Parser.parse("this");
//    assertEquals(1, node.body.statements.length);
//    Statement stmt = node.body.statements.maybeHead().fromJust();
//    assertTrue(stmt instanceof ExpressionStatement);
//    assertTrue(((ExpressionStatement) stmt).expression instanceof ThisExpression);
//  }
//
//  private void testParser(String source) throws JsError {
//    Parser.parse(source);
//  }
//
//  private void testParser(String name, String source) throws JsError, IllegalAccessException, IOException {
//    {
//      Script node = Parser.parse(source);
//      String jsonString = Serializer.serialize(node);
//      jsonString = jsonString.substring(0, jsonString.length() - 1);
//      jsonString += ",\"source\":" + Utils.escapeStringLiteral(source) + "}";
//      if (!Files.exists(getPath("parsing/" + name + ".json"))) {
//        assert false;
//        Files.createDirectories(getPath("parsing/" + name + ".json").getParent());
//        Files.write(getPath("parsing/" + name + ".json"), jsonString.getBytes(StandardCharsets.UTF_8));
//      } else {
//        String expected = readFile("parsing/" + name + ".json");
//        assertEquals(expected, jsonString);
//      }
//    }
//    {
//      Script node = Parser.parseWithLocation(source);
//      String jsonString = Serializer.serialize(node);
//      jsonString = jsonString.substring(0, jsonString.length() - 1);
//      jsonString += ",\"source\":" + Utils.escapeStringLiteral(source) + "}";
//      if (!Files.exists(getPath("parsing_with_loc/" + name + ".json"))) {
//        assert false;
//        Files.createDirectories(getPath("parsing_with_loc/" + name + ".json").getParent());
//        Files.write(getPath("parsing_with_loc/" + name + ".json"), jsonString.getBytes(StandardCharsets.UTF_8));
//      } else {
//        String expected = readFile("parsing_with_loc/" + name + ".json");
//        assertEquals(expected, jsonString);
//      }
//
//      node.reduce(new RangeCheckerReducer());
//    }
//  }
//
//  @Test
//  public void testBasicParsing() throws IllegalAccessException, JsError, IOException {
//
//    // Unicode
//    testParser("unicode/00", "日本語 = []");
//    testParser("unicode/01", "T\u203F = []");
//    testParser("unicode/02", "T\u200C = []");
//    testParser("unicode/03", "T\u200D = []");
//    testParser("unicode/04", "\u2163\u2161 = []");
//    testParser("unicode/05", "\u2163\u2161\u200A=\u2009[]");
//
//    // Comments
//    testParser("comments/00", "/* block comment */ 42");
//    testParser("comments/01", "42 /* block comment 1 */ /* block comment 2 */");
//    testParser("comments/02", "(a + /* assignment */b ) * c");
//    testParser("comments/03", "/* assignment */\n a = b");
//    testParser("comments/04", "42 /*The*/ /*Answer*/");
//    testParser("comments/05", "42 /*the*/ /*answer*/");
//    testParser("comments/06", "42 /* the * answer */");
//    testParser("comments/07", "42 /* The * answer */");
//    testParser("comments/08", "/* multiline\ncomment\nshould\nbe\nignored */ 42");
//    testParser("comments/09", "/*a\r\nb*/ 42");
//    testParser("comments/10", "/*a\rb*/ 42");
//    testParser("comments/11", "/*a\nb*/ 42");
//    testParser("comments/12", "/*a\nc*/ 42");
//    testParser("comments/13", "// line comment\n42");
//    testParser("comments/14", "42 // line comment");
//    testParser("comments/15", "// Hello, world!\n42");
//    testParser("comments/16", "// Hello, world!\n");
//    testParser("comments/17", "// Hallo, world!\n");
//    testParser("comments/18", "//\n42");
//    testParser("comments/19", "//");
//    testParser("comments/20", "// ");
//    testParser("comments/21", "/**/42");
//    testParser("comments/22", "42/**/");
//    testParser("comments/23", "// Hello, world!\n\n//   Another hello\n42");
//    testParser("comments/24", "if (x) { doThat() // Some comment\n }");
//    testParser("comments/25", "if (x) { // Some comment\ndoThat(); }");
//    testParser("comments/26", "if (x) { /* Some comment */ doThat() }");
//    testParser("comments/27", "if (x) { doThat() /* Some comment */ }");
//    testParser("comments/28", "switch (answer) { case 42: /* perfect */ bingo() }");
//    testParser("comments/29", "switch (answer) { case 42: bingo() /* perfect */ }");
//    testParser("comments/30", "/* header */ (function(){ var version = 1; }).call(this)");
//    testParser("comments/31", "(function(){ var version = 1; /* sync */ }).call(this)");
//    testParser("comments/32", "function f() { /* infinite */ while (true) { } /* bar */ var each; }");
//    testParser("comments/33", "<!-- foo");
//    testParser("comments/34", "var x = 1<!--foo");
//    testParser("comments/35", "--> comment");
//    testParser("comments/36", "<!-- comment");
//    testParser("comments/37", " \t --> comment");
//    testParser("comments/38", " \t /* block comment */  --> comment");
//    testParser("comments/39", "/* block comment */--> comment");
//    testParser("comments/40", "/* not comment*/; i-->0");
//    testParser("comments/41", "while (i-->0) {}");
//
//    // Primary Expression
//    testParser("expression/primary/00", "this\n");
//    testParser("expression/primary/01", "null\n");
//    testParser("expression/primary/02", "\n    42\n\n");
//    testParser("expression/primary/03", "(1 + 2 ) * 3");
//
//    // Grouping Operator
//    testParser("expression/grouping/00", "(1) + (2  ) + 3");
//    testParser("expression/grouping/01", "4 + 5 << (6)");
//
//    // Array Initializer
//    testParser("expression/array/00", "x = []");
//    testParser("expression/array/01", "x = [ ]");
//    testParser("expression/array/02", "x = [ 42 ]");
//    testParser("expression/array/03", "x = [ 42, ]");
//    testParser("expression/array/04", "x = [ ,, 42 ]");
//    testParser("expression/array/05", "x = [ 1, 2, 3, ]");
//    testParser("expression/array/06", "x = [ 1, 2,, 3, ]");
//
//    // Object Initializer
//    testParser("expression/object/00", "x = {}");
//    testParser("expression/object/01", "x = { }");
//    testParser("expression/object/02", "x = { answer: 42 }");
//    testParser("expression/object/03", "x = { if: 42 }");
//    testParser("expression/object/04", "x = { true: 42 }");
//    testParser("expression/object/05", "x = { false: 42 }");
//    testParser("expression/object/06", "x = { null: 42 }");
//    testParser("expression/object/07", "x = { \"answer\": 42 }");
//    testParser("expression/object/08", "x = { x: 1, x: 2 }");
//    testParser("expression/object/09", "x = { get width() { return m_width } }");
//    testParser("expression/object/10", "x = { get undef() {} }");
//    testParser("expression/object/11", "x = { get if() {} }");
//    testParser("expression/object/12", "x = { get true() {} }");
//    testParser("expression/object/13", "x = { get false() {} }");
//    testParser("expression/object/14", "x = { get null() {} }");
//    testParser("expression/object/15", "x = { get \"undef\"() {} }");
//    testParser("expression/object/16", "x = { get 10() {} }");
//    testParser("expression/object/17", "x = { set width(w) { m_width = w } }");
//    testParser("expression/object/18", "x = { set if(w) { m_if = w } }");
//    testParser("expression/object/19", "x = { set true(w) { m_true = w } }");
//    testParser("expression/object/20", "x = { set false(w) { m_false = w } }");
//    testParser("expression/object/21", "x = { set null(w) { m_null = w } }");
//    testParser("expression/object/22", "x = { set \"null\"(w) { m_null = w } }");
//    testParser("expression/object/23", "x = { set 10(w) { m_null = w } }");
//    testParser("expression/object/24", "x = { get: 42 }");
//    testParser("expression/object/25", "x = { set: 43 }");
//    testParser("expression/object/26", "x = { __proto__: 2 }");
//    testParser("expression/object/27", "x = {\"__proto__\": 2 }");
//    testParser("expression/object/28", "x = { get width() { return m_width }, set width(width) { m_width = width; } }");
//    testParser("expression/object/29", "x = { 2e308: 0 }");
//    testParser("expression/object/30", "x = { get 2e308(){}, set 2e308(x){} }");
//
//    // Numeric Literals
//    testParser("expression/numeric/00", "0");
//    testParser("expression/numeric/01", "3");
//    testParser("expression/numeric/02", "5");
//    testParser("expression/numeric/03", "42");
//    testParser("expression/numeric/04", ".14");
//    testParser("expression/numeric/05", "3.14159");
//    testParser("expression/numeric/06", "6.02214179e+23");
//    testParser("expression/numeric/07", "1.492417830e-10");
//    testParser("expression/numeric/08", "0x0");
//    testParser("expression/numeric/09", "0x0;");
//    testParser("expression/numeric/10", "0e+100 ");
//    testParser("expression/numeric/11", "0e+100");
//    testParser("expression/numeric/12", "0xabc");
//    testParser("expression/numeric/13", "0xdef");
//    testParser("expression/numeric/14", "0X1A");
//    testParser("expression/numeric/15", "0x10");
//    testParser("expression/numeric/16", "0x100");
//    testParser("expression/numeric/17", "0X04");
//    testParser("expression/numeric/18", "02");
//    testParser("expression/numeric/19", "012");
//    testParser("expression/numeric/20", "0012");
//    testParser("expression/numeric/21", "1e308");
//    testParser("expression/numeric/22", "1e309");
//    testParser("expression/numeric/23", "-1e309");
//
//    // String Literals
//    testParser("expression/string/00", "\"Hello\"");
//    testParser("expression/string/01", "\"\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0\"");
//    testParser("expression/string/02", "\"\\u0061\"");
//    testParser("expression/string/03", "\"\\x61\"");
//    testParser("expression/string/04", "\"\\u00\"");
//    testParser("expression/string/05", "\"\\xt\"");
//    testParser("expression/string/06", "\"Hello\\nworld\"");
//    testParser("expression/string/07", "\"Hello\\\nworld\"");
//    testParser("expression/string/08", "\"Hello\\02World\"");
//    testParser("expression/string/09", "\"Hello\\012World\"");
//    testParser("expression/string/10", "\"Hello\\122World\"");
//    testParser("expression/string/11", "\"Hello\\0122World\"");
//    testParser("expression/string/12", "\"Hello\\312World\"");
//    testParser("expression/string/13", "\"Hello\\412World\"");
//    testParser("expression/string/14", "\"Hello\\812World\"");
//    testParser("expression/string/15", "\"Hello\\712World\"");
//    testParser("expression/string/16", "\"Hello\\0World\"");
//    testParser("expression/string/17", "\"Hello\\\r\nworld\"");
//    testParser("expression/string/18", "\"Hello\\1World\"");
//
//    // Regular Expression Literals
//    testParser("expression/regexp/00", "var x = /[a-z]/i");
//    testParser("expression/regexp/01", "var x = /[x-z]/i");
//    testParser("expression/regexp/02", "var x = /[a-c]/i");
//    testParser("expression/regexp/03", "var x = /[P QR]/i");
//    testParser("expression/regexp/04", "var x = /[\\]/]/");
//    testParser("expression/regexp/05", "var x = /foo\\/bar/");
//    testParser("expression/regexp/06", "var x = /=([^=\\s])+/g");
//    testParser("expression/regexp/07", "var x = /[P QR]/\\g");
//    testParser("expression/regexp/08", "var x = /42/g.test");
//
//    // Left-Hand-Side Expression
//    testParser("expression/lhs/00", "new Button");
//    testParser("expression/lhs/01", "new Button()");
//    testParser("expression/lhs/02", "new new foo");
//    testParser("expression/lhs/03", "new new foo()");
//    testParser("expression/lhs/04", "new foo().bar()");
//    testParser("expression/lhs/05", "new foo[bar]");
//    testParser("expression/lhs/06", "new foo.bar()");
//    testParser("expression/lhs/07", "( new foo).bar()");
//    testParser("expression/lhs/08", "foo(bar, baz)");
//    testParser("expression/lhs/09", "(    foo  )()");
//    testParser("expression/lhs/10", "universe.milkyway");
//    testParser("expression/lhs/11", "universe.milkyway.solarsystem");
//    testParser("expression/lhs/12", "universe.milkyway.solarsystem.Earth");
//    testParser("expression/lhs/13", "universe[galaxyName, otherUselessName]");
//    testParser("expression/lhs/14", "universe[galaxyName]");
//    testParser("expression/lhs/15", "universe[42].galaxies");
//    testParser("expression/lhs/16", "universe(42).galaxies");
//    testParser("expression/lhs/17", "universe(42).galaxies(14, 3, 77).milkyway");
//    testParser("expression/lhs/18", "earth.asia.Indonesia.prepareForElection(2014)");
//    testParser("expression/lhs/19", "universe.if");
//    testParser("expression/lhs/20", "universe.true");
//    testParser("expression/lhs/21", "universe.false");
//    testParser("expression/lhs/22", "universe.null");
//
//    // Postfix Expressions
//    testParser("expression/postfix/00", "x++");
//    testParser("expression/postfix/01", "x--");
//    testParser("expression/postfix/02", "eval++");
//    testParser("expression/postfix/03", "eval--");
//    testParser("expression/postfix/04", "arguments++");
//    testParser("expression/postfix/05", "arguments--");
//
//    // Unary Operators
//    testParser("expression/unary/00", "++x");
//    testParser("expression/unary/01", "--x");
//    testParser("expression/unary/02", "++eval");
//    testParser("expression/unary/03", "--eval");
//    testParser("expression/unary/04", "++arguments");
//    testParser("expression/unary/05", "--arguments");
//    testParser("expression/unary/06", "+x");
//    testParser("expression/unary/07", "-x");
//    testParser("expression/unary/08", "~x");
//    testParser("expression/unary/09", "!x");
//    testParser("expression/unary/10", "void x");
//    testParser("expression/unary/11", "delete x");
//    testParser("expression/unary/12", "typeof x");
//
//    // Multiplicative Operators
//    testParser("expression/mul/00", "x * y");
//    testParser("expression/mul/01", "x / y");
//    testParser("expression/mul/02", "x % y");
//
//    // Additive Operators
//    testParser("expression/add/00", "x + y");
//    testParser("expression/add/01", "x - y");
//    testParser("expression/add/02", "\"use strict\" + 42");
//
//    // Bitwise Shift Operator
//    testParser("expression/shift/00", "x << y");
//    testParser("expression/shift/01", "x >> y");
//    testParser("expression/shift/02", "x >>> y");
//
//    // Relational Operators
//    testParser("expression/rel/00", "x < y");
//    testParser("expression/rel/01", "x > y");
//    testParser("expression/rel/02", "x <= y");
//    testParser("expression/rel/03", "x >= y");
//    testParser("expression/rel/04", "x in y");
//    testParser("expression/rel/05", "x instanceof y");
//    testParser("expression/rel/06", "x < y < z");
//
//    // Equality Operators
//    testParser("expression/eq/00", "x == y");
//    testParser("expression/eq/01", "x != y");
//    testParser("expression/eq/02", "x === y");
//    testParser("expression/eq/03", "x !== y");
//
//    // Binary Bitwise Operators
//    testParser("expression/bit/00", "x & y");
//    testParser("expression/bit/01", "x ^ y");
//    testParser("expression/bit/02", "x | y");
//
//    // Binary Expressions
//    testParser("expression/binary/00", "x + y + z");
//    testParser("expression/binary/01", "x - y + z");
//    testParser("expression/binary/02", "x + y - z");
//    testParser("expression/binary/03", "x - y - z");
//    testParser("expression/binary/04", "x + y * z");
//    testParser("expression/binary/05", "x + y / z");
//    testParser("expression/binary/06", "x - y % z");
//    testParser("expression/binary/07", "x * y * z");
//    testParser("expression/binary/08", "x * y / z");
//    testParser("expression/binary/09", "x * y % z");
//    testParser("expression/binary/10", "x % y * z");
//    testParser("expression/binary/11", "x << y << z");
//    testParser("expression/binary/12", "x | y | z");
//    testParser("expression/binary/13", "x & y & z");
//    testParser("expression/binary/14", "x ^ y ^ z");
//    testParser("expression/binary/15", "x & y | z");
//    testParser("expression/binary/16", "x | y ^ z");
//    testParser("expression/binary/17", "x | y & z");
//
//    // Binary Logical Operators
//    testParser("expression/logic/00", "x || y");
//    testParser("expression/logic/01", "x && y");
//    testParser("expression/logic/02", "x || y || z");
//    testParser("expression/logic/03", "x && y && z");
//    testParser("expression/logic/04", "x || y && z");
//    testParser("expression/logic/05", "x || y ^ z");
//
//    // Conditional Operator
//    testParser("expression/cond/00", "y ? 1 : 2");
//    testParser("expression/cond/01", "x && y ? 1 : 2");
//    testParser("expression/cond/02", "x = (0) ? 1 : 2");
//
//    // Assignment Operators
//    testParser("expression/assignment/00", "x = 42");
//    testParser("expression/assignment/01", "eval = 42");
//    testParser("expression/assignment/02", "arguments = 42");
//    testParser("expression/assignment/03", "x *= 42");
//    testParser("expression/assignment/04", "x /= 42");
//    testParser("expression/assignment/05", "x %= 42");
//    testParser("expression/assignment/06", "x += 42");
//    testParser("expression/assignment/07", "x -= 42");
//    testParser("expression/assignment/08", "x <<= 42");
//    testParser("expression/assignment/09", "x >>= 42");
//    testParser("expression/assignment/10", "x >>>= 42");
//    testParser("expression/assignment/11", "x &= 42");
//    testParser("expression/assignment/12", "x ^= 42");
//    testParser("expression/assignment/13", "x |= 42");
//
//    // Complex Expression
//    testParser("expression/complex", "a || b && c | d ^ e & f == g < h >>> i + j * k");
//
//    // Block
//    testParser("statement/block/00", "{ foo }");
//    testParser("statement/block/01", "{ doThis(); doThat(); }");
//    testParser("statement/block/02", "{}");
//
//    // Variable Statement
//    testParser("statement/var/00", "var x");
//    testParser("statement/var/01", "var x, y;");
//    testParser("statement/var/02", "var x = 42");
//    testParser("statement/var/03", "var eval = 42, arguments = 42");
//    testParser("statement/var/04", "var x = 14, y = 3, z = 1977");
//    testParser("statement/var/05", "var implements, interface, package");
//    testParser("statement/var/06", "var private, protected, public, static");
//
//    // Let Statement
//    testParser("statement/let/00", "let x");
//    testParser("statement/let/01", "{ let x }");
//    testParser("statement/let/02", "{ let x = 42 }");
//    testParser("statement/let/03", "{ let x = 14, y = 3, z = 1977 }");
//
//    // Const Statement
//    testParser("statement/const/00", "const x = 42");
//    testParser("statement/const/01", "{ const x = 42 }");
//    testParser("statement/const/02", "{ const x = 14, y = 3, z = 1977 }");
//
//    // Empty Statement
//    testParser("statement/empty", ";");
//
//    // Expression Statement
//    testParser("statement/expression/00", "x");
//    testParser("statement/expression/01", "x, y");
//    testParser("statement/expression/02", "\\u0061");
//    testParser("statement/expression/03", "a\\u0061");
//    testParser("statement/expression/04", "\\u0061a");
//    testParser("statement/expression/05", "\\u0061a ");
//
//    // If Statement
//    testParser("statement/if/00", "if (morning) goodMorning()");
//    testParser("statement/if/01", "if (morning) (function(){})");
//    testParser("statement/if/02", "if (morning) var x = 0;");
//    testParser("statement/if/03", "if (morning) function a(){}");
//    testParser("statement/if/04", "if (morning) goodMorning(); else goodDay()");
//
//    // Iteration Statements
//    testParser("statement/iteration/00", "do keep(); while (true)");
//    testParser("statement/iteration/01", "do keep(); while (true);");
//    testParser("statement/iteration/02", "do { x++; y--; } while (x < 10)");
//    testParser("statement/iteration/03", "{ do { } while (false) false }");
//    testParser("statement/iteration/04", "while (true) doSomething()");
//    testParser("statement/iteration/05", "while (x < 10) { x++; y--; }");
//    testParser("statement/iteration/06", "for(;;);");
//    testParser("statement/iteration/07", "for(;;){}");
//    testParser("statement/iteration/08", "for(x = 0;;);");
//    testParser("statement/iteration/09", "for(var x = 0;;);");
//    testParser("statement/iteration/10", "for(let x = 0;;);");
//    testParser("statement/iteration/11", "for(var x = 0, y = 1;;);");
//    testParser("statement/iteration/12", "for(x = 0; x < 42;);");
//    testParser("statement/iteration/13", "for(x = 0; x < 42; x++);");
//    testParser("statement/iteration/14", "for(x = 0; x < 42; x++) process(x);");
//    testParser("statement/iteration/15", "for(x in list) process(x);");
//    testParser("statement/iteration/16", "for (var x in list) process(x);");
//    testParser("statement/iteration/17", "for (var x = 42 in list) process(x);");
//    testParser("statement/iteration/18", "for (let x in list) process(x);");
//    testParser("statement/iteration/19", "for (var x = y = z in q);");
//    testParser("statement/iteration/20", "for (var a = b = c = (d in e) in z);");
//    testParser("statement/iteration/21", "for (var i = function() { return 10 in [] } in list) process(x);");
//
//    // continue body
//    testParser("statement/continue/00", "while (true) { continue; }");
//    testParser("statement/continue/01", "while (true) { continue }");
//    testParser("statement/continue/02", "done: while (true) { continue done }");
//    testParser("statement/continue/03", "done: while (true) { continue done; }");
//    testParser("statement/continue/04", "__proto__: while (true) { continue __proto__; }");
//
//    // break body
//    testParser("statement/break/00", "while (true) { break }");
//    testParser("statement/break/01", "done: while (true) { break done }");
//    testParser("statement/break/02", "done: while (true) { break done; }");
//    testParser("statement/break/03", "__proto__: while (true) { break __proto__; }");
//
//    // return body
//    testParser("statement/return/00", "(function(){ return })");
//    testParser("statement/return/01", "(function(){ return; })");
//    testParser("statement/return/02", "(function(){ return x; })");
//    testParser("statement/return/03", "(function(){ return x * y })");
//
//    // with body
//    testParser("statement/with/00", "with (x) foo = bar");
//    testParser("statement/with/01", "with (x) foo = bar;");
//    testParser("statement/with/02", "with (x) { foo = bar }");
//
//    // switch body
//    testParser("statement/switch/00", "switch (x) {}");
//    testParser("statement/switch/01", "switch (answer) { case 42: hi(); break; }");
//    testParser("statement/switch/02", "switch (answer) { case 42: hi(); break; default: break }");
//
//    // Labelled Statements
//    testParser("statement/labeled/00", "start: for (;;) break start");
//    testParser("statement/labeled/01", "start: while (true) break start");
//    testParser("statement/labeled/02", "__proto__: test");
//
//    // throw body
//    testParser("statement/throw/00", "throw x;");
//    testParser("statement/throw/01", "throw x * y");
//    testParser("statement/throw/02", "throw { message: \"Error\" }");
//
//    // try body
//    testParser("statement/try/00", "try { } catch (e) { }");
//    testParser("statement/try/01", "try { } catch (eval) { }");
//    testParser("statement/try/02", "try { } catch (arguments) { }");
//    testParser("statement/try/03", "try { } catch (e) { say(e) }");
//    testParser("statement/try/04", "try { } finally { cleanup(stuff) }");
//    testParser("statement/try/05", "try { doThat(); } catch (e) { say(e) }");
//    testParser("statement/try/06", "try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }");
//
//    // debugger body
//    testParser("statement/debugger", "debugger;");
//
//    // FunctionId Definition
//    testParser("statement/functionDecl/00", "function hello() { sayHi(); }");
//    testParser("statement/functionDecl/01", "function eval() { }");
//    testParser("statement/functionDecl/02", "function arguments() { }");
//    testParser("statement/functionDecl/03", "function test(t, t) { }");
//    testParser("statement/functionDecl/04", "(function test(t, t) { })");
//    testParser("statement/functionDecl/05", "function eval() { function inner() { \"use strict\" } }");
//    testParser("statement/functionDecl/06", "function hello(a) { sayHi(); }");
//    testParser("statement/functionDecl/07", "function hello(a, b) { sayHi(); }");
//    testParser("statement/functionDecl/08", "var hi = function() { sayHi() };");
//    testParser("statement/functionDecl/09", "var hi = function eval() { };");
//    testParser("statement/functionDecl/10", "var hi = function arguments() { };");
//    testParser("statement/functionDecl/11", "var hello = function hi() { sayHi() };");
//    testParser("statement/functionDecl/12", "(function(){})");
//    testParser("statement/functionDecl/13", "function universe(__proto__) { }");
//    testParser("statement/functionDecl/14", "function test() { \"use strict\" + 42; }");
//
//    // Automatic semicolon insertion
//    testParser("asi/00", "{ x\n++y }");
//    testParser("asi/01", "{ x\n--y }");
//    testParser("asi/02", "var x /* comment */;");
//    testParser("asi/03", "{ var x = 14, y = 3\nz; }");
//    testParser("asi/04", "while (true) { continue\nthere; }");
//    testParser("asi/05", "while (true) { continue // Comment\nthere; }");
//    testParser("asi/06", "while (true) { continue /* Multiline\nComment */there; }");
//    testParser("asi/07", "while (true) { break\nthere; }");
//    testParser("asi/08", "while (true) { break // Comment\nthere; }");
//    testParser("asi/09", "while (true) { break /* Multiline\nComment */there; }");
//    testParser("asi/10", "(function(){ return\nx; })");
//    testParser("asi/11", "(function(){ return // Comment\nx; })");
//    testParser("asi/12", "(function(){ return/* Multiline\nComment */x; })");
//    testParser("asi/13", "{ throw error\nerror; }");
//    testParser("asi/14", "{ throw error// Comment\nerror; }");
//    testParser("asi/15", "{ throw error/* Multiline\nComment */error; }");
//
//    // Directive Prolog
//    testParser("directive/00", "(function () { 'use\\x20strict'; with (i); }())");
//    testParser("directive/01", "(function () { 'use\\nstrict'; with (i); }())");
//
//    // Whitespace
//    testParser("ws/00",
//        "new\u0020\u0009\u000B\u000C\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\uFEFFa");
//    testParser("ws/01", "{0\n1\r2\u20283\u20294}");
//  }
//
//  @Test
//  public void testLibrary() throws IOException, JsError {
//    setFatal(false); // Collect the failures in an ErrorCollector
//
//    // Whitelisted libraries to test to avoid generating too much mess
//    java.util.List<String> whitelist = Arrays.asList(
//        "angular-1.2.5.js",
//        "angular-1.2.5.min.js",
//        "angular-1.2.5.min.ugly.js",
//        "backbone-1.1.0.js",
//        "backbone-min-1.1.2.js",
//        "dojo-1.10.0.js",
//        "ember-1.7.0.js",
//        "everything-0.0.4.js",
//        "ga.js",
//        "jquery.mobile.min-1.4.3.js",
//        "knockout-v3.2.0.js",
//        "mootools-1.4.5.js",
//        "mootools-yui-compressed-1.5.0.js",
//        "prototype-1.7.2.0.js",
//        "qunit-v1.14.0.js",
//        "scriptaculous-1.9.0.js",
//        "swfobject-2.2.js",
//        "three.min-r67.js",
//        "underscore-1.5.2.js",
//        "webfont-1.5.3.js",
//        "yui-3.12.0.js",
//        "yui-min-3.17.2.js"
//    );
//
//
//    // Test the hell out of it... ": )
//    System.out.println("Testing " + whitelist.size() + " javascript libraries.");
//    long start = System.nanoTime();
//    for (String jsLib : whitelist) {
//      System.out.print(".");
//      testLibrary(jsLib);
//    }
//    System.out.println("");
//    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
//    System.out.printf("Library testing time: %.1fsec\n", elapsed);
//    setFatal(true); // Revert back to the default behavior
//  }
//
//  private void testFailureML(@NotNull String source, int line, int column, int index, @NotNull String error) {
//    try {
//      Parser.parse(source);
//    } catch (JsError jsError) {
//      assertEquals(error, jsError.getDescription());
//      assertEquals(line, jsError.getLine());
//      assertEquals(column, jsError.getColumn());
//      assertEquals(index, jsError.getIndex());
//
//      return;
//    }
//    fail("Parsing error not found");
//  }
//
//
//  private void testFailure(@NotNull String source, int index, @NotNull String error) {
//    testFailureML(source, 1, index, index, error);
//  }
//
//  @Test
//  @Ignore
//  public void testRegexFailure() {
//    // TODO: regex engine.
//    testFailureML("var x = /(s/g", 0, 0, 0, "Invalid regular expression");
//    testFailureML("var x = /[a-z]/\\ux", 0, 0, 0, "Unexpected token ILLEGAL");
//    testFailureML("var x = /[a-z\n]/\\ux", 0, 0, 0, "Invalid regular expression: missing /");
//    testFailureML("var x = /[a-z]/\\\\ux", 0, 0, 0, "Unexpected token ILLEGAL");
//    testFailureML("var x = /[P QR]/\\\\u0067", 0, 0, 0, "Unexpected token ILLEGAL");
//  }
//
//  @Test
//  @Ignore
//  public void testES6Failure() {
//    // TODO: ES6:
//    testFailureML("((a)) => 42", 0, 0, 0, "Unexpected token =>");
//    testFailureML("(a, (b)) => 42", 0, 0, 0, "Unexpected token =>");
//    testFailureML("\"use strict\"; (eval = 10) => 42", 0, 0, 0,
//        "Assignment to eval or arguments is not allowed in strict mode");
//    // strict mode, using eval when IsSimpleParameterList is true
//    testFailureML("\"use strict\"; eval => 42", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//    // strict mode, using arguments when IsSimpleParameterList is true
//    testFailureML("\"use strict\"; arguments => 42", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//    // strict mode, using eval when IsSimpleParameterList is true
//    testFailureML("\"use strict\"; (eval, a) => 42", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//    // strict mode, using arguments when IsSimpleParameterList is true
//    testFailureML("\"use strict\"; (arguments, a) => 42", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//    testFailureML("(a, a) => 42", 0, 0, 0, "Strict mode function may not have duplicate parameter names");
//    testFailureML("\"use strict\"; (a, a) => 42",
//        0,
//        0,
//        0,
//        "Strict mode function may not have duplicate parameter names");
//    testFailureML("\"use strict\"; (a) => 00", 0, 0, 0, "Octal literals are not allowed in strict mode.");
//    testFailureML("() <= 42", 0, 0, 0, "Unexpected token <=");
//    testFailureML("() ? 42", 0, 0, 0, "Unexpected token ?");
//    testFailureML("() + 42", 0, 0, 0, "Unexpected token +");
//    testFailureML("(10) => 00", 0, 0, 0, "Unexpected token =>");
//    testFailureML("(10, 20) => 00", 0, 0, 0, "Unexpected token =>");
//    testFailureML("\"use strict\"; (eval) => 42", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//    testFailureML("(eval) => { \"use strict\"; 42 }", 0, 0, 0,
//        "Parameter name eval or arguments is not allowed in strict mode");
//  }
//
//  @Test
//  public void testES5Failure() {
//    testFailure("{", 1, "Unexpected end of input");
//    testFailure("}", 1, "Unexpected token }");
//    testFailure("3ea", 2, "Unexpected token ILLEGAL");
//    testFailure("3in []", 1, "Unexpected token ILLEGAL");
//    testFailure("3e", 2, "Unexpected token ILLEGAL");
//    testFailure("3e+", 3, "Unexpected token ILLEGAL");
//    testFailure("3e-", 3, "Unexpected token ILLEGAL");
//    testFailure("3x", 1, "Unexpected token ILLEGAL");
//    testFailure("3x0", 1, "Unexpected token ILLEGAL");
//    testFailure("0x", 2, "Unexpected token ILLEGAL");
//    testFailure("09", 1, "Unexpected token ILLEGAL");
//    testFailure("018", 2, "Unexpected token ILLEGAL");
//    testFailure("01a", 2, "Unexpected token ILLEGAL");
//    testFailure("3in[]", 1, "Unexpected token ILLEGAL");
//    testFailure("0x3in[]", 2, "Unexpected token ILLEGAL");
//    testFailure("\"Hello\nWorld\"", 6, "Unexpected token ILLEGAL");
//    testFailure("x\\", 2, "Unexpected token ILLEGAL");
//    testFailure("x\\u005c", 7, "Unexpected token ILLEGAL");
//    testFailure("x\\u002a", 7, "Unexpected token ILLEGAL");
//    testFailure("a\\u", 3, "Unexpected token ILLEGAL");
//    testFailure("\\ua", 2, "Unexpected token ILLEGAL");
//    testFailure("/", 0, "Invalid regular expression: missing /");
//    testFailure("/test", 0, "Invalid regular expression: missing /");
//    testFailure("/test\n/", 0, "Invalid regular expression: missing /");
//    testFailure("[", 1, "Unexpected end of input");
//    testFailure("[,", 2, "Unexpected end of input");
//    testFailure("1 + {", 5, "Unexpected end of input");
//    testFailure("1 + { t:t ", 10, "Unexpected end of input");
//    testFailure("1 + { t:t,", 10, "Unexpected end of input");
//    testFailure("var x = /\n/", 8, "Invalid regular expression: missing /");
//    testFailure("var x = \"\n", 9, "Unexpected token ILLEGAL");
//    testFailure("var if = 42", 4, "Unexpected token if");
//    testFailure("i #= 42", 2, "Unexpected token ILLEGAL");
//    testFailure("1 + (", 5, "Unexpected end of input");
//    testFailureML("\n\n\n{", 4, 1, 4, "Unexpected end of input");
//    testFailureML("\n/* Some multiline\ncomment */\n)", 4, 1, 31, "Unexpected token )");
//    testFailure("{ set 1 }", 6, "Unexpected number");
//    testFailure("{ get 2 }", 6, "Unexpected number");
//    testFailure("({ set: s(if) { } })", 12, "Unexpected token if");
//    testFailure("({ set s(.) { } })", 9, "Unexpected token .");
//    testFailure("({ set s() { } })", 3, "Unexpected token )");
//    testFailure("({ set: s() { } })", 12, "Unexpected token {");
//    testFailure("({ set: s(a, b) { } })", 16, "Unexpected token {");
//    testFailure("({ get: g(d) { } })", 13, "Unexpected token {");
//    testFailure("({ get i() { }, i: 42 })", 22, "Object literal may not have data and accessor property with the same name");
//    testFailure("({ i: 42, get i() { } })", 22, "Object literal may not have data and accessor property with the same name");
//    testFailure("({ set i(x) { }, i: 42 })", 23, "Object literal may not have data and accessor property with the same name");
//    testFailure("({ i: 42, set i(x) { } })", 23, "Object literal may not have data and accessor property with the same name");
//    testFailure("({ get i() { }, get i() { } })", 28, "Object literal may not have multiple get/set accessors with the same name");
//    testFailure("({ set i(x) { }, set i(x) { } })", 30, "Object literal may not have multiple get/set accessors with the same name");
//    testFailure("function t(if) { }", 11, "Unexpected token if");
//    testFailure("function t(true) { }", 11, "Unexpected token true");
//    testFailure("function t(false) { }", 11, "Unexpected token false");
//    testFailure("function t(null) { }", 11, "Unexpected token null");
//    testFailure("function null() { }", 9, "Unexpected token null");
//    testFailure("function true() { }", 9, "Unexpected token true");
//    testFailure("function false() { }", 9, "Unexpected token false");
//    testFailure("function if() { }", 9, "Unexpected token if");
//    testFailure("a b;", 2, "Unexpected identifier");
//    testFailure("if.a;", 2, "Unexpected token .");
//    testFailure("a if;", 2, "Unexpected token if");
//    testFailure("a class;", 2, "Unexpected reserved word");
//    testFailure("break\n", 0, "Illegal break statement");
//    testFailure("break 1;", 6, "Unexpected number");
//    testFailure("continue\n", 0, "Illegal continue statement");
//    testFailure("continue 2;", 9, "Unexpected number");
//    testFailure("throw", 5, "Unexpected end of input");
//    testFailure("throw;", 6, "Unexpected token ;");
//    testFailure("throw\n", 0, "Illegal newline after throw");
//    testFailure("for (var i, i2 in {});", 15, "Unexpected token in");
//    testFailure("for ((i in {}));", 14, "Unexpected token )");
//    testFailure("for (i + 1 in {});", 11, "Invalid left-hand side in for-in");
//    testFailure("for ((i) + (1) in {});", 15, "Invalid left-hand side in for-in");
//    testFailure("for (+i in {});", 8, "Invalid left-hand side in for-in");
//    testFailure("if(false)", 9, "Unexpected end of input");
//    testFailure("if(false) doThis(); else", 24, "Unexpected end of input");
//    testFailure("do", 2, "Unexpected end of input");
//    testFailure("while(false)", 12, "Unexpected end of input");
//    testFailure("for(;;)", 7, "Unexpected end of input");
//    testFailure("with(x)", 7, "Unexpected end of input");
//    testFailure("try { }", 7, "Missing catch or finally after try");
//    testFailure("try {} catch (42) {} ", 14, "Unexpected number");
//    testFailure("try {} catch (answer()) {} ", 20, "Unexpected token (");
//    testFailure("try {} catch (-x) {} ", 14, "Unexpected token -");
//    testFailure("\u203f = 10", 0, "Unexpected token ILLEGAL");
//    // TODO: discussion.
//    // testFailure("if(true) let a = 1;", 13, "Unexpected token let");
//    // testFailure("if(true) const a = 1;", 15, "Unexpected token const");
//    testFailure("switch (c) { default: default: }", 22, "More than one default clause in switch statement");
//    testFailure("new X().\"s\"", 11, "Unexpected string");
//    testFailure("/*", 2, "Unexpected token ILLEGAL");
//    testFailureML("/*\n\n\n", 4, 0, 5, "Unexpected token ILLEGAL");
//    testFailure("/**", 3, "Unexpected token ILLEGAL");
//    testFailureML("/*\n\n*", 3, 1, 5, "Unexpected token ILLEGAL");
//    testFailure("/*hello", 7, "Unexpected token ILLEGAL");
//    testFailure("/*hello  *", 10, "Unexpected token ILLEGAL");
//    testFailureML("\n]", 2, 1, 2, "Unexpected token ]");
//    testFailureML("\r]", 2, 1, 2, "Unexpected token ]");
//    testFailureML("\r\n]", 2, 1, 3, "Unexpected token ]");
//    testFailureML("\n\r]", 3, 1, 3, "Unexpected token ]");
//    testFailureML("//\r\n]", 2, 1, 5, "Unexpected token ]");
//    testFailureML("//\n\r]", 3, 1, 5, "Unexpected token ]");
//    testFailure("/a\\\n/", 0, "Invalid regular expression: missing /");
//    testFailureML("//\r \n]", 3, 1, 6, "Unexpected token ]");
//    testFailureML("/*\r\n*/]", 2, 3, 7, "Unexpected token ]");
//    testFailureML("/*\n\r*/]", 3, 3, 7, "Unexpected token ]");
//    testFailureML("/*\r \n*/]", 3, 3, 8, "Unexpected token ]");
//    testFailure("\\\\", 1, "Unexpected token ILLEGAL");
//    testFailure("\\u005c", 6, "Unexpected token ILLEGAL");
//    testFailure("\\x", 1, "Unexpected token ILLEGAL");
//    testFailure("\\u0000", 6, "Unexpected token ILLEGAL");
//    testFailure("\u200c = []", 0, "Unexpected token ILLEGAL");
//    testFailure("\u200d = []", 0, "Unexpected token ILLEGAL");
//    testFailure("\"\\", 2, "Unexpected token ILLEGAL");
//    testFailure("\"\\u", 3, "Unexpected token ILLEGAL");
//    testFailure("try { } catch() {}", 14, "Unexpected token )");
//    testFailure("return", 6, "Illegal return statement");
//    testFailure("break", 0, "Illegal break statement");
//    testFailure("continue", 0, "Illegal continue statement");
//    testFailure("switch (x) { default: continue; }", 22, "Illegal continue statement");
//    testFailure("do { x } *", 9, "Unexpected token *");
//    testFailure("while (true) { break x; }", 22, "Undefined label 'x'");
//    testFailure("while (true) { continue x; }", 25, "Undefined label 'x'");
//    testFailure("x: while (true) { (function () { break x; }); }", 40, "Undefined label 'x'");
//    testFailure("x: while (true) { (function () { continue x; }); }", 43, "Undefined label 'x'");
//    testFailure("x: while (true) { (function () { break; }); }", 33, "Illegal break statement");
//    testFailure("x: while (true) { (function () { continue; }); }", 33, "Illegal continue statement");
//    testFailure("x: while (true) { x: while (true) { } }", 21, "Label 'x' has already been declared");
//    testFailure("(function () { 'use strict'; delete i; }())", 37, "Delete of an unqualified identifier in strict mode.");
//    testFailure("(function () { 'use strict'; with (i); }())", 29, "Strict mode code may not include a with statement");
//    testFailure("function hello() {'use strict'; ({ i: 42, i: 42 }) }", 48, "Duplicate data property in object literal not allowed in strict mode");
//    testFailure("function hello() {'use strict'; ({ hasOwnProperty: 42, hasOwnProperty: 42 }) }", 74, "Duplicate data property in object literal not allowed in strict mode");
//    testFailure("function hello() {'use strict'; var eval = 10; }", 41, "Variable name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; var arguments = 10; }", 46, "Variable name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; try { } catch (eval) { } }", 51, "Catch variable may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; try { } catch (arguments) { } }", 56, "Catch variable may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; eval = 10; }", 32, "Assignment to eval or arguments is not allowed in strict mode");
//    testFailure("function hello() {'use strict'; arguments = 10; }", 32, "Assignment to eval or arguments is not allowed in strict mode");
//    testFailure("function hello() {'use strict'; ++eval; }", 38, "Prefix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; --eval; }", 38, "Prefix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; ++arguments; }", 43, "Prefix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; --arguments; }", 43, "Prefix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; eval++; }", 38, "Postfix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; eval--; }", 38, "Postfix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; arguments++; }", 43, "Postfix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; arguments--; }", 43, "Postfix increment/decrement may not have eval or arguments operand in strict mode");
//    testFailure("function hello() {'use strict'; function eval() { } }", 32, "Function name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; function arguments() { } }", 32, "Function name may not be eval or arguments in strict mode");
//    testFailure("function eval() {'use strict'; }", 32, "Function name may not be eval or arguments in strict mode");
//    testFailure("function arguments() {'use strict'; }", 37, "Function name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; (function eval() { }()) }", 33, "Function name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; (function arguments() { }()) }", 33, "Function name may not be eval or arguments in strict mode");
//    testFailure("(function eval() {'use strict'; })()", 10, "Function name may not be eval or arguments in strict mode");
//    testFailure("(function arguments() {'use strict'; })()", 10, "Function name may not be eval or arguments in strict mode");
//    testFailure("function hello() {'use strict'; ({ s: function eval() { } }); }", 38, "Function name may not be eval or arguments in strict mode");
//    testFailure("(function package() {'use strict'; })()", 10, "Use of future reserved word in strict mode");
//    testFailure("function hello() {'use strict'; ({ i: 10, set s(eval) { } }); }", 58, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello() {'use strict'; ({ set s(eval) { } }); }", 51, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello() {'use strict'; ({ s: function s(eval) { } }); }", 49, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello(eval) {'use strict';}", 36, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello(arguments) {'use strict';}", 41, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello() { 'use strict'; function inner(eval) {} }", 57, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function hello() { 'use strict'; function inner(arguments) {} }", 62, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("\"\\1\"; 'use strict';", 0, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { 'use strict'; \"\\1\"; }", 33, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { 'use strict'; 021; }", 33, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { 'use strict'; ({ \"\\1\": 42 }); }", 36, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { 'use strict'; ({ 021: 42 }); }", 36, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { \"octal directive\\1\"; \"use strict\"; }", 19, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { \"octal directive\\1\"; \"octal directive\\2\"; \"use strict\"; }", 19, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { \"use strict\"; function inner() { \"octal directive\\1\"; } }", 52, "Octal literals are not allowed in strict mode.");
//    testFailure("function hello() { \"use strict\"; var implements; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var interface; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var package; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var private; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var protected; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var public; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var static; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var yield; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function hello() { \"use strict\"; var let; }", 37, "Unexpected token let");
//    testFailure("function hello(static) { \"use strict\"; }", 40, "Use of future reserved word in strict mode");
//    testFailure("function static() { \"use strict\"; }", 35, "Use of future reserved word in strict mode");
//    testFailure("function eval(a) { \"use strict\"; }", 34, "Function name may not be eval or arguments in strict mode");
//    testFailure("function arguments(a) { \"use strict\"; }", 39, "Function name may not be eval or arguments in strict mode");
//    testFailure("var let", 4, "Unexpected token let");
//    testFailure("\"use strict\"; function static() { }", 23, "Use of future reserved word in strict mode");
//    testFailure("function a(t, t) { \"use strict\"; }", 34, "Strict mode function may not have duplicate parameter names");
//    testFailure("function a(eval) { \"use strict\"; }", 34, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("function a(package) { \"use strict\"; }", 37, "Use of future reserved word in strict mode");
//    testFailure("function a() { \"use strict\"; function b(t, t) { }; }", 49, "Strict mode function may not have duplicate parameter names");
//    testFailure("(function a(t, t) { \"use strict\"; })", 15, "Strict mode function may not have duplicate parameter names");
//    testFailure("function a() { \"use strict\"; (function b(t, t) { }); }", 44, "Strict mode function may not have duplicate parameter names");
//    testFailure("(function a(eval) { \"use strict\"; })", 12, "Parameter name eval or arguments is not allowed in strict mode");
//    testFailure("(function a(package) { \"use strict\"; })", 12, "Use of future reserved word in strict mode");
//    testFailure("__proto__: __proto__: 42;", 22, "Label '__proto__' has already been declared");
//    testFailure("\"use strict\"; function t(__proto__, __proto__) { }", 50, "Strict mode function may not have duplicate parameter names");
//    testFailure("\"use strict\"; x = { __proto__: 42, __proto__: 43 }", 49, "Duplicate data property in object literal not allowed in strict mode");
//    testFailure("\"use strict\"; x = { get __proto__() { }, __proto__: 43 }", 55, "Object literal may not have data and accessor property with the same name");
//    testFailure("var", 3, "Unexpected end of input");
//    testFailure("let", 3, "Unexpected end of input");
//    testFailure("const", 5, "Unexpected end of input");
//    testFailure("{ ;  ;  ", 8, "Unexpected end of input");
//    testFailure("function t() { ;  ;  ", 21, "Unexpected end of input");
//  }
//
//  @Test
//  // programs that parse according to ES3 but either fail or parse differently according to ES5
//  public void testES5BackwardIncompatibilities() throws JsError {
//    // ES3: zero-width non-breaking space is allowed in an identifier
//    // ES5: zero-width non-breaking space is a whitespace character
//    testFailure("_\uFEFF_", 2, "Unexpected identifier");
//
//    // ES3: a slash in a regexp character class will terminate the regexp
//    // ES5: a slash is allowed within a regexp character class
//    testFailure("[/[/]", 1, "Invalid regular expression: missing /");
//  }
//
//  @Test
//  // programs where we choose to diverge from the ES5 specification
//  public void testES5Divergences() throws JsError {
//    // ES5: assignment to computed member expression
//    // ES6: variable declaration statement
//    // We choose to fail here because we support ES5 with a minor addition: let/const with binding identifier.
//    // This is the same decision esprima has made.
//    testFailure("let[a] = b;", 3, "Unexpected token [");
//    testFailure("const[a] = b;", 5, "Unexpected token [");
//    testFailure("var let", 4, "Unexpected token let");
//    testFailure("var const", 4, "Unexpected token const");
//
//    // ES5: group expression is a left hand side expression
//    // ES6: failed to parse rather than generating a TypeError.
//    // No browsers parse this. But we are trying to be permissive.
//    testParser("(1++) = 3");
//    testParser("(1++)++");
//    testParser("for((++this) in'');");
//
//    testFailure("1++ = 3", 4, "Invalid left-hand side in assignment");
//    testFailure("1++ ++", 4, "Unexpected token ++");
//    testFailure("for(++this in'');", 11, "Invalid left-hand side in for-in");
//    testFailure("for((1) + (2) in'');", 14, "Invalid left-hand side in for-in");
//
//    // ES5: let is a strict mode reserved word.
//    // ES6: parses fine.
//    // Due to the same reason as above, this is allowed.
//    testParser("function f(){'use strict'; let a = 0;}");
//
//    // ES5/6: does not recognize XML-style comments ("<!--" and "-->")
//    // We chose to recognize XML-style comment because all browsers and node.js supports it.
//    testParser("a <!--b#ERROR");
//    testParser("\n    -->b#ERROR");
//
//    // ES5: invalid program
//    // ES6: function declaration within a block
//    // We choose to parse this because of ubiquitous support among popular interpreters, despite disagreements about semantics.
//    testParser("{ function f(){} }");
//  }
//
//  @Test
//  // programs that parse according to ES5 but either fail or parse differently according to ES6
//  public void testES6BackwardIncompatibilities() throws JsError {
//    // ES5: in sloppy mode, future reserved words (including yield) are regular identifiers
//    // ES6: yield has been moved from the future reserved words list to the keywords list
//    testParser("var yield = function yield(){};");
//
//    // ES5: this declares a function-scoped variable while at the same time assigning to the block-scoped variable
//    // ES6: this particular construction is explicitly disallowed
//    testParser("try {} catch(e) { var e = 0; }");
//
//    // ES5: group expression not considered LeftHandSideExpression
//    // ES6: does not parse
//    testParser("for((1 + 1) in list) process(x);");
//  }
//}
