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

package com.shapesecurity.shift.parser;

import com.shapesecurity.shift.TestBase;
import com.shapesecurity.shift.utils.Utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenizerTest extends TestBase {
  public static final double NANOS_TO_SECONDS = 1e-9;

  private static String serializeTokens(String source) throws JsError {
    ArrayList<Token> tokens = Tokenizer.tokenize(source);
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = 0; i < tokens.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(toJson(tokens.get(i)));
    }
    sb.append(']');
    return sb.toString();
  }

  private static void testFailure(String source) {
    try {
      Tokenizer.tokenize(source);
    } catch (JsError ignored) {
      return;
    }
    assert false : "No exception.";
  }

  private static String toJson(Token token) {
    return '{' + "\"type\":\"" + token.type.klass.getName() + "\"," + "\"value\":" + Utils.escapeStringLiteral(
        token.slice.toString()) + ',' + "\"range\":[" + token.slice.start + ',' + token.slice.end + ']' + '}';
  }

  @Test
  public void testSimpleScanner() throws JsError {
    Tokenizer.tokenize("\n    42");
    Tokenizer.tokenize("\r\n    42");
    Tokenizer.tokenize("// a \r");
    Tokenizer.tokenize("// a \r\n");
    Tokenizer.tokenize("// 你好\r\n");
    Tokenizer.tokenize("/* 你好*/");
    Tokenizer.tokenize("enum yield class super export import extends");
    Tokenizer.tokenize("1.");
    Tokenizer.tokenize("1.e5");
    Tokenizer.tokenize("1.e+1");
    Tokenizer.tokenize("1.e-1");

    ArrayList<Token> tokens = Tokenizer.tokenize("this");
    assertEquals(tokens.size(), 1);
    Token token = tokens.get(0);
    assertEquals(token.type, TokenType.THIS);
  }

  private void testSource(String msg, String source, String expected) throws JsError, IOException {
    String actual = serializeTokens(source);
    boolean match = actual.equals(expected);
    assertEquals(msg, match, true);
  }

  private void testLibrary(String testCase) throws IOException, JsError {
    String source = readLibrary(testCase);
    testCase = testCase.substring(0, testCase.lastIndexOf('.'));
    // TODO: this just plain does not work
    if (!Files.exists(getPath("syntax/" + testCase + ".tokens"))) {
      Files.write(getPath("syntax/" + testCase + ".tokens"), serializeTokens(source).getBytes(StandardCharsets.UTF_8));
      return;
    }
    String expected = readFile("syntax/" + testCase + ".tokens");
    assertEquals(expected.isEmpty(), false);
    testSource(testCase + ".js", source, expected);
  }

  private void testRegExp(String source, boolean containsRegExp) throws Exception {
    ArrayList<Token> tokens = Tokenizer.tokenize(source);
    for (Token token : tokens) {
      if (token.type == TokenType.REGEXP) {
        assert containsRegExp : "Token stream should not contain regular expression.";
        assertEquals("/42/g", token.slice.getString());
        return;
      }
    }

    assert !containsRegExp : "Token stream should contain regular expression.";
  }

  @Test
  public void testJsRegexp() throws Exception {
    testRegExp("/42", false);
    testRegExp("/\n42", false);
    testRegExp("/42/g", true);
    testRegExp("[]/42/g", false);
    testRegExp("[/42/g", true);
    testRegExp("return /42/g", true);
    testRegExp("(1)/42/g", false);
    testRegExp("with (1)/42/g", true);
    testRegExp("if (1)/42/g", true);
    testRegExp("while (1)/42/g", true);
    testRegExp("for (1)/42/g", true);
    testRegExp("return (1)/42/g", false);
    testRegExp("function (1) { /42/g", true);
    testRegExp("function (1) {} /42/g", true);
    testRegExp("~function (1) {} /42/g", false);
    testRegExp("~function a(1) {} /42/g", false);
    testRegExp("if (1) {} /42/g", true);
    testRegExp("~ {} /42/g", false);
    testRegExp("} /42/g", true);
    // Func check token.
    testRegExp("+function () {} /42/g", false);
    testRegExp("-function () {} /42/g", false);
    testRegExp("*function () {} /42/g", false);
    testRegExp("[]/function () {} /42/g", false);
    testRegExp("%function () {} /42/g", false);
    testRegExp("<<function () {} /42/g", false);
    testRegExp(">>>function () {} /42/g", false);
    testRegExp(">>function () {} /42/g", false);
    testRegExp("&function () {} /42/g", false);
    testRegExp("|function () {} /42/g", false);
    testRegExp("&&function () {} /42/g", false);
    testRegExp("||function () {} /42/g", false);
    testRegExp("++function () {} /42/g", false);
    testRegExp("--function () {} /42/g", false);
    testRegExp("^function () {} /42/g", false);
    testRegExp("<function () {} /42/g", false);
    testRegExp(">function () {} /42/g", false);
    testRegExp("<=function () {} /42/g", false);
    testRegExp(">=function () {} /42/g", false);
    testRegExp("!=function () {} /42/g", false);
    testRegExp("!==function () {} /42/g", false);
    testRegExp("==function () {} /42/g", false);
    testRegExp("===function () {} /42/g", false);
    testRegExp("=function () {} /42/g", false);
    testRegExp("(function () {} /42/g", false);
    testRegExp("[function () {} /42/g", false);
    testRegExp(":function () {} /42/g", false);
    testRegExp("{function () {} /42/g", false);
    testRegExp("!function () {} /42/g", false);
    testRegExp("?function () {} /42/g", false);
    testRegExp("in function () {} /42/g", false);
    testRegExp("typeof function () {} /42/g", false);
    testRegExp("instanceof function () {} /42/g", false);
    testRegExp("new function () {} /42/g", false);
    testRegExp("return function () {} /42/g", false);
    testRegExp("case function () {} /42/g", false);
    testRegExp("delete function () {} /42/g", false);
    testRegExp("throw function () {} /42/g", false);
    testRegExp("void function () {} /42/g", false);
    testRegExp("= function () {} /42/g", false);
    testRegExp("+= function () {} /42/g", false);
    testRegExp("-= function () {} /42/g", false);
    testRegExp("*= function () {} /42/g", false);
    testRegExp("%= function () {} /42/g", false);
    testRegExp("<<= function () {} /42/g", false);
    testRegExp(">>= function () {} /42/g", false);
    testRegExp(">>>= function () {} /42/g", false);
    testRegExp("a /= function () {} /42/g", false);
    testRegExp("&= function () {} /42/g", false);
    testRegExp("^= function () {} /42/g", false);
    testRegExp("|= function () {} /42/g", false);
    testRegExp(", function () {} /42/g", false);
    testRegExp(".function () {} /42/g", true);
    testRegExp(".function () {} /42/g", true);
    testRegExp("debugger function () {} /42/g", true);
    testRegExp("do function () {} /42/g", true);
    testRegExp("else function () {} /42/g", true);
    testRegExp("finally function () {} /42/g", true);
    testRegExp("for function () {} /42/g", true);
    testRegExp("try function () {} /42/g", true);
    testRegExp("var function () {} /42/g", true);
    testRegExp("null function () {} /42/g", true);
    testRegExp("'use strict'\n function () {} /42/g", true);
  }

  @Test
  public void testJsErrors() {
    testFailure("/*");
    testFailure("/*\r");
    testFailure("/*\r\n");
    testFailure("/*\u2028");
    testFailure("/*\u2029");
    testFailure("/**");
    testFailure("\\");
    testFailure("\\u");
    testFailure("\\x");
    testFailure("\\o");
    testFailure("\\u1");
    testFailure("\\u12");
    testFailure("\\u113");
    testFailure("a\\uz   ");
    testFailure("a\\u1z  ");
    testFailure("a\\u11z ");
    testFailure("a\\u111z");
    testFailure("a\\");
    testFailure("a\\u");
    testFailure("a\\x");
    testFailure("a\\o");
    testFailure("a\\u1");
    testFailure("a\\u12");
    testFailure("a\\u113");
    testFailure("'\\03");
    testFailure("'\\x");
    testFailure("'\\x1");
    testFailure("'\\x1   ");
    testFailure("'\\x12  ");
    testFailure("'\n");
    testFailure("'\\");
    testFailure("＊");
    testFailure("1.a");
    testFailure("1.e");
    testFailure("1.e+");
    testFailure("1.e+z");
    testFailure("/\\\n42");
    testFailure("0x");
    testFailure("0xz");
    testFailure("0x1z");
    testFailure("0a");
    testFailure("08a");
    testFailure("\u0008");
  }

  @Test
  public void testComplex() throws JsError, IOException {
    Map<String, String> testCases = new HashMap<>();

    testCases.put("tokenize(/42/)",
        "[{\"type\":\"Identifier\",\"value\":\"tokenize\",\"range\":[0,8]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[8,9]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[9,13]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[13,14]}]");
    testCases.put("if (false) { /42/ }",
        "[{\"type\":\"Keyword\",\"value\":\"if\",\"range\":[0,2]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[3,4]},{\"type\":\"Boolean\",\"value\":\"false\",\"range\":[4,9]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[9,10]},{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[11,12]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[13,17]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[18,19]}]");
    testCases.put("with (false) /42/",
        "[{\"type\":\"Keyword\",\"value\":\"with\",\"range\":[0,4]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[5,6]},{\"type\":\"Boolean\",\"value\":\"false\",\"range\":[6,11]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[11,12]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[13,17]}]");
    testCases.put("(false) /42/",
        "[{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[0,1]},{\"type\":\"Boolean\",\"value\":\"false\",\"range\":[1,6]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[6,7]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[8,9]},{\"type\":\"Numeric\",\"value\":\"42\",\"range\":[9,11]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[11,12]}]");
    testCases.put("function f(){} /42/",
        "[{\"type\":\"Keyword\",\"value\":\"function\",\"range\":[0,8]},{\"type\":\"Identifier\",\"value\":\"f\",\"range\":[9,10]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[10,11]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[11,12]},{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[12,13]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[13,14]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[15,19]}]");
    testCases.put("function(){} /42",
        "[{\"type\":\"Keyword\",\"value\":\"function\",\"range\":[0,8]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[8,9]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[9,10]},{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[10,11]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[11,12]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[13,14]},{\"type\":\"Numeric\",\"value\":\"42\",\"range\":[14,16]}]");
    testCases.put("{} /42",
        "[{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[0,1]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[1,2]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[3,4]},{\"type\":\"Numeric\",\"value\":\"42\",\"range\":[4,6]}]");
    testCases.put("[function(){} /42]",
        "[{\"type\":\"Punctuator\",\"value\":\"[\",\"range\":[0,1]},{\"type\":\"Keyword\",\"value\":\"function\",\"range\":[1,9]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[9,10]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[10,11]},{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[11,12]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[12,13]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[14,15]},{\"type\":\"Numeric\",\"value\":\"42\",\"range\":[15,17]},{\"type\":\"Punctuator\",\"value\":\"]\",\"range\":[17,18]}]");
    testCases.put(";function f(){} /42/",
        "[{\"type\":\"Punctuator\",\"value\":\";\",\"range\":[0,1]},{\"type\":\"Keyword\",\"value\":\"function\",\"range\":[1,9]},{\"type\":\"Identifier\",\"value\":\"f\",\"range\":[10,11]},{\"type\":\"Punctuator\",\"value\":\"(\",\"range\":[11,12]},{\"type\":\"Punctuator\",\"value\":\")\",\"range\":[12,13]},{\"type\":\"Punctuator\",\"value\":\"{\",\"range\":[13,14]},{\"type\":\"Punctuator\",\"value\":\"}\",\"range\":[14,15]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[16,20]}]");
    testCases.put("void /42/",
        "[{\"type\":\"Keyword\",\"value\":\"void\",\"range\":[0,4]},{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[5,9]}]");
    testCases.put("/42/", "[{\"type\":\"RegularExpression\",\"value\":\"/42/\",\"range\":[0,4]}]");
    testCases.put("[a] / b",
        "[{\"type\":\"Punctuator\",\"value\":\"[\",\"range\":[0,1]},{\"type\":\"Identifier\",\"value\":\"a\",\"range\":[1,2]},{\"type\":\"Punctuator\",\"value\":\"]\",\"range\":[2,3]},{\"type\":\"Punctuator\",\"value\":\"/\",\"range\":[4,5]},{\"type\":\"Identifier\",\"value\":\"b\",\"range\":[6,7]}]");

    for (String source : testCases.keySet()) {
      testSource("testComplex", source, testCases.get(source));
    }
  }

  @Test
  public void testLibrary() throws IOException, JsError {
    setFatal(false); // Collect the failures in an ErrorCollector

    // Whitelisted libraries to test to avoid generating too much mess
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    java.util.List<String> whitelist = Arrays.asList(
      "angular-1.2.5.js",
      "angular-1.2.5.min.js",
      "angular-1.2.5.min.ugly.js",
      "backbone-1.1.0.js",
      "backbone-min-1.1.2.js",
      "dojo-1.10.0.js",
      "ember-1.7.0.js",
      "everything-0.0.4.js",
      "ga.js",
      "jquery.mobile.min-1.4.3.js",
      "knockout-v3.2.0.js",
      "mootools-1.4.5.js",
      "mootools-yui-compressed-1.5.0.js",
      "prototype-1.7.2.0.js",
      "qunit-v1.14.0.js",
      "scriptaculous-1.9.0.js",
      "swfobject-2.2.js",
      "three.min-r67.js",
      "underscore-1.5.2.js",
      "webfont-1.5.3.js",
      "yui-3.12.0.js",
      "yui-min-3.17.2.js"
    );

    // Test the hell out of it... ": )
    System.out.println("Testing " + whitelist.size() + " javascript libraries.");
    long start = System.nanoTime();
    for (String jsLib : whitelist) {
      System.out.print(".");
      testLibrary(jsLib);
    }
    System.out.println("");
    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
    System.out.printf("Library testing time: %.1fsec\n", elapsed);
    setFatal(true); // Revert back to the default behavior
  }
}
