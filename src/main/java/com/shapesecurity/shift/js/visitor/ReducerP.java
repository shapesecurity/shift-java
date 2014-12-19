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

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.ArrayExpression;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
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

import javax.annotation.Nonnull;

public interface ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> {
  @Nonnull
  ScriptState reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull ProgramBodyState body);

  @Nonnull
  IdentifierState reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull IdentifierState name);

  @Nonnull
  ExpressionState reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull List<Branch> path);

  @Nonnull
  ExpressionState reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<IdentifierState> name,
      @Nonnull List<IdentifierState> parameters,
      @Nonnull ProgramBodyState body);

  @Nonnull
  ExpressionState reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState object,
      @Nonnull IdentifierState property);

  @Nonnull
  ExpressionState reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState object,
      @Nonnull ExpressionState expression);

  @Nonnull
  ExpressionState reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<PropertyState> properties);

  @Nonnull
  ExpressionState reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState left,
      @Nonnull ExpressionState right);

  @Nonnull
  ExpressionState reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState binding,
      @Nonnull ExpressionState expression);

  @Nonnull
  ExpressionState reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<ExpressionState>> elements);

  @Nonnull
  ExpressionState reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState callee,
      @Nonnull List<ExpressionState> arguments);

  @Nonnull
  ExpressionState reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState callee,
      @Nonnull List<ExpressionState> arguments);

  @Nonnull
  ExpressionState reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState operand);

  @Nonnull
  ExpressionState reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState operand);

  @Nonnull
  ExpressionState reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState test,
      @Nonnull ExpressionState consequent,
      @Nonnull ExpressionState alternate);

  @Nonnull
  StatementState reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull IdentifierState id,
      @Nonnull List<IdentifierState> params,
      @Nonnull ProgramBodyState body);

  @Nonnull
  DirectiveState reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path);

  @Nonnull
  DirectiveState reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path);

  @Nonnull
  StatementState reduceBlockStatement(
      @Nonnull BlockStatement node,
      @Nonnull List<Branch> path,
      @Nonnull BlockState block);

  @Nonnull
  StatementState reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<IdentifierState> label);

  @Nonnull
  CatchClauseState reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull IdentifierState param,
      @Nonnull BlockState body);

  @Nonnull
  StatementState reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<IdentifierState> label);

  @Nonnull
  StatementState reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path);

  @Nonnull
  StatementState reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull StatementState body,
      @Nonnull ExpressionState test);

  @Nonnull
  StatementState reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path);

  @Nonnull
  StatementState reduceExpressionStatement(
      @Nonnull ExpressionStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState expression);

  @Nonnull
  StatementState reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<DeclarationState, ExpressionState> left,
      @Nonnull ExpressionState right,
      @Nonnull StatementState body);

  @Nonnull
  StatementState reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<DeclarationState, ExpressionState>> init,
      @Nonnull Maybe<ExpressionState> test,
      @Nonnull Maybe<ExpressionState> update,
      @Nonnull StatementState body);

  @Nonnull
  StatementState reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState test,
      @Nonnull StatementState consequent,
      @Nonnull Maybe<StatementState> alternate);

  @Nonnull
  StatementState reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull IdentifierState label,
      @Nonnull StatementState body);

  @Nonnull
  StatementState reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<ExpressionState> argument);

  @Nonnull
  SwitchCaseState reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState test,
      @Nonnull List<StatementState> consequent);

  @Nonnull
  SwitchDefaultState reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<StatementState> consequent);

  @Nonnull
  StatementState reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState discriminant,
      @Nonnull List<SwitchCaseState> cases);

  @Nonnull
  StatementState reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState discriminant,
      @Nonnull List<SwitchCaseState> cases,
      @Nonnull SwitchDefaultState defaultCase,
      @Nonnull List<SwitchCaseState> postDefaultCases);

  @Nonnull
  StatementState reduceThrowStatement(
      @Nonnull ThrowStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState argument);

  @Nonnull
  StatementState reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull BlockState block,
      @Nonnull CatchClauseState catchClause);

  @Nonnull
  StatementState reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull BlockState block,
      @Nonnull Maybe<CatchClauseState> catchClause,
      @Nonnull BlockState finalizer);

  @Nonnull
  StatementState reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DeclarationState declaration);

  @Nonnull
  DeclarationState reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<DeclaratorState> declarators);

  @Nonnull
  StatementState reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState test,
      @Nonnull StatementState body);

  @Nonnull
  StatementState reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ExpressionState object,
      @Nonnull StatementState body);

  @Nonnull
  PropertyState reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull PropertyNameState key,
      @Nonnull ExpressionState value);

  @Nonnull
  PropertyState reduceGetter(
      @Nonnull Getter node,
      @Nonnull List<Branch> path,
      @Nonnull PropertyNameState key,
      @Nonnull ProgramBodyState body);

  @Nonnull
  PropertyState reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull PropertyNameState key,
      @Nonnull IdentifierState parameter,
      @Nonnull ProgramBodyState body);

  @Nonnull
  PropertyNameState reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path);

  @Nonnull
  ProgramBodyState reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirectiveState> directives,
      @Nonnull List<StatementState> sourceElements);

  @Nonnull
  DeclaratorState reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull IdentifierState id,
      @Nonnull Maybe<ExpressionState> init);

  @Nonnull
  BlockState reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path, @Nonnull List<StatementState> statements);
}
