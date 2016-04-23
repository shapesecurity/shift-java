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
import static org.junit.Assert.assertTrue;

public class ValidatorTest {

  private void assertCorrectErrors(Node node, int expectedNumErrors, String expectedErrorMsg) {
    ImmutableList<ValidationError> errors;
    if (node instanceof Script) {
      errors = Validator.validate((Script)node);
    } else {
      errors = Validator.validate((Module)node);
    }
    assertEquals(expectedNumErrors, errors.length);
    for (ValidationError error : errors) {
      assertEquals(error.message, expectedErrorMsg);
    }
  }

  private void assertNoErrors(Node node) {
    ImmutableList<ValidationError> errors;
    if (node instanceof Script) {
      errors = Validator.validate((Script)node);
    } else {
      errors = Validator.validate((Module)node);
    }
    assertTrue(errors.length == 0);
  }

  @Test
  public void testAssignmentTargetIdentifier() throws JsError {
    Script test0 = Parser.parseScript("x=1");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("1"), new LiteralNumericExpression(0.0)))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_ASSIGNMENT_TARGET_IDENTIFIER_NAME);
  }

  @Test
  public void testBindingIdentifier() throws JsError {
    Script test0 = Parser.parseScript("var x");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("1"), Maybe.nothing()))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_BINDING_IDENTIFIER_NAME);
  }

  @Test
  public void testBreakStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { break; }");
    assertNoErrors(test0);

    Script test1 = Parser.parseScript("done: while (true) { break done; }");
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("1done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_BREAK_STATEMENT_LABEL);
  }


  @Test
  public void testContinueStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { continue; }");
    assertNoErrors(test0);

    Script test1 = Parser.parseScript("done: while (true) { continue done; }");
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.just("1done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_CONTINUE_STATEMENT_LABEL);
  }

  @Test
  public void testDirective() {
    Script test0 = new Script(ImmutableList.list(new Directive("use strict;"), new Directive("linda;"), new Directive(".-#($*&#")), ImmutableList.nil());
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.list(new Directive("'use strict;")), ImmutableList.nil());
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test2 = new Script(ImmutableList.list(new Directive("'use strict;"), new Directive("linda';"), new Directive("'.-#($*&#")), ImmutableList.nil());
    assertCorrectErrors(test2, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test3 = new Script(ImmutableList.list(new Directive("'use stri\"ct;"), new Directive("li\"nda;'"), new Directive(".-\"#'($*&#")), ImmutableList.nil());
    assertCorrectErrors(test3, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test4 = new Script(ImmutableList.list(new Directive("'use s\ntrict;"), new Directive("lind\na;'"), new Directive(".-#'($\n*&#")), ImmutableList.nil());
    assertCorrectErrors(test4, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test5 = new Script(ImmutableList.list(new Directive("'use s\rtrict;"), new Directive("lind\ra;'"), new Directive(".-#'($\r*&#")), ImmutableList.nil());
    assertCorrectErrors(test5, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test6 = new Script(ImmutableList.list(new Directive("('\\x0');"), new Directive("('\u2028')"), new Directive("(\\u{110000}\\\")")), ImmutableList.nil());
    assertCorrectErrors(test6, 3, ValidationErrorMessages.VALID_DIRECTIVE);
  }

  @Test
  public void testExportSpecifier() throws JsError {
    Module test0 = Parser.parseModule("export * from 'a';");
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportFromSpecifier("2a_", Maybe.just("b"))), "a")));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_EXPORT_SPECIFIER_NAME);

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportFromSpecifier("b", Maybe.just("%dlk45"))), "a")));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_EXPORTED_NAME);

    Module test3 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportFromSpecifier("a", Maybe.nothing())), "a")));
    assertNoErrors(test3);
  }

  @Test
  public void testForInStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x in [1,2,3]){}");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);
  }

  @Test
  public void testForOfStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x of [1,2,3]){}");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);
  }

  @Test
  public void testIdentifierExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("linda"))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("9458723"))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("A*9458723"))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("0lkdjaf"))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);
  }

  @Test
  public void testIfStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.just(new ExpressionStatement(new ThisExpression())))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new IdentifierExpression("a"), new ExpressionStatement(new IdentifierExpression("b")), Maybe.nothing())));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new IdentifierExpression("a"), new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.nothing()), Maybe.just(new ExpressionStatement(new ThisExpression())))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IF_STATEMENT);
  }

  @Test
  public void testImportSpecifier() {
    Module test0 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.nothing(), new BindingIdentifier("b"))), "c")));
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.just("a"), new BindingIdentifier("b"))), "c")));
    assertNoErrors(test1);

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.just("2d849"), new BindingIdentifier("b"))), "c")));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IMPORT_SPECIFIER_NAME);
  }

  @Test
  public void testLabeledStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("done")))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("5346done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("done")))))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_LABEL);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("#das*($839da", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.just("done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_LABEL);

  }

  @Test
  public void testLiteralNumericExpression() throws JsError {
    Script test0 = Parser.parseScript("1.0");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(-1.0))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NEGATIVE);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(1.0/0))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_INFINITE);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(Double.NaN))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NAN);

  }

  @Test
  public void testLiteralRegExpExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("@", false, false, false, false, false))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_REG_EX_PATTERN);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("[a-z]foo\\\\/bar=([^=\\\\s])+", false, false, false, false, false))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("/[a-z]/", true, false, false, false, false))));
    assertNoErrors(test4);
  }

  @Test
  public void testStaticMemberExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new StaticMemberExpression(new IdentifierExpression("a"), "b"))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new StaticMemberExpression(new IdentifierExpression("a"), "45829"))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME);
  }

  @Test
  public void testTemplateElement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("abc"))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("\"abc'"))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("'abc\""))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);
  }

  @Test
  public void testTemplateExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"), new TemplateElement("c"), new TemplateElement("c"))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new IdentifierExpression("b"), new TemplateElement("c"), new IdentifierExpression("b"))))));
    assertCorrectErrors(test3, 3, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);
  }

  @Test
  public void testVariableDeclaration() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list()))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list()))));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list()))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.nil()))));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test7 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.nil()))));
    assertCorrectErrors(test7, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test8 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.nil()))));
    assertCorrectErrors(test8, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);
  }

  @Test
  public void testVariableDeclarationStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.CONST_VARIABLE_DECLARATION_MUST_HAVE_INIT);
  }

  @Test
  public void testBindingIdentifierCalledDefaultAndExportDefaultInteraction() {
    Module test0 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new FunctionDeclaration(false, new BindingIdentifier("*default*"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new FunctionExpression(false, Maybe.just(new BindingIdentifier("*default*")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new ClassDeclaration(new BindingIdentifier("*default*"), Maybe.nothing(), ImmutableList.nil()))));
    assertNoErrors(test2);

    Module test3 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new ClassExpression(Maybe.just(new BindingIdentifier("*default*")), Maybe.nothing(), ImmutableList.nil()))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);
  }

  @Test
  public void testReturnStatementAndFunctionBodyInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new IdentifierExpression("a")))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing()))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new ThisExpression())))))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(false, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(false, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new ThisExpression()))))))));
    assertNoErrors(test5);
  }

  @Test
  public void testYieldExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new IdentifierExpression("a"))))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(false, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))))));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(false, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test7 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))))));
    assertCorrectErrors(test7, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test8 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))))));
    assertNoErrors(test8);

    Script test9 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(true, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))));
    assertNoErrors(test9);

    Script test10 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))))));
    assertNoErrors(test10);

    Script test11 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))))));
    assertNoErrors(test11);

    Script test12 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(true, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))));
    assertNoErrors(test12);

    Script test13 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))))));
    assertNoErrors(test13);
  }

  @Test
  public void testYieldGeneratorExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(false, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(true, Maybe.just(new BindingIdentifier("a")), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertNoErrors(test5);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))))));
    assertNoErrors(test6);
  }
}
