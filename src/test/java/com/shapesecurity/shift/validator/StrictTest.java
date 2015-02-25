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

import static com.shapesecurity.functional.data.ImmutableList.list;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.AstHelper;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.VariableDeclaration.VariableDeclarationKind;
import com.shapesecurity.shift.ast.directive.UnknownDirective;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.operators.AssignmentOperator;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
import com.shapesecurity.shift.ast.property.DataProperty;
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.property.PropertyName;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;

import org.junit.Test;

public class StrictTest extends AstHelper {
  private static FunctionExpression strictFE(Statement s) {
    return new FunctionExpression(Maybe.nothing(), ImmutableList.nil(), new FunctionBody(
        ImmutableList.<Directive>list(
            new UseStrictDirective()), list(s)));
  }

  @Test
  public final void testBasicDirectiveSupport() {
    validStmt(new FunctionDeclaration(ID, ImmutableList.nil(), new FunctionBody(
            ImmutableList.<Directive>list(new UseStrictDirective()),
        ImmutableList.nil())));
    validStmt(new FunctionDeclaration(ID, ImmutableList.nil(), new FunctionBody(
            ImmutableList.<Directive>list(
                new UseStrictDirective(),
                new UnknownDirective("directive")), ImmutableList.nil())));

    validStmt(exprStmt(new FunctionExpression(Maybe.nothing(), ImmutableList.nil(), new FunctionBody(
                ImmutableList.list(
                    new UseStrictDirective()), ImmutableList.nil()))));
    validStmt(exprStmt(new FunctionExpression(Maybe.nothing(), ImmutableList.nil(), new FunctionBody(
                ImmutableList.list(
                    new UseStrictDirective(), new UnknownDirective("directive")), ImmutableList.nil()))));
    assertOk(Validator.validate(new Script(new FunctionBody(
                ImmutableList.<Directive>list(new UseStrictDirective()),
        ImmutableList.nil()))));
    validExpr(new FunctionExpression(Maybe.nothing(), ImmutableList.nil(), new FunctionBody(
            ImmutableList.<Directive>list(
                new UseStrictDirective()), ImmutableList.nil())));
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
    validExpr(new FunctionExpression(Maybe.just(new Identifier("eval")), ImmutableList.nil(), BLOCK_WRAPPED));
    validExpr(new FunctionExpression(Maybe.just(new Identifier("arguments")), ImmutableList.nil(), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(new Identifier("eval"), ImmutableList.nil(), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(new Identifier("arguments"), ImmutableList.nil(), BLOCK_WRAPPED));

    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.just(new Identifier("eval")), ImmutableList.nil(),
        BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.just(new Identifier("arguments")),
        ImmutableList.nil(), BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(new FunctionDeclaration(new Identifier("eval"), ImmutableList.nil(), BLOCK_WRAPPED)));
    invalidExpr(1, strictFE(new FunctionDeclaration(new Identifier("arguments"), ImmutableList.nil(),
        BLOCK_WRAPPED)));
  }

  @Test
  public final void testFunctionParametersNotBeRestrictedInStrictMode() {
    validExpr(new FunctionExpression(Maybe.nothing(), list(new Identifier("eval")), BLOCK_WRAPPED));
    validExpr(new FunctionExpression(Maybe.nothing(), list(new Identifier("arguments")), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(ID, list(new Identifier("eval")), BLOCK_WRAPPED));
    validStmt(new FunctionDeclaration(ID, list(new Identifier("arguments")), BLOCK_WRAPPED));

    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.nothing(),
        list(new Identifier("eval")),
        BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(exprStmt(new FunctionExpression(Maybe.nothing(),
        list(new Identifier("arguments")),
        BLOCK_WRAPPED))));
    invalidExpr(1, strictFE(new FunctionDeclaration(ID, list(new Identifier("eval")), BLOCK_WRAPPED)));
    invalidExpr(1, strictFE(new FunctionDeclaration(ID, list(new Identifier("arguments")), BLOCK_WRAPPED)));
  }

  @Test
  public final void testSetterParametersNotBeRestrictedInStrictMode() {
    validExpr(new ObjectExpression(
            ImmutableList.<ObjectProperty>list(
                new Setter(
                    new PropertyName(ID), new Identifier("eval"),
                    BLOCK_WRAPPED))));
    validExpr(new ObjectExpression(
            ImmutableList.<ObjectProperty>list(
                new Setter(
                    new PropertyName(ID), new Identifier(
                    "arguments"), BLOCK_WRAPPED))));

    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(
                    ImmutableList.<ObjectProperty>list(
                        new Setter(
                            new PropertyName(ID),
                            new Identifier("eval"), BLOCK_WRAPPED))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(
                    ImmutableList.<ObjectProperty>list(
                        new Setter(
                            new PropertyName(ID),
                            new Identifier("arguments"), BLOCK_WRAPPED))))));
  }

  @Test
  public final void testAssignmentExpressionNotBeRestrictedInStrictMode() {
    validExpr(new AssignmentExpression(AssignmentOperator.Assign, new IdentifierExpression(new Identifier("eval")), EXPR));
    validExpr(new AssignmentExpression(AssignmentOperator.Assign, new IdentifierExpression(new Identifier("arguments")), EXPR));

    invalidExpr(1, strictFE(exprStmt(new AssignmentExpression(
                    AssignmentOperator.Assign, new IdentifierExpression(
        new Identifier("eval")), EXPR))));
    invalidExpr(1, strictFE(exprStmt(new AssignmentExpression(
                    AssignmentOperator.Assign, new IdentifierExpression(
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
    validExpr(new ObjectExpression(
            ImmutableList.<ObjectProperty>list(
                new DataProperty(new PropertyName("a"), EXPR),
                new DataProperty(new PropertyName("a"), EXPR))));
    validExpr(new ObjectExpression(
            ImmutableList.<ObjectProperty>list(
                new DataProperty(new PropertyName("__proto__"), EXPR),
                new DataProperty(new PropertyName("a"), EXPR))));

    validExpr(strictFE(exprStmt(new ObjectExpression(
                    ImmutableList.<ObjectProperty>list(
                        new DataProperty(
                            new PropertyName(
                                "hasOwnProperty"), EXPR), new DataProperty(new PropertyName("a"), EXPR))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(
                    ImmutableList.<ObjectProperty>list(
                        new DataProperty(
                            new PropertyName(
                                "a"), EXPR), new DataProperty(new PropertyName("a"), EXPR))))));
    invalidExpr(1, strictFE(exprStmt(new ObjectExpression(
                    ImmutableList.<ObjectProperty>list(
                        new DataProperty(
                            new PropertyName(
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
