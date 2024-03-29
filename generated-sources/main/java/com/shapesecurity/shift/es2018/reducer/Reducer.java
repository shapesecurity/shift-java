// Generated by reducer.js
/**
 * Copyright 2018 Shape Security, Inc.
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


package com.shapesecurity.shift.es2018.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.*;
import com.shapesecurity.shift.es2018.ast.Module;

import javax.annotation.Nonnull;

public interface Reducer<State> {
    @Nonnull
    State reduceArrayAssignmentTarget(
            @Nonnull ArrayAssignmentTarget node,
            @Nonnull ImmutableList<Maybe<State>> elements,
            @Nonnull Maybe<State> rest);

    @Nonnull
    State reduceArrayBinding(
            @Nonnull ArrayBinding node,
            @Nonnull ImmutableList<Maybe<State>> elements,
            @Nonnull Maybe<State> rest);

    @Nonnull
    State reduceArrayExpression(
            @Nonnull ArrayExpression node,
            @Nonnull ImmutableList<Maybe<State>> elements);

    @Nonnull
    State reduceArrowExpression(
            @Nonnull ArrowExpression node,
            @Nonnull State params,
            @Nonnull State body);

    @Nonnull
    State reduceAssignmentExpression(
            @Nonnull AssignmentExpression node,
            @Nonnull State binding,
            @Nonnull State expression);

    @Nonnull
    State reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node);

    @Nonnull
    State reduceAssignmentTargetPropertyIdentifier(
            @Nonnull AssignmentTargetPropertyIdentifier node,
            @Nonnull State binding,
            @Nonnull Maybe<State> init);

    @Nonnull
    State reduceAssignmentTargetPropertyProperty(
            @Nonnull AssignmentTargetPropertyProperty node,
            @Nonnull State name,
            @Nonnull State binding);

    @Nonnull
    State reduceAssignmentTargetWithDefault(
            @Nonnull AssignmentTargetWithDefault node,
            @Nonnull State binding,
            @Nonnull State init);

    @Nonnull
    State reduceAwaitExpression(
            @Nonnull AwaitExpression node,
            @Nonnull State expression);

    @Nonnull
    State reduceBinaryExpression(
            @Nonnull BinaryExpression node,
            @Nonnull State left,
            @Nonnull State right);

    @Nonnull
    State reduceBindingIdentifier(@Nonnull BindingIdentifier node);

    @Nonnull
    State reduceBindingPropertyIdentifier(
            @Nonnull BindingPropertyIdentifier node,
            @Nonnull State binding,
            @Nonnull Maybe<State> init);

    @Nonnull
    State reduceBindingPropertyProperty(
            @Nonnull BindingPropertyProperty node,
            @Nonnull State name,
            @Nonnull State binding);

    @Nonnull
    State reduceBindingWithDefault(
            @Nonnull BindingWithDefault node,
            @Nonnull State binding,
            @Nonnull State init);

    @Nonnull
    State reduceBlock(
            @Nonnull Block node,
            @Nonnull ImmutableList<State> statements);

    @Nonnull
    State reduceBlockStatement(
            @Nonnull BlockStatement node,
            @Nonnull State block);

    @Nonnull
    State reduceBreakStatement(@Nonnull BreakStatement node);

    @Nonnull
    State reduceCallExpression(
            @Nonnull CallExpression node,
            @Nonnull State callee,
            @Nonnull ImmutableList<State> arguments);

    @Nonnull
    State reduceCatchClause(
            @Nonnull CatchClause node,
            @Nonnull State binding,
            @Nonnull State body);

    @Nonnull
    State reduceClassDeclaration(
            @Nonnull ClassDeclaration node,
            @Nonnull State name,
            @Nonnull Maybe<State> _super,
            @Nonnull ImmutableList<State> elements);

    @Nonnull
    State reduceClassElement(
            @Nonnull ClassElement node,
            @Nonnull State method);

    @Nonnull
    State reduceClassExpression(
            @Nonnull ClassExpression node,
            @Nonnull Maybe<State> name,
            @Nonnull Maybe<State> _super,
            @Nonnull ImmutableList<State> elements);

    @Nonnull
    State reduceCompoundAssignmentExpression(
            @Nonnull CompoundAssignmentExpression node,
            @Nonnull State binding,
            @Nonnull State expression);

    @Nonnull
    State reduceComputedMemberAssignmentTarget(
            @Nonnull ComputedMemberAssignmentTarget node,
            @Nonnull State object,
            @Nonnull State expression);

    @Nonnull
    State reduceComputedMemberExpression(
            @Nonnull ComputedMemberExpression node,
            @Nonnull State object,
            @Nonnull State expression);

    @Nonnull
    State reduceComputedPropertyName(
            @Nonnull ComputedPropertyName node,
            @Nonnull State expression);

    @Nonnull
    State reduceConditionalExpression(
            @Nonnull ConditionalExpression node,
            @Nonnull State test,
            @Nonnull State consequent,
            @Nonnull State alternate);

    @Nonnull
    State reduceContinueStatement(@Nonnull ContinueStatement node);

    @Nonnull
    State reduceDataProperty(
            @Nonnull DataProperty node,
            @Nonnull State name,
            @Nonnull State expression);

    @Nonnull
    State reduceDebuggerStatement(@Nonnull DebuggerStatement node);

    @Nonnull
    State reduceDirective(@Nonnull Directive node);

    @Nonnull
    State reduceDoWhileStatement(
            @Nonnull DoWhileStatement node,
            @Nonnull State body,
            @Nonnull State test);

    @Nonnull
    State reduceEmptyStatement(@Nonnull EmptyStatement node);

    @Nonnull
    State reduceExport(
            @Nonnull Export node,
            @Nonnull State declaration);

    @Nonnull
    State reduceExportAllFrom(@Nonnull ExportAllFrom node);

    @Nonnull
    State reduceExportDefault(
            @Nonnull ExportDefault node,
            @Nonnull State body);

    @Nonnull
    State reduceExportFrom(
            @Nonnull ExportFrom node,
            @Nonnull ImmutableList<State> namedExports);

    @Nonnull
    State reduceExportFromSpecifier(@Nonnull ExportFromSpecifier node);

    @Nonnull
    State reduceExportLocalSpecifier(
            @Nonnull ExportLocalSpecifier node,
            @Nonnull State name);

    @Nonnull
    State reduceExportLocals(
            @Nonnull ExportLocals node,
            @Nonnull ImmutableList<State> namedExports);

    @Nonnull
    State reduceExpressionStatement(
            @Nonnull ExpressionStatement node,
            @Nonnull State expression);

    @Nonnull
    State reduceForAwaitStatement(
            @Nonnull ForAwaitStatement node,
            @Nonnull State left,
            @Nonnull State right,
            @Nonnull State body);

    @Nonnull
    State reduceForInStatement(
            @Nonnull ForInStatement node,
            @Nonnull State left,
            @Nonnull State right,
            @Nonnull State body);

    @Nonnull
    State reduceForOfStatement(
            @Nonnull ForOfStatement node,
            @Nonnull State left,
            @Nonnull State right,
            @Nonnull State body);

    @Nonnull
    State reduceForStatement(
            @Nonnull ForStatement node,
            @Nonnull Maybe<State> init,
            @Nonnull Maybe<State> test,
            @Nonnull Maybe<State> update,
            @Nonnull State body);

    @Nonnull
    State reduceFormalParameters(
            @Nonnull FormalParameters node,
            @Nonnull ImmutableList<State> items,
            @Nonnull Maybe<State> rest);

    @Nonnull
    State reduceFunctionBody(
            @Nonnull FunctionBody node,
            @Nonnull ImmutableList<State> directives,
            @Nonnull ImmutableList<State> statements);

    @Nonnull
    State reduceFunctionDeclaration(
            @Nonnull FunctionDeclaration node,
            @Nonnull State name,
            @Nonnull State params,
            @Nonnull State body);

    @Nonnull
    State reduceFunctionExpression(
            @Nonnull FunctionExpression node,
            @Nonnull Maybe<State> name,
            @Nonnull State params,
            @Nonnull State body);

    @Nonnull
    State reduceGetter(
            @Nonnull Getter node,
            @Nonnull State name,
            @Nonnull State body);

    @Nonnull
    State reduceIdentifierExpression(@Nonnull IdentifierExpression node);

    @Nonnull
    State reduceIfStatement(
            @Nonnull IfStatement node,
            @Nonnull State test,
            @Nonnull State consequent,
            @Nonnull Maybe<State> alternate);

    @Nonnull
    State reduceImport(
            @Nonnull Import node,
            @Nonnull Maybe<State> defaultBinding,
            @Nonnull ImmutableList<State> namedImports);

    @Nonnull
    State reduceImportNamespace(
            @Nonnull ImportNamespace node,
            @Nonnull Maybe<State> defaultBinding,
            @Nonnull State namespaceBinding);

    @Nonnull
    State reduceImportSpecifier(
            @Nonnull ImportSpecifier node,
            @Nonnull State binding);

    @Nonnull
    State reduceLabeledStatement(
            @Nonnull LabeledStatement node,
            @Nonnull State body);

    @Nonnull
    State reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node);

    @Nonnull
    State reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node);

    @Nonnull
    State reduceLiteralNullExpression(@Nonnull LiteralNullExpression node);

    @Nonnull
    State reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node);

    @Nonnull
    State reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node);

    @Nonnull
    State reduceLiteralStringExpression(@Nonnull LiteralStringExpression node);

    @Nonnull
    State reduceMethod(
            @Nonnull Method node,
            @Nonnull State name,
            @Nonnull State params,
            @Nonnull State body);

    @Nonnull
    State reduceModule(
            @Nonnull Module node,
            @Nonnull ImmutableList<State> directives,
            @Nonnull ImmutableList<State> items);

    @Nonnull
    State reduceNewExpression(
            @Nonnull NewExpression node,
            @Nonnull State callee,
            @Nonnull ImmutableList<State> arguments);

    @Nonnull
    State reduceNewTargetExpression(@Nonnull NewTargetExpression node);

    @Nonnull
    State reduceObjectAssignmentTarget(
            @Nonnull ObjectAssignmentTarget node,
            @Nonnull ImmutableList<State> properties,
            @Nonnull Maybe<State> rest);

    @Nonnull
    State reduceObjectBinding(
            @Nonnull ObjectBinding node,
            @Nonnull ImmutableList<State> properties,
            @Nonnull Maybe<State> rest);

    @Nonnull
    State reduceObjectExpression(
            @Nonnull ObjectExpression node,
            @Nonnull ImmutableList<State> properties);

    @Nonnull
    State reduceReturnStatement(
            @Nonnull ReturnStatement node,
            @Nonnull Maybe<State> expression);

    @Nonnull
    State reduceScript(
            @Nonnull Script node,
            @Nonnull ImmutableList<State> directives,
            @Nonnull ImmutableList<State> statements);

    @Nonnull
    State reduceSetter(
            @Nonnull Setter node,
            @Nonnull State name,
            @Nonnull State param,
            @Nonnull State body);

    @Nonnull
    State reduceShorthandProperty(
            @Nonnull ShorthandProperty node,
            @Nonnull State name);

    @Nonnull
    State reduceSpreadElement(
            @Nonnull SpreadElement node,
            @Nonnull State expression);

    @Nonnull
    State reduceSpreadProperty(
            @Nonnull SpreadProperty node,
            @Nonnull State expression);

    @Nonnull
    State reduceStaticMemberAssignmentTarget(
            @Nonnull StaticMemberAssignmentTarget node,
            @Nonnull State object);

    @Nonnull
    State reduceStaticMemberExpression(
            @Nonnull StaticMemberExpression node,
            @Nonnull State object);

    @Nonnull
    State reduceStaticPropertyName(@Nonnull StaticPropertyName node);

    @Nonnull
    State reduceSuper(@Nonnull Super node);

    @Nonnull
    State reduceSwitchCase(
            @Nonnull SwitchCase node,
            @Nonnull State test,
            @Nonnull ImmutableList<State> consequent);

    @Nonnull
    State reduceSwitchDefault(
            @Nonnull SwitchDefault node,
            @Nonnull ImmutableList<State> consequent);

    @Nonnull
    State reduceSwitchStatement(
            @Nonnull SwitchStatement node,
            @Nonnull State discriminant,
            @Nonnull ImmutableList<State> cases);

    @Nonnull
    State reduceSwitchStatementWithDefault(
            @Nonnull SwitchStatementWithDefault node,
            @Nonnull State discriminant,
            @Nonnull ImmutableList<State> preDefaultCases,
            @Nonnull State defaultCase,
            @Nonnull ImmutableList<State> postDefaultCases);

    @Nonnull
    State reduceTemplateElement(@Nonnull TemplateElement node);

    @Nonnull
    State reduceTemplateExpression(
            @Nonnull TemplateExpression node,
            @Nonnull Maybe<State> tag,
            @Nonnull ImmutableList<State> elements);

    @Nonnull
    State reduceThisExpression(@Nonnull ThisExpression node);

    @Nonnull
    State reduceThrowStatement(
            @Nonnull ThrowStatement node,
            @Nonnull State expression);

    @Nonnull
    State reduceTryCatchStatement(
            @Nonnull TryCatchStatement node,
            @Nonnull State body,
            @Nonnull State catchClause);

    @Nonnull
    State reduceTryFinallyStatement(
            @Nonnull TryFinallyStatement node,
            @Nonnull State body,
            @Nonnull Maybe<State> catchClause,
            @Nonnull State finalizer);

    @Nonnull
    State reduceUnaryExpression(
            @Nonnull UnaryExpression node,
            @Nonnull State operand);

    @Nonnull
    State reduceUpdateExpression(
            @Nonnull UpdateExpression node,
            @Nonnull State operand);

    @Nonnull
    State reduceVariableDeclaration(
            @Nonnull VariableDeclaration node,
            @Nonnull ImmutableList<State> declarators);

    @Nonnull
    State reduceVariableDeclarationStatement(
            @Nonnull VariableDeclarationStatement node,
            @Nonnull State declaration);

    @Nonnull
    State reduceVariableDeclarator(
            @Nonnull VariableDeclarator node,
            @Nonnull State binding,
            @Nonnull Maybe<State> init);

    @Nonnull
    State reduceWhileStatement(
            @Nonnull WhileStatement node,
            @Nonnull State test,
            @Nonnull State body);

    @Nonnull
    State reduceWithStatement(
            @Nonnull WithStatement node,
            @Nonnull State object,
            @Nonnull State body);

    @Nonnull
    State reduceYieldExpression(
            @Nonnull YieldExpression node,
            @Nonnull Maybe<State> expression);

    @Nonnull
    State reduceYieldGeneratorExpression(
            @Nonnull YieldGeneratorExpression node,
            @Nonnull State expression);
}
