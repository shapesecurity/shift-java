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
//package com.shapesecurity.shift.minifier;
//
//import com.shapesecurity.shift.TestBase;
//import Script;
//import CodeGen;
//import com.shapesecurity.shift.minifier.passes.expansion.ExpandBooleanLiterals;
//import com.shapesecurity.shift.minifier.passes.expansion.ReplaceStaticMemberAccessWithDynamicMemberAccess;
//import com.shapesecurity.shift.minifier.passes.expansion.TopLevelExpressionWithProhibitedFirstToken;
//import com.shapesecurity.shift.minifier.passes.reduction.FlattenBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.ReduceNestedIfStatements;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyStatements;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyTrailingDefault;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveSingleStatementBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.ReplaceWhileWithFor;
//import JsError;
//import Parser;
//import GlobalScope;
//import ScopeAnalyzer;
//
//import org.junit.Test;
//
//import java.io.IOException;
//
//public class MinifierTest extends TestBase {
//  public static final ReductionRule[] NO_REDUCTION_RULES = new ReductionRule[0];
//  public static final ExpansionRule[] NO_EXPANSION_RULES = new ExpansionRule[0];
//  public static final double NANOS_TO_MILLIS = 1e-6;
//
//  private static void testEffectiveness(String fileName) throws IOException, JsError {
//    int esmangled = readLibrary(fileName + ".min.js").length();
//    int uglified = readLibrary(fileName + ".min.ugly.js").length();
//    long start = System.nanoTime();
//    String source = readLibrary(fileName + ".js");
//    Script script = Parser.parse(source);
//    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
//    String minified = CodeGen.codeGen(script);
//    //String minified = CodeGen.codeGen(Parser.parse(source));
//    double elapsed = (System.nanoTime() - start) * NANOS_TO_MILLIS;
//    int length = source.length();
//    int minLength = minified.length();
//    System.out.printf("%s (%d)\n", fileName, length);
//    System.out.printf("Minification time (shape-js): %.3fms\n", elapsed);
//    System.out.printf("%.2f%% as good as esmangle (%d/%d)\n",
//        (1 - ((double) minLength - esmangled) / (length - esmangled)) * 100, minLength, esmangled);
//    System.out.printf("%.2f%% as good as Uglify-JS (%d/%d)\n",
//        (1 - ((double) minLength - uglified) / (length - uglified)) * 100, minLength, uglified);
//    System.out.printf("%.2f%% larger than esmangle (%d/%d)\n", ((double) minLength / esmangled - 1) * 100, minLength,
//        esmangled);
//    System.out.printf("%.2f%% larger than Uglify-JS (%d/%d)\n", ((double) minLength / esmangled - 1) * 100, minLength,
//        uglified);
//    System.out.println("------------------------");
//  }
//
//  private void testMinifyAllRules(String expected, String source) throws JsError {
//    Script minified = Minifier.minify(Parser.parse(source));
//    assertEquals(expected, CodeGen.codeGen(minified));
//    // TODO: minify again and assert it is the same as the first pass
//  }
//
//  private void testMinify(MinificationRule rule, String expected, String source) throws JsError {
//    Script p = Parser.parse(source);
//    Script minified = rule instanceof ExpansionRule ? Minifier.minify(p, NO_REDUCTION_RULES,
//        new ExpansionRule[]{(ExpansionRule) rule}) : Minifier.minify(p, new ReductionRule[]{(ReductionRule) rule},
//        NO_EXPANSION_RULES);
//    assertEquals(expected, CodeGen.codeGen(minified));
//    // TODO: minify again and assert it is the same as the first pass
//  }
//
//  @Test
//  public void testSimple() throws JsError {
//
//    // ExpandBooleanLiterals
//    testMinify(ExpandBooleanLiterals.INSTANCE, "f(!0)", "f(true);");
//    testMinify(ExpandBooleanLiterals.INSTANCE, "f(!1)", "f(false);");
//
//    // ReplaceStaticMemberAccessWithDynamicMemberAccess
//    testMinify(ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, "a[void 0]", "a.undefined");
//    testMinify(ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, "a[1/0]", "a.Infinity");
//    testMinify(ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, "a[!0]", "a.true");
//    testMinify(ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, "a[!1]", "a.false");
//    testMinify(ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, "a.b", "a.b");
//
//    // TopLevelExpressionWithProhibitedFirstToken
//    testMinify(TopLevelExpressionWithProhibitedFirstToken.INSTANCE, "!function(){}", "(function(){})");
//    testMinify(TopLevelExpressionWithProhibitedFirstToken.INSTANCE, "!function(){}()", "(function(){}())");
//    testMinify(TopLevelExpressionWithProhibitedFirstToken.INSTANCE, "!{a:0}.a++", "({a:0}.a++)");
//    testMinify(TopLevelExpressionWithProhibitedFirstToken.INSTANCE, "!{a:function(){}}.a()", "({a:function(){}}.a())");
//
//    // FlattenBlocks
//    testMinify(FlattenBlocks.INSTANCE, "a;b;c;d;e;f;g", "a; { b; {} {{{}}{}} c; { d; { { { e; } } {} } } f } g;");
//    testMinify(FlattenBlocks.INSTANCE, "switch(a){case b:c;d}", "switch(a) { case b: {c; d} }");
//    testMinify(FlattenBlocks.INSTANCE, "switch(a){default:c;d}", "switch(a) { default: {c; d} }");
//
//    // ReduceNestedIfStatements
//    testMinify(ReduceNestedIfStatements.INSTANCE, "if(a&&b)f()", "if(a) if(b) f();");
//
//    // RemoveEmptyBlocks
//    testMinify(RemoveEmptyBlocks.INSTANCE, "do;while(a)", "do {} while(a);");
//    testMinify(RemoveEmptyBlocks.INSTANCE, "try{}catch(e){}", "try {} catch(e) {}");
//
//    // RemoveEmptyStatements
//    testMinify(RemoveEmptyStatements.INSTANCE, "", ";");
//    testMinify(RemoveEmptyStatements.INSTANCE, "{}", "{;}");
//    testMinify(RemoveEmptyStatements.INSTANCE, "a;b", ";;a;;;b;");
//    testMinify(RemoveEmptyStatements.INSTANCE, "a", "with(a);");
//    testMinify(RemoveEmptyStatements.INSTANCE, "with(a)b", "with(a)b");
//    testMinify(RemoveEmptyStatements.INSTANCE, "while(a);", "do; while(a);");
//    testMinify(RemoveEmptyStatements.INSTANCE, "a", "if(a);");
//    testMinify(RemoveEmptyStatements.INSTANCE, "switch(a){case b:default:case c:}",
//        "switch(a){case b:;default:;case c:;}");
//    testMinify(RemoveEmptyStatements.INSTANCE, "if(a)b()", "if(a)b();else;");
//    testMinify(RemoveEmptyStatements.INSTANCE, "if(!a)b()", "if(a);else b();");
//
//    // RemoveEmptyTrailingDefault
//    testMinify(RemoveEmptyTrailingDefault.INSTANCE, "switch(a){case 0:a()}", "switch(a){case 0:a();default:}");
//    testMinify(RemoveEmptyTrailingDefault.INSTANCE, "switch(a){case 0:a()}", "switch(a){case 0:a()}");
//    testMinify(RemoveEmptyTrailingDefault.INSTANCE, "switch(a){case 0:a();default:b();case 1:c()}",
//        "switch(a){case 0:a();default:b();case 1:c()}");
//
//    // RemoveSingleStatementBlocks
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "{}", "{}");
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "a", "{a}");
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "{a;b}", "{a; b}");
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "do f();while(a)", "do { f(); } while(a);");
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "if(a)b", "if(a){b;}");
//    testMinify(RemoveSingleStatementBlocks.INSTANCE, "for(;;)a", "for(;;){a}");
//
//    // ReplaceWhileWithFor
//    testMinify(ReplaceWhileWithFor.INSTANCE, "for(;a;)f()", "while(a) f();");
//
//    // TODO: these need to be split up into the specific rules they target
//    testMinifyAllRules("if(a)b", "if(a) { if(b) ; }");
//    testMinifyAllRules("for(var j=0;j<e.length;++j){var c=e[j];if(c[1])g[c[0]]=[j,c[1]];else g[c[0]]=[j]}",
//        "for (var j = 0; j < e.length; ++j) {var c = e[j];if (c[1]) {g[c[0]] = [j, c[1]];} else {g[c[0]] = [j]}}");
//  }
//
//  @Test
//  public void testMinificationEffectiveness() throws IOException, JsError {
//    testEffectiveness("angular-1.2.5");
//  }
//}
