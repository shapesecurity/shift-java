package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.codegen.CodeGen;
import com.shapesecurity.shift.es2017.ast.ComputedMemberExpression;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;
import org.junit.Test;

public class CloneReducerTest extends CloneReducerTestCase {
    @Test
    public void testScript() throws JsError {
        cloneTestScript("");
    }

    @Test
    public void testModule() throws JsError {
        cloneTestModule("");
    }

    @Test
    public void testClassDeclaration() throws JsError {
        cloneTestScript("class A{}");
        cloneTestScript("class A{;}");
    }

    @Test
    public void testFunctionDeclaration() throws JsError {
        cloneTestScript("function hello() { z(); }");
        cloneTestScript("function test(t, t) { }");
        cloneTestScript("function test() { \"use strict\"\n + 0; }");
    }

    @Test
    public void testGeneratorDeclaration() throws JsError {
        cloneTestScript("function* a(){}");
        cloneTestScript("function* a(){yield}");
        cloneTestScript("function* a(x){yield x}");
    }

    @Test
    public void testLexicalDeclaration() throws JsError {
        cloneTestScript("let a");
    }

    @Test
    public void testArrayBinding() throws JsError{
        cloneTestScript("[x] = 0");
        cloneTestScript("[x,] = 0");
        cloneTestScript("[x,,] = 0");
        cloneTestScript("[[x]] = 0");
        cloneTestScript("[x, y, ...z] = 0");
        cloneTestScript("[, x,,] = 0");
        cloneTestScript("[x, x] = 0");
        cloneTestScript("[x, ...x] = 0");
        cloneTestScript("[x.a=a] = b");
        cloneTestScript("[x[a]=a] = b");
        cloneTestScript("[] = 0");
        cloneTestScript("[{a=0},{a=0}] = 0");
        cloneTestScript("[,...a]=0");
    }

    @Test
    public void testObjectBinding() throws JsError{
        cloneTestScript("({x} = 0)");
        cloneTestScript("({x,} = 0)");
        cloneTestScript("({x,y} = 0)");
        cloneTestScript("({[a]: a} = 1)");
        cloneTestScript("({x = 0} = 1)");
        cloneTestScript("({x = 0,} = 1)");
        cloneTestScript("({x: y} = 0)");
        cloneTestScript("({0: x, 1: x} = 0)");
        cloneTestScript("({x: y = 0} = 1)");
    }

    @Test
    public void testBindingIdentifier() throws JsError{
        cloneTestScript("a = 0");
        cloneTestScript("abc123 = 0");
        cloneTestScript("var a");
        cloneTestScript("for(a in 0);");
        cloneTestScript("for(a of 0);");
    }

    @Test
    public void testLiteralInfinityExpression() throws JsError{
        cloneTestScript("2e308");
    }

    @Test
    public void testLiteralNullExpression() throws JsError{
        cloneTestScript("null");
    }

    @Test
    public void testNumericExpression() throws JsError{
        cloneTestScript("0");
        cloneTestScript(".14");
        cloneTestScript("3.14159");
        cloneTestScript("6.02214179e+23");
        cloneTestScript("1.492417830e-10");
    }

    @Test
    public void testLiteralRegExpExpression() throws JsError{
        cloneTestScript("/a/");
        cloneTestScript("/\\0/");
        cloneTestScript("/\\1/u");
        cloneTestScript("/a/;");
        cloneTestScript("/a/i");
        cloneTestScript("/a/i;");
        cloneTestScript("/[--]/");
        cloneTestScript("/[a-z]/i");
        cloneTestScript("/[x-z]/i");
        cloneTestScript("/[a-c]/i");
        cloneTestScript("/[P QR]/i");
        cloneTestScript("/[\\]/]/");
        cloneTestScript("/foo\\/bar/");
        cloneTestScript("/=([^=\\s])+/g");
        cloneTestScript("/(()(?:\\2)((\\4)))/;");
        cloneTestScript("/((((((((((((.))))))))))))\\12/;");
        cloneTestScript("/\\.\\/\\\\/u");
        cloneTestScript("/\\uD834\\uDF06\\u{1d306}/u");
        cloneTestScript("/\\uD834/u");
        cloneTestScript("/\\uDF06/u");
        cloneTestScript("/[-a-]/");
        cloneTestScript("/[-\\-]/u");
        cloneTestScript("/[-a-b-]/");
        cloneTestScript("/[]/");
        cloneTestScript("/0/g.test");
        cloneTestScript("/{/;");
        cloneTestScript("/}/;");
        cloneTestScript("/}?/u;");
        cloneTestScript("/{*/u;");
        cloneTestScript("/{}/;");
        cloneTestScript("/.{.}/;");
        cloneTestScript("/[\\w-\\s]/;");
        cloneTestScript("/[\\s-\\w]/;");
        cloneTestScript("/(?=.)*/;");
        cloneTestScript("/(?!.){0,}?/;");
        cloneTestScript("/(?!.){0,}?/u");
    }

