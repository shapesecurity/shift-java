// Generated by shift-spec-java/reducer.js

/**
 * Copyright 2016 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2016.ast.ArrayAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.ArrayBinding;
import com.shapesecurity.shift.es2016.ast.ArrayExpression;
import com.shapesecurity.shift.es2016.ast.ArrowExpression;
import com.shapesecurity.shift.es2016.ast.AssignmentExpression;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetPropertyIdentifier;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetPropertyProperty;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetWithDefault;
import com.shapesecurity.shift.es2016.ast.BinaryExpression;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingPropertyIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingPropertyProperty;
import com.shapesecurity.shift.es2016.ast.BindingWithDefault;
import com.shapesecurity.shift.es2016.ast.Block;
import com.shapesecurity.shift.es2016.ast.BlockStatement;
import com.shapesecurity.shift.es2016.ast.BreakStatement;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.CatchClause;
import com.shapesecurity.shift.es2016.ast.ClassDeclaration;
import com.shapesecurity.shift.es2016.ast.ClassElement;
import com.shapesecurity.shift.es2016.ast.ClassExpression;
import com.shapesecurity.shift.es2016.ast.CompoundAssignmentExpression;
import com.shapesecurity.shift.es2016.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.ComputedMemberExpression;
import com.shapesecurity.shift.es2016.ast.ComputedPropertyName;
import com.shapesecurity.shift.es2016.ast.ConditionalExpression;
import com.shapesecurity.shift.es2016.ast.ContinueStatement;
import com.shapesecurity.shift.es2016.ast.DataProperty;
import com.shapesecurity.shift.es2016.ast.DebuggerStatement;
import com.shapesecurity.shift.es2016.ast.Directive;
import com.shapesecurity.shift.es2016.ast.DoWhileStatement;
import com.shapesecurity.shift.es2016.ast.EmptyStatement;
import com.shapesecurity.shift.es2016.ast.Export;
import com.shapesecurity.shift.es2016.ast.ExportAllFrom;
import com.shapesecurity.shift.es2016.ast.ExportDefault;
import com.shapesecurity.shift.es2016.ast.ExportFrom;
import com.shapesecurity.shift.es2016.ast.ExportFromSpecifier;
import com.shapesecurity.shift.es2016.ast.ExportLocalSpecifier;
import com.shapesecurity.shift.es2016.ast.ExportLocals;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.ForInStatement;
import com.shapesecurity.shift.es2016.ast.ForOfStatement;
import com.shapesecurity.shift.es2016.ast.ForStatement;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2016.ast.FunctionExpression;
import com.shapesecurity.shift.es2016.ast.Getter;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.IfStatement;
import com.shapesecurity.shift.es2016.ast.Import;
import com.shapesecurity.shift.es2016.ast.ImportNamespace;
import com.shapesecurity.shift.es2016.ast.ImportSpecifier;
import com.shapesecurity.shift.es2016.ast.LabeledStatement;
import com.shapesecurity.shift.es2016.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2016.ast.LiteralInfinityExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2016.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2016.ast.Method;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.NewExpression;
import com.shapesecurity.shift.es2016.ast.NewTargetExpression;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.ObjectAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.ObjectBinding;
import com.shapesecurity.shift.es2016.ast.ObjectExpression;
import com.shapesecurity.shift.es2016.ast.ReturnStatement;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.Setter;
import com.shapesecurity.shift.es2016.ast.ShorthandProperty;
import com.shapesecurity.shift.es2016.ast.SpreadElement;
import com.shapesecurity.shift.es2016.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.ast.StaticPropertyName;
import com.shapesecurity.shift.es2016.ast.Super;
import com.shapesecurity.shift.es2016.ast.SwitchCase;
import com.shapesecurity.shift.es2016.ast.SwitchDefault;
import com.shapesecurity.shift.es2016.ast.SwitchStatement;
import com.shapesecurity.shift.es2016.ast.SwitchStatementWithDefault;
import com.shapesecurity.shift.es2016.ast.TemplateElement;
import com.shapesecurity.shift.es2016.ast.TemplateExpression;
import com.shapesecurity.shift.es2016.ast.ThisExpression;
import com.shapesecurity.shift.es2016.ast.ThrowStatement;
import com.shapesecurity.shift.es2016.ast.TryCatchStatement;
import com.shapesecurity.shift.es2016.ast.TryFinallyStatement;
import com.shapesecurity.shift.es2016.ast.UnaryExpression;
import com.shapesecurity.shift.es2016.ast.UpdateExpression;
import com.shapesecurity.shift.es2016.ast.VariableDeclaration;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.VariableDeclarator;
import com.shapesecurity.shift.es2016.ast.WhileStatement;
import com.shapesecurity.shift.es2016.ast.WithStatement;
import com.shapesecurity.shift.es2016.ast.YieldExpression;
import com.shapesecurity.shift.es2016.ast.YieldGeneratorExpression;
import com.shapesecurity.shift.es2016.parser.RangeCheckerReducer.RangeChecker;
import com.shapesecurity.shift.es2016.reducer.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.assertTrue;

public class RangeCheckerReducer extends MonoidalReducer<RangeChecker> {
    private final ParserWithLocation parserWithLocation;

    protected RangeCheckerReducer(ParserWithLocation parserWithLocation) {
        super(RangeChecker.MONOID);
        this.parserWithLocation = parserWithLocation;
    }

    private RangeChecker accept(Node node, RangeChecker innerBounds) {
        Maybe<SourceSpan> span = this.parserWithLocation.getLocation(node);
        assertTrue(span.isJust());
        RangeChecker outerBounds = new RangeChecker(span.fromJust());
        assertTrue(outerBounds.start <= outerBounds.end);

        assertTrue(outerBounds.start <= innerBounds.start);
        assertTrue(innerBounds.end <= outerBounds.end);

        return outerBounds;
    }

    static class RangeChecker {
        public final static Monoid<RangeChecker> MONOID = new Monoid<RangeChecker>() {
            @NotNull
            @Override
            public RangeChecker identity() {
                return new RangeChecker(Integer.MAX_VALUE, Integer.MIN_VALUE);
            }

            @NotNull
            @Override
            public RangeChecker append(RangeChecker a, RangeChecker b) {
                assertTrue(a.end <= b.start);
                return new RangeChecker(a.start, b.end);
            }
        };
        public final int start, end;

        private RangeChecker(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public RangeChecker(SourceSpan sourceSpan) {
            this(sourceSpan.start.offset, sourceSpan.end.offset);
        }
    }

    @NotNull
    @Override
    public RangeChecker reduceArrayAssignmentTarget(@NotNull ArrayAssignmentTarget node, @NotNull ImmutableList<Maybe<RangeChecker>> elements, @NotNull Maybe<RangeChecker> rest) {
      return accept(node, super.reduceArrayAssignmentTarget(node, elements, rest));
    }

    @NotNull
    @Override
    public RangeChecker reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<RangeChecker>> elements, @NotNull Maybe<RangeChecker> rest) {
      return accept(node, super.reduceArrayBinding(node, elements, rest));
    }

    @NotNull
    @Override
    public RangeChecker reduceArrayExpression(@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<RangeChecker>> elements) {
      return accept(node, super.reduceArrayExpression(node, elements));
    }

    @NotNull
    @Override
    public RangeChecker reduceArrowExpression(@NotNull ArrowExpression node, @NotNull RangeChecker params, @NotNull RangeChecker body) {
      return accept(node, super.reduceArrowExpression(node, params, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull RangeChecker binding, @NotNull RangeChecker expression) {
      return accept(node, super.reduceAssignmentExpression(node, binding, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceAssignmentTargetIdentifier(@NotNull AssignmentTargetIdentifier node) {
      return accept(node, super.reduceAssignmentTargetIdentifier(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceAssignmentTargetPropertyIdentifier(@NotNull AssignmentTargetPropertyIdentifier node, @NotNull RangeChecker binding, @NotNull Maybe<RangeChecker> init) {
      return accept(node, super.reduceAssignmentTargetPropertyIdentifier(node, binding, init));
    }

    @NotNull
    @Override
    public RangeChecker reduceAssignmentTargetPropertyProperty(@NotNull AssignmentTargetPropertyProperty node, @NotNull RangeChecker name, @NotNull RangeChecker binding) {
      return accept(node, super.reduceAssignmentTargetPropertyProperty(node, name, binding));
    }

    @NotNull
    @Override
    public RangeChecker reduceAssignmentTargetWithDefault(@NotNull AssignmentTargetWithDefault node, @NotNull RangeChecker binding, @NotNull RangeChecker init) {
      return accept(node, super.reduceAssignmentTargetWithDefault(node, binding, init));
    }

    @NotNull
    @Override
    public RangeChecker reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull RangeChecker left, @NotNull RangeChecker right) {
      return accept(node, super.reduceBinaryExpression(node, left, right));
    }

    @NotNull
    @Override
    public RangeChecker reduceBindingIdentifier(@NotNull BindingIdentifier node) {
      return accept(node, super.reduceBindingIdentifier(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull RangeChecker binding, @NotNull Maybe<RangeChecker> init) {
      return accept(node, super.reduceBindingPropertyIdentifier(node, binding, init));
    }

    @NotNull
    @Override
    public RangeChecker reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull RangeChecker name, @NotNull RangeChecker binding) {
      return accept(node, super.reduceBindingPropertyProperty(node, name, binding));
    }

    @NotNull
    @Override
    public RangeChecker reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull RangeChecker binding, @NotNull RangeChecker init) {
      return accept(node, super.reduceBindingWithDefault(node, binding, init));
    }

    @NotNull
    @Override
    public RangeChecker reduceBlock(@NotNull Block node, @NotNull ImmutableList<RangeChecker> statements) {
      return accept(node, super.reduceBlock(node, statements));
    }

    @NotNull
    @Override
    public RangeChecker reduceBlockStatement(@NotNull BlockStatement node, @NotNull RangeChecker block) {
      return accept(node, super.reduceBlockStatement(node, block));
    }

    @NotNull
    @Override
    public RangeChecker reduceBreakStatement(@NotNull BreakStatement node) {
      return accept(node, super.reduceBreakStatement(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceCallExpression(@NotNull CallExpression node, @NotNull RangeChecker callee, @NotNull ImmutableList<RangeChecker> arguments) {
      return accept(node, super.reduceCallExpression(node, callee, arguments));
    }

    @NotNull
    @Override
    public RangeChecker reduceCatchClause(@NotNull CatchClause node, @NotNull RangeChecker binding, @NotNull RangeChecker body) {
      return accept(node, super.reduceCatchClause(node, binding, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull RangeChecker name, @NotNull Maybe<RangeChecker> _super, @NotNull ImmutableList<RangeChecker> elements) {
      return accept(node, super.reduceClassDeclaration(node, name, _super, elements));
    }

    @NotNull
    @Override
    public RangeChecker reduceClassElement(@NotNull ClassElement node, @NotNull RangeChecker method) {
      return accept(node, super.reduceClassElement(node, method));
    }

    @NotNull
    @Override
    public RangeChecker reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<RangeChecker> name, @NotNull Maybe<RangeChecker> _super, @NotNull ImmutableList<RangeChecker> elements) {
      return accept(node, super.reduceClassExpression(node, name, _super, elements));
    }

    @NotNull
    @Override
    public RangeChecker reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull RangeChecker binding, @NotNull RangeChecker expression) {
      return accept(node, super.reduceCompoundAssignmentExpression(node, binding, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceComputedMemberAssignmentTarget(@NotNull ComputedMemberAssignmentTarget node, @NotNull RangeChecker object, @NotNull RangeChecker expression) {
      return accept(node, super.reduceComputedMemberAssignmentTarget(node, object, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull RangeChecker object, @NotNull RangeChecker expression) {
      return accept(node, super.reduceComputedMemberExpression(node, object, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull RangeChecker expression) {
      return accept(node, super.reduceComputedPropertyName(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceConditionalExpression(@NotNull ConditionalExpression node, @NotNull RangeChecker test, @NotNull RangeChecker consequent, @NotNull RangeChecker alternate) {
      return accept(node, super.reduceConditionalExpression(node, test, consequent, alternate));
    }

    @NotNull
    @Override
    public RangeChecker reduceContinueStatement(@NotNull ContinueStatement node) {
      return accept(node, super.reduceContinueStatement(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceDataProperty(@NotNull DataProperty node, @NotNull RangeChecker name, @NotNull RangeChecker expression) {
      return accept(node, super.reduceDataProperty(node, name, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceDebuggerStatement(@NotNull DebuggerStatement node) {
      return accept(node, super.reduceDebuggerStatement(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceDirective(@NotNull Directive node) {
      return accept(node, super.reduceDirective(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceDoWhileStatement(@NotNull DoWhileStatement node, @NotNull RangeChecker body, @NotNull RangeChecker test) {
      return accept(node, super.reduceDoWhileStatement(node, body, test));
    }

    @NotNull
    @Override
    public RangeChecker reduceEmptyStatement(@NotNull EmptyStatement node) {
      return accept(node, super.reduceEmptyStatement(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceExport(@NotNull Export node, @NotNull RangeChecker declaration) {
      return accept(node, super.reduceExport(node, declaration));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportAllFrom(@NotNull ExportAllFrom node) {
      return accept(node, super.reduceExportAllFrom(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportDefault(@NotNull ExportDefault node, @NotNull RangeChecker body) {
      return accept(node, super.reduceExportDefault(node, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<RangeChecker> namedExports) {
      return accept(node, super.reduceExportFrom(node, namedExports));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportFromSpecifier(@NotNull ExportFromSpecifier node) {
      return accept(node, super.reduceExportFromSpecifier(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportLocalSpecifier(@NotNull ExportLocalSpecifier node, @NotNull RangeChecker name) {
      return accept(node, super.reduceExportLocalSpecifier(node, name));
    }

    @NotNull
    @Override
    public RangeChecker reduceExportLocals(@NotNull ExportLocals node, @NotNull ImmutableList<RangeChecker> namedExports) {
      return accept(node, super.reduceExportLocals(node, namedExports));
    }

    @NotNull
    @Override
    public RangeChecker reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull RangeChecker expression) {
      return accept(node, super.reduceExpressionStatement(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceForInStatement(@NotNull ForInStatement node, @NotNull RangeChecker left, @NotNull RangeChecker right, @NotNull RangeChecker body) {
      return accept(node, super.reduceForInStatement(node, left, right, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceForOfStatement(@NotNull ForOfStatement node, @NotNull RangeChecker left, @NotNull RangeChecker right, @NotNull RangeChecker body) {
      return accept(node, super.reduceForOfStatement(node, left, right, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<RangeChecker> init, @NotNull Maybe<RangeChecker> test, @NotNull Maybe<RangeChecker> update, @NotNull RangeChecker body) {
      return accept(node, super.reduceForStatement(node, init, test, update, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<RangeChecker> items, @NotNull Maybe<RangeChecker> rest) {
      return accept(node, super.reduceFormalParameters(node, items, rest));
    }

    @NotNull
    @Override
    public RangeChecker reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<RangeChecker> directives, @NotNull ImmutableList<RangeChecker> statements) {
      return accept(node, super.reduceFunctionBody(node, directives, statements));
    }

    @NotNull
    @Override
    public RangeChecker reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull RangeChecker name, @NotNull RangeChecker params, @NotNull RangeChecker body) {
      return accept(node, super.reduceFunctionDeclaration(node, name, params, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<RangeChecker> name, @NotNull RangeChecker params, @NotNull RangeChecker body) {
      return accept(node, super.reduceFunctionExpression(node, name, params, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceGetter(@NotNull Getter node, @NotNull RangeChecker name, @NotNull RangeChecker body) {
      return accept(node, super.reduceGetter(node, name, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceIdentifierExpression(@NotNull IdentifierExpression node) {
      return accept(node, super.reduceIdentifierExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceIfStatement(@NotNull IfStatement node, @NotNull RangeChecker test, @NotNull RangeChecker consequent, @NotNull Maybe<RangeChecker> alternate) {
      return accept(node, super.reduceIfStatement(node, test, consequent, alternate));
    }

    @NotNull
    @Override
    public RangeChecker reduceImport(@NotNull Import node, @NotNull Maybe<RangeChecker> defaultBinding, @NotNull ImmutableList<RangeChecker> namedImports) {
      return accept(node, super.reduceImport(node, defaultBinding, namedImports));
    }

    @NotNull
    @Override
    public RangeChecker reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<RangeChecker> defaultBinding, @NotNull RangeChecker namespaceBinding) {
      return accept(node, super.reduceImportNamespace(node, defaultBinding, namespaceBinding));
    }

    @NotNull
    @Override
    public RangeChecker reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull RangeChecker binding) {
      return accept(node, super.reduceImportSpecifier(node, binding));
    }

    @NotNull
    @Override
    public RangeChecker reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull RangeChecker body) {
      return accept(node, super.reduceLabeledStatement(node, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
      return accept(node, super.reduceLiteralBooleanExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
      return accept(node, super.reduceLiteralInfinityExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
      return accept(node, super.reduceLiteralNullExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
      return accept(node, super.reduceLiteralNumericExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
      return accept(node, super.reduceLiteralRegExpExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
      return accept(node, super.reduceLiteralStringExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceMethod(@NotNull Method node, @NotNull RangeChecker name, @NotNull RangeChecker params, @NotNull RangeChecker body) {
      return accept(node, super.reduceMethod(node, name, params, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceModule(@NotNull Module node, @NotNull ImmutableList<RangeChecker> directives, @NotNull ImmutableList<RangeChecker> items) {
      return accept(node, super.reduceModule(node, directives, items));
    }

    @NotNull
    @Override
    public RangeChecker reduceNewExpression(@NotNull NewExpression node, @NotNull RangeChecker callee, @NotNull ImmutableList<RangeChecker> arguments) {
      return accept(node, super.reduceNewExpression(node, callee, arguments));
    }

    @NotNull
    @Override
    public RangeChecker reduceNewTargetExpression(@NotNull NewTargetExpression node) {
      return accept(node, super.reduceNewTargetExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceObjectAssignmentTarget(@NotNull ObjectAssignmentTarget node, @NotNull ImmutableList<RangeChecker> properties) {
      return accept(node, super.reduceObjectAssignmentTarget(node, properties));
    }

    @NotNull
    @Override
    public RangeChecker reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<RangeChecker> properties) {
      return accept(node, super.reduceObjectBinding(node, properties));
    }

    @NotNull
    @Override
    public RangeChecker reduceObjectExpression(@NotNull ObjectExpression node, @NotNull ImmutableList<RangeChecker> properties) {
      return accept(node, super.reduceObjectExpression(node, properties));
    }

    @NotNull
    @Override
    public RangeChecker reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<RangeChecker> expression) {
      return accept(node, super.reduceReturnStatement(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceScript(@NotNull Script node, @NotNull ImmutableList<RangeChecker> directives, @NotNull ImmutableList<RangeChecker> statements) {
      return accept(node, super.reduceScript(node, directives, statements));
    }

    @NotNull
    @Override
    public RangeChecker reduceSetter(@NotNull Setter node, @NotNull RangeChecker name, @NotNull RangeChecker param, @NotNull RangeChecker body) {
      return accept(node, super.reduceSetter(node, name, param, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceShorthandProperty(@NotNull ShorthandProperty node, @NotNull RangeChecker name) {
      return accept(node, super.reduceShorthandProperty(node, name));
    }

    @NotNull
    @Override
    public RangeChecker reduceSpreadElement(@NotNull SpreadElement node, @NotNull RangeChecker expression) {
      return accept(node, super.reduceSpreadElement(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceStaticMemberAssignmentTarget(@NotNull StaticMemberAssignmentTarget node, @NotNull RangeChecker object) {
      return accept(node, super.reduceStaticMemberAssignmentTarget(node, object));
    }

    @NotNull
    @Override
    public RangeChecker reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull RangeChecker object) {
      return accept(node, super.reduceStaticMemberExpression(node, object));
    }

    @NotNull
    @Override
    public RangeChecker reduceStaticPropertyName(@NotNull StaticPropertyName node) {
      return accept(node, super.reduceStaticPropertyName(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceSuper(@NotNull Super node) {
      return accept(node, super.reduceSuper(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceSwitchCase(@NotNull SwitchCase node, @NotNull RangeChecker test, @NotNull ImmutableList<RangeChecker> consequent) {
      return accept(node, super.reduceSwitchCase(node, test, consequent));
    }

    @NotNull
    @Override
    public RangeChecker reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<RangeChecker> consequent) {
      return accept(node, super.reduceSwitchDefault(node, consequent));
    }

    @NotNull
    @Override
    public RangeChecker reduceSwitchStatement(@NotNull SwitchStatement node, @NotNull RangeChecker discriminant, @NotNull ImmutableList<RangeChecker> cases) {
      return accept(node, super.reduceSwitchStatement(node, discriminant, cases));
    }

    @NotNull
    @Override
    public RangeChecker reduceSwitchStatementWithDefault(@NotNull SwitchStatementWithDefault node, @NotNull RangeChecker discriminant, @NotNull ImmutableList<RangeChecker> preDefaultCases, @NotNull RangeChecker defaultCase, @NotNull ImmutableList<RangeChecker> postDefaultCases) {
      return accept(node, super.reduceSwitchStatementWithDefault(node, discriminant, preDefaultCases, defaultCase, postDefaultCases));
    }

    @NotNull
    @Override
    public RangeChecker reduceTemplateElement(@NotNull TemplateElement node) {
      return accept(node, super.reduceTemplateElement(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<RangeChecker> tag, @NotNull ImmutableList<RangeChecker> elements) {
      return accept(node, super.reduceTemplateExpression(node, tag, elements));
    }

    @NotNull
    @Override
    public RangeChecker reduceThisExpression(@NotNull ThisExpression node) {
      return accept(node, super.reduceThisExpression(node));
    }

    @NotNull
    @Override
    public RangeChecker reduceThrowStatement(@NotNull ThrowStatement node, @NotNull RangeChecker expression) {
      return accept(node, super.reduceThrowStatement(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceTryCatchStatement(@NotNull TryCatchStatement node, @NotNull RangeChecker body, @NotNull RangeChecker catchClause) {
      return accept(node, super.reduceTryCatchStatement(node, body, catchClause));
    }

    @NotNull
    @Override
    public RangeChecker reduceTryFinallyStatement(@NotNull TryFinallyStatement node, @NotNull RangeChecker body, @NotNull Maybe<RangeChecker> catchClause, @NotNull RangeChecker finalizer) {
      return accept(node, super.reduceTryFinallyStatement(node, body, catchClause, finalizer));
    }

    @NotNull
    @Override
    public RangeChecker reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull RangeChecker operand) {
      return accept(node, super.reduceUnaryExpression(node, operand));
    }

    @NotNull
    @Override
    public RangeChecker reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull RangeChecker operand) {
      return accept(node, super.reduceUpdateExpression(node, operand));
    }

    @NotNull
    @Override
    public RangeChecker reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<RangeChecker> declarators) {
      return accept(node, super.reduceVariableDeclaration(node, declarators));
    }

    @NotNull
    @Override
    public RangeChecker reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull RangeChecker declaration) {
      return accept(node, super.reduceVariableDeclarationStatement(node, declaration));
    }

    @NotNull
    @Override
    public RangeChecker reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull RangeChecker binding, @NotNull Maybe<RangeChecker> init) {
      return accept(node, super.reduceVariableDeclarator(node, binding, init));
    }

    @NotNull
    @Override
    public RangeChecker reduceWhileStatement(@NotNull WhileStatement node, @NotNull RangeChecker test, @NotNull RangeChecker body) {
      return accept(node, super.reduceWhileStatement(node, test, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceWithStatement(@NotNull WithStatement node, @NotNull RangeChecker object, @NotNull RangeChecker body) {
      return accept(node, super.reduceWithStatement(node, object, body));
    }

    @NotNull
    @Override
    public RangeChecker reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<RangeChecker> expression) {
      return accept(node, super.reduceYieldExpression(node, expression));
    }

    @NotNull
    @Override
    public RangeChecker reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull RangeChecker expression) {
      return accept(node, super.reduceYieldGeneratorExpression(node, expression));
    }
}