//package com.shapesecurity.shift.visitor;
//
//import com.shapesecurity.functional.Pair;
//import com.shapesecurity.functional.data.FreePairingMonoid;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.functional.data.Monoid;
//import com.shapesecurity.shift.ast.*;
//
//import javax.annotation.Nonnull;
//
//public class ComposedMonoidalReducer<A, B> extends MonoidalReducer<Pair<A, B>> {
//    private final MonoidalReducer<A> reducerA;
//    private final MonoidalReducer<B> reducerB;
//
//    protected ComposedMonoidalReducer(@Nonnull Monoid<Pair<A, B>> monoidClass, MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB) {
//        super(monoidClass);
//        this.reducerA = reducerA;
//        this.reducerB = reducerB;
//    }
//
//    public static <A, B> ComposedMonoidalReducer<A, B> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB) {
//        return new ComposedMonoidalReducer<>(new FreePairingMonoid<>(reducerA.monoidClass, reducerB.monoidClass), reducerA, reducerB);
//    }
//
//    public static <A, B, C> ComposedMonoidalReducer<A, Pair<B, C>> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB, MonoidalReducer<C> reducerC) {
//        return ComposedMonoidalReducer.from(reducerA, ComposedMonoidalReducer.from(reducerB, reducerC));
//    }
//
//    public static <A, B, C, D> ComposedMonoidalReducer<A, Pair<B, Pair<C, D>>> from(MonoidalReducer<A> reducerA, MonoidalReducer<B> reducerB, MonoidalReducer<C> reducerC, MonoidalReducer<D> reducerD) {
//        return ComposedMonoidalReducer.from(reducerA, ComposedMonoidalReducer.from(reducerB, ComposedMonoidalReducer.from(reducerC, reducerD)));
//    }
//
//    private A a(Pair<A, B> pair) {
//        return pair.a;
//    }
//
//    private B b(Pair<A, B> pair) {
//        return pair.b;
//    }
//
//    private ImmutableList<A> a(ImmutableList<Pair<A, B>> list) {
//        return list.map(x -> x.a);
//    }
//
//    private ImmutableList<B> b(ImmutableList<Pair<A, B>> list) {
//        return list.map(x -> x.b);
//    }
//
//    private Maybe<A> a(Maybe<Pair<A, B>> maybe) {
//        return maybe.map(x -> x.a);
//    }
//
//    private Maybe<B> b(Maybe<Pair<A, B>> maybe) {
//        return maybe.map(x -> x.b);
//    }
//
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceArrayBinding(@Nonnull ArrayBinding node, @Nonnull ImmutableList<Maybe<Pair<A, B>>> elements, @Nonnull Maybe<Pair<A, B>> restElement) {
//        return new Pair<>(reducerA.reduceArrayBinding(node, elements.map(this::a), a(restElement)), reducerB.reduceArrayBinding(node, elements.map(this::b), b(restElement)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceArrayExpression(
//            @Nonnull ArrayExpression node,
//            @Nonnull ImmutableList<Maybe<Pair<A, B>>> elements) {
//        return new Pair<>(reducerA.reduceArrayExpression(node, elements.map(this::a)), reducerB.reduceArrayExpression(node, elements.map(this::b)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull Pair<A, B> params, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceArrowExpression(node, a(params), a(body)), reducerB.reduceArrowExpression(node, b(params), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceAssignmentExpression(
//            @Nonnull AssignmentExpression node,
//            @Nonnull Pair<A, B> binding,
//            @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceAssignmentExpression(node, a(binding), a(expression)), reducerB.reduceAssignmentExpression(node, b(binding), b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBinaryExpression(
//            @Nonnull BinaryExpression node,
//            @Nonnull Pair<A, B> left,
//            @Nonnull Pair<A, B> right) {
//        return new Pair<>(reducerA.reduceBinaryExpression(node, a(left), a(right)), reducerB.reduceBinaryExpression(node, b(left), b(right)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
//        return new Pair<>(reducerA.reduceBindingIdentifier(node), reducerB.reduceBindingIdentifier(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBindingPropertyIdentifier(@Nonnull BindingPropertyIdentifier node, @Nonnull Pair<A, B> binding, @Nonnull Maybe<Pair<A, B>> init) {
//        return new Pair<>(reducerA.reduceBindingPropertyIdentifier(node, a(binding), a(init)), reducerB.reduceBindingPropertyIdentifier(node, b(binding), b(init)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBindingPropertyProperty(@Nonnull BindingPropertyProperty node, @Nonnull Pair<A, B> name, @Nonnull Pair<A, B> binding) {
//        return new Pair<>(reducerA.reduceBindingPropertyProperty(node, a(name), a(binding)), reducerB.reduceBindingPropertyProperty(node, b(name), b(binding)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBindingWithDefault(@Nonnull BindingWithDefault node, @Nonnull Pair<A, B> binding, @Nonnull Pair<A, B> init) {
//        return new Pair<>(reducerA.reduceBindingWithDefault(node, a(binding), a(init)), reducerB.reduceBindingWithDefault(node, b(binding), b(init)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<Pair<A, B>> statements) {
//        return new Pair<>(reducerA.reduceBlock(node, a(statements)), reducerB.reduceBlock(node, b(statements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBlockStatement(@Nonnull BlockStatement node, @Nonnull Pair<A, B> block) {
//        return new Pair<>(reducerA.reduceBlockStatement(node, a(block)), reducerB.reduceBlockStatement(node, b(block)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceBreakStatement(@Nonnull BreakStatement node) {
//        return new Pair<>(reducerA.reduceBreakStatement(node), reducerB.reduceBreakStatement(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceCallExpression(
//            @Nonnull CallExpression node,
//            @Nonnull Pair<A, B> callee,
//            @Nonnull ImmutableList<Pair<A, B>> arguments) {
//        return new Pair<>(reducerA.reduceCallExpression(node, a(callee), a(arguments)), reducerB.reduceCallExpression(node, b(callee), b(arguments)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceCatchClause(
//            @Nonnull CatchClause node,
//            @Nonnull Pair<A, B> binding,
//            @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceCatchClause(node, a(binding), a(body)), reducerB.reduceCatchClause(node, b(binding), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull Pair<A, B> name, @Nonnull Maybe<Pair<A, B>> _super, @Nonnull ImmutableList<Pair<A, B>> elements) {
//        return new Pair<>(reducerA.reduceClassDeclaration(node, a(name), a(_super), a(elements)), reducerB.reduceClassDeclaration(node, b(name), b(_super), b(elements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceClassElement(@Nonnull ClassElement node, @Nonnull Pair<A, B> method) {
//        return new Pair<>(reducerA.reduceClassElement(node, a(method)), reducerB.reduceClassElement(node, b(method)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<Pair<A, B>> name, @Nonnull Maybe<Pair<A, B>> _super, @Nonnull ImmutableList<Pair<A, B>> elements) {
//        return new Pair<>(reducerA.reduceClassExpression(node, a(name), a(_super), a(elements)), reducerB.reduceClassExpression(node, b(name), b(_super), b(elements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceCompoundAssignmentExpression(@Nonnull CompoundAssignmentExpression node, @Nonnull Pair<A, B> binding, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceCompoundAssignmentExpression(node, a(binding), a(expression)), reducerB.reduceCompoundAssignmentExpression(node, b(binding), b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceComputedMemberExpression(
//            @Nonnull ComputedMemberExpression node,
//            @Nonnull Pair<A, B> object,
//            @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceComputedMemberExpression(node, a(object), a(expression)), reducerB.reduceComputedMemberExpression(node, b(object), b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceComputedPropertyName(@Nonnull ComputedPropertyName node, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceComputedPropertyName(node, a(expression)), reducerB.reduceComputedPropertyName(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceConditionalExpression(
//            @Nonnull ConditionalExpression node,
//            @Nonnull Pair<A, B> test,
//            @Nonnull Pair<A, B> consequent,
//            @Nonnull Pair<A, B> alternate) {
//        return new Pair<>(reducerA.reduceConditionalExpression(node, a(test), a(consequent), a(alternate)), reducerB.reduceConditionalExpression(node, b(test), b(consequent), b(alternate)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceContinueStatement(@Nonnull ContinueStatement node) {
//        return new Pair<>(reducerA.reduceContinueStatement(node), reducerB.reduceContinueStatement(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceDataProperty(
//            @Nonnull DataProperty node,
//            @Nonnull Pair<A, B> name,
//            @Nonnull Pair<A, B> value) {
//        return new Pair<>(reducerA.reduceDataProperty(node, a(name), a(value)), reducerB.reduceDataProperty(node, b(name), b(value)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceDebuggerStatement(@Nonnull DebuggerStatement node) {
//        return new Pair<>(reducerA.reduceDebuggerStatement(node), reducerB.reduceDebuggerStatement(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceDirective(@Nonnull Directive node) {
//        return new Pair<>(reducerA.reduceDirective(node), reducerB.reduceDirective(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceDoWhileStatement(
//            @Nonnull DoWhileStatement node,
//            @Nonnull Pair<A, B> body,
//            @Nonnull Pair<A, B> test) {
//        return new Pair<>(reducerA.reduceDoWhileStatement(node, a(body), a(test)), reducerB.reduceDoWhileStatement(node, b(body), b(test)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceEmptyStatement(@Nonnull EmptyStatement node) {
//        return new Pair<>(reducerA.reduceEmptyStatement(node), reducerB.reduceEmptyStatement(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExport(@Nonnull Export node, @Nonnull Pair<A, B> declaration) {
//        return new Pair<>(reducerA.reduceExport(node, a(declaration)), reducerB.reduceExport(node, b(declaration)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExportAllFrom(@Nonnull ExportAllFrom node) {
//        return new Pair<>(reducerA.reduceExportAllFrom(node), reducerB.reduceExportAllFrom(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExportDefault(@Nonnull ExportDefault node, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceExportDefault(node, a(body)), reducerB.reduceExportDefault(node, b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExportFrom(@Nonnull ExportFrom node, @Nonnull ImmutableList<Pair<A, B>> namedExports) {
//        return new Pair<>(reducerA.reduceExportFrom(node, a(namedExports)), reducerB.reduceExportFrom(node, b(namedExports)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExportSpecifier(@Nonnull ExportSpecifier node) {
//        return new Pair<>(reducerA.reduceExportSpecifier(node), reducerB.reduceExportSpecifier(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceExpressionStatement(@Nonnull ExpressionStatement node, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceExpressionStatement(node, a(expression)), reducerB.reduceExpressionStatement(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceForInStatement(@Nonnull ForInStatement node, @Nonnull Pair<A, B> left, @Nonnull Pair<A, B> right, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceForInStatement(node, a(left), a(right), a(body)), reducerB.reduceForInStatement(node, b(left), b(right), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull Pair<A, B> left, @Nonnull Pair<A, B> right, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceForOfStatement(node, a(left), a(right), a(body)), reducerB.reduceForOfStatement(node, b(left), b(right), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceForStatement(@Nonnull ForStatement node, @Nonnull Maybe<Pair<A, B>> init, @Nonnull Maybe<Pair<A, B>> test, @Nonnull Maybe<Pair<A, B>> update, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceForStatement(node, a(init), a(test), a(update), a(body)), reducerB.reduceForStatement(node, b(init), b(test), b(update), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceFormalParameters(@Nonnull FormalParameters node, @Nonnull ImmutableList<Pair<A, B>> items, @Nonnull Maybe<Pair<A, B>> rest) {
//        return new Pair<>(reducerA.reduceFormalParameters(node, a(items), a(rest)), reducerB.reduceFormalParameters(node, b(items), b(rest)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceFunctionBody(
//            @Nonnull FunctionBody node,
//            @Nonnull ImmutableList<Pair<A, B>> directives,
//            @Nonnull ImmutableList<Pair<A, B>> statements) {
//        return new Pair<>(reducerA.reduceFunctionBody(node, a(directives), a(statements)), reducerB.reduceFunctionBody(node, b(directives), b(statements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull Pair<A, B> name, @Nonnull Pair<A, B> params, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceFunctionDeclaration(node, a(name), a(params), a(body)), reducerB.reduceFunctionDeclaration(node, b(name), b(params), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<Pair<A, B>> name, @Nonnull Pair<A, B> params, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceFunctionExpression(node, a(name), a(params), a(body)), reducerB.reduceFunctionExpression(node, b(name), b(params), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceGetter(@Nonnull Getter node, @Nonnull Pair<A, B> name, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceGetter(node, a(name), a(body)), reducerB.reduceGetter(node, b(name), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
//        return new Pair<>(reducerA.reduceIdentifierExpression(node), reducerB.reduceIdentifierExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceIfStatement(
//            @Nonnull IfStatement node,
//            @Nonnull Pair<A, B> test,
//            @Nonnull Pair<A, B> consequent,
//            @Nonnull Maybe<Pair<A, B>> alternate) {
//        return new Pair<>(reducerA.reduceIfStatement(node, a(test), a(consequent), a(alternate)), reducerB.reduceIfStatement(node, b(test), b(consequent), b(alternate)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceImport(@Nonnull Import node, @Nonnull Maybe<Pair<A, B>> defaultBinding, @Nonnull ImmutableList<Pair<A, B>> namedImports) {
//        return new Pair<>(reducerA.reduceImport(node, a(defaultBinding), a(namedImports)), reducerB.reduceImport(node, b(defaultBinding), b(namedImports)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<Pair<A, B>> defaultBinding, @Nonnull Pair<A, B> namespaceBinding) {
//        return new Pair<>(reducerA.reduceImportNamespace(node, a(defaultBinding), a(namespaceBinding)), reducerB.reduceImportNamespace(node, b(defaultBinding), b(namespaceBinding)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceImportSpecifier(@Nonnull ImportSpecifier node, @Nonnull Pair<A, B> binding) {
//        return new Pair<>(reducerA.reduceImportSpecifier(node, a(binding)), reducerB.reduceImportSpecifier(node, b(binding)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLabeledStatement(@Nonnull LabeledStatement node, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceLabeledStatement(node, a(body)), reducerB.reduceLabeledStatement(node, b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node) {
//        return new Pair<>(reducerA.reduceLiteralBooleanExpression(node), reducerB.reduceLiteralBooleanExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node) {
//        return new Pair<>(reducerA.reduceLiteralInfinityExpression(node), reducerB.reduceLiteralInfinityExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralNullExpression(@Nonnull LiteralNullExpression node) {
//        return new Pair<>(reducerA.reduceLiteralNullExpression(node), reducerB.reduceLiteralNullExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node) {
//        return new Pair<>(reducerA.reduceLiteralNumericExpression(node), reducerB.reduceLiteralNumericExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
//        return new Pair<>(reducerA.reduceLiteralRegExpExpression(node), reducerB.reduceLiteralRegExpExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceLiteralStringExpression(@Nonnull LiteralStringExpression node) {
//        return new Pair<>(reducerA.reduceLiteralStringExpression(node), reducerB.reduceLiteralStringExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceMethod(@Nonnull Method node, @Nonnull Pair<A, B> name, @Nonnull Pair<A, B> params, @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceMethod(node, a(name), a(params), a(body)), reducerB.reduceMethod(node, b(name), b(params), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceModule(@Nonnull Module node, @Nonnull ImmutableList<Pair<A, B>> directives, @Nonnull ImmutableList<Pair<A, B>> items) {
//        return new Pair<>(reducerA.reduceModule(node, a(directives), a(items)), reducerB.reduceModule(node, b(directives), b(items)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceNewExpression(
//            @Nonnull NewExpression node,
//            @Nonnull Pair<A, B> callee,
//            @Nonnull ImmutableList<Pair<A, B>> arguments) {
//        return new Pair<>(reducerA.reduceNewExpression(node, a(callee), a(arguments)), reducerB.reduceNewExpression(node, b(callee), b(arguments)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceNewTargetExpression(@Nonnull NewTargetExpression node) {
//        return new Pair<>(reducerA.reduceNewTargetExpression(node), reducerB.reduceNewTargetExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceObjectBinding(@Nonnull ObjectBinding node, @Nonnull ImmutableList<Pair<A, B>> properties) {
//        return new Pair<>(reducerA.reduceObjectBinding(node, a(properties)), reducerB.reduceObjectBinding(node, b(properties)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceObjectExpression(
//            @Nonnull ObjectExpression node,
//            @Nonnull ImmutableList<Pair<A, B>> properties) {
//        return new Pair<>(reducerA.reduceObjectExpression(node, a(properties)), reducerB.reduceObjectExpression(node, b(properties)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceReturnStatement(
//            @Nonnull ReturnStatement node,
//            @Nonnull Maybe<Pair<A, B>> expression) {
//        return new Pair<>(reducerA.reduceReturnStatement(node, a(expression)), reducerB.reduceReturnStatement(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceScript(@Nonnull Script node, @Nonnull ImmutableList<Pair<A, B>> directives, @Nonnull ImmutableList<Pair<A, B>> statements) {
//        return new Pair<>(reducerA.reduceScript(node, a(directives), a(statements)), reducerB.reduceScript(node, b(directives), b(statements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSetter(
//            @Nonnull Setter node,
//            @Nonnull Pair<A, B> name,
//            @Nonnull Pair<A, B> param,
//            @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceSetter(node, a(name), a(param), a(body)), reducerB.reduceSetter(node, b(name), b(param), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceShorthandProperty(@Nonnull ShorthandProperty node) {
//        return new Pair<>(reducerA.reduceShorthandProperty(node), reducerB.reduceShorthandProperty(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSpreadElement(@Nonnull SpreadElement node, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceSpreadElement(node, a(expression)), reducerB.reduceSpreadElement(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull Pair<A, B> object) {
//        return new Pair<>(reducerA.reduceStaticMemberExpression(node, a(object)), reducerB.reduceStaticMemberExpression(node, b(object)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceStaticPropertyName(@Nonnull StaticPropertyName node) {
//        return new Pair<>(reducerA.reduceStaticPropertyName(node), reducerB.reduceStaticPropertyName(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSuper(@Nonnull Super node) {
//        return new Pair<>(reducerA.reduceSuper(node), reducerB.reduceSuper(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSwitchCase(
//            @Nonnull SwitchCase node,
//            @Nonnull Pair<A, B> test,
//            @Nonnull ImmutableList<Pair<A, B>> consequent) {
//        return new Pair<>(reducerA.reduceSwitchCase(node, a(test), a(consequent)), reducerB.reduceSwitchCase(node, b(test), b(consequent)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSwitchDefault(
//            @Nonnull SwitchDefault node,
//            @Nonnull ImmutableList<Pair<A, B>> consequent) {
//        return new Pair<>(reducerA.reduceSwitchDefault(node, a(consequent)), reducerB.reduceSwitchDefault(node, b(consequent)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSwitchStatement(
//            @Nonnull SwitchStatement node,
//            @Nonnull Pair<A, B> discriminant,
//            @Nonnull ImmutableList<Pair<A, B>> cases) {
//        return new Pair<>(reducerA.reduceSwitchStatement(node, a(discriminant), a(cases)), reducerB.reduceSwitchStatement(node, b(discriminant), b(cases)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceSwitchStatementWithDefault(
//            @Nonnull SwitchStatementWithDefault node,
//            @Nonnull Pair<A, B> discriminant,
//            @Nonnull ImmutableList<Pair<A, B>> preDefaultCases,
//            @Nonnull Pair<A, B> defaultCase,
//            @Nonnull ImmutableList<Pair<A, B>> postDefaultCases) {
//        return new Pair<>(reducerA.reduceSwitchStatementWithDefault(node, a(discriminant), a(preDefaultCases), a(defaultCase), a(postDefaultCases)), reducerB.reduceSwitchStatementWithDefault(node, b(discriminant), b(preDefaultCases), b(defaultCase), b(postDefaultCases)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceTemplateElement(@Nonnull TemplateElement node) {
//        return new Pair<>(reducerA.reduceTemplateElement(node), reducerB.reduceTemplateElement(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceTemplateExpression(@Nonnull TemplateExpression node, @Nonnull Maybe<Pair<A, B>> tag, @Nonnull ImmutableList<Pair<A, B>> elements) {
//        return new Pair<>(reducerA.reduceTemplateExpression(node, a(tag), a(elements)), reducerB.reduceTemplateExpression(node, b(tag), b(elements)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceThisExpression(@Nonnull ThisExpression node) {
//        return new Pair<>(reducerA.reduceThisExpression(node), reducerB.reduceThisExpression(node));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceThrowStatement(@Nonnull ThrowStatement node, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceThrowStatement(node, a(expression)), reducerB.reduceThrowStatement(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceTryCatchStatement(
//            @Nonnull TryCatchStatement node,
//            @Nonnull Pair<A, B> block,
//            @Nonnull Pair<A, B> catchClause) {
//        return new Pair<>(reducerA.reduceTryCatchStatement(node, a(block), a(catchClause)), reducerB.reduceTryCatchStatement(node, b(block), b(catchClause)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceTryFinallyStatement(
//            @Nonnull TryFinallyStatement node,
//            @Nonnull Pair<A, B> block,
//            @Nonnull Maybe<Pair<A, B>> catchClause,
//            @Nonnull Pair<A, B> finalizer) {
//        return new Pair<>(reducerA.reduceTryFinallyStatement(node, a(block), a(catchClause), a(finalizer)), reducerB.reduceTryFinallyStatement(node, b(block), b(catchClause), b(finalizer)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceUnaryExpression(@Nonnull UnaryExpression node, @Nonnull Pair<A, B> operand) {
//        return new Pair<>(reducerA.reduceUnaryExpression(node, a(operand)), reducerB.reduceUnaryExpression(node, b(operand)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceUpdateExpression(@Nonnull UpdateExpression node, @Nonnull Pair<A, B> operand) {
//        return new Pair<>(reducerA.reduceUpdateExpression(node, a(operand)), reducerB.reduceUpdateExpression(node, b(operand)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull ImmutableList<Pair<A, B>> declarators) {
//        return new Pair<>(reducerA.reduceVariableDeclaration(node, a(declarators)), reducerB.reduceVariableDeclaration(node, b(declarators)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceVariableDeclarationStatement(
//            @Nonnull VariableDeclarationStatement node,
//            @Nonnull Pair<A, B> declaration) {
//        return new Pair<>(reducerA.reduceVariableDeclarationStatement(node, a(declaration)), reducerB.reduceVariableDeclarationStatement(node, b(declaration)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceVariableDeclarator(
//            @Nonnull VariableDeclarator node,
//            @Nonnull Pair<A, B> binding,
//            @Nonnull Maybe<Pair<A, B>> init) {
//        return new Pair<>(reducerA.reduceVariableDeclarator(node, a(binding), a(init)), reducerB.reduceVariableDeclarator(node, b(binding), b(init)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceWhileStatement(
//            @Nonnull WhileStatement node,
//            @Nonnull Pair<A, B> test,
//            @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceWhileStatement(node, a(test), a(body)), reducerB.reduceWhileStatement(node, b(test), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceWithStatement(
//            @Nonnull WithStatement node,
//            @Nonnull Pair<A, B> object,
//            @Nonnull Pair<A, B> body) {
//        return new Pair<>(reducerA.reduceWithStatement(node, a(object), a(body)), reducerB.reduceWithStatement(node, b(object), b(body)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceYieldExpression(@Nonnull YieldExpression node, @Nonnull Maybe<Pair<A, B>> expression) {
//        return new Pair<>(reducerA.reduceYieldExpression(node, a(expression)), reducerB.reduceYieldExpression(node, b(expression)));
//    }
//
//    @Nonnull
//    @Override
//    public Pair<A, B> reduceYieldGeneratorExpression(@Nonnull YieldGeneratorExpression node, @Nonnull Pair<A, B> expression) {
//        return new Pair<>(reducerA.reduceYieldGeneratorExpression(node, a(expression)), reducerB.reduceYieldGeneratorExpression(node, b(expression)));
//    }
//}
