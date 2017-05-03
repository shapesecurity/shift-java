///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.others;
//
//import com.shapesecurity.functional.Unit;
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.functional.data.NonEmptyImmutableList;
//import com.shapesecurity.shift.ast.*;
//import javax.annotation.Nonnull;
//
//public abstract class TestReducerWithPath implements Reducer<Unit> {
//
//  protected abstract void accept(@Nonnull Node node, @Nonnull ImmutableList<Branch> path);
//
//  @Nonnull
//  @Override
//  public final Unit reduceScript(@Nonnull Script node, @Nonnull ImmutableList<Branch> path, @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceIdentifier(@Nonnull Identifier node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceIdentifierExpression(
//      @Nonnull IdentifierExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit identifier) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceThisExpression(@Nonnull ThisExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceFunctionExpression(
//      @Nonnull FunctionExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Unit> name,
//      @Nonnull ImmutableList<Unit> parameters,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceStaticMemberExpression(
//      @Nonnull StaticMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit object,
//      @Nonnull Unit property) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceComputedMemberExpression(
//      @Nonnull ComputedMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit object,
//      @Nonnull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceObjectExpression(
//      @Nonnull ObjectExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Unit> properties) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceBinaryExpression(
//      @Nonnull BinaryExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit left,
//      @Nonnull Unit right) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceAssignmentExpression(
//      @Nonnull AssignmentExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit binding,
//      @Nonnull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceArrayExpression(
//      @Nonnull ArrayExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Maybe<Unit>> elements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceNewExpression(
//      @Nonnull NewExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit callee,
//      @Nonnull ImmutableList<Unit> arguments) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceCallExpression(
//      @Nonnull CallExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit callee,
//      @Nonnull ImmutableList<Unit> arguments) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reducePostfixExpression(
//      @Nonnull PostfixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit operand) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reducePrefixExpression(
//      @Nonnull PrefixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit operand) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceConditionalExpression(
//      @Nonnull ConditionalExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit test,
//      @Nonnull Unit consequent,
//      @Nonnull Unit alternate) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceFunctionDeclaration(
//      @Nonnull FunctionDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit name,
//      @Nonnull ImmutableList<Unit> params,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceBlockStatement(
//      @Nonnull BlockStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit block) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceBreakStatement(
//      @Nonnull BreakStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Unit> label) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceCatchClause(
//      @Nonnull CatchClause node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit binding,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceContinueStatement(
//      @Nonnull ContinueStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Unit> label) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceDoWhileStatement(
//      @Nonnull DoWhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit body,
//      @Nonnull Unit test) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceExpressionStatement(
//      @Nonnull ExpressionStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceForInStatement(
//      @Nonnull ForInStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Either<Unit, Unit> left,
//      @Nonnull Unit right,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceForStatement(
//      @Nonnull ForStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Either<Unit, Unit>> init,
//      @Nonnull Maybe<Unit> test,
//      @Nonnull Maybe<Unit> update,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceIfStatement(
//      @Nonnull IfStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit test,
//      @Nonnull Unit consequent,
//      @Nonnull Maybe<Unit> alternate) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceLabeledStatement(
//      @Nonnull LabeledStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit label,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceReturnStatement(
//      @Nonnull ReturnStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Unit> expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceSwitchCase(
//      @Nonnull SwitchCase node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit test,
//      @Nonnull ImmutableList<Unit> consequent) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceSwitchDefault(
//      @Nonnull SwitchDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Unit> consequent) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceSwitchStatement(
//      @Nonnull SwitchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit discriminant,
//      @Nonnull ImmutableList<Unit> cases) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceSwitchStatementWithDefault(
//      @Nonnull SwitchStatementWithDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit discriminant,
//      @Nonnull ImmutableList<Unit> preDefaultCases,
//      @Nonnull Unit defaultCase,
//      @Nonnull ImmutableList<Unit> postDefaultCases) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceThrowStatement(
//      @Nonnull ThrowStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceTryCatchStatement(
//      @Nonnull TryCatchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit block,
//      @Nonnull Unit catchClause) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceTryFinallyStatement(
//      @Nonnull TryFinallyStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit block,
//      @Nonnull Maybe<Unit> catchClause,
//      @Nonnull Unit finalizer) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceVariableDeclarationStatement(
//      @Nonnull VariableDeclarationStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit declaration) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceVariableDeclaration(
//      @Nonnull VariableDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull NonEmptyImmutableList<Unit> declarators) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceWhileStatement(
//      @Nonnull WhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit test,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceWithStatement(
//      @Nonnull WithStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit object,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceDataProperty(
//      @Nonnull DataProperty node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit name,
//      @Nonnull Unit value) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceGetter(
//      @Nonnull Getter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit name,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceSetter(
//      @Nonnull Setter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit name,
//      @Nonnull Unit parameter,
//      @Nonnull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reducePropertyName(@Nonnull PropertyName node, @Nonnull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceFunctionBody(
//      @Nonnull FunctionBody node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Unit> directives,
//      @Nonnull ImmutableList<Unit> statements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceVariableDeclarator(
//      @Nonnull VariableDeclarator node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Unit binding,
//      @Nonnull Maybe<Unit> init) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @Nonnull
//  @Override
//  public final Unit reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<Branch> path, @Nonnull ImmutableList<Unit> statements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//}
