package com.shapesecurity.shift.serialization;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import org.json.JSONException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

public class DeserializerTest {

  @Test
  public void testDeserializeArrayBinding() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("[x] = 0");
    testHelperFromCode("[x,] = 0");
    testHelperFromCode("[, x,,] = 0");
    testHelperFromCode("var [a, a] = 0;");
    testHelperFromCode("var [a]=[1];");
    testHelperFromCode("var [,a] = 0;");
    testHelperFromCode("var [a, ...a] = 0;");
    // TODO fix issue with computed member expression
//    testHelperFromCode("[x[a]=a] = b");
//    testHelperFromCode("[...[...a[x]]] = b");
  }

  @Test
  public void testArrayExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("[ 1, 2, 3, ]");
    testHelperFromCode("[,,1,,,2,3,,]");
    testHelperFromCode("[]");
    testHelperFromCode("[ 0 ]");
    testHelperFromCode("[ ,, 0 ]");
    testHelperFromCode("[ 0, ]");
  }

  @Test
  public void testArrowExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(()=>0)");
    testHelperFromCode("(...a) => 0");
    testHelperFromCode("({a}) => 0");
    testHelperFromCode("(a,b,...c) => 0 + 1");
    testHelperFromCode("() => (a) = 0");
    testHelperFromCode("a => b => c => 0");
    testHelperFromCode("({x = 0}, {y = 0}, {z = 0})=>0");
  }

  @Test
  public void testDeserializeAssignmentExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("x=0");
    testHelperFromCode("(x)=(0)");
    testHelperFromCode("x = (y += 0)");
    testHelperFromCode("x.x *= 0");
    testHelperFromCode("x *= 0");
    testHelperFromCode("((((((((((((((((((((((((((((((((((((((((a)))))))))))))))))))))))))))))))))))))))) = 0");
    // TODO fix issue with static member expression
//    testHelperFromCode("'use strict'; arguments[0] = 0");
  }

  @Test
  public void testDeserializeBinaryExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("1 + 2");
    testHelperFromCode("1 == 2");
    testHelperFromCode("1 * 2");
    testHelperFromCode("1 && 2");
    testHelperFromCode("1 < 2");
    testHelperFromCode("1 >>> 2");
    testHelperFromCode("1 ^ 2");
    testHelperFromCode("x || y && z");
  }

  @Test
  public void testBindingIdentifier() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("for(let in 0);");
  }

  @Test
  public void testClassDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("class A{}");
  }

  @Test
  public void testFunctionDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("function hello() { z(); }");
    testHelperFromCode("function test(t, t) { }");
    testHelperFromCode("function eval() { function inner() { \"use strict\" } }");
    testHelperFromCode("function a() {} function a() {}");
    testHelperFromCode("if (0) function a(){}");
    testHelperFromCode("if (0) function a(){} else;");
    testHelperFromCode("try {} catch (e) { if(0) function e(){} }");
    testHelperFromCode("function arguments() { }");
  }

  @Test
  public void testGeneratorDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("function* a(){({[yield]:a}=0)}");
    testHelperFromCode("function a() { function* a() {} function a() {} }");
  }

  @Test
  public void testLexicalDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("while(true) var a");
    testHelperFromCode("{ let a; }");
  }

  @Test
  public void testObjectBinding() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("({x} = 0)");
    testHelperFromCode("({x,y} = 0)");
    testHelperFromCode("({[a]: a} = 1)");
    testHelperFromCode("({x = 0,} = 1)");
    testHelperFromCode("({x: y,} = 0)");
    testHelperFromCode("({x: y = z = 0} = 1)");
    testHelperFromCode("({0: x, 1: x} = 0)");
    testHelperFromCode("({yield = 0} = 0);");
    testHelperFromCode("let {a:b=c} = 0;");
    testHelperFromCode("var {a, x: {y: a}} = 0;");
    testHelperFromCode("var a, {x: {y: a}} = 0;");
    testHelperFromCode("(a, b, [c]) => 0");
  }

  @Test
  public void testLiterals() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("2e308");
    testHelperFromCode("null");
    testHelperFromCode("0");
    testHelperFromCode("1.5");
    testHelperFromCode("/[a-z]/i");
    testHelperFromCode("/(?!.){0,}?/u");
    testHelperFromCode("('x')");
    testHelperFromCode("('\\\n')");
  }

  @Test
  public void testCallExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("a(b,c)");
    testHelperFromCode("(    foo  )()");
    testHelperFromCode("f(...a = b)");
    testHelperFromCode("f(a, ...b, c)");
    testHelperFromCode("f(.0)");
  }

  @Test
  public void testModule() throws IllegalAccessException, InstantiationException, JSONException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
    testHelperFromAST(new Module(ImmutableList.nil(), ImmutableList.nil()));
    testHelperFromAST(new Module(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.nil()));
    testHelperFromAST(new Module(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.list(new DebuggerStatement(), new EmptyStatement())));
  }

  @Test
  public void testDeserializeScript() throws JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException, JsError {
    testHelperFromAST(new Script(ImmutableList.nil(), ImmutableList.nil()));
    testHelperFromAST(new Script(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.nil()));
    testHelperFromAST(new Script(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.list(new DebuggerStatement(), new EmptyStatement())));
  }

  @Test
  public void testClassExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(class A extends A {})");
    testHelperFromCode("(class {set a(b) {'use strict';}})");
    testHelperFromCode("(class {a(b) {'use strict';}})");
    testHelperFromCode("(class extends (a,b) {})");
    testHelperFromCode("var x = class extends (a,b) {};");
    testHelperFromCode("(class {static constructor(){}})");
    testHelperFromCode("(class {})");
  }

  @Test
  public void testComputedMemberExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("a[b, c]");
    testHelperFromCode("a[b]");
