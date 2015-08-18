package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.FreePairingMonoid;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.*;

import org.jetbrains.annotations.NotNull;

public class ComposedMonoidalReducer<A, B> extends MonoidalReducer<Pair<A, B>> {
    private final MonoidalReducer<A> reducerA;
    private final MonoidalReducer<B> reducerB;

    protected ComposedMonoidalReducer(@NotNull Monoid<Pair<A, B>> monoidClass, MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB) {
        super(monoidClass);
        this.reducerA = reducerA;
        this.reducerB = reducerB;
    }

    public static <A, B> ComposedMonoidalReducer<A, B> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB) {
        return new ComposedMonoidalReducer<>(new FreePairingMonoid<>(reducerA.monoidClass, reducerB.monoidClass), reducerA, reducerB);
    }

    public static <A, B, C> ComposedMonoidalReducer<A, Pair<B, C>> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB, MonoidalReducer<C> reducerC) {
        return ComposedMonoidalReducer.from(reducerA, ComposedMonoidalReducer.from(reducerB, reducerC));
    }

    public static <A, B, C, D> ComposedMonoidalReducer<A, Pair<B, Pair<C, D>>> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB, MonoidalReducer<C> reducerC, MonoidalReducer<D> reducerD) {
        return ComposedMonoidalReducer.from(reducerA, ComposedMonoidalReducer.from(reducerB, ComposedMonoidalReducer.from(reducerC, reducerD)));
    }

    private A a(Pair<A, B> pair) {
        return pair.a;
    }

    private B b(Pair<A, B> pair) {
        return pair.b;
    }

    private ImmutableList<A> a(ImmutableList<Pair<A, B>> list) {
        return list.map(x -> x.a);
    }

    private ImmutableList<B> b(ImmutableList<Pair<A, B>> list) {
        return list.map(x -> x.b);
    }

    private Maybe<A> a(Maybe<Pair<A, B>> maybe) {
        return maybe.map(x -> x.a);
    }

    private Maybe<B> b(Maybe<Pair<A, B>> maybe) {
        return maybe.map(x -> x.b);
    }


    @NotNull
    @Override
    public Pair<A, B> reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<Pair<A, B>>> elements, @NotNull Maybe<Pair<A, B>> restElement) {
        return new Pair<>(reducerA.reduceArrayBinding(node, elements.map(this::a), a(restElement)), reducerB.reduceArrayBinding(node, elements.map(this::b), b(restElement)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceArrayExpression(
            @NotNull ArrayExpression node,
            @NotNull ImmutableList<Maybe<Pair<A, B>>> elements) {
        return new Pair<>(reducerA.reduceArrayExpression(node, elements.map(this::a)), reducerB.reduceArrayExpression(node, elements.map(this::b)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceArrowExpression(@NotNull ArrowExpression node, @NotNull Pair<A, B> params, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceArrowExpression(node, a(params), a(body)), reducerB.reduceArrowExpression(node, b(params), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceAssignmentExpression(
            @NotNull AssignmentExpression node,
            @NotNull Pair<A, B> binding,
            @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceAssignmentExpression(node, a(binding), a(expression)), reducerB.reduceAssignmentExpression(node, b(binding), b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBinaryExpression(
            @NotNull BinaryExpression node,
            @NotNull Pair<A, B> left,
            @NotNull Pair<A, B> right) {
        return new Pair<>(reducerA.reduceBinaryExpression(node, a(left), a(right)), reducerB.reduceBinaryExpression(node, b(left), b(right)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        return new Pair<>(reducerA.reduceBindingIdentifier(node), reducerB.reduceBindingIdentifier(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull Pair<A, B> binding, @NotNull Maybe<Pair<A, B>> init) {
        return new Pair<>(reducerA.reduceBindingPropertyIdentifier(node, a(binding), a(init)), reducerB.reduceBindingPropertyIdentifier(node, b(binding), b(init)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull Pair<A, B> name, @NotNull Pair<A, B> binding) {
        return new Pair<>(reducerA.reduceBindingPropertyProperty(node, a(name), a(binding)), reducerB.reduceBindingPropertyProperty(node, b(name), b(binding)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull Pair<A, B> binding, @NotNull Pair<A, B> init) {
        return new Pair<>(reducerA.reduceBindingWithDefault(node, a(binding), a(init)), reducerB.reduceBindingWithDefault(node, b(binding), b(init)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBlock(@NotNull Block node, @NotNull ImmutableList<Pair<A, B>> statements) {
        return new Pair<>(reducerA.reduceBlock(node, a(statements)), reducerB.reduceBlock(node, b(statements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBlockStatement(@NotNull BlockStatement node, @NotNull Pair<A, B> block) {
        return new Pair<>(reducerA.reduceBlockStatement(node, a(block)), reducerB.reduceBlockStatement(node, b(block)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceBreakStatement(@NotNull BreakStatement node) {
        return new Pair<>(reducerA.reduceBreakStatement(node), reducerB.reduceBreakStatement(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceCallExpression(
            @NotNull CallExpression node,
            @NotNull Pair<A, B> callee,
            @NotNull ImmutableList<Pair<A, B>> arguments) {
        return new Pair<>(reducerA.reduceCallExpression(node, a(callee), a(arguments)), reducerB.reduceCallExpression(node, b(callee), b(arguments)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceCatchClause(
            @NotNull CatchClause node,
            @NotNull Pair<A, B> binding,
            @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceCatchClause(node, a(binding), a(body)), reducerB.reduceCatchClause(node, b(binding), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull Pair<A, B> name, @NotNull Maybe<Pair<A, B>> _super, @NotNull ImmutableList<Pair<A, B>> elements) {
        return new Pair<>(reducerA.reduceClassDeclaration(node, a(name), a(_super), a(elements)), reducerB.reduceClassDeclaration(node, b(name), b(_super), b(elements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceClassElement(@NotNull ClassElement node, @NotNull Pair<A, B> method) {
        return new Pair<>(reducerA.reduceClassElement(node, a(method)), reducerB.reduceClassElement(node, b(method)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<Pair<A, B>> name, @NotNull Maybe<Pair<A, B>> _super, @NotNull ImmutableList<Pair<A, B>> elements) {
        return new Pair<>(reducerA.reduceClassExpression(node, a(name), a(_super), a(elements)), reducerB.reduceClassExpression(node, b(name), b(_super), b(elements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull Pair<A, B> binding, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceCompoundAssignmentExpression(node, a(binding), a(expression)), reducerB.reduceCompoundAssignmentExpression(node, b(binding), b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceComputedMemberExpression(
            @NotNull ComputedMemberExpression node,
            @NotNull Pair<A, B> expression,
            @NotNull Pair<A, B> object) {
        return new Pair<>(reducerA.reduceComputedMemberExpression(node, a(expression), a(object)), reducerB.reduceComputedMemberExpression(node, b(expression), b(object)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceComputedPropertyName(node, a(expression)), reducerB.reduceComputedPropertyName(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceConditionalExpression(
            @NotNull ConditionalExpression node,
            @NotNull Pair<A, B> test,
            @NotNull Pair<A, B> consequent,
            @NotNull Pair<A, B> alternate) {
        return new Pair<>(reducerA.reduceConditionalExpression(node, a(test), a(consequent), a(alternate)), reducerB.reduceConditionalExpression(node, b(test), b(consequent), b(alternate)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceContinueStatement(@NotNull ContinueStatement node) {
        return new Pair<>(reducerA.reduceContinueStatement(node), reducerB.reduceContinueStatement(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceDataProperty(
            @NotNull DataProperty node,
            @NotNull Pair<A, B> name,
            @NotNull Pair<A, B> value) {
        return new Pair<>(reducerA.reduceDataProperty(node, a(name), a(value)), reducerB.reduceDataProperty(node, b(name), b(value)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceDebuggerStatement(@NotNull DebuggerStatement node) {
        return new Pair<>(reducerA.reduceDebuggerStatement(node), reducerB.reduceDebuggerStatement(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceDirective(@NotNull Directive node) {
        return new Pair<>(reducerA.reduceDirective(node), reducerB.reduceDirective(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceDoWhileStatement(
            @NotNull DoWhileStatement node,
            @NotNull Pair<A, B> body,
            @NotNull Pair<A, B> test) {
        return new Pair<>(reducerA.reduceDoWhileStatement(node, a(body), a(test)), reducerB.reduceDoWhileStatement(node, b(body), b(test)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceEmptyStatement(@NotNull EmptyStatement node) {
        return new Pair<>(reducerA.reduceEmptyStatement(node), reducerB.reduceEmptyStatement(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExport(@NotNull Export node, @NotNull Pair<A, B> declaration) {
        return new Pair<>(reducerA.reduceExport(node, a(declaration)), reducerB.reduceExport(node, b(declaration)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExportAllFrom(@NotNull ExportAllFrom node) {
        return new Pair<>(reducerA.reduceExportAllFrom(node), reducerB.reduceExportAllFrom(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExportDefault(@NotNull ExportDefault node, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceExportDefault(node, a(body)), reducerB.reduceExportDefault(node, b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<Pair<A, B>> namedExports) {
        return new Pair<>(reducerA.reduceExportFrom(node, a(namedExports)), reducerB.reduceExportFrom(node, b(namedExports)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExportSpecifier(@NotNull ExportSpecifier node) {
        return new Pair<>(reducerA.reduceExportSpecifier(node), reducerB.reduceExportSpecifier(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceExpressionStatement(node, a(expression)), reducerB.reduceExpressionStatement(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceForInStatement(@NotNull ForInStatement node, @NotNull Pair<A, B> left, @NotNull Pair<A, B> right, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceForInStatement(node, a(left), a(right), a(body)), reducerB.reduceForInStatement(node, b(left), b(right), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceForOfStatement(@NotNull ForOfStatement node, @NotNull Pair<A, B> left, @NotNull Pair<A, B> right, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceForOfStatement(node, a(left), a(right), a(body)), reducerB.reduceForOfStatement(node, b(left), b(right), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<Pair<A, B>> init, @NotNull Maybe<Pair<A, B>> test, @NotNull Maybe<Pair<A, B>> update, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceForStatement(node, a(init), a(test), a(update), a(body)), reducerB.reduceForStatement(node, b(init), b(test), b(update), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<Pair<A, B>> items, @NotNull Maybe<Pair<A, B>> rest) {
        return new Pair<>(reducerA.reduceFormalParameters(node, a(items), a(rest)), reducerB.reduceFormalParameters(node, b(items), b(rest)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceFunctionBody(
            @NotNull FunctionBody node,
            @NotNull ImmutableList<Pair<A, B>> directives,
            @NotNull ImmutableList<Pair<A, B>> statements) {
        return new Pair<>(reducerA.reduceFunctionBody(node, a(directives), a(statements)), reducerB.reduceFunctionBody(node, b(directives), b(statements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull Pair<A, B> name, @NotNull Pair<A, B> params, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceFunctionDeclaration(node, a(name), a(params), a(body)), reducerB.reduceFunctionDeclaration(node, b(name), b(params), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<Pair<A, B>> name, @NotNull Pair<A, B> parameters, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceFunctionExpression(node, a(name), a(parameters), a(body)), reducerB.reduceFunctionExpression(node, b(name), b(parameters), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceGetter(@NotNull Getter node, @NotNull Pair<A, B> name, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceGetter(node, a(name), a(body)), reducerB.reduceGetter(node, b(name), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        return new Pair<>(reducerA.reduceIdentifierExpression(node), reducerB.reduceIdentifierExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceIfStatement(
            @NotNull IfStatement node,
            @NotNull Pair<A, B> test,
            @NotNull Pair<A, B> consequent,
            @NotNull Maybe<Pair<A, B>> alternate) {
        return new Pair<>(reducerA.reduceIfStatement(node, a(test), a(consequent), a(alternate)), reducerB.reduceIfStatement(node, b(test), b(consequent), b(alternate)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceImport(@NotNull Import node, @NotNull Maybe<Pair<A, B>> defaultBinding, @NotNull ImmutableList<Pair<A, B>> namedImports) {
        return new Pair<>(reducerA.reduceImport(node, a(defaultBinding), a(namedImports)), reducerB.reduceImport(node, b(defaultBinding), b(namedImports)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<Pair<A, B>> defaultBinding, @NotNull Pair<A, B> namespaceBinding) {
        return new Pair<>(reducerA.reduceImportNamespace(node, a(defaultBinding), a(namespaceBinding)), reducerB.reduceImportNamespace(node, b(defaultBinding), b(namespaceBinding)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull Pair<A, B> binding) {
        return new Pair<>(reducerA.reduceImportSpecifier(node, a(binding)), reducerB.reduceImportSpecifier(node, b(binding)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceLabeledStatement(node, a(body)), reducerB.reduceLabeledStatement(node, b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
        return new Pair<>(reducerA.reduceLiteralBooleanExpression(node), reducerB.reduceLiteralBooleanExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
        return new Pair<>(reducerA.reduceLiteralInfinityExpression(node), reducerB.reduceLiteralInfinityExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
        return new Pair<>(reducerA.reduceLiteralNullExpression(node), reducerB.reduceLiteralNullExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
        return new Pair<>(reducerA.reduceLiteralNumericExpression(node), reducerB.reduceLiteralNumericExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        return new Pair<>(reducerA.reduceLiteralRegExpExpression(node), reducerB.reduceLiteralRegExpExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
        return new Pair<>(reducerA.reduceLiteralStringExpression(node), reducerB.reduceLiteralStringExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceMethod(@NotNull Method node, @NotNull Pair<A, B> name, @NotNull Pair<A, B> params, @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceMethod(node, a(name), a(params), a(body)), reducerB.reduceMethod(node, b(name), b(params), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceModule(@NotNull Module node, @NotNull ImmutableList<Pair<A, B>> directives, @NotNull ImmutableList<Pair<A, B>> items) {
        return new Pair<>(reducerA.reduceModule(node, a(directives), a(items)), reducerB.reduceModule(node, b(directives), b(items)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceNewExpression(
            @NotNull NewExpression node,
            @NotNull Pair<A, B> callee,
            @NotNull ImmutableList<Pair<A, B>> arguments) {
        return new Pair<>(reducerA.reduceNewExpression(node, a(callee), a(arguments)), reducerB.reduceNewExpression(node, b(callee), b(arguments)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceNewTargetExpression(@NotNull NewTargetExpression node) {
        return new Pair<>(reducerA.reduceNewTargetExpression(node), reducerB.reduceNewTargetExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<Pair<A, B>> properties) {
        return new Pair<>(reducerA.reduceObjectBinding(node, a(properties)), reducerB.reduceObjectBinding(node, b(properties)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceObjectExpression(
            @NotNull ObjectExpression node,
            @NotNull ImmutableList<Pair<A, B>> properties) {
        return new Pair<>(reducerA.reduceObjectExpression(node, a(properties)), reducerB.reduceObjectExpression(node, b(properties)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceReturnStatement(
            @NotNull ReturnStatement node,
            @NotNull Maybe<Pair<A, B>> expression) {
        return new Pair<>(reducerA.reduceReturnStatement(node, a(expression)), reducerB.reduceReturnStatement(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceScript(@NotNull Script node, @NotNull ImmutableList<Pair<A, B>> directives, @NotNull ImmutableList<Pair<A, B>> statements) {
        return new Pair<>(reducerA.reduceScript(node, a(directives), a(statements)), reducerB.reduceScript(node, b(directives), b(statements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSetter(
            @NotNull Setter node,
            @NotNull Pair<A, B> name,
            @NotNull Pair<A, B> parameter,
            @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceSetter(node, a(name), a(parameter), a(body)), reducerB.reduceSetter(node, b(name), b(parameter), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceShorthandProperty(@NotNull ShorthandProperty node) {
        return new Pair<>(reducerA.reduceShorthandProperty(node), reducerB.reduceShorthandProperty(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSpreadElement(@NotNull SpreadElement node, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceSpreadElement(node, a(expression)), reducerB.reduceSpreadElement(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull Pair<A, B> object) {
        return new Pair<>(reducerA.reduceStaticMemberExpression(node, a(object)), reducerB.reduceStaticMemberExpression(node, b(object)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceStaticPropertyName(@NotNull StaticPropertyName node) {
        return new Pair<>(reducerA.reduceStaticPropertyName(node), reducerB.reduceStaticPropertyName(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSuper(@NotNull Super node) {
        return new Pair<>(reducerA.reduceSuper(node), reducerB.reduceSuper(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSwitchCase(
            @NotNull SwitchCase node,
            @NotNull Pair<A, B> test,
            @NotNull ImmutableList<Pair<A, B>> consequent) {
        return new Pair<>(reducerA.reduceSwitchCase(node, a(test), a(consequent)), reducerB.reduceSwitchCase(node, b(test), b(consequent)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSwitchDefault(
            @NotNull SwitchDefault node,
            @NotNull ImmutableList<Pair<A, B>> consequent) {
        return new Pair<>(reducerA.reduceSwitchDefault(node, a(consequent)), reducerB.reduceSwitchDefault(node, b(consequent)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSwitchStatement(
            @NotNull SwitchStatement node,
            @NotNull Pair<A, B> discriminant,
            @NotNull ImmutableList<Pair<A, B>> cases) {
        return new Pair<>(reducerA.reduceSwitchStatement(node, a(discriminant), a(cases)), reducerB.reduceSwitchStatement(node, b(discriminant), b(cases)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceSwitchStatementWithDefault(
            @NotNull SwitchStatementWithDefault node,
            @NotNull Pair<A, B> discriminant,
            @NotNull ImmutableList<Pair<A, B>> preDefaultCases,
            @NotNull Pair<A, B> defaultCase,
            @NotNull ImmutableList<Pair<A, B>> postDefaultCases) {
        return new Pair<>(reducerA.reduceSwitchStatementWithDefault(node, a(discriminant), a(preDefaultCases), a(defaultCase), a(postDefaultCases)), reducerB.reduceSwitchStatementWithDefault(node, b(discriminant), b(preDefaultCases), b(defaultCase), b(postDefaultCases)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceTemplateElement(@NotNull TemplateElement node) {
        return new Pair<>(reducerA.reduceTemplateElement(node), reducerB.reduceTemplateElement(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<Pair<A, B>> tag, @NotNull ImmutableList<Pair<A, B>> elements) {
        return new Pair<>(reducerA.reduceTemplateExpression(node, a(tag), a(elements)), reducerB.reduceTemplateExpression(node, b(tag), b(elements)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceThisExpression(@NotNull ThisExpression node) {
        return new Pair<>(reducerA.reduceThisExpression(node), reducerB.reduceThisExpression(node));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceThrowStatement(@NotNull ThrowStatement node, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceThrowStatement(node, a(expression)), reducerB.reduceThrowStatement(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceTryCatchStatement(
            @NotNull TryCatchStatement node,
            @NotNull Pair<A, B> block,
            @NotNull Pair<A, B> catchClause) {
        return new Pair<>(reducerA.reduceTryCatchStatement(node, a(block), a(catchClause)), reducerB.reduceTryCatchStatement(node, b(block), b(catchClause)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceTryFinallyStatement(
            @NotNull TryFinallyStatement node,
            @NotNull Pair<A, B> block,
            @NotNull Maybe<Pair<A, B>> catchClause,
            @NotNull Pair<A, B> finalizer) {
        return new Pair<>(reducerA.reduceTryFinallyStatement(node, a(block), a(catchClause), a(finalizer)), reducerB.reduceTryFinallyStatement(node, b(block), b(catchClause), b(finalizer)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull Pair<A, B> operand) {
        return new Pair<>(reducerA.reduceUnaryExpression(node, a(operand)), reducerB.reduceUnaryExpression(node, b(operand)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull Pair<A, B> operand) {
        return new Pair<>(reducerA.reduceUpdateExpression(node, a(operand)), reducerB.reduceUpdateExpression(node, b(operand)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<Pair<A, B>> declarators) {
        return new Pair<>(reducerA.reduceVariableDeclaration(node, a(declarators)), reducerB.reduceVariableDeclaration(node, b(declarators)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceVariableDeclarationStatement(
            @NotNull VariableDeclarationStatement node,
            @NotNull Pair<A, B> declaration) {
        return new Pair<>(reducerA.reduceVariableDeclarationStatement(node, a(declaration)), reducerB.reduceVariableDeclarationStatement(node, b(declaration)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceVariableDeclarator(
            @NotNull VariableDeclarator node,
            @NotNull Pair<A, B> binding,
            @NotNull Maybe<Pair<A, B>> init) {
        return new Pair<>(reducerA.reduceVariableDeclarator(node, a(binding), a(init)), reducerB.reduceVariableDeclarator(node, b(binding), b(init)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceWhileStatement(
            @NotNull WhileStatement node,
            @NotNull Pair<A, B> test,
            @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceWhileStatement(node, a(test), a(body)), reducerB.reduceWhileStatement(node, b(test), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceWithStatement(
            @NotNull WithStatement node,
            @NotNull Pair<A, B> object,
            @NotNull Pair<A, B> body) {
        return new Pair<>(reducerA.reduceWithStatement(node, a(object), a(body)), reducerB.reduceWithStatement(node, b(object), b(body)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<Pair<A, B>> expression) {
        return new Pair<>(reducerA.reduceYieldExpression(node, a(expression)), reducerB.reduceYieldExpression(node, b(expression)));
    }

    @NotNull
    @Override
    public Pair<A, B> reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull Pair<A, B> expression) {
        return new Pair<>(reducerA.reduceYieldGeneratorExpression(node, a(expression)), reducerB.reduceYieldGeneratorExpression(node, b(expression)));
    }
}
