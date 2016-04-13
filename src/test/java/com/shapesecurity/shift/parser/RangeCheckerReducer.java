/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"));
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

package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.visitor.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.assertTrue;

public class RangeCheckerReducer extends MonoidalReducer<RangeCheckerReducer.RangeChecker> {
	private final ParserWithLocation parserWithLocation;

	protected RangeCheckerReducer(ParserWithLocation parserWithLocation) {
		super(RangeChecker.MONOID);
		this.parserWithLocation = parserWithLocation;
	}

	private RangeChecker accept(Node node, RangeChecker innerBounds) {
		Maybe<SourceSpan> span = this.parserWithLocation.getLocation(node);
		assertTrue(span.isJust());
		RangeChecker outerBounds = new RangeChecker(span.just());
		assertTrue(outerBounds.start <= outerBounds.end);

		assertTrue(outerBounds.start <= innerBounds.start);
		assertTrue(innerBounds.end <= outerBounds.end);

		return outerBounds;
	}

	@NotNull
	@Override
	public RangeChecker reduceArrayBinding(
		@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<RangeChecker>> elements,
		@NotNull Maybe<RangeChecker> restElement
	) {
		return accept(node, super.reduceArrayBinding(node, elements, restElement));
	}

	@NotNull
	@Override
	public RangeChecker reduceArrayExpression(
		@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<RangeChecker>> elements
	) {
		return accept(node, super.reduceArrayExpression(node, elements));
	}

