package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.Module;

import javax.annotation.Nonnull;

public class WrappedReducer<T> implements Reducer<T> {

	@Nonnull
	private final F2<Node, T, T> wrap;

	@Nonnull
	private final Reducer<T> reducer;

	public WrappedReducer(@Nonnull F2<Node, T, T> wrap, @Nonnull Reducer<T> reducer) {
		this.wrap = wrap;
		this.reducer = reducer;
	}

	@Nonnull
	@Override
	public T reduceArrayAssignmentTarget(@Nonnull ArrayAssignmentTarget node, @Nonnull ImmutableList<Maybe<T>> elements, @Nonnull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceArrayAssignmentTarget(node, elements, rest));
	}

	@Nonnull
	@Override
	public T reduceArrayBinding(@Nonnull ArrayBinding node, @Nonnull ImmutableList<Maybe<T>> elements, @Nonnull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceArrayBinding(node, elements, rest));
	}

	@Nonnull
	@Override
	public T reduceArrayExpression(@Nonnull ArrayExpression node, @Nonnull ImmutableList<Maybe<T>> elements) {
		return wrap.apply(node, reducer.reduceArrayExpression(node, elements));
	}

	@Nonnull
	@Override
	public T reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull T params, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceArrowExpression(node, params, body));
	}

	@Nonnull
	@Override
	public T reduceAssignmentExpression(@Nonnull AssignmentExpression node, @Nonnull T binding, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceAssignmentExpression(node, binding, expression));
	}

	@Nonnull
	@Override
	public T reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
		return wrap.apply(node, reducer.reduceAssignmentTargetIdentifier(node));
	}

	@Nonnull
	@Override
	public T reduceAssignmentTargetPropertyIdentifier(@Nonnull AssignmentTargetPropertyIdentifier node, @Nonnull T binding, @Nonnull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceAssignmentTargetPropertyIdentifier(node, binding, init));
	}

	@Nonnull
	@Override
	public T reduceAssignmentTargetPropertyProperty(@Nonnull AssignmentTargetPropertyProperty node, @Nonnull T name, @Nonnull T binding) {
		return wrap.apply(node, reducer.reduceAssignmentTargetPropertyProperty(node, name, binding));
	}

	@Nonnull
	@Override
	public T reduceAssignmentTargetWithDefault(@Nonnull AssignmentTargetWithDefault node, @Nonnull T binding, @Nonnull T init) {
		return wrap.apply(node, reducer.reduceAssignmentTargetWithDefault(node, binding, init));
	}

	@Nonnull
	@Override
	public T reduceAwaitExpression(@Nonnull AwaitExpression node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceAwaitExpression(node, expression));
	}

	@Nonnull
	@Override
	public T reduceBinaryExpression(@Nonnull BinaryExpression node, @Nonnull T left, @Nonnull T right) {
		return wrap.apply(node, reducer.reduceBinaryExpression(node, left, right));
	}

	@Nonnull
	@Override
	public T reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
		return wrap.apply(node, reducer.reduceBindingIdentifier(node));
	}

	@Nonnull
	@Override
	public T reduceBindingPropertyIdentifier(@Nonnull BindingPropertyIdentifier node, @Nonnull T binding, @Nonnull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceBindingPropertyIdentifier(node, binding, init));
	}

	@Nonnull
	@Override
	public T reduceBindingPropertyProperty(@Nonnull BindingPropertyProperty node, @Nonnull T name, @Nonnull T binding) {
		return wrap.apply(node, reducer.reduceBindingPropertyProperty(node, name, binding));
	}

	@Nonnull
	@Override
	public T reduceBindingWithDefault(@Nonnull BindingWithDefault node, @Nonnull T binding, @Nonnull T init) {
		return wrap.apply(node, reducer.reduceBindingWithDefault(node, binding, init));
	}

	@Nonnull
	@Override
	public T reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceBlock(node, statements));
	}

	@Nonnull
	@Override
	public T reduceBlockStatement(@Nonnull BlockStatement node, @Nonnull T block) {
		return wrap.apply(node, reducer.reduceBlockStatement(node, block));
	}

	@Nonnull
	@Override
	public T reduceBreakStatement(@Nonnull BreakStatement node) {
		return wrap.apply(node, reducer.reduceBreakStatement(node));
	}

	@Nonnull
	@Override
	public T reduceCallExpression(@Nonnull CallExpression node, @Nonnull T callee, @Nonnull ImmutableList<T> arguments) {
		return wrap.apply(node, reducer.reduceCallExpression(node, callee, arguments));
	}

	@Nonnull
	@Override
	public T reduceCatchClause(@Nonnull CatchClause node, @Nonnull T binding, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceCatchClause(node, binding, body));
	}

	@Nonnull
	@Override
	public T reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull T name, @Nonnull Maybe<T> _super, @Nonnull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceClassDeclaration(node, name, _super, elements));
	}

	@Nonnull
	@Override
	public T reduceClassElement(@Nonnull ClassElement node, @Nonnull T method) {
		return wrap.apply(node, reducer.reduceClassElement(node, method));
	}

	@Nonnull
	@Override
	public T reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<T> name, @Nonnull Maybe<T> _super, @Nonnull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceClassExpression(node, name, _super, elements));
	}

	@Nonnull
	@Override
	public T reduceCompoundAssignmentExpression(@Nonnull CompoundAssignmentExpression node, @Nonnull T binding, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceCompoundAssignmentExpression(node, binding, expression));
	}

	@Nonnull
	@Override
	public T reduceComputedMemberAssignmentTarget(@Nonnull ComputedMemberAssignmentTarget node, @Nonnull T object, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceComputedMemberAssignmentTarget(node, object, expression));
	}

	@Nonnull
	@Override
	public T reduceComputedMemberExpression(@Nonnull ComputedMemberExpression node, @Nonnull T object, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceComputedMemberExpression(node, object, expression));
	}

	@Nonnull
	@Override
	public T reduceComputedPropertyName(@Nonnull ComputedPropertyName node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceComputedPropertyName(node, expression));
	}

	@Nonnull
	@Override
	public T reduceConditionalExpression(@Nonnull ConditionalExpression node, @Nonnull T test, @Nonnull T consequent, @Nonnull T alternate) {
		return wrap.apply(node, reducer.reduceConditionalExpression(node, test, consequent, alternate));
	}

	@Nonnull
	@Override
	public T reduceContinueStatement(@Nonnull ContinueStatement node) {
		return wrap.apply(node, reducer.reduceContinueStatement(node));
	}

	@Nonnull
	@Override
	public T reduceDataProperty(@Nonnull DataProperty node, @Nonnull T name, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceDataProperty(node, name, expression));
	}

	@Nonnull
	@Override
	public T reduceDebuggerStatement(@Nonnull DebuggerStatement node) {
		return wrap.apply(node, reducer.reduceDebuggerStatement(node));
	}

	@Nonnull
	@Override
	public T reduceDirective(@Nonnull Directive node) {
		return wrap.apply(node, reducer.reduceDirective(node));
	}

	@Nonnull
	@Override
	public T reduceDoWhileStatement(@Nonnull DoWhileStatement node, @Nonnull T body, @Nonnull T test) {
		return wrap.apply(node, reducer.reduceDoWhileStatement(node, body, test));
	}

	@Nonnull
	@Override
	public T reduceEmptyStatement(@Nonnull EmptyStatement node) {
		return wrap.apply(node, reducer.reduceEmptyStatement(node));
	}

	@Nonnull
	@Override
	public T reduceExport(@Nonnull Export node, @Nonnull T declaration) {
		return wrap.apply(node, reducer.reduceExport(node, declaration));
	}

	@Nonnull
	@Override
	public T reduceExportAllFrom(@Nonnull ExportAllFrom node) {
		return wrap.apply(node, reducer.reduceExportAllFrom(node));
	}

	@Nonnull
	@Override
	public T reduceExportDefault(@Nonnull ExportDefault node, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceExportDefault(node, body));
	}

	@Nonnull
	@Override
	public T reduceExportFrom(@Nonnull ExportFrom node, @Nonnull ImmutableList<T> namedExports) {
		return wrap.apply(node, reducer.reduceExportFrom(node, namedExports));
	}

	@Nonnull
	@Override
	public T reduceExportFromSpecifier(@Nonnull ExportFromSpecifier node) {
		return wrap.apply(node, reducer.reduceExportFromSpecifier(node));
	}

	@Nonnull
	@Override
	public T reduceExportLocalSpecifier(@Nonnull ExportLocalSpecifier node, @Nonnull T name) {
		return wrap.apply(node, reducer.reduceExportLocalSpecifier(node, name));
	}

	@Nonnull
	@Override
	public T reduceExportLocals(@Nonnull ExportLocals node, @Nonnull ImmutableList<T> namedExports) {
		return wrap.apply(node, reducer.reduceExportLocals(node, namedExports));
	}

	@Nonnull
	@Override
	public T reduceExpressionStatement(@Nonnull ExpressionStatement node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceExpressionStatement(node, expression));
	}

	@Nonnull
	@Override
	public T reduceForInStatement(@Nonnull ForInStatement node, @Nonnull T left, @Nonnull T right, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceForInStatement(node, left, right, body));
	}

	@Nonnull
	@Override
	public T reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull T left, @Nonnull T right, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceForOfStatement(node, left, right, body));
	}

	@Nonnull
	@Override
	public T reduceForStatement(@Nonnull ForStatement node, @Nonnull Maybe<T> init, @Nonnull Maybe<T> test, @Nonnull Maybe<T> update, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceForStatement(node, init, test, update, body));
	}

	@Nonnull
	@Override
	public T reduceFormalParameters(@Nonnull FormalParameters node, @Nonnull ImmutableList<T> items, @Nonnull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceFormalParameters(node, items, rest));
	}

	@Nonnull
	@Override
	public T reduceFunctionBody(@Nonnull FunctionBody node, @Nonnull ImmutableList<T> directives, @Nonnull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceFunctionBody(node, directives, statements));
	}

	@Nonnull
	@Override
	public T reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull T name, @Nonnull T params, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceFunctionDeclaration(node, name, params, body));
	}

	@Nonnull
	@Override
	public T reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<T> name, @Nonnull T params, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceFunctionExpression(node, name, params, body));
	}

	@Nonnull
	@Override
	public T reduceGetter(@Nonnull Getter node, @Nonnull T name, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceGetter(node, name, body));
	}

	@Nonnull
	@Override
	public T reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
		return wrap.apply(node, reducer.reduceIdentifierExpression(node));
	}

	@Nonnull
	@Override
	public T reduceIfStatement(@Nonnull IfStatement node, @Nonnull T test, @Nonnull T consequent, @Nonnull Maybe<T> alternate) {
		return wrap.apply(node, reducer.reduceIfStatement(node, test, consequent, alternate));
	}

	@Nonnull
	@Override
	public T reduceImport(@Nonnull Import node, @Nonnull Maybe<T> defaultBinding, @Nonnull ImmutableList<T> namedImports) {
		return wrap.apply(node, reducer.reduceImport(node, defaultBinding, namedImports));
	}

	@Nonnull
	@Override
	public T reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<T> defaultBinding, @Nonnull T namespaceBinding) {
		return wrap.apply(node, reducer.reduceImportNamespace(node, defaultBinding, namespaceBinding));
	}

	@Nonnull
	@Override
	public T reduceImportSpecifier(@Nonnull ImportSpecifier node, @Nonnull T binding) {
		return wrap.apply(node, reducer.reduceImportSpecifier(node, binding));
	}

	@Nonnull
	@Override
	public T reduceLabeledStatement(@Nonnull LabeledStatement node, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceLabeledStatement(node, body));
	}

	@Nonnull
	@Override
	public T reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node) {
		return wrap.apply(node, reducer.reduceLiteralBooleanExpression(node));
	}

	@Nonnull
	@Override
	public T reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node) {
		return wrap.apply(node, reducer.reduceLiteralInfinityExpression(node));
	}

	@Nonnull
	@Override
	public T reduceLiteralNullExpression(@Nonnull LiteralNullExpression node) {
		return wrap.apply(node, reducer.reduceLiteralNullExpression(node));
	}

	@Nonnull
	@Override
	public T reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node) {
		return wrap.apply(node, reducer.reduceLiteralNumericExpression(node));
	}

	@Nonnull
	@Override
	public T reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
		return wrap.apply(node, reducer.reduceLiteralRegExpExpression(node));
	}

	@Nonnull
	@Override
	public T reduceLiteralStringExpression(@Nonnull LiteralStringExpression node) {
		return wrap.apply(node, reducer.reduceLiteralStringExpression(node));
	}

	@Nonnull
	@Override
	public T reduceMethod(@Nonnull Method node, @Nonnull T name, @Nonnull T params, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceMethod(node, name, params, body));
	}

	@Nonnull
	@Override
	public T reduceModule(@Nonnull Module node, @Nonnull ImmutableList<T> directives, @Nonnull ImmutableList<T> items) {
		return wrap.apply(node, reducer.reduceModule(node, directives, items));
	}

	@Nonnull
	@Override
	public T reduceNewExpression(@Nonnull NewExpression node, @Nonnull T callee, @Nonnull ImmutableList<T> arguments) {
		return wrap.apply(node, reducer.reduceNewExpression(node, callee, arguments));
	}

	@Nonnull
	@Override
	public T reduceNewTargetExpression(@Nonnull NewTargetExpression node) {
		return wrap.apply(node, reducer.reduceNewTargetExpression(node));
	}

	@Nonnull
	@Override
	public T reduceObjectAssignmentTarget(@Nonnull ObjectAssignmentTarget node, @Nonnull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectAssignmentTarget(node, properties));
	}

	@Nonnull
	@Override
	public T reduceObjectBinding(@Nonnull ObjectBinding node, @Nonnull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectBinding(node, properties));
	}

	@Nonnull
	@Override
	public T reduceObjectExpression(@Nonnull ObjectExpression node, @Nonnull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectExpression(node, properties));
	}

	@Nonnull
	@Override
	public T reduceReturnStatement(@Nonnull ReturnStatement node, @Nonnull Maybe<T> expression) {
		return wrap.apply(node, reducer.reduceReturnStatement(node, expression));
	}

	@Nonnull
	@Override
	public T reduceScript(@Nonnull Script node, @Nonnull ImmutableList<T> directives, @Nonnull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceScript(node, directives, statements));
	}

	@Nonnull
	@Override
	public T reduceSetter(@Nonnull Setter node, @Nonnull T name, @Nonnull T param, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceSetter(node, name, param, body));
	}

	@Nonnull
	@Override
	public T reduceShorthandProperty(@Nonnull ShorthandProperty node, @Nonnull T name) {
		return wrap.apply(node, reducer.reduceShorthandProperty(node, name));
	}

	@Nonnull
	@Override
	public T reduceSpreadElement(@Nonnull SpreadElement node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceSpreadElement(node, expression));
	}

	@Nonnull
	@Override
	public T reduceStaticMemberAssignmentTarget(@Nonnull StaticMemberAssignmentTarget node, @Nonnull T object) {
		return wrap.apply(node, reducer.reduceStaticMemberAssignmentTarget(node, object));
	}

	@Nonnull
	@Override
	public T reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull T object) {
		return wrap.apply(node, reducer.reduceStaticMemberExpression(node, object));
	}

	@Nonnull
	@Override
	public T reduceStaticPropertyName(@Nonnull StaticPropertyName node) {
		return wrap.apply(node, reducer.reduceStaticPropertyName(node));
	}

	@Nonnull
	@Override
	public T reduceSuper(@Nonnull Super node) {
		return wrap.apply(node, reducer.reduceSuper(node));
	}

	@Nonnull
	@Override
	public T reduceSwitchCase(@Nonnull SwitchCase node, @Nonnull T test, @Nonnull ImmutableList<T> consequent) {
		return wrap.apply(node, reducer.reduceSwitchCase(node, test, consequent));
	}

	@Nonnull
	@Override
	public T reduceSwitchDefault(@Nonnull SwitchDefault node, @Nonnull ImmutableList<T> consequent) {
		return wrap.apply(node, reducer.reduceSwitchDefault(node, consequent));
	}

	@Nonnull
	@Override
	public T reduceSwitchStatement(@Nonnull SwitchStatement node, @Nonnull T discriminant, @Nonnull ImmutableList<T> cases) {
		return wrap.apply(node, reducer.reduceSwitchStatement(node, discriminant, cases));
	}

	@Nonnull
	@Override
	public T reduceSwitchStatementWithDefault(@Nonnull SwitchStatementWithDefault node, @Nonnull T discriminant, @Nonnull ImmutableList<T> preDefaultCases, @Nonnull T defaultCase, @Nonnull ImmutableList<T> postDefaultCases) {
		return wrap.apply(node, reducer.reduceSwitchStatementWithDefault(node, discriminant, preDefaultCases, defaultCase, postDefaultCases));
	}

	@Nonnull
	@Override
	public T reduceTemplateElement(@Nonnull TemplateElement node) {
		return wrap.apply(node, reducer.reduceTemplateElement(node));
	}

	@Nonnull
	@Override
	public T reduceTemplateExpression(@Nonnull TemplateExpression node, @Nonnull Maybe<T> tag, @Nonnull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceTemplateExpression(node, tag, elements));
	}

	@Nonnull
	@Override
	public T reduceThisExpression(@Nonnull ThisExpression node) {
		return wrap.apply(node, reducer.reduceThisExpression(node));
	}

	@Nonnull
	@Override
	public T reduceThrowStatement(@Nonnull ThrowStatement node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceThrowStatement(node, expression));
	}

	@Nonnull
	@Override
	public T reduceTryCatchStatement(@Nonnull TryCatchStatement node, @Nonnull T body, @Nonnull T catchClause) {
		return wrap.apply(node, reducer.reduceTryCatchStatement(node, body, catchClause));
	}

	@Nonnull
	@Override
	public T reduceTryFinallyStatement(@Nonnull TryFinallyStatement node, @Nonnull T body, @Nonnull Maybe<T> catchClause, @Nonnull T finalizer) {
		return wrap.apply(node, reducer.reduceTryFinallyStatement(node, body, catchClause, finalizer));
	}

	@Nonnull
	@Override
	public T reduceUnaryExpression(@Nonnull UnaryExpression node, @Nonnull T operand) {
		return wrap.apply(node, reducer.reduceUnaryExpression(node, operand));
	}

	@Nonnull
	@Override
	public T reduceUpdateExpression(@Nonnull UpdateExpression node, @Nonnull T operand) {
		return wrap.apply(node, reducer.reduceUpdateExpression(node, operand));
	}

	@Nonnull
	@Override
	public T reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull ImmutableList<T> declarators) {
		return wrap.apply(node, reducer.reduceVariableDeclaration(node, declarators));
	}

	@Nonnull
	@Override
	public T reduceVariableDeclarationStatement(@Nonnull VariableDeclarationStatement node, @Nonnull T declaration) {
		return wrap.apply(node, reducer.reduceVariableDeclarationStatement(node, declaration));
	}

	@Nonnull
	@Override
	public T reduceVariableDeclarator(@Nonnull VariableDeclarator node, @Nonnull T binding, @Nonnull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceVariableDeclarator(node, binding, init));
	}

	@Nonnull
	@Override
	public T reduceWhileStatement(@Nonnull WhileStatement node, @Nonnull T test, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceWhileStatement(node, test, body));
	}

	@Nonnull
	@Override
	public T reduceWithStatement(@Nonnull WithStatement node, @Nonnull T object, @Nonnull T body) {
		return wrap.apply(node, reducer.reduceWithStatement(node, object, body));
	}

	@Nonnull
	@Override
	public T reduceYieldExpression(@Nonnull YieldExpression node, @Nonnull Maybe<T> expression) {
		return wrap.apply(node, reducer.reduceYieldExpression(node, expression));
	}

	@Nonnull
	@Override
	public T reduceYieldGeneratorExpression(@Nonnull YieldGeneratorExpression node, @Nonnull T expression) {
		return wrap.apply(node, reducer.reduceYieldGeneratorExpression(node, expression));
	}
}