//    testHelperFromCode("a[b] = b"); // TODO: fix computed member expression
//    testHelperFromCode("(a[b]||(c[d]=e))");
    testHelperFromCode("a&&(b=c)&&(d=e)");
  }

  @Test
  public void testConditionalExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("a?b:c");
    testHelperFromCode("y ? 1 : 2");
    testHelperFromCode("x && y ? 1 : 2");
    testHelperFromCode("x = (0) ? 1 : 2");
  }

  @Test
  public void testFunctionExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(function x() { y; z() });");
    testHelperFromCode("(function(){})");
    testHelperFromCode("(function(a = b){})");
    testHelperFromCode("(function x(y, z) { })");
    testHelperFromCode("(function({a: x, a: y}){})");
    testHelperFromCode("(function(a, ...b){})");
    testHelperFromCode("(function([a]){})");
    testHelperFromCode("label: !function(){ label:; };");
    testHelperFromCode("(function({a = 0}){})");
    testHelperFromCode("(function([]){})");
  }

  @Test
  public void testGroupedExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(0, a)");
    testHelperFromCode("((a,a),(a,a))");
    testHelperFromCode("((((((((((((((((((((((((((((((((((((((((a))))))))))))))))))))))))))))))))))))))))");
  }

  @Test
  public void testIdentifierExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("for(let yield in 0);");
    testHelperFromCode("let.let");
    testHelperFromCode("(let[let])");
    testHelperFromCode("x");
    testHelperFromCode("x;");
  }

  @Test
  public void testNewExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("new a(b,c)");
    testHelperFromCode("new Button(a)");
    testHelperFromCode("new new foo");
    testHelperFromCode("new f(...a, b, ...c)");
    testHelperFromCode("new f(...a = b)");
    testHelperFromCode("new Button");
  }

  @Test
  public void testNewTargetExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("function f() { new.target; }");
    testHelperFromCode("(function f(a = new.target){})");
    testHelperFromCode("({ m(a = new.target){} })");
    testHelperFromCode("({ set m(a = new.target){} })");
    testHelperFromCode("function f() { new.target(); }");
    testHelperFromCode("function f() { new new.target; }");
  }

  @Test
  public void testObjectExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("+{ }");
    testHelperFromCode("({ true: 0 })");
    testHelperFromCode("({ x: 1, x: 2 })");
    testHelperFromCode("({ get width() { return m_width } })");
    testHelperFromCode("({ get false() {} })");
    testHelperFromCode("({ set width(w) { w } })");
    testHelperFromCode("({a(){let a;}})");
    testHelperFromCode("({ set a([{b = 0}]){}, })");
  }

  @Test
  public void testStaticMemberExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("a.b");
    testHelperFromCode("a.b.c");
    testHelperFromCode("a.$._.B0");
    testHelperFromCode("a.true");
  }

  @Test
  public void testSuperExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(class extends B { constructor() { super() } });");
    testHelperFromCode("class A extends B { constructor() { ({a: super()}); } }");
    testHelperFromCode("class A extends B { constructor() { () => { super(); } } }");
    testHelperFromCode("({ *a() { super.b = 0; } });");
    testHelperFromCode("({ a() { super.b(); } });");
