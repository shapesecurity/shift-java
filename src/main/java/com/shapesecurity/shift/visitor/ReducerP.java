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

import java.lang.reflect.Array;

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
  PropertyState,
  VariableDeclaratorState,
  FunctionBodyExpressionState,
  BindingPropertyState,
  BindingPatternState,
  BindingPropertyIdentifierState,
  BindingPropertyPropertyState,
  BindingWithDefaultState,
  ClassElementState,
  MethodDefinitionState,
  ExportDeclarationState,
  FunctionDeclarationClassDeclarationVariableDeclarationState,
  FunctionDeclarationClassDeclarationExpressionState,
  ExportSpecifierState,
  ImportDeclarationState,
  ImportSpecifierState,
  ModuleState,
  ImportDeclarationExportDeclarationStatementState,
  NodeState,
  ObjectBindingState,
  SpreadElementState,
  SuperState,
  TemplateState,
  ExpressionTemplateElementState
  > {

  @NotNull
  ScriptState reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  ExpressionState reduceIdentifierExpression(@NotNull IdentifierExpression node);

  @NotNull
  ExpressionState reduceThisExpression(@NotNull ThisExpression node);

  @NotNull
  ExpressionState reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node);

  @NotNull
  ExpressionState reduceLiteralStringExpression(@NotNull LiteralStringExpression node);

  @NotNull
  ExpressionState reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node);

  @NotNull
  ExpressionState reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node);

  @NotNull
  ExpressionState reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node);

  @NotNull
  ExpressionState reduceLiteralNullExpression(@NotNull LiteralNullExpression node);

  @NotNull
  ExpressionState reduceFunctionExpression(
    @NotNull FunctionExpression node,
    @NotNull Maybe<BindingIdentifierState> name,
    @NotNull FormalParametersState parameters,
    @NotNull FunctionBodyState body);

  @NotNull
  ExpressionState reduceStaticMemberExpression(
    @NotNull StaticMemberExpression node,
    @NotNull ExpressionSuperState object);

  @NotNull
  ExpressionState reduceComputedMemberExpression(
    @NotNull ComputedMemberExpression node,
    @NotNull ExpressionSuperState object,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceObjectExpression(
    @NotNull ObjectExpression node,
    @NotNull ImmutableList<ObjectPropertyState> properties);

  @NotNull
  ExpressionState reduceBinaryExpression(
    @NotNull BinaryExpression node,
    @NotNull ExpressionState left,
    @NotNull ExpressionState right);

  @NotNull
  ExpressionState reduceAssignmentExpression(
    @NotNull AssignmentExpression node,
    @NotNull BindingState binding,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceCompoundAssignmentExpression(
    @NotNull CompoundAssignmentExpression node,
    @NotNull BindingIdentifierMemberExpressionState binding,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceArrayExpression(
    @NotNull ArrayExpression node,
    @NotNull ImmutableList<Maybe<SpreadElementExpressionState>> elements);

  @NotNull
  ExpressionState reduceNewExpression(
    @NotNull NewExpression node,
    @NotNull ExpressionState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  ExpressionState reduceCallExpression(
    @NotNull CallExpression node,
    @NotNull ExpressionSuperState callee,
    @NotNull ImmutableList<SpreadElementExpressionState> arguments);

  @NotNull
  ExpressionState reduceConditionalExpression(
    @NotNull ConditionalExpression node,
    @NotNull ExpressionState test,
    @NotNull ExpressionState consequent,
    @NotNull ExpressionState alternate);

  @NotNull
  StatementState reduceFunctionDeclaration(
    @NotNull FunctionDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyState body);

  @NotNull
  StatementState reduceBlockStatement(
    @NotNull BlockStatement node,
    @NotNull BlockState block);

  @NotNull
  StatementState reduceBreakStatement(@NotNull BreakStatement node);

  @NotNull
  CatchClauseState reduceCatchClause(
    @NotNull CatchClause node,
    @NotNull BindingState binding,
    @NotNull BlockState body);

  @NotNull
  StatementState reduceContinueStatement(@NotNull ContinueStatement node);

  @NotNull
  StatementState reduceDebuggerStatement(@NotNull DebuggerStatement node);

  @NotNull
  StatementState reduceDoWhileStatement(
    @NotNull DoWhileStatement node,
    @NotNull StatementState body,
    @NotNull ExpressionState test);

  @NotNull
  StatementState reduceEmptyStatement(@NotNull EmptyStatement node);

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
  StatementState reduceForStatement(
    @NotNull ForStatement node,
    @NotNull Maybe<VariableDeclarationExpressionState> init,
    @NotNull Maybe<ExpressionState> test,
    @NotNull Maybe<ExpressionState> update,
    @NotNull StatementState body);

  @NotNull
  StatementState reduceIfStatement(
    @NotNull IfStatement node,
    @NotNull ExpressionState test,
    @NotNull StatementState consequent,
    @NotNull Maybe<StatementState> alternate);

  @NotNull
  StatementState reduceLabeledStatement(
    @NotNull LabeledStatement node,
    @NotNull StatementState body);

  @NotNull
  StatementState reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<ExpressionState> expression);

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
  StatementState reduceVariableDeclarationStatement(
    @NotNull VariableDeclarationStatement node,
    @NotNull VariableDeclarationState declaration);

  @NotNull
  VariableDeclarationState reduceVariableDeclaration(
    @NotNull VariableDeclaration node,
    @NotNull ImmutableList<VariableDeclarator> declarators);

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
  ObjectPropertyState reduceDataProperty(
    @NotNull DataProperty node,
    @NotNull ExpressionState value,
    @NotNull PropertyNameState name);

  @NotNull
  MethodDefinitionState reduceGetter(
    @NotNull Getter node,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  MethodDefinitionState reduceSetter(
    @NotNull Setter node,
    @NotNull BindingBindingWithDefaultState params,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  PropertyNameState reducePropertyName(@NotNull PropertyName node);

  @NotNull
  FunctionBodyState reduceFunctionBody(
    @NotNull FunctionBody node,
    @NotNull ImmutableList<DirectiveState> directives,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  VariableDeclaratorState reduceVariableDeclarator(
    @NotNull VariableDeclarator node,
    @NotNull BindingState binding,
    @NotNull Maybe<ExpressionState> init);

  @NotNull
  BlockState reduceBlock(
    @NotNull Block node,
    @NotNull ImmutableList<StatementState> statements);

  @NotNull
  BindingState reduceArrayBinding(
    @NotNull ArrayBinding node,
    @NotNull ImmutableList<Maybe<BindingBindingWithDefaultState>> elements,
    @NotNull Maybe<BindingState> restElement);

  @NotNull
  ExpressionState reduceArrowExpression(
    @NotNull ArrowExpression node,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyExpressionState body);

  @NotNull
  BindingIdentifierState reduceBindingIdentifier(
    @NotNull BindingIdentifier node);

  @NotNull
  BindingPatternState reduceBindingPattern(
    @NotNull BindingPattern node);

  @NotNull
  BindingPropertyState reduceBindingProperty(
    @NotNull BindingProperty node);

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
  StatementState reduceClassDeclaration(
    @NotNull ClassDeclaration node,
    @NotNull BindingIdentifierState name,
    @NotNull Maybe<ExpressionState> _super);

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
  PropertyNameState reduceComputedPropertyName(
    @NotNull ComputedPropertyName node,
    @NotNull ExpressionState expression);

  @NotNull
  ExpressionState reduceConditionExpression(
    @NotNull ConditionalExpression node,
    @NotNull ExpressionState test,
    @NotNull ExpressionState consequent,
    @NotNull ExpressionState alternate);

  @NotNull
  DirectiveState reduceDirective(@NotNull Directive node);

  @NotNull
  ExportDeclarationState reduceExport(
    @NotNull Export node,
    @NotNull FunctionDeclarationClassDeclarationVariableDeclarationState declaration);

  @NotNull
  ExportDeclarationState reduceExportAllFrom(@NotNull ExportAllFrom node);

  @NotNull
  ExportDeclarationState reduceExportDeclaration(@NotNull ExportDeclaration node);

  @NotNull
  ExportDeclarationState reduceExportDefault(
    @NotNull ExportDefault node,
    @NotNull FunctionDeclarationClassDeclarationExpressionState body);

  @NotNull
  ExportDeclarationState reduceExportFrom(
    @NotNull ExportFrom node,
    @NotNull ImmutableList<ExportSpecifierState> namedExports);

  @NotNull
  ExportSpecifierState reduceExportSpecifier(@NotNull ExportSpecifier node);

  @NotNull
  ExpressionState reduceExpression(@NotNull Expression node);

  @NotNull
  FormalParametersState reduceFormalParameters(
    @NotNull FormalParameters node,
    @NotNull ImmutableList<BindingBindingWithDefaultState> items,
    @NotNull Maybe<BindingIdentifierState> rest);

  @NotNull
  StatementState reduceForOfStatement(
    @NotNull ForOfStatement node,
    @NotNull VariableDeclarationBindingState left,
    @NotNull ExpressionState right,
    @NotNull StatementState body);

  @NotNull
  ImportDeclarationState reduceImport(
    @NotNull Import node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull ImmutableList<ImportSpecifierState> namedImports);

  @NotNull
  ImportDeclarationState reduceImportDeclaration(@NotNull ImportDeclaration node);

  @NotNull
  ImportDeclarationState reduceImportNamespace(
    @NotNull ImportNamespace node,
    @NotNull Maybe<BindingIdentifierState> defaultBinding,
    @NotNull BindingIdentifierState namespaceBinding);

  @NotNull
  ImportSpecifierState reduceImportSpecifier(
    @NotNull ImportSpecifier node,
    @NotNull BindingIdentifierState binding);

  @NotNull
  StatementState reduceIterationStatement(
    @NotNull IterationStatement node,
    @NotNull StatementState statement);

  @NotNull
  ExpressionState reduceMemberExpression(
    @NotNull MemberExpression node,
    @NotNull ExpressionSuperState object);

  @NotNull
  MethodDefinitionState reduceMethod(
    @NotNull Method node,
    @NotNull FormalParametersState params,
    @NotNull FunctionBodyState body,
    @NotNull PropertyNameState name);

  @NotNull
  MethodDefinitionState reduceMethodDefinition(
    @NotNull MethodDefinition node,
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
  ExpressionState reduceNewTargetExpression(@NotNull NewTargetExpression node);

  @NotNull
  NodeState reduceNode(@NotNull Node node);

  @NotNull
  ObjectBindingState reduceObjectBinding(
    @NotNull ObjectBinding node,
    @NotNull ImmutableList<BindingProperty> properties);

  @NotNull
  ObjectPropertyState reduceObjectProperty(@NotNull ObjectProperty node);

  @NotNull
  ObjectPropertyState reduceShorthandProperty(@NotNull ShorthandProperty node);

  @NotNull
  SpreadElementState reduceSpreadElement(
    @NotNull SpreadElement node,
    @NotNull ExpressionState expression);

  @NotNull
  StatementState reduceStatement(@NotNull Statement statement);

  @NotNull
  PropertyNameState reduceStaticPropertyName(@NotNull StaticPropertyName node);

  @NotNull
  SuperState reduceSuper(@NotNull Super node);

  @NotNull
  TemplateState reduceTemplate(@NotNull TemplateElement node);

  @NotNull
  ExpressionState reduceTemplateExpression(
    @NotNull TemplateExpression node,
    @NotNull Maybe<ExpressionState> tag,
    @NotNull ImmutableList<ExpressionTemplateElementState> elements);

  @NotNull
  ExpressionState reduceUnaryExpression(
    @NotNull UnaryExpression node,
    @NotNull ExpressionState operand);

  @NotNull
  ExpressionState reduceUpdateExpression(
    @NotNull UpdateExpression node,
    @NotNull BindingIdentifierMemberExpressionState operand);

  @NotNull
  ExpressionState reduceYieldExpression(
    @NotNull YieldExpression node,
    @NotNull Maybe<ExpressionState> expression);

  @NotNull
  ExpressionState reduceYieldGeneratorExpression(
    @NotNull YieldGeneratorExpression node,
    @NotNull ExpressionState expression);
}