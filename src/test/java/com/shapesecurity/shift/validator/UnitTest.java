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
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_BINDING_IDENTIFIER_NAME);
  }

  @Test
  public void testBreakStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { break; }");
    assertCorrectFailures(test0, 0, "");

    Script test1 = Parser.parseScript("done: while (true) { break done; }");
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("1done")))))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_BREAK_STATEMENT_LABEL);
  }

  @Test
  public void testCatchClause() throws JsError {
    Script test0 = Parser.parseScript("try{} catch(e){} finally{}");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new TryFinallyStatement(new Block(ImmutableList.nil()), Maybe.just(new CatchClause(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), new Block(ImmutableList.nil()))), new Block(ImmutableList.nil()))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.CATCH_CLAUSE_BINDING_NOT_MEMBER_EXPRESSION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new TryFinallyStatement(new Block(ImmutableList.nil()), Maybe.just(new CatchClause(new StaticMemberExpression("a", new IdentifierExpression("b")), new Block(ImmutableList.nil()))), new Block(ImmutableList.nil()))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.CATCH_CLAUSE_BINDING_NOT_MEMBER_EXPRESSION);
  }

  @Test
  public void testContinueStatement() throws JsError {
    Script test0 = Parser.parseScript("for(var i = 0; i < 10; i++) { continue; }");
    assertCorrectFailures(test0, 0, "");

    Script test1 = Parser.parseScript("done: while (true) { continue done; }");
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.just("1done")))))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_CONTINUE_STATEMENT_LABEL);
  }

  @Test
  public void testDirective() {
    Script test0 = new Script(ImmutableList.list(new Directive("use strict;"), new Directive("linda;"), new Directive(".-#($*&#")), ImmutableList.nil());
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.list(new Directive("'use strict;")), ImmutableList.nil());
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test2 = new Script(ImmutableList.list(new Directive("'use strict;"), new Directive("linda';"), new Directive("'.-#($*&#")), ImmutableList.nil());
    assertCorrectFailures(test2, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test3 = new Script(ImmutableList.list(new Directive("'use stri\"ct;"), new Directive("li\"nda;'"), new Directive(".-\"#'($*&#")), ImmutableList.nil());
    assertCorrectFailures(test3, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test4 = new Script(ImmutableList.list(new Directive("'use s\ntrict;"), new Directive("lind\na;'"), new Directive(".-#'($\n*&#")), ImmutableList.nil());
    assertCorrectFailures(test4, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test5 = new Script(ImmutableList.list(new Directive("'use s\rtrict;"), new Directive("lind\ra;'"), new Directive(".-#'($\r*&#")), ImmutableList.nil());
    assertCorrectFailures(test5, 3, ValidationErrorMessages.VALID_DIRECTIVE);

    Script test6 = new Script(ImmutableList.list(new Directive("('\\x0');"), new Directive("('\u2028')"), new Directive("(\\u{110000}\\\")")), ImmutableList.nil());
    assertCorrectFailures(test6, 3, ValidationErrorMessages.VALID_DIRECTIVE);
  }

  @Test
  public void testExportSpecifier() throws JsError {
    Module test0 = Parser.parseModule("export * from 'a';");
    assertCorrectFailures(test0, 0, "");

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportSpecifier(Maybe.just("2a_"), "b")), Maybe.just("a"))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_EXPORT_SPECIFIER_NAME);

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportSpecifier(Maybe.just("b"), "%dlk45")), Maybe.just("a"))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_EXPORTED_NAME);

    Module test3 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportFrom(ImmutableList.list(new ExportSpecifier(Maybe.nothing(), "a")), Maybe.just("a"))));
    assertCorrectFailures(test3, 0, "");
  }

  @Test
  public void testForInStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x in [1,2,3]){}");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN);
  }

  @Test
  public void testForOfStatement() throws JsError {
    Script test0 = Parser.parseScript("for (var x of [1,2,3]){}");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new BindingIdentifier("b"), Maybe.nothing()))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))), new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(0.0)), Maybe.just(new LiteralNumericExpression(1.0)))), new EmptyStatement())));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF);
  }

  @Test
  public void testFormalParameters() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new BindingIdentifier("a")), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b"))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.FORMAL_PARAMETER_ITEMS_NOT_MEMBER_EXPRESSION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new StaticMemberExpression("a", new IdentifierExpression("b"))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.FORMAL_PARAMETER_ITEMS_NOT_MEMBER_EXPRESSION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new BindingWithDefault(new BindingIdentifier("a"), new IdentifierExpression("b"))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test4, 0, "");

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new BindingWithDefault(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), new IdentifierExpression("b"))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.FORMAL_PARAMETER_ITEMS_BINDING_NOT_MEMBER_EXPRESSION);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("hello"), false, new FormalParameters(ImmutableList.list(new BindingWithDefault(new StaticMemberExpression("a", new IdentifierExpression("b")), new IdentifierExpression("b"))), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("z"), ImmutableList.nil())))))));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.FORMAL_PARAMETER_ITEMS_BINDING_NOT_MEMBER_EXPRESSION);
  }

  @Test
  public void testIdentifierExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("linda"))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("9458723"))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("A*9458723"))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("0lkdjaf"))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.VALID_IDENTIFIER_NAME);
  }

  @Test
  public void testIfStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.just(new ExpressionStatement(new ThisExpression())))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new IdentifierExpression("a"), new ExpressionStatement(new IdentifierExpression("b")), Maybe.nothing())));
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new IfStatement(new IdentifierExpression("a"), new IfStatement(new ThisExpression(), new ExpressionStatement(new ThisExpression()), Maybe.nothing()), Maybe.just(new ExpressionStatement(new ThisExpression())))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_IF_STATEMENT);
  }

  @Test
  public void testImportSpecifier() {
    Module test0 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.nothing(), new BindingIdentifier("b"))), "c")));
    assertCorrectFailures(test0, 0, "");

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.just("a"), new BindingIdentifier("b"))), "c")));
    assertCorrectFailures(test1, 0, "");

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new Import(Maybe.just(new BindingIdentifier("a")), ImmutableList.list(new ImportSpecifier(Maybe.just("2d849"), new BindingIdentifier("b"))), "c")));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_IMPORT_SPECIFIER_NAME);
  }

  @Test
  public void testLabeledStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("done")))))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("5346done", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new BreakStatement(Maybe.just("done")))))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_LABEL);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new LabeledStatement("#das*($839da", new WhileStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(ImmutableList.list(new ContinueStatement(Maybe.just("done")))))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_LABEL);

  }

  @Test
  public void testLiteralNumericExpression() throws JsError {
    Script test0 = Parser.parseScript("1.0");
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(-1.0))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NEGATIVE);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(1.0/0))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_INFINITE);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralNumericExpression(Double.NaN))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NAN);

  }

  @Test
  public void testLiteralRegExpExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("@", ""))));
    assertCorrectFailures(test0, 1, ValidationErrorMessages.VALID_REG_EX_PATTERN);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("/a/", "j"))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_REG_EX_FLAG);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new LiteralRegExpExpression("/a/", "gg"))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.NO_DUPLICATE_REG_EX_FLAG);
  }

  @Test
  public void testSetter() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.SETTER_PARAM_NOT_MEMBER_EXPRESSION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new StaticMemberExpression("a", new IdentifierExpression("b")), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.SETTER_PARAM_NOT_MEMBER_EXPRESSION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new BindingWithDefault(new BindingIdentifier("a"), new LiteralNumericExpression(0.0)), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test3, 0, "");

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new BindingWithDefault(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), new LiteralNumericExpression(0.0)), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.SETTER_PARAM_BINDING_NOT_MEMBER_EXPRESSION);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Setter(new BindingWithDefault(new StaticMemberExpression("a", new IdentifierExpression("b")), new LiteralNumericExpression(0.0)), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))), new StaticPropertyName("width")))))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.SETTER_PARAM_BINDING_NOT_MEMBER_EXPRESSION);
  }

  @Test
  public void testShorthandProperty() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new ShorthandProperty("a"), new ShorthandProperty("asajd_nk"))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new ShorthandProperty("429adk"), new ShorthandProperty("asajd_nk"))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_SHORTHAND_PROPERTY_NAME);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new ShorthandProperty("a"), new ShorthandProperty("^34d9"))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_SHORTHAND_PROPERTY_NAME);
  }

  @Test
  public void testStaticMemberExpression() throws JsError {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new StaticMemberExpression("b", new IdentifierExpression("a")))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new StaticMemberExpression("45829", new IdentifierExpression("a")))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME);
  }

  @Test
  public void testTemplateElement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("abc"))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("\"abc'"))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("'abc\""))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE);
  }

  @Test
  public void testTemplateExpression() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new IdentifierExpression("b"), new TemplateElement("c"))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("a"), new IdentifierExpression("b"), new TemplateElement("c"), new TemplateElement("c"), new TemplateElement("c"))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new IdentifierExpression("b"), new TemplateElement("c"), new IdentifierExpression("b"))))));
    assertCorrectFailures(test3, 3, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS);
  }

  @Test
  public void testVariableDeclaration() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test2, 0, "");

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list()))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list()))));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list()))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.nil()))));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test7 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.nil()))));
    assertCorrectFailures(test7, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);

    Script test8 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.nil()))));
    assertCorrectFailures(test8, 1, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST);
  }

  @Test
  public void testVariableDeclarationStatement() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test2, 0, "");

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertCorrectFailures(test3, 0, "");

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertCorrectFailures(test4, 0, "");

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.CONST_VARIABLE_DECLARATION_MUST_HAVE_INIT);
  }

  @Test
  public void testVariableDeclarator() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test0, 0, "");

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test1, 0, "");

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test2, 0, "");

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new StaticMemberExpression("a", new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new StaticMemberExpression("a", new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);

    Script test7 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test7, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);

    Script test8 = new Script(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(new VariableDeclarator(new StaticMemberExpression("a", new IdentifierExpression("b")), Maybe.just(new LiteralNumericExpression(0.0))))))));
    assertCorrectFailures(test8, 1, ValidationErrorMessages.VARIABLE_DECLARATION_BINDING_NOT_MEMBER_EXPRESSION);
  }

  @Test
  public void testBindingIdentifierCalledDefaultAndExportDefaultInteraction() {
    Module test0 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new FunctionDeclaration(new BindingIdentifier("*default*"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));
    assertCorrectFailures(test0, 0, "");

    Module test1 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new FunctionExpression(Maybe.just(new BindingIdentifier("*default*")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);

    Module test2 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new ClassDeclaration(new BindingIdentifier("*default*"), Maybe.nothing(), ImmutableList.nil()))));
    assertCorrectFailures(test2, 0, "");

    Module test3 = new Module(ImmutableList.nil(), ImmutableList.list(new ExportDefault(new ClassExpression(Maybe.just(new BindingIdentifier("*default*")), Maybe.nothing(), ImmutableList.nil()))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT);
  }

  @Test
  public void testReturnStatementAndFunctionBodyInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())));
    assertCorrectFailures(test0, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new IdentifierExpression("a")))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing()))))));
    assertCorrectFailures(test2, 0 , "");

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new ThisExpression())))))));
    assertCorrectFailures(test3, 0 , "");

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())))))));
    assertCorrectFailures(test4, 0 , "");

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(new ThisExpression()))))))));
    assertCorrectFailures(test5, 0 , "");
  }

  @Test
  public void testYieldExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new IdentifierExpression("a"))))));
    assertCorrectFailures(test0, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test4, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))))));
    assertCorrectFailures(test5, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))));
    assertCorrectFailures(test6, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test7 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test7, 1, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION);

    Script test8 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))))));
    assertCorrectFailures(test8, 0, "");

    Script test9 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing()))))))));
    assertCorrectFailures(test9, 0, "");

    Script test10 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test10, 0, "");

    Script test11 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))))));
    assertCorrectFailures(test11, 0, "");

    Script test12 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression())))))))));
    assertCorrectFailures(test12, 0, "");

    Script test13 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ThisExpression()))))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test13, 0, "");
  }

  @Test
  public void testYieldGeneratorExpressionAndFunctionDeclarationFunctionExpressionMethodInteraction() {
    Script test0 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new IdentifierExpression("a")))));
    assertCorrectFailures(test0, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test1 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertCorrectFailures(test1, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test2 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertCorrectFailures(test2, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test3 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test3, 1, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION);

    Script test4 = new Script(ImmutableList.nil(), ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("a"), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))))));
    assertCorrectFailures(test4, 0, "");

    Script test5 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new FunctionExpression(Maybe.just(new BindingIdentifier("a")), true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression()))))))));
    assertCorrectFailures(test5, 0, "");

    Script test6 = new Script(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new YieldGeneratorExpression(new ThisExpression())))), new StaticPropertyName("a")))))));
    assertCorrectFailures(test6, 0, "");
  }
}
