package com.shapesecurity.shift.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.visitor.Reducer;
import org.jetbrains.annotations.NotNull;

public class CloneReducer implements Reducer<Node> {

    @NotNull
    @Override
    public Binding reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<Node>> elements, @NotNull Maybe<Node> restElement) {
        ImmutableList<Maybe<BindingBindingWithDefault>> newElements = elements.map(x -> x.map(y -> (BindingBindingWithDefault)y));
        Maybe<Binding> newRestElement = restElement.map(x -> (Binding) x);
        return new ArrayBinding(newElements, newRestElement);
    }


    @NotNull
    @Override
    public Expression reduceArrayExpression(@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<Node>> elements) {
        ImmutableList<Maybe<SpreadElementExpression>> newElements = elements.map(x -> x.map(y -> (SpreadElementExpression)y));
        return new ArrayExpression(newElements);
    }

    @NotNull
    @Override
    public Expression reduceArrowExpression(@NotNull ArrowExpression node, @NotNull Node params, @NotNull Node body) {
        return new ArrowExpression((FormalParameters)params, (FunctionBodyExpression) body);
    }

    @NotNull
    @Override
    public Expression reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull Node binding, @NotNull Node expression) {
        return new AssignmentExpression((Binding) binding, (Expression) expression);
    }

    @NotNull
    @Override
    public Expression reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull Node left, @NotNull Node right) {
        return new BinaryExpression(node.getOperator(), (Expression) left, (Expression) right);
    }

    @NotNull
    @Override
    public BindingIdentifier reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        return node;
    }

    @NotNull
    @Override
    public BindingProperty reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull Node binding, @NotNull Maybe<Node> init) {
        Maybe<Expression> newInit = init.map(x -> (Expression) x);
        return new BindingPropertyIdentifier((BindingIdentifier) binding, newInit);
    }

    @NotNull
    @Override
    public BindingProperty reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull Node name, @NotNull Node binding) {
        return new BindingPropertyProperty((PropertyName) name, (BindingBindingWithDefault) binding);
    }

    @NotNull
    @Override
    public BindingWithDefault reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull Node binding, @NotNull Node init) {
        return new BindingWithDefault((Binding) binding, (Expression) init);
    }

    @NotNull
    @Override
    public Block reduceBlock(@NotNull Block node, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);
        return new Block(newStatements);
    }

    @NotNull
    @Override
    public Statement reduceBlockStatement(@NotNull BlockStatement node, @NotNull Node block) {
        return new BlockStatement((Block) block);
    }

    @NotNull
    @Override
    public Statement reduceBreakStatement(@NotNull BreakStatement node) {
        return new BreakStatement(node.getLabel());
    }

    @NotNull
    @Override
    public Expression reduceCallExpression(@NotNull CallExpression node, @NotNull Node callee, @NotNull ImmutableList<Node> arguments) {
        ImmutableList<SpreadElementExpression> newArguments = arguments.map(x -> (SpreadElementExpression) x);
        return new CallExpression((ExpressionSuper) callee, newArguments);
    }

    @NotNull
    @Override
    public CatchClause reduceCatchClause(@NotNull CatchClause node, @NotNull Node binding, @NotNull Node body) {
        return new CatchClause((Binding) binding, (Block) body);
    }

    @NotNull
    @Override
    public Statement reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull Node name, @NotNull Maybe<Node> _super, @NotNull ImmutableList<Node> elements) {
        Maybe<Expression> newSuper = _super.map(x -> (Expression)x);
        ImmutableList<ClassElement> newElements = elements.map(x -> (ClassElement) x);
        return new ClassDeclaration((BindingIdentifier) name, newSuper, newElements);
    }

    @NotNull
    @Override
    public ClassElement reduceClassElement(@NotNull ClassElement node, @NotNull Node method) {
        return new ClassElement(node.isStatic, (MethodDefinition) method);
    }

    @NotNull
    @Override
    public Expression reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<Node> name, @NotNull Maybe<Node> _super, @NotNull ImmutableList<Node> elements) {
        Maybe<BindingIdentifier> newName = name.map(x -> (BindingIdentifier) x);
        Maybe<Expression> newSuper = _super.map(x -> (Expression) x);
        ImmutableList<ClassElement> newElements = elements.map(x -> (ClassElement) x);
        return new ClassExpression(newName, newSuper, newElements);
    }

    @NotNull
    @Override
    public Expression reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull Node binding, @NotNull Node expression) {
        return new CompoundAssignmentExpression(node.operator, (BindingIdentifierMemberExpression) binding, (Expression) expression);
    }

    @NotNull
    @Override
    public MemberExpression reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull Node object, @NotNull Node expression) {
        return new ComputedMemberExpression((Expression) expression, (ExpressionSuper) object);
    }

    @NotNull
    @Override
    public PropertyName reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull Node expression) {
        return new ComputedPropertyName((Expression) expression);
    }

    @NotNull
    @Override
    public Expression reduceConditionalExpression(@NotNull ConditionalExpression node, @NotNull Node test, @NotNull Node consequent, @NotNull Node alternate) {
        return new ConditionalExpression((Expression) test, (Expression) consequent, (Expression) alternate);
    }

    @NotNull
    @Override
    public Statement reduceContinueStatement(@NotNull ContinueStatement node) {
        return new ContinueStatement(node.getLabel());
    }

    @NotNull
    @Override
    public ObjectProperty reduceDataProperty(@NotNull DataProperty node, @NotNull Node value, @NotNull Node name) {
        return new DataProperty((Expression) value, (PropertyName) name); //TODO: should value be here?
    }

    @NotNull
    @Override
    public Statement reduceDebuggerStatement(@NotNull DebuggerStatement node) {
        return new DebuggerStatement(); //TODO: is this correct?
    }

    @NotNull
    @Override
    public Directive reduceDirective(@NotNull Directive node) {
        return node;
    }

    @NotNull
    @Override
    public Statement reduceDoWhileStatement(@NotNull DoWhileStatement node, @NotNull Node body, @NotNull Node test) {
        return new DoWhileStatement((Expression) test, (Statement) body);
    }

    @NotNull
    @Override
    public Statement reduceEmptyStatement(@NotNull EmptyStatement node) {
        return node;
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceExport(@NotNull Export node, @NotNull Node declaration) {
        return new Export((FunctionDeclarationClassDeclarationVariableDeclaration) declaration);
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceExportAllFrom(@NotNull ExportAllFrom node) {
        return node;
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceExportDefault(@NotNull ExportDefault node, @NotNull Node body) {
        return new ExportDefault((FunctionDeclarationClassDeclarationExpression) body);
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<Node> namedExports) {
        ImmutableList<ExportSpecifier> newNamedExports = namedExports.map(x -> (ExportSpecifier) x);
        return new ExportFrom(newNamedExports, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ExportSpecifier reduceExportSpecifier(@NotNull ExportSpecifier node) {
        return node;
    }

    @NotNull
    @Override
    public Statement reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull Node expression) {
        return new ExpressionStatement((Expression) expression);
    }

    @NotNull
    @Override
    public Statement reduceForInStatement(@NotNull ForInStatement node, @NotNull Node left, @NotNull Node right, @NotNull Node body) {
        return new ForInStatement((VariableDeclarationBinding) left, (Expression) right, (Statement) body);
    }

    @NotNull
    @Override
    public Statement reduceForOfStatement(@NotNull ForOfStatement node, @NotNull Node left, @NotNull Node right, @NotNull Node body) {
        return new ForOfStatement((VariableDeclarationBinding) left, (Expression) right, (Statement) body);
    }

    @NotNull
    @Override
    public Statement reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<Node> init, @NotNull Maybe<Node> test, @NotNull Maybe<Node> update, @NotNull Node body) {
        Maybe<VariableDeclarationExpression> newInit = init.map(x -> (VariableDeclarationExpression) x);
        Maybe<Expression> newTest = test.map(x -> (Expression) x);
        Maybe<Expression> newUpdate = update.map(x -> (Expression) x);
        return new ForStatement(newInit, newTest, newUpdate, (Statement) body);
    }

    @NotNull
    @Override
    public FormalParameters reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<Node> items, @NotNull Maybe<Node> rest) {
        ImmutableList<BindingBindingWithDefault> newItems = items.map(x -> (BindingBindingWithDefault) x);
        Maybe<BindingIdentifier> newRest = rest.map(x -> (BindingIdentifier) x);
        return new FormalParameters(newItems, newRest);
    }

    @NotNull
    @Override
    public FunctionBody reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);
        return new FunctionBody(newDirectives, newStatements);
    }

    @NotNull
    @Override
    public Statement reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull Node name, @NotNull Node params, @NotNull Node body) {
        return new FunctionDeclaration((BindingIdentifier) name, node.getIsGenerator(), (FormalParameters) params, (FunctionBody) body);
    }

    @NotNull
    @Override
    public Expression reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<Node> name, @NotNull Node params, @NotNull Node body) {
        Maybe<BindingIdentifier> newName = name.map(x -> (BindingIdentifier) x);
        return new FunctionExpression(newName, node.getIsGenerator(), (FormalParameters) params, (FunctionBody) body);
    }

    @NotNull
    @Override
    public MethodDefinition reduceGetter(@NotNull Getter node, @NotNull Node name, @NotNull Node body) {
        return new Getter((FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public Expression reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Statement reduceIfStatement(@NotNull IfStatement node, @NotNull Node test, @NotNull Node consequent, @NotNull Maybe<Node> alternate) {
        Maybe<Statement> newAlternate = alternate.map(x -> (Statement) x);
        return new IfStatement((Expression) test, (Statement) consequent, newAlternate);
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceImport(@NotNull Import node, @NotNull Maybe<Node> defaultBinding, @NotNull ImmutableList<Node> namedImports) {
        Maybe<BindingIdentifier> newDefaultBinding = defaultBinding.map(x -> (BindingIdentifier) x);
        ImmutableList<ImportSpecifier> newNamedImports = namedImports.map(x -> (ImportSpecifier) x);
        return new Import(newDefaultBinding, newNamedImports, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ImportDeclarationExportDeclarationStatement reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<Node> defaultBinding, @NotNull Node namespaceBinding) {
        Maybe<BindingIdentifier> newDefaultBinding = defaultBinding.map(x -> (BindingIdentifier) x);
        return new ImportNamespace(newDefaultBinding, (BindingIdentifier) namespaceBinding, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ImportSpecifier reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull Node binding) {
        return new ImportSpecifier(node.getName(), (BindingIdentifier) binding);
    }

    @NotNull
    @Override
    public Statement reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull Node body) {
        return new LabeledStatement(node.getLabel(), (Statement) body);
    }

    @NotNull
    @Override
    public Expression reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
        return node;
    }

    @NotNull
    @Override
    public MethodDefinition reduceMethod(@NotNull Method node, @NotNull Node name, @NotNull Node params, @NotNull Node body) {
        return new Method(node.getIsGenerator(), (FormalParameters) params, (FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public Module reduceModule(@NotNull Module node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> items) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<ImportDeclarationExportDeclarationStatement> newItems = items.map(x -> (ImportDeclarationExportDeclarationStatement) x);
        return new Module(newDirectives, newItems);
    }

    @NotNull
    @Override
    public Expression reduceNewExpression(@NotNull NewExpression node, @NotNull Node callee, @NotNull ImmutableList<Node> arguments) {
        ImmutableList<SpreadElementExpression> newArguments = arguments.map(x -> (SpreadElementExpression) x);
        return new NewExpression((Expression) callee, newArguments);
    }

    @NotNull
    @Override
    public Expression reduceNewTargetExpression(@NotNull NewTargetExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Binding reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<Node> properties) {
        ImmutableList<BindingProperty> newProperties = properties.map(x -> (BindingProperty) x);
        return new ObjectBinding(newProperties);
    }

    @NotNull
    @Override
    public Expression reduceObjectExpression(@NotNull ObjectExpression node, @NotNull ImmutableList<Node> properties) {
        ImmutableList<ObjectProperty> newProperties = properties.map(x -> (ObjectProperty) x);
        return new ObjectExpression(newProperties);
    }

    @NotNull
    @Override
    public Statement reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<Node> expression) {
        Maybe<Expression> newExpression = expression.map(x -> (Expression) x);
        return new ReturnStatement(newExpression);
    }

    @NotNull
    @Override
    public Script reduceScript(@NotNull Script node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);
        return new Script(newDirectives, newStatements);
    }

    @NotNull
    @Override
    public MethodDefinition reduceSetter(@NotNull Setter node, @NotNull Node name, @NotNull Node params, @NotNull Node body) {
        return new Setter((BindingBindingWithDefault) params, (FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public ObjectProperty reduceShorthandProperty(@NotNull ShorthandProperty node) {
        return node;
    }

    @NotNull
    @Override
    public SpreadElementExpression reduceSpreadElement(@NotNull SpreadElement node, @NotNull Node expression) {
        return new SpreadElement((Expression) expression);
    }

    @NotNull
    @Override
    public MemberExpression reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull Node object) {
        return new StaticMemberExpression(node.getProperty(), (ExpressionSuper) object);
    }

    @NotNull
    @Override
    public PropertyName reduceStaticPropertyName(@NotNull StaticPropertyName node) {
        return node;
    }

    @NotNull
    @Override
    public ExpressionSuper reduceSuper(@NotNull Super node) {
        return node;
    }

    @NotNull
    @Override
    public SwitchCase reduceSwitchCase(@NotNull SwitchCase node, @NotNull Node test, @NotNull ImmutableList<Node> consequent) {
        ImmutableList<Statement> newConsequent = consequent.map(x -> (Statement) x);
        return new SwitchCase((Expression) test, newConsequent);
    }

    @NotNull
    @Override
    public SwitchDefault reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<Node> consequent) {
        ImmutableList<Statement> newConsequent = consequent.map(x -> (Statement) x);
        return new SwitchDefault(newConsequent);
    }

    @NotNull
    @Override
    public Statement reduceSwitchStatement(@NotNull SwitchStatement node, @NotNull Node discriminant, @NotNull ImmutableList<Node> cases) {
        ImmutableList<SwitchCase> newCases = cases.map(x -> (SwitchCase) x);
        return new SwitchStatement((Expression) discriminant, newCases);
    }

    @NotNull
    @Override
    public Statement reduceSwitchStatementWithDefault(@NotNull SwitchStatementWithDefault node, @NotNull Node discriminant, @NotNull ImmutableList<Node> preDefaultCases, @NotNull Node defaultCase, @NotNull ImmutableList<Node> postDefaultCases) {
        ImmutableList<SwitchCase> newPreDefaultCases = preDefaultCases.map(x -> (SwitchCase) x);
        ImmutableList<SwitchCase> newPostDefaultCases = postDefaultCases.map(x -> (SwitchCase) x);
        return new SwitchStatementWithDefault((Expression) discriminant, newPreDefaultCases, (SwitchDefault) defaultCase, newPostDefaultCases);
    }

    @NotNull
    @Override
    public TemplateElement reduceTemplateElement(@NotNull TemplateElement node) {
        return node;
    }

    @NotNull
    @Override
    public Expression reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<Node> tag, @NotNull ImmutableList<Node> elements) {
        Maybe<Expression> newTag = tag.map(x -> (Expression) x);
        ImmutableList<ExpressionTemplateElement> newElements = elements.map(x -> (ExpressionTemplateElement) x);
        return new TemplateExpression(newTag, newElements);
    }

    @NotNull
    @Override
    public Expression reduceThisExpression(@NotNull ThisExpression node) {
        return node;
    }

    @NotNull
    @Override
    public Statement reduceThrowStatement(@NotNull ThrowStatement node, @NotNull Node expression) {
        return new ThrowStatement((Expression) expression);
    }

    @NotNull
    @Override
    public Statement reduceTryCatchStatement(@NotNull TryCatchStatement node, @NotNull Node block, @NotNull Node catchClause) {
        return new TryCatchStatement((Block) block, (CatchClause) catchClause);
    }

    @NotNull
    @Override
    public Statement reduceTryFinallyStatement(@NotNull TryFinallyStatement node, @NotNull Node block, @NotNull Maybe<Node> catchClause, @NotNull Node finalizer) {
        Maybe<CatchClause> newCatchClause = catchClause.map(x -> (CatchClause) x);
        return new TryFinallyStatement((Block) block, newCatchClause, (Block) finalizer);
    }

    @NotNull
    @Override
    public Expression reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull Node operand) {
        return new UnaryExpression(node.getOperator(), (Expression) operand);
    }

    @NotNull
    @Override
    public Expression reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull Node operand) {
        return new UpdateExpression(node.getIsPrefix(), node.getOperator(), (BindingIdentifierMemberExpression) operand);
    }

    @NotNull
    @Override
    public VariableDeclaration reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<Node> declarators) {
        ImmutableList<VariableDeclarator> newDeclarators = declarators.map(x -> (VariableDeclarator) x);
        return new VariableDeclaration(node.getKind(), newDeclarators);
    }

    @NotNull
    @Override
    public Statement reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull Node declaration) {
        return new VariableDeclarationStatement((VariableDeclaration) declaration);
    }

    @NotNull
    @Override
    public VariableDeclarator reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull Node binding, @NotNull Maybe<Node> init) {
        Maybe<Expression> newInit = init.map(x -> (Expression) x);
        return new VariableDeclarator((Binding) binding, newInit);
    }

    @NotNull
    @Override
    public Statement reduceWhileStatement(@NotNull WhileStatement node, @NotNull Node test, @NotNull Node body) {
        return new WhileStatement((Expression) test, (Statement) body);
    }

    @NotNull
    @Override
    public Statement reduceWithStatement(@NotNull WithStatement node, @NotNull Node object, @NotNull Node body) {
        return new WithStatement((Expression) object, (Statement) body);
    }

    @NotNull
    @Override
    public Expression reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<Node> expression) {
        Maybe<Expression> newExpression = expression.map(x -> (Expression) x);
        return new YieldExpression(newExpression);
    }

    @NotNull
    @Override
    public Expression reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull Node expression) {
        return new YieldGeneratorExpression((Expression) expression);
    }
}