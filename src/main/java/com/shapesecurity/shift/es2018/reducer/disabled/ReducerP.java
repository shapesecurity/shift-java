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
//package com.shapesecurity.shift.visitor.disabled;
//
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.*;
//import javax.annotation.Nonnull;
//
//public interface ReducerP<
//  ArrayBindingState,
//  ArrayExpressionState,
//  ArrowExpressionState,
//  AssignmentExpressionState,
//  BinaryExpressionState,
//  BindingIdentifierState,
//  BindingPropertyIdentifierState,
//  BindingPropertyPropertyState,
//  BindingWithDefaultState,
//  BlockState,
//  BlockStatementState,
//  BreakStatementState,
//  CallExpressionState,
//  CatchClauseState,
//  ClassDeclarationState,
//  ClassElementState,
//  ClassExpressionState,
//  CompoundAssignmentExpressionState,
//  ComputedMemberExpressionState,
//  ComputedPropertyNameState,
//  ConditionalExpressionState,
//  ContinueStatementState,
//  DataPropertyState,
//  DebuggerStatementState,
//  DirectiveState,
//  DoWhileStatementState,
//  EmptyStatementState,
//  ExportState,
//  ExportAllFromState,
//  ExportDeclarationState,
//  ExportDefaultState,
//  ExportFromState,
//  ExportSpecifierState,
//  ExpressionStatementState,
//  ForInStatementState,
//  ForOfStatementState,
//  ForStatementState,
//  FormalParametersState,
//  FunctionBodyState,
//  FunctionDeclarationState,
//  FunctionExpressionState,
//  GetterState,
//  IdentifierExpressionState,
//  IfStatementState,
//  ImportState,
//  ImportDeclarationState,
//  ImportNamespaceState,
//  ImportSpecifierState,
//  LabeledStatementState,
//  LiteralBooleanExpressionState,
//  LiteralInfinityExpressionState,
//  LiteralNullExpressionState,
//  LiteralNumericExpressionState,
//  LiteralRegExpExpressionState,
//  LiteralStringExpressionState,
//  MethodState,
//  ModuleState,
//  NewExpressionState,
//  NewTargetExpressionState,
//  ObjectBindingState,
//  ObjectExpressionState,
//  ReturnStatementState,
//  ScriptState,
//  SetterState,
//  ShorthandPropertyState,
//  SpreadElementState,
//  StatementState,
//  StaticMemberExpressionState,
//  StaticPropertyNameState,
//  SuperState,
//  SwitchCaseState,
//  SwitchDefaultState,
//  SwitchStatementState,
//  SwitchStatementWithDefaultState,
//  TemplateElementState,
//  TemplateExpressionState,
//  ThisExpressionState,
//  ThrowStatementState,
//  TryCatchStatementState,
//  TryFinallyStatementState,
//  UnaryExpressionState,
//  UpdateExpressionState,
//  VariableDeclarationState,
//  VariableDeclarationStatementState,
//  VariableDeclaratorState,
//  WhileStatementState,
//  WithStatementState,
//  YieldExpressionState,
//  YieldGeneratorExpressionState
//  > {
//
//  @Nonnull
//  ArrayBindingState reduceArrayBinding(
//    @Nonnull ArrayBinding node,
//    @Nonnull ImmutableList<Maybe<BindingWithDefaultState>> elements,
//    @Nonnull Maybe<BindingState> restElement);
//
//  @Nonnull
//  ArrayExpressionState reduceArrayExpression(
//    @Nonnull ArrayExpression node,
//    @Nonnull ImmutableList<Maybe<SpreadElementExpressionState>> elements);
//
//  @Nonnull
//  ArrowExpressionState reduceArrowExpression(
//    @Nonnull ArrowExpression node,
//    @Nonnull FormalParametersState params,
//    @Nonnull FunctionBodyExpressionState body);
//
//  @Nonnull
//  AssignmentExpressionState reduceAssignmentExpression(
//    @Nonnull AssignmentExpression node,
//    @Nonnull BindingState binding,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  BinaryExpressionState reduceBinaryExpression(
//    @Nonnull BinaryExpression node,
//    @Nonnull ExpressionState left,
//    @Nonnull ExpressionState right);
//
//  @Nonnull
//  BindingIdentifierState reduceBindingIdentifier(
//    @Nonnull BindingIdentifier node);
//
//  @Nonnull
//  BindingPropertyIdentifierState reduceBindingPropertyIdentifier(
//    @Nonnull BindingPropertyIdentifier node,
//    @Nonnull BindingIdentifierState binding,
//    @Nonnull Maybe<ExpressionState> init);
//
//  @Nonnull
//  BindingPropertyPropertyState reduceBindingPropertyProperty(
//    @Nonnull BindingPropertyProperty node,
//    @Nonnull PropertyNameState name,
//    @Nonnull BindingBindingWithDefaultState binding);
//
//  @Nonnull
//  BindingWithDefaultState reduceBindingWithDefault(
//    @Nonnull BindingWithDefault node,
//    @Nonnull BindingState binding,
//    @Nonnull ExpressionState init);
//
//  @Nonnull
//  BlockState reduceBlock(
//    @Nonnull Block node,
//    @Nonnull ImmutableList<StatementState> statements);
//
//  @Nonnull
//  BlockStatementState reduceBlockStatement(
//    @Nonnull BlockStatement node,
//    @Nonnull BlockState block);
//
//  @Nonnull
//  BreakStatementState reduceBreakStatement(@Nonnull BreakStatement node);
//
//  @Nonnull
//  CallExpressionState reduceCallExpression(
//    @Nonnull CallExpression node,
//    @Nonnull ExpressionSuperState callee,
//    @Nonnull ImmutableList<SpreadElementExpressionState> arguments);
//
//  @Nonnull
//  CatchClauseState reduceCatchClause(
//    @Nonnull CatchClause node,
//    @Nonnull BindingState binding,
//    @Nonnull BlockState body);
//
//  @Nonnull
//  ClassDeclarationState reduceClassDeclaration(
//    @Nonnull ClassDeclaration node,
//    @Nonnull BindingIdentifierState name,
//    @Nonnull Maybe<ExpressionState> _super,
//    @Nonnull ImmutableList<ClassElementState> elements);
//
//  @Nonnull
//  ClassElementState reduceClassElement(
//    @Nonnull ClassElement node,
//    @Nonnull MethodDefinitionState method);
//
//  @Nonnull
//  ClassExpressionState reduceClassExpression(
//    @Nonnull ClassExpression node,
//    @Nonnull Maybe<BindingIdentifierState> name,
//    @Nonnull Maybe<ExpressionState> _super,
//    @Nonnull ImmutableList<ClassElementState> elements);
//
//  @Nonnull
//  CompoundAssignmentExpressionState reduceCompoundAssignmentExpression(
//    @Nonnull CompoundAssignmentExpression node,
//    @Nonnull BindingIdentifierMemberExpressionState binding,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  ComputedMemberExpressionState reduceComputedMemberExpression(
//    @Nonnull ComputedMemberExpression node,
//    @Nonnull ExpressionSuperState object,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  ComputedPropertyNameState reduceComputedPropertyName(
//    @Nonnull ComputedPropertyName node,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  ConditionalExpressionState reduceConditionalExpression(
//    @Nonnull ConditionalExpression node,
//    @Nonnull ExpressionState test,
//    @Nonnull ExpressionState consequent,
//    @Nonnull ExpressionState alternate);
//
//  @Nonnull
//  ContinueStatementState reduceContinueStatement(@Nonnull ContinueStatement node);
//
//  @Nonnull
//  DataPropertyState reduceDataProperty(
//    @Nonnull DataProperty node,
//    @Nonnull ExpressionState value,
//    @Nonnull PropertyNameState name);
//
//  @Nonnull
//  DebuggerStatementState reduceDebuggerStatement(@Nonnull DebuggerStatement node);
//
//  @Nonnull
//  DirectiveState reduceDirective(@Nonnull Directive node);
//
//  @Nonnull
//  DoWhileStatementState reduceDoWhileStatement(
//    @Nonnull DoWhileStatement node,
//    @Nonnull StatementState body,
//    @Nonnull ExpressionState test);
//
//  @Nonnull
//  EmptyStatementState reduceEmptyStatement(@Nonnull EmptyStatement node);
//
//  @Nonnull
//  ExportState reduceExport(
//    @Nonnull Export node,
//    @Nonnull FunctionDeclarationClassDeclarationVariableDeclarationState declaration);
//
//  @Nonnull
//  ExportAllFromState reduceExportAllFrom(@Nonnull ExportAllFrom node);
//
//  @Nonnull
//  ExportDeclarationState reduceExportDeclaration(@Nonnull ExportDeclaration node);
//
//  @Nonnull
//  ExportDefaultState reduceExportDefault(
//    @Nonnull ExportDefault node,
//    @Nonnull FunctionDeclarationClassDeclarationExpressionState body);
//
//  @Nonnull
//  ExportFromState reduceExportFrom(
//    @Nonnull ExportFrom node,
//    @Nonnull ImmutableList<ExportSpecifierState> namedExports);
//
//  @Nonnull
//  ExportSpecifierState reduceExportSpecifier(@Nonnull ExportSpecifier node);
//
//  @Nonnull
//  ExpressionStatementState reduceExpressionStatement(
//    @Nonnull ExpressionStatement node,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  ForInStatementState reduceForInStatement(
//    @Nonnull ForInStatement node,
//    @Nonnull VariableDeclarationBindingState left,
//    @Nonnull ExpressionState right,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  ForOfStatementState reduceForOfStatement(
//    @Nonnull ForOfStatement node,
//    @Nonnull VariableDeclarationBindingState left,
//    @Nonnull ExpressionState right,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  ForStatementState reduceForStatement(
//    @Nonnull ForStatement node,
//    @Nonnull Maybe<VariableDeclarationExpressionState> init,
//    @Nonnull Maybe<ExpressionState> test,
//    @Nonnull Maybe<ExpressionState> update,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  FormalParametersState reduceFormalParameters(
//    @Nonnull FormalParameters node,
//    @Nonnull ImmutableList<BindingBindingWithDefaultState> items,
//    @Nonnull Maybe<BindingIdentifierState> rest);
//
//  @Nonnull
//  FunctionBodyState reduceFunctionBody(
//    @Nonnull FunctionBody node,
//    @Nonnull ImmutableList<DirectiveState> directives,
//    @Nonnull ImmutableList<StatementState> statements);
//
//  @Nonnull
//  FunctionDeclarationState reduceFunctionDeclaration(
//    @Nonnull FunctionDeclaration node,
//    @Nonnull BindingIdentifierState name,
//    @Nonnull FormalParametersState params,
//    @Nonnull FunctionBodyState body);
//
//  @Nonnull
//  FunctionExpressionState reduceFunctionExpression(
//    @Nonnull FunctionExpression node,
//    @Nonnull Maybe<BindingIdentifierState> name,
//    @Nonnull FormalParametersState parameters,
//    @Nonnull FunctionBodyState body);
//
//  @Nonnull
//  GetterState reduceGetter(
//    @Nonnull Getter node,
//    @Nonnull FunctionBodyState body,
//    @Nonnull PropertyNameState name);
//
//  @Nonnull
//  IdentifierExpressionState reduceIdentifierExpression(@Nonnull IdentifierExpression node);
//
//  @Nonnull
//  IfStatementState reduceIfStatement(
//    @Nonnull IfStatement node,
//    @Nonnull ExpressionState test,
//    @Nonnull StatementState consequent,
//    @Nonnull Maybe<StatementState> alternate);
//
//  @Nonnull
//  ImportState reduceImport(
//    @Nonnull Import node,
//    @Nonnull Maybe<BindingIdentifierState> defaultBinding,
//    @Nonnull ImmutableList<ImportSpecifierState> namedImports);
//
//  @Nonnull
//  ImportDeclarationState reduceImportDeclaration(@Nonnull ImportDeclaration node);
//
//  @Nonnull
//  ImportNamespaceState reduceImportNamespace(
//    @Nonnull ImportNamespace node,
//    @Nonnull Maybe<BindingIdentifierState> defaultBinding,
//    @Nonnull BindingIdentifierState namespaceBinding);
//
//  @Nonnull
//  ImportSpecifierState reduceImportSpecifier(
//    @Nonnull ImportSpecifier node,
//    @Nonnull BindingIdentifierState binding);
//
//  @Nonnull
//  LabeledStatementState reduceLabeledStatement(
//    @Nonnull LabeledStatement node,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  LiteralBooleanExpressionState reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node);
//
//  @Nonnull
//  LiteralInfinityExpressionState reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node);
//
//  @Nonnull
//  LiteralNullExpressionState reduceLiteralNullExpression(@Nonnull LiteralNullExpression node);
//
//  @Nonnull
//  LiteralNumericExpressionState reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node);
//
//  @Nonnull
//  LiteralRegExpExpressionState reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node);
//
//  @Nonnull
//  LiteralStringExpressionState reduceLiteralStringExpression(@Nonnull LiteralStringExpression node);
//
//  @Nonnull
//  MethodState reduceMethod(
//    @Nonnull Method node,
//    @Nonnull FormalParametersState params,
//    @Nonnull FunctionBodyState body,
//    @Nonnull PropertyNameState name);
//
//  @Nonnull
//  ModuleState reduceModule(
//    @Nonnull Module node,
//    @Nonnull ImmutableList<DirectiveState> directives,
//    @Nonnull ImmutableList<ImportDeclarationExportDeclarationStatementState> items);
//
//  @Nonnull
//  NewExpressionState reduceNewExpression(
//    @Nonnull NewExpression node,
//    @Nonnull ExpressionState callee,
//    @Nonnull ImmutableList<SpreadElementExpressionState> arguments);
//
//  @Nonnull
//  NewTargetExpressionState reduceNewTargetExpression(@Nonnull NewTargetExpression node);
//
//  @Nonnull
//  ObjectBindingState reduceObjectBinding(
//    @Nonnull ObjectBinding node,
//    @Nonnull ImmutableList<BindingPropertyState> properties);
//
//  @Nonnull
//  ObjectExpressionState reduceObjectExpression(
//    @Nonnull ObjectExpression node,
//    @Nonnull ImmutableList<ObjectPropertyState> properties);
//
//  @Nonnull
//  ReturnStatementState reduceReturnStatement(
//    @Nonnull ReturnStatement node,
//    @Nonnull Maybe<ExpressionState> expression);
//
//  @Nonnull
//  ScriptState reduceScript(
//    @Nonnull Script node,
//    @Nonnull ImmutableList<DirectiveState> directives,
//    @Nonnull ImmutableList<StatementState> statements);
//
//  @Nonnull
//  SetterState reduceSetter(
//    @Nonnull Setter node,
//    @Nonnull BindingBindingWithDefaultState params,
//    @Nonnull FunctionBodyState body,
//    @Nonnull PropertyNameState name);
//
//  @Nonnull
//  ShorthandPropertyState reduceShorthandProperty(@Nonnull ShorthandProperty node);
//
//  @Nonnull
//  SpreadElementState reduceSpreadElement(
//    @Nonnull SpreadElement node,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  StaticMemberExpressionState reduceStaticMemberExpression(
//    @Nonnull StaticMemberExpression node,
//    @Nonnull ExpressionSuperState object);
//
//  @Nonnull
//  StaticPropertyNameState reduceStaticPropertyName(@Nonnull StaticPropertyName node);
//
//  @Nonnull
//  SuperState reduceSuper(@Nonnull Super node);
//
//  @Nonnull
//  SwitchCaseState reduceSwitchCase(
//    @Nonnull SwitchCase node,
//    @Nonnull ExpressionState test,
//    @Nonnull ImmutableList<StatementState> consequent);
//
//  @Nonnull
//  SwitchDefaultState reduceSwitchDefault(
//    @Nonnull SwitchDefault node,
//    @Nonnull ImmutableList<StatementState> consequent);
//
//  @Nonnull
//  SwitchStatementState reduceSwitchStatement(
//    @Nonnull SwitchStatement node,
//    @Nonnull ExpressionState discriminant,
//    @Nonnull ImmutableList<SwitchCaseState> cases);
//
//  @Nonnull
//  SwitchStatementWithDefaultState reduceSwitchStatementWithDefault(
//    @Nonnull SwitchStatementWithDefault node,
//    @Nonnull ExpressionState discriminant,
//    @Nonnull ImmutableList<SwitchCaseState> preDefaultCases,
//    @Nonnull SwitchDefaultState defaultCase,
//    @Nonnull ImmutableList<SwitchCaseState> postDefaultCases);
//
//  @Nonnull
//  TemplateElementState reduceTemplateElement(@Nonnull TemplateElement node);
//
//  @Nonnull
//  TemplateExpressionState reduceTemplateExpression(
//    @Nonnull TemplateExpression node,
//    @Nonnull Maybe<ExpressionState> tag,
//    @Nonnull ImmutableList<ExpressionTemplateElementState> elements);
//
//  @Nonnull
//  ThisExpressionState reduceThisExpression(@Nonnull ThisExpression node);
//
//  @Nonnull
//  ThrowStatementState reduceThrowStatement(
//    @Nonnull ThrowStatement node,
//    @Nonnull ExpressionState expression);
//
//  @Nonnull
//  TryCatchStatementState reduceTryCatchStatement(
//    @Nonnull TryCatchStatement node,
//    @Nonnull BlockState block,
//    @Nonnull CatchClauseState catchClause);
//
//  @Nonnull
//  TryFinallyStatementState reduceTryFinallyStatement(
//    @Nonnull TryFinallyStatement node,
//    @Nonnull BlockState block,
//    @Nonnull Maybe<CatchClauseState> catchClause,
//    @Nonnull BlockState finalizer);
//
//  @Nonnull
//  UnaryExpressionState reduceUnaryExpression(
//    @Nonnull UnaryExpression node,
//    @Nonnull ExpressionState operand);
//
//  @Nonnull
//  UpdateExpressionState reduceUpdateExpression(
//    @Nonnull UpdateExpression node,
//    @Nonnull BindingIdentifierMemberExpressionState operand);
//
//  @Nonnull
//  VariableDeclarationState reduceVariableDeclaration(
//    @Nonnull VariableDeclaration node,
//    @Nonnull ImmutableList<VariableDeclaratorState> declarators);
//
//  @Nonnull
//  VariableDeclarationStatementState reduceVariableDeclarationStatement(
//    @Nonnull VariableDeclarationStatement node,
//    @Nonnull VariableDeclarationState declaration);
//
//  @Nonnull
//  VariableDeclaratorState reduceVariableDeclarator(
//    @Nonnull VariableDeclarator node,
//    @Nonnull BindingState binding,
//    @Nonnull Maybe<ExpressionState> init);
//
//  @Nonnull
//  WhileStatementState reduceWhileStatement(
//    @Nonnull WhileStatement node,
//    @Nonnull ExpressionState test,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  WithStatementState reduceWithStatement(
//    @Nonnull WithStatement node,
//    @Nonnull ExpressionState object,
//    @Nonnull StatementState body);
//
//  @Nonnull
//  YieldExpressionState reduceYieldExpression(
//    @Nonnull YieldExpression node,
//    @Nonnull Maybe<ExpressionState> expression);
//
//  @Nonnull
//  YieldGeneratorExpressionState reduceYieldGeneratorExpression(
//    @Nonnull YieldGeneratorExpression node,
//    @Nonnull ExpressionState expression);
//}
