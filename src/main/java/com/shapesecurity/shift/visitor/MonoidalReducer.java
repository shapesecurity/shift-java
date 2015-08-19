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

    protected MonoidalReducer(@NotNull Monoid<State> monoidClass) {
        this.monoidClass = monoidClass;
    }
    
    private State identity() {
        return this.monoidClass.identity();
    }

    private State append(State a, State b) {
        return this.monoidClass.append(a, b);
    }

    private State append(State a, State b, State c) {
        return append(append(a, b), c);
    }

    private State append(State a, State b, State c, State d) {
        return append(append(a, b, c), d);
    }

    private State fold(ImmutableList<State> as) {
        return as.foldLeft(this::append, this.identity());
    }

    private State fold1(ImmutableList<State> as, State a) {
        return as.foldLeft(this::append, a);
    }

    @NotNull
    private State o(@NotNull Maybe<State> s) {
        return s.orJust(this.identity());
    }

    @NotNull
    @Override
    public State reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<State>> elements, @NotNull Maybe<State> restElement) {
        return append(fold(Maybe.catMaybes(elements)), o(restElement));
    }

    @NotNull
    @Override
    public State reduceArrayExpression(
            @NotNull ArrayExpression node,
            @NotNull ImmutableList<Maybe<State>> elements) {
        return fold(Maybe.catMaybes(elements));
    }

    @NotNull
    @Override
    public State reduceArrowExpression(@NotNull ArrowExpression node, @NotNull State params, @NotNull State body) {
        return append(params, body);
    }

    @NotNull
    @Override
    public State reduceAssignmentExpression(
            @NotNull AssignmentExpression node,
            @NotNull State binding,
            @NotNull State expression) {
        return append(binding, expression);
    }

    @NotNull
    @Override
    public State reduceBinaryExpression(
            @NotNull BinaryExpression node,
            @NotNull State left,
            @NotNull State right) {
        return append(left, right);
    }

    @NotNull
    @Override
    public State reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull State binding, @NotNull Maybe<State> init) {
        return append(binding, o(init));
    }

    @NotNull
    @Override
    public State reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull State name, @NotNull State binding) {
        return append(name, binding);
    }

    @NotNull
    @Override
    public State reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull State binding, @NotNull State init) {
        return append(binding, init);
    }

    @NotNull
    @Override
    public State reduceBlock(@NotNull Block node, @NotNull ImmutableList<State> statements) {
        return fold(statements);
    }

    @NotNull
    @Override
    public State reduceBlockStatement(@NotNull BlockStatement node, @NotNull State block) {
        return block;
    }

    @NotNull
    @Override
    public State reduceBreakStatement(@NotNull BreakStatement node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceCallExpression(
            @NotNull CallExpression node,
            @NotNull State callee,
            @NotNull ImmutableList<State> arguments) {
        return fold1(arguments, callee);
    }

    @NotNull
    @Override
    public State reduceCatchClause(
            @NotNull CatchClause node,
            @NotNull State binding,
            @NotNull State body) {
        return append(binding, body);
    }

    @NotNull
    @Override
    public State reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull State name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        return fold1(elements, append(name, o(_super)));
    }

    @NotNull
    @Override
    public State reduceClassElement(@NotNull ClassElement node, @NotNull State method) {
        return method;
    }

    @NotNull
    @Override
    public State reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<State> name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        return fold1(elements, append(o(name), o(_super)));
    }

    @NotNull
    @Override
    public State reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull State binding, @NotNull State expression) {
        return append(binding, expression);
    }

    @NotNull
    @Override
    public State reduceComputedMemberExpression(
            @NotNull ComputedMemberExpression node,
            @NotNull State expression,
            @NotNull State object) {
        return append(expression, object);
    }

    @NotNull
    @Override
    public State reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull State expression) {
        return expression;
    }

    @NotNull
    @Override
    public State reduceConditionalExpression(
            @NotNull ConditionalExpression node,
            @NotNull State test,
            @NotNull State consequent,
            @NotNull State alternate) {
        return append(test, consequent, alternate);
    }

    @NotNull
    @Override
    public State reduceContinueStatement(@NotNull ContinueStatement node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceDataProperty(
            @NotNull DataProperty node,
            @NotNull State name,
            @NotNull State value) {
        return append(name, value);
    }

    @NotNull
    @Override
    public State reduceDebuggerStatement(@NotNull DebuggerStatement node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceDirective(@NotNull Directive node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceDoWhileStatement(
            @NotNull DoWhileStatement node,
            @NotNull State body,
            @NotNull State test) {
        return append(body, test);
    }

    @NotNull
    @Override
    public State reduceEmptyStatement(@NotNull EmptyStatement node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceExport(@NotNull Export node, @NotNull State declaration) {
        return declaration;
    }

    @NotNull
    @Override
    public State reduceExportAllFrom(@NotNull ExportAllFrom node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceExportDefault(@NotNull ExportDefault node, @NotNull State body) {
        return body;
    }

    @NotNull
    @Override
    public State reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<State> namedExports) {
        return fold(namedExports);
    }

    @NotNull
    @Override
    public State reduceExportSpecifier(@NotNull ExportSpecifier node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull State expression) {
        return expression;
    }

    @NotNull
    @Override
    public State reduceForInStatement(@NotNull ForInStatement node, @NotNull State left, @NotNull State right, @NotNull State body) {
        return append(left, right, body);
    }

    @NotNull
    @Override
    public State reduceForOfStatement(@NotNull ForOfStatement node, @NotNull State left, @NotNull State right, @NotNull State body) {
        return append(left, right, body);
    }

    @NotNull
    @Override
    public State reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<State> init, @NotNull Maybe<State> test, @NotNull Maybe<State> update, @NotNull State body) {
        return append(o(init), o(test), o(update), body);
    }

    @NotNull
    @Override
    public State reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<State> items, @NotNull Maybe<State> rest) {
        return append(fold(items), o(rest));
    }

    @NotNull
    @Override
    public State reduceFunctionBody(
            @NotNull FunctionBody node,
            @NotNull ImmutableList<State> directives,
            @NotNull ImmutableList<State> statements) {
        return append(fold(directives), fold(statements));
    }

    @NotNull
    @Override
    public State reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull State name, @NotNull State params, @NotNull State body) {
        return append(name, params, body);
    }

    @NotNull
    @Override
    public State reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<State> name, @NotNull State parameters, @NotNull State body) {
        return append(o(name), parameters, body);
    }

    @NotNull
    @Override
    public State reduceGetter(@NotNull Getter node, @NotNull State body, @NotNull State name) {
        return append(body, name);
    }

    @NotNull
    @Override
    public State reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceIfStatement(
            @NotNull IfStatement node,
            @NotNull State test,
            @NotNull State consequent,
            @NotNull Maybe<State> alternate) {
        return append(test, consequent, o(alternate));
    }

    @NotNull
    @Override
    public State reduceImport(@NotNull Import node, @NotNull Maybe<State> defaultBinding, @NotNull ImmutableList<State> namedImports) {
        return fold1(namedImports, o(defaultBinding));
    }

    @NotNull
    @Override
    public State reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<State> defaultBinding, @NotNull State namespaceBinding) {
        return append(o(defaultBinding), namespaceBinding);
    }

    @NotNull
    @Override
    public State reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull State binding) {
        return binding;
    }

    @NotNull
    @Override
    public State reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull State body) {
        return body;
    }

    @NotNull
    @Override
    public State reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceMethod(@NotNull Method node, @NotNull State params, @NotNull State body, @NotNull State name) {
        return append(params, body, name);
    }

    @NotNull
    @Override
    public State reduceModule(@NotNull Module node, @NotNull ImmutableList<State> directives, @NotNull ImmutableList<State> items) {
        return append(fold(directives), fold(items));
    }

    @NotNull
    @Override
    public State reduceNewExpression(
            @NotNull NewExpression node,
            @NotNull State callee,
            @NotNull ImmutableList<State> arguments) {
        return fold1(arguments, callee);
    }

    @NotNull
    @Override
    public State reduceNewTargetExpression(@NotNull NewTargetExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<State> properties) {
        return fold(properties);
    }

    @NotNull
    @Override
    public State reduceObjectExpression(
            @NotNull ObjectExpression node,
            @NotNull ImmutableList<State> properties) {
        return fold(properties);
    }

    @NotNull
    @Override
    public State reduceReturnStatement(
            @NotNull ReturnStatement node,
            @NotNull Maybe<State> expression) {
        return o(expression);
    }

    @NotNull
    @Override
    public State reduceScript(@NotNull Script node, @NotNull ImmutableList<State> directives, @NotNull ImmutableList<State> statements) {
        return append(fold(directives), fold(statements));
    }

    @NotNull
    @Override
    public State reduceSetter(
            @NotNull Setter node,
            @NotNull State params,
            @NotNull State body,
            @NotNull State name) {
        return append(params, body, name);
    }

    @NotNull
    @Override
    public State reduceShorthandProperty(@NotNull ShorthandProperty node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceSpreadElement(@NotNull SpreadElement node, @NotNull State expression) {
        return expression;
    }

    @NotNull
    @Override
    public State reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull State object) {
        return object;
    }

    @NotNull
    @Override
    public State reduceStaticPropertyName(@NotNull StaticPropertyName node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceSuper(@NotNull Super node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceSwitchCase(
            @NotNull SwitchCase node,
            @NotNull State test,
            @NotNull ImmutableList<State> consequent) {
        return fold1(consequent, test);
    }

    @NotNull
    @Override
    public State reduceSwitchDefault(
            @NotNull SwitchDefault node,
            @NotNull ImmutableList<State> consequent) {
        return fold(consequent);
    }

    @NotNull
    @Override
    public State reduceSwitchStatement(
            @NotNull SwitchStatement node,
            @NotNull State discriminant,
            @NotNull ImmutableList<State> cases) {
        return fold1(cases, discriminant);
    }

    @NotNull
    @Override
    public State reduceSwitchStatementWithDefault(
            @NotNull SwitchStatementWithDefault node,
            @NotNull State discriminant,
            @NotNull ImmutableList<State> preDefaultCases,
            @NotNull State defaultCase,
            @NotNull ImmutableList<State> postDefaultCases) {
        return append(discriminant, fold(preDefaultCases), defaultCase, fold(postDefaultCases));
    }

    @NotNull
    @Override
    public State reduceTemplateElement(@NotNull TemplateElement node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<State> tag, @NotNull ImmutableList<State> elements) {
        return fold1(elements, o(tag));
    }

    @NotNull
    @Override
    public State reduceThisExpression(@NotNull ThisExpression node) {
        return this.identity();
    }

    @NotNull
    @Override
    public State reduceThrowStatement(@NotNull ThrowStatement node, @NotNull State expression) {
        return expression;
    }

    @NotNull
    @Override
    public State reduceTryCatchStatement(
            @NotNull TryCatchStatement node,
            @NotNull State block,
            @NotNull State catchClause) {
        return append(block, catchClause);
    }

    @NotNull
    @Override
    public State reduceTryFinallyStatement(
            @NotNull TryFinallyStatement node,
            @NotNull State block,
            @NotNull Maybe<State> catchClause,
            @NotNull State finalizer) {
        return append(block, o(catchClause), finalizer);
    }

    @NotNull
    @Override
    public State reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull State operand) {
        return operand;
    }

    @NotNull
    @Override
    public State reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull State operand) {
        return operand;
    }

    @NotNull
    @Override
    public State reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<State> declarators) {
        return fold(declarators);
    }

    @NotNull
    @Override
    public State reduceVariableDeclarationStatement(
            @NotNull VariableDeclarationStatement node,
            @NotNull State declaration) {
        return declaration;
    }

    @NotNull
    @Override
    public State reduceVariableDeclarator(
            @NotNull VariableDeclarator node,
            @NotNull State binding,
            @NotNull Maybe<State> init) {
        return append(binding, o(init));
    }

    @NotNull
    @Override
    public State reduceWhileStatement(
            @NotNull WhileStatement node,
            @NotNull State test,
            @NotNull State body) {
        return append(test, body);
    }

    @NotNull
    @Override
    public State reduceWithStatement(
            @NotNull WithStatement node,
            @NotNull State object,
            @NotNull State body) {
        return append(object, body);
    }

    @NotNull
    @Override
    public State reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<State> expression) {
        return o(expression);
    }

    @NotNull
    @Override
    public State reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull State expression) {
        return expression;
    }
}