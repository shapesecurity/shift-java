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

public interface ReducerP<
  ScriptState,
  DirectiveState,
  StatementState,
  ExpressionState,
  BindingIdentifierState,
  FormalParametersState,
  FunctionBodyState,
  ExpressionSuperState,
  ObjectPropertyState,
  BindingState,
  BindingIdentifierMemberExpressionState,
  SpreadElementExpressionState,
  BlockState,
  CatchClauseState,
  VariableDeclarationBindingState,
  VariableDeclarationExpressionState,
  SwitchCaseState,
  SwitchDefaultState,
  VariableDeclarationState,
  PropertyNameState,
  BindingBindingWithDefaultState,
  VariableDeclaratorState,
  FunctionBodyExpressionState,
  BindingPropertyIdentifierState,
  BindingPropertyPropertyState,
  BindingWithDefaultState,
  ClassElementState,
  MethodDefinitionState,
  FunctionDeclarationClassDeclarationVariableDeclarationState,
  FunctionDeclarationClassDeclarationExpressionState,
  ExportSpecifierState,
  ImportSpecifierState,
  ModuleState,
  ImportDeclarationExportDeclarationStatementState,
  ObjectBindingState,
  BindingPropertyState,
  SpreadElementState,
  SuperState,
  TemplateState,
  ExpressionTemplateElementState
  > {

  @NotNull
  BindingState reduceArrayBinding(
    @NotNull ArrayBinding node,
    @NotNull ImmutableList<Maybe<BindingBindingWithDefaultState>> elements,
    @NotNull Maybe<BindingState> restElement);

  @NotNull
  ExpressionState reduceArrayExpression(
    @NotNull ArrayExpression node,
    @NotNull ImmutableList<Maybe<SpreadElementExpressionState>> elements);

  @NotNull
  ExpressionState reduceArrowExpression(
    @NotNull ArrowExpression node,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyExpressionState body);

  @NotNull
  ExpressionState reduceAssignmentExpression(
    @NotNull AssignmentExpression node,
    @NotNull BindingState binding,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceBinaryExpression(
    @NotNull BinaryExpression node,
    @NotNull ExpressionState left,
    @NotNull ExpressionState right);

  @NotNull
  BindingIdentifierState reduceBindingIdentifier(
    @NotNull BindingIdentifier node);

  @NotNull
  BindingPropertyIdentifierState reduceBindingPropertyIdentifier(
    @NotNull BindingPropertyIdentifier node,
    @NotNull BindingIdentifierState binding,
    @NotNull Maybe<ExpressionState> init);

  @NotNull
  BindingPropertyPropertyState reduceBindingPropertyProperty(
    @NotNull BindingPropertyProperty node,
    @NotNull PropertyNameState name,
    @NotNull BindingBindingWithDefaultState binding);

  @NotNull
  BindingWithDefaultState reduceBindingWithDefault(
    @NotNull BindingWithDefault node,
    @NotNull BindingState binding,
    @NotNull ExpressionState init);

  @NotNull
  BlockState reduceBlock(
    @NotNull Block node,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  StatementState reduceBlockStatement(
    @NotNull BlockStatement node,
    @NotNull BlockState block);

  @NotNull
  StatementState reduceBreakStatement(@NotNull BreakStatement node);

  @NotNull
  ExpressionState reduceCallExpression(
    @NotNull CallExpression node,
    @NotNull ExpressionSuperState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  CatchClauseState reduceCatchClause(
    @NotNull CatchClause node,
    @NotNull BindingState binding,
    @NotNull BlockState body);

  @NotNull
  StatementState reduceClassDeclaration(
    @NotNull ClassDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull Maybe<ExpressionState> _super,
    @NotNull ImmutableList<ClassElementState> elements);

  @NotNull
  ClassElementState reduceClassElement(
    @NotNull ClassElement node,
    @NotNull MethodDefinitionState method);

  @NotNull
  ExpressionState reduceClassExpression(
    @NotNull ClassExpression node,
    @NotNull Maybe<BindingIdentifierState> name,
    @NotNull Maybe<ExpressionState> _super,
    @NotNull ImmutableList<ClassElementState> elements);

  @NotNull
  ExpressionState reduceCompoundAssignmentExpression(
    @NotNull CompoundAssignmentExpression node,
    @NotNull BindingIdentifierMemberExpressionState binding,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceComputedMemberExpression(
    @NotNull ComputedMemberExpression node,
    @NotNull ExpressionSuperState object,
    @NotNull ExpressionState expression);

  @NotNull
  PropertyNameState reduceComputedPropertyName(
    @NotNull ComputedPropertyName node,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceConditionalExpression(
    @NotNull ConditionalExpression node,
    @NotNull ExpressionState test,
    @NotNull ExpressionState consequent,
    @NotNull ExpressionState alternate);

  @NotNull
  StatementState reduceContinueStatement(@NotNull ContinueStatement node);

  @NotNull
  ObjectPropertyState reduceDataProperty(
    @NotNull DataProperty node,
    @NotNull ExpressionState value,
    @NotNull PropertyNameState name);

  @NotNull
  StatementState reduceDebuggerStatement(@NotNull DebuggerStatement node);

  @NotNull
  DirectiveState reduceDirective(@NotNull Directive node);

  @NotNull
  StatementState reduceDoWhileStatement(
    @NotNull DoWhileStatement node,
    @NotNull StatementState body,
    @NotNull ExpressionState test);

  @NotNull
  StatementState reduceEmptyStatement(@NotNull EmptyStatement node);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceExport(
    @NotNull Export node,
    @NotNull FunctionDeclarationClassDeclarationVariableDeclarationState declaration);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceExportAllFrom(@NotNull ExportAllFrom node);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceExportDeclaration(@NotNull ExportDeclaration node);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceExportDefault(
    @NotNull ExportDefault node,
    @NotNull FunctionDeclarationClassDeclarationExpressionState body);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceExportFrom(
    @NotNull ExportFrom node,
    @NotNull ImmutableList<ExportSpecifierState> namedExports);

  @NotNull
  ExportSpecifierState reduceExportSpecifier(@NotNull ExportSpecifier node);

  @NotNull
  StatementState reduceExpressionStatement(
    @NotNull ExpressionStatement node,
    @NotNull ExpressionState expression);

  @NotNull
  StatementState reduceForInStatement(
    @NotNull ForInStatement node,
    @NotNull VariableDeclarationBindingState left,
    @NotNull ExpressionState right,
    @NotNull StatementState body);

  @NotNull
  StatementState reduceForOfStatement(
    @NotNull ForOfStatement node,
    @NotNull VariableDeclarationBindingState left,
    @NotNull ExpressionState right,
    @NotNull StatementState body);

  @NotNull
  StatementState reduceForStatement(
    @NotNull ForStatement node,
    @NotNull Maybe<VariableDeclarationExpressionState> init,
    @NotNull Maybe<ExpressionState> test,
    @NotNull Maybe<ExpressionState> update,
    @NotNull StatementState body);

  @NotNull
  FormalParametersState reduceFormalParameters(
    @NotNull FormalParameters node,
    @NotNull ImmutableList<BindingBindingWithDefaultState> items,
    @NotNull Maybe<BindingIdentifierState> rest);

  @NotNull
  FunctionBodyState reduceFunctionBody(
    @NotNull FunctionBody node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  StatementState reduceFunctionDeclaration(
    @NotNull FunctionDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyState body);

  @NotNull
  ExpressionState reduceFunctionExpression(
    @NotNull FunctionExpression node,
    @NotNull Maybe<BindingIdentifierState> name,
    @NotNull FormalParametersState parameters,
    @NotNull FunctionBodyState body);

  @NotNull
  MethodDefinitionState reduceGetter(
    @NotNull Getter node,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  ExpressionState reduceIdentifierExpression(@NotNull IdentifierExpression node);

  @NotNull
  StatementState reduceIfStatement(
    @NotNull IfStatement node,
    @NotNull ExpressionState test,
    @NotNull StatementState consequent,
    @NotNull Maybe<StatementState> alternate);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceImport(
    @NotNull Import node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull ImmutableList<ImportSpecifierState> namedImports);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceImportDeclaration(@NotNull ImportDeclaration node);

  @NotNull
  ImportDeclarationExportDeclarationStatementState reduceImportNamespace(
    @NotNull ImportNamespace node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull BindingIdentifierState namespaceBinding);

  @NotNull
  ImportSpecifierState reduceImportSpecifier(
    @NotNull ImportSpecifier node,
    @NotNull BindingIdentifierState binding);

  @NotNull
  StatementState reduceLabeledStatement(
    @NotNull LabeledStatement node,
    @NotNull StatementState body);

  @NotNull
  ExpressionState reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node);

  @NotNull
  ExpressionState reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node);

  @NotNull
  ExpressionState reduceLiteralNullExpression(@NotNull LiteralNullExpression node);

  @NotNull
  ExpressionState reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node);

  @NotNull
  ExpressionState reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node);

  @NotNull
  ExpressionState reduceLiteralStringExpression(@NotNull LiteralStringExpression node);

  @NotNull
  MethodDefinitionState reduceMethod(
    @NotNull Method node,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  ModuleState reduceModule(
    @NotNull Module node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<ImportDeclarationExportDeclarationStatementState> items);

  @NotNull
  ObjectPropertyState reduceNamedObjectProperty(
    @NotNull NamedObjectProperty node,
    @NotNull PropertyNameState name);

  @NotNull
  ExpressionState reduceNewExpression(
    @NotNull NewExpression node,
    @NotNull ExpressionState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  ExpressionState reduceNewTargetExpression(@NotNull NewTargetExpression node);

  @NotNull
  ObjectBindingState reduceObjectBinding(
    @NotNull ObjectBinding node,
    @NotNull ImmutableList<BindingPropertyState> properties);

  @NotNull
  ExpressionState reduceObjectExpression(
    @NotNull ObjectExpression node,
    @NotNull ImmutableList<ObjectPropertyState> properties);

  @NotNull
  StatementState reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<ExpressionState> expression);

  @NotNull
  ScriptState reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  MethodDefinitionState reduceSetter(
    @NotNull Setter node,
    @NotNull BindingBindingWithDefaultState params,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  ObjectPropertyState reduceShorthandProperty(@NotNull ShorthandProperty node);

  @NotNull
  SpreadElementState reduceSpreadElement(
    @NotNull SpreadElement node,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceStaticMemberExpression(
    @NotNull StaticMemberExpression node,
    @NotNull ExpressionSuperState object);

  @NotNull
  PropertyNameState reduceStaticPropertyName(@NotNull StaticPropertyName node);

  @NotNull
  SuperState reduceSuper(@NotNull Super node);

  @NotNull
  SwitchCaseState reduceSwitchCase(
    @NotNull SwitchCase node,
    @NotNull ExpressionState test,
    @NotNull ImmutableList<StatementState> consequent);

  @NotNull
  SwitchDefaultState reduceSwitchDefault(
    @NotNull SwitchDefault node,
    @NotNull ImmutableList<StatementState> consequent);

  @NotNull
  StatementState reduceSwitchStatement(
    @NotNull SwitchStatement node,
    @NotNull ExpressionState discriminant,
    @NotNull ImmutableList<SwitchCaseState> cases);

  @NotNull
  StatementState reduceSwitchStatementWithDefault(
    @NotNull SwitchStatementWithDefault node,
    @NotNull ExpressionState discriminant,
    @NotNull ImmutableList<SwitchCaseState> preDefaultCases,
    @NotNull SwitchDefaultState defaultCase,
    @NotNull ImmutableList<SwitchCaseState> postDefaultCases);

  @NotNull
  TemplateState reduceTemplateElement(@NotNull TemplateElement node);

  @NotNull
  ExpressionState reduceTemplateExpression(
    @NotNull TemplateExpression node,
    @NotNull Maybe<ExpressionState> tag,
    @NotNull ImmutableList<ExpressionTemplateElementState> elements);

  @NotNull
  ExpressionState reduceThisExpression(@NotNull ThisExpression node);

  @NotNull
  StatementState reduceThrowStatement(
    @NotNull ThrowStatement node,
    @NotNull ExpressionState expression);

  @NotNull
  StatementState reduceTryCatchStatement(
    @NotNull TryCatchStatement node,
    @NotNull BlockState block,
    @NotNull CatchClauseState catchClause);

  @NotNull
  StatementState reduceTryFinallyStatement(
    @NotNull TryFinallyStatement node,
    @NotNull BlockState block,
    @NotNull Maybe<CatchClauseState> catchClause,
    @NotNull BlockState finalizer);

  @NotNull
  ExpressionState reduceUnaryExpression(
    @NotNull UnaryExpression node,
    @NotNull ExpressionState operand);

  @NotNull
  ExpressionState reduceUpdateExpression(
    @NotNull UpdateExpression node,
    @NotNull BindingIdentifierMemberExpressionState operand);

  @NotNull
  VariableDeclarationState reduceVariableDeclaration(
    @NotNull VariableDeclaration node,
    @NotNull ImmutableList<VariableDeclaratorState> declarators);

  @NotNull
  StatementState reduceVariableDeclarationStatement(
    @NotNull VariableDeclarationStatement node,
    @NotNull VariableDeclarationState declaration);

  @NotNull
  VariableDeclaratorState reduceVariableDeclarator(
    @NotNull VariableDeclarator node,
    @NotNull BindingState binding,
    @NotNull Maybe<ExpressionState> init);

  @NotNull
  StatementState reduceWhileStatement(
    @NotNull WhileStatement node,
    @NotNull ExpressionState test,
    @NotNull StatementState body);

  @NotNull
  StatementState reduceWithStatement(
    @NotNull WithStatement node,
    @NotNull ExpressionState object,
    @NotNull StatementState body);

  @NotNull
  ExpressionState reduceYieldExpression(
    @NotNull YieldExpression node,
    @NotNull Maybe<ExpressionState> expression);

  @NotNull
  ExpressionState reduceYieldGeneratorExpression(
    @NotNull YieldGeneratorExpression node,
    @NotNull ExpressionState expression);
}