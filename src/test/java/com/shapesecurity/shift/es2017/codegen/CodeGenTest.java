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

package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.ForInStatement;
import com.shapesecurity.shift.es2017.ast.ForStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.IfStatement;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.Statement;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.ast.WithStatement;
import com.shapesecurity.shift.es2017.codegen.location.CodeGenWithLocation;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.reducer.MonoidalReducer;
import com.shapesecurity.shift.es2017.reducer.WrappedReducer;
import javax.annotation.Nonnull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodeGenTest {

    @Nonnull
    private static Script statement(@Nonnull Statement stmt) {
        return new Script(ImmutableList.empty(), ImmutableList.of(stmt));
    }

    private static void test(String source) throws JsError {
        ParserWithLocation parserWithLocation = new ParserWithLocation();
        CodeGenWithLocation codeGenWithLocation = new CodeGenWithLocation(new CodeGen(new CodeRepFactory()));
        Module module = parserWithLocation.parseModule(source);
        String code = codeGenWithLocation.codeGen(module);
        assertEquals(source, code);
        assertEquals(module, Parser.parseModule(code));

        /*
       Check location agreement. This isn't quite the right contract; we actually want to assert that,
       for any tree, if we codegen it then parse it, the two will agree on locations for analagous nodes.
       Here we take advantage of the fact that the input is required to be in the format that codeGen produces,
       which allows us to compare the locations for the codegen with the locations for the parser for the same
       tree.

       This approach is kind of hacky: we really just want a visitor. But it does work.
        */
        Director.reduceModule(new WrappedReducer<>((node, unit) -> {
            assertEquals("Location for " + node.getClass().getSimpleName() + " should be the same according to the parser and the code generator", parserWithLocation.getLocation(node), codeGenWithLocation.getLocation(node));
            return unit;
        }, new MonoidalReducer<>(Monoid.UNIT)), module);
    }

    private static void test(String expected, String source) throws JsError {
        Module module = Parser.parseModule(source);
        String code = CodeGen.codeGen(module);
        assertEquals(expected, code);
        assertEquals(module, Parser.parseModule(code));
    }

    private void testShift(@Nonnull String expected, @Nonnull Script script) {
        assertEquals(expected, CodeGen.codeGen(script));
    }

    private void testShift(@Nonnull String expected, @Nonnull Module module) {
        assertEquals(expected, CodeGen.codeGen(module));
    }

    private void testScript(String source) throws JsError {
        Script script = Parser.parseScript(source);
        String code = CodeGen.codeGen(script);
        assertEquals(source, code);
        assertEquals(script, Parser.parseScript(code));
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
    public void testArrayBinding() throws JsError {
        test("[]=0");
        test("[...a]=0");
        test("[a,...a]=0");
        test("[a,a=0,...a]=0");
        test("[,,]=0");
        test("[,...a]=0");
    }

    @Test
    public void testBindingPropertyIdentifier() throws JsError {
        test("({a=0}=0)");
    }

    @Test
    public void testBindingPropertyProperty() throws JsError {
        test("({a:b}=0)");
    }

    @Test
    public void testBindingWithDefault() throws JsError {
        test("[a=0]=0");
        test("({a:b=0}=0)");
    }

    @Test
    public void testClassDeclaration() throws JsError {
        test("class A{get[[]](){}[1.5608526501574786](){}}");
        test("class A{}");
        test("class A extends B{}");
    }

    @Test
    public void testClassExpression() throws JsError {
        test("(class{})");
        test("(class A{})");
        test("(class A extends B{})");
        test("(class extends B{})");
    }

    @Test
    public void testClassElement() throws JsError {
        test("(class{a(){}})");
        test("(class{*a(){}})");
        test("(class{static a(){}})");
        test("(class{static*a(){}})");
        test("(class{constructor(){}})");
    }

    @Test
    public void testAssignment() throws JsError {
        test("a=b");
    }

    @Test
    public void testCodeGenDirectives() throws JsError {
        test("\"use strict\"");
        test("\"use\u0020strict\"");
        test("\"use strict\";0");
        testShift("\"use\u0020strict\"", new Script(ImmutableList.of(new Directive("use strict")), ImmutableList.empty()));
        testShift("'\"'", new Script(ImmutableList.of(new Directive("\"")), ImmutableList.empty()));
        testShift("\"\\\"\"", new Script(ImmutableList.of(new Directive("\\\"")), ImmutableList.empty()));
    }

    @Test
    public void testCompoundAssignment() throws JsError {
        test("a+=b");
        test("a*=b");
        test("a%=b");
        test("a**=b");
        test("a<<=b");
        test("a>>=b");
        test("a>>>=b");
        test("a/=b");
        test("a|=b");
        test("a^=b");
        test("a,b=c");
        test("a=b,c");
        test("a=(b,c)");
        test("a,b^=c");
        test("a^=b,c");
        test("a^=(b,c)");
        test("a.b=0");
        test("a[b]=0");
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
    public void testBitwiseOr() throws JsError {
        test("a|b");
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
    public void testBitwiseAnd() throws JsError {
        test("a&b");
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
    public void testBitwiseXor() throws JsError {
        test("a^b");
        test("a^b&b");
        test("(a^b)&b");
    }

    @Test
    public void testExponential() throws JsError {
        test("a**b");
        test("a%b**c");
        test("(a^b)**c");
    }

    @Test
    public void testExport() throws JsError {
        test("export var a");
        test("export var a;0");
        test("export var a=0");
        test("export var a,b");
        test("export var a=0,b=0");
        test("export const a=0");
        test("export let a");
        test("export function f(){}");
        test("export function f(){}0");
        test("export class A{}");
        test("export class A{}0");
    }

    @Test
    public void testExportAllFrom() throws JsError {
        test("export*from\"m\"");
        test("export*from\"m\";0");
    }

    @Test
    public void testExportDefault() throws JsError {
        test("export default function(){}");
        test("export default function(){}0");
        test("export default 0");
        test("export default 0;0");
        test("export default function f(){}");
        test("export default function*f(){}");
        test("export default class A{}");
        test("export default(class{})");
        test("export default(function(){})");
        test("export default{}");
    }

    @Test
    public void testExportFrom() throws JsError {
        test("export{}from\"m\"");
        test("export{}from\"m\";0");
        test("let a;export{a}from\"m\"");
        test("let a,b;export{a,b}from\"m\"");
        test("export{}");
        test("let a;export{a}");
        test("let a,b;export{a,b}");
    }

    @Test
    public void testAdditive() throws JsError {
        test("a+b");
        test("a-b");
        test("a+(b+b)");
        test("a+(b<<b)");
        test("a+b<<b");
        test("(a<<b)+(c>>d)");
        test("a*b+c/d", "(a*b)+(c/d)");
    }

    @Test
    public void testExportSpecifier() throws JsError {
        test("let a;export{a}");
        test("let a,b;export{a as b}");
        test("let a,b;export{a,b}");
        test("let a,b,c;export{a,b as c}");
        test("let a,b,c;export{a as b,c}");
        test("let a,b,c,d;export{a as b,c as d}");
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
    public void testFloatingPoint() throws JsError {
        test("1.1.valueOf()");
        test("15..valueOf()");
        test("1..valueOf()");
        test("1e300.valueOf()");
        test("8e15.valueOf()", "8000000000000000..valueOf()");
        test("10..valueOf()", "1e1.valueOf()");
        test("1.3754889325393114", "1.3754889325393114");
        test("1.3754889325393114e24", "0x0123456789abcdefABCDEF");
        test("4.185580496821357e298", "4.1855804968213567e298");
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
    public void testForInStatement() throws JsError {
        test("for(var a in 1);");
        testScript("for((let)in 1);");
        test("for(a in 1);");
    }

    @Test
    public void testForOfStatement() throws JsError {
        test("for(a of b);");
        test("for([a]of[b]);");
        test("for(let[a]of[b]);");
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
    public void testFunctionBody() throws JsError {
        test("");
        test("\"use strict\"");
        test(";\"use strict\"");
        testShift("\"use strict\"", new Script(ImmutableList.of(new Directive("use strict")), ImmutableList.empty()));
        testShift("(\"use strict\")", statement(new ExpressionStatement(new LiteralStringExpression("use strict"))));
        testShift("(\"use strict\");;", new Script(
                ImmutableList.empty(),
                ImmutableList.of(
                        new ExpressionStatement(new LiteralStringExpression("use strict")),
                        new EmptyStatement()
                )
        ));
        testShift("\"use strict\";;", new Script(
                ImmutableList.of(new Directive("use strict")),
                ImmutableList.of(new EmptyStatement())
        ));
        testShift("\"use strict\";(\"use strict\")", new Script(
                ImmutableList.of(new Directive("use strict")),
                ImmutableList.of(new ExpressionStatement(new LiteralStringExpression("use strict")))
        ));
        testShift("\"use strict\";;\"use strict\"", new Script(
                ImmutableList.of(new Directive("use strict")),
                ImmutableList.of(new EmptyStatement(), new ExpressionStatement(new LiteralStringExpression("use strict")))
        ));
    }

    @Test
    public void testFunctionDeclaration() throws JsError {
        test("function f(){}");
        test("function*f(){}");
        test("function f(a){}");
        test("function f(a,b){}");
        test("function f(a,b,...rest){}");
    }

    @Test
    public void testFunctionExpression() throws JsError {
        test("(function(){})");
        test("(function f(){})");
        test("(function*(){})");
        test("(function*f(){})");
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

        IfStatement MISSING_ELSE = new IfStatement(IDENT, EMPTY, Maybe.empty());
        testShift("if(a){a:if(a);}else;", statement(new IfStatement(IDENT, new LabeledStatement("a",
                MISSING_ELSE), Maybe.of(EMPTY))));
        testShift("if(a){if(a);else if(a);}else;", statement(new IfStatement(IDENT, new IfStatement(IDENT, EMPTY,
                Maybe.of(MISSING_ELSE)), Maybe.of(EMPTY))));
        testShift("if(a){if(a);}else;", statement(new IfStatement(IDENT, MISSING_ELSE, Maybe.of(EMPTY))));
        testShift("if(a){while(a)if(a);}else;", statement(new IfStatement(IDENT, new WhileStatement(IDENT, MISSING_ELSE),
                Maybe.of(EMPTY))));
        testShift("if(a){with(a)if(a);}else;", statement(new IfStatement(IDENT, new WithStatement(IDENT, MISSING_ELSE),
                Maybe.of(EMPTY))));
        testShift("if(a){for(;;)if(a);}else;", statement(new IfStatement(IDENT, new ForStatement(Maybe.empty(),
                Maybe.empty(), Maybe.empty(), MISSING_ELSE), Maybe.of(EMPTY))));
        testShift("if(a){for(a in a)if(a);}else;",
                statement(
                        new IfStatement(
                                IDENT,
                                new ForInStatement(new AssignmentTargetIdentifier("a"), IDENT, MISSING_ELSE),
                                Maybe.of(EMPTY))));
    }

    @Test
    public void testArrowExpression() throws JsError {
        test("a=>a");
        test("()=>a");
        test("a=>a", "(a)=>a");
        test("(...a)=>a");
        test("(a,...b)=>a");
        test("(a=0)=>a");
        test("(a,b)=>a");
        test("({a})=>a");
        test("({a=0})=>a");
        test("([a])=>a");
        test("a=>({})");
        test("a=>{}");
        test("a=>{({})}");
        test("a=>{0;return}");
        test("()=>function(){}");
        test("()=>class{}");
        test("()=>(1,2)");
        test("(()=>0)()");
    }

    @Test
    public void testImport() throws JsError {
        test("import\"m\"");
        test("import\"m\";0");
        test("import a from\"m\"");
        test("import{a}from\"m\"");
        test("import{a,b}from\"m\"");
        test("import a,{b}from\"m\"");
        test("import a,{b,c}from\"m\"");
        test("import a,{b,c}from\"m\";0");
        test("import\"m\"", "import {} from \"m\"");
        test("import a from\"m\"", "import a,{}from \"m\"");
    }

    @Test
    public void testImportNamespace() throws JsError {
        test("import*as a from\"m\"");
        test("import*as a from\"m\";0");
        test("import a,*as b from\"m\"");
    }

    @Test
    public void testImportSpecifier() throws JsError {
        test("import{a}from\"m\"");
        test("import{a}from\"m\";0");
        test("import{a as b}from\"m\"");
        test("import{a,b}from\"m\"");
        test("import{a,b as c}from\"m\"");
        test("import{a as b,c}from\"m\"");
        test("import{a as b,c as d}from\"m\"");
    }

    @Test
    public void testLabeledStatement() throws JsError {
        test("a:;");
        test("a:b:;");
    }

    @Test
    public void testLiteralBooleanExpression() throws JsError {
        test("true");
        test("false");
    }

    @Test
    public void testLiteralInfinityExpression() throws JsError {
        test("2e308", "2e308");
        test("1+2e308", "1+2e308");
        test("2e308", "3e308");
    }

    @Test
    public void testLiteralNullExpression() throws JsError {
        test("null");
        test("null", "nul\\u006c");
    }

    @Test
    public void testLiteralNumericExpression() throws JsError {
        test("0");
        test("0", "0x0");
        test("0", "0o0");
        test("0", "0b0");
        test("1");
        test("2");
        test("0x38D7EA4C68001", "1000000000000001");
        test("15e5", "1500000");
        test("155e3", "155000");
        test(".1");
        test(".1", "0.1");
        // floats
        test("1.1.valueOf()");
        test("15..valueOf()");
        test("1..valueOf()");
        test("1e300.valueOf()", "1e+300.valueOf()");
        test("8e15.valueOf()", "8000000000000000..valueOf()");
        test("1e20", "100000000000000000001");
        test("10..valueOf()", "1e1.valueOf()");
        test("100", "1e2");
        test("1.3754889325393114", "1.3754889325393114");
        test("1.3754889325393114e24", "0x0123456789abcdefABCDEF");
        test("4.185580496821357e298", "4.1855804968213567e+298");
        test("5.562684646268003e-308", "5.5626846462680035e-308");
        test("5.562684646268003e-309", "5.5626846462680035e-309");
        test("2147483648", "2147483648.0");
        test("1e-7");
        test("1e-8");
        test("1e-9");
        test("1e308", "1e+308");
        test("1e308");
        test("1e-308");
    }

    @Test
    public void testLiteralRegExpExpression() throws JsError {
        test("/a/");
        test("/a/i");
        test("/a/gi");
        test("/a\\s/gi");
        test("/a\\r/gi");
        test("/a\\r/ instanceof 3");
        test("/a\\r/g instanceof 3");
    }

    @Test
    public void testLiteralStringExpression() throws JsError {
        test("(\"\")");
        testShift("(\"\0" + "5\")", statement(new ExpressionStatement(new LiteralStringExpression("\u00005"))));
        test("(\"\")", "('')");
        test("(\"a\")", "('a')");
        test("('\"')", "(\"\\\"\")");
        test("(\"a\")", "('a')");
        test("(\"'\")", "('\\'')");
        test("(\"a\")");
        test("('\"')");
        test("(\"\0\\b\\n\\r\\t\\v\\f\\\\\\\"'\\u2028\\u2029日本\")", "(\"\\0\\b\\n\\r\\t\\v\\f\\\\\\\"'\\u2028\\u2029日本\")");
        testShift("(\"\0" + "5\")", statement(new ExpressionStatement(new LiteralStringExpression("\u00005"))));
    }

    @Test
    public void testLogicalAnd() throws JsError {
        test("a&&b");
    }

    @Test
    public void testLogicalOr() throws JsError {
        test("a||b");
    }

    @Test
    public void testModule() throws JsError {
        test("");
        test("a;a");
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
        test("!a*b");
        test("a*(b+c)");
    }

    @Test
    public void testNewCallMember() throws JsError {
        test("new a");
        test("new a(a)");
        test("new a(a,b)");
        test("new this.a");
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
        test("(new a``).a");
    }

    @Test
    public void testNewTargetExpression() throws JsError {
        test("function f(){new.target}");
        test("function f(){new.target}", "function f() { new . target }");
    }

    @Test
    public void testObjectExpression() throws JsError {
        test("({})");
        test("({\"-1\":0})");
        test("({a:1})", "({a:1,})");
        test("({}.a--)");
        test("({1:1})", "({1.0:1})");
        test("({a:b})", "({a:b})");
        test("({get a(){;}})");
        test("({set a(param){;}})");
        test("({get a(){;},set a(param){;},b:1})");
        test("({a:(a,b)})");
        test("({Infinity:0})", "({2e308:0})");

        // from js
        test("({})");
        test("({a:1})", "({a:1,})");
        test("({}.a--)");
        test("({1:1})", "({1.0:1})");
        test("({a:b})");
        test("({255:0})", "({0xFF:0})");
        test("({63:0})", "({0o77:0})");
        test("({3:0})", "({0b11:0})");
        test("({0:0})", "({0.:0})");
        test("({0:0})", "({.0:0})");
        test("({.1:0})", "({0.1:0})");
        test("({1e17:0})", "({0.1e+18:0})");
        test("({[a]:b})");
        test("({a:b})", "({\"a\":b})");
        test("({\" \":b})");
        test("({get a(){;}})");
        test("({set a(param){;}})");
        test("({get a(){;},set a(param){;},b:1})");
        test("({a:(a,b)})");
        test("({a})");
    }

    @Test
    public void testPostfix() throws JsError {
        test("a++");
        test("a--");
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
    public void testPrimary() throws JsError {
        test("0");
        test("1");
        test("2");
        test("(\"a\")", "('a')");
        test("(\"'\")", "('\\'')");
        test(";\"a\"");
        test(";'\"'");
        test("/a/");
        test("/a/i");
        test("/a/gi");
        test("/a\\s/gi");
        test("/a\\r/gi");
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
    public void testReturnStatement() throws JsError {
        test("function a(){return}");
        test("function a(){return 0}");
        test("function a(){return function a(){return 0}}");
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
    public void testSequence() throws JsError {
        test("a,b,c,d");
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
    public void testSpreadElement() throws JsError {
        test("[...a]");
        test("[...a,...b]");
        test("[...a,b,...c]");
        test("[...a=b]");
        test("[...(a,b)]");
        test("f(...a)");
    }

    @Test
    public void testSuper() throws JsError {
        test("class A extends B{constructor(){super()}}");
        test("({m(){super.m()}})");
    }

    @Test
    public void testSwitchStatement() throws JsError {
        test("switch(0){}");
        test("switch(0){default:}");
        test("switch(0){case 0:default:}");
        test("switch(0){case 0:a;default:c:b}");
    }

    @Test
    public void testTemplateExpression() throws JsError {
        test("``");
        test("````");
        test("a``");
        test("a.b``");
        test("a[b]``");
        test("a()[b]``");
        test("a()``");
        test("(a+b)``");
        test("(function(){}``)", "(function(){})``");
        test("(class{}``)", "(class{})``");
        test("({}``)", "({})``");
        test("`a`");
        test("a`a`");
        test("`a${b}c`");
        test("`${a}`");
        test("`${a}${b}`");
        test("` ${a} ${b} `");
        test("` ${a} ${b} `", "` ${ a } ${ b } `");
        test("`a\\${b}c`");
        test("``.a");
        test("``()");
        test("new``");
        test("new``", "new ``()");
        test("new``(a)", "new ``(a)");
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
        testShift("with(null);", new Script(ImmutableList.empty(), ImmutableList.of(new WithStatement(new LiteralNullExpression(), new EmptyStatement()))));
    }

    @Test
    public void testYieldExpression() throws JsError {
        test("function*f(){yield}");
        test("function*f(){yield a}");
        test("function*f(){yield 0}");
        test("function*f(){yield{}}");
        test("function*f(){yield a+b}");
        test("function*f(){yield a=b}");
        test("function*f(){yield(a,b)}");
        test("function*f(){f(yield,yield)}");
        test("function*f(){f(yield a,yield b)}");
        test("function*f(){yield yield yield}");
    }

    @Test
    public void testYieldGeneratorExpression() throws JsError {
        test("function*f(){yield*a}");
        test("function*f(){yield*0}");
        test("function*f(){yield*{}}");
        test("function*f(){yield*a+b}");
        test("function*f(){yield*a=b}");
        test("function*f(){yield*(a,b)}");
        test("function*f(){f(yield*a,yield*b)}");
        test("function*f(){yield*yield*(yield)*(yield)}");
    }
}
