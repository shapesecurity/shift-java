/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.laserbat.js.parser;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.shapesecurity.laserbat.js.TestBase;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.expression.ThisExpression;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.serialization.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class ParserTest extends TestBase {
  public static final double NANOS_TO_SECONDS = 1e-9;

  private static boolean testLibrary(String name) throws IOException, JsError {
    String source = readLibrary(name);
    name = name.substring(0, name.lastIndexOf('.'));
    Script script = new Parser(source).parse();
    Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
    String jsonString = gson.toJson(Serializer.serialize(script));
    String expected = jsonString;
    if (!Files.exists(getPath("parsing/library/" + name + ".json"))) {
      Files.createDirectories(getPath("parsing/library/" + name + ".json").getParent());
      Files.write(getPath("parsing/library/" + name + ".json"), jsonString.getBytes(StandardCharsets.UTF_8));
    } else {
      expected = readFile("parsing/library/" + name + ".json");
    }
    return jsonString.equals(expected);
  }

  @Test
  public void testSimple() throws JsError {
    Parser parser = new Parser("this");
    Script node = parser.parse();
    assertEquals(1, node.body.statements.length());
    Statement stmt = node.body.statements.maybeHead().just();
    assertTrue(stmt instanceof ExpressionStatement);
    assertTrue(((ExpressionStatement) stmt).expression instanceof ThisExpression);
  }

  private void testParser(String name, String source) throws JsError, IllegalAccessException, IOException {
    Parser parser = new Parser(source);
    Script node = parser.parse();
    JsonObject jsonObject = Serializer.serialize(node);

    jsonObject.addProperty("source", source);
    Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
    String jsonString = gson.toJson(jsonObject);

    if (!Files.exists(getPath("parsing/" + name + ".json"))) {
      Files.createDirectories(getPath("parsing/" + name + ".json").getParent());
      Files.write(getPath("parsing/" + name + ".json"), jsonString.getBytes(StandardCharsets.UTF_8));
    } else {
      String expected = readFile("parsing/" + name + ".json");
      assertEquals(expected, jsonString);
    }
  }

  private void testFailure(String source, String error) {
    try {
      Parser.parse(source);
    } catch (JsError jsError) {
      assertEquals(error, jsError.getDescription());
      return;
    }
    fail("Parsing error not found");
  }

  @Test
  public void testBasicParsing() throws IllegalAccessException, JsError, IOException {

    // Unicode
    testParser("unicode/00", "日本語 = []");
    testParser("unicode/01", "T\u203F = []");
    testParser("unicode/02", "T\u200C = []");
    testParser("unicode/03", "T\u200D = []");
    testParser("unicode/04", "\u2163\u2161 = []");
    testParser("unicode/05", "\u2163\u2161\u200A=\u2009[]");

    // Comments
    testParser("comments/00", "/* block comment */ 42");
    testParser("comments/01", "42 /* block comment 1 */ /* block comment 2 */");
    testParser("comments/02", "(a + /* assignment */b ) * c");
    testParser("comments/03", "/* assignment */\n a = b");
    testParser("comments/04", "42 /*The*/ /*Answer*/");
    testParser("comments/05", "42 /*the*/ /*answer*/");
    testParser("comments/06", "42 /* the * answer */");
    testParser("comments/07", "42 /* The * answer */");
    testParser("comments/08", "/* multiline\ncomment\nshould\nbe\nignored */ 42");
    testParser("comments/09", "/*a\r\nb*/ 42");
    testParser("comments/10", "/*a\rb*/ 42");
    testParser("comments/11", "/*a\nb*/ 42");
    testParser("comments/12", "/*a\nc*/ 42");
    testParser("comments/13", "// line comment\n42");
    testParser("comments/14", "42 // line comment");
    testParser("comments/15", "// Hello, world!\n42");
    testParser("comments/16", "// Hello, world!\n");
    testParser("comments/17", "// Hallo, world!\n");
    testParser("comments/18", "//\n42");
    testParser("comments/19", "//");
    testParser("comments/20", "// ");
    testParser("comments/21", "/**/42");
    testParser("comments/22", "42/**/");
    testParser("comments/23", "// Hello, world!\n\n//   Another hello\n42");
    testParser("comments/24", "if (x) { doThat() // Some comment\n }");
    testParser("comments/25", "if (x) { // Some comment\ndoThat(); }");
    testParser("comments/26", "if (x) { /* Some comment */ doThat() }");
    testParser("comments/27", "if (x) { doThat() /* Some comment */ }");
    testParser("comments/28", "switch (answer) { case 42: /* perfect */ bingo() }");
    testParser("comments/29", "switch (answer) { case 42: bingo() /* perfect */ }");
    testParser("comments/30", "/* header */ (function(){ var version = 1; }).call(this)");
    testParser("comments/31", "(function(){ var version = 1; /* sync */ }).call(this)");
    testParser("comments/32", "function f() { /* infinite */ while (true) { } /* bar */ var each; }");
    testParser("comments/33", "<!-- foo");
    testParser("comments/34", "var x = 1<!--foo");
    testParser("comments/35", "--> comment");
    testParser("comments/36", "<!-- comment");
    testParser("comments/37", " \t --> comment");
    testParser("comments/38", " \t /* block comment */  --> comment");
    testParser("comments/39", "/* block comment */--> comment");
    testParser("comments/40", "/* not comment*/; i-->0");
    testParser("comments/41", "while (i-->0) {}");

    // Primary Expression
    testParser("expression/primary/00", "this\n");
    testParser("expression/primary/01", "null\n");
    testParser("expression/primary/02", "\n    42\n\n");
    testParser("expression/primary/03", "(1 + 2 ) * 3");

    // Grouping Operator
    testParser("expression/grouping/00", "(1) + (2  ) + 3");
    testParser("expression/grouping/01", "4 + 5 << (6)");

    // Array Initializer
    testParser("expression/array/00", "x = []");
    testParser("expression/array/01", "x = [ ]");
    testParser("expression/array/02", "x = [ 42 ]");
    testParser("expression/array/03", "x = [ 42, ]");
    testParser("expression/array/04", "x = [ ,, 42 ]");
    testParser("expression/array/05", "x = [ 1, 2, 3, ]");
    testParser("expression/array/06", "x = [ 1, 2,, 3, ]");

    // Object Initializer
    testParser("expression/object/00", "x = {}");
    testParser("expression/object/01", "x = { }");
    testParser("expression/object/02", "x = { answer: 42 }");
    testParser("expression/object/03", "x = { if: 42 }");
    testParser("expression/object/04", "x = { true: 42 }");
    testParser("expression/object/05", "x = { false: 42 }");
    testParser("expression/object/06", "x = { null: 42 }");
    testParser("expression/object/07", "x = { \"answer\": 42 }");
    testParser("expression/object/08", "x = { x: 1, x: 2 }");
    testParser("expression/object/09", "x = { get width() { return m_width } }");
    testParser("expression/object/10", "x = { get undef() {} }");
    testParser("expression/object/11", "x = { get if() {} }");
    testParser("expression/object/12", "x = { get true() {} }");
    testParser("expression/object/13", "x = { get false() {} }");
    testParser("expression/object/14", "x = { get null() {} }");
    testParser("expression/object/15", "x = { get \"undef\"() {} }");
    testParser("expression/object/16", "x = { get 10() {} }");
    testParser("expression/object/17", "x = { set width(w) { m_width = w } }");
    testParser("expression/object/18", "x = { set if(w) { m_if = w } }");
    testParser("expression/object/19", "x = { set true(w) { m_true = w } }");
    testParser("expression/object/20", "x = { set false(w) { m_false = w } }");
    testParser("expression/object/21", "x = { set null(w) { m_null = w } }");
    testParser("expression/object/22", "x = { set \"null\"(w) { m_null = w } }");
    testParser("expression/object/23", "x = { set 10(w) { m_null = w } }");
    testParser("expression/object/24", "x = { get: 42 }");
    testParser("expression/object/25", "x = { set: 43 }");
    testParser("expression/object/26", "x = { __proto__: 2 }");
    testParser("expression/object/27", "x = {\"__proto__\": 2 }");
    testParser("expression/object/28", "x = { get width() { return m_width }, set width(width) { m_width = width; } }");

    // Numeric Literals
    testParser("expression/numeric/00", "0");
    testParser("expression/numeric/01", "3");
    testParser("expression/numeric/02", "5");
    testParser("expression/numeric/03", "42");
    testParser("expression/numeric/04", ".14");
    testParser("expression/numeric/05", "3.14159");
    testParser("expression/numeric/06", "6.02214179e+23");
    testParser("expression/numeric/07", "1.492417830e-10");
    testParser("expression/numeric/08", "0x0");
    testParser("expression/numeric/09", "0x0;");
    testParser("expression/numeric/10", "0e+100 ");
    testParser("expression/numeric/11", "0e+100");
    testParser("expression/numeric/12", "0xabc");
    testParser("expression/numeric/13", "0xdef");
    testParser("expression/numeric/14", "0X1A");
    testParser("expression/numeric/15", "0x10");
    testParser("expression/numeric/16", "0x100");
    testParser("expression/numeric/17", "0X04");
    testParser("expression/numeric/18", "02");
    testParser("expression/numeric/19", "012");
    testParser("expression/numeric/20", "0012");

    // String Literals
    testParser("expression/string/00", "\"Hello\"");
    testParser("expression/string/01", "\"\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0\"");
    testParser("expression/string/02", "\"\\u0061\"");
    testParser("expression/string/03", "\"\\x61\"");
    testParser("expression/string/04", "\"\\u00\"");
    testParser("expression/string/05", "\"\\xt\"");
    testParser("expression/string/06", "\"Hello\\nworld\"");
    testParser("expression/string/07", "\"Hello\\\nworld\"");
    testParser("expression/string/08", "\"Hello\\02World\"");
    testParser("expression/string/09", "\"Hello\\012World\"");
    testParser("expression/string/10", "\"Hello\\122World\"");
    testParser("expression/string/11", "\"Hello\\0122World\"");
    testParser("expression/string/12", "\"Hello\\312World\"");
    testParser("expression/string/13", "\"Hello\\412World\"");
    testParser("expression/string/14", "\"Hello\\812World\"");
    testParser("expression/string/15", "\"Hello\\712World\"");
    testParser("expression/string/16", "\"Hello\\0World\"");
    testParser("expression/string/17", "\"Hello\\\r\nworld\"");
    testParser("expression/string/18", "\"Hello\\1World\"");

    // Regular Expression Literals
    testParser("expression/regexp/00", "var x = /[a-z]/i");
    testParser("expression/regexp/01", "var x = /[x-z]/i");
    testParser("expression/regexp/02", "var x = /[a-c]/i");
    testParser("expression/regexp/03", "var x = /[P QR]/i");
    testParser("expression/regexp/04", "var x = /[\\]/]/");
    testParser("expression/regexp/05", "var x = /foo\\/bar/");
    testParser("expression/regexp/06", "var x = /=([^=\\s])+/g");
    testParser("expression/regexp/07", "var x = /[P QR]/\\g");
    testParser("expression/regexp/08", "var x = /42/g.test");

    // Left-Hand-Side Expression
    testParser("expression/lhs/00", "new Button");
    testParser("expression/lhs/01", "new Button()");
    testParser("expression/lhs/02", "new new foo");
    testParser("expression/lhs/03", "new new foo()");
    testParser("expression/lhs/04", "new foo().bar()");
    testParser("expression/lhs/05", "new foo[bar]");
    testParser("expression/lhs/06", "new foo.bar()");
    testParser("expression/lhs/07", "( new foo).bar()");
    testParser("expression/lhs/08", "foo(bar, baz)");
    testParser("expression/lhs/09", "(    foo  )()");
    testParser("expression/lhs/10", "universe.milkyway");
    testParser("expression/lhs/11", "universe.milkyway.solarsystem");
    testParser("expression/lhs/12", "universe.milkyway.solarsystem.Earth");
    testParser("expression/lhs/13", "universe[galaxyName, otherUselessName]");
    testParser("expression/lhs/14", "universe[galaxyName]");
    testParser("expression/lhs/15", "universe[42].galaxies");
    testParser("expression/lhs/16", "universe(42).galaxies");
    testParser("expression/lhs/17", "universe(42).galaxies(14, 3, 77).milkyway");
    testParser("expression/lhs/18", "earth.asia.Indonesia.prepareForElection(2014)");
    testParser("expression/lhs/19", "universe.if");
    testParser("expression/lhs/20", "universe.true");
    testParser("expression/lhs/21", "universe.false");
    testParser("expression/lhs/22", "universe.null");

    // Postfix Expressions
    testParser("expression/postfix/00", "x++");
    testParser("expression/postfix/01", "x--");
    testParser("expression/postfix/02", "eval++");
    testParser("expression/postfix/03", "eval--");
    testParser("expression/postfix/04", "arguments++");
    testParser("expression/postfix/05", "arguments--");

    // Unary Operators
    testParser("expression/unary/00", "++x");
    testParser("expression/unary/01", "--x");
    testParser("expression/unary/02", "++eval");
    testParser("expression/unary/03", "--eval");
    testParser("expression/unary/04", "++arguments");
    testParser("expression/unary/05", "--arguments");
    testParser("expression/unary/06", "+x");
    testParser("expression/unary/07", "-x");
    testParser("expression/unary/08", "~x");
    testParser("expression/unary/09", "!x");
    testParser("expression/unary/10", "void x");
    testParser("expression/unary/11", "delete x");
    testParser("expression/unary/12", "typeof x");

    // Multiplicative Operators
    testParser("expression/mul/00", "x * y");
    testParser("expression/mul/01", "x / y");
    testParser("expression/mul/02", "x % y");

    // Additive Operators
    testParser("expression/add/00", "x + y");
    testParser("expression/add/01", "x - y");
    testParser("expression/add/02", "\"use strict\" + 42");

    // Bitwise Shift Operator
    testParser("expression/shift/00", "x << y");
    testParser("expression/shift/01", "x >> y");
    testParser("expression/shift/02", "x >>> y");

    // Relational Operators
    testParser("expression/rel/00", "x < y");
    testParser("expression/rel/01", "x > y");
    testParser("expression/rel/02", "x <= y");
    testParser("expression/rel/03", "x >= y");
    testParser("expression/rel/04", "x in y");
    testParser("expression/rel/05", "x instanceof y");
    testParser("expression/rel/06", "x < y < z");

    // Equality Operators
    testParser("expression/eq/00", "x == y");
    testParser("expression/eq/01", "x != y");
    testParser("expression/eq/02", "x === y");
    testParser("expression/eq/03", "x !== y");

    // Binary Bitwise Operators
    testParser("expression/bit/00", "x & y");
    testParser("expression/bit/01", "x ^ y");
    testParser("expression/bit/02", "x | y");

    // Binary Expressions
    testParser("expression/binary/00", "x + y + z");
    testParser("expression/binary/01", "x - y + z");
    testParser("expression/binary/02", "x + y - z");
    testParser("expression/binary/03", "x - y - z");
    testParser("expression/binary/04", "x + y * z");
    testParser("expression/binary/05", "x + y / z");
    testParser("expression/binary/06", "x - y % z");
    testParser("expression/binary/07", "x * y * z");
    testParser("expression/binary/08", "x * y / z");
    testParser("expression/binary/09", "x * y % z");
    testParser("expression/binary/10", "x % y * z");
    testParser("expression/binary/11", "x << y << z");
    testParser("expression/binary/12", "x | y | z");
    testParser("expression/binary/13", "x & y & z");
    testParser("expression/binary/14", "x ^ y ^ z");
    testParser("expression/binary/15", "x & y | z");
    testParser("expression/binary/16", "x | y ^ z");
    testParser("expression/binary/17", "x | y & z");

    // Binary Logical Operators
    testParser("expression/logic/00", "x || y");
    testParser("expression/logic/01", "x && y");
    testParser("expression/logic/02", "x || y || z");
    testParser("expression/logic/03", "x && y && z");
    testParser("expression/logic/04", "x || y && z");
    testParser("expression/logic/05", "x || y ^ z");

    // Conditional Operator
    testParser("expression/cond/00", "y ? 1 : 2");
    testParser("expression/cond/01", "x && y ? 1 : 2");
    testParser("expression/cond/02", "x = (0) ? 1 : 2");

    // Assignment Operators
    testParser("expression/assignment/00", "x = 42");
    testParser("expression/assignment/01", "eval = 42");
    testParser("expression/assignment/02", "arguments = 42");
    testParser("expression/assignment/03", "x *= 42");
    testParser("expression/assignment/04", "x /= 42");
    testParser("expression/assignment/05", "x %= 42");
    testParser("expression/assignment/06", "x += 42");
    testParser("expression/assignment/07", "x -= 42");
    testParser("expression/assignment/08", "x <<= 42");
    testParser("expression/assignment/09", "x >>= 42");
    testParser("expression/assignment/10", "x >>>= 42");
    testParser("expression/assignment/11", "x &= 42");
    testParser("expression/assignment/12", "x ^= 42");
    testParser("expression/assignment/13", "x |= 42");

    // Complex Expression
    testParser("expression/complex", "a || b && c | d ^ e & f == g < h >>> i + j * k");

    // Block
    testParser("statement/block/00", "{ foo }");
    testParser("statement/block/01", "{ doThis(); doThat(); }");
    testParser("statement/block/02", "{}");

    // Variable Statement
    testParser("statement/var/00", "var x");
    testParser("statement/var/01", "var x, y;");
    testParser("statement/var/02", "var x = 42");
    testParser("statement/var/03", "var eval = 42, arguments = 42");
    testParser("statement/var/04", "var x = 14, y = 3, z = 1977");
    testParser("statement/var/05", "var implements, interface, package");
    testParser("statement/var/06", "var private, protected, public, static");

    // Let Statement
    testParser("statement/let/00", "let x");
    testParser("statement/let/01", "{ let x }");
    testParser("statement/let/02", "{ let x = 42 }");
    testParser("statement/let/03", "{ let x = 14, y = 3, z = 1977 }");

    // Const Statement
    testParser("statement/const/00", "const x = 42");
    testParser("statement/const/01", "{ const x = 42 }");
    testParser("statement/const/02", "{ const x = 14, y = 3, z = 1977 }");

    // Empty Statement
    testParser("statement/empty", ";");

    // Expression Statement
    testParser("statement/expression/00", "x");
    testParser("statement/expression/01", "x, y");
    testParser("statement/expression/02", "\\u0061");
    testParser("statement/expression/03", "a\\u0061");
    testParser("statement/expression/04", "\\u0061a");
    testParser("statement/expression/05", "\\u0061a ");

    // If Statement
    testParser("statement/if/00", "if (morning) goodMorning()");
    testParser("statement/if/01", "if (morning) (function(){})");
    testParser("statement/if/02", "if (morning) var x = 0;");
    testParser("statement/if/03", "if (morning) function a(){}");
    testParser("statement/if/04", "if (morning) goodMorning(); else goodDay()");

    // Iteration Statements
    testParser("statement/iteration/00", "do keep(); while (true)");
    testParser("statement/iteration/01", "do keep(); while (true);");
    testParser("statement/iteration/02", "do { x++; y--; } while (x < 10)");
    testParser("statement/iteration/03", "{ do { } while (false) false }");
    testParser("statement/iteration/04", "while (true) doSomething()");
    testParser("statement/iteration/05", "while (x < 10) { x++; y--; }");
    testParser("statement/iteration/06", "for(;;);");
    testParser("statement/iteration/07", "for(;;){}");
    testParser("statement/iteration/08", "for(x = 0;;);");
    testParser("statement/iteration/09", "for(var x = 0;;);");
    testParser("statement/iteration/10", "for(let x = 0;;);");
    testParser("statement/iteration/11", "for(var x = 0, y = 1;;);");
    testParser("statement/iteration/12", "for(x = 0; x < 42;);");
    testParser("statement/iteration/13", "for(x = 0; x < 42; x++);");
    testParser("statement/iteration/14", "for(x = 0; x < 42; x++) process(x);");
    testParser("statement/iteration/15", "for(x in list) process(x);");
    testParser("statement/iteration/16", "for (var x in list) process(x);");
    testParser("statement/iteration/17", "for (var x = 42 in list) process(x);");
    testParser("statement/iteration/18", "for (let x in list) process(x);");
    testParser("statement/iteration/19", "for (var x = y = z in q);");
    testParser("statement/iteration/20", "for (var a = b = c = (d in e) in z);");
    testParser("statement/iteration/21", "for (var i = function() { return 10 in [] } in list) process(x);");

    // continue body
    testParser("statement/continue/00", "while (true) { continue; }");
    testParser("statement/continue/01", "while (true) { continue }");
    testParser("statement/continue/02", "done: while (true) { continue done }");
    testParser("statement/continue/03", "done: while (true) { continue done; }");
    testParser("statement/continue/04", "__proto__: while (true) { continue __proto__; }");

    // break body
    testParser("statement/break/00", "while (true) { break }");
    testParser("statement/break/01", "done: while (true) { break done }");
    testParser("statement/break/02", "done: while (true) { break done; }");
    testParser("statement/break/03", "__proto__: while (true) { break __proto__; }");

    // return body
    testParser("statement/return/00", "(function(){ return })");
    testParser("statement/return/01", "(function(){ return; })");
    testParser("statement/return/02", "(function(){ return x; })");
    testParser("statement/return/03", "(function(){ return x * y })");

    // with body
    testParser("statement/with/00", "with (x) foo = bar");
    testParser("statement/with/01", "with (x) foo = bar;");
    testParser("statement/with/02", "with (x) { foo = bar }");

    // switch body
    testParser("statement/switch/00", "switch (x) {}");
    testParser("statement/switch/01", "switch (answer) { case 42: hi(); break; }");
    testParser("statement/switch/02", "switch (answer) { case 42: hi(); break; default: break }");

    // Labelled Statements
    testParser("statement/labeled/00", "start: for (;;) break start");
    testParser("statement/labeled/01", "start: while (true) break start");
    testParser("statement/labeled/02", "__proto__: test");

    // throw body
    testParser("statement/throw/00", "throw x;");
    testParser("statement/throw/01", "throw x * y");
    testParser("statement/throw/02", "throw { message: \"Error\" }");

    // try body
    testParser("statement/try/00", "try { } catch (e) { }");
    testParser("statement/try/01", "try { } catch (eval) { }");
    testParser("statement/try/02", "try { } catch (arguments) { }");
    testParser("statement/try/03", "try { } catch (e) { say(e) }");
    testParser("statement/try/04", "try { } finally { cleanup(stuff) }");
    testParser("statement/try/05", "try { doThat(); } catch (e) { say(e) }");
    testParser("statement/try/06", "try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }");

    // debugger body
    testParser("statement/debugger", "debugger;");

    // FunctionId Definition
    testParser("statement/functionDecl/00", "function hello() { sayHi(); }");
    testParser("statement/functionDecl/01", "function eval() { }");
    testParser("statement/functionDecl/02", "function arguments() { }");
    testParser("statement/functionDecl/03", "function test(t, t) { }");
    testParser("statement/functionDecl/04", "(function test(t, t) { })");
    testParser("statement/functionDecl/05", "function eval() { function inner() { \"use strict\" } }");
    testParser("statement/functionDecl/06", "function hello(a) { sayHi(); }");
    testParser("statement/functionDecl/07", "function hello(a, b) { sayHi(); }");
    testParser("statement/functionDecl/08", "var hi = function() { sayHi() };");
    testParser("statement/functionDecl/09", "var hi = function eval() { };");
    testParser("statement/functionDecl/10", "var hi = function arguments() { };");
    testParser("statement/functionDecl/11", "var hello = function hi() { sayHi() };");
    testParser("statement/functionDecl/12", "(function(){})");
    testParser("statement/functionDecl/13", "function universe(__proto__) { }");
    testParser("statement/functionDecl/14", "function test() { \"use strict\" + 42; }");

    // Automatic semicolon insertion
    testParser("asi/00", "{ x\n++y }");
    testParser("asi/01", "{ x\n--y }");
    testParser("asi/02", "var x /* comment */;");
    testParser("asi/03", "{ var x = 14, y = 3\nz; }");
    testParser("asi/04", "while (true) { continue\nthere; }");
    testParser("asi/05", "while (true) { continue // Comment\nthere; }");
    testParser("asi/06", "while (true) { continue /* Multiline\nComment */there; }");
    testParser("asi/07", "while (true) { break\nthere; }");
    testParser("asi/08", "while (true) { break // Comment\nthere; }");
    testParser("asi/09", "while (true) { break /* Multiline\nComment */there; }");
    testParser("asi/10", "(function(){ return\nx; })");
    testParser("asi/11", "(function(){ return // Comment\nx; })");
    testParser("asi/12", "(function(){ return/* Multiline\nComment */x; })");
    testParser("asi/13", "{ throw error\nerror; }");
    testParser("asi/14", "{ throw error// Comment\nerror; }");
    testParser("asi/15", "{ throw error/* Multiline\nComment */error; }");

    // Directive Prolog
    testParser("directive/00", "(function () { 'use\\x20strict'; with (i); }())");
    testParser("directive/01", "(function () { 'use\\nstrict'; with (i); }())");

    // Whitespace
    testParser("ws/00",
        "new\u0020\u0009\u000B\u000C\u00A0\u1680\u180E\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\uFEFFa");
    testParser("ws/01", "{0\n1\r2\u20283\u20294}");
  }

  @Test
  public void testLibrary() throws IOException, JsError {
    setFatal(false); // Collect the failures in an ErrorCollector

    // Whitelisted libraries to test to avoid generating too much mess
    List<String> whitelist = Arrays.asList("agility-0.1.3.js", "agility.min-0.1.3.js", "amplify-1.1.2.js",
        "amplify.core-1.1.2.js", "amplify.core.min-1.1.2.js", "amplify.min-1.1.2.js", "amplify.request-1.1.2.js",
        "amplify.request.min-1.1.2.js", "amplify.store-1.1.2.js", "amplify.store.min-1.1.2.js", "angular-1.2.5.js",
        "angular-1.2.5.min.js", "angular-1.2.5.min.ugly.js", "agility.min-0.1.3.js", "backbone-1.1.2.js",
        "backbone-min-1.1.2.js", "client.js", "client.min.js", "client.min.ugly.js", "dojo-1.9.3.js", "ember-1.7.0.js",
        "everything.js", "ext-core-3.1.0.js", "feathers-0.1.0.js", "ga.js", "infuse-v2.1.0.js", "jquery.min-1.11.1.js",
        "jquery.mobile-1.4.3.js", "jquery.mobile.min-1.4.3.js", "knockout-raw-v3.0.0.js", "knockout-v3.2.0.js",
        "knockout.debug-v3.2.0.js", "mootools-1.4.5.js", "mootools-yui-compressed-1.5.0.js", "prototype-1.7.2.0.js",
        "qunit-1.15.0.js", "sammy-latest.min-v0.7.6.js", "sammy-v0.7.6.js", "scriptaculous-1.9.0.js",
        "serenade.0.5.0.min.js", "snack-min-v1.2.3.js", "snack-qwery-min-v1.2.3.js", "snack-qwery-v1.2.3.js",
        "snack-sizzle-min-v1.2.3.js", "snack-sizzle-v1.2.3.js", "snack-slick-min-v1.2.3.js", "snack-slick-v1.2.3.js",
        "snack-v1.2.3.js", "soma-events-v2.1.0.js", "spine-v.1.3.2.js", "soma_v1.0.3.min.js", "stapes-v0.8.1.js",
        "stapes.min-v0.8.1.js", "swfobject-2.2.js", "three.min-r67.js", "underscore-1.5.2.js", "webfont-1.5.3.js",
        "yui-3.9.1.js", "yui-min-3.17.2.js");

    // Test the hell out of it... ": )
    System.out.println("Testing " + whitelist.size() + " javascript libraries.");
    long start = System.nanoTime();
    for (String jsLib : whitelist) {
      System.out.print(".");
      assertEquals("Library: " + jsLib, testLibrary(jsLib), true);
    }
    System.out.println("");
    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
    System.out.printf("Library testing time: %.1fsec\n", elapsed);
    setFatal(true); // Revert back to the default behavior
  }

  @Test
  public void testFailure() {
    testFailure("{", "Unexpected end of input");
    testFailure("}", "Unexpected token }");
    testFailure("3ea", "Unexpected token ILLEGAL");
    testFailure("3in []", "Unexpected token ILLEGAL");
    testFailure("3e", "Unexpected token ILLEGAL");
    testFailure("3e+", "Unexpected token ILLEGAL");
    testFailure("3e-", "Unexpected token ILLEGAL");
    testFailure("3x", "Unexpected token ILLEGAL");
    testFailure("3x0", "Unexpected token ILLEGAL");
    testFailure("0x", "Unexpected token ILLEGAL");
    testFailure("09", "Unexpected token ILLEGAL");
    testFailure("018", "Unexpected token ILLEGAL");
    testFailure("01a", "Unexpected token ILLEGAL");
    testFailure("3in[]", "Unexpected token ILLEGAL");
    testFailure("0x3in[]", "Unexpected token ILLEGAL");
    testFailure("\"Hello\nWorld\"", "Unexpected token ILLEGAL");
    testFailure("x\\", "Unexpected token ILLEGAL");
    testFailure("x\\u005c", "Unexpected token ILLEGAL");
    testFailure("x\\u002a", "Unexpected token ILLEGAL");
    // TODO: regex engine.
    // testFailure("var x = /(s/g", "Invalid regular expression");
    testFailure("a\\u", "Unexpected token ILLEGAL");
    testFailure("\\ua", "Unexpected token ILLEGAL");
    testFailure("/", "Invalid regular expression: missing /");
    testFailure("/test", "Invalid regular expression: missing /");
    testFailure("/test\n/", "Invalid regular expression: missing /");
    // TODO: regex engine.
    // testFailure("var x = /[a-z]/\\ux", "Unexpected token ILLEGAL");
    // TODO: regex engine.
    // testFailure("var x = /[a-z\n]/\\ux", "Invalid regular expression: missing /");
    // TODO: regex engine.
    // testFailure("var x = /[a-z]/\\\\ux", "Unexpected token ILLEGAL");
    // TODO: regex engine.
    // testFailure("var x = /[P QR]/\\\\u0067", "Unexpected token ILLEGAL");

    // testFailure("3 = 4", "Invalid left-hand side in assignment");
    // testFailure("func() = 4", "Invalid left-hand side in assignment");
    // testFailure("(1 + 1) = 10", "Invalid left-hand side in assignment");

    // testFailure("1++", "Invalid left-hand side in assignment");
    // testFailure("1--", "Invalid left-hand side in assignment");
    // testFailure("++1", "Invalid left-hand side in assignment");
    // testFailure("--1", "Invalid left-hand side in assignment");

    testFailure("for((1 + 1) in list) process(x);", "Invalid left-hand side in for-in");
    testFailure("[", "Unexpected end of input");
    testFailure("[,", "Unexpected end of input");
    testFailure("1 + {", "Unexpected end of input");
    testFailure("1 + { t:t ", "Unexpected end of input");
    testFailure("1 + { t:t,", "Unexpected end of input");
    testFailure("var x = /\n/", "Invalid regular expression: missing /");
    testFailure("var x = \"\n", "Unexpected token ILLEGAL");
    testFailure("var if = 42", "Unexpected token if");
    testFailure("i #= 42", "Unexpected token ILLEGAL");

    // testFailure("i + 2 = 42", "Invalid left-hand side in assignment");
    // testFailure("+i = 42", "Invalid left-hand side in assignment");

    testFailure("1 + (", "Unexpected end of input");
    testFailure("\n\n\n{", "Unexpected end of input");
    testFailure("\n/* Some multiline\ncomment */\n)", "Unexpected token )");
    testFailure("{ set 1 }", "Unexpected number");
    testFailure("{ get 2 }", "Unexpected number");
    testFailure("({ set: s(if) { } })", "Unexpected token if");
    testFailure("({ set s(.) { } })", "Unexpected token .");
    testFailure("({ set s() { } })", "Unexpected token )");
    testFailure("({ set: s() { } })", "Unexpected token {");
    testFailure("({ set: s(a, b) { } })", "Unexpected token {");
    testFailure("({ get: g(d) { } })", "Unexpected token {");
    testFailure("({ get i() { }, i: 42 })",
        "Object literal may not have data and accessor property with the same name");
    testFailure("({ i: 42, get i() { } })",
        "Object literal may not have data and accessor property with the same name");
    testFailure("({ set i(x) { }, i: 42 })",
        "Object literal may not have data and accessor property with the same name");
    testFailure("({ i: 42, set i(x) { } })",
        "Object literal may not have data and accessor property with the same name");
    testFailure("({ get i() { }, get i() { } })",
        "Object literal may not have multiple get/set accessors with the same name");
    testFailure("({ set i(x) { }, set i(x) { } })",
        "Object literal may not have multiple get/set accessors with the same name");
    // TODO: ES6:
    // testFailure("((a)) => 42", "Unexpected token =>");
    // testFailure("(a, (b)) => 42", "Unexpected token =>");
    // testFailure("\"use strict\"; (eval = 10) => 42", "Assignment to eval or arguments is not allowed in strict mode");
    // strict mode, using eval when IsSimpleParameterList is true
    // testFailure("\"use strict\"; eval => 42", "Parameter name eval or arguments is not allowed in strict mode");
    // strict mode, using arguments when IsSimpleParameterList is true
    // testFailure("\"use strict\"; arguments => 42", "Parameter name eval or arguments is not allowed in strict mode");
    // strict mode, using eval when IsSimpleParameterList is true
    // testFailure("\"use strict\"; (eval, a) => 42", "Parameter name eval or arguments is not allowed in strict mode");
    // strict mode, using arguments when IsSimpleParameterList is true
    // testFailure("\"use strict\"; (arguments, a) => 42", "Parameter name eval or arguments is not allowed in strict mode");
    // testFailure("(a, a) => 42", "Strict mode function may not have duplicate parameter names");
    // testFailure("\"use strict\"; (a, a) => 42", "Strict mode function may not have duplicate parameter names");
    // testFailure("\"use strict\"; (a) => 00", "Octal literals are not allowed in strict mode.");
    // testFailure("() <= 42", "Unexpected token <=");
    // testFailure("() ? 42", "Unexpected token ?");
    // testFailure("() + 42", "Unexpected token +");
    // testFailure("(10) => 00", "Unexpected token =>");
    // testFailure("(10, 20) => 00", "Unexpected token =>");
    // testFailure("\"use strict\"; (eval) => 42", "Parameter name eval or arguments is not allowed in strict mode");
    // testFailure("(eval) => { \"use strict\"; 42 }", "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function t(if) { }", "Unexpected token if");
    testFailure("function t(true) { }", "Unexpected token true");
    testFailure("function t(false) { }", "Unexpected token false");
    testFailure("function t(null) { }", "Unexpected token null");
    testFailure("function null() { }", "Unexpected token null");
    testFailure("function true() { }", "Unexpected token true");
    testFailure("function false() { }", "Unexpected token false");
    testFailure("function if() { }", "Unexpected token if");
    testFailure("a b;", "Unexpected identifier");
    testFailure("if.a;", "Unexpected token .");
    testFailure("a if;", "Unexpected token if");
    testFailure("a class;", "Unexpected reserved word");
    testFailure("break\n", "Illegal break statement");
    testFailure("break 1;", "Unexpected number");
    testFailure("continue\n", "Illegal continue statement");
    testFailure("continue 2;", "Unexpected number");
    testFailure("throw", "Unexpected end of input");
    testFailure("throw;", "Unexpected token ;");
    testFailure("throw\n", "Illegal newline after throw");
    testFailure("for (var i, i2 in {});", "Unexpected token in");
    testFailure("for ((i in {}));", "Unexpected token )");
    testFailure("for (i + 1 in {});", "Invalid left-hand side in for-in");
    testFailure("for (+i in {});", "Invalid left-hand side in for-in");
    testFailure("if(false)", "Unexpected end of input");
    testFailure("if(false) doThis(); else", "Unexpected end of input");
    testFailure("do", "Unexpected end of input");
    testFailure("while(false)", "Unexpected end of input");
    testFailure("for(;;)", "Unexpected end of input");
    testFailure("with(x)", "Unexpected end of input");
    testFailure("try { }", "Missing catch or finally after try");
    testFailure("try {} catch (42) {} ", "Unexpected number");
    testFailure("try {} catch (answer()) {} ", "Unexpected token (");
    testFailure("try {} catch (-x) {} ", "Unexpected token -");
    testFailure("\u203F = 10", "Unexpected token ILLEGAL");
    testFailure("const x = 12, y;", "Unexpected token ;");
    testFailure("const x, y = 12;", "Unexpected token ,");
    testFailure("const x;", "Unexpected token ;");
    testFailure("if(true) let a = 1;", "Unexpected token let");
    testFailure("if(true) const a = 1;", "Unexpected token const");
    testFailure("switch (c) { default: default: }", "More than one default clause in switch statement");
    testFailure("new X().\"s\"", "Unexpected string");
    testFailure("/*", "Unexpected token ILLEGAL");
    testFailure("/*\n\n\n", "Unexpected token ILLEGAL");
    testFailure("/**", "Unexpected token ILLEGAL");
    testFailure("/*\n\n*", "Unexpected token ILLEGAL");
    testFailure("/*hello", "Unexpected token ILLEGAL");
    testFailure("/*hello  *", "Unexpected token ILLEGAL");
    testFailure("\n]", "Unexpected token ]");
    testFailure("\r]", "Unexpected token ]");
    testFailure("\r\n]", "Unexpected token ]");
    testFailure("\n\r]", "Unexpected token ]");
    testFailure("//\r\n]", "Unexpected token ]");
    testFailure("//\n\r]", "Unexpected token ]");
    testFailure("/a\\\n/", "Invalid regular expression: missing /");
    testFailure("//\r \n]", "Unexpected token ]");
    testFailure("/*\r\n*/]", "Unexpected token ]");
    testFailure("/*\n\r*/]", "Unexpected token ]");
    testFailure("/*\r \n*/]", "Unexpected token ]");
    testFailure("\\\\", "Unexpected token ILLEGAL");
    testFailure("\\u005c", "Unexpected token ILLEGAL");
    testFailure("\\x", "Unexpected token ILLEGAL");
    testFailure("\\u0000", "Unexpected token ILLEGAL");
    testFailure("\u200C = []", "Unexpected token ILLEGAL");
    testFailure("\u200D = []", "Unexpected token ILLEGAL");
    testFailure("\"\\", "Unexpected token ILLEGAL");
    testFailure("\"\\u", "Unexpected token ILLEGAL");
    testFailure("try { } catch() {}", "Unexpected token )");
    testFailure("return", "Illegal return statement");
    testFailure("break", "Illegal break statement");
    testFailure("continue", "Illegal continue statement");
    testFailure("switch (x) { default: continue; }", "Illegal continue statement");
    testFailure("do { x } *", "Unexpected token *");
    testFailure("while (true) { break x; }", "Undefined label \'x\'");
    testFailure("while (true) { continue x; }", "Undefined label \'x\'");
    testFailure("x: while (true) { (function () { break x; }); }", "Undefined label \'x\'");
    testFailure("x: while (true) { (function () { continue x; }); }", "Undefined label \'x\'");
    testFailure("x: while (true) { (function () { break; }); }", "Illegal break statement");
    testFailure("x: while (true) { (function () { continue; }); }", "Illegal continue statement");
    testFailure("x: while (true) { x: while (true) { } }", "Label \'x\' has already been declared");
    testFailure("(function () { \'use strict\'; delete i; }())", "Delete of an unqualified identifier in strict mode.");
    testFailure("(function () { \'use strict\'; with (i); }())", "Strict mode code may not include a with statement");
    testFailure("function hello() {\'use strict\'; ({ i: 42, i: 42 }) }",
        "Duplicate data property in object literal not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; ({ hasOwnProperty: 42, hasOwnProperty: 42 }) }",
        "Duplicate data property in object literal not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; var eval = 10; }",
        "Variable name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; var arguments = 10; }",
        "Variable name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; try { } catch (eval) { } }",
        "Catch variable may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; try { } catch (arguments) { } }",
        "Catch variable may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; eval = 10; }",
        "Assignment to eval or arguments is not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; arguments = 10; }",
        "Assignment to eval or arguments is not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; ++eval; }",
        "Prefix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; --eval; }",
        "Prefix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; ++arguments; }",
        "Prefix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; --arguments; }",
        "Prefix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; eval++; }",
        "Postfix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; eval--; }",
        "Postfix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; arguments++; }",
        "Postfix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; arguments--; }",
        "Postfix increment/decrement may not have eval or arguments operand in strict mode");
    testFailure("function hello() {\'use strict\'; function eval() { } }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; function arguments() { } }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("function eval() {\'use strict\'; }", "Function name may not be eval or arguments in strict mode");
    testFailure("function arguments() {\'use strict\'; }", "Function name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; (function eval() { }()) }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; (function arguments() { }()) }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("(function eval() {\'use strict\'; })()", "Function name may not be eval or arguments in strict mode");
    testFailure("(function arguments() {\'use strict\'; })()",
        "Function name may not be eval or arguments in strict mode");
    testFailure("function hello() {\'use strict\'; ({ s: function eval() { } }); }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("(function package() {\'use strict\'; })()", "Use of future reserved word in strict mode");
    testFailure("function hello() {\'use strict\'; ({ i: 10, set s(eval) { } }); }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; ({ set s(eval) { } }); }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello() {\'use strict\'; ({ s: function s(eval) { } }); }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello(eval) {\'use strict\';}",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello(arguments) {\'use strict\';}",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello() { \'use strict\'; function inner(eval) {} }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function hello() { \'use strict\'; function inner(arguments) {} }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure(" \"\\1\"; \'use strict\';", "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \'use strict\'; \"\\1\"; }", "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \'use strict\'; 021; }", "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \'use strict\'; ({ \"\\1\": 42 }); }",
        "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \'use strict\'; ({ 021: 42 }); }",
        "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \"octal directive\\1\"; \"use strict\"; }",
        "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \"octal directive\\1\"; \"octal directive\\2\"; \"use strict\"; }",
        "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \"use strict\"; function inner() { \"octal directive\\1\"; } }",
        "Octal literals are not allowed in strict mode.");
    testFailure("function hello() { \"use strict\"; var implements; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var interface; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var package; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var private; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var protected; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var public; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var static; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var yield; }", "Use of future reserved word in strict mode");
    testFailure("function hello() { \"use strict\"; var let; }", "Use of future reserved word in strict mode");
    testFailure("function hello(static) { \"use strict\"; }", "Use of future reserved word in strict mode");
    testFailure("function static() { \"use strict\"; }", "Use of future reserved word in strict mode");
    testFailure("function eval(a) { \"use strict\"; }", "Function name may not be eval or arguments in strict mode");
    testFailure("function arguments(a) { \"use strict\"; }",
        "Function name may not be eval or arguments in strict mode");
    testFailure("var yield", "Unexpected token yield");
    testFailure("var let", "Unexpected token let");
    testFailure("\"use strict\"; function static() { }", "Use of future reserved word in strict mode");
    testFailure("function a(t, t) { \"use strict\"; }", "Strict mode function may not have duplicate parameter names");
    testFailure("function a(eval) { \"use strict\"; }",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("function a(package) { \"use strict\"; }", "Use of future reserved word in strict mode");
    testFailure("function a() { \"use strict\"; function b(t, t) { }; }",
        "Strict mode function may not have duplicate parameter names");
    testFailure("(function a(t, t) { \"use strict\"; })",
        "Strict mode function may not have duplicate parameter names");
    testFailure("function a() { \"use strict\"; (function b(t, t) { }); }",
        "Strict mode function may not have duplicate parameter names");
    testFailure("(function a(eval) { \"use strict\"; })",
        "Parameter name eval or arguments is not allowed in strict mode");
    testFailure("(function a(package) { \"use strict\"; })", "Use of future reserved word in strict mode");
    testFailure("__proto__: __proto__: 42;", "Label \'__proto__\' has already been declared");
    testFailure("\"use strict\"; function t(__proto__, __proto__) { }",
        "Strict mode function may not have duplicate parameter names");
    testFailure("\"use strict\"; x = { __proto__: 42, __proto__: 43 }",
        "Duplicate data property in object literal not allowed in strict mode");
    testFailure("\"use strict\"; x = { get __proto__() { }, __proto__: 43 }",
        "Object literal may not have data and accessor property with the same name");
    testFailure("var", "Unexpected end of input");
    testFailure("let", "Unexpected end of input");
    testFailure("const", "Unexpected end of input");
    testFailure("{ ;  ;  ", "Unexpected end of input");
    testFailure("function t() { ;  ;  ", "Unexpected end of input");
  }
}
