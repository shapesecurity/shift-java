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
//package com.shapesecurity.shift.others;
//
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import Block;
//import Expression;
//import FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import Script;
//import Statement;
//import VariableDeclaration;
//import VariableDeclarator;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.ObjectExpression;
//import com.shapesecurity.shift.ast.property.DataProperty;
//import com.shapesecurity.shift.ast.property.ObjectProperty;
//import com.shapesecurity.shift.ast.property.PropertyName;
//import com.shapesecurity.shift.ast.statement.BlockStatement;
//import com.shapesecurity.shift.ast.statement.EmptyStatement;
//import com.shapesecurity.shift.ast.statement.ExpressionStatement;
//import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
//import com.shapesecurity.shift.ast.statement.LabeledStatement;
//import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
//import com.shapesecurity.shift.ast.statement.WhileStatement;
//import ValidationError;
//import Validator;
//
//import org.junit.Assert;
//
//public class AstHelper extends TestBase {
//  public static final EmptyStatement STMT = new EmptyStatement();
//  public static final Block BLOCK = new Block(ImmutableList.empty());
//  public static final BlockStatement BLOCK_STMT = new BlockStatement(BLOCK);
//  public static final FunctionBody EMPTY_BODY =
//      new FunctionBody(ImmutableList.empty(), ImmutableList.empty());
//  public static final FunctionBody BLOCK_WRAPPED =
//      new FunctionBody(ImmutableList.empty(), ImmutableList.of(BLOCK_STMT));
//  public static final LiteralNullExpression EXPR = new LiteralNullExpression();
//  public static final LiteralNumericExpression NUM = new LiteralNumericExpression(0);
//  public static final Identifier ID = new Identifier("a");
//  public static final Identifier BAD_ID = new Identifier("if");
//
//  // wrap a statement in a program
//  public static Script wrapProgram(Statement s) {
//    return new Script(new FunctionBody(ImmutableList.empty(), ImmutableList.of(s)));
//  }
//
//  // wrap a statement in an iteration statement
//  public static Statement wrapIter(Statement s) {
//    return new WhileStatement(new LiteralBooleanExpression(true), s);
//  }
//
//  protected FunctionBody body(Statement... statements) {
//    return new FunctionBody(ImmutableList.empty(), ImmutableList.from(statements));
//  }
//
//  protected IdentifierExpression identExpr(String name) {
//    return new IdentifierExpression(ident(name));
//  }
//
//  protected Identifier ident(String name) {
//    return new Identifier(name);
//  }
//
//  public static VariableDeclarator declarator(String d) {
//    return new VariableDeclarator(new Identifier(d), Maybe.empty());
//  }
//
//  public static VariableDeclarationStatement varss(
//      VariableDeclaration.VariableDeclarationKind kind,
//      String d0,
//      String... d) {
//    return new VariableDeclarationStatement(vars(kind, d0, d));
//  }
//
//  public static VariableDeclaration vars(VariableDeclaration.VariableDeclarationKind kind, String d0, String... d) {
//    return new VariableDeclaration(kind, ImmutableList.of(d0, d).map(AstHelper::declarator));
//  }
//
//  // wrap zero or more statements in a function expression
//  public static FunctionExpression FE(Statement s) {
//    return new FunctionExpression(Maybe.of(ID), ImmutableList.empty(), new FunctionBody(
//        ImmutableList.empty(),
//        ImmutableList.of(s)));
//  }
//
//  // wrap zero or more statements in a function declaration
//  public static FunctionDeclaration FD(Statement s) {
//    return new FunctionDeclaration(ID, ImmutableList.empty(), new FunctionBody(ImmutableList.empty(), ImmutableList.of(s)));
//  }
//
//  // wrap a statement in a LabeledStatement
//  public static LabeledStatement label(String l, Statement n) {
//    return new LabeledStatement(new Identifier(l), n);
//  }
//
//  // wrap an expression in an ExpressionStatement
//  public static ExpressionStatement exprStmt(Expression e) {
//    return new ExpressionStatement(e);
//  }
//
//  public static void printErrs(ImmutableList<ValidationError> errs) {
//    if (!errs.isEmpty()) {
//      for (ValidationError validationError : errs) {
//        System.out.println("ERROR>>>  " + validationError.message);
//      }
//    }
//  }
//
//  public static void assertOk(ImmutableList<ValidationError> errs) {
//    if (!errs.isEmpty()) {
//      printErrs(errs);
//    }
//    Assert.assertEquals(0, errs.length);
//  }
//
//  public static void validStmt(Statement s) {
//    assertOk(Validator.validate(wrapProgram(s)));
//  }
//
//  public static void invalidStmt(int numExpectedErrs, Statement s) {
//    ImmutableList<ValidationError> errs = Validator.validate(wrapProgram(s));
//    Assert.assertTrue(!errs.isEmpty());
//    Assert.assertEquals(errs.length, numExpectedErrs);
//  }
//
//  public static void validExpr(Expression e) {
//    assertOk(Validator.validate(wrapProgram(exprStmt(e))));
//  }
//
//  public static void invalidExpr(int numExpectedErrs, Expression e) {
//    ImmutableList<ValidationError> errs = Validator.validate(wrapProgram(exprStmt(e)));
//    Assert.assertTrue(!errs.isEmpty());
//    Assert.assertEquals(numExpectedErrs, errs.length);
//  }
//
//  protected static ObjectExpression obj(ObjectProperty... properties) {
//    return new ObjectExpression(ImmutableList.from(properties));
//  }
//
//  protected static PropertyName pn(Identifier ident) {
//    return new PropertyName(ident);
//  }
//
//  protected static PropertyName pn(double value) {
//    return new PropertyName(value);
//  }
//
//  protected static DataProperty init(PropertyName propertyName, Expression value) {
//    return new DataProperty(propertyName, value);
//  }
//}
