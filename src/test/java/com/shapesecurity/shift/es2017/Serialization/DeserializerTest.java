package com.shapesecurity.shift.es2017.Serialization;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.DebuggerStatement;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;

import com.shapesecurity.shift.es2017.serialization.Deserializer;
import com.shapesecurity.shift.es2017.serialization.Serializer;
import org.json.JSONException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

public class DeserializerTest {

    @Test
    public void testDeserializeArrayBinding() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("[x] = 0");
        testHelperFromScriptCode("[x,] = 0");
        testHelperFromScriptCode("[, x,,] = 0");
        testHelperFromScriptCode("var [a, a] = 0;");
        testHelperFromScriptCode("var [a]=[1];");
        testHelperFromScriptCode("var [,a] = 0;");
        testHelperFromScriptCode("var [a, ...a] = 0;");
        testHelperFromScriptCode("[x[a]=a] = b");
        testHelperFromScriptCode("[...[...a[x]]] = b");
    }

    @Test
    public void testArrayExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("[ 1, 2, 3, ]");
        testHelperFromScriptCode("[,,1,,,2,3,,]");
        testHelperFromScriptCode("[]");
        testHelperFromScriptCode("[ 0 ]");
        testHelperFromScriptCode("[ ,, 0 ]");
        testHelperFromScriptCode("[ 0, ]");
    }

    @Test
    public void testArrowExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(()=>0)");
        testHelperFromScriptCode("(...a) => 0");
        testHelperFromScriptCode("({a}) => 0");
        testHelperFromScriptCode("(a,b,...c) => 0 + 1");
        testHelperFromScriptCode("() => (a) = 0");
        testHelperFromScriptCode("a => b => c => 0");
        testHelperFromScriptCode("({x = 0}, {y = 0}, {z = 0})=>0");
    }

    @Test
    public void testDeserializeAssignmentExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("x=0");
        testHelperFromScriptCode("(x)=(0)");
        testHelperFromScriptCode("x = (y += 0)");
        testHelperFromScriptCode("x.x *= 0");
        testHelperFromScriptCode("x *= 0");
        testHelperFromScriptCode("((((((((((((((((((((((((((((((((((((((((a)))))))))))))))))))))))))))))))))))))))) = 0");
        testHelperFromScriptCode("'use strict'; arguments[0] = 0");
    }

    @Test
    public void testDeserializeBinaryExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("1 + 2");
        testHelperFromScriptCode("1 == 2");
        testHelperFromScriptCode("1 * 2");
        testHelperFromScriptCode("1 && 2");
        testHelperFromScriptCode("1 < 2");
        testHelperFromScriptCode("1 >>> 2");
        testHelperFromScriptCode("1 ^ 2");
        testHelperFromScriptCode("x || y && z");
    }

    @Test
    public void testBindingIdentifier() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("for(let in 0);");
    }

    @Test
    public void testClassDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("class A{}");
    }

    @Test
    public void testFunctionDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("function hello() { z(); }");
        testHelperFromScriptCode("function test(t, t) { }");
        testHelperFromScriptCode("function eval() { function inner() { \"use strict\" } }");
        testHelperFromScriptCode("function a() {} function a() {}");
        testHelperFromScriptCode("if (0) function a(){}");
        testHelperFromScriptCode("if (0) function a(){} else;");
        testHelperFromScriptCode("try {} catch (e) { if(0) function e(){} }");
        testHelperFromScriptCode("function arguments() { }");
    }

    @Test
    public void testGeneratorDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("function* a(){({[yield]:a}=0)}");
        testHelperFromScriptCode("function a() { function* a() {} function a() {} }");
    }

    @Test
    public void testLexicalDeclaration() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("while(true) var a");
        testHelperFromScriptCode("{ let a; }");
    }

    @Test
    public void testObjectBinding() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("({x} = 0)");
        testHelperFromScriptCode("({x,y} = 0)");
        testHelperFromScriptCode("({[a]: a} = 1)");
        testHelperFromScriptCode("({x = 0,} = 1)");
        testHelperFromScriptCode("({x: y,} = 0)");
        testHelperFromScriptCode("({x: y = z = 0} = 1)");
        testHelperFromScriptCode("({0: x, 1: x} = 0)");
        testHelperFromScriptCode("({yield = 0} = 0);");
        testHelperFromScriptCode("let {a:b=c} = 0;");
        testHelperFromScriptCode("var {a, x: {y: a}} = 0;");
        testHelperFromScriptCode("var a, {x: {y: a}} = 0;");
        testHelperFromScriptCode("(a, b, [c]) => 0");
    }

    @Test
    public void testLiterals() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("2e308");
        testHelperFromScriptCode("null");
        testHelperFromScriptCode("0");
        testHelperFromScriptCode("1.5");
        testHelperFromScriptCode("/[a-z]/i");
        testHelperFromScriptCode("/(?!.){0,}?/u");
        testHelperFromScriptCode("('x')");
        testHelperFromScriptCode("('\\\n')");
    }

    @Test
    public void testCallExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("a(b,c)");
        testHelperFromScriptCode("(    foo  )()");
        testHelperFromScriptCode("f(...a = b)");
        testHelperFromScriptCode("f(a, ...b, c)");
        testHelperFromScriptCode("f(.0)");
    }

    @Test
    public void testModule() throws IllegalAccessException, InstantiationException, JSONException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        testHelperFromAST(new Module(ImmutableList.empty(), ImmutableList.empty()));
        testHelperFromAST(new Module(ImmutableList.of(new Directive("hi"), new Directive("hello")), ImmutableList.empty()));
        testHelperFromAST(new Module(ImmutableList.of(new Directive("hi"), new Directive("hello")), ImmutableList.of(new DebuggerStatement(), new EmptyStatement())));
    }

    @Test
    public void testDeserializeScript() throws JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException, JsError {
        testHelperFromAST(new Script(ImmutableList.empty(), ImmutableList.empty()));
        testHelperFromAST(new Script(ImmutableList.of(new Directive("hi"), new Directive("hello")), ImmutableList.empty()));
        testHelperFromAST(new Script(ImmutableList.of(new Directive("hi"), new Directive("hello")), ImmutableList.of(new DebuggerStatement(), new EmptyStatement())));
    }

    @Test
    public void testClassExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(class A extends A {})");
        testHelperFromScriptCode("(class {set a(b) {'use strict';}})");
        testHelperFromScriptCode("(class {a(b) {'use strict';}})");
        testHelperFromScriptCode("(class extends (a,b) {})");
        testHelperFromScriptCode("var x = class extends (a,b) {};");
        testHelperFromScriptCode("(class {static constructor(){}})");
        testHelperFromScriptCode("(class {})");
    }

    @Test
    public void testComputedMemberExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("a[b, c]");
        testHelperFromScriptCode("a[b]");
        testHelperFromScriptCode("a[b] = b");
        testHelperFromScriptCode("(a[b]||(c[d]=e))");
        testHelperFromScriptCode("a&&(b=c)&&(d=e)");
    }

    @Test
    public void testConditionalExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("a?b:c");
        testHelperFromScriptCode("y ? 1 : 2");
        testHelperFromScriptCode("x && y ? 1 : 2");
        testHelperFromScriptCode("x = (0) ? 1 : 2");
    }

    @Test
    public void testFunctionExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(function x() { y; z() });");
        testHelperFromScriptCode("(function(){})");
        testHelperFromScriptCode("(function(a = b){})");
        testHelperFromScriptCode("(function x(y, z) { })");
        testHelperFromScriptCode("(function({a: x, a: y}){})");
        testHelperFromScriptCode("(function(a, ...b){})");
        testHelperFromScriptCode("(function([a]){})");
        testHelperFromScriptCode("label: !function(){ label:; };");
        testHelperFromScriptCode("(function({a = 0}){})");
        testHelperFromScriptCode("(function([]){})");
    }

    @Test
    public void testGroupedExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(0, a)");
        testHelperFromScriptCode("((a,a),(a,a))");
        testHelperFromScriptCode("((((((((((((((((((((((((((((((((((((((((a))))))))))))))))))))))))))))))))))))))))");
    }

    @Test
    public void testIdentifierExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("for(let yield in 0);");
        testHelperFromScriptCode("let.let");
        testHelperFromScriptCode("(let[let])");
        testHelperFromScriptCode("x");
        testHelperFromScriptCode("x;");
    }

    @Test
    public void testNewExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("new a(b,c)");
        testHelperFromScriptCode("new Button(a)");
        testHelperFromScriptCode("new new foo");
        testHelperFromScriptCode("new f(...a, b, ...c)");
        testHelperFromScriptCode("new f(...a = b)");
        testHelperFromScriptCode("new Button");
    }

    @Test
    public void testNewTargetExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("function f() { new.target; }");
        testHelperFromScriptCode("(function f(a = new.target){})");
        testHelperFromScriptCode("({ m(a = new.target){} })");
        testHelperFromScriptCode("({ set m(a = new.target){} })");
        testHelperFromScriptCode("function f() { new.target(); }");
        testHelperFromScriptCode("function f() { new new.target; }");
    }

    @Test
    public void testObjectExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("+{ }");
        testHelperFromScriptCode("({ true: 0 })");
        testHelperFromScriptCode("({ x: 1, x: 2 })");
        testHelperFromScriptCode("({ get width() { return m_width } })");
        testHelperFromScriptCode("({ get false() {} })");
        testHelperFromScriptCode("({ set width(w) { w } })");
        testHelperFromScriptCode("({a(){let a;}})");
        testHelperFromScriptCode("({ set a([{b = 0}]){}, })");
    }

    @Test
    public void testStaticMemberExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("a.b");
        testHelperFromScriptCode("a.b.c");
        testHelperFromScriptCode("a.$._.B0");
        testHelperFromScriptCode("a.true");
    }

    @Test
    public void testSuperExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(class extends B { constructor() { super() } });");
        testHelperFromScriptCode("class A extends B { constructor() { ({a: super()}); } }");
        testHelperFromScriptCode("class A extends B { constructor() { () => { super(); } } }");
        testHelperFromScriptCode("({ *a() { super.b = 0; } });");
        testHelperFromScriptCode("({ a() { super.b(); } });");
        testHelperFromScriptCode("class A { a() { () => super.b; } }");
        testHelperFromScriptCode("({ set a(x) { super.b[0] = 1; } });");
    }

    @Test
    public void testTemplateExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("``");
        testHelperFromScriptCode("`$$$${a}`");
        testHelperFromScriptCode("`abc`");
        testHelperFromScriptCode("new a()``");
        testHelperFromScriptCode("`${a}${b}`");
    }

    @Test
    public void testThisExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("this;");
    }

    @Test
    public void testUnaryExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("!a");
        testHelperFromScriptCode("!(a=b)");
        testHelperFromScriptCode("typeof a");
        testHelperFromScriptCode("~a");
    }

    @Test
    public void testUpdateExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("++a");
        testHelperFromScriptCode("x--");
    }

    @Test
    public void testYieldAndGeneratorExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("function*a(){yield\na}");
        testHelperFromScriptCode("function *a(){yield 0}");
        testHelperFromScriptCode("({set a(yield){}})");
        testHelperFromScriptCode("function *a(){yield+0}");
        testHelperFromScriptCode("function *a(){yield class{}}");
        testHelperFromScriptCode("function *a(){yield ++a}");
        testHelperFromScriptCode("function*a(){yield*a}");
        testHelperFromScriptCode("function a(){yield*a}");
    }

    @Test
    public void testExport() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromModuleCode("export * from 'a'");
        testHelperFromModuleCode("export {a} from 'a'");
        testHelperFromModuleCode("export {a,} from 'a'");
        testHelperFromModuleCode("export {a,b} from 'a'");
        testHelperFromModuleCode("export {as as as} from 'as'");
        testHelperFromModuleCode("export {if as var} from 'a';");
        testHelperFromModuleCode("export const a = 0, b = 0;");
        testHelperFromModuleCode("export let[a] = 0;");
        testHelperFromModuleCode("export let a = 0, b = 0;");
        testHelperFromModuleCode("export default function* a(){}");
        testHelperFromModuleCode("export default function a(){}");
        testHelperFromModuleCode("export class A{};0");
    }

    @Test
    public void testImport() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromModuleCode("import * as a from 'a'");
        testHelperFromModuleCode("import a, {} from 'c'");
        testHelperFromModuleCode("import a, {function as c} from 'c'");
        testHelperFromModuleCode("import a, {as as c} from 'c'");
        testHelperFromModuleCode("import a, {b,c} from 'd'");
        testHelperFromModuleCode("import a, {b,c,} from 'd'");
    }

    @Test
    public void testBlockStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("{}");
        testHelperFromScriptCode("{ foo }");
        testHelperFromScriptCode("{ doThis(); doThat(); }");
    }

    @Test
    public void testBreakStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("done: while (true) { break done }");
        testHelperFromScriptCode("while (true) { break }");
    }

    @Test
    public void testContinueStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("done: while (true) { continue done }");
        testHelperFromScriptCode("while (true) { continue }");
        testHelperFromScriptCode("a: while (0) { continue \n b; }");
        testHelperFromScriptCode("a: do continue a; while(1);");
    }

    @Test
    public void testDebuggerStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("debugger");
        testHelperFromScriptCode("debugger;");
    }

    @Test
    public void testDoWhileStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("do keep(); while (true);");
        testHelperFromScriptCode("do ; while (true)");
        testHelperFromScriptCode("do {} while (true)");
    }

    @Test
    public void testEmptyStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode(";");
    }

    @Test
    public void testExpressionStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("x, y");
    }

    @Test
    public void testForInStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("for(x in list) process(x);");
        testHelperFromScriptCode("for (let x in list) process(x);");
        testHelperFromScriptCode("for (var x in list) process(x);");
        testHelperFromScriptCode("for(a.b in c);");
    }

    @Test
    public void testForOfStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("for (var x of list) process(x);");
        testHelperFromScriptCode("for(a of b);");
        testHelperFromScriptCode("for(let [a] of b);");
    }

    @Test
    public void testForStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("for(x, y;;);");
        testHelperFromScriptCode("for(var x = 0;;);");
        testHelperFromScriptCode("for(x; x < 0; x++);");
        testHelperFromScriptCode("for(var x = 0, y = 1;;);");
        testHelperFromScriptCode("for(var a;b;c);");
        testHelperFromScriptCode("for(;b;c);");
    }

    @Test
    public void testIfStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("if (a) b; else c;");
        testHelperFromScriptCode("if (morning) (function(){})");
        testHelperFromScriptCode("if (a) b;");
    }

    @Test
    public void testLabeledStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("start: for (;;) break start");
        testHelperFromScriptCode("start: while (true) break start");
        testHelperFromScriptCode("a:{break a;}");
    }

    @Test
    public void testReturnStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("(function(){ return })");
        testHelperFromScriptCode("(function(){ return x * y })");
        testHelperFromScriptCode("_ => { return 0; }");
    }

    @Test
    public void testSwitchAndDefaultStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("switch (x) {}");
        testHelperFromScriptCode("switch (answer) { case 0: hi(); break; }");
        testHelperFromScriptCode("switch(a){case 1:}");
        testHelperFromScriptCode("switch(a){case 1:default:case 2:}");
        testHelperFromScriptCode("switch (answer) { case 0: hi(); break; default: break }");
    }

    @Test
    public void testTryCatchFinallyStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("try{ } catch (e) { }");
        testHelperFromScriptCode("try { doThat(); } catch (e) { say(e) }");
        testHelperFromScriptCode("try { } finally { cleanup(stuff) }");
        testHelperFromScriptCode("try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }");
    }

    @Test
    public void testThrowStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("throw this");
        testHelperFromScriptCode("throw x");
        testHelperFromScriptCode("throw {}");
    }

    @Test
    public void testVariableDeclarationStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("var private, protected, public");
        testHelperFromScriptCode("var eval = 0, arguments = 1");
        testHelperFromScriptCode("var x = 0, y = 1, z = 2");
        testHelperFromScriptCode("{ let x = 0, y = 1, z = 2 }");
        testHelperFromScriptCode("var yield;");
        testHelperFromScriptCode("let[let]=0");
    }

    @Test
    public void testWhileStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("while(true) doSomething()");
        testHelperFromScriptCode("while (x < 10) {x++; y--; }");
    }

    @Test
    public void testWithStatement() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
        testHelperFromScriptCode("with (x) foo");
        testHelperFromScriptCode("with (x) { foo }");
    }

    /******************
     * HELPER METHODS *
     ******************/

    private void testHelperFromScriptCode(String jsCode) throws JsError, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
        testHelperFromAST(Parser.parseScript(jsCode));
    }

    private void testHelperFromModuleCode(String jsCode) throws JsError, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
        testHelperFromAST(Parser.parseModule(jsCode));
    }


    private void testHelperFromAST(Program nodeOriginal) throws IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
        String nodeSerialized = Serializer.serialize(nodeOriginal);
        Node nodeDeserialized = Deserializer.deserialize(nodeSerialized);
        assertTrue(nodeOriginal.equals(nodeDeserialized));
    }

}
