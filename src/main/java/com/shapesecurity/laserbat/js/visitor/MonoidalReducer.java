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

package com.shapesecurity.laserbat.js.visitor;

import com.shapesecurity.laserbat.functional.data.Either;
import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.Monoid;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.CatchClause;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Script;
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
import com.shapesecurity.laserbat.js.path.Branch;

import javax.annotation.Nonnull;

public class MonoidalReducer<State, Def extends Monoid<State>> implements Reducer<State> {
  @Nonnull
  protected final Def monoidClass;
  private final State identity;

  protected MonoidalReducer(@Nonnull Def monoidClass) {
    this.monoidClass = monoidClass;
    this.identity = this.monoidClass.identity();
  }

  @Nonnull
  @Override
  public State reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull State body) {
    return body;
  }

  @Nonnull
  @Override
  public State reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State name) {
    return name;
  }

  @Nonnull
  @Override
  public State reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceLiteralRegexExpression(@Nonnull LiteralRegExpExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> id,
      @Nonnull List<State> params,
      @Nonnull State body) {
    return append(fold1(params, o(id)), body);
  }

  private State append(State a, State b) {
    return this.monoidClass.append(a, b);
  }

  private State fold1(List<State> as, State a) {
    return as.foldLeft(this::append, a);
  }

  @Nonnull
  private State o(@Nonnull Maybe<State> s) {
    return s.orJust(this.identity);
  }

  @Nonnull
  @Override
  public State reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State property) {
    return append(object, property);
  }

  @Nonnull
  @Override
  public State reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State expression) {
    return append(object, expression);
  }

  @Nonnull
  @Override
  public State reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> properties) {
    return fold(properties);
  }

  private State fold(List<State> as) {
    return as.foldLeft(this::append, this.identity);
  }

  @Nonnull
  @Override
  public State reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State left,
      @Nonnull State right) {
    return append(left, right);
  }

  @Nonnull
  @Override
  public State reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State binding,
      @Nonnull State expression) {
    return append(binding, expression);
  }

  @Nonnull
  @Override
  public State reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<State>> elements) {
    return fold(Maybe.catMaybes(elements));
  }

  @Nonnull
  @Override
  public State reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State callee,
      @Nonnull List<State> arguments) {
    return fold1(arguments, callee);
  }

  @Nonnull
  @Override
  public State reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State callee,
      @Nonnull List<State> arguments) {
    return fold1(arguments, callee);
  }

  @Nonnull
  @Override
  public State reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    return operand;
  }

  @Nonnull
  @Override
  public State reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    return operand;
  }

  @Nonnull
  @Override
  public State reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State consequent,
      @Nonnull State alternate) {
    return append(test, consequent, alternate);
  }

  private State append(State a, State b, State c) {
    return append(append(a, b), c);
  }

  @Nonnull
  @Override
  public State reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull List<State> params,
      @Nonnull State body) {
    return append(fold1(params, id), body);
  }

  @Nonnull
  @Override
  public State reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceBlockStatement(@Nonnull BlockStatement node, @Nonnull List<Branch> path, @Nonnull State block) {
    return block;
  }

  @Nonnull
  @Override
  public State reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> label) {
    return o(label);
  }

  @Nonnull
  @Override
  public State reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull State param,
      @Nonnull State body) {
    return append(param, body);
  }

  @Nonnull
  @Override
  public State reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> label) {
    return o(label);
  }

  @Nonnull
  @Override
  public State reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State body,
      @Nonnull State test) {
    return append(body, test);
  }

  @Nonnull
  @Override
  public State reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceExpressionStatement(
      @Nonnull ExpressionStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State expression) {
    return expression;
  }

  @Nonnull
  @Override
  public State reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<State, State> left,
      @Nonnull State right,
      @Nonnull State body) {
    return append(Either.extract(left), right, body);
  }

  @Nonnull
  @Override
  public State reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<State, State>> init,
      @Nonnull Maybe<State> test,
      @Nonnull Maybe<State> update,
      @Nonnull State body) {
    return append(o(init.map(Either::extract)), o(test), o(update), body);
  }

  private State append(State a, State b, State c, State d) {
    return append(append(a, b, c), d);
  }

  @Nonnull
  @Override
  public State reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State consequent,
      @Nonnull Maybe<State> alternate) {
    return append(test, consequent, o(alternate));
  }

  @Nonnull
  @Override
  public State reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State label,
      @Nonnull State body) {
    return append(label, body);
  }

  @Nonnull
  @Override
  public State reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> argument) {
    return o(argument);
  }

  @Nonnull
  @Override
  public State reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull List<State> consequent) {
    return fold1(consequent, test);
  }

  @Nonnull
  @Override
  public State reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> consequent) {
    return fold(consequent);
  }

  @Nonnull
  @Override
  public State reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State discriminant,
      @Nonnull List<State> cases) {
    return fold1(cases, discriminant);
  }

  @Nonnull
  @Override
  public State reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull State discriminant,
      @Nonnull List<State> cases,
      @Nonnull State defaultCase,
      @Nonnull List<State> postDefaultCases) {
    return append(discriminant, fold(cases), defaultCase, fold(postDefaultCases));
  }

  @Nonnull
  @Override
  public State reduceThrowStatement(@Nonnull ThrowStatement node, @Nonnull List<Branch> path, @Nonnull State argument) {
    return argument;
  }

  @Nonnull
  @Override
  public State reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State block,
      @Nonnull State catchClause) {
    return append(block, catchClause);
  }

  @Nonnull
  @Override
  public State reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State block,
      @Nonnull Maybe<State> catchClause,
      @Nonnull State finalizer) {
    return append(block, o(catchClause), finalizer);
  }

  @Nonnull
  @Override
  public State reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State declaration) {
    return declaration;
  }

  @Nonnull
  @Override
  public State reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<State> declarators) {
    return fold(declarators);
  }

  @Nonnull
  @Override
  public State reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State body) {
    return append(test, body);
  }

  @Nonnull
  @Override
  public State reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State body) {
    return append(object, body);
  }

  @Nonnull
  @Override
  public State reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull State key,
      @Nonnull State value) {
    return append(key, value);
  }

  @Nonnull
  @Override
  public State reduceGetter(@Nonnull Getter node, @Nonnull List<Branch> path, @Nonnull State key, @Nonnull State body) {
    return append(key, body);
  }

  @Nonnull
  @Override
  public State reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull State key,
      @Nonnull State parameter,
      @Nonnull State body) {
    return append(key, parameter, body);
  }

  @Nonnull
  @Override
  public State reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    return this.identity;
  }

  @Nonnull
  @Override
  public State reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> directives,
      @Nonnull List<State> sourceElements) {
    return append(fold(directives), fold(sourceElements));
  }

  @Nonnull
  @Override
  public State reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull Maybe<State> init) {
    return append(id, o(init));
  }

  @Nonnull
  @Override
  public State reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path, @Nonnull List<State> statements) {
    return fold(statements);
  }
}
