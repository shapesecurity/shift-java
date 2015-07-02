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

import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.*;
import org.jetbrains.annotations.NotNull;

public class MonoidalReducer<State> implements Reducer<State> {
  @NotNull
  protected final Monoid<State> monoidClass;
  private final State identity;

  protected MonoidalReducer(@NotNull Monoid<State> monoidClass) {
    this.monoidClass = monoidClass;
    this.identity = this.monoidClass.identity();
  }

  @NotNull
  @Override
  public State reduceScript(@NotNull Script node, @NotNull ImmutableList<Branch> path, @NotNull State body) {
    return body;
  }

  @NotNull
  @Override
  public State reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State identifier) {
    return identifier;
  }

  @NotNull
  @Override
  public State reduceThisExpression(@NotNull ThisExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<State> name,
      @NotNull ImmutableList<State> parameters,
      @NotNull State body) {
    return append(fold1(parameters, o(name)), body);
  }

  private State append(State a, State b) {
    return this.monoidClass.append(a, b);
  }

  private State fold1(ImmutableList<State> as, State a) {
    return as.foldLeft(this::append, a);
  }

  @NotNull
  private State o(@NotNull Maybe<State> s) {
    return s.orJust(this.identity);
  }

  @NotNull
  @Override
  public State reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State object,
      @NotNull State property) {
    return append(object, property);
  }

  @NotNull
  @Override
  public State reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State object,
      @NotNull State expression) {
    return append(object, expression);
  }

  @NotNull
  @Override
  public State reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<State> properties) {
    return fold(properties);
  }

  private State fold(ImmutableList<State> as) {
    return as.foldLeft(this::append, this.identity);
  }

  @NotNull
  @Override
  public State reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State left,
      @NotNull State right) {
    return append(left, right);
  }

  @NotNull
  @Override
  public State reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State binding,
      @NotNull State expression) {
    return append(binding, expression);
  }

  @NotNull
  @Override
  public State reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<Maybe<State>> elements) {
    return fold(Maybe.catMaybes(elements));
  }

  @NotNull
  @Override
  public State reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State callee,
      @NotNull ImmutableList<State> arguments) {
    return fold1(arguments, callee);
  }

  @NotNull
  @Override
  public State reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State callee,
      @NotNull ImmutableList<State> arguments) {
    return fold1(arguments, callee);
  }

  @NotNull
  @Override
  public State reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State operand) {
    return operand;
  }

  @NotNull
  @Override
  public State reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State operand) {
    return operand;
  }

  @NotNull
  @Override
  public State reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State test,
      @NotNull State consequent,
      @NotNull State alternate) {
    return append(test, consequent, alternate);
  }

  private State append(State a, State b, State c) {
    return append(append(a, b), c);
  }

  @NotNull
  @Override
  public State reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State name,
      @NotNull ImmutableList<State> params,
      @NotNull State body) {
    return append(fold1(params, name), body);
  }

  @NotNull
  @Override
  public State reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceBlockStatement(@NotNull BlockStatement node, @NotNull ImmutableList<Branch> path, @NotNull State block) {
    return block;
  }

  @NotNull
  @Override
  public State reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<State> label) {
    return o(label);
  }

  @NotNull
  @Override
  public State reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State binding,
      @NotNull State body) {
    return append(binding, body);
  }

  @NotNull
  @Override
  public State reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<State> label) {
    return o(label);
  }

  @NotNull
  @Override
  public State reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State body,
      @NotNull State test) {
    return append(body, test);
  }

  @NotNull
  @Override
  public State reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State expression) {
    return expression;
  }

  @NotNull
  @Override
  public State reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Either<State, State> left,
      @NotNull State right,
      @NotNull State body) {
    return append(Either.extract(left), right, body);
  }

  @NotNull
  @Override
  public State reduceForStatement(
      @NotNull ForStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Either<State, State>> init,
      @NotNull Maybe<State> test,
      @NotNull Maybe<State> update,
      @NotNull State body) {
    return append(o(init.map(Either::extract)), o(test), o(update), body);
  }

  private State append(State a, State b, State c, State d) {
    return append(append(a, b, c), d);
  }

  @NotNull
  @Override
  public State reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State test,
      @NotNull State consequent,
      @NotNull Maybe<State> alternate) {
    return append(test, consequent, o(alternate));
  }

  @NotNull
  @Override
  public State reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State label,
      @NotNull State body) {
    return append(label, body);
  }

  @NotNull
  @Override
  public State reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<State> expression) {
    return o(expression);
  }

  @NotNull
  @Override
  public State reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State test,
      @NotNull ImmutableList<State> consequent) {
    return fold1(consequent, test);
  }

  @NotNull
  @Override
  public State reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<State> consequent) {
    return fold(consequent);
  }

  @NotNull
  @Override
  public State reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State discriminant,
      @NotNull ImmutableList<State> cases) {
    return fold1(cases, discriminant);
  }

  @NotNull
  @Override
  public State reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State discriminant,
      @NotNull ImmutableList<State> preDefaultCases,
      @NotNull State defaultCase,
      @NotNull ImmutableList<State> postDefaultCases) {
    return append(discriminant, fold(preDefaultCases), defaultCase, fold(postDefaultCases));
  }

  @NotNull
  @Override
  public State reduceThrowStatement(@NotNull ThrowStatement node, @NotNull ImmutableList<Branch> path, @NotNull State expression) {
    return expression;
  }

  @NotNull
  @Override
  public State reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State block,
      @NotNull State catchClause) {
    return append(block, catchClause);
  }

  @NotNull
  @Override
  public State reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State block,
      @NotNull Maybe<State> catchClause,
      @NotNull State finalizer) {
    return append(block, o(catchClause), finalizer);
  }

  @NotNull
  @Override
  public State reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State declaration) {
    return declaration;
  }

  @NotNull
  @Override
  public State reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull ImmutableList<Branch> path,
      @NotNull NonEmptyImmutableList<State> declarators) {
    return fold(declarators);
  }

  @NotNull
  @Override
  public State reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State test,
      @NotNull State body) {
    return append(test, body);
  }

  @NotNull
  @Override
  public State reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State object,
      @NotNull State body) {
    return append(object, body);
  }

  @NotNull
  @Override
  public State reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State name,
      @NotNull State value) {
    return append(name, value);
  }

  @NotNull
  @Override
  public State reduceGetter(@NotNull Getter node, @NotNull ImmutableList<Branch> path, @NotNull State name, @NotNull State body) {
    return append(name, body);
  }

  @NotNull
  @Override
  public State reduceSetter(
      @NotNull Setter node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State name,
      @NotNull State parameter,
      @NotNull State body) {
    return append(name, parameter, body);
  }

  @NotNull
  @Override
  public State reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
    return this.identity;
  }

  @NotNull
  @Override
  public State reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<State> directives,
      @NotNull ImmutableList<State> statements) {
    return append(fold(directives), fold(statements));
  }

  @NotNull
  @Override
  public State reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull ImmutableList<Branch> path,
      @NotNull State binding,
      @NotNull Maybe<State> init) {
    return append(binding, o(init));
  }

  @NotNull
  @Override
  public State reduceBlock(@NotNull Block node, @NotNull ImmutableList<Branch> path, @NotNull ImmutableList<State> statements) {
    return fold(statements);
  }
}
