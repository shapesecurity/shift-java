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

package com.shapesecurity.shift.js.validator;

import static com.shapesecurity.shift.functional.data.List.list;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.AstHelper;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.VariableDeclaration.VariableDeclarationKind;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.operators.Assignment;
import com.shapesecurity.shift.js.ast.operators.PrefixOperator;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;

import org.junit.Test;

public class StrictTest extends AstHelper {
  private static FunctionExpression strictFE(Statement s) {
    return new FunctionExpression(List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective()), list(s)));
  }

  @Test
  public final void testBasicDirectiveSupport() {
    validStmt(new FunctionDeclaration(ID, List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective()), List.<Statement>nil())));
    validStmt(new FunctionDeclaration(ID, List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective(), new UnknownDirective("directive")), List.<Statement>nil())));

    validStmt(exprStmt(new FunctionExpression(List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective()), List.<Statement>nil()))));
    validStmt(exprStmt(new FunctionExpression(List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective(), new UnknownDirective("directive")), List.<Statement>nil()))));
    assertOk(Validator.validate(new Script(new FunctionBody(List.<Directive>list(new UseStrictDirective()),
        List.<Statement>nil()))));
    validExpr(new FunctionExpression(List.<Identifier>nil(), new FunctionBody(List.<Directive>list(
        new UseStrictDirective()), List.<Statement>nil())));
  }

  @Test
  public final void testCatchClauseParamMustNotBeRestrictedInStrictMode() {
    validStmt(new TryCatchStatement(BLOCK, new CatchClause(new Identifier("eval"), BLOCK)));
    validStmt(new TryCatchStatement(BLOCK, new CatchClause(new Identifier("arguments"), BLOCK)));

    validExpr(strictFE(new TryCatchStatement(BLOCK, new CatchClause(new Identifier("x"), BLOCK))));
    invalidExpr(1, strictFE(new TryCatchStatement(BLOCK, new CatchClause(new Identifier("eval"), BLOCK))));
    invalidExpr(1, strictFE(new TryCatchStatement(BLOCK, new CatchClause(new Identifier("arguments"), BLOCK))));
  }

  @Test
  public final void testFunctionNamesMustNotBeRestrictedInStrictMode() {
    validExpr(new FunctionExpression(Maybe.just(new Identifier("eval")), List.<Identifier>nil(), BLOCK_WRAPPED));
    validExpr(new FunctionExpression(Maybe.just(new Identifier("arguments")), List.<Identifier>nil(), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(new Identifier("eval"), List.<Identifier>nil(), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(new Identifier("arguments"), List.<Identifier>nil(), BLOCK_WRAPPED));

    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.just(new Identifier("eval")), List.<Identifier>nil(),
        BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.just(new Identifier("arguments")),
        List.<Identifier>nil(), BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(new FunctionDeclaration(new Identifier("eval"), List.<Identifier>nil(), BLOCK_WRAPPED)));
    invalidExpr(1, strictFE(new FunctionDeclaration(new Identifier("arguments"), List.<Identifier>nil(),
        BLOCK_WRAPPED)));
  }

  @Test
  public final void testFunctionParametersNotBeRestrictedInStrictMode() {
    validExpr(new FunctionExpression(list(new Identifier("eval")), BLOCK_WRAPPED));
    validExpr(new FunctionExpression(list(new Identifier("arguments")), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(ID, list(new Identifier("eval")), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(ID, list(new Identifier("arguments")), BLOCK_WRAPPED));

    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(list(new Identifier("eval")), BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(list(new Identifier("arguments")), BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(new FunctionDeclaration(ID, list(new Identifier("eval")), BLOCK_WRAPPED)));
    invalidExpr(1, strictFE(new FunctionDeclaration(ID, list(new Identifier("arguments")), BLOCK_WRAPPED)));
  }

  @Test
  public final void testSetterParametersNotBeRestrictedInStrictMode() {
    validExpr(new ObjectExpression(List.<ObjectProperty>list(new Setter(new PropertyName(ID), new Identifier("eval"),
        BLOCK_WRAPPED))));
    validExpr(new ObjectExpression(List.<ObjectProperty>list(new Setter(new PropertyName(ID), new Identifier(
        "arguments"), BLOCK_WRAPPED))));

    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(List.<ObjectProperty>list(new Setter(new PropertyName(ID),
        new Identifier("eval"), BLOCK_WRAPPED))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(List.<ObjectProperty>list(new Setter(new PropertyName(ID),
        new Identifier("arguments"), BLOCK_WRAPPED))))));
  }

  @Test
  public final void testAssignmentExpressionNotBeRestrictedInStrictMode() {
    validExpr(new AssignmentExpression(Assignment.Assign, new IdentifierExpression(new Identifier("eval")), EXPR));
    validExpr(new AssignmentExpression(Assignment.Assign, new IdentifierExpression(new Identifier("arguments")), EXPR));

    invalidExpr(1, strictFE(exprStmt(new AssignmentExpression(Assignment.Assign, new IdentifierExpression(
        new Identifier("eval")), EXPR))));
    invalidExpr(1, strictFE(exprStmt(new AssignmentExpression(Assignment.Assign, new IdentifierExpression(
        new Identifier("arguments")), EXPR))));
  }

  @Test
  public final void testVariableDeclarationsNotBeRestrictedInStrictMode() {
    validStmt(varss(VariableDeclarationKind.Var, "eval"));
    validStmt(varss(VariableDeclarationKind.Var, "arguments"));
    validStmt(varss(VariableDeclarationKind.Let, "eval"));
    validStmt(varss(VariableDeclarationKind.Let, "arguments"));

    invalidExpr(1, strictFE(varss(VariableDeclarationKind.Var, "eval")));
    invalidExpr(1, strictFE(varss(VariableDeclarationKind.Var, "arguments")));
    invalidExpr(1, strictFE(varss(VariableDeclarationKind.Let, "eval")));
    invalidExpr(1, strictFE(varss(VariableDeclarationKind.Let, "arguments")));
  }

  @Test
  public final void testFunctionDeclarationParameterNamesMustBeUniqueInStrictMode() {
    validExpr(strictFE(new FunctionDeclaration(ID, list(ID), BLOCK_WRAPPED)));
    validExpr(strictFE(new FunctionDeclaration(new Identifier("a"), list(new Identifier("A")), BLOCK_WRAPPED)));
    validStmt(new FunctionDeclaration(ID, list(ID, ID), BLOCK_WRAPPED));

    invalidExpr(1, strictFE(new FunctionDeclaration(ID, list(ID, ID), BLOCK_WRAPPED)));
  }

  @Test
  public final void testFunctionExpressionParameterNamesMustBeUniqueInStrictMode() {
    validExpr(strictFE(exprStmt(FE(BLOCK_STMT))));
    validExpr(strictFE(exprStmt(new FunctionExpression(Maybe.just(ID), list(new Identifier("a"), new Identifier(
        "A")), BLOCK_WRAPPED))));

    validExpr(new FunctionExpression(Maybe.just(ID), list(ID, ID), BLOCK_WRAPPED));
    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.just(ID), list(ID, ID), BLOCK_WRAPPED))));
  }

  @Test
  public final void testIdentifierFutureReservedWords() {
    validExpr(new IdentifierExpression(new Identifier("let")));
    validExpr(new IdentifierExpression(new Identifier("yield")));

    invalidExpr(1, strictFE(exprStmt(new IdentifierExpression(new Identifier("let")))));
    invalidExpr(1, strictFE(exprStmt(new IdentifierExpression(new Identifier("yield")))));
  }

  @Test
  public final void testObjectExpressionDuplicateKeys() {
    validExpr(new ObjectExpression(List.<ObjectProperty>list(new DataProperty(new PropertyName("a"), EXPR),
        new DataProperty(new PropertyName("a"), EXPR))));
    validExpr(new ObjectExpression(List.<ObjectProperty>list(new DataProperty(new PropertyName("__proto__"), EXPR),
        new DataProperty(new PropertyName("a"), EXPR))));

    validExpr(strictFE(exprStmt(new ObjectExpression(List.<ObjectProperty>list(new DataProperty(new PropertyName(
        "hasOwnProperty"), EXPR), new DataProperty(new PropertyName("a"), EXPR))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(List.<ObjectProperty>list(new DataProperty(new PropertyName(
        "a"), EXPR), new DataProperty(new PropertyName("a"), EXPR))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(List.<ObjectProperty>list(new DataProperty(new PropertyName(
        0), EXPR), new DataProperty(new PropertyName(0), EXPR))))));
  }

  @Test
  public final void testUnaryExpressionDeleteWithUnqualifiedIdentifier() {
    validExpr(new PrefixExpression(PrefixOperator.Delete, NUM));
    validExpr(new PrefixExpression(PrefixOperator.Delete, new IdentifierExpression(ID)));
    validExpr(strictFE(exprStmt(new PrefixExpression(PrefixOperator.Delete, NUM))));
    invalidExpr(1, strictFE(exprStmt(new PrefixExpression(PrefixOperator.Delete, new IdentifierExpression(ID)))));
  }

  @Test
  public final void testWithStatementNotAllowed() {
    validStmt(new WithStatement(EXPR, STMT));
    invalidExpr(1, strictFE(new WithStatement(EXPR, STMT)));
  }
}