    @Test
    public void testLiteralStringExpression() throws JsError{
        cloneTestScript("('x')");
        cloneTestScript("('\\\\\\'')");
        cloneTestScript("(\"x\")");
        cloneTestScript("(\"\\\\\\\"\")");
        cloneTestScript("('\\\r')");
        cloneTestScript("('\\\r\n')");
        cloneTestScript("('\\\n')");
        cloneTestScript("('\\\u2028')");
        cloneTestScript("('\\\u2029')");
        cloneTestScript("('\u202a')");
        cloneTestScript("('\\0')");
        cloneTestScript("'use strict'; ('\\0')");
        cloneTestScript("'use strict'; ('\\0x')");
        cloneTestScript("('\\11')");
        cloneTestScript("('\\111')");
        cloneTestScript("('\\1111')");
        cloneTestScript("('\\5111')");
        cloneTestScript("('\\a')");
        cloneTestScript("('\\`')");
        cloneTestScript("('\\u{0}')");
        cloneTestScript("('\\u{10FFFF}')");
        cloneTestScript("('\\01')");
        cloneTestScript("('\\1')");
        cloneTestScript("('\\2111')");
        cloneTestScript("('\\5a')");
        cloneTestScript("('\\7a')");
        cloneTestScript("('\\u{00F8}')");
        cloneTestScript("('\\u{0000000000F8}')");
    }

    @Test
    public void testArrayExpression() throws JsError{
        cloneTestScript("[]");
        cloneTestScript("[ 0 ]");
        cloneTestScript("[ ,, 0 ]");
        cloneTestScript("[ 1, 2, 3, ]");
        cloneTestScript("[ 1, 2,, 3, ]");
        cloneTestScript("[,,1,,,2,3,,]");
        cloneTestScript("[a, ...(b=c)]");
        cloneTestScript("[,...a]");
    }

    @Test
    public void testArrowExpression() throws JsError{
        cloneTestScript("() => 0");
        cloneTestScript("(...a) => 0");
        cloneTestScript("() => {}");
        cloneTestScript("(a) => 0");
        cloneTestScript("([a]) => 0");
        cloneTestScript("a => 0");
        cloneTestScript("({a}) => 0");
        cloneTestScript("(x)=>{'use strict';}");
        cloneTestScript("eval => 'use strict'");
    }

    @Test
    public void testAssignmentExpression() throws JsError{
        cloneTestScript("a=0;");
        cloneTestScript("x *= 0");
        cloneTestScript("x.x *= 0");
        cloneTestScript("x /= 0");
        cloneTestScript("x %= 0");
        cloneTestScript("x += 0");
        cloneTestScript("x -= 0");
        cloneTestScript("x <<= 0");
        cloneTestScript("x >>= 0");
        cloneTestScript("x >>>= 0");
        cloneTestScript("x &= 0");
        cloneTestScript("x ^= 0");
        cloneTestScript("x |= 0");
        cloneTestScript("x = (y += 0)");
    }

    @Test
    public void testBinaryExpression() throws JsError{
        cloneTestScript("1+2");
        cloneTestScript("x & y");
        cloneTestScript("x ^ y");
        cloneTestScript("x | y");
        cloneTestScript("x * y");
        cloneTestScript("x / y");
        cloneTestScript("x % y");
        cloneTestScript("x + y");
        cloneTestScript("x - y");
        cloneTestScript("x << y");
        cloneTestScript("x >> y");
        cloneTestScript("x >>> y");
        cloneTestScript("x < y");
        cloneTestScript("x > y");
        cloneTestScript("x <= y");
        cloneTestScript("x >= y");
        cloneTestScript("x in y");
        cloneTestScript("x instanceof y");
        cloneTestScript("x == y");
        cloneTestScript("x != y");
        cloneTestScript("x === y");
        cloneTestScript("x !== y");
        cloneTestScript("(a, e=0)");
    }

    @Test
    public void testCallExpression() throws JsError{
        cloneTestScript("a()");
        cloneTestScript("a(b,c)");
        cloneTestScript("f(...a)");
        cloneTestScript("f(...a = b)");
        cloneTestScript("f(...a, ...b)");
        cloneTestScript("f(a, ...b, c)");
        cloneTestScript("f(...a, b, ...c)");
        cloneTestScript("f(....0)");
        cloneTestScript("f(.0)");
    }

    @Test
    public void testClassExpression() throws JsError{
        cloneTestScript("(class {})");
        cloneTestScript("(class extends A {})");
        cloneTestScript("(class A extends A {})");
        cloneTestScript("(class {;})");
        cloneTestScript("(class {;;a(){}})");
        cloneTestScript("(class {set a(b) {}})");
        cloneTestScript("(class {get a() {}})");
        cloneTestScript("(class{[3+5](){}})");
        cloneTestScript("(class {static(){}})");
        cloneTestScript("(class {static constructor(){}})");
    }

    @Test
    public void testComputedMemberExpression() throws JsError{
        cloneTestScript("a[b]");
    }

    @Test
    public void testConditionalExpression() throws JsError{
        cloneTestScript("a?b:c");
    }

    @Test
    public void testFunctionExpression() throws JsError {
        cloneTestScript("(function(){})");
        cloneTestScript("(function x() { y; z() });");
        cloneTestScript("(function x(y, z) { })");
        cloneTestScript("(function(a = b){})");
        cloneTestScript("(function(...a){})");
        cloneTestScript("(function(a, ...b){})");
        cloneTestScript("(function({a: x, a: y}){})");
        cloneTestScript("(function([a]){})");
        cloneTestScript("(function({a = 0}){})");
        cloneTestScript("(function([]){})");
    }

    @Test
    public void testGeneratorExpression() throws JsError {
        cloneTestScript("function* g(){}");
        cloneTestScript("(function*(){});");
    }

    @Test
    public void testIdentifierExpression() throws JsError{
        cloneTestScript("x");
        cloneTestScript("日本語");
        cloneTestScript("\uD800\uDC00");
        cloneTestScript("T\u203F");
        cloneTestScript("T\u200C");
        cloneTestScript("T\u200D");
        cloneTestScript("\u2163\u2161");
        cloneTestScript("\u2163\u2161\u200A");
    }

    @Test
    public void testNewExpression() throws JsError{
        cloneTestScript("new a(b,c)");
        cloneTestScript("new new foo");
        cloneTestScript("new f(...a)");
        cloneTestScript("new f(...a = b)");
        cloneTestScript("new f(...a, ...b)");
        cloneTestScript("new f(a, ...b, c)");
        cloneTestScript("new f(...a, b, ...c)");
    }

    @Test
    public void testNewTargetExpression() throws JsError{
        cloneTestScript("function f() { new.target; }");
    }

    @Test
    public void testObjectExpression() throws JsError{
        cloneTestScript("({})");
        cloneTestScript("({ x: 1, x: 2 })");
        cloneTestScript("({ get width() { return m_width } })");
        cloneTestScript("({ set width(w) { w } })");
        cloneTestScript("({ __proto__: 2 })");
        cloneTestScript("({a})");
        cloneTestScript("({a, b: 0, c})");
        cloneTestScript("({a(){}})");
        cloneTestScript("({a(b){}})");
        cloneTestScript("({a(b,...c){}})");
        cloneTestScript("({a(b,c){}})");
        cloneTestScript("({a(b,c){let d;}})");
    }

    @Test
    public void testStrictMemberExpression() throws JsError{
        cloneTestScript("a.b");
        cloneTestScript("a.b.c");
    }

    @Test
    public void testSuperExpression() throws JsError{
        cloneTestScript("class A extends B { constructor() { super() } }");
        cloneTestScript("({ a() { super.b(); } });");
    }

    @Test
    public void testTemplateExpression() throws JsError{
        cloneTestScript("``");
        cloneTestScript("`abc`");
        cloneTestScript("`\n`");
        cloneTestScript("`\r\n\t\n`");
        cloneTestScript("`\\``");
        cloneTestScript("`$$$`");
        cloneTestScript("`$$$${a}`");
        cloneTestScript("`${a}`");
        cloneTestScript("`${a}$`");
        cloneTestScript("`${a}${b}`");
        cloneTestScript("````");
        cloneTestScript("``````");
        cloneTestScript("a``");
        cloneTestScript("a()``");
        cloneTestScript("new a``");
        cloneTestScript("new a()``");
    }

    @Test
    public void testThisExpression() throws JsError{
        cloneTestScript("this;");
    }

    @Test
    public void testUnaryExpression() throws JsError{
        cloneTestScript("!a");
        cloneTestScript("!(a=b)");
        cloneTestScript("typeof a");
        cloneTestScript("void a");
        cloneTestScript("delete a");
        cloneTestScript("+a");
        cloneTestScript("~a");
        cloneTestScript("-a");
    }

    @Test
    public void testUpdateExpression() throws JsError{
        cloneTestScript("++a");
        cloneTestScript("--a");
        cloneTestScript("x++");
        cloneTestScript("x--");
    }

    @Test
    public void testYieldExpression() throws JsError{
        cloneTestScript("function *a(){yield}");
        cloneTestScript("function *a(){yield 0}");
    }

    @Test
    public void testYieldGeneratorExpression() throws JsError{
        cloneTestScript("function*a(){yield*a}");
    }

    @Test
    public void testInteractions() throws JsError{
        cloneTestScript("0 .toString");
        cloneTestScript("0.0.toString");
        cloneTestScript("0..toString");
        cloneTestScript("01.toString");
        cloneTestScript("a.b(b, c)");
        cloneTestScript("a[b](b,c)");
        cloneTestScript("new foo().bar()");
        cloneTestScript("new foo[bar]");
        cloneTestScript("new foo.bar()");
        cloneTestScript("(new foo).bar()");
        cloneTestScript("a[0].b");
        cloneTestScript("a(0).b");
        cloneTestScript("a(0).b(14, 3, 77).c");
        cloneTestScript("a.b.c(2014)");
        cloneTestScript("a || b && c | d ^ e & f == g < h >>> i + j * k");
        cloneTestScript("while (i-->0) {}");
        cloneTestScript("var x = 1<!--foo");
        cloneTestScript("class A extends B { a() { [super.b] = c } }");
        cloneTestScript("class A extends B { a() { ({b: super[c]} = d) } }");
    }

    @Test
    public void testExport() throws JsError{
        cloneTestModule("export * from 'a'");
        cloneTestModule("export {} from 'a'");
        cloneTestModule("export {a} from 'a'");
        cloneTestModule("export {a,b} from 'a'");
        cloneTestModule("export {a as b} from 'a'");
        cloneTestModule("export var a = 0, b;");
        cloneTestModule("export const a = 0, b = 0;");
        cloneTestModule("export let a = 0, b = 0;");
        cloneTestModule("export let[a] = 0;");
        cloneTestModule("export default function (){} /* no semi */ false");
        cloneTestModule("export default class {} /* no semi */ false");
        cloneTestModule("export default function a(){}");
        cloneTestModule("export default class a{}");
        cloneTestModule("export default function* a(){}");
        cloneTestModule("export default 0;0");
        cloneTestModule("export function f(){};0");
        cloneTestModule("export class A{};0");
        cloneTestModule("export {};0");
    }

    @Test
    public void testImport() throws JsError{
        cloneTestModule("import * as a from 'a'");
        cloneTestModule("import a, {} from 'a'");
        cloneTestModule("import a, * as b from 'a'");
        cloneTestModule("import a, {b} from 'c'");
        cloneTestModule("import a, {b as c} from 'c'");
        cloneTestModule("import {a} from 'b'");
        cloneTestModule("import {a as b} from 'c'");
        cloneTestModule("import a, {b,c} from 'd'");
    }

    @Test
    public void testGeneratorMethod() throws JsError{
        cloneTestScript("({*a(){}})");
        cloneTestScript("({*a(b){}})");
    }

