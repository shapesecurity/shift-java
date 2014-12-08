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

package com.shapesecurity.laserbat.js.minifier;

import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.CatchClause;
import com.shapesecurity.laserbat.js.ast.Directive;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.SwitchCase;
import com.shapesecurity.laserbat.js.ast.SwitchDefault;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.ast.VariableDeclarator;
import com.shapesecurity.laserbat.js.ast.directive.UnknownDirective;
import com.shapesecurity.laserbat.js.ast.directive.UseStrictDirective;
import com.shapesecurity.laserbat.js.ast.expression.ArrayExpression;
import com.shapesecurity.laserbat.js.ast.expression.AssignmentExpression;
import com.shapesecurity.laserbat.js.ast.expression.BinaryExpression;
import com.shapesecurity.laserbat.js.ast.expression.CallExpression;
import com.shapesecurity.laserbat.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ConditionalExpression;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.laserbat.js.ast.expression.NewExpression;
import com.shapesecurity.laserbat.js.ast.expression.ObjectExpression;
import com.shapesecurity.laserbat.js.ast.expression.PostfixExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ThisExpression;
import com.shapesecurity.laserbat.js.ast.property.DataProperty;
import com.shapesecurity.laserbat.js.ast.property.Getter;
import com.shapesecurity.laserbat.js.ast.property.ObjectProperty;
import com.shapesecurity.laserbat.js.ast.property.PropertyName;
import com.shapesecurity.laserbat.js.ast.property.Setter;
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.ast.statement.BreakStatement;
import com.shapesecurity.laserbat.js.ast.statement.ContinueStatement;
import com.shapesecurity.laserbat.js.ast.statement.DebuggerStatement;
import com.shapesecurity.laserbat.js.ast.statement.DoWhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.EmptyStatement;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForInStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForStatement;
import com.shapesecurity.laserbat.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.laserbat.js.ast.statement.IfStatement;
import com.shapesecurity.laserbat.js.ast.statement.LabeledStatement;
import com.shapesecurity.laserbat.js.ast.statement.ReturnStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.laserbat.js.ast.statement.ThrowStatement;
import com.shapesecurity.laserbat.js.ast.statement.TryCatchStatement;
import com.shapesecurity.laserbat.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.laserbat.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.visitor.DirtyState;
import com.shapesecurity.laserbat.js.visitor.TransformerP;

import javax.annotation.Nonnull;

public class MinificationRule
    implements TransformerP<DirtyState<Script>, DirtyState<FunctionBody>, DirtyState<ObjectProperty>, DirtyState<PropertyName>, DirtyState<Identifier>, DirtyState<Expression>, DirtyState<Directive>, DirtyState<Statement>, DirtyState<Block>, DirtyState<VariableDeclarator>, DirtyState<VariableDeclaration>, DirtyState<SwitchCase>, DirtyState<SwitchDefault>, DirtyState<CatchClause>> {
  @Nonnull
  @Override
  public DirtyState<CatchClause> transform(@Nonnull CatchClause node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclarator> transform(@Nonnull VariableDeclarator node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Directive> transform(@Nonnull UnknownDirective node) {
    return DirtyState.<Directive>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Directive> transform(@Nonnull UseStrictDirective node) {
    return DirtyState.<Directive>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull ArrayExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull BinaryExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull AssignmentExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull CallExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull ComputedMemberExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull ConditionalExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull FunctionExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull IdentifierExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull LiteralBooleanExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull LiteralNullExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull LiteralNumericExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull LiteralRegExpExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull LiteralStringExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull NewExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull ObjectExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull PostfixExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull PrefixExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull StaticMemberExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull ThisExpression node) {
    return DirtyState.<Expression>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Identifier> transform(@Nonnull Identifier node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Script> transform(@Nonnull Script node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<PropertyName> transform(@Nonnull PropertyName node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> transform(@Nonnull Getter node) {
    return DirtyState.<ObjectProperty>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> transform(@Nonnull DataProperty node) {
    return DirtyState.<ObjectProperty>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> transform(@Nonnull Setter node) {
    return DirtyState.<ObjectProperty>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull BlockStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull BreakStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ContinueStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull DebuggerStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull DoWhileStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull EmptyStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ExpressionStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ForInStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ForStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull FunctionDeclaration node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull IfStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull LabeledStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ReturnStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull SwitchStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull SwitchStatementWithDefault node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ThrowStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull TryCatchStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull TryFinallyStatement node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull VariableDeclarationStatement node) {
    return DirtyState.clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Block> transform(@Nonnull Block node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclaration> transform(@Nonnull VariableDeclaration node) {
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull WhileStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull WithStatement node) {
    return DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
    return DirtyState.<SwitchCase>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
    return DirtyState.<SwitchDefault>clean(node);
  }
}
