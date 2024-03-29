// Generated by thunkify.js
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
import java.util.function.Supplier;

public class Thunked <State, T extends Reducer<State>> implements ThunkedReducer<State> {
    private Reducer<State> reducer;
    public Thunked(Reducer<State> reducer) {
        this.reducer = reducer;
    }

    @Override
    @Nonnull
    public State reduceArrayAssignmentTarget(
        @Nonnull ArrayAssignmentTarget node,
        @Nonnull ImmutableList<Maybe<Supplier<State>>> elements,
        @Nonnull Maybe<Supplier<State>> rest
    ) {
        return reducer.reduceArrayAssignmentTarget(node, elements.map(x -> x.map(Supplier::get)), rest.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceArrayBinding(
        @Nonnull ArrayBinding node,
        @Nonnull ImmutableList<Maybe<Supplier<State>>> elements,
        @Nonnull Maybe<Supplier<State>> rest
    ) {
        return reducer.reduceArrayBinding(node, elements.map(x -> x.map(Supplier::get)), rest.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceArrayExpression(
        @Nonnull ArrayExpression node,
        @Nonnull ImmutableList<Maybe<Supplier<State>>> elements
    ) {
        return reducer.reduceArrayExpression(node, elements.map(x -> x.map(Supplier::get)));
    }

    @Override
    @Nonnull
    public State reduceArrowExpression(
        @Nonnull ArrowExpression node,
        @Nonnull Supplier<State> params,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceArrowExpression(node, params.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceAssignmentExpression(
        @Nonnull AssignmentExpression node,
        @Nonnull Supplier<State> binding,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceAssignmentExpression(node, binding.get(), expression.get());
    }

    @Override
    @Nonnull
    public State reduceAssignmentTargetIdentifier(
        @Nonnull AssignmentTargetIdentifier node
    ) {
        return reducer.reduceAssignmentTargetIdentifier(node);
    }

    @Override
    @Nonnull
    public State reduceAssignmentTargetPropertyIdentifier(
        @Nonnull AssignmentTargetPropertyIdentifier node,
        @Nonnull Supplier<State> binding,
        @Nonnull Maybe<Supplier<State>> init
    ) {
        return reducer.reduceAssignmentTargetPropertyIdentifier(node, binding.get(), init.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceAssignmentTargetPropertyProperty(
        @Nonnull AssignmentTargetPropertyProperty node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> binding
    ) {
        return reducer.reduceAssignmentTargetPropertyProperty(node, name.get(), binding.get());
    }

    @Override
    @Nonnull
    public State reduceAssignmentTargetWithDefault(
        @Nonnull AssignmentTargetWithDefault node,
        @Nonnull Supplier<State> binding,
        @Nonnull Supplier<State> init
    ) {
        return reducer.reduceAssignmentTargetWithDefault(node, binding.get(), init.get());
    }

    @Override
    @Nonnull
    public State reduceAwaitExpression(
        @Nonnull AwaitExpression node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceAwaitExpression(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceBinaryExpression(
        @Nonnull BinaryExpression node,
        @Nonnull Supplier<State> left,
        @Nonnull Supplier<State> right
    ) {
        return reducer.reduceBinaryExpression(node, left.get(), right.get());
    }

    @Override
    @Nonnull
    public State reduceBindingIdentifier(
        @Nonnull BindingIdentifier node
    ) {
        return reducer.reduceBindingIdentifier(node);
    }

    @Override
    @Nonnull
    public State reduceBindingPropertyIdentifier(
        @Nonnull BindingPropertyIdentifier node,
        @Nonnull Supplier<State> binding,
        @Nonnull Maybe<Supplier<State>> init
    ) {
        return reducer.reduceBindingPropertyIdentifier(node, binding.get(), init.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceBindingPropertyProperty(
        @Nonnull BindingPropertyProperty node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> binding
    ) {
        return reducer.reduceBindingPropertyProperty(node, name.get(), binding.get());
    }

    @Override
    @Nonnull
    public State reduceBindingWithDefault(
        @Nonnull BindingWithDefault node,
        @Nonnull Supplier<State> binding,
        @Nonnull Supplier<State> init
    ) {
        return reducer.reduceBindingWithDefault(node, binding.get(), init.get());
    }

    @Override
    @Nonnull
    public State reduceBlock(
        @Nonnull Block node,
        @Nonnull ImmutableList<Supplier<State>> statements
    ) {
        return reducer.reduceBlock(node, statements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceBlockStatement(
        @Nonnull BlockStatement node,
        @Nonnull Supplier<State> block
    ) {
        return reducer.reduceBlockStatement(node, block.get());
    }

    @Override
    @Nonnull
    public State reduceBreakStatement(
        @Nonnull BreakStatement node
    ) {
        return reducer.reduceBreakStatement(node);
    }

    @Override
    @Nonnull
    public State reduceCallExpression(
        @Nonnull CallExpression node,
        @Nonnull Supplier<State> callee,
        @Nonnull ImmutableList<Supplier<State>> arguments
    ) {
        return reducer.reduceCallExpression(node, callee.get(), arguments.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceCatchClause(
        @Nonnull CatchClause node,
        @Nonnull Supplier<State> binding,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceCatchClause(node, binding.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceClassDeclaration(
        @Nonnull ClassDeclaration node,
        @Nonnull Supplier<State> name,
        @Nonnull Maybe<Supplier<State>> _super,
        @Nonnull ImmutableList<Supplier<State>> elements
    ) {
        return reducer.reduceClassDeclaration(node, name.get(), _super.map(Supplier::get), elements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceClassElement(
        @Nonnull ClassElement node,
        @Nonnull Supplier<State> method
    ) {
        return reducer.reduceClassElement(node, method.get());
    }

    @Override
    @Nonnull
    public State reduceClassExpression(
        @Nonnull ClassExpression node,
        @Nonnull Maybe<Supplier<State>> name,
        @Nonnull Maybe<Supplier<State>> _super,
        @Nonnull ImmutableList<Supplier<State>> elements
    ) {
        return reducer.reduceClassExpression(node, name.map(Supplier::get), _super.map(Supplier::get), elements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceCompoundAssignmentExpression(
        @Nonnull CompoundAssignmentExpression node,
        @Nonnull Supplier<State> binding,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceCompoundAssignmentExpression(node, binding.get(), expression.get());
    }

    @Override
    @Nonnull
    public State reduceComputedMemberAssignmentTarget(
        @Nonnull ComputedMemberAssignmentTarget node,
        @Nonnull Supplier<State> object,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceComputedMemberAssignmentTarget(node, object.get(), expression.get());
    }

    @Override
    @Nonnull
    public State reduceComputedMemberExpression(
        @Nonnull ComputedMemberExpression node,
        @Nonnull Supplier<State> object,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceComputedMemberExpression(node, object.get(), expression.get());
    }

    @Override
    @Nonnull
    public State reduceComputedPropertyName(
        @Nonnull ComputedPropertyName node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceComputedPropertyName(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceConditionalExpression(
        @Nonnull ConditionalExpression node,
        @Nonnull Supplier<State> test,
        @Nonnull Supplier<State> consequent,
        @Nonnull Supplier<State> alternate
    ) {
        return reducer.reduceConditionalExpression(node, test.get(), consequent.get(), alternate.get());
    }

    @Override
    @Nonnull
    public State reduceContinueStatement(
        @Nonnull ContinueStatement node
    ) {
        return reducer.reduceContinueStatement(node);
    }

    @Override
    @Nonnull
    public State reduceDataProperty(
        @Nonnull DataProperty node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceDataProperty(node, name.get(), expression.get());
    }

    @Override
    @Nonnull
    public State reduceDebuggerStatement(
        @Nonnull DebuggerStatement node
    ) {
        return reducer.reduceDebuggerStatement(node);
    }

    @Override
    @Nonnull
    public State reduceDirective(
        @Nonnull Directive node
    ) {
        return reducer.reduceDirective(node);
    }

    @Override
    @Nonnull
    public State reduceDoWhileStatement(
        @Nonnull DoWhileStatement node,
        @Nonnull Supplier<State> body,
        @Nonnull Supplier<State> test
    ) {
        return reducer.reduceDoWhileStatement(node, body.get(), test.get());
    }

    @Override
    @Nonnull
    public State reduceEmptyStatement(
        @Nonnull EmptyStatement node
    ) {
        return reducer.reduceEmptyStatement(node);
    }

    @Override
    @Nonnull
    public State reduceExport(
        @Nonnull Export node,
        @Nonnull Supplier<State> declaration
    ) {
        return reducer.reduceExport(node, declaration.get());
    }

    @Override
    @Nonnull
    public State reduceExportAllFrom(
        @Nonnull ExportAllFrom node
    ) {
        return reducer.reduceExportAllFrom(node);
    }

    @Override
    @Nonnull
    public State reduceExportDefault(
        @Nonnull ExportDefault node,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceExportDefault(node, body.get());
    }

    @Override
    @Nonnull
    public State reduceExportFrom(
        @Nonnull ExportFrom node,
        @Nonnull ImmutableList<Supplier<State>> namedExports
    ) {
        return reducer.reduceExportFrom(node, namedExports.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceExportFromSpecifier(
        @Nonnull ExportFromSpecifier node
    ) {
        return reducer.reduceExportFromSpecifier(node);
    }

    @Override
    @Nonnull
    public State reduceExportLocalSpecifier(
        @Nonnull ExportLocalSpecifier node,
        @Nonnull Supplier<State> name
    ) {
        return reducer.reduceExportLocalSpecifier(node, name.get());
    }

    @Override
    @Nonnull
    public State reduceExportLocals(
        @Nonnull ExportLocals node,
        @Nonnull ImmutableList<Supplier<State>> namedExports
    ) {
        return reducer.reduceExportLocals(node, namedExports.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceExpressionStatement(
        @Nonnull ExpressionStatement node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceExpressionStatement(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceForAwaitStatement(
        @Nonnull ForAwaitStatement node,
        @Nonnull Supplier<State> left,
        @Nonnull Supplier<State> right,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceForAwaitStatement(node, left.get(), right.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceForInStatement(
        @Nonnull ForInStatement node,
        @Nonnull Supplier<State> left,
        @Nonnull Supplier<State> right,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceForInStatement(node, left.get(), right.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceForOfStatement(
        @Nonnull ForOfStatement node,
        @Nonnull Supplier<State> left,
        @Nonnull Supplier<State> right,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceForOfStatement(node, left.get(), right.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceForStatement(
        @Nonnull ForStatement node,
        @Nonnull Maybe<Supplier<State>> init,
        @Nonnull Maybe<Supplier<State>> test,
        @Nonnull Maybe<Supplier<State>> update,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceForStatement(node, init.map(Supplier::get), test.map(Supplier::get), update.map(Supplier::get), body.get());
    }

    @Override
    @Nonnull
    public State reduceFormalParameters(
        @Nonnull FormalParameters node,
        @Nonnull ImmutableList<Supplier<State>> items,
        @Nonnull Maybe<Supplier<State>> rest
    ) {
        return reducer.reduceFormalParameters(node, items.map(Supplier::get), rest.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceFunctionBody(
        @Nonnull FunctionBody node,
        @Nonnull ImmutableList<Supplier<State>> directives,
        @Nonnull ImmutableList<Supplier<State>> statements
    ) {
        return reducer.reduceFunctionBody(node, directives.map(Supplier::get), statements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceFunctionDeclaration(
        @Nonnull FunctionDeclaration node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> params,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceFunctionDeclaration(node, name.get(), params.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceFunctionExpression(
        @Nonnull FunctionExpression node,
        @Nonnull Maybe<Supplier<State>> name,
        @Nonnull Supplier<State> params,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceFunctionExpression(node, name.map(Supplier::get), params.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceGetter(
        @Nonnull Getter node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceGetter(node, name.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceIdentifierExpression(
        @Nonnull IdentifierExpression node
    ) {
        return reducer.reduceIdentifierExpression(node);
    }

    @Override
    @Nonnull
    public State reduceIfStatement(
        @Nonnull IfStatement node,
        @Nonnull Supplier<State> test,
        @Nonnull Supplier<State> consequent,
        @Nonnull Maybe<Supplier<State>> alternate
    ) {
        return reducer.reduceIfStatement(node, test.get(), consequent.get(), alternate.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceImport(
        @Nonnull Import node,
        @Nonnull Maybe<Supplier<State>> defaultBinding,
        @Nonnull ImmutableList<Supplier<State>> namedImports
    ) {
        return reducer.reduceImport(node, defaultBinding.map(Supplier::get), namedImports.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceImportNamespace(
        @Nonnull ImportNamespace node,
        @Nonnull Maybe<Supplier<State>> defaultBinding,
        @Nonnull Supplier<State> namespaceBinding
    ) {
        return reducer.reduceImportNamespace(node, defaultBinding.map(Supplier::get), namespaceBinding.get());
    }

    @Override
    @Nonnull
    public State reduceImportSpecifier(
        @Nonnull ImportSpecifier node,
        @Nonnull Supplier<State> binding
    ) {
        return reducer.reduceImportSpecifier(node, binding.get());
    }

    @Override
    @Nonnull
    public State reduceLabeledStatement(
        @Nonnull LabeledStatement node,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceLabeledStatement(node, body.get());
    }

    @Override
    @Nonnull
    public State reduceLiteralBooleanExpression(
        @Nonnull LiteralBooleanExpression node
    ) {
        return reducer.reduceLiteralBooleanExpression(node);
    }

    @Override
    @Nonnull
    public State reduceLiteralInfinityExpression(
        @Nonnull LiteralInfinityExpression node
    ) {
        return reducer.reduceLiteralInfinityExpression(node);
    }

    @Override
    @Nonnull
    public State reduceLiteralNullExpression(
        @Nonnull LiteralNullExpression node
    ) {
        return reducer.reduceLiteralNullExpression(node);
    }

    @Override
    @Nonnull
    public State reduceLiteralNumericExpression(
        @Nonnull LiteralNumericExpression node
    ) {
        return reducer.reduceLiteralNumericExpression(node);
    }

    @Override
    @Nonnull
    public State reduceLiteralRegExpExpression(
        @Nonnull LiteralRegExpExpression node
    ) {
        return reducer.reduceLiteralRegExpExpression(node);
    }

    @Override
    @Nonnull
    public State reduceLiteralStringExpression(
        @Nonnull LiteralStringExpression node
    ) {
        return reducer.reduceLiteralStringExpression(node);
    }

    @Override
    @Nonnull
    public State reduceMethod(
        @Nonnull Method node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> params,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceMethod(node, name.get(), params.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceModule(
        @Nonnull Module node,
        @Nonnull ImmutableList<Supplier<State>> directives,
        @Nonnull ImmutableList<Supplier<State>> items
    ) {
        return reducer.reduceModule(node, directives.map(Supplier::get), items.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceNewExpression(
        @Nonnull NewExpression node,
        @Nonnull Supplier<State> callee,
        @Nonnull ImmutableList<Supplier<State>> arguments
    ) {
        return reducer.reduceNewExpression(node, callee.get(), arguments.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceNewTargetExpression(
        @Nonnull NewTargetExpression node
    ) {
        return reducer.reduceNewTargetExpression(node);
    }

    @Override
    @Nonnull
    public State reduceObjectAssignmentTarget(
        @Nonnull ObjectAssignmentTarget node,
        @Nonnull ImmutableList<Supplier<State>> properties,
        @Nonnull Maybe<Supplier<State>> rest
    ) {
        return reducer.reduceObjectAssignmentTarget(node, properties.map(Supplier::get), rest.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceObjectBinding(
        @Nonnull ObjectBinding node,
        @Nonnull ImmutableList<Supplier<State>> properties,
        @Nonnull Maybe<Supplier<State>> rest
    ) {
        return reducer.reduceObjectBinding(node, properties.map(Supplier::get), rest.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceObjectExpression(
        @Nonnull ObjectExpression node,
        @Nonnull ImmutableList<Supplier<State>> properties
    ) {
        return reducer.reduceObjectExpression(node, properties.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceReturnStatement(
        @Nonnull ReturnStatement node,
        @Nonnull Maybe<Supplier<State>> expression
    ) {
        return reducer.reduceReturnStatement(node, expression.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceScript(
        @Nonnull Script node,
        @Nonnull ImmutableList<Supplier<State>> directives,
        @Nonnull ImmutableList<Supplier<State>> statements
    ) {
        return reducer.reduceScript(node, directives.map(Supplier::get), statements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceSetter(
        @Nonnull Setter node,
        @Nonnull Supplier<State> name,
        @Nonnull Supplier<State> param,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceSetter(node, name.get(), param.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceShorthandProperty(
        @Nonnull ShorthandProperty node,
        @Nonnull Supplier<State> name
    ) {
        return reducer.reduceShorthandProperty(node, name.get());
    }

    @Override
    @Nonnull
    public State reduceSpreadElement(
        @Nonnull SpreadElement node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceSpreadElement(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceSpreadProperty(
        @Nonnull SpreadProperty node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceSpreadProperty(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceStaticMemberAssignmentTarget(
        @Nonnull StaticMemberAssignmentTarget node,
        @Nonnull Supplier<State> object
    ) {
        return reducer.reduceStaticMemberAssignmentTarget(node, object.get());
    }

    @Override
    @Nonnull
    public State reduceStaticMemberExpression(
        @Nonnull StaticMemberExpression node,
        @Nonnull Supplier<State> object
    ) {
        return reducer.reduceStaticMemberExpression(node, object.get());
    }

    @Override
    @Nonnull
    public State reduceStaticPropertyName(
        @Nonnull StaticPropertyName node
    ) {
        return reducer.reduceStaticPropertyName(node);
    }

    @Override
    @Nonnull
    public State reduceSuper(
        @Nonnull Super node
    ) {
        return reducer.reduceSuper(node);
    }

    @Override
    @Nonnull
    public State reduceSwitchCase(
        @Nonnull SwitchCase node,
        @Nonnull Supplier<State> test,
        @Nonnull ImmutableList<Supplier<State>> consequent
    ) {
        return reducer.reduceSwitchCase(node, test.get(), consequent.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceSwitchDefault(
        @Nonnull SwitchDefault node,
        @Nonnull ImmutableList<Supplier<State>> consequent
    ) {
        return reducer.reduceSwitchDefault(node, consequent.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceSwitchStatement(
        @Nonnull SwitchStatement node,
        @Nonnull Supplier<State> discriminant,
        @Nonnull ImmutableList<Supplier<State>> cases
    ) {
        return reducer.reduceSwitchStatement(node, discriminant.get(), cases.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceSwitchStatementWithDefault(
        @Nonnull SwitchStatementWithDefault node,
        @Nonnull Supplier<State> discriminant,
        @Nonnull ImmutableList<Supplier<State>> preDefaultCases,
        @Nonnull Supplier<State> defaultCase,
        @Nonnull ImmutableList<Supplier<State>> postDefaultCases
    ) {
        return reducer.reduceSwitchStatementWithDefault(node, discriminant.get(), preDefaultCases.map(Supplier::get), defaultCase.get(), postDefaultCases.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceTemplateElement(
        @Nonnull TemplateElement node
    ) {
        return reducer.reduceTemplateElement(node);
    }

    @Override
    @Nonnull
    public State reduceTemplateExpression(
        @Nonnull TemplateExpression node,
        @Nonnull Maybe<Supplier<State>> tag,
        @Nonnull ImmutableList<Supplier<State>> elements
    ) {
        return reducer.reduceTemplateExpression(node, tag.map(Supplier::get), elements.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceThisExpression(
        @Nonnull ThisExpression node
    ) {
        return reducer.reduceThisExpression(node);
    }

    @Override
    @Nonnull
    public State reduceThrowStatement(
        @Nonnull ThrowStatement node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceThrowStatement(node, expression.get());
    }

    @Override
    @Nonnull
    public State reduceTryCatchStatement(
        @Nonnull TryCatchStatement node,
        @Nonnull Supplier<State> body,
        @Nonnull Supplier<State> catchClause
    ) {
        return reducer.reduceTryCatchStatement(node, body.get(), catchClause.get());
    }

    @Override
    @Nonnull
    public State reduceTryFinallyStatement(
        @Nonnull TryFinallyStatement node,
        @Nonnull Supplier<State> body,
        @Nonnull Maybe<Supplier<State>> catchClause,
        @Nonnull Supplier<State> finalizer
    ) {
        return reducer.reduceTryFinallyStatement(node, body.get(), catchClause.map(Supplier::get), finalizer.get());
    }

    @Override
    @Nonnull
    public State reduceUnaryExpression(
        @Nonnull UnaryExpression node,
        @Nonnull Supplier<State> operand
    ) {
        return reducer.reduceUnaryExpression(node, operand.get());
    }

    @Override
    @Nonnull
    public State reduceUpdateExpression(
        @Nonnull UpdateExpression node,
        @Nonnull Supplier<State> operand
    ) {
        return reducer.reduceUpdateExpression(node, operand.get());
    }

    @Override
    @Nonnull
    public State reduceVariableDeclaration(
        @Nonnull VariableDeclaration node,
        @Nonnull ImmutableList<Supplier<State>> declarators
    ) {
        return reducer.reduceVariableDeclaration(node, declarators.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceVariableDeclarationStatement(
        @Nonnull VariableDeclarationStatement node,
        @Nonnull Supplier<State> declaration
    ) {
        return reducer.reduceVariableDeclarationStatement(node, declaration.get());
    }

    @Override
    @Nonnull
    public State reduceVariableDeclarator(
        @Nonnull VariableDeclarator node,
        @Nonnull Supplier<State> binding,
        @Nonnull Maybe<Supplier<State>> init
    ) {
        return reducer.reduceVariableDeclarator(node, binding.get(), init.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceWhileStatement(
        @Nonnull WhileStatement node,
        @Nonnull Supplier<State> test,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceWhileStatement(node, test.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceWithStatement(
        @Nonnull WithStatement node,
        @Nonnull Supplier<State> object,
        @Nonnull Supplier<State> body
    ) {
        return reducer.reduceWithStatement(node, object.get(), body.get());
    }

    @Override
    @Nonnull
    public State reduceYieldExpression(
        @Nonnull YieldExpression node,
        @Nonnull Maybe<Supplier<State>> expression
    ) {
        return reducer.reduceYieldExpression(node, expression.map(Supplier::get));
    }

    @Override
    @Nonnull
    public State reduceYieldGeneratorExpression(
        @Nonnull YieldGeneratorExpression node,
        @Nonnull Supplier<State> expression
    ) {
        return reducer.reduceYieldGeneratorExpression(node, expression.get());
    }

}
