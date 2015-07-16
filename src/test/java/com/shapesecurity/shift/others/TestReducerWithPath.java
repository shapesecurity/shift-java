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
//import org.jetbrains.annotations.NotNull;
//
//public abstract class TestReducerWithPath implements Reducer<Unit> {
//
//  protected abstract void accept(@NotNull Node node, @NotNull ImmutableList<Branch> path);
//
//  @NotNull
//  @Override
//  public final Unit reduceScript(@NotNull Script node, @NotNull ImmutableList<Branch> path, @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceIdentifier(@NotNull Identifier node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceIdentifierExpression(
//      @NotNull IdentifierExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit identifier) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceThisExpression(@NotNull ThisExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceFunctionExpression(
//      @NotNull FunctionExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<Unit> name,
//      @NotNull ImmutableList<Unit> parameters,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceStaticMemberExpression(
//      @NotNull StaticMemberExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit object,
//      @NotNull Unit property) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceComputedMemberExpression(
//      @NotNull ComputedMemberExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit object,
//      @NotNull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceObjectExpression(
//      @NotNull ObjectExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull ImmutableList<Unit> properties) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceBinaryExpression(
//      @NotNull BinaryExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit left,
//      @NotNull Unit right) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceAssignmentExpression(
//      @NotNull AssignmentExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit binding,
//      @NotNull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceArrayExpression(
//      @NotNull ArrayExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull ImmutableList<Maybe<Unit>> elements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceNewExpression(
//      @NotNull NewExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit callee,
//      @NotNull ImmutableList<Unit> arguments) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceCallExpression(
//      @NotNull CallExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit callee,
//      @NotNull ImmutableList<Unit> arguments) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reducePostfixExpression(
//      @NotNull PostfixExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit operand) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reducePrefixExpression(
//      @NotNull PrefixExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit operand) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceConditionalExpression(
//      @NotNull ConditionalExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit test,
//      @NotNull Unit consequent,
//      @NotNull Unit alternate) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceFunctionDeclaration(
//      @NotNull FunctionDeclaration node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit name,
//      @NotNull ImmutableList<Unit> params,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceBlockStatement(
//      @NotNull BlockStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit block) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceBreakStatement(
//      @NotNull BreakStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<Unit> label) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceCatchClause(
//      @NotNull CatchClause node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit binding,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceContinueStatement(
//      @NotNull ContinueStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<Unit> label) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceDoWhileStatement(
//      @NotNull DoWhileStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit body,
//      @NotNull Unit test) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceExpressionStatement(
//      @NotNull ExpressionStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceForInStatement(
//      @NotNull ForInStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Either<Unit, Unit> left,
//      @NotNull Unit right,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceForStatement(
//      @NotNull ForStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<Either<Unit, Unit>> init,
//      @NotNull Maybe<Unit> test,
//      @NotNull Maybe<Unit> update,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceIfStatement(
//      @NotNull IfStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit test,
//      @NotNull Unit consequent,
//      @NotNull Maybe<Unit> alternate) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceLabeledStatement(
//      @NotNull LabeledStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit label,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceReturnStatement(
//      @NotNull ReturnStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<Unit> expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceSwitchCase(
//      @NotNull SwitchCase node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit test,
//      @NotNull ImmutableList<Unit> consequent) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceSwitchDefault(
//      @NotNull SwitchDefault node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull ImmutableList<Unit> consequent) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceSwitchStatement(
//      @NotNull SwitchStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit discriminant,
//      @NotNull ImmutableList<Unit> cases) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceSwitchStatementWithDefault(
//      @NotNull SwitchStatementWithDefault node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit discriminant,
//      @NotNull ImmutableList<Unit> preDefaultCases,
//      @NotNull Unit defaultCase,
//      @NotNull ImmutableList<Unit> postDefaultCases) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceThrowStatement(
//      @NotNull ThrowStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit expression) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceTryCatchStatement(
//      @NotNull TryCatchStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit block,
//      @NotNull Unit catchClause) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceTryFinallyStatement(
//      @NotNull TryFinallyStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit block,
//      @NotNull Maybe<Unit> catchClause,
//      @NotNull Unit finalizer) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceVariableDeclarationStatement(
//      @NotNull VariableDeclarationStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit declaration) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceVariableDeclaration(
//      @NotNull VariableDeclaration node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull NonEmptyImmutableList<Unit> declarators) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceWhileStatement(
//      @NotNull WhileStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit test,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceWithStatement(
//      @NotNull WithStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit object,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceDataProperty(
//      @NotNull DataProperty node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit name,
//      @NotNull Unit value) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceGetter(
//      @NotNull Getter node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit name,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceSetter(
//      @NotNull Setter node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit name,
//      @NotNull Unit parameter,
//      @NotNull Unit body) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceFunctionBody(
//      @NotNull FunctionBody node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull ImmutableList<Unit> directives,
//      @NotNull ImmutableList<Unit> statements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceVariableDeclarator(
//      @NotNull VariableDeclarator node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Unit binding,
//      @NotNull Maybe<Unit> init) {
//    accept(node, path);
//    return Unit.unit;
//  }
//
//  @NotNull
//  @Override
//  public final Unit reduceBlock(@NotNull Block node, @NotNull ImmutableList<Branch> path, @NotNull ImmutableList<Unit> statements) {
//    accept(node, path);
//    return Unit.unit;
//  }
//}
