package com.shapesecurity.shift.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.visitor.Reducer;
import org.jetbrains.annotations.NotNull;

public class CloneReducer implements Reducer<Node> {

    @NotNull
    @Override
    public ArrayBinding reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<Node>> elements, @NotNull Maybe<Node> restElement) {
        ImmutableList<Maybe<BindingBindingWithDefault>> newElements = elements.map(x -> x.map(y -> (BindingBindingWithDefault)y));
        Maybe<Binding> newRestElement = restElement.map(x -> (Binding) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new ArrayBinding(loc.just(), newElements, newRestElement) :
                new ArrayBinding(newElements, newRestElement);
    }


    @NotNull
    @Override
    public ArrayExpression reduceArrayExpression(@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<Node>> elements) {
        ImmutableList<Maybe<SpreadElementExpression>> newElements = elements.map(x -> x.map(y -> (SpreadElementExpression)y));
        return new ArrayExpression(newElements);
    }

    @NotNull
    @Override
    public ArrowExpression reduceArrowExpression(@NotNull ArrowExpression node, @NotNull Node params, @NotNull Node body) {
        return new ArrowExpression((FormalParameters)params, (FunctionBodyExpression) body);
    }

    @NotNull
    @Override
    public AssignmentExpression reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull Node binding, @NotNull Node expression) {
        return new AssignmentExpression((Binding) binding, (Expression) expression);
    }

    @NotNull
    @Override
    public BinaryExpression reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull Node left, @NotNull Node right) {
        return new BinaryExpression(node.getOperator(), (Expression) left, (Expression) right);
    }

    @NotNull
    @Override
    public BindingIdentifier reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new BindingIdentifier(loc.just(), node.getName()) :
                new BindingIdentifier(node.getName());
    }

    @NotNull
    @Override
    public BindingPropertyIdentifier reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull Node binding, @NotNull Maybe<Node> init) {
        Maybe<Expression> newInit = init.map(x -> (Expression) x);
        return new BindingPropertyIdentifier((BindingIdentifier) binding, newInit);
    }

    @NotNull
    @Override
    public BindingPropertyProperty reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull Node name, @NotNull Node binding) {
        return new BindingPropertyProperty((PropertyName) name, (BindingBindingWithDefault) binding);
    }

    @NotNull
    @Override
    public BindingWithDefault reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull Node binding, @NotNull Node init) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new BindingWithDefault(loc.just(), (Binding) binding, (Expression) init) :
                new BindingWithDefault((Binding) binding, (Expression) init);
    }

    @NotNull
    @Override
    public Block reduceBlock(@NotNull Block node, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new Block(loc.just(), newStatements) :
                new Block(newStatements);
    }

    @NotNull
    @Override
    public BlockStatement reduceBlockStatement(@NotNull BlockStatement node, @NotNull Node block) {
        return new BlockStatement((Block) block);
    }

    @NotNull
    @Override
    public BreakStatement reduceBreakStatement(@NotNull BreakStatement node) {
        return new BreakStatement(node.getLabel());
    }

    @NotNull
    @Override
    public CallExpression reduceCallExpression(@NotNull CallExpression node, @NotNull Node callee, @NotNull ImmutableList<Node> arguments) {
        ImmutableList<SpreadElementExpression> newArguments = arguments.map(x -> (SpreadElementExpression) x);
        return new CallExpression((ExpressionSuper) callee, newArguments);
    }

    @NotNull
    @Override
    public CatchClause reduceCatchClause(@NotNull CatchClause node, @NotNull Node binding, @NotNull Node body) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new CatchClause(loc.just(), (Binding) binding, (Block) body) :
                new CatchClause((Binding) binding, (Block) body);
    }

    @NotNull
    @Override
    public ClassDeclaration reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull Node name, @NotNull Maybe<Node> _super, @NotNull ImmutableList<Node> elements) {
        Maybe<Expression> newSuper = _super.map(x -> (Expression)x);
        ImmutableList<ClassElement> newElements = elements.map(x -> (ClassElement) x);

        return new ClassDeclaration((BindingIdentifier) name, newSuper, newElements);
    }

    @NotNull
    @Override
    public ClassElement reduceClassElement(@NotNull ClassElement node, @NotNull Node method) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new ClassElement(loc.just(), node.isStatic, (MethodDefinition) method) :
                new ClassElement(node.isStatic, (MethodDefinition) method);
    }

    @NotNull
    @Override
    public ClassExpression reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<Node> name, @NotNull Maybe<Node> _super, @NotNull ImmutableList<Node> elements) {
        Maybe<BindingIdentifier> newName = name.map(x -> (BindingIdentifier) x);
        Maybe<Expression> newSuper = _super.map(x -> (Expression) x);
        ImmutableList<ClassElement> newElements = elements.map(x -> (ClassElement) x);

        return new ClassExpression(newName, newSuper, newElements);
    }

    @NotNull
    @Override
    public CompoundAssignmentExpression reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull Node binding, @NotNull Node expression) {
        return new CompoundAssignmentExpression(node.operator, (BindingIdentifierMemberExpression) binding, (Expression) expression);
    }

    @NotNull
    @Override
    public ComputedMemberExpression reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull Node object, @NotNull Node expression) {
        return new ComputedMemberExpression((Expression) object, (ExpressionSuper) expression);
    }

    @NotNull
    @Override
    public ComputedPropertyName reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull Node expression) {
        return new ComputedPropertyName((Expression) expression);
    }

    @NotNull
    @Override
    public ConditionalExpression reduceConditionalExpression(@NotNull ConditionalExpression node, @NotNull Node test, @NotNull Node consequent, @NotNull Node alternate) {
        return new ConditionalExpression((Expression) test, (Expression) consequent, (Expression) alternate);
    }

    @NotNull
    @Override
    public ContinueStatement reduceContinueStatement(@NotNull ContinueStatement node) {
        return new ContinueStatement(node.getLabel());
    }

    @NotNull
    @Override
    public DataProperty reduceDataProperty(@NotNull DataProperty node, @NotNull Node value, @NotNull Node name) {
        return new DataProperty((Expression) value, (PropertyName) name); //TODO: should value be here?
    }

    @NotNull
    @Override
    public DebuggerStatement reduceDebuggerStatement(@NotNull DebuggerStatement node) {
        return new DebuggerStatement(); //TODO: is this correct?
    }

    @NotNull
    @Override
    public Directive reduceDirective(@NotNull Directive node) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new Directive(loc.just(), node.getRawValue()) :
                new Directive(node.getRawValue());
    }

    @NotNull
    @Override
    public DoWhileStatement reduceDoWhileStatement(@NotNull DoWhileStatement node, @NotNull Node test, @NotNull Node body) {
        return new DoWhileStatement((Expression) test, (Statement) body);
    }

    @NotNull
    @Override
    public EmptyStatement reduceEmptyStatement(@NotNull EmptyStatement node) {
        return new EmptyStatement(); //TODO: is this correct?
    }

    @NotNull
    @Override
    public Export reduceExport(@NotNull Export node, @NotNull Node declaration) {
        return new Export((FunctionDeclarationClassDeclarationVariableDeclaration) declaration);
    }

    @NotNull
    @Override
    public ExportAllFrom reduceExportAllFrom(@NotNull ExportAllFrom node) {
        return new ExportAllFrom(node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ExportDefault reduceExportDefault(@NotNull ExportDefault node, @NotNull Node body) {
        return new ExportDefault((FunctionDeclarationClassDeclarationExpression) body);
    }

    @NotNull
    @Override
    public ExportFrom reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<Node> namedExports) {
        ImmutableList<ExportSpecifier> newNamedExports = namedExports.map(x -> (ExportSpecifier) x);
        return new ExportFrom(newNamedExports, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ExportSpecifier reduceExportSpecifier(@NotNull ExportSpecifier node) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new ExportSpecifier(loc.just(), node.getName(), node.getExportedName()) :
                new ExportSpecifier(node.getName(), node.getExportedName());
    }

    @NotNull
    @Override
    public ExpressionStatement reduceExpressionStatement(@NotNull ExpressionStatement node, @NotNull Node expression) {
        return new ExpressionStatement((Expression) expression);
    }

    @NotNull
    @Override
    public ForInStatement reduceForInStatement(@NotNull ForInStatement node, @NotNull Node left, @NotNull Node right, @NotNull Node body) {
        return new ForInStatement((VariableDeclarationBinding) left, (Expression) right, (Statement) body);
    }

    @NotNull
    @Override
    public ForOfStatement reduceForOfStatement(@NotNull ForOfStatement node, @NotNull Node left, @NotNull Node right, @NotNull Node body) {
        return new ForOfStatement((VariableDeclarationBinding) left, (Expression) right, (Statement) body);
    }

    @NotNull
    @Override
    public ForStatement reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<Node> init, @NotNull Maybe<Node> test, @NotNull Maybe<Node> update, @NotNull Node body) {
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

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new FormalParameters(loc.just(), newItems, newRest) : new FormalParameters(newItems, newRest);
    }

    @NotNull
    @Override
    public FunctionBody reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new FunctionBody(loc.just(), newDirectives, newStatements) :
                new FunctionBody(newDirectives, newStatements);
    }

    @NotNull
    @Override
    public FunctionDeclaration reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull Node name, @NotNull Node params, @NotNull Node body) {
        return new FunctionDeclaration((BindingIdentifier) name, node.getIsGenerator(), (FormalParameters) params, (FunctionBody) body);
    }

    @NotNull
    @Override
    public FunctionExpression reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<Node> name, @NotNull Node parameters, @NotNull Node body) {
        Maybe<BindingIdentifier> newName = name.map(x -> (BindingIdentifier) x);

        return new FunctionExpression(newName, node.getIsGenerator(), (FormalParameters) parameters, (FunctionBody) body);
    }

    @NotNull
    @Override
    public Getter reduceGetter(@NotNull Getter node, @NotNull Node body, @NotNull Node name) {
        return new Getter((FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public IdentifierExpression reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        return new IdentifierExpression(node.getName());
    }

    @NotNull
    @Override
    public IfStatement reduceIfStatement(@NotNull IfStatement node, @NotNull Node test, @NotNull Node consequent, @NotNull Maybe<Node> alternate) {
        Maybe<Statement> newAlternate = alternate.map(x -> (Statement) x);

        return new IfStatement((Expression) test, (Statement) consequent, newAlternate);
    }

    @NotNull
    @Override
    public Import reduceImport(@NotNull Import node, @NotNull Maybe<Node> defaultBinding, @NotNull ImmutableList<Node> namedImports) {
        Maybe<BindingIdentifier> newDefaultBinding = defaultBinding.map(x -> (BindingIdentifier) x);
        ImmutableList<ImportSpecifier> newNamedImports = namedImports.map(x -> (ImportSpecifier) x);

        return new Import(newDefaultBinding, newNamedImports, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ImportNamespace reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<Node> defaultBinding, @NotNull Node namespaceBinding) {
        Maybe<BindingIdentifier> newDefaultBinding = defaultBinding.map(x -> (BindingIdentifier) x);

        return new ImportNamespace(newDefaultBinding, (BindingIdentifier) namespaceBinding, node.getModuleSpecifier());
    }

    @NotNull
    @Override
    public ImportSpecifier reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull Node binding) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new ImportSpecifier(loc.just(), node.getName(), (BindingIdentifier) binding) :
                new ImportSpecifier(node.getName(), (BindingIdentifier) binding);
    }

    @NotNull
    @Override
    public LabeledStatement reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull Node body) {
        return new LabeledStatement(node.getLabel(), (Statement) body);
    }

    @NotNull
    @Override
    public LiteralBooleanExpression reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
        return new LiteralBooleanExpression(node.getValue());
    }

    @NotNull
    @Override
    public LiteralInfinityExpression reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
        return new LiteralInfinityExpression(); //TODO: is this correct?
    }

    @NotNull
    @Override
    public LiteralNullExpression reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
        return new LiteralNullExpression(); //TODO: is this correct?
    }

    @NotNull
    @Override
    public LiteralNumericExpression reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
        return new LiteralNumericExpression(node.getValue());
    }

    @NotNull
    @Override
    public LiteralRegExpExpression reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        return new LiteralRegExpExpression(node.getPattern(), node.getFlags());
    }

    @NotNull
    @Override
    public LiteralStringExpression reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
        return new LiteralStringExpression(node.getValue());
    }

    @NotNull
    @Override
    public Method reduceMethod(@NotNull Method node, @NotNull Node params, @NotNull Node body, @NotNull Node name) {
        return new Method(node.getIsGenerator(), (FormalParameters) params, (FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public Module reduceModule(@NotNull Module node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> items) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<ImportDeclarationExportDeclarationStatement> newItems = items.map(x -> (ImportDeclarationExportDeclarationStatement) x);


        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new Module(loc.just(), newDirectives, newItems) : new Module(newDirectives, newItems);
    }

    @NotNull
    @Override
    public NewExpression reduceNewExpression(@NotNull NewExpression node, @NotNull Node callee, @NotNull ImmutableList<Node> arguments) {
        ImmutableList<SpreadElementExpression> newArguments = arguments.map(x -> (SpreadElementExpression) x);

        return new NewExpression((Expression) callee, newArguments);
    }

    @NotNull
    @Override
    public NewTargetExpression reduceNewTargetExpression(@NotNull NewTargetExpression node) {
        return new NewTargetExpression(); //TODO: double-check this!
    }

    @NotNull
    @Override
    public ObjectBinding reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<Node> properties) {
        ImmutableList<BindingProperty> newProperties = properties.map(x -> (BindingProperty) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new ObjectBinding(loc.just(), newProperties) : new ObjectBinding(newProperties);
    }

    @NotNull
    @Override
    public ObjectExpression reduceObjectExpression(@NotNull ObjectExpression node, @NotNull ImmutableList<Node> properties) {
        ImmutableList<ObjectProperty> newProperties = properties.map(x -> (ObjectProperty) x);

        return new ObjectExpression(newProperties);
    }

    @NotNull
    @Override
    public ReturnStatement reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<Node> expression) {
        Maybe<Expression> newExpression = expression.map(x -> (Expression) x);

        return new ReturnStatement(newExpression);
    }

    @NotNull
    @Override
    public Script reduceScript(@NotNull Script node, @NotNull ImmutableList<Node> directives, @NotNull ImmutableList<Node> statements) {
        ImmutableList<Directive> newDirectives = directives.map(x -> (Directive) x);
        ImmutableList<Statement> newStatements = statements.map(x -> (Statement) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new Script(loc.just(), newDirectives, newStatements) : new Script(newDirectives, newStatements);
    }

    @NotNull
    @Override
    public Setter reduceSetter(@NotNull Setter node, @NotNull Node params, @NotNull Node body, @NotNull Node name) {
        return new Setter((BindingBindingWithDefault) params, (FunctionBody) body, (PropertyName) name);
    }

    @NotNull
    @Override
    public ShorthandProperty reduceShorthandProperty(@NotNull ShorthandProperty node) {
        return new ShorthandProperty(node.getName());
    }

    @NotNull
    @Override
    public SpreadElement reduceSpreadElement(@NotNull SpreadElement node, @NotNull Node expression) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new SpreadElement(loc.just(), (Expression) expression) : new SpreadElement((Expression) expression);
    }

    @NotNull
    @Override
    public StaticMemberExpression reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull Node object) {
        return new StaticMemberExpression(node.getProperty(), (ExpressionSuper) object);
    }

    @NotNull
    @Override
    public StaticPropertyName reduceStaticPropertyName(@NotNull StaticPropertyName node) {
        return new StaticPropertyName(node.getValue());
    }

    @NotNull
    @Override
    public Super reduceSuper(@NotNull Super node) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new Super(loc.just()) : new Super();
    }

    @NotNull
    @Override
    public SwitchCase reduceSwitchCase(@NotNull SwitchCase node, @NotNull Node test, @NotNull ImmutableList<Node> consequent) {
        ImmutableList<Statement> newConsequent = consequent.map(x -> (Statement) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new SwitchCase(loc.just(), (Expression) test, newConsequent) :
                new SwitchCase((Expression) test, newConsequent);
    }

    @NotNull
    @Override
    public SwitchDefault reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<Node> consequent) {
        ImmutableList<Statement> newConsequent = consequent.map(x -> (Statement) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new SwitchDefault(loc.just(), newConsequent) : new SwitchDefault(newConsequent);
    }

    @NotNull
    @Override
    public SwitchStatement reduceSwitchStatement(@NotNull SwitchStatement node, @NotNull Node discriminant, @NotNull ImmutableList<Node> cases) {
        ImmutableList<SwitchCase> newCases = cases.map(x -> (SwitchCase) x);

        return new SwitchStatement((Expression) discriminant, newCases);
    }

    @NotNull
    @Override
    public SwitchStatementWithDefault reduceSwitchStatementWithDefault(@NotNull SwitchStatementWithDefault node, @NotNull Node discriminant, @NotNull ImmutableList<Node> preDefaultCases, @NotNull Node defaultCase, @NotNull ImmutableList<Node> postDefaultCases) {
        ImmutableList<SwitchCase> newPreDefaultCases = preDefaultCases.map(x -> (SwitchCase) x);
        ImmutableList<SwitchCase> newPostDefaultCases = postDefaultCases.map(x -> (SwitchCase) x);

        return new SwitchStatementWithDefault((Expression) discriminant, newPreDefaultCases, (SwitchDefault) defaultCase, newPostDefaultCases);
    }

    @NotNull
    @Override
    public TemplateElement reduceTemplateElement(@NotNull TemplateElement node) {
        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ? new TemplateElement(loc.just(), node.getRawValue()) : new TemplateElement(node.getRawValue());
    }

    @NotNull
    @Override
    public TemplateExpression reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<Node> tag, @NotNull ImmutableList<Node> elements) {
        Maybe<Expression> newTag = tag.map(x -> (Expression) x);
        ImmutableList<ExpressionTemplateElement> newElements = elements.map(x -> (ExpressionTemplateElement) x);

        return new TemplateExpression(newTag, newElements);
    }

    @NotNull
    @Override
    public ThisExpression reduceThisExpression(@NotNull ThisExpression node) {
        return new ThisExpression(); //TODO: see if this is correct
    }

    @NotNull
    @Override
    public ThrowStatement reduceThrowStatement(@NotNull ThrowStatement node, @NotNull Node expression) {
        return new ThrowStatement((Expression) expression);
    }

    @NotNull
    @Override
    public TryCatchStatement reduceTryCatchStatement(@NotNull TryCatchStatement node, @NotNull Node block, @NotNull Node catchClause) {
        return new TryCatchStatement((Block) block, (CatchClause) catchClause);
    }

    @NotNull
    @Override
    public TryFinallyStatement reduceTryFinallyStatement(@NotNull TryFinallyStatement node, @NotNull Node block, @NotNull Maybe<Node> catchClause, @NotNull Node finalizer) {
        Maybe<CatchClause> newCatchClause = catchClause.map(x -> (CatchClause) x);

        return new TryFinallyStatement((Block) block, newCatchClause, (Block) finalizer);
    }

    @NotNull
    @Override
    public UnaryExpression reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull Node operand) {
        return new UnaryExpression(node.getOperator(), (Expression) operand);
    }

    @NotNull
    @Override
    public UpdateExpression reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull Node operand) {
        return new UpdateExpression(node.getIsPrefix(), node.getOperator(), (BindingIdentifierMemberExpression) operand);
    }

    @NotNull
    @Override
    public VariableDeclaration reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<Node> declarators) {
        ImmutableList<VariableDeclarator> newDeclarators = declarators.map(x -> (VariableDeclarator) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new VariableDeclaration(loc.just(), node.getKind(), newDeclarators) :
                new VariableDeclaration(node.getKind(), newDeclarators);
    }

    @NotNull
    @Override
    public VariableDeclarationStatement reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull Node declaration) {
        return new VariableDeclarationStatement((VariableDeclaration) declaration);
    }

    @NotNull
    @Override
    public VariableDeclarator reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull Node binding, @NotNull Maybe<Node> init) {
        Maybe<Expression> newInit = init.map(x -> (Expression) x);

        Maybe<SourceSpan> loc = node.getLoc();
        return loc.isJust() ?
                new VariableDeclarator(loc.just(), (Binding) binding, newInit) :
                new VariableDeclarator((Binding) binding, newInit);
    }

    @NotNull
    @Override
    public WhileStatement reduceWhileStatement(@NotNull WhileStatement node, @NotNull Node test, @NotNull Node body) {
        return new WhileStatement((Expression) test, (Statement) body);
    }

    @NotNull
    @Override
    public WithStatement reduceWithStatement(@NotNull WithStatement node, @NotNull Node object, @NotNull Node body) {
        return new WithStatement((Expression) object, (Statement) body);
    }

    @NotNull
    @Override
    public YieldExpression reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<Node> expression) {
        Maybe<Expression> newExpression = expression.map(x -> (Expression) x);

        return new YieldExpression(newExpression);
    }

    @NotNull
    @Override
    public YieldGeneratorExpression reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull Node expression) {
        return new YieldGeneratorExpression((Expression) expression);
    }
}