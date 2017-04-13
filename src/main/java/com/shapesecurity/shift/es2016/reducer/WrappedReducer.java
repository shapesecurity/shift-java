package com.shapesecurity.shift.es2016.reducer;

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
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
import com.shapesecurity.shift.es2016.reducer.Reducer;
import org.jetbrains.annotations.NotNull;

public class WrappedReducer<T> implements Reducer<T> {

	@NotNull
	private final F2<Node, T, T> wrap;

	@NotNull
	private final Reducer<T> reducer;

	public WrappedReducer(@NotNull F2<Node, T, T> wrap, @NotNull Reducer<T> reducer) {
		this.wrap = wrap;
		this.reducer = reducer;
	}

	@NotNull
	@Override
	public T reduceArrayAssignmentTarget(@NotNull ArrayAssignmentTarget node, @NotNull ImmutableList<Maybe<T>> elements, @NotNull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceArrayAssignmentTarget(node, elements, rest));
	}

	@NotNull
	@Override
	public T reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<T>> elements, @NotNull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceArrayBinding(node, elements, rest));
	}

	@NotNull
	@Override
	public T reduceArrayExpression(@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<T>> elements) {
		return wrap.apply(node, reducer.reduceArrayExpression(node, elements));
	}

	@NotNull
	@Override
	public T reduceArrowExpression(@NotNull ArrowExpression node, @NotNull T params, @NotNull T body) {
		return wrap.apply(node, reducer.reduceArrowExpression(node, params, body));
	}

	@NotNull
	@Override
	public T reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull T binding, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceAssignmentExpression(node, binding, expression));
	}

	@NotNull
	@Override
	public T reduceAssignmentTargetIdentifier(@NotNull AssignmentTargetIdentifier node) {
		return wrap.apply(node, reducer.reduceAssignmentTargetIdentifier(node));
	}

	@NotNull
	@Override
	public T reduceAssignmentTargetPropertyIdentifier(@NotNull AssignmentTargetPropertyIdentifier node, @NotNull T binding, @NotNull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceAssignmentTargetPropertyIdentifier(node, binding, init));
	}

	@NotNull
	@Override
	public T reduceAssignmentTargetPropertyProperty(@NotNull AssignmentTargetPropertyProperty node, @NotNull T name, @NotNull T binding) {
		return wrap.apply(node, reducer.reduceAssignmentTargetPropertyProperty(node, name, binding));
	}

	@NotNull
	@Override
	public T reduceAssignmentTargetWithDefault(@NotNull AssignmentTargetWithDefault node, @NotNull T binding, @NotNull T init) {
		return wrap.apply(node, reducer.reduceAssignmentTargetWithDefault(node, binding, init));
	}

	@NotNull
	@Override
	public T reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull T left, @NotNull T right) {
		return wrap.apply(node, reducer.reduceBinaryExpression(node, left, right));
	}

	@NotNull
	@Override
	public T reduceBindingIdentifier(@NotNull BindingIdentifier node) {
		return wrap.apply(node, reducer.reduceBindingIdentifier(node));
	}

	@NotNull
	@Override
	public T reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull T binding, @NotNull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceBindingPropertyIdentifier(node, binding, init));
	}

	@NotNull
	@Override
	public T reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull T name, @NotNull T binding) {
		return wrap.apply(node, reducer.reduceBindingPropertyProperty(node, name, binding));
	}

	@NotNull
	@Override
	public T reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull T binding, @NotNull T init) {
		return wrap.apply(node, reducer.reduceBindingWithDefault(node, binding, init));
	}

	@NotNull
	@Override
	public T reduceBlock(@NotNull Block node, @NotNull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceBlock(node, statements));
	}

	@NotNull
	@Override
	public T reduceBlockStatement(@NotNull BlockStatement node, @NotNull T block) {
		return wrap.apply(node, reducer.reduceBlockStatement(node, block));
	}

	@NotNull
	@Override
	public T reduceBreakStatement(@NotNull BreakStatement node) {
		return wrap.apply(node, reducer.reduceBreakStatement(node));
	}

	@NotNull
	@Override
	public T reduceCallExpression(@NotNull CallExpression node, @NotNull T callee, @NotNull ImmutableList<T> arguments) {
		return wrap.apply(node, reducer.reduceCallExpression(node, callee, arguments));
	}

	@NotNull
	@Override
	public T reduceCatchClause(@NotNull CatchClause node, @NotNull T binding, @NotNull T body) {
		return wrap.apply(node, reducer.reduceCatchClause(node, binding, body));
	}

	@NotNull
	@Override
	public T reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull T name, @NotNull Maybe<T> _super, @NotNull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceClassDeclaration(node, name, _super, elements));
	}

	@NotNull
	@Override
	public T reduceClassElement(@NotNull ClassElement node, @NotNull T method) {
		return wrap.apply(node, reducer.reduceClassElement(node, method));
	}

	@NotNull
	@Override
	public T reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<T> name, @NotNull Maybe<T> _super, @NotNull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceClassExpression(node, name, _super, elements));
	}

	@NotNull
	@Override
	public T reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull T binding, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceCompoundAssignmentExpression(node, binding, expression));
	}

	@NotNull
	@Override
	public T reduceComputedMemberAssignmentTarget(@NotNull ComputedMemberAssignmentTarget node, @NotNull T object, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceComputedMemberAssignmentTarget(node, object, expression));
	}

	@NotNull
	@Override
	public T reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull T object, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceComputedMemberExpression(node, object, expression));
	}

	@NotNull
	@Override
	public T reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceComputedPropertyName(node, expression));
	}

	@NotNull
	@Override
	public T reduceConditionalExpression(@NotNull ConditionalExpression node, @NotNull T test, @NotNull T consequent, @NotNull T alternate) {
		return wrap.apply(node, reducer.reduceConditionalExpression(node, test, consequent, alternate));
	}

	@NotNull
	@Override
	public T reduceContinueStatement(@NotNull ContinueStatement node) {
		return wrap.apply(node, reducer.reduceContinueStatement(node));
	}

	@NotNull
	@Override
	public T reduceDataProperty(@NotNull DataProperty node, @NotNull T name, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceDataProperty(node, name, expression));
	}

	@NotNull
	@Override
	public T reduceDebuggerStatement(@NotNull DebuggerStatement node) {
		return wrap.apply(node, reducer.reduceDebuggerStatement(node));
	}

	@NotNull
	@Override
	public T reduceDirective(@NotNull Directive node) {
		return wrap.apply(node, reducer.reduceDirective(node));
	}

	@NotNull
	@Override
	public T reduceDoWhileStatement(@NotNull DoWhileStatement node, @NotNull T body, @NotNull T test) {
		return wrap.apply(node, reducer.reduceDoWhileStatement(node, body, test));
	}

	@NotNull
	@Override
	public T reduceEmptyStatement(@NotNull EmptyStatement node) {
		return wrap.apply(node, reducer.reduceEmptyStatement(node));
	}

	@NotNull
	@Override
	public T reduceExport(@NotNull Export node, @NotNull T declaration) {
		return wrap.apply(node, reducer.reduceExport(node, declaration));
	}

	@NotNull
	@Override
	public T reduceExportAllFrom(@NotNull ExportAllFrom node) {
		return wrap.apply(node, reducer.reduceExportAllFrom(node));
	}

	@NotNull
	@Override
	public T reduceExportDefault(@NotNull ExportDefault node, @NotNull T body) {
		return wrap.apply(node, reducer.reduceExportDefault(node, body));
	}

	@NotNull
	@Override
	public T reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<T> namedExports) {
		return wrap.apply(node, reducer.reduceExportFrom(node, namedExports));
	}

	@NotNull
	@Override
	public T reduceExportFromSpecifier(@NotNull ExportFromSpecifier node) {
		return wrap.apply(node, reducer.reduceExportFromSpecifier(node));
	}

	@NotNull
	@Override
	public T reduceExportLocalSpecifier(@NotNull ExportLocalSpecifier node, @NotNull T name) {
		return wrap.apply(node, reducer.reduceExportLocalSpecifier(node, name));
	}

	@NotNull
	@Override
	public T reduceExportLocals(@NotNull ExportLocals node, @NotNull ImmutableList<T> namedExports) {
		return wrap.apply(node, reducer.reduceExportLocals(node, namedExports));
	}

	@NotNull
	@Override
	public T reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceExpressionStatement(node, expression));
	}

	@NotNull
	@Override
	public T reduceForInStatement(@NotNull ForInStatement node, @NotNull T left, @NotNull T right, @NotNull T body) {
		return wrap.apply(node, reducer.reduceForInStatement(node, left, right, body));
	}

	@NotNull
	@Override
	public T reduceForOfStatement(@NotNull ForOfStatement node, @NotNull T left, @NotNull T right, @NotNull T body) {
		return wrap.apply(node, reducer.reduceForOfStatement(node, left, right, body));
	}

	@NotNull
	@Override
	public T reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<T> init, @NotNull Maybe<T> test, @NotNull Maybe<T> update, @NotNull T body) {
		return wrap.apply(node, reducer.reduceForStatement(node, init, test, update, body));
	}

	@NotNull
	@Override
	public T reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<T> items, @NotNull Maybe<T> rest) {
		return wrap.apply(node, reducer.reduceFormalParameters(node, items, rest));
	}

	@NotNull
	@Override
	public T reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<T> directives, @NotNull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceFunctionBody(node, directives, statements));
	}

	@NotNull
	@Override
	public T reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull T name, @NotNull T params, @NotNull T body) {
		return wrap.apply(node, reducer.reduceFunctionDeclaration(node, name, params, body));
	}

	@NotNull
	@Override
	public T reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<T> name, @NotNull T params, @NotNull T body) {
		return wrap.apply(node, reducer.reduceFunctionExpression(node, name, params, body));
	}

	@NotNull
	@Override
	public T reduceGetter(@NotNull Getter node, @NotNull T name, @NotNull T body) {
		return wrap.apply(node, reducer.reduceGetter(node, name, body));
	}

	@NotNull
	@Override
	public T reduceIdentifierExpression(@NotNull IdentifierExpression node) {
		return wrap.apply(node, reducer.reduceIdentifierExpression(node));
	}

	@NotNull
	@Override
	public T reduceIfStatement(@NotNull IfStatement node, @NotNull T test, @NotNull T consequent, @NotNull Maybe<T> alternate) {
		return wrap.apply(node, reducer.reduceIfStatement(node, test, consequent, alternate));
	}

	@NotNull
	@Override
	public T reduceImport(@NotNull Import node, @NotNull Maybe<T> defaultBinding, @NotNull ImmutableList<T> namedImports) {
		return wrap.apply(node, reducer.reduceImport(node, defaultBinding, namedImports));
	}

	@NotNull
	@Override
	public T reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<T> defaultBinding, @NotNull T namespaceBinding) {
		return wrap.apply(node, reducer.reduceImportNamespace(node, defaultBinding, namespaceBinding));
	}

	@NotNull
	@Override
	public T reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull T binding) {
		return wrap.apply(node, reducer.reduceImportSpecifier(node, binding));
	}

	@NotNull
	@Override
	public T reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull T body) {
		return wrap.apply(node, reducer.reduceLabeledStatement(node, body));
	}

	@NotNull
	@Override
	public T reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
		return wrap.apply(node, reducer.reduceLiteralBooleanExpression(node));
	}

	@NotNull
	@Override
	public T reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
		return wrap.apply(node, reducer.reduceLiteralInfinityExpression(node));
	}

	@NotNull
	@Override
	public T reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
		return wrap.apply(node, reducer.reduceLiteralNullExpression(node));
	}

	@NotNull
	@Override
	public T reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
		return wrap.apply(node, reducer.reduceLiteralNumericExpression(node));
	}

	@NotNull
	@Override
	public T reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
		return wrap.apply(node, reducer.reduceLiteralRegExpExpression(node));
	}

	@NotNull
	@Override
	public T reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
		return wrap.apply(node, reducer.reduceLiteralStringExpression(node));
	}

	@NotNull
	@Override
	public T reduceMethod(@NotNull Method node, @NotNull T name, @NotNull T params, @NotNull T body) {
		return wrap.apply(node, reducer.reduceMethod(node, name, params, body));
	}

	@NotNull
	@Override
	public T reduceModule(@NotNull Module node, @NotNull ImmutableList<T> directives, @NotNull ImmutableList<T> items) {
		return wrap.apply(node, reducer.reduceModule(node, directives, items));
	}

	@NotNull
	@Override
	public T reduceNewExpression(@NotNull NewExpression node, @NotNull T callee, @NotNull ImmutableList<T> arguments) {
		return wrap.apply(node, reducer.reduceNewExpression(node, callee, arguments));
	}

	@NotNull
	@Override
	public T reduceNewTargetExpression(@NotNull NewTargetExpression node) {
		return wrap.apply(node, reducer.reduceNewTargetExpression(node));
	}

	@NotNull
	@Override
	public T reduceObjectAssignmentTarget(@NotNull ObjectAssignmentTarget node, @NotNull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectAssignmentTarget(node, properties));
	}

	@NotNull
	@Override
	public T reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectBinding(node, properties));
	}

	@NotNull
	@Override
	public T reduceObjectExpression(@NotNull ObjectExpression node, @NotNull ImmutableList<T> properties) {
		return wrap.apply(node, reducer.reduceObjectExpression(node, properties));
	}

	@NotNull
	@Override
	public T reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<T> expression) {
		return wrap.apply(node, reducer.reduceReturnStatement(node, expression));
	}

	@NotNull
	@Override
	public T reduceScript(@NotNull Script node, @NotNull ImmutableList<T> directives, @NotNull ImmutableList<T> statements) {
		return wrap.apply(node, reducer.reduceScript(node, directives, statements));
	}

	@NotNull
	@Override
	public T reduceSetter(@NotNull Setter node, @NotNull T name, @NotNull T param, @NotNull T body) {
		return wrap.apply(node, reducer.reduceSetter(node, name, param, body));
	}

	@NotNull
	@Override
	public T reduceShorthandProperty(@NotNull ShorthandProperty node, @NotNull T name) {
		return wrap.apply(node, reducer.reduceShorthandProperty(node, name));
	}

	@NotNull
	@Override
	public T reduceSpreadElement(@NotNull SpreadElement node, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceSpreadElement(node, expression));
	}

	@NotNull
	@Override
	public T reduceStaticMemberAssignmentTarget(@NotNull StaticMemberAssignmentTarget node, @NotNull T object) {
		return wrap.apply(node, reducer.reduceStaticMemberAssignmentTarget(node, object));
	}

	@NotNull
	@Override
	public T reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull T object) {
		return wrap.apply(node, reducer.reduceStaticMemberExpression(node, object));
	}

	@NotNull
	@Override
	public T reduceStaticPropertyName(@NotNull StaticPropertyName node) {
		return wrap.apply(node, reducer.reduceStaticPropertyName(node));
	}

	@NotNull
	@Override
	public T reduceSuper(@NotNull Super node) {
		return wrap.apply(node, reducer.reduceSuper(node));
	}

	@NotNull
	@Override
	public T reduceSwitchCase(@NotNull SwitchCase node, @NotNull T test, @NotNull ImmutableList<T> consequent) {
		return wrap.apply(node, reducer.reduceSwitchCase(node, test, consequent));
	}

	@NotNull
	@Override
	public T reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<T> consequent) {
		return wrap.apply(node, reducer.reduceSwitchDefault(node, consequent));
	}

	@NotNull
	@Override
	public T reduceSwitchStatement(@NotNull SwitchStatement node, @NotNull T discriminant, @NotNull ImmutableList<T> cases) {
		return wrap.apply(node, reducer.reduceSwitchStatement(node, discriminant, cases));
	}

	@NotNull
	@Override
	public T reduceSwitchStatementWithDefault(@NotNull SwitchStatementWithDefault node, @NotNull T discriminant, @NotNull ImmutableList<T> preDefaultCases, @NotNull T defaultCase, @NotNull ImmutableList<T> postDefaultCases) {
		return wrap.apply(node, reducer.reduceSwitchStatementWithDefault(node, discriminant, preDefaultCases, defaultCase, postDefaultCases));
	}

	@NotNull
	@Override
	public T reduceTemplateElement(@NotNull TemplateElement node) {
		return wrap.apply(node, reducer.reduceTemplateElement(node));
	}

	@NotNull
	@Override
	public T reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<T> tag, @NotNull ImmutableList<T> elements) {
		return wrap.apply(node, reducer.reduceTemplateExpression(node, tag, elements));
	}

	@NotNull
	@Override
	public T reduceThisExpression(@NotNull ThisExpression node) {
		return wrap.apply(node, reducer.reduceThisExpression(node));
	}

	@NotNull
	@Override
	public T reduceThrowStatement(@NotNull ThrowStatement node, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceThrowStatement(node, expression));
	}

	@NotNull
	@Override
	public T reduceTryCatchStatement(@NotNull TryCatchStatement node, @NotNull T body, @NotNull T catchClause) {
		return wrap.apply(node, reducer.reduceTryCatchStatement(node, body, catchClause));
	}

	@NotNull
	@Override
	public T reduceTryFinallyStatement(@NotNull TryFinallyStatement node, @NotNull T body, @NotNull Maybe<T> catchClause, @NotNull T finalizer) {
		return wrap.apply(node, reducer.reduceTryFinallyStatement(node, body, catchClause, finalizer));
	}

	@NotNull
	@Override
	public T reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull T operand) {
		return wrap.apply(node, reducer.reduceUnaryExpression(node, operand));
	}

	@NotNull
	@Override
	public T reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull T operand) {
		return wrap.apply(node, reducer.reduceUpdateExpression(node, operand));
	}

	@NotNull
	@Override
	public T reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<T> declarators) {
		return wrap.apply(node, reducer.reduceVariableDeclaration(node, declarators));
	}

	@NotNull
	@Override
	public T reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull T declaration) {
		return wrap.apply(node, reducer.reduceVariableDeclarationStatement(node, declaration));
	}

	@NotNull
	@Override
	public T reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull T binding, @NotNull Maybe<T> init) {
		return wrap.apply(node, reducer.reduceVariableDeclarator(node, binding, init));
	}

	@NotNull
	@Override
	public T reduceWhileStatement(@NotNull WhileStatement node, @NotNull T test, @NotNull T body) {
		return wrap.apply(node, reducer.reduceWhileStatement(node, test, body));
	}

	@NotNull
	@Override
	public T reduceWithStatement(@NotNull WithStatement node, @NotNull T object, @NotNull T body) {
		return wrap.apply(node, reducer.reduceWithStatement(node, object, body));
	}

	@NotNull
	@Override
	public T reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<T> expression) {
		return wrap.apply(node, reducer.reduceYieldExpression(node, expression));
	}

	@NotNull
	@Override
	public T reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull T expression) {
		return wrap.apply(node, reducer.reduceYieldGeneratorExpression(node, expression));
	}
}
