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

package com.shapesecurity.shift.codegen;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CodeGenTest extends TestCase {

  @NotNull
  private static Script statement(@NotNull Statement stmt) {
    return new Script(ImmutableList.<Directive>nil(), ImmutableList.list(stmt));
  }

//  private void testLibrary(String fileName) throws JsError, IOException {
//    String source = readLibrary(fileName);
//    Script script = Parser.parse(source);
//    String code = CodeGen.codeGen(script);
//    Script actual = Parser.parse(code);
//    assertEquals(fileName, script, actual);
//  }

  private void testShift(@NotNull String expected, @NotNull Script script) {
    assertEquals(expected, CodeGen.codeGen(script));
  }

  private void testShift(@NotNull String expected, @NotNull Module module) {
    assertEquals(expected, CodeGen.codeGen(module));
  }

  private void test(String source) throws JsError {
    Module module = Parser.parseModule(source);
    String code = CodeGen.codeGen(module);
    assertEquals(source, code);
    assertEquals(module, Parser.parseModule(code));
  }

  private void testScript(String source) throws JsError {
    Script script = Parser.parseScript(source);
    String code = CodeGen.codeGen(script);
    assertEquals(source, code);
    assertEquals(script, Parser.parseScript(code));
  }

  private void test(String expected, String source) throws JsError {
    Module module = Parser.parseModule(source);
    String code = CodeGen.codeGen(module);
    assertEquals(expected, code);
    assertEquals(module, Parser.parseModule(code));
  }

//  private void testPretty(String source) throws JsError {
//    Script script = Parser.parse(source);
//    String code = CodeGen.codeGen(script, true);
//    assertEquals(source, code);
//    code = CodeGen.codeGen(script, FormattedCodeRepFactory.INSTANCE);
//    assertEquals(source, code);
//  }

  @Test
  public void testCodeGenDirectives() throws JsError {
    test("\"use strict\"");
    test("\"use\\u0020strict\"");
    testShift("\"use\\u0020strict\"", new Script(ImmutableList.<Directive>list(new Directive("use strict")), ImmutableList.<Statement>nil()));
  }

  @Test
  public void testArrayExpression() throws JsError {
    test("[]");
    test("[a]");
    test("[a]", "[a,]");
    test("[a,b,c]", "[a,b,c,]");
    test("[a,,]");
    test("[a,,,]");
    test("[[a]]");
    test("[(a,a)]");
  }

  @Test
  public void testObjectExpression() throws JsError {
    test("({})");
    test("({a:1})", "({a:1,})");
    test("({}.a--)");
    test("({1:1})", "({1.0:1})");
    test("({a:b})", "({a:b})");
    test("({get a(){;}})");
    test("({set a(param){;}})");
    test("({get a(){;},set a(param){;},b:1})");
    test("({a:(a,b)})");
    test("({2e308:0})", "({2e308:0})");
  }

  @Test
  public void testSequence() throws JsError {
    test("a,b,c,d");
  }

  @Test
  public void testAssignment() throws JsError {
    test("a=b");
    test("a+=b");
    test("a*=b");
    test("a%=b");
    test("a<<=b");
    test("a>>=b");
    test("a>>>=b");
    test("a/=b");
    test("a|=b");
    test("a^=b");
    test("a,b^=b");
    test("b^=b,b");
    test("b^=(b,b)");
  }

  @Test
  public void testConditional() throws JsError {
    test("a?b:c");
    test("a?b?c:d:e");
    test("a?b:c?d:e");
    test("a?b?c:d:e?f:g");
    test("(a?b:c)?d:e");
    test("(a,b)?(c,d):(e,f)");
    test("a?b=c:d");
    test("a?b=c:d=e");
    test("a||b?c=d:e=f");
    test("(a=b)?c:d");
    test("a||(b?c:d)");
    test("a?b||c:d");
    test("a?b:c||d");
  }

  @Test
  public void testLogicalOr() throws JsError {
    test("a||b");
  }

  @Test
  public void testLogicalAnd() throws JsError {
    test("a||b");
  }

  @Test
  public void testBitwiseOr() throws JsError {
    test("a|b");
  }

  @Test
  public void testBitwiseAnd() throws JsError {
    test("a&b");
  }

  @Test
  public void testBitwiseXor() throws JsError {
    test("a^b");
    test("a^b&b");
    test("(a^b)&b");
  }

  @Test
  public void testEquality() throws JsError {
    test("a==b");
    test("a!=b");
    test("a==b");
    test("a!=b");
    test("a==b==c");
    test("a==(b==c)");
  }

  @Test
  public void testRelational() throws JsError {
    test("a<b");
    test("a<=b");
    test("a>b");
    test("a>=b");
    test("a instanceof b");
    test("a in b");
    test("a==b<b");
    test("(a==b)<b");
    test("for((b in b);;);");
    test("for((b in b);b in b;b in b);");
    test("for(var a=(b in b);b in b;b in b);");
    test("for(var a=(b in b),c=(b in b);b in b;b in b);");
    test("for(b in c in d);");
  }

  @Test
  public void testShift() throws JsError {
    test("a<<b");
    test("a>>b");
    test("a>>>b");
    test("a<<b<<c");
    test("a<<(b<<c)");
    test("a<<b<c");
    test("a<<b<c");
    test("a<<(b<c)");
  }

  @Test
  public void testAdditive() throws JsError {
    test("a+b");
    test("a-b");
    test("a+(b+b)");
    test("a+(b<<b)");
    test("a+b<<b");
  }

  @Test
  public void testMultiplicative() throws JsError {
    test("a*b");
    test("a/b");
    test("a%b");
    test("a%b%c");
    test("a%(b%c)");
    test("a+b%c");
    test("(a+b)%c");
  }

  @Test
  public void testPrefix() throws JsError {
    test("+a");
    test("-a");
    test("!a");
    test("~a");
    test("typeof a");
    test("void a");
    test("delete a");
    test("++a");
    test("--a");
    test("+ ++a");
    test("- --a");
    test("a+ +a");
    test("a-a");
    test("typeof-a");
    test("!!a");
    test("!!(a+a)");
  }

  @Test
  public void testPostfix() throws JsError {
    test("a++");
    test("a--");
  }

  @Test
  public void testNewCallMember() throws JsError {
    test("new a");
    test("new a(a)");
    test("new a(a,b)");
    test("a()");
    test("a(a)");
    test("a(a,b)");
    test("a.a");
    test("a[a]");
    test("new a", "new a()");
    test("new a(a)");
    test("(new a).a", "new a().a");
    test("new a(a).v");
    test("new(a(a).v)");
    test("(new a)()");
    test("(new new a(a).a.a).a", "(new (new a(a).a).a).a");
    test("new((new a)().a)", "new((new a)()).a");
    test("new a.a");
    test("new(a().a)");
  }

  @Test
  public void testLeftHandSideExpression() throws JsError {
    test("(+this)++");
    test("(+this)--");
    test("this--");
    test("this=0");
    test("(+this)=0");
    test("for((+this)in 0);");
    test("for(this in 0);");
  }

  @Test
  public void testXMLStyleComment() throws JsError {
    test("1", "1<!--2");
    test("1<! --2");
    // lines start with "-->" cannot exist in our code gen.
  }

  @Test
  public void testPrimary() throws JsError {
    test("0");
    test("1");
    test("2");
    test("(\"a\")", "('a')");
    test("(\"'\")", "('\\'')");
    test(";\"a\"");
    test(";\"\\\"\"");
    test("/a/");
    test("/a/i");
    test("/a/ig");
    test("/a\\s/ig");
    test("/a\\r/ig");
    test("/a\\r/ instanceof 3");
    test("/a\\r/g instanceof 3");
    test("3/ /a/g");
    test("true");
    test("false");
    test("null");
    test("null", "nul\\u006c");
    test("(function(){})");
  }

  @Test
  public void testFloatingPoint() throws JsError {
    test("1.1.valueOf()");
    test("15..valueOf()");
    test("1..valueOf()");
    test("1e+300.valueOf()");
    test("8000000000000000..valueOf()");
    test("10..valueOf()", "1e1.valueOf()");
    test("1.3754889325393114", "1.3754889325393114");
    test("1.3754889325393114e+24", "0x0123456789abcdefABCDEF");
    test("4.185580496821357e+298", "4.1855804968213567e298");
    test("5.562684646268003e-308", "5.5626846462680035e-308");
    test("5.562684646268003e-309", "5.5626846462680035e-309");
    test("2147483648", "2147483648.0");
    test("1e-7");
    test("1e-8");
    test("1e-9");
    test("2e308", "1e1000");
    test("-2e308", "-1e1000");
  }

  @Test
  public void testBlockStatement() throws JsError {
    test("{}");
    test("{{}}");
    test("{a:{}}");
    test("{a;b}", "{a\nb\n}");
  }

  @Test
  public void testBreakStatement() throws JsError {
    test("while(1)break", "while(1)break");
    test("while(1){break;break}", "while(1){break;break;}");
    test("a:while(1){break;break a}", "a:while(1){break;break a;}");
    test("switch(1){case 1:break}", "switch(1){case 1:break;}");
  }

  @Test
  public void testContinueStatement() throws JsError {
    test("while(1)continue", "while(1)continue");
    test("while(1){continue;continue}", "while(1){continue;continue;}");
    test("a:while(1){continue;continue a}", "a:while(1){continue;continue a;}");
  }

  @Test
  public void testDebuggerStatement() throws JsError {
    test("debugger", "debugger");
  }

  @Test
  public void testDoWhileStatement() throws JsError {
    test("do;while(1)", "do;while(1)");
    test("do{}while(1)", "do{}while(1)");
    test("do debugger;while(1)");
    test("do if(3){}while(1)");
    test("do 3;while(1)", "do(3);while(1)");
  }

  @Test
  public void testExpressionStatement() throws JsError {
    test("a");
    test("({a:3})");
    test("do({a:3});while(1)");
    test("~{a:3}");
    test("({a:3}+1)");
    test("a:~{a:3}");
    test("~function(){}");
    test("~function(){}()");
    test("function name(){}");
  }

  @Test
  public void testForInStatement() throws JsError {
    test("for(var a in 1);");
    test("for(var a=3 in 1);");
    test("for(var a=(3 in 5)in 1);");
    test("for(var a=(3 in 5==7 in 4)in 1);");
    test("for(var a=1+1 in 1);");
  }

  @Test
  public void testForStatement() throws JsError {
    test("for(var i=(1 in[]);;);");
    test("for(var i=(1 in[]),b,c=(1 in[]);;);");
    test("for((1 in[]);;);");
    test("for(1*(1 in[]);;);");
    test("for(1*(1+1 in[]);;);");
    test("for(1*(1+1 in[]);;);");
    test("for(1*(1+(1 in[]));;);");
  }

  @Test
  public void testIfStatement() throws JsError {
    test("if(a);");
    test("if(a)b");
    test("if(a)if(a)b");
    test("if(a){}");
    test("if(a);else;");
    test("if(a);else{}");
    test("if(a){}else{}");
    test("if(a)if(a){}else{}else{}");
    IdentifierExpression IDENT = new IdentifierExpression("a");
    EmptyStatement EMPTY = new EmptyStatement();

    IfStatement MISSING_ELSE = new IfStatement(IDENT, EMPTY, Maybe.<Statement>nothing());
    testShift("if(a){a:if(a);}else;", statement(new IfStatement(IDENT, new LabeledStatement("a",
      MISSING_ELSE), Maybe.<Statement>just(EMPTY))));
    testShift("if(a){if(a);else if(a);}else;", statement(new IfStatement(IDENT, new IfStatement(IDENT, EMPTY,
      Maybe.<Statement>just(MISSING_ELSE)), Maybe.<Statement>just(EMPTY))));
    testShift("if(a){if(a);}else;", statement(new IfStatement(IDENT, MISSING_ELSE, Maybe.<Statement>just(EMPTY))));
    testShift("if(a){while(a)if(a);}else;", statement(new IfStatement(IDENT, new WhileStatement(IDENT, MISSING_ELSE),
      Maybe.<Statement>just(EMPTY))));
    testShift("if(a){with(a)if(a);}else;", statement(new IfStatement(IDENT, new WithStatement(IDENT, MISSING_ELSE),
      Maybe.<Statement>just(EMPTY))));
    testShift("if(a){for(;;)if(a);}else;", statement(new IfStatement(IDENT, new ForStatement(Maybe.nothing(),
      Maybe.nothing(), Maybe.nothing(), MISSING_ELSE), Maybe.<Statement>just(EMPTY))));
    testShift("if(a){for(a in a)if(a);}else;",
      statement(
        new IfStatement(
          IDENT,
          new ForInStatement(new BindingIdentifier("a"), IDENT, MISSING_ELSE),
          Maybe.<Statement>just(EMPTY))));
  }

  @Test
  public void testLabeledStatement() throws JsError {
    test("a:;");
    test("a:b:;");
  }

  @Test
  public void testReturnStatement() throws JsError {
    test("function a(){return}");
    test("function a(){return 0}");
    test("function a(){return function a(){return 0}}");
  }

  @Test
  public void testSwitchStatement() throws JsError {
    test("switch(0){}");
    test("switch(0){default:}");
    test("switch(0){case 0:default:}");
    test("switch(0){case 0:a;default:c:b}");
  }

  @Test
  public void testThrowStatement() throws JsError {
    test("throw 0");
    test("throw(1<1)+1");
  }

  @Test
  public void testTryStatement() throws JsError {
    test("try{}catch(a){}");
    test("try{}catch(a){}finally{}");
    test("try{}finally{}");
  }

  @Test
  public void testVariableDeclarationStatement() throws JsError {
    test("var a=0");
    test("var a=0,b=0");
    test("var a=(0,0)");
    test("var a=(0,0,0)");
    test("var a");
    test("var a,b");
    test("var a=\"\"in{}");
  }

  @Test
  public void testWhileStatement() throws JsError {
    test("while(0);");
    test("while(0)while(0);");
  }

  @Test
  public void testWithStatement() throws JsError {
    test("with(0);");
    test("with(0)with(0);");
  }

  @Test
  public void testFunctionBody() throws JsError {
    test("");
    test("\"use strict\"");
    test(";\"use strict\"");
    testShift("\"use strict\"", new Script(ImmutableList.list(new Directive("use strict")), ImmutableList.nil()));
    testShift("(\"use strict\")", statement(new ExpressionStatement(new LiteralStringExpression("use strict"))));
    testShift("(\"use strict\");;", new Script(
      ImmutableList.nil(),
      ImmutableList.list(
        new ExpressionStatement(new LiteralStringExpression("use strict")),
        new EmptyStatement()
      )
    ));
    testShift("\"use strict\";;", new Script(
      ImmutableList.list(new Directive("use strict")),
      ImmutableList.list(new EmptyStatement())
    ));
    testShift("\"use strict\";(\"use strict\")", new Script(
      ImmutableList.list(new Directive("use strict")),
      ImmutableList.list(new ExpressionStatement(new LiteralStringExpression("use strict")))
    ));
    testShift("\"use strict\";;\"use strict\"", new Script(
      ImmutableList.list(new Directive("use strict")),
      ImmutableList.list(new EmptyStatement(), new ExpressionStatement(new LiteralStringExpression("use strict")))
    ));
  }

//  @Test
//  public void testPrettyPrintSemi() throws JsError {
//    testPretty("var a=0;\n");
//    testPretty("var a=0;\nvar b=0;\n");
//  }
//
//  @Test
//  public void testPrettyPrintBracket() throws JsError {
//    testPretty("function a(){\nreturn;\n}");
//  }

//  @Test
//  public void testLibrary() throws IOException, JsError {
//    ImmutableList<String> jsFiles = ImmutableList.nil();
//    setFatal(false); // Collect the failures in an ErrorCollector
//
//    // Get a list of the js files within the resources directory to process
//    File[] files = new File(getPath(".").toString()).listFiles();
//    if (files == null) {
//      System.out.println("Error retrieving list of javascript libraries.");
//      return;
//    }
//    for (File file : files) {
//      if (file.isFile() && file.getName().endsWith(".js")) {
//        jsFiles = ImmutableList.cons(file.getName(), jsFiles);
//      }
//    }
//
//    // Test the hell out of it... ": )
//    long start = System.nanoTime();
//    System.out.println("Testing " + jsFiles.length + " javascript libraries.");
//    for (String jsLib : jsFiles) {
//      System.out.print(".");
//      testLibrary(jsLib);
//    }
//    System.out.println("");
//    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
//    System.out.printf("Library testing time: %.1fsec\n", elapsed);
//    setFatal(true); // Revert back to the default behavior
//  }
}