//    testHelperFromCode("({ set a(x) { super.b[0] = 1; } });"); // TODO: fix computed member expression
    testHelperFromCode("class A { a() { () => super.b; } }");
  }

  @Test
  public void testTemplateExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("``");
    testHelperFromCode("`$$$${a}`");
    testHelperFromCode("`abc`");
    testHelperFromCode("new a()``");
    testHelperFromCode("`${a}${b}`");
  }

  @Test
  public void testThisExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("this;");
  }

  @Test
  public void testUnaryExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("!a");
    testHelperFromCode("!(a=b)");
    testHelperFromCode("typeof a");
    testHelperFromCode("~a");
  }

  @Test
  public void testUpdateExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("++a");
    testHelperFromCode("x--");
  }

  @Test
  public void testYieldAndGeneratorExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("function*a(){yield\na}");
    testHelperFromCode("function *a(){yield 0}");
    testHelperFromCode("({set a(yield){}})");
    testHelperFromCode("function *a(){yield+0}");
    testHelperFromCode("function *a(){yield class{}}");
    testHelperFromCode("function *a(){yield ++a}");
    testHelperFromCode("function*a(){yield*a}");
    testHelperFromCode("function a(){yield*a}");
  }

  @Test
  public void testExport() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("");
    testHelperFromCode("export * from 'a'");
    testHelperFromCode("export {a} from 'a'");
    testHelperFromCode("export {a,} from 'a'");
    testHelperFromCode("export {a,b} from 'a'");
    testHelperFromCode("export {as as as} from 'as'");
    testHelperFromCode("export {if as var} from 'a';");
    testHelperFromCode("export const a = 0, b = 0;");
    testHelperFromCode("export let[a] = 0;");
    testHelperFromCode("export let a = 0, b = 0;");
    testHelperFromCode("export default function* a(){}");
    testHelperFromCode("export default function a(){}");
    testHelperFromCode("export class A{};0");
  }

  @Test
  public void testImport() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("import * as a from 'a'");
    testHelperFromCode("import a, {} from 'c'");
    testHelperFromCode("import a, {function as c} from 'c'");
    testHelperFromCode("import a, {as as c} from 'c'");
    testHelperFromCode("import a, {b,c} from 'd'");
    testHelperFromCode("import a, {b,c,} from 'd'");
  }

  @Test
  public void testBlockStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("{}");
    testHelperFromCode("{ foo }");
    testHelperFromCode("{ doThis(); doThat(); }");
  }

  @Test
  public void testBreakStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("done: while (true) { break done }");
    testHelperFromCode("while (true) { break }");
  }

  @Test
  public void testContinueStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("done: while (true) { continue done }");
    testHelperFromCode("while (true) { continue }");
    testHelperFromCode("a: while (0) { continue \n b; }");
    testHelperFromCode("a: do continue a; while(1);");
  }

  @Test
  public void testDebuggerStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("debugger");
    testHelperFromCode("debugger;");
  }

  @Test
  public void testDoWhileStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("do keep(); while (true);");
    testHelperFromCode("do ; while (true)");
    testHelperFromCode("do {} while (true)");
  }

  @Test
  public void testEmptyStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode(";");
  }

  @Test
  public void testExpressionStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("x, y");
  }

  @Test
  public void testForInStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("for(x in list) process(x);");
    testHelperFromCode("for (let x in list) process(x);");
    testHelperFromCode("for (var x in list) process(x);");
    testHelperFromCode("for(a.b in c);");
  }

  @Test
  public void testForOfStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("for (var x of list) process(x);");
    testHelperFromCode("for(a of b);");
    testHelperFromCode("for(let [a] of b);");
  }

  @Test
  public void testForStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("for(x, y;;);");
    testHelperFromCode("for(var x = 0;;);");
    testHelperFromCode("for(x; x < 0; x++);");
    testHelperFromCode("for(var x = 0, y = 1;;);");
    testHelperFromCode("for(var a;b;c);");
    testHelperFromCode("for(;b;c);");
  }

  @Test
  public void testIfStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("if (a) b; else c;");
    testHelperFromCode("if (morning) (function(){})");
    testHelperFromCode("if (a) b;");
  }

  @Test
  public void testLabeledStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("start: for (;;) break start");
    testHelperFromCode("start: while (true) break start");
    testHelperFromCode("a:{break a;}");
  }

  @Test
  public void testReturnStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("(function(){ return })");
    testHelperFromCode("(function(){ return x * y })");
    testHelperFromCode("_ => { return 0; }");
  }

  @Test
  public void testSwitchAndDefaultStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("switch (x) {}");
    testHelperFromCode("switch (answer) { case 0: hi(); break; }");
    testHelperFromCode("switch(a){case 1:}");
    testHelperFromCode("switch(a){case 1:default:case 2:}");
    testHelperFromCode("switch (answer) { case 0: hi(); break; default: break }");
  }

  @Test
  public void testTryCatchFinallyStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("try{ } catch (e) { }");
    testHelperFromCode("try { doThat(); } catch (e) { say(e) }");
    testHelperFromCode("try { } finally { cleanup(stuff) }");
    testHelperFromCode("try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }");
  }

  @Test
  public void testThrowStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("throw this");
    testHelperFromCode("throw x");
    testHelperFromCode("throw {}");
  }

  @Test
  public void testVariableDeclarationStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("var private, protected, public");
    testHelperFromCode("var eval = 0, arguments = 1");
    testHelperFromCode("var x = 0, y = 1, z = 2");
    testHelperFromCode("{ let x = 0, y = 1, z = 2 }");
    testHelperFromCode("var yield;");
    testHelperFromCode("let[let]=0");
  }

  @Test
  public void testWhileStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("while(true) doSomething()");
    testHelperFromCode("while (x < 10) {x++; y--; }");
  }

  @Test
  public void testWithStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("with (x) foo");
    testHelperFromCode("with (x) { foo }");
  }

  /******************
   * HELPER METHODS *
   ******************/

  private void testHelperFromCode(String jsCode) throws JsError, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
    Script nodeOriginal = Parser.parseScript(jsCode);
    testHelperFromAST(nodeOriginal);
  }

  private void testHelperFromAST(Node nodeOriginal) throws IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
    String nodeSerialized = Serializer.serialize(nodeOriginal);
    Deserializer deserializer = new Deserializer();
    Node nodeDeserialized = deserializer.deserialize(nodeSerialized);
    assertTrue(nodeOriginal.equals(nodeDeserialized));
  }

}
