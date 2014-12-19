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

package com.shapesecurity.shift.js.visitor;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.F;
import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.ArrayExpression;
import com.shapesecurity.shift.js.ast.expression.BinaryExpression;
import com.shapesecurity.shift.js.ast.expression.CallExpression;
import com.shapesecurity.shift.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.js.ast.expression.NewExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PostfixExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ThisExpression;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.Getter;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.BlockStatement;
import com.shapesecurity.shift.js.ast.statement.BreakStatement;
import com.shapesecurity.shift.js.ast.statement.ContinueStatement;
import com.shapesecurity.shift.js.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.js.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.js.ast.statement.EmptyStatement;
import com.shapesecurity.shift.js.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.js.ast.statement.ForInStatement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.IfStatement;
import com.shapesecurity.shift.js.ast.statement.LabeledStatement;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.js.ast.statement.ThrowStatement;
import com.shapesecurity.shift.js.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;
import com.shapesecurity.shift.js.path.Branch;

public class FixPointTransformer extends LazyCloner {
  @Nonnull
  public final TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>>
      t;

  private final Director<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>> d;
  public FixPointTransformer(@Nonnull TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>> t) {
    super();
    this.t = t;
    this.d = new Director<>(this);
  }

  private F<Expression, DirtyState<Expression>> bindExp(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceExpression(node1, path).setDirty());
  }

  private F<Identifier, DirtyState<Identifier>> bindIdent(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceIdentifier(node1, path).setDirty());
  }

  private F<Statement, DirtyState<Statement>> bindStmt(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceStatement(node1, path).setDirty());
  }

  private F<Block, DirtyState<Block>> bindBlock(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceBlock(node1, path).setDirty());
  }

  private F<VariableDeclaration, DirtyState<VariableDeclaration>> bindVarDeclStmt(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceVariableDeclaration(node1, path).setDirty());
  }

  private F<SwitchCase, DirtyState<SwitchCase>> bindCase(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceSwitchCase(node1, path).setDirty());
  }

  private F<SwitchDefault, DirtyState<SwitchDefault>> bindDefault(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceSwitchDefault(node1, path).setDirty());
  }

  private F<Directive, DirtyState<Directive>> bindDirective(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceDirective(node1, path).setDirty());
  }

  private F<PropertyName, DirtyState<PropertyName>> bindPropName(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reducePropertyName(node1, path).setDirty());
  }

  private F<ObjectProperty, DirtyState<ObjectProperty>> bindProp(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceObjectProperty(node1, path).setDirty());
  }

  private F<Script, DirtyState<Script>> bindProgram(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceScript(node1, path).setDirty());
  }

  private F<FunctionBody, DirtyState<FunctionBody>> bindProgramBody(@Nonnull final List<Branch> path) {
    return node -> node.transform(this.t).onDirty(node1 -> this.d.reduceFunctionBody(node1, path).setDirty());
  }

  @Nonnull
  public Script transform(@Nonnull Script script, @Nonnull List<Branch> path) {
    return this.d.reduceScript(script, path).node;
  }

  @Nonnull
  @Override
  public DirtyState<Script> reduceScript(
      @Nonnull Script node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<FunctionBody> body) {
    return super.reduceScript(node, path, body).bind(this.bindProgram(path));
  }

  @Nonnull
  @Override
  public DirtyState<Identifier> reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return super.reduceIdentifier(node, path).bind(this.bindIdent(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> name) {
    return super.reduceIdentifierExpression(node, path, name).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return super.reduceThisExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralBooleanExpression(
      @Nonnull LiteralBooleanExpression node,
      @Nonnull List<Branch> path) {
    return super.reduceLiteralBooleanExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralStringExpression(
      @Nonnull LiteralStringExpression node,
      @Nonnull List<Branch> path) {
    return super.reduceLiteralStringExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralRegExpExpression(
      @Nonnull LiteralRegExpExpression node,
      @Nonnull List<Branch> path) {
    return super.reduceLiteralRegExpExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralNumericExpression(
      @Nonnull LiteralNumericExpression node,
      @Nonnull List<Branch> path) {
    return super.reduceLiteralNumericExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralNullExpression(
      @Nonnull LiteralNullExpression node,
      @Nonnull List<Branch> path) {
    return super.reduceLiteralNullExpression(node, path).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> name,
      @Nonnull List<DirtyState<Identifier>> parameters,
      @Nonnull DirtyState<FunctionBody> body) {
    return super.reduceFunctionExpression(node, path, name, parameters, body).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull DirtyState<Identifier> property) {
    return super.reduceStaticMemberExpression(node, path, object, property).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull DirtyState<Expression> expression) {
    return super.reduceComputedMemberExpression(node, path, object, expression).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<ObjectProperty>> properties) {
    return super.reduceObjectExpression(node, path, properties).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> left,
      @Nonnull DirtyState<Expression> right) {
    return super.reduceBinaryExpression(node, path, left, right).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<DirtyState<Expression>>> elements) {
    return super.reduceArrayExpression(node, path, elements).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> callee,
      @Nonnull List<DirtyState<Expression>> arguments) {
    return super.reduceNewExpression(node, path, callee, arguments).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> callee,
      @Nonnull List<DirtyState<Expression>> arguments) {
    return super.reduceCallExpression(node, path, callee, arguments).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> operand) {
    return super.reducePostfixExpression(node, path, operand).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> operand) {
    return super.reducePrefixExpression(node, path, operand).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull DirtyState<Expression> consequent,
      @Nonnull DirtyState<Expression> alternate) {
    return super.reduceConditionalExpression(node, path, test, consequent, alternate).bind(this.bindExp(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> id,
      @Nonnull List<DirtyState<Identifier>> params,
      @Nonnull DirtyState<FunctionBody> body) {
    return super.reduceFunctionDeclaration(node, path, id, params, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Directive> reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return super.reduceUseStrictDirective(node, path).bind(this.bindDirective(path));
  }

  @Nonnull
  @Override
  public DirtyState<Directive> reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return super.reduceUnknownDirective(node, path).bind(this.bindDirective(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceBlockStatement(
      @Nonnull BlockStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Block> block) {
    return super.reduceBlockStatement(node, path, block).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> label) {
    return super.reduceBreakStatement(node, path, label).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<CatchClause> reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull final List<Branch> path,
      @Nonnull DirtyState<Identifier> param,
      @Nonnull DirtyState<Block> body) {
    return super.reduceCatchClause(node, path, param, body).bind(
        node1 -> node1.transform(this.t).onDirty(
            node2 -> this.d.reduceCatchClause(node2, path).setDirty()));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> label) {
    return super.reduceContinueStatement(node, path, label).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return super.reduceDebuggerStatement(node, path).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Statement> body,
      @Nonnull DirtyState<Expression> test) {
    return super.reduceDoWhileStatement(node, path, body, test).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return super.reduceEmptyStatement(node, path).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceExpressionStatement(
      @Nonnull ExpressionStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> expression) {
    return super.reduceExpressionStatement(node, path, expression).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<DirtyState<VariableDeclaration>, DirtyState<Expression>> left,
      @Nonnull DirtyState<Expression> right,
      @Nonnull DirtyState<Statement> body) {
    return super.reduceForInStatement(node, path, left, right, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<DirtyState<VariableDeclaration>, DirtyState<Expression>>> init,
      @Nonnull Maybe<DirtyState<Expression>> test,
      @Nonnull Maybe<DirtyState<Expression>> update,
      @Nonnull DirtyState<Statement> body) {
    return super.reduceForStatement(node, path, init, test, update, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull DirtyState<Statement> consequent,
      @Nonnull Maybe<DirtyState<Statement>> alternate) {
    return super.reduceIfStatement(node, path, test, consequent, alternate).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> label,
      @Nonnull DirtyState<Statement> body) {
    return super.reduceLabeledStatement(node, path, label, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Expression>> argument) {
    return super.reduceReturnStatement(node, path, argument).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchCase> reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull List<DirtyState<Statement>> consequent) {
    return super.reduceSwitchCase(node, path, test, consequent).bind(this.bindCase(path));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchDefault> reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Statement>> consequent) {
    return super.reduceSwitchDefault(node, path, consequent).bind(this.bindDefault(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> discriminant,
      @Nonnull List<DirtyState<SwitchCase>> cases) {
    return super.reduceSwitchStatement(node, path, discriminant, cases).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> discriminant,
      @Nonnull List<DirtyState<SwitchCase>> cases,
      @Nonnull DirtyState<SwitchDefault> defaultCase,
      @Nonnull List<DirtyState<SwitchCase>> postDefaultCases) {
    return super.reduceSwitchStatementWithDefault(node, path, discriminant, cases, defaultCase, postDefaultCases).bind(
        this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceThrowStatement(
      @Nonnull ThrowStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> argument) {
    return super.reduceThrowStatement(node, path, argument).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Block> block,
      @Nonnull DirtyState<CatchClause> catchClause) {
    return super.reduceTryCatchStatement(node, path, block, catchClause).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Block> block,
      @Nonnull Maybe<DirtyState<CatchClause>> catchClause,
      @Nonnull DirtyState<Block> finalizer) {
    return super.reduceTryFinallyStatement(node, path, block, catchClause, finalizer).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<VariableDeclaration> declaration) {
    return super.reduceVariableDeclarationStatement(node, path, declaration).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclaration> reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<DirtyState<VariableDeclarator>> declarators) {
    return super.reduceVariableDeclaration(node, path, declarators).bind(this.bindVarDeclStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull DirtyState<Statement> body) {
    return super.reduceWhileStatement(node, path, test, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull DirtyState<Statement> body) {
    return super.reduceWithStatement(node, path, object, body).bind(this.bindStmt(path));
  }

  @Nonnull
  @Override
  public DirtyState<Block> reduceBlock(
      @Nonnull Block node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Statement>> statements) {
    return super.reduceBlock(node, path, statements).bind(this.bindBlock(path));
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<PropertyName> key,
      @Nonnull DirtyState<Expression> value) {
    return super.reduceDataProperty(node, path, key, value).bind(this.bindProp(path));
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceGetter(
      @Nonnull Getter node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<PropertyName> key,
      @Nonnull DirtyState<FunctionBody> body) {
    return super.reduceGetter(node, path, key, body).bind(this.bindProp(path));
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<PropertyName> key,
      @Nonnull DirtyState<Identifier> parameter,
      @Nonnull DirtyState<FunctionBody> body) {
    return super.reduceSetter(node, path, key, parameter, body).bind(this.bindProp(path));
  }

  @Nonnull
  @Override
  public DirtyState<PropertyName> reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    return super.reducePropertyName(node, path).bind(this.bindPropName(path));
  }

  @Nonnull
  @Override
  public DirtyState<FunctionBody> reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Directive>> directives,
      @Nonnull List<DirtyState<Statement>> sourceElements) {
    return super.reduceFunctionBody(node, path, directives, sourceElements).bind(this.bindProgramBody(path));
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclarator> reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull final List<Branch> path,
      @Nonnull DirtyState<Identifier> id,
      @Nonnull Maybe<DirtyState<Expression>> init) {
    return super.reduceVariableDeclarator(node, path, id, init).bind(
        node1 -> node1.transform(this.t).onDirty(
            variableDeclarator -> this.d.reduceVariableDeclarator(variableDeclarator, path).setDirty()));
  }
}
