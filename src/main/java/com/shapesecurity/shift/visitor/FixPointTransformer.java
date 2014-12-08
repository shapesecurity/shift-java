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

package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.directive.UnknownDirective;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.expression.ArrayExpression;
import com.shapesecurity.shift.ast.expression.BinaryExpression;
import com.shapesecurity.shift.ast.expression.CallExpression;
import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.ast.expression.NewExpression;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.expression.PostfixExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.ast.expression.ThisExpression;
import com.shapesecurity.shift.ast.property.DataProperty;
import com.shapesecurity.shift.ast.property.Getter;
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.property.PropertyName;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.BlockStatement;
import com.shapesecurity.shift.ast.statement.BreakStatement;
import com.shapesecurity.shift.ast.statement.ContinueStatement;
import com.shapesecurity.shift.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.ast.statement.EmptyStatement;
import com.shapesecurity.shift.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.ast.statement.ForInStatement;
import com.shapesecurity.shift.ast.statement.ForStatement;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.IfStatement;
import com.shapesecurity.shift.ast.statement.LabeledStatement;
import com.shapesecurity.shift.ast.statement.ReturnStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.ast.statement.ThrowStatement;
import com.shapesecurity.shift.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.ast.statement.WhileStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.path.Branch;

import org.jetbrains.annotations.NotNull;

public class FixPointTransformer extends LazyCloner {
  @NotNull
  private final TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>>
      t;

  private final Director<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>> d;

  public FixPointTransformer(
      @NotNull TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends
          DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends
          DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends
          DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ?
          extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>> t) {
    super();
    this.t = t;
    this.d = new Director<>(this);
  }

  private F<Expression, DirtyState<Expression>> bindExp(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceExpression(node1, path).setDirty());
  }

  private F<Identifier, DirtyState<Identifier>> bindIdent(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceIdentifier(node1, path).setDirty());
  }

  private F<Statement, DirtyState<Statement>> bindStmt(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceStatement(node1, path).setDirty());
  }

  private F<Block, DirtyState<Block>> bindBlock(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceBlock(node1, path).setDirty());
  }

  private F<VariableDeclaration, DirtyState<VariableDeclaration>> bindVarDeclStmt(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceVariableDeclaration(node1, path).setDirty());
  }

  private F<SwitchCase, DirtyState<SwitchCase>> bindCase(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceSwitchCase(node1, path).setDirty());
  }

  private F<SwitchDefault, DirtyState<SwitchDefault>> bindDefault(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceSwitchDefault(node1, path).setDirty());
  }

  private F<Directive, DirtyState<Directive>> bindDirective(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceDirective(node1, path).setDirty());
  }

  private F<PropertyName, DirtyState<PropertyName>> bindPropName(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reducePropertyName(node1, path).setDirty());
  }

  private F<ObjectProperty, DirtyState<ObjectProperty>> bindProp(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceObjectProperty(node1, path).setDirty());
  }

  private F<Script, DirtyState<Script>> bindProgram(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceScript(node1, path).setDirty());
  }

  private F<FunctionBody, DirtyState<FunctionBody>> bindProgramBody(@NotNull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceFunctionBody(node1, path).setDirty());
  }

  @NotNull
  public Script transform(@NotNull Script script, @NotNull List<Branch> path) {
    return this.d.reduceScript(script, path).node;
  }

  @NotNull
  @Override
  public DirtyState<Script> reduceScript(
      @NotNull Script node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<FunctionBody> body) {
    return super.reduceScript(node, path, body).bind(this.bindProgram(path));
  }

  @NotNull
  @Override
  public DirtyState<Identifier> reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return super.reduceIdentifier(node, path).bind(this.bindIdent(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> name) {
    return super.reduceIdentifierExpression(node, path, name).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path) {
    return super.reduceThisExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralBooleanExpression(
      @NotNull LiteralBooleanExpression node,
      @NotNull List<Branch> path) {
    return super.reduceLiteralBooleanExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralStringExpression(
      @NotNull LiteralStringExpression node,
      @NotNull List<Branch> path) {
    return super.reduceLiteralStringExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralRegExpExpression(
      @NotNull LiteralRegExpExpression node,
      @NotNull List<Branch> path) {
    return super.reduceLiteralRegExpExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node,
      @NotNull List<Branch> path) {
    return super.reduceLiteralNumericExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralNullExpression(
      @NotNull LiteralNullExpression node,
      @NotNull List<Branch> path) {
    return super.reduceLiteralNullExpression(node, path).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> name,
      @NotNull List<DirtyState<Identifier>> parameters,
      @NotNull DirtyState<FunctionBody> body) {
    return super.reduceFunctionExpression(node, path, name, parameters, body).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull DirtyState<Identifier> property) {
    return super.reduceStaticMemberExpression(node, path, object, property).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull DirtyState<Expression> expression) {
    return super.reduceComputedMemberExpression(node, path, object, expression).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<ObjectProperty>> properties) {
    return super.reduceObjectExpression(node, path, properties).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> left,
      @NotNull DirtyState<Expression> right) {
    return super.reduceBinaryExpression(node, path, left, right).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Maybe<DirtyState<Expression>>> elements) {
    return super.reduceArrayExpression(node, path, elements).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> callee,
      @NotNull List<DirtyState<Expression>> arguments) {
    return super.reduceNewExpression(node, path, callee, arguments).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> callee,
      @NotNull List<DirtyState<Expression>> arguments) {
    return super.reduceCallExpression(node, path, callee, arguments).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> operand) {
    return super.reducePostfixExpression(node, path, operand).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> operand) {
    return super.reducePrefixExpression(node, path, operand).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull DirtyState<Expression> consequent,
      @NotNull DirtyState<Expression> alternate) {
    return super.reduceConditionalExpression(node, path, test, consequent, alternate).bind(this.bindExp(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> id,
      @NotNull List<DirtyState<Identifier>> params,
      @NotNull DirtyState<FunctionBody> body) {
    return super.reduceFunctionDeclaration(node, path, id, params, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Directive> reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path) {
    return super.reduceUseStrictDirective(node, path).bind(this.bindDirective(path));
  }

  @NotNull
  @Override
  public DirtyState<Directive> reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path) {
    return super.reduceUnknownDirective(node, path).bind(this.bindDirective(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Block> block) {
    return super.reduceBlockStatement(node, path, block).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> label) {
    return super.reduceBreakStatement(node, path, label).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<CatchClause> reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull final List<Branch> path,
      @NotNull DirtyState<Identifier> param,
      @NotNull DirtyState<Block> body) {
    return super.reduceCatchClause(node, path, param, body).bind(
        node1 -> node1.transform(this.t).onDirty(
            node2 -> this.d.reduceCatchClause(node2, path).setDirty()));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> label) {
    return super.reduceContinueStatement(node, path, label).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path) {
    return super.reduceDebuggerStatement(node, path).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Statement> body,
      @NotNull DirtyState<Expression> test) {
    return super.reduceDoWhileStatement(node, path, body, test).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path) {
    return super.reduceEmptyStatement(node, path).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> expression) {
    return super.reduceExpressionStatement(node, path, expression).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<DirtyState<VariableDeclaration>, DirtyState<Expression>> left,
      @NotNull DirtyState<Expression> right,
      @NotNull DirtyState<Statement> body) {
    return super.reduceForInStatement(node, path, left, right, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<DirtyState<VariableDeclaration>, DirtyState<Expression>>> init,
      @NotNull Maybe<DirtyState<Expression>> test,
      @NotNull Maybe<DirtyState<Expression>> update,
      @NotNull DirtyState<Statement> body) {
    return super.reduceForStatement(node, path, init, test, update, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull DirtyState<Statement> consequent,
      @NotNull Maybe<DirtyState<Statement>> alternate) {
    return super.reduceIfStatement(node, path, test, consequent, alternate).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> label,
      @NotNull DirtyState<Statement> body) {
    return super.reduceLabeledStatement(node, path, label, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Expression>> argument) {
    return super.reduceReturnStatement(node, path, argument).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<SwitchCase> reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull List<DirtyState<Statement>> consequent) {
    return super.reduceSwitchCase(node, path, test, consequent).bind(this.bindCase(path));
  }

  @NotNull
  @Override
  public DirtyState<SwitchDefault> reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Statement>> consequent) {
    return super.reduceSwitchDefault(node, path, consequent).bind(this.bindDefault(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> discriminant,
      @NotNull List<DirtyState<SwitchCase>> cases) {
    return super.reduceSwitchStatement(node, path, discriminant, cases).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> discriminant,
      @NotNull List<DirtyState<SwitchCase>> cases,
      @NotNull DirtyState<SwitchDefault> defaultCase,
      @NotNull List<DirtyState<SwitchCase>> postDefaultCases) {
    return super.reduceSwitchStatementWithDefault(node, path, discriminant, cases, defaultCase, postDefaultCases).bind(
        this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> argument) {
    return super.reduceThrowStatement(node, path, argument).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Block> block,
      @NotNull DirtyState<CatchClause> catchClause) {
    return super.reduceTryCatchStatement(node, path, block, catchClause).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Block> block,
      @NotNull Maybe<DirtyState<CatchClause>> catchClause,
      @NotNull DirtyState<Block> finalizer) {
    return super.reduceTryFinallyStatement(node, path, block, catchClause, finalizer).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<VariableDeclaration> declaration) {
    return super.reduceVariableDeclarationStatement(node, path, declaration).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<VariableDeclaration> reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path,
      @NotNull NonEmptyList<DirtyState<VariableDeclarator>> declarators) {
    return super.reduceVariableDeclaration(node, path, declarators).bind(this.bindVarDeclStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull DirtyState<Statement> body) {
    return super.reduceWhileStatement(node, path, test, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull DirtyState<Statement> body) {
    return super.reduceWithStatement(node, path, object, body).bind(this.bindStmt(path));
  }

  @NotNull
  @Override
  public DirtyState<Block> reduceBlock(
      @NotNull Block node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Statement>> statements) {
    return super.reduceBlock(node, path, statements).bind(this.bindBlock(path));
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<PropertyName> key,
      @NotNull DirtyState<Expression> value) {
    return super.reduceDataProperty(node, path, key, value).bind(this.bindProp(path));
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceGetter(
      @NotNull Getter node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<PropertyName> key,
      @NotNull DirtyState<FunctionBody> body) {
    return super.reduceGetter(node, path, key, body).bind(this.bindProp(path));
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<PropertyName> key,
      @NotNull DirtyState<Identifier> parameter,
      @NotNull DirtyState<FunctionBody> body) {
    return super.reduceSetter(node, path, key, parameter, body).bind(this.bindProp(path));
  }

  @NotNull
  @Override
  public DirtyState<PropertyName> reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
    return super.reducePropertyName(node, path).bind(this.bindPropName(path));
  }

  @NotNull
  @Override
  public DirtyState<FunctionBody> reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Directive>> directives,
      @NotNull List<DirtyState<Statement>> sourceElements) {
    return super.reduceFunctionBody(node, path, directives, sourceElements).bind(this.bindProgramBody(path));
  }

  @NotNull
  @Override
  public DirtyState<VariableDeclarator> reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull final List<Branch> path,
      @NotNull DirtyState<Identifier> id,
      @NotNull Maybe<DirtyState<Expression>> init) {
    return super.reduceVariableDeclarator(node, path, id, init).bind(
        node1 -> node1.transform(this.t).onDirty(
            variableDeclarator -> this.d.reduceVariableDeclarator(variableDeclarator, path).setDirty()));
  }
}