    @Test
    public void testPropertyName() throws JsError{
        cloneTestScript("({0x0:0})");
        cloneTestScript("({2e308:0})");
        cloneTestScript("({get b() {}})");
        cloneTestScript("({set c(x) {}})");
        cloneTestScript("({__proto__:0})");
        cloneTestScript("({get __proto__() {}})");
        cloneTestScript("({set __proto__(x) {}})");
        cloneTestScript("({get __proto__() {}, set __proto__(x) {}})");
        cloneTestScript("({[\"nUmBeR\"+9]:\"nein\"})");
        cloneTestScript("({[2*308]:0})");
        cloneTestScript("({get [6+3]() {}, set [5/4](x) {}})");
        cloneTestScript("({[6+3]() {}})");
        cloneTestScript("({3() {}})");
        cloneTestScript("({\"moo\"() {}})");
        cloneTestScript("({\"oink\"(that, little, piggy) {}})");
    }

    @Test
    public void testBlockStatement() throws JsError{
        cloneTestScript("{ foo }");
        cloneTestScript("{ doThis(); doThat(); }");
        cloneTestScript("{}");
    }

    @Test
    public void testBreakStatement() throws JsError{
        cloneTestScript("while (true) { break }");
        cloneTestScript("done: while (true) { break done }");
    }

    @Test
    public void testContinueStatement() throws JsError{
        cloneTestScript("while (true) { continue }");
        cloneTestScript("done: while (true) { continue done }");
    }

    @Test
    public void testDebuggerStatement() throws JsError{
        cloneTestScript("debugger");
    }

    @Test
    public void testDoWhileStatement() throws JsError{
        cloneTestScript("do ; while (true)");
    }

    @Test
    public void testEmptyStatement() throws JsError{
        cloneTestScript(";");
    }

    @Test
    public void testExpressionStatement() throws JsError{
        cloneTestScript("x");
    }

    @Test
    public void testForInStatement() throws JsError{
        cloneTestScript("for(var a in b);");
        cloneTestScript("for(a in b);");
        cloneTestScript("for(a.b in c);");
        cloneTestScript("for(let of in of);");
        cloneTestScript("for(const a in b);");
    }

    @Test
    public void testForOfStatement() throws JsError{
        cloneTestScript("for(var a of b);");
        cloneTestScript("for(a of b);");
        cloneTestScript("for(let [a] of b);");
        cloneTestScript("for(let of of b);");
        cloneTestScript("for(const a of b);");
    }

    @Test
    public void testForStatement() throws JsError{
        cloneTestScript("for(x, y;;);");
        cloneTestScript("for(x = 0;;);");
        cloneTestScript("for(var x = 0;;);");
        cloneTestScript("for(let x = 0;;);");
        cloneTestScript("for(var x = 0, y = 1;;);");
        cloneTestScript("for(x; x < 0;);");
        cloneTestScript("for(x; x < 0; x++);");
        cloneTestScript("for(x; x < 0; x++) process(x);");
        cloneTestScript("for(a;b;c);");
        cloneTestScript("for(var a;b;c);");
        cloneTestScript("for(var a = 0;b;c);");
        cloneTestScript("for(var a = 0;;) { let a; }");
        cloneTestScript("for(;b;c);");
        cloneTestScript("for(let of;;);");
        cloneTestScript("for(let a;;); let a;");
    }

    @Test
    public void testIfStatement() throws JsError{
        cloneTestScript("if (a) b;");
        cloneTestScript("if (a) b; else c;");
        cloneTestScript("if (morning) goodMorning();");
        cloneTestScript("if (morning) (function(){})");
        cloneTestScript("if (morning) var x = 0;");
        cloneTestScript("if (morning) goodMorning(); else goodDay();");
    }

    @Test
    public void testLabeledStatement() throws JsError{
        cloneTestScript("start: for (;;) break start");
        cloneTestScript("__proto__: test");
        cloneTestScript("a:{break a;}");
    }

    @Test
    public void testReturnStatement() throws JsError{
        cloneTestScript("(function(){ return; })");
        cloneTestScript("(function(){ return x; })");
    }

    @Test
    public void testSwitchStatement() throws JsError{
        cloneTestScript("switch (x) {}");
        cloneTestScript("switch(a){case 1:}");
        cloneTestScript("switch (answer) { case 0: hi(); break; }");
    }

    @Test
    public void testSwitchStatementWithDefault() throws JsError{
        cloneTestScript("switch(a){case 1:default:case 2:}");
        cloneTestScript("switch(a){case 1:default:}");
        cloneTestScript("switch(a){default:case 2:}");
        cloneTestScript("switch (answer) { case 0: hi(); break; default: break }");
    }

    @Test
    public void testThrowStatement() throws JsError{
        cloneTestScript("throw x");
    }

    @Test
    public void testTryCatchStatement() throws JsError{
        cloneTestScript("try{}catch(a){}");
        cloneTestScript("try { a } catch (e) { }");
        cloneTestScript("try { } catch (e) { e }");
        cloneTestScript("try { a } catch (e) { e }");
    }

    @Test
    public void testFinallyStatement() throws JsError{
        cloneTestScript("try { } finally { }");
        cloneTestScript("try { } finally { _finally_ }");
        cloneTestScript("try{}catch(a){}finally{}");
        cloneTestScript("try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }");
    }

    @Test
    public void testVariableDeclarationStatement() throws JsError{
        cloneTestScript("var x");
        cloneTestScript("var x, y;");
        cloneTestScript("var x = 0");
        cloneTestScript("var x = 0, y = 1, z = 2");
        cloneTestScript("let x");
        cloneTestScript("{ let x }");
        cloneTestScript("{ let x = 0, y = 1, z = 2 }");
        cloneTestScript("{ const x = 0 }");
        cloneTestScript("{ const x = 0, y = 1, z = 2 }");
        cloneTestScript("let[let]=0");
    }

    @Test
    public void testWhileStatement() throws JsError{
        cloneTestScript("while(1);");
    }

    @Test
    public void testWithStatement() throws JsError{
        cloneTestScript("with(1);");
    }

    @Test
    public void testDirective() throws JsError{
        cloneTestScript("\"Hello\"");
        cloneTestScript("\"\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0\"");
        cloneTestScript("\"\\u0061\"");
        cloneTestScript("\"\\x61\"");
        cloneTestScript("\"Hello\\nworld\"");
        cloneTestScript("\"Hello\\\nworld\"");
        cloneTestScript("\"Hello\\02World\"");
        cloneTestScript("\"Hello\\012World\"");
        cloneTestScript("\"Hello\\122World\"");
        cloneTestScript("\"Hello\\0122World\"");
        cloneTestScript("\"Hello\\312World\"");
        cloneTestScript("\"Hello\\412World\"");
        cloneTestScript("\"Hello\\712World\"");
        cloneTestScript("\"Hello\\0World\"");
        cloneTestScript("\"Hello\\\r\nworld\"");
        cloneTestScript("\"Hello\\1World\"");
        cloneTestScript("(function () { 'use\\x20strict'; with (i); })");
        cloneTestScript("(function () { 'use\\nstrict'; with (i); })");
        cloneTestScript("function a() {'use strict';return 0;};");
        cloneTestScript("(function() {'use strict';return 0;});");
        cloneTestScript("(function a() {'use strict';return 0;});");
        cloneTestScript("\"use strict\" + 0");
        cloneTestModule("\"use strict\";");
    }


    @Test
    public void testTypeAlteringCloneReducerSubclass() throws JsError {
        Script script, clone;

        script =  Parser.parseScript("{}");
        clone = (Script) Director.reduceScript(new ReplaceBlockStatementsWithEmptyStatements(), script);
        assertEquals(CodeGen.codeGen(clone), ";");

        script =  Parser.parseScript("a.b");
        clone = (Script) Director.reduceScript(new ReplaceStaticMemberExpressionWithComputedMemberExpression(), script);
        assertEquals(CodeGen.codeGen(clone), "null[null]");
    }

    class ReplaceStaticMemberExpressionWithComputedMemberExpression extends ReconstructingReducer {
        @Override
        @Nonnull
        public ComputedMemberExpression reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull Node object) {
            return new ComputedMemberExpression(new LiteralNullExpression(), new LiteralNullExpression());
        }
    }

    // TODO: replace MemberExpression with some other Binding
    // TODO: replace MemberExpression with some non-MemberExpression Expression

    class ReplaceBlockStatementsWithEmptyStatements extends ReconstructingReducer {
        @Override
        @Nonnull
        public EmptyStatement reduceBlockStatement(@Nonnull BlockStatement blockStatement, @Nonnull Node block) {
            return new EmptyStatement();
        }
    }
}
