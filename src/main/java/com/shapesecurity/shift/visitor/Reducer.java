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

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import org.jetbrains.annotations.NotNull;

public interface Reducer<State> {

  @NotNull
  State reduceArrayBinding(
    @NotNull ArrayBinding node,
    @NotNull ImmutableList<Maybe<State>> elements,
    @NotNull Maybe<State> restElement);

  @NotNull
  State reduceArrayExpression(
    @NotNull ArrayExpression node,
    @NotNull ImmutableList<Maybe<State>> elements);

  @NotNull
  State reduceArrowExpression(
    @NotNull ArrowExpression node,
    @NotNull State params,
    @NotNull State body);

  @NotNull
  State reduceAssignmentExpression(
    @NotNull AssignmentExpression node,
    @NotNull State binding,
    @NotNull State expression);

  @NotNull
  State reduceBinaryExpression(
    @NotNull BinaryExpression node,
    @NotNull State left,
    @NotNull State right);

  @NotNull
  State reduceBindingIdentifier(
    @NotNull BindingIdentifier node);

  @NotNull
  State reduceBindingPropertyIdentifier(
    @NotNull BindingPropertyIdentifier node,
    @NotNull State binding,
    @NotNull Maybe<State> init);

  @NotNull
  State reduceBindingPropertyProperty(
    @NotNull BindingPropertyProperty node,
    @NotNull State name,
    @NotNull State binding);

  @NotNull
  State reduceBindingWithDefault(
    @NotNull BindingWithDefault node,
    @NotNull State binding,
    @NotNull State init);

  @NotNull
  State reduceBlock(
    @NotNull Block node,
    @NotNull ImmutableList<State> statements);

  @NotNull
  State reduceBlockStatement(
    @NotNull BlockStatement node,
    @NotNull State block);

  @NotNull
  State reduceBreakStatement(@NotNull BreakStatement node);

  @NotNull
  State reduceCallExpression(
    @NotNull CallExpression node,
    @NotNull State callee,
    @NotNull ImmutableList<State> arguments);

  @NotNull
  State reduceCatchClause(
    @NotNull CatchClause node,
    @NotNull State binding,
    @NotNull State body);

  @NotNull
  State reduceClassDeclaration(
    @NotNull ClassDeclaration node,
    @NotNull State name,
    @NotNull Maybe<State> _super,
    @NotNull ImmutableList<State> elements);

  @NotNull
  State reduceClassElement(
    @NotNull ClassElement node,
    @NotNull State method);

  @NotNull
  State reduceClassExpression(
    @NotNull ClassExpression node,
    @NotNull Maybe<State> name,
    @NotNull Maybe<State> _super,
    @NotNull ImmutableList<State> elements);

  @NotNull
  State reduceCompoundAssignmentExpression(
    @NotNull CompoundAssignmentExpression node,
    @NotNull State binding,
    @NotNull State expression);

  @NotNull
  State reduceComputedMemberExpression(
    @NotNull ComputedMemberExpression node,
    @NotNull State expression,
    @NotNull State object);

  @NotNull
  State reduceComputedPropertyName(
    @NotNull ComputedPropertyName node,
    @NotNull State expression);

  @NotNull
  State reduceConditionalExpression(
    @NotNull ConditionalExpression node,
    @NotNull State test,
    @NotNull State consequent,
    @NotNull State alternate);

  @NotNull
  State reduceContinueStatement(@NotNull ContinueStatement node);

  @NotNull
  State reduceDataProperty(
    @NotNull DataProperty node,
    @NotNull State value,
    @NotNull State name);

  @NotNull
  State reduceDebuggerStatement(@NotNull DebuggerStatement node);

  @NotNull
  State reduceDirective(@NotNull Directive node);

  @NotNull
  State reduceDoWhileStatement(
    @NotNull DoWhileStatement node,
    @NotNull State body,
    @NotNull State test);

