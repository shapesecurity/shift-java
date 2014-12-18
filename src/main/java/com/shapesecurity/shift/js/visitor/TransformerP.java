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

import javax.annotation.Nonnull;

public interface TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> {
  @Nonnull
  CatchClauseState transform(@Nonnull CatchClause node);

  @Nonnull
  DeclaratorState transform(@Nonnull VariableDeclarator node);

  @Nonnull
  DirectiveState transform(@Nonnull UnknownDirective node);

  @Nonnull
  DirectiveState transform(@Nonnull UseStrictDirective node);

  @Nonnull
  ExpressionState transform(@Nonnull ArrayExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull BinaryExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull AssignmentExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull CallExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull ComputedMemberExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull ConditionalExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull FunctionExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull IdentifierExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull LiteralBooleanExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull LiteralNullExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull LiteralNumericExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull LiteralRegExpExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull LiteralStringExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull NewExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull ObjectExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull PostfixExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull PrefixExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull StaticMemberExpression node);

  @Nonnull
  ExpressionState transform(@Nonnull ThisExpression node);

  @Nonnull
  IdentifierState transform(@Nonnull Identifier node);

  @Nonnull
  ProgramBodyState transform(@Nonnull FunctionBody node);

  @Nonnull
  ScriptState transform(@Nonnull Script node);

  @Nonnull
  PropertyNameState transform(@Nonnull PropertyName node);

  @Nonnull
  PropertyState transform(@Nonnull Getter node);

  @Nonnull
  PropertyState transform(@Nonnull DataProperty node);

  @Nonnull
  PropertyState transform(@Nonnull Setter node);

  @Nonnull
  StatementState transform(@Nonnull BlockStatement node);

  @Nonnull
  StatementState transform(@Nonnull BreakStatement node);

  @Nonnull
  StatementState transform(@Nonnull ContinueStatement node);

  @Nonnull
  StatementState transform(@Nonnull DebuggerStatement node);

  @Nonnull
  StatementState transform(@Nonnull DoWhileStatement node);

  @Nonnull
  StatementState transform(@Nonnull EmptyStatement node);

  @Nonnull
  StatementState transform(@Nonnull ExpressionStatement node);

  @Nonnull
  StatementState transform(@Nonnull ForInStatement node);

  @Nonnull
  StatementState transform(@Nonnull ForStatement node);

  @Nonnull
  StatementState transform(@Nonnull FunctionDeclaration node);

  @Nonnull
  StatementState transform(@Nonnull IfStatement node);

  @Nonnull
  StatementState transform(@Nonnull LabeledStatement node);

  @Nonnull
  StatementState transform(@Nonnull ReturnStatement node);

  @Nonnull
  StatementState transform(@Nonnull SwitchStatement node);

  @Nonnull
  StatementState transform(@Nonnull SwitchStatementWithDefault node);

  @Nonnull
  StatementState transform(@Nonnull ThrowStatement node);

  @Nonnull
  StatementState transform(@Nonnull TryCatchStatement node);

  @Nonnull
  StatementState transform(@Nonnull TryFinallyStatement node);

  @Nonnull
  StatementState transform(@Nonnull VariableDeclarationStatement node);

  @Nonnull
  BlockState transform(@Nonnull Block node);

  @Nonnull
  DeclarationState transform(@Nonnull VariableDeclaration node);

  @Nonnull
  StatementState transform(@Nonnull WhileStatement node);

  @Nonnull
  StatementState transform(@Nonnull WithStatement node);

  @Nonnull
  SwitchCaseState transform(@Nonnull SwitchCase node);

  @Nonnull
  SwitchDefaultState transform(@Nonnull SwitchDefault node);
}
