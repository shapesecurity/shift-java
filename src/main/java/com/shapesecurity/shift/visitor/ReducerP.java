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

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
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

import org.jetbrains.annotations.NotNull;

public interface ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> {
  @NotNull
  ScriptState reduceScript(@NotNull Script node, @NotNull List<Branch> path, @NotNull ProgramBodyState body);

  @NotNull
  IdentifierState reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull IdentifierState identifier);

  @NotNull
  ExpressionState reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull List<Branch> path);

  @NotNull
  ExpressionState reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<IdentifierState> name,
      @NotNull List<IdentifierState> parameters,
      @NotNull ProgramBodyState body);

  @NotNull
  ExpressionState reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState object,
      @NotNull IdentifierState property);

  @NotNull
  ExpressionState reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState object,
      @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull List<Branch> path,
      @NotNull List<PropertyState> properties);

  @NotNull
  ExpressionState reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState left,
      @NotNull ExpressionState right);

  @NotNull
  ExpressionState reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState binding,
      @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Maybe<ExpressionState>> elements);

  @NotNull
  ExpressionState reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState callee,
      @NotNull List<ExpressionState> arguments);

  @NotNull
  ExpressionState reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState callee,
      @NotNull List<ExpressionState> arguments);

  @NotNull
  ExpressionState reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState operand);

  @NotNull
  ExpressionState reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState operand);

  @NotNull
  ExpressionState reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState test,
      @NotNull ExpressionState consequent,
      @NotNull ExpressionState alternate);

  @NotNull
  StatementState reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull IdentifierState name,
      @NotNull List<IdentifierState> params,
      @NotNull ProgramBodyState body);

  @NotNull
  DirectiveState reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path);

  @NotNull
  DirectiveState reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path);

  @NotNull
  StatementState reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull List<Branch> path,
      @NotNull BlockState block);

  @NotNull
  StatementState reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<IdentifierState> label);

  @NotNull
  CatchClauseState reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull IdentifierState binding,
      @NotNull BlockState body);

  @NotNull
  StatementState reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<IdentifierState> label);

  @NotNull
  StatementState reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path);

  @NotNull
  StatementState reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull StatementState body,
      @NotNull ExpressionState test);

  @NotNull
  StatementState reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path);

  @NotNull
  StatementState reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState expression);

  @NotNull
  StatementState reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<DeclarationState, ExpressionState> left,
      @NotNull ExpressionState right,
      @NotNull StatementState body);

  @NotNull
  StatementState reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<DeclarationState, ExpressionState>> init,
      @NotNull Maybe<ExpressionState> test,
      @NotNull Maybe<ExpressionState> update,
      @NotNull StatementState body);

  @NotNull
  StatementState reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState test,
      @NotNull StatementState consequent,
      @NotNull Maybe<StatementState> alternate);

  @NotNull
  StatementState reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull List<Branch> path,
      @NotNull IdentifierState label,
      @NotNull StatementState body);

  @NotNull
  StatementState reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<ExpressionState> expression);

  @NotNull
  SwitchCaseState reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState test,
      @NotNull List<StatementState> consequent);

  @NotNull
  SwitchDefaultState reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path,
      @NotNull List<StatementState> consequent);

  @NotNull
  StatementState reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState discriminant,
      @NotNull List<SwitchCaseState> cases);

  @NotNull
  StatementState reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState discriminant,
      @NotNull List<SwitchCaseState> preDefaultCases,
      @NotNull SwitchDefaultState defaultCase,
      @NotNull List<SwitchCaseState> postDefaultCases);

  @NotNull
  StatementState reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState expression);

  @NotNull
  StatementState reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull BlockState block,
      @NotNull CatchClauseState catchClause);

  @NotNull
  StatementState reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull BlockState block,
      @NotNull Maybe<CatchClauseState> catchClause,
      @NotNull BlockState finalizer);

  @NotNull
  StatementState reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull List<Branch> path,
      @NotNull DeclarationState declaration);

  @NotNull
  DeclarationState reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path,
      @NotNull NonEmptyList<DeclaratorState> declarators);

  @NotNull
  StatementState reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState test,
      @NotNull StatementState body);

  @NotNull
  StatementState reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull ExpressionState object,
      @NotNull StatementState body);

  @NotNull
  PropertyState reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull List<Branch> path,
      @NotNull PropertyNameState name,
      @NotNull ExpressionState value);

  @NotNull
  PropertyState reduceGetter(
      @NotNull Getter node,
      @NotNull List<Branch> path,
      @NotNull PropertyNameState name,
      @NotNull ProgramBodyState body);

  @NotNull
  PropertyState reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull PropertyNameState name,
      @NotNull IdentifierState parameter,
      @NotNull ProgramBodyState body);

  @NotNull
  PropertyNameState reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path);

  @NotNull
  ProgramBodyState reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<DirectiveState> directives,
      @NotNull List<StatementState> statements);

  @NotNull
  DeclaratorState reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull IdentifierState binding,
      @NotNull Maybe<ExpressionState> init);

  @NotNull
  BlockState reduceBlock(@NotNull Block node, @NotNull List<Branch> path, @NotNull List<StatementState> statements);
}