  @NotNull
  State reduceEmptyStatement(@NotNull EmptyStatement node);

  @NotNull
  State reduceExport(
    @NotNull Export node,
    @NotNull State declaration);

  @NotNull
  State reduceExportAllFrom(@NotNull ExportAllFrom node);

  @NotNull
  State reduceExportDefault(
    @NotNull ExportDefault node,
    @NotNull State body);

  @NotNull
  State reduceExportFrom(
    @NotNull ExportFrom node,
    @NotNull ImmutableList<State> namedExports);

  @NotNull
  State reduceExportSpecifier(@NotNull ExportSpecifier node);

  @NotNull
  State reduceExpressionStatement(
    @NotNull ExpressionStatement node,
    @NotNull State expression);

  @NotNull
  State reduceForInStatement(
    @NotNull ForInStatement node,
    @NotNull State left,
    @NotNull State right,
    @NotNull State body);

  @NotNull
  State reduceForOfStatement(
    @NotNull ForOfStatement node,
    @NotNull State left,
    @NotNull State right,
    @NotNull State body);

  @NotNull
  State reduceForStatement(
    @NotNull ForStatement node,
    @NotNull Maybe<State> init,
    @NotNull Maybe<State> test,
    @NotNull Maybe<State> update,
    @NotNull State body);

  @NotNull
  State reduceFormalParameters(
    @NotNull FormalParameters node,
    @NotNull ImmutableList<State> items,
    @NotNull Maybe<State> rest);

  @NotNull
  State reduceFunctionBody(
    @NotNull FunctionBody node,
    @NotNull ImmutableList<State> directives,
    @NotNull ImmutableList<State> statements);

  @NotNull
  State reduceFunctionDeclaration(
    @NotNull FunctionDeclaration node,
    @NotNull State name,
    @NotNull State params,
    @NotNull State body);

  @NotNull
  State reduceFunctionExpression(
    @NotNull FunctionExpression node,
    @NotNull Maybe<State> name,
    @NotNull State parameters,
    @NotNull State body);

  @NotNull
  State reduceGetter(
    @NotNull Getter node,
    @NotNull State name,
    @NotNull State body);

  @NotNull
  State reduceIdentifierExpression(@NotNull IdentifierExpression node);

  @NotNull
  State reduceIfStatement(
    @NotNull IfStatement node,
    @NotNull State test,
    @NotNull State consequent,
    @NotNull Maybe<State> alternate);

  @NotNull
  State reduceImport(
    @NotNull Import node,
    @NotNull Maybe<State> defaultBinding,
    @NotNull ImmutableList<State> namedImports);

  @NotNull
  State reduceImportNamespace(
    @NotNull ImportNamespace node,
    @NotNull Maybe<State> defaultBinding,
    @NotNull State namespaceBinding);

  @NotNull
  State reduceImportSpecifier(
    @NotNull ImportSpecifier node,
    @NotNull State binding);

  @NotNull
  State reduceLabeledStatement(
    @NotNull LabeledStatement node,
    @NotNull State body);

