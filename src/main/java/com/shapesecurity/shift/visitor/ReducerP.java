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
  ArrayBindingState, ArrayExpressionState, ArrowExpressionState, AssignmentExpressionState, BinaryExpressionState, BindingState, BindingBindingWithDefaultState, BindingIdentifierState, BindingIdentifierMemberExpressionState, BindingPropertyState, BindingPropertyIdentifierState, BindingPropertyPropertyState, BindingWithDefaultState, BlockState, BlockStatementState, BreakStatementState, CallExpressionState, CatchClauseState, ClassDeclarationState, ClassElementState, ClassExpressionState, CompoundAssignmentExpressionState, ComputedMemberExpressionState, ComputedPropertyNameState, ConditionalExpressionState, ContinueStatementState, DataPropertyState, DebuggerStatementState, DirectiveState, DoWhileStatementState, EmptyStatementState, ExportState, ExportAllFromState, ExportDeclarationState, ExportDefaultState, ExportFromState, ExportSpecifierState, ExpressionState, ExpressionStatementState, ExpressionSuperState, ExpressionTemplateElementState, ForInStatementState, ForOfStatementState, ForStatementState, FormalParametersState, FunctionBodyState, FunctionBodyExpressionState, FunctionDeclarationState, FunctionDeclarationClassDeclarationExpressionState, FunctionDeclarationClassDeclarationVariableDeclarationState, FunctionExpressionState, GetterState, IdentifierExpressionState, IfStatementState, ImportState, ImportDeclarationState, ImportDeclarationExportDeclarationStatementState, ImportNamespaceState, ImportSpecifierState, LabeledStatementState, LiteralBooleanExpressionState, LiteralInfinityExpressionState, LiteralNullExpressionState, LiteralNumericExpressionState, LiteralRegExpExpressionState, LiteralStringExpressionState, MethodState, MethodDefinitionState, ModuleState, NewExpressionState, NewTargetExpressionState, ObjectBindingState, ObjectExpressionState, ObjectPropertyState, PropertyNameState, ReturnStatementState, ScriptState, SetterState, ShorthandPropertyState, SpreadElementState, SpreadElementExpressionState, StatementState, StaticMemberExpressionState, StaticPropertyNameState, SuperState, SwitchCaseState, SwitchDefaultState, SwitchStatementState, SwitchStatementWithDefaultState, TemplateElementState, TemplateExpressionState, ThisExpressionState, ThrowStatementState, TryCatchStatementState, TryFinallyStatementState, UnaryExpressionState, UpdateExpressionState, VariableDeclarationState, VariableDeclarationBindingState, VariableDeclarationExpressionState, VariableDeclarationStatementState, VariableDeclaratorState, WhileStatementState, WithStatementState, YieldExpressionState, YieldGeneratorExpressionState
  > {

  @NotNull
  ArrayBindingState reduceArrayBinding(
    @NotNull ArrayBinding node,
    @NotNull ImmutableList<Maybe<BindingBindingWithDefaultState>> elements,
    @NotNull Maybe<BindingState> restElement);

  @NotNull
  ArrayExpressionState reduceArrayExpression(
    @NotNull ArrayExpression node,
    @NotNull ImmutableList<Maybe<SpreadElementExpressionState>> elements);

  @NotNull
  ArrowExpressionState reduceArrowExpression(
    @NotNull ArrowExpression node,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyExpressionState body);

  @NotNull
  AssignmentExpressionState reduceAssignmentExpression(
    @NotNull AssignmentExpression node,
    @NotNull BindingState binding,
    @NotNull ExpressionState expression);

  @NotNull
  BinaryExpressionState reduceBinaryExpression(
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
  BlockStatementState reduceBlockStatement(
    @NotNull BlockStatement node,
    @NotNull BlockState block);

  @NotNull
  BreakStatementState reduceBreakStatement(@NotNull BreakStatement node);

  @NotNull
  CallExpressionState reduceCallExpression(
    @NotNull CallExpression node,
    @NotNull ExpressionSuperState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  CatchClauseState reduceCatchClause(
    @NotNull CatchClause node,
    @NotNull BindingState binding,
    @NotNull BlockState body);

  @NotNull
  ClassDeclarationState reduceClassDeclaration(
    @NotNull ClassDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull Maybe<ExpressionState> _super,
    @NotNull ImmutableList<ClassElementState> elements);

  @NotNull
  ClassElementState reduceClassElement(
    @NotNull ClassElement node,
    @NotNull MethodDefinitionState method);

  @NotNull
  ClassExpressionState reduceClassExpression(
    @NotNull ClassExpression node,
    @NotNull Maybe<BindingIdentifierState> name,
    @NotNull Maybe<ExpressionState> _super,
    @NotNull ImmutableList<ClassElementState> elements);

  @NotNull
  CompoundAssignmentExpressionState reduceCompoundAssignmentExpression(
    @NotNull CompoundAssignmentExpression node,
    @NotNull BindingIdentifierMemberExpressionState binding,
    @NotNull ExpressionState expression);

  @NotNull
  ComputedMemberExpressionState reduceComputedMemberExpression(
    @NotNull ComputedMemberExpression node,
    @NotNull ExpressionSuperState object,
    @NotNull ExpressionState expression);

  @NotNull
  ComputedPropertyNameState reduceComputedPropertyName(
    @NotNull ComputedPropertyName node,
    @NotNull ExpressionState expression);

  @NotNull
  ConditionalExpressionState reduceConditionalExpression(
    @NotNull ConditionalExpression node,
    @NotNull ExpressionState test,
    @NotNull ExpressionState consequent,
    @NotNull ExpressionState alternate);

  @NotNull
  ContinueStatementState reduceContinueStatement(@NotNull ContinueStatement node);

  @NotNull
  DataPropertyState reduceDataProperty(
    @NotNull DataProperty node,
    @NotNull ExpressionState value,
    @NotNull PropertyNameState name);

  @NotNull
  DebuggerStatementState reduceDebuggerStatement(@NotNull DebuggerStatement node);

  @NotNull
  DirectiveState reduceDirective(@NotNull Directive node);

  @NotNull
  DoWhileStatementState reduceDoWhileStatement(
    @NotNull DoWhileStatement node,
    @NotNull StatementState body,
    @NotNull ExpressionState test);

  @NotNull
  EmptyStatementState reduceEmptyStatement(@NotNull EmptyStatement node);

  @NotNull
  ExportState reduceExport(
    @NotNull Export node,
    @NotNull FunctionDeclarationClassDeclarationVariableDeclarationState declaration);

  @NotNull
  ExportAllFromState reduceExportAllFrom(@NotNull ExportAllFrom node);

  @NotNull
  ExportDeclarationState reduceExportDeclaration(@NotNull ExportDeclaration node);

  @NotNull
  ExportDefaultState reduceExportDefault(
    @NotNull ExportDefault node,
    @NotNull FunctionDeclarationClassDeclarationExpressionState body);

  @NotNull
  ExportFromState reduceExportFrom(
    @NotNull ExportFrom node,
    @NotNull ImmutableList<ExportSpecifierState> namedExports);

  @NotNull
  ExportSpecifierState reduceExportSpecifier(@NotNull ExportSpecifier node);

  @NotNull
  ExpressionStatementState reduceExpressionStatement(
    @NotNull ExpressionStatement node,
    @NotNull ExpressionState expression);

  @NotNull
  ForInStatementState reduceForInStatement(
    @NotNull ForInStatement node,
    @NotNull VariableDeclarationBindingState left,
    @NotNull ExpressionState right,
    @NotNull StatementState body);

  @NotNull
  ForOfStatementState reduceForOfStatement(
    @NotNull ForOfStatement node,
    @NotNull VariableDeclarationBindingState left,
    @NotNull ExpressionState right,
    @NotNull StatementState body);

  @NotNull
  ForStatementState reduceForStatement(
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
  FunctionDeclarationState reduceFunctionDeclaration(
    @NotNull FunctionDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyState body);

  @NotNull
  FunctionExpressionState reduceFunctionExpression(
    @NotNull FunctionExpression node,
    @NotNull Maybe<BindingIdentifierState> name,
    @NotNull FormalParametersState parameters,
    @NotNull FunctionBodyState body);

  @NotNull
  GetterState reduceGetter(
    @NotNull Getter node,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  IdentifierExpressionState reduceIdentifierExpression(@NotNull IdentifierExpression node);

  @NotNull
  IfStatementState reduceIfStatement(
    @NotNull IfStatement node,
    @NotNull ExpressionState test,
    @NotNull StatementState consequent,
    @NotNull Maybe<StatementState> alternate);

  @NotNull
  ImportState reduceImport(
    @NotNull Import node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull ImmutableList<ImportSpecifierState> namedImports);

  @NotNull
  ImportDeclarationState reduceImportDeclaration(@NotNull ImportDeclaration node);

  @NotNull
  ImportNamespaceState reduceImportNamespace(
    @NotNull ImportNamespace node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull BindingIdentifierState namespaceBinding);

  @NotNull
  ImportSpecifierState reduceImportSpecifier(
    @NotNull ImportSpecifier node,
    @NotNull BindingIdentifierState binding);

  @NotNull
  LabeledStatementState reduceLabeledStatement(
    @NotNull LabeledStatement node,
    @NotNull StatementState body);

  @NotNull
  LiteralBooleanExpressionState reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node);

  @NotNull
  LiteralInfinityExpressionState reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node);

  @NotNull
  LiteralNullExpressionState reduceLiteralNullExpression(@NotNull LiteralNullExpression node);

  @NotNull
  LiteralNumericExpressionState reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node);

  @NotNull
  LiteralRegExpExpressionState reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node);

  @NotNull
  LiteralStringExpressionState reduceLiteralStringExpression(@NotNull LiteralStringExpression node);

  @NotNull
  MethodState reduceMethod(
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
  NewExpressionState reduceNewExpression(
    @NotNull NewExpression node,
    @NotNull ExpressionState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  NewTargetExpressionState reduceNewTargetExpression(@NotNull NewTargetExpression node);

  @NotNull
  ObjectBindingState reduceObjectBinding(
    @NotNull ObjectBinding node,
    @NotNull ImmutableList<BindingPropertyState> properties);

  @NotNull
  ObjectExpressionState reduceObjectExpression(
    @NotNull ObjectExpression node,
    @NotNull ImmutableList<ObjectPropertyState> properties);

  @NotNull
  ReturnStatementState reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<ExpressionState> expression);

  @NotNull
  ScriptState reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  SetterState reduceSetter(
    @NotNull Setter node,
    @NotNull BindingBindingWithDefaultState params,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  ShorthandPropertyState reduceShorthandProperty(@NotNull ShorthandProperty node);

  @NotNull
  SpreadElementState reduceSpreadElement(
    @NotNull SpreadElement node,
    @NotNull ExpressionState expression);

  @NotNull
  StaticMemberExpressionState reduceStaticMemberExpression(
    @NotNull StaticMemberExpression node,
    @NotNull ExpressionSuperState object);

  @NotNull
  StaticPropertyNameState reduceStaticPropertyName(@NotNull StaticPropertyName node);

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
  SwitchStatementState reduceSwitchStatement(
    @NotNull SwitchStatement node,
    @NotNull ExpressionState discriminant,
    @NotNull ImmutableList<SwitchCaseState> cases);

  @NotNull
  SwitchStatementWithDefaultState reduceSwitchStatementWithDefault(
    @NotNull SwitchStatementWithDefault node,
    @NotNull ExpressionState discriminant,
    @NotNull ImmutableList<SwitchCaseState> preDefaultCases,
    @NotNull SwitchDefaultState defaultCase,
    @NotNull ImmutableList<SwitchCaseState> postDefaultCases);

  @NotNull
  TemplateElementState reduceTemplateElement(@NotNull TemplateElement node);

  @NotNull
  TemplateExpressionState reduceTemplateExpression(
    @NotNull TemplateExpression node,
    @NotNull Maybe<ExpressionState> tag,
    @NotNull ImmutableList<ExpressionTemplateElementState> elements);

  @NotNull
  ThisExpressionState reduceThisExpression(@NotNull ThisExpression node);

  @NotNull
  ThrowStatementState reduceThrowStatement(
    @NotNull ThrowStatement node,
    @NotNull ExpressionState expression);

  @NotNull
  TryCatchStatementState reduceTryCatchStatement(
    @NotNull TryCatchStatement node,
    @NotNull BlockState block,
    @NotNull CatchClauseState catchClause);

  @NotNull
  TryFinallyStatementState reduceTryFinallyStatement(
    @NotNull TryFinallyStatement node,
    @NotNull BlockState block,
    @NotNull Maybe<CatchClauseState> catchClause,
    @NotNull BlockState finalizer);

  @NotNull
  UnaryExpressionState reduceUnaryExpression(
    @NotNull UnaryExpression node,
    @NotNull ExpressionState operand);

  @NotNull
  UpdateExpressionState reduceUpdateExpression(
    @NotNull UpdateExpression node,
    @NotNull BindingIdentifierMemberExpressionState operand);

  @NotNull
  VariableDeclarationState reduceVariableDeclaration(
    @NotNull VariableDeclaration node,
    @NotNull ImmutableList<VariableDeclaratorState> declarators);

  @NotNull
  VariableDeclarationStatementState reduceVariableDeclarationStatement(
    @NotNull VariableDeclarationStatement node,
    @NotNull VariableDeclarationState declaration);

  @NotNull
  VariableDeclaratorState reduceVariableDeclarator(
    @NotNull VariableDeclarator node,
    @NotNull BindingState binding,
    @NotNull Maybe<ExpressionState> init);

  @NotNull
  WhileStatementState reduceWhileStatement(
    @NotNull WhileStatement node,
    @NotNull ExpressionState test,
    @NotNull StatementState body);

  @NotNull
  WithStatementState reduceWithStatement(
    @NotNull WithStatement node,
    @NotNull ExpressionState object,
    @NotNull StatementState body);

  @NotNull
  YieldExpressionState reduceYieldExpression(
    @NotNull YieldExpression node,
    @NotNull Maybe<ExpressionState> expression);

  @NotNull
  YieldGeneratorExpressionState reduceYieldGeneratorExpression(
    @NotNull YieldGeneratorExpression node,
    @NotNull ExpressionState expression);
}