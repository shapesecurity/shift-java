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

package com.shapesecurity.shift;

import org.jetbrains.annotations.NotNull;

import com.shapesecurity.functional.Unit;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.directive.UnknownDirective;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.expression.ArrayExpression;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.BinaryExpression;
import com.shapesecurity.shift.ast.expression.CallExpression;
import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.ast.expression.LiteralInfinityExpression;
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
import com.shapesecurity.shift.visitor.Reducer;

public abstract class TestReducerWithPath implements Reducer<Unit> {

  protected abstract void accept(@NotNull Node node, @NotNull List<Branch> path);

  @NotNull
  @Override
  public final Unit reduceScript(@NotNull Script node, @NotNull List<Branch> path, @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit identifier) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Unit> name,
      @NotNull List<Unit> parameters,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit object,
      @NotNull Unit property) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit object,
      @NotNull Unit expression) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Unit> properties) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit left,
      @NotNull Unit right) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit binding,
      @NotNull Unit expression) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Maybe<Unit>> elements) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit callee,
      @NotNull List<Unit> arguments) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit callee,
      @NotNull List<Unit> arguments) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit operand) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit operand) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
      @NotNull Unit test,
      @NotNull Unit consequent,
      @NotNull Unit alternate) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull Unit name,
      @NotNull List<Unit> params,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit block) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Unit> label) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull Unit binding,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Unit> label) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit body,
      @NotNull Unit test) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit expression) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<Unit, Unit> left,
      @NotNull Unit right,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<Unit, Unit>> init,
      @NotNull Maybe<Unit> test,
      @NotNull Maybe<Unit> update,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit test,
      @NotNull Unit consequent,
      @NotNull Maybe<Unit> alternate) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit label,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Unit> expression) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull List<Branch> path,
      @NotNull Unit test,
      @NotNull List<Unit> consequent) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path,
      @NotNull List<Unit> consequent) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit discriminant,
      @NotNull List<Unit> cases) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull Unit discriminant,
      @NotNull List<Unit> preDefaultCases,
      @NotNull Unit defaultCase,
      @NotNull List<Unit> postDefaultCases) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit expression) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit block,
      @NotNull Unit catchClause) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit block,
      @NotNull Maybe<Unit> catchClause,
      @NotNull Unit finalizer) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit declaration) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path,
      @NotNull NonEmptyList<Unit> declarators) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit test,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull Unit object,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull List<Branch> path,
      @NotNull Unit name,
      @NotNull Unit value) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceGetter(
      @NotNull Getter node,
      @NotNull List<Branch> path,
      @NotNull Unit name,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull Unit name,
      @NotNull Unit parameter,
      @NotNull Unit body) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<Unit> directives,
      @NotNull List<Unit> statements) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull Unit binding,
      @NotNull Maybe<Unit> init) {
    accept(node, path);
    return Unit.unit;
  }

  @NotNull
  @Override
  public final Unit reduceBlock(@NotNull Block node, @NotNull List<Branch> path, @NotNull List<Unit> statements) {
    accept(node, path);
    return Unit.unit;
  }
}
