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

package com.shapesecurity.laserbat.js;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.Directive;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.ast.VariableDeclarator;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.ast.statement.EmptyStatement;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.laserbat.js.ast.statement.LabeledStatement;
import com.shapesecurity.laserbat.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.validator.ValidationError;
import com.shapesecurity.laserbat.js.validator.Validator;

import org.junit.Assert;

public class AstHelper extends TestBase {
  public static final EmptyStatement STMT = new EmptyStatement();
  public static final Block BLOCK = new Block(List.<Statement>nil());
  public static final BlockStatement BLOCK_STMT = new BlockStatement(BLOCK);
  public static final FunctionBody EMPTY_BODY =
      new FunctionBody(List.<Directive>nil(), List.<Statement>nil());
  public static final FunctionBody BLOCK_WRAPPED =
      new FunctionBody(List.<Directive>nil(), List.<Statement>list(BLOCK_STMT));
  public static final LiteralNullExpression EXPR = new LiteralNullExpression();
  public static final LiteralNumericExpression NUM = new LiteralNumericExpression(0);
  public static final Identifier ID = new Identifier("a");

  // wrap a statement in a program
  public static Script wrapProgram(Statement s) {
    return new Script(new FunctionBody(List.<Directive>nil(), List.list(s)));
  }

  // wrap a statement in an iteration statement
  public static Statement wrapIter(Statement s) {
    return new WhileStatement(new LiteralBooleanExpression(true), s);
  }

  protected FunctionBody body(Statement... statements) {
    return new FunctionBody(List.nil(), List.from(statements));
  }

  protected IdentifierExpression identExpr(String name) {
    return new IdentifierExpression(ident(name));
  }

  protected Identifier ident(String name) {
    return new Identifier(name);
  }

  public static VariableDeclarator declarator(String d) {
    return new VariableDeclarator(new Identifier(d));
  }

  public static VariableDeclarationStatement varss(
      VariableDeclaration.VariableDeclarationKind kind,
      String d0,
      String... d) {
    return new VariableDeclarationStatement(vars(kind, d0, d));
  }

  public static VariableDeclaration vars(VariableDeclaration.VariableDeclarationKind kind, String d0, String... d) {
    return new VariableDeclaration(kind, List.list(d0, d).map(AstHelper::declarator));
  }

  // wrap zero or more statements in a function expression
  public static FunctionExpression FE(Statement s) {
    return new FunctionExpression(Maybe.just(ID), List.<Identifier>nil(), new FunctionBody(List.<Directive>nil(),
        List.list(s)));
  }

  // wrap zero or more statements in a function declaration
  public static FunctionDeclaration FD(Statement s) {
    return new FunctionDeclaration(ID, List.<Identifier>nil(), new FunctionBody(List.<Directive>nil(), List.list(s)));
  }

  // wrap a statement in a LabeledStatement
  public static LabeledStatement label(String l, Statement n) {
    return new LabeledStatement(new Identifier(l), n);
  }

  // wrap an expression in an ExpressionStatement
  public static ExpressionStatement exprStmt(Expression e) {
    return new ExpressionStatement(e);
  }

  public static void printErrs(List<ValidationError> errs) {
    if (!errs.isEmpty()) {
      for (ValidationError validationError : errs) {
        System.out.println("ERROR>>>  " + validationError.message);
      }
    }
  }

  public static void assertOk(List<ValidationError> errs) {
    if (!errs.isEmpty()) {
      printErrs(errs);
    }
    Assert.assertEquals(0, errs.length());
  }

  public static void validStmt(Statement s) {
    assertOk(Validator.validate(wrapProgram(s)));
  }

  public static void invalidStmt(int numExpectedErrs, Statement s) {
    List<ValidationError> errs = Validator.validate(wrapProgram(s));
    Assert.assertTrue(!errs.isEmpty());
    Assert.assertEquals(errs.length(), numExpectedErrs);
  }

  public static void validExpr(Expression e) {
    assertOk(Validator.validate(wrapProgram(exprStmt(e))));
  }

  public static void invalidExpr(int numExpectedErrs, Expression e) {
    List<ValidationError> errs = Validator.validate(wrapProgram(exprStmt(e)));
    Assert.assertTrue(!errs.isEmpty());
    Assert.assertEquals(numExpectedErrs, errs.length());
  }
}