	@NotNull
	@Override
	public RangeChecker reduceArrowExpression(
		@NotNull ArrowExpression node, @NotNull RangeChecker params, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceArrowExpression(node, params, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceAssignmentExpression(
		@NotNull AssignmentExpression node, @NotNull RangeChecker binding, @NotNull RangeChecker expression
	) {
		return accept(node, super.reduceAssignmentExpression(node, binding, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceBinaryExpression(
		@NotNull BinaryExpression node, @NotNull RangeChecker left, @NotNull RangeChecker right
	) {
		return accept(node, super.reduceBinaryExpression(node, left, right));
	}

	@NotNull
	@Override
	public RangeChecker reduceBindingIdentifier(@NotNull BindingIdentifier node) {
		return accept(node, super.reduceBindingIdentifier(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceBindingPropertyIdentifier(
		@NotNull BindingPropertyIdentifier node, @NotNull RangeChecker binding, @NotNull Maybe<RangeChecker> init
	) {
		return accept(node, super.reduceBindingPropertyIdentifier(node, binding, init));
	}

	@NotNull
	@Override
	public RangeChecker reduceBindingPropertyProperty(
		@NotNull BindingPropertyProperty node, @NotNull RangeChecker name, @NotNull RangeChecker binding
	) {
		return accept(node, super.reduceBindingPropertyProperty(node, name, binding));
	}

	@NotNull
	@Override
	public RangeChecker reduceBindingWithDefault(
		@NotNull BindingWithDefault node, @NotNull RangeChecker binding, @NotNull RangeChecker init
	) {
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
	public RangeChecker reduceCallExpression(
		@NotNull CallExpression node, @NotNull RangeChecker callee, @NotNull ImmutableList<RangeChecker> arguments
	) {
		return accept(node, super.reduceCallExpression(node, callee, arguments));
	}

	@NotNull
	@Override
	public RangeChecker reduceCatchClause(
		@NotNull CatchClause node, @NotNull RangeChecker binding, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceCatchClause(node, binding, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceClassDeclaration(
		@NotNull ClassDeclaration node, @NotNull RangeChecker name, @NotNull Maybe<RangeChecker> _super,
		@NotNull ImmutableList<RangeChecker> elements
	) {
		return accept(node, super.reduceClassDeclaration(node, name, _super, elements));
	}

	@NotNull
	@Override
	public RangeChecker reduceClassElement(@NotNull ClassElement node, @NotNull RangeChecker method) {
		return accept(node, super.reduceClassElement(node, method));
	}

	@NotNull
	@Override
	public RangeChecker reduceClassExpression(
		@NotNull ClassExpression node, @NotNull Maybe<RangeChecker> name, @NotNull Maybe<RangeChecker> _super,
		@NotNull ImmutableList<RangeChecker> elements
	) {
		return accept(node, super.reduceClassExpression(node, name, _super, elements));
	}

	@NotNull
	@Override
	public RangeChecker reduceCompoundAssignmentExpression(
		@NotNull CompoundAssignmentExpression node, @NotNull RangeChecker binding, @NotNull RangeChecker expression
	) {
		return accept(node, super.reduceCompoundAssignmentExpression(node, binding, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceComputedMemberExpression(
		@NotNull ComputedMemberExpression node, @NotNull RangeChecker object, @NotNull RangeChecker expression
	) {
		return accept(node, super.reduceComputedMemberExpression(node, object, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceComputedPropertyName(
		@NotNull ComputedPropertyName node, @NotNull RangeChecker expression
	) {
		return accept(node, super.reduceComputedPropertyName(node, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceConditionalExpression(
		@NotNull ConditionalExpression node, @NotNull RangeChecker test, @NotNull RangeChecker consequent,
		@NotNull RangeChecker alternate
	) {
		return accept(node, super.reduceConditionalExpression(node, test, consequent, alternate));
	}

	@NotNull
	@Override
	public RangeChecker reduceContinueStatement(@NotNull ContinueStatement node) {
		return accept(node, super.reduceContinueStatement(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceDataProperty(
		@NotNull DataProperty node, @NotNull RangeChecker expression, @NotNull RangeChecker name
	) {
		// NOTE: these are being passed to reduceDataProperty in the opposite order because
		// DataProperty's parameters are in the opposite order of their source position. :(
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
	public RangeChecker reduceDoWhileStatement(
		@NotNull DoWhileStatement node, @NotNull RangeChecker body, @NotNull RangeChecker test
	) {
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
	public RangeChecker reduceExportSpecifier(@NotNull ExportSpecifier node) {
		return accept(node, super.reduceExportSpecifier(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull RangeChecker expression) {
		return accept(node, super.reduceExpressionStatement(node, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceForInStatement(
		@NotNull ForInStatement node, @NotNull RangeChecker left, @NotNull RangeChecker right,
		@NotNull RangeChecker body
	) {
		return accept(node, super.reduceForInStatement(node, left, right, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceForOfStatement(
		@NotNull ForOfStatement node, @NotNull RangeChecker left, @NotNull RangeChecker right,
		@NotNull RangeChecker body
	) {
		return accept(node, super.reduceForOfStatement(node, left, right, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceForStatement(
		@NotNull ForStatement node, @NotNull Maybe<RangeChecker> init, @NotNull Maybe<RangeChecker> test,
		@NotNull Maybe<RangeChecker> update, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceForStatement(node, init, test, update, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceFormalParameters(
		@NotNull FormalParameters node, @NotNull ImmutableList<RangeChecker> items, @NotNull Maybe<RangeChecker> rest
	) {
		return accept(node, super.reduceFormalParameters(node, items, rest));
	}

	@NotNull
	@Override
	public RangeChecker reduceFunctionBody(
		@NotNull FunctionBody node, @NotNull ImmutableList<RangeChecker> directives,
		@NotNull ImmutableList<RangeChecker> statements
	) {
		return accept(node, super.reduceFunctionBody(node, directives, statements));
	}

	@NotNull
	@Override
	public RangeChecker reduceFunctionDeclaration(
		@NotNull FunctionDeclaration node, @NotNull RangeChecker name, @NotNull RangeChecker params,
		@NotNull RangeChecker body
	) {
		return accept(node, super.reduceFunctionDeclaration(node, name, params, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceFunctionExpression(
		@NotNull FunctionExpression node, @NotNull Maybe<RangeChecker> name, @NotNull RangeChecker params,
		@NotNull RangeChecker body
	) {
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
	public RangeChecker reduceIfStatement(
		@NotNull IfStatement node, @NotNull RangeChecker test, @NotNull RangeChecker consequent,
		@NotNull Maybe<RangeChecker> alternate
	) {
		return accept(node, super.reduceIfStatement(node, test, consequent, alternate));
	}

	@NotNull
	@Override
	public RangeChecker reduceImport(
		@NotNull Import node, @NotNull Maybe<RangeChecker> defaultBinding,
		@NotNull ImmutableList<RangeChecker> namedImports
	) {
		return accept(node, super.reduceImport(node, defaultBinding, namedImports));
	}

	@NotNull
	@Override
	public RangeChecker reduceImportNamespace(
		@NotNull ImportNamespace node, @NotNull Maybe<RangeChecker> defaultBinding,
		@NotNull RangeChecker namespaceBinding
	) {
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
	public RangeChecker reduceMethod(
		@NotNull Method node, @NotNull RangeChecker name, @NotNull RangeChecker params, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceMethod(node, name, params, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceModule(
		@NotNull Module node, @NotNull ImmutableList<RangeChecker> directives,
		@NotNull ImmutableList<RangeChecker> items
	) {
		return accept(node, super.reduceModule(node, directives, items));
	}

	@NotNull
	@Override
	public RangeChecker reduceNewExpression(
		@NotNull NewExpression node, @NotNull RangeChecker callee, @NotNull ImmutableList<RangeChecker> arguments
	) {
		return accept(node, super.reduceNewExpression(node, callee, arguments));
	}

	@NotNull
	@Override
	public RangeChecker reduceNewTargetExpression(@NotNull NewTargetExpression node) {
		return accept(node, super.reduceNewTargetExpression(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceObjectBinding(
		@NotNull ObjectBinding node, @NotNull ImmutableList<RangeChecker> properties
	) {
		return accept(node, super.reduceObjectBinding(node, properties));
	}

	@NotNull
	@Override
	public RangeChecker reduceObjectExpression(
		@NotNull ObjectExpression node, @NotNull ImmutableList<RangeChecker> properties
	) {
		return accept(node, super.reduceObjectExpression(node, properties));
	}

	@NotNull
	@Override
	public RangeChecker reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<RangeChecker> expression) {
		return accept(node, super.reduceReturnStatement(node, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceScript(
		@NotNull Script node, @NotNull ImmutableList<RangeChecker> directives,
		@NotNull ImmutableList<RangeChecker> statements
	) {
		return accept(node, super.reduceScript(node, directives, statements));
	}

	@NotNull
	@Override
	public RangeChecker reduceSetter(
		@NotNull Setter node, @NotNull RangeChecker name, @NotNull RangeChecker parameter, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceSetter(node, name, parameter, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceShorthandProperty(@NotNull ShorthandProperty node) {
		return accept(node, super.reduceShorthandProperty(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceSpreadElement(@NotNull SpreadElement node, @NotNull RangeChecker expression) {
		return accept(node, super.reduceSpreadElement(node, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceStaticMemberExpression(
		@NotNull StaticMemberExpression node, @NotNull RangeChecker object
	) {
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
	public RangeChecker reduceSwitchCase(
		@NotNull SwitchCase node, @NotNull RangeChecker test, @NotNull ImmutableList<RangeChecker> consequent
	) {
		return accept(node, super.reduceSwitchCase(node, test, consequent));
	}

	@NotNull
	@Override
	public RangeChecker reduceSwitchDefault(
		@NotNull SwitchDefault node, @NotNull ImmutableList<RangeChecker> consequent
	) {
		return accept(node, super.reduceSwitchDefault(node, consequent));
	}

	@NotNull
	@Override
	public RangeChecker reduceSwitchStatement(
		@NotNull SwitchStatement node, @NotNull RangeChecker discriminant, @NotNull ImmutableList<RangeChecker> cases
	) {
		return accept(node, super.reduceSwitchStatement(node, discriminant, cases));
	}

	@NotNull
	@Override
	public RangeChecker reduceSwitchStatementWithDefault(
		@NotNull SwitchStatementWithDefault node, @NotNull RangeChecker discriminant,
		@NotNull ImmutableList<RangeChecker> preDefaultCases, @NotNull RangeChecker defaultCase,
		@NotNull ImmutableList<RangeChecker> postDefaultCases
	) {
		return accept(node,
			super.reduceSwitchStatementWithDefault(node, discriminant, preDefaultCases, defaultCase, postDefaultCases)
		);
	}

	@NotNull
	@Override
	public RangeChecker reduceTemplateElement(@NotNull TemplateElement node) {
		return accept(node, super.reduceTemplateElement(node));
	}

	@NotNull
	@Override
	public RangeChecker reduceTemplateExpression(
		@NotNull TemplateExpression node, @NotNull Maybe<RangeChecker> tag,
		@NotNull ImmutableList<RangeChecker> elements
	) {
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
	public RangeChecker reduceTryCatchStatement(
		@NotNull TryCatchStatement node, @NotNull RangeChecker block, @NotNull RangeChecker catchClause
	) {
		return accept(node, super.reduceTryCatchStatement(node, block, catchClause));
	}

	@NotNull
	@Override
	public RangeChecker reduceTryFinallyStatement(
		@NotNull TryFinallyStatement node, @NotNull RangeChecker block, @NotNull Maybe<RangeChecker> catchClause,
		@NotNull RangeChecker finalizer
	) {
		return accept(node, super.reduceTryFinallyStatement(node, block, catchClause, finalizer));
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
	public RangeChecker reduceVariableDeclaration(
		@NotNull VariableDeclaration node, @NotNull ImmutableList<RangeChecker> declarators
	) {
		return accept(node, super.reduceVariableDeclaration(node, declarators));
	}

	@NotNull
	@Override
	public RangeChecker reduceVariableDeclarationStatement(
		@NotNull VariableDeclarationStatement node, @NotNull RangeChecker declaration
	) {
		return accept(node, super.reduceVariableDeclarationStatement(node, declaration));
	}

	@NotNull
	@Override
	public RangeChecker reduceVariableDeclarator(
		@NotNull VariableDeclarator node, @NotNull RangeChecker binding, @NotNull Maybe<RangeChecker> init
	) {
		return accept(node, super.reduceVariableDeclarator(node, binding, init));
	}

	@NotNull
	@Override
	public RangeChecker reduceWhileStatement(
		@NotNull WhileStatement node, @NotNull RangeChecker test, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceWhileStatement(node, test, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceWithStatement(
		@NotNull WithStatement node, @NotNull RangeChecker object, @NotNull RangeChecker body
	) {
		return accept(node, super.reduceWithStatement(node, object, body));
	}

	@NotNull
	@Override
	public RangeChecker reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<RangeChecker> expression) {
		return accept(node, super.reduceYieldExpression(node, expression));
	}

	@NotNull
	@Override
	public RangeChecker reduceYieldGeneratorExpression(
		@NotNull YieldGeneratorExpression node, @NotNull RangeChecker expression
	) {
		return accept(node, super.reduceYieldGeneratorExpression(node, expression));
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
}
