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

package com.shapesecurity.shift.validator;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitTest {

  private void assertCorrectFailures(Script script, int expectedNumErrors, String expectedErrorMsg) {
    ImmutableList<ValidationError> errors = Validator.validate(script);
    assertEquals(expectedNumErrors, errors.length);
    for (ValidationError error : errors) {
      assertEquals(error.message, expectedErrorMsg);
    }
  }

  private void assertCorrectFailures(Module module, int expectedNumErrors, String expectedErrorMsg) {
    ImmutableList<ValidationError> errors = Validator.validate(module);
    assertEquals(expectedNumErrors, errors.length);
    for (ValidationError error : errors) {
      assertEquals(error.message, expectedErrorMsg);
    }
  }

  @Test
  public void testBindingIdentifier() throws JsError {
    Script test0 = Parser.parseScript("x=1");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new BindingIdentifier("1"), new LiteralNumericExpression(0.0)))));
    assertCorrectFailures(test1, 1, "the name field of binding identifier must be a valid identifier name");

    // TODO test for *default*
  }

  @Test
  public void testBreakStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { break; }");
    assertCorrectFailures(test0, 0, "");

    Script test1 = Parser.parseScript("done: while (true) { break done; }");
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("1done")))))))));
    assertCorrectFailures(test2, 1, "the label field of break statement exists and must be a valid identifier name");
  }

  @Test
  public void testCatchClause() throws JsError {
    Script test0 = Parser.parseScript("try{} catch(e){} finally{}");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new TryFinallyStatement(new Block(ImmutableList.nil()), Maybe.just(new CatchClause(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), new Block(ImmutableList.nil()))), new Block(ImmutableList.nil()))));
    assertCorrectFailures(test1, 1, "the binding field of CatchClause must not be a member expression");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new TryFinallyStatement(new Block(ImmutableList.nil()), Maybe.just(new CatchClause(new StaticMemberExpression("a", new IdentifierExpression("b")), new Block(ImmutableList.nil()))), new Block(ImmutableList.nil()))));
    assertCorrectFailures(test2, 1, "the binding field of CatchClause must not be a member expression");
  }

  @Test
  public void testContinueStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { continue; }");
    assertCorrectFailures(test0, 0, "");

    Script test1 = Parser.parseScript("done: while (true) { continue done; }");
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.just("1done")))))))));
    assertCorrectFailures(test2, 1, "the label field of continue statement exists and must be a valid identifier name");
  }

  @Test
  public void testDirective() {
    Script test0 = new Script(ImmutableList.list(new Directive("use strict;"), new Directive("linda;"), new Directive(".-#($*&#")), ImmutableList.nil());
    assertCorrectFailures(test0, 0, "");

    // TODO java will not allow not string literals, so can't test those...
  }

  @Test
  public void testExportDefault() {
    // TODO test interaction with binding identifiers called default
  }

  @Test
  public void testExportSpecifier() throws JsError {
    Module test0 = Parser.parseModule("export * from 'a';");
    assertCorrectFailures(test0, 0, "");

    // TODO doesn't fail ever
  }

  @Test
  public void testForInStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x in [1,2,3]){}");
    assertCorrectFailures(test0, 0, "");

    // TODO fail with more than 1 variable declarator

    // TODO fail with initializer in variable declarator
  }

  @Test
  public void testForOfStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x of [1,2,3]){}");
    assertCorrectFailures(test0, 0, "");

    // TODO fail with more than 1 variable declarator

    // TODO fail with initializer in variable declarator
  }

  @Test
  public void testFormalParameters() {

  }

  @Test
  public void testFunctionBody() {

  }

  @Test
  public void testFunctionDeclaration() {

  }

  @Test
  public void testFunctionExpression() {

  }

  @Test
  public void testIdentifierExpression() {

  }

  @Test
  public void testIfStatement() {

  }

  @Test
  public void testImportSpcifier() {

  }

  @Test
  public void testLabeledStatement() {

  }

  @Test
  public void testLiteralNumericExpression() {

  }

  @Test
  public void testLiteralRegExpExpression() {

  }

  @Test
  public void testModule() {

  }

  @Test
  public void testReturnStatement() {

  }

  @Test
  public void testScript() {

  }

  @Test
  public void testSetter() {

  }

  @Test
  public void testShorthandProperty() {

  }

  @Test
  public void testStaticMemberExpression() {

  }

  @Test
  public void testTemplateElement() {

  }

  @Test
  public void testTemplateExpression() {

  }

  @Test
  public void testVariableDeclaration() {

  }

  @Test
  public void testVariableDeclarationStatement() {

  }

  @Test
  public void testVariableDeclarator() {

  }

  @Test
  public void testYieldExpression() {

  }

  @Test
  public void testYieldGeneratorExpression() {

  }
}
