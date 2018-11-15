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

package com.shapesecurity.shift.es2017.validator;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.ClassExpression;
import com.shapesecurity.shift.es2017.ast.ContinueStatement;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExportDefault;
import com.shapesecurity.shift.es2017.ast.ExportFrom;
import com.shapesecurity.shift.es2017.ast.ExportFromSpecifier;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.ForInStatement;
import com.shapesecurity.shift.es2017.ast.ForOfStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.IfStatement;
import com.shapesecurity.shift.es2017.ast.Import;
import com.shapesecurity.shift.es2017.ast.ImportSpecifier;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.ReturnStatement;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.TemplateElement;
import com.shapesecurity.shift.es2017.ast.TemplateExpression;
import com.shapesecurity.shift.es2017.ast.ThisExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.ast.YieldExpression;
import com.shapesecurity.shift.es2017.ast.YieldGeneratorExpression;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
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

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new AssignmentTargetIdentifier("1"), new LiteralNumericExpression(0.0)))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_ASSIGNMENT_TARGET_IDENTIFIER_NAME);
  }

  @Test
  public void testBindingIdentifier() throws JsError {
    Script test0 = Parser.parseScript("var x");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(
		VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("1"), Maybe.empty()))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_BINDING_IDENTIFIER_NAME);
  }

  @Test
  public void testBreakStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { break; }");
    assertNoErrors(test0);

    Script test1 = Parser.parseScript("done: while (true) { break done; }");
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(Maybe.of("1done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_BREAK_STATEMENT_LABEL);
  }


  @Test
  public void testContinueStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { continue; }");
    assertNoErrors(test0);

    Script test1 = Parser.parseScript("done: while (true) { continue done; }");
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.of("1done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_CONTINUE_STATEMENT_LABEL);
  }

  @Test
  public void testDirective() {
    Script test0 = new Script(ImmutableList.of(new Directive("use strict;"), new Directive("linda;"), new Directive(".-#($*&#")), ImmutableList.empty());
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.of(new Directive("'use strict;")), ImmutableList.empty());
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test2 = new Script(ImmutableList.of(new Directive("'use strict;"), new Directive("linda';"), new Directive("'.-#($*&#")), ImmutableList.empty());
    assertCorrectErrors(test2, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test3 = new Script(ImmutableList.of(new Directive("'use stri\"ct;"), new Directive("li\"nda;'"), new Directive(".-\"#'($*&#")), ImmutableList.empty());
    assertCorrectErrors(test3, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test4 = new Script(ImmutableList.of(new Directive("'use s\ntrict;"), new Directive("lind\na;'"), new Directive(".-#'($\n*&#")), ImmutableList.empty());
    assertCorrectErrors(test4, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test5 = new Script(ImmutableList.of(new Directive("'use s\rtrict;"), new Directive("lind\ra;'"), new Directive(".-#'($\r*&#")), ImmutableList.empty());
    assertCorrectErrors(test5, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test6 = new Script(ImmutableList.of(new Directive("('\\x0');"), new Directive("('\u2028')"), new Directive("(\\u{110000}\\\")")), ImmutableList.empty());
    assertCorrectErrors(test6, 3, ValidationErrorMessages.VALID_DIRECTIVE);
  }

  @Test
  public void testExportSpecifier() throws JsError {
    Module test0 = Parser.parseModule("export * from 'a';");
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportFrom(ImmutableList.of(new ExportFromSpecifier("2a_", Maybe.of("b"))), "a")));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_EXPORT_SPECIFIER_NAME);

    Module test2 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportFrom(ImmutableList.of(new ExportFromSpecifier("b", Maybe.of("%dlk45"))), "a")));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_EXPORTED_NAME);

    Module test3 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.empty())), "a")));
    assertNoErrors(test3);
  }

  @Test
  public void testForInStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x in [1,2,3]){}");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test6 = new Script(ImmutableList.empty(), ImmutableList.of(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);
  }

  @Test
  public void testForOfStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x of [1,2,3]){}");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty()))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test6 = new Script(ImmutableList.empty(), ImmutableList.of(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(0.0)), Maybe.of(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);
  }

  @Test
  public void testIdentifierExpression() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("linda"))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("9458723"))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("A*9458723"))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("0lkdjaf"))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);
  }

  @Test
  public void testIfStatement() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.of(new ExpressionStatement(new ThisExpression())))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new IfStatement(new IdentifierExpression("a"), new ExpressionStatement(new IdentifierExpression("b")), Maybe.empty())));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new IfStatement(new IdentifierExpression("a"), new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.empty()), Maybe.of(new ExpressionStatement(new ThisExpression())))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IF_STATEMENT);
  }

  @Test
  public void testImportSpecifier() {
    Module test0 = new Module(ImmutableList.empty(), ImmutableList.of(new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(new ImportSpecifier(Maybe.empty(), new BindingIdentifier("b"))), "c")));
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.empty(), ImmutableList.of(new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(new ImportSpecifier(Maybe.of("a"), new BindingIdentifier("b"))), "c")));
    assertNoErrors(test1);

    Module test2 = new Module(ImmutableList.empty(), ImmutableList.of(new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(new ImportSpecifier(Maybe.of("2d849"), new BindingIdentifier("b"))), "c")));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_IMPORT_SPECIFIER_NAME);
  }

  @Test
  public void testLabeledStatement() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(Maybe.of("done")))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("5346done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new BreakStatement(Maybe.of("done")))))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_LABEL);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new LabeledStatement("#das*($839da", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.of(new ContinueStatement(Maybe.of("done")))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_LABEL);

  }

  @Test
  public void testLiteralNumericExpression() throws JsError {
    Script test0 = Parser.parseScript("1.0");
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralNumericExpression(-1.0))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NEGATIVE);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralNumericExpression(1.0/0))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_INFINITE);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralNumericExpression(Double.NaN))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NAN);

  }

  @Test
  public void testLiteralRegExpExpression() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralRegExpExpression("[", false, false, false, false, false))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_REG_EX_PATTERN);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralRegExpExpression("[a-z]foo\\\\/bar=([^=\\\\s])+", false, false, false, false, false))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new LiteralRegExpExpression("[a-z]", true, false, false, false, false))));
    assertNoErrors(test4);
  }

  @Test
  public void testStaticMemberExpression() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new StaticMemberExpression(new IdentifierExpression("a"), "b"))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new StaticMemberExpression(new IdentifierExpression("a"), "45829"))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME);
  }

  @Test
  public void testTemplateElement() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("abc"))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("${"))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("`"))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);
  }

  @Test
  public void testTemplateExpression() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"), new TemplateElement("c"), new TemplateElement("c"))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new TemplateExpression(Maybe.empty(), ImmutableList.of(new IdentifierExpression("b"), new TemplateElement("c"), new IdentifierExpression("b"))))));
    assertCorrectErrors(test3, 3, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);
  }

  @Test
  public void testVariableDeclaration() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.empty()))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.empty()))));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.empty()))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test6 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.empty()))));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test7 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.empty()))));
    assertCorrectErrors(test7, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test8 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.empty()))));
    assertCorrectErrors(test8, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);
  }

  @Test
  public void testVariableDeclarationStatement() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test0);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test1);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.CONST_VARIABLE_DECLARATION_MUST_HAVE_INIT);
  }

  @Test
  public void testBindingIdentifierCalledDefaultAndExportDefaultInteraction() {
    Module test0 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(new FunctionDeclaration(false, false, new BindingIdentifier("*default*"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
    assertNoErrors(test0);

    Module test1 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("*default*")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);

    Module test2 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(new ClassDeclaration(new BindingIdentifier("*default*"), Maybe.empty(), ImmutableList.empty()))));
    assertNoErrors(test2);

    Module test3 = new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(new ClassExpression(Maybe.of(new BindingIdentifier("*default*")), Maybe.empty(), ImmutableList.empty()))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);
  }

  @Test
  public void testReturnStatementAndFunctionBodyInteraction() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(new IdentifierExpression("a")))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty()))))));
    assertNoErrors(test2);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(new ThisExpression())))))));
    assertNoErrors(test3);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(new ThisExpression()))))))));
    assertNoErrors(test5);
  }

  @Test
  public void testYieldExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new IdentifierExpression("a"))))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty()))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty())))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty()))))))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty()))))))))));
    assertCorrectErrors(test4, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression()))))))));
    assertCorrectErrors(test5, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test6 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression())))))))));
    assertCorrectErrors(test6, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test7 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression())))))))))));
    assertCorrectErrors(test7, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test8 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty())))))));
    assertNoErrors(test8);

    Script test9 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, true, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty()))))))));
    assertNoErrors(test9);

    Script test10 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty()))))))))));
    assertNoErrors(test10);

    Script test11 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression()))))))));
    assertNoErrors(test11);

    Script test12 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, true, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression())))))))));
    assertNoErrors(test12);

    Script test13 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ThisExpression())))))))))));
    assertNoErrors(test13);
  }

  @Test
  public void testYieldGeneratorExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))));
    assertCorrectErrors(test0, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertCorrectErrors(test1, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertCorrectErrors(test2, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))))));
    assertCorrectErrors(test3, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(false, true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertNoErrors(test4);

    Script test5 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new FunctionExpression(false, true, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertNoErrors(test5);

    Script test6 = new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))))));
    assertNoErrors(test6);
  }
}