  @NotNull
  State reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node);

  @NotNull
  State reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node);

  @NotNull
  State reduceLiteralNullExpression(@NotNull LiteralNullExpression node);

  @NotNull
  State reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node);

  @NotNull
  State reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node);

  @NotNull
  State reduceLiteralStringExpression(@NotNull LiteralStringExpression node);

  @NotNull
  State reduceMethod(
    @NotNull Method node,
    @NotNull State params,
    @NotNull State body,
    @NotNull State name);

  @NotNull
  State reduceModule(
    @NotNull Module node,
    @NotNull ImmutableList<State> directives,
    @NotNull ImmutableList<State> items);

  @NotNull
  State reduceNewExpression(
    @NotNull NewExpression node,
    @NotNull State callee,
    @NotNull ImmutableList<State> arguments);

  @NotNull
  State reduceNewTargetExpression(@NotNull NewTargetExpression node);

  @NotNull
  State reduceObjectBinding(
    @NotNull ObjectBinding node,
    @NotNull ImmutableList<State> properties);

  @NotNull
  State reduceObjectExpression(
    @NotNull ObjectExpression node,
    @NotNull ImmutableList<State> properties);

  @NotNull
  State reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<State> expression);

  @NotNull
  State reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<State> directives,
    @NotNull ImmutableList<State> statements);

  @NotNull
  State reduceSetter(
    @NotNull Setter node,
    @NotNull State params,
    @NotNull State body,
    @NotNull State name);

  @NotNull
  State reduceShorthandProperty(@NotNull ShorthandProperty node);

  @NotNull
  State reduceSpreadElement(
    @NotNull SpreadElement node,
    @NotNull State expression);

  @NotNull
  State reduceStaticMemberExpression(
    @NotNull StaticMemberExpression node,
    @NotNull State object);

  @NotNull
  State reduceStaticPropertyName(@NotNull StaticPropertyName node);

  @NotNull
  State reduceSuper(@NotNull Super node);

  @NotNull
  State reduceSwitchCase(
    @NotNull SwitchCase node,
    @NotNull State test,
    @NotNull ImmutableList<State> consequent);

  @NotNull
  State reduceSwitchDefault(
    @NotNull SwitchDefault node,
    @NotNull ImmutableList<State> consequent);

  @NotNull
  State reduceSwitchStatement(
    @NotNull SwitchStatement node,
    @NotNull State discriminant,
    @NotNull ImmutableList<State> cases);

  @NotNull
  State reduceSwitchStatementWithDefault(
    @NotNull SwitchStatementWithDefault node,
    @NotNull State discriminant,
    @NotNull ImmutableList<State> preDefaultCases,
    @NotNull State defaultCase,
    @NotNull ImmutableList<State> postDefaultCases);

  @NotNull
  State reduceTemplateElement(@NotNull TemplateElement node);

  @NotNull
  State reduceTemplateExpression(
    @NotNull TemplateExpression node,
    @NotNull Maybe<State> tag,
    @NotNull ImmutableList<State> elements);

  @NotNull
  State reduceThisExpression(@NotNull ThisExpression node);

  @NotNull
  State reduceThrowStatement(
    @NotNull ThrowStatement node,
    @NotNull State expression);

  @NotNull
  State reduceTryCatchStatement(
    @NotNull TryCatchStatement node,
    @NotNull State block,
    @NotNull State catchClause);

  @NotNull
  State reduceTryFinallyStatement(
    @NotNull TryFinallyStatement node,
    @NotNull State block,
    @NotNull Maybe<State> catchClause,
    @NotNull State finalizer);

  @NotNull
  State reduceUnaryExpression(
    @NotNull UnaryExpression node,
    @NotNull State operand);

  @NotNull
  State reduceUpdateExpression(
    @NotNull UpdateExpression node,
    @NotNull State operand);

  @NotNull
  State reduceVariableDeclaration(
    @NotNull VariableDeclaration node,
    @NotNull ImmutableList<State> declarators);

  @NotNull
  State reduceVariableDeclarationStatement(
    @NotNull VariableDeclarationStatement node,
    @NotNull State declaration);

  @NotNull
  State reduceVariableDeclarator(
    @NotNull VariableDeclarator node,
    @NotNull State binding,
    @NotNull Maybe<State> init);

  @NotNull
  State reduceWhileStatement(
    @NotNull WhileStatement node,
    @NotNull State test,
    @NotNull State body);

  @NotNull
  State reduceWithStatement(
    @NotNull WithStatement node,
    @NotNull State object,
    @NotNull State body);

  @NotNull
  State reduceYieldExpression(
    @NotNull YieldExpression node,
    @NotNull Maybe<State> expression);

  @NotNull
  State reduceYieldGeneratorExpression(
    @NotNull YieldGeneratorExpression node,
    @NotNull State expression);
}