//package com.shapesecurity.shift.visitor;
//
//
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.*;
//import org.jetbrains.annotations.NotNull;
//
//public class Flattener extends MonoidalReducer<ImmutableList<Node>> { // TODO should be concatlist, but functional needs work
//    private static final Flattener INSTANCE = new Flattener();
//
//    private Flattener() {
//        super(new com.shapesecurity.functional.data.Monoid.ImmutableListAppend<>());
//    }
//
//    @NotNull
//    public static ImmutableList<Node> flatten(@NotNull Script script) {
//        return Director.reduceScript(INSTANCE, script);
//    }
//
//    @NotNull
//    public static ImmutableList<Node> flatten(@NotNull Module module) {
//        return Director.reduceModule(INSTANCE, module);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<ImmutableList<Node>>> elements, @NotNull Maybe<ImmutableList<Node>> restElement) {
//        return ImmutableList.<Node>list(node).append(super.reduceArrayBinding(node, elements, restElement));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceArrayExpression(
//            @NotNull ArrayExpression node,
//            @NotNull ImmutableList<Maybe<ImmutableList<Node>>> elements) {
//        return ImmutableList.<Node>list(node).append(super.reduceArrayExpression(node, elements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceArrowExpression(@NotNull ArrowExpression node, @NotNull ImmutableList<Node> params, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceArrowExpression(node, params, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceAssignmentExpression(
//            @NotNull AssignmentExpression node,
//            @NotNull ImmutableList<Node> binding,
//            @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceAssignmentExpression(node, binding, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBinaryExpression(
//            @NotNull BinaryExpression node,
//            @NotNull ImmutableList<Node> left,
//            @NotNull ImmutableList<Node> right) {
//        return ImmutableList.<Node>list(node).append(super.reduceBinaryExpression(node, left, right));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBindingIdentifier(@NotNull BindingIdentifier node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull ImmutableList<Node> binding, @NotNull Maybe<ImmutableList<Node>> init) {
//        return ImmutableList.<Node>list(node).append(super.reduceBindingPropertyIdentifier(node, binding, init));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull ImmutableList<Node> name, @NotNull ImmutableList<Node> binding) {
//        return ImmutableList.<Node>list(node).append(super.reduceBindingPropertyProperty(node, name, binding));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull ImmutableList<Node> binding, @NotNull ImmutableList<Node> init) {
//        return ImmutableList.<Node>list(node).append(super.reduceBindingWithDefault(node, binding, init));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBlock(@NotNull Block node, @NotNull ImmutableList<ImmutableList<Node>> statements) {
//        return ImmutableList.<Node>list(node).append(super.reduceBlock(node, statements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBlockStatement(@NotNull BlockStatement node, @NotNull ImmutableList<Node> block) {
//        return ImmutableList.<Node>list(node).append(super.reduceBlockStatement(node, block));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceBreakStatement(@NotNull BreakStatement node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceCallExpression(
//            @NotNull CallExpression node,
//            @NotNull ImmutableList<Node> callee,
//            @NotNull ImmutableList<ImmutableList<Node>> arguments) {
//        return ImmutableList.<Node>list(node).append(super.reduceCallExpression(node, callee, arguments));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceCatchClause(
//            @NotNull CatchClause node,
//            @NotNull ImmutableList<Node> binding,
//            @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceCatchClause(node, binding, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull ImmutableList<Node> name, @NotNull Maybe<ImmutableList<Node>> _super, @NotNull ImmutableList<ImmutableList<Node>> elements) {
//        return ImmutableList.<Node>list(node).append(super.reduceClassDeclaration(node, name, _super, elements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceClassElement(@NotNull ClassElement node, @NotNull ImmutableList<Node> method) {
//        return ImmutableList.<Node>list(node).append(super.reduceClassElement(node, method));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<ImmutableList<Node>> name, @NotNull Maybe<ImmutableList<Node>> _super, @NotNull ImmutableList<ImmutableList<Node>> elements) {
//        return ImmutableList.<Node>list(node).append(super.reduceClassExpression(node, name, _super, elements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull ImmutableList<Node> binding, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceCompoundAssignmentExpression(node, binding, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceComputedMemberExpression(
//                                                 @NotNull ComputedMemberExpression node,
//                                                 @NotNull ImmutableList<Node> object,
//                                                 @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceComputedMemberExpression(node, object, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceComputedPropertyName(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceConditionalExpression(
//            @NotNull ConditionalExpression node,
//            @NotNull ImmutableList<Node> test,
//            @NotNull ImmutableList<Node> consequent,
//            @NotNull ImmutableList<Node> alternate) {
//        return ImmutableList.<Node>list(node).append(super.reduceConditionalExpression(node, test, consequent, alternate));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceContinueStatement(@NotNull ContinueStatement node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceDataProperty(
//            @NotNull DataProperty node,
//            @NotNull ImmutableList<Node> name,
//            @NotNull ImmutableList<Node> value) {
//        return ImmutableList.<Node>list(node).append(super.reduceDataProperty(node, name, value));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceDebuggerStatement(@NotNull DebuggerStatement node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceDirective(@NotNull Directive node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceDoWhileStatement(
//            @NotNull DoWhileStatement node,
//            @NotNull ImmutableList<Node> body,
//            @NotNull ImmutableList<Node> test) {
//        return ImmutableList.<Node>list(node).append(super.reduceDoWhileStatement(node, body, test));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceEmptyStatement(@NotNull EmptyStatement node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExport(@NotNull Export node, @NotNull ImmutableList<Node> declaration) {
//        return ImmutableList.<Node>list(node).append(super.reduceExport(node, declaration));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExportAllFrom(@NotNull ExportAllFrom node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExportDefault(@NotNull ExportDefault node, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceExportDefault(node, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<ImmutableList<Node>> namedExports) {
//        return ImmutableList.<Node>list(node).append(super.reduceExportFrom(node, namedExports));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExportSpecifier(@NotNull ExportSpecifier node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceExpressionStatement(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceForInStatement(@NotNull ForInStatement node, @NotNull ImmutableList<Node> left, @NotNull ImmutableList<Node> right, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceForInStatement(node, left, right, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceForOfStatement(@NotNull ForOfStatement node, @NotNull ImmutableList<Node> left, @NotNull ImmutableList<Node> right, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceForOfStatement(node, left, right, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<ImmutableList<Node>> init, @NotNull Maybe<ImmutableList<Node>> test, @NotNull Maybe<ImmutableList<Node>> update, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceForStatement(node, init, test, update, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<ImmutableList<Node>> items, @NotNull Maybe<ImmutableList<Node>> rest) {
//        return ImmutableList.<Node>list(node).append(super.reduceFormalParameters(node, items, rest));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceFunctionBody(
//            @NotNull FunctionBody node,
//            @NotNull ImmutableList<ImmutableList<Node>> directives,
//            @NotNull ImmutableList<ImmutableList<Node>> statements) {
//        return ImmutableList.<Node>list(node).append(super.reduceFunctionBody(node, directives, statements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull ImmutableList<Node> name, @NotNull ImmutableList<Node> params, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceFunctionDeclaration(node, name, params, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<ImmutableList<Node>> name, @NotNull ImmutableList<Node> params, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceFunctionExpression(node, name, params, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceGetter(@NotNull Getter node, @NotNull ImmutableList<Node> name, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceGetter(node, name, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceIdentifierExpression(@NotNull IdentifierExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceIfStatement(
//            @NotNull IfStatement node,
//            @NotNull ImmutableList<Node> test,
//            @NotNull ImmutableList<Node> consequent,
//            @NotNull Maybe<ImmutableList<Node>> alternate) {
//        return ImmutableList.<Node>list(node).append(super.reduceIfStatement(node, test, consequent, alternate));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceImport(@NotNull Import node, @NotNull Maybe<ImmutableList<Node>> defaultBinding, @NotNull ImmutableList<ImmutableList<Node>> namedImports) {
//        return ImmutableList.<Node>list(node).append(super.reduceImport(node, defaultBinding, namedImports));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<ImmutableList<Node>> defaultBinding, @NotNull ImmutableList<Node> namespaceBinding) {
//        return ImmutableList.<Node>list(node).append(super.reduceImportNamespace(node, defaultBinding, namespaceBinding));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull ImmutableList<Node> binding) {
//        return ImmutableList.<Node>list(node).append(super.reduceImportSpecifier(node, binding));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceLabeledStatement(node, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceMethod(@NotNull Method node, @NotNull ImmutableList<Node> name, @NotNull ImmutableList<Node> params, @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceMethod(node, name, params, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceModule(@NotNull Module node, @NotNull ImmutableList<ImmutableList<Node>> directives, @NotNull ImmutableList<ImmutableList<Node>> items) {
//        return ImmutableList.<Node>list(node).append(super.reduceModule(node, directives, items));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceNewExpression(
//            @NotNull NewExpression node,
//            @NotNull ImmutableList<Node> callee,
//            @NotNull ImmutableList<ImmutableList<Node>> arguments) {
//        return ImmutableList.<Node>list(node).append(super.reduceNewExpression(node, callee, arguments));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceNewTargetExpression(@NotNull NewTargetExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<ImmutableList<Node>> properties) {
//        return ImmutableList.<Node>list(node).append(super.reduceObjectBinding(node, properties));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceObjectExpression(
//            @NotNull ObjectExpression node,
//            @NotNull ImmutableList<ImmutableList<Node>> properties) {
//        return ImmutableList.<Node>list(node).append(super.reduceObjectExpression(node, properties));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceReturnStatement(
//            @NotNull ReturnStatement node,
//            @NotNull Maybe<ImmutableList<Node>> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceReturnStatement(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceScript(@NotNull Script node, @NotNull ImmutableList<ImmutableList<Node>> directives, @NotNull ImmutableList<ImmutableList<Node>> statements) {
//        return ImmutableList.<Node>list(node).append(super.reduceScript(node, directives, statements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSetter(
//            @NotNull Setter node,
//            @NotNull ImmutableList<Node> name,
//            @NotNull ImmutableList<Node> param,
//            @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceSetter(node, name, param, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceShorthandProperty(@NotNull ShorthandProperty node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSpreadElement(@NotNull SpreadElement node, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceSpreadElement(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull ImmutableList<Node> object) {
//        return ImmutableList.<Node>list(node).append(super.reduceStaticMemberExpression(node, object));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceStaticPropertyName(@NotNull StaticPropertyName node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSuper(@NotNull Super node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSwitchCase(
//            @NotNull SwitchCase node,
//            @NotNull ImmutableList<Node> test,
//            @NotNull ImmutableList<ImmutableList<Node>> consequent) {
//        return ImmutableList.<Node>list(node).append(super.reduceSwitchCase(node, test, consequent));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSwitchDefault(
//            @NotNull SwitchDefault node,
//            @NotNull ImmutableList<ImmutableList<Node>> consequent) {
//        return ImmutableList.<Node>list(node).append(super.reduceSwitchDefault(node, consequent));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSwitchStatement(
//            @NotNull SwitchStatement node,
//            @NotNull ImmutableList<Node> discriminant,
//            @NotNull ImmutableList<ImmutableList<Node>> cases) {
//        return ImmutableList.<Node>list(node).append(super.reduceSwitchStatement(node, discriminant, cases));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceSwitchStatementWithDefault(
//            @NotNull SwitchStatementWithDefault node,
//            @NotNull ImmutableList<Node> discriminant,
//            @NotNull ImmutableList<ImmutableList<Node>> preDefaultCases,
//            @NotNull ImmutableList<Node> defaultCase,
//            @NotNull ImmutableList<ImmutableList<Node>> postDefaultCases) {
//        return ImmutableList.<Node>list(node).append(super.reduceSwitchStatementWithDefault(node, discriminant, preDefaultCases, defaultCase, postDefaultCases));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceTemplateElement(@NotNull TemplateElement node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<ImmutableList<Node>> tag, @NotNull ImmutableList<ImmutableList<Node>> elements) {
//        return ImmutableList.<Node>list(node).append(super.reduceTemplateExpression(node, tag, elements));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceThisExpression(@NotNull ThisExpression node) {
//        return ImmutableList.<Node>list(node);
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceThrowStatement(@NotNull ThrowStatement node, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceThrowStatement(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceTryCatchStatement(
//            @NotNull TryCatchStatement node,
//            @NotNull ImmutableList<Node> block,
//            @NotNull ImmutableList<Node> catchClause) {
//        return ImmutableList.<Node>list(node).append(super.reduceTryCatchStatement(node, block, catchClause));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceTryFinallyStatement(
//            @NotNull TryFinallyStatement node,
//            @NotNull ImmutableList<Node> block,
//            @NotNull Maybe<ImmutableList<Node>> catchClause,
//            @NotNull ImmutableList<Node> finalizer) {
//        return ImmutableList.<Node>list(node).append(super.reduceTryFinallyStatement(node, block, catchClause, finalizer));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull ImmutableList<Node> operand) {
//        return ImmutableList.<Node>list(node).append(super.reduceUnaryExpression(node, operand));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull ImmutableList<Node> operand) {
//        return ImmutableList.<Node>list(node).append(super.reduceUpdateExpression(node, operand));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<ImmutableList<Node>> declarators) {
//        return ImmutableList.<Node>list(node).append(super.reduceVariableDeclaration(node, declarators));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceVariableDeclarationStatement(
//            @NotNull VariableDeclarationStatement node,
//            @NotNull ImmutableList<Node> declaration) {
//        return ImmutableList.<Node>list(node).append(super.reduceVariableDeclarationStatement(node, declaration));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceVariableDeclarator(
//            @NotNull VariableDeclarator node,
//            @NotNull ImmutableList<Node> binding,
//            @NotNull Maybe<ImmutableList<Node>> init) {
//        return ImmutableList.<Node>list(node).append(super.reduceVariableDeclarator(node, binding, init));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceWhileStatement(
//            @NotNull WhileStatement node,
//            @NotNull ImmutableList<Node> test,
//            @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceWhileStatement(node, test, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceWithStatement(
//            @NotNull WithStatement node,
//            @NotNull ImmutableList<Node> object,
//            @NotNull ImmutableList<Node> body) {
//        return ImmutableList.<Node>list(node).append(super.reduceWithStatement(node, object, body));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<ImmutableList<Node>> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceYieldExpression(node, expression));
//    }
//
//    @NotNull
//    @Override
//    public ImmutableList<Node> reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull ImmutableList<Node> expression) {
//        return ImmutableList.<Node>list(node).append(super.reduceYieldGeneratorExpression(node, expression));
//    }
//}
