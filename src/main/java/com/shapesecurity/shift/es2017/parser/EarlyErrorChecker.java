package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.Unit;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.utils.Utils;
import com.shapesecurity.shift.es2017.reducer.MonoidalReducer;

import javax.annotation.Nonnull;

public class EarlyErrorChecker extends MonoidalReducer<EarlyErrorState> {
    public EarlyErrorChecker() {
        super(EarlyErrorState.MONOID);
    }

    public static ImmutableList<EarlyError> extract(EarlyErrorState state) {
        return state.errors;
    }

    public static ImmutableList<EarlyError> validate(Program program) {
        return EarlyErrorChecker.extract(Director.reduceProgram(new EarlyErrorChecker(), program));
    }

    private boolean isStrictFunctionBody(@Nonnull FunctionBody functionBody) {
        return isStrictDirectives(functionBody.directives);
    }

    private boolean isStrictDirectives(@Nonnull ImmutableList<Directive> directives) {
        return directives.exists(d -> d.rawValue.equals("use strict"));
    }

    private boolean containsDuplicates(@Nonnull String arr) { // TODO maybe should go elsewhere
        HashTable<Character, Unit> seen = HashTable.emptyUsingEquality(); // aka set
        for (int i = 0, l = arr.length(); i < l; ++i) {
            if (seen.get(arr.charAt(i)).isJust()) {
                return true;
            }
            seen = seen.put(arr.charAt(i), Unit.unit);
        }
        return false;
    }

    private boolean isLabeledFunction(@Nonnull Node node) {
        if (!(node instanceof LabeledStatement)) {
            return false;
        }
        LabeledStatement labeledStatement = (LabeledStatement) node;
        return labeledStatement.body instanceof FunctionDeclaration || isLabeledFunction(labeledStatement.body);
    }

    private boolean isIterationStatement(@Nonnull Node node) {
        if (node instanceof LabeledStatement) {
            return isIterationStatement(((LabeledStatement) node).body);
        }
        return (node instanceof DoWhileStatement
                || node instanceof ForInStatement
                || node instanceof ForOfStatement
                || node instanceof ForStatement
                || node instanceof WhileStatement);
    }

    private boolean isSpecialMethod(@Nonnull MethodDefinition methodDefinition) {
        if (!(methodDefinition.name instanceof StaticPropertyName) || !((StaticPropertyName) methodDefinition.name).value.equals("constructor")) {
            return false;
        }
        if (methodDefinition instanceof Getter || methodDefinition instanceof Setter) {
            return true;
        }
        return ((Method) methodDefinition).isGenerator || ((Method) methodDefinition).isAsync;
    }

    private boolean isSimpleParameterList(@Nonnull FormalParameters params) {
        return params.rest.isNothing() && !params.items.exists(i -> !(i instanceof BindingIdentifier));
    }

    @Nonnull
    private EarlyErrorState enforceDuplicateConstructorMethods(@Nonnull ImmutableList<ClassElement> elements, @Nonnull EarlyErrorState s) {
        elements = elements.filter(e ->
                        !e.isStatic
                                && e.method instanceof Method
                                && !((Method) e.method).isGenerator
                                && e.method.name instanceof StaticPropertyName
                                && ((StaticPropertyName) e.method.name).value.equals("constructor")
        );

        ImmutableList<EarlyError> errors = elements.length > 1 ? elements.maybeTail().fromJust().map(ErrorMessages.DUPLICATE_CTOR::apply) : ImmutableList.empty();
        return s.addErrors(errors);
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull EarlyErrorState params, @Nonnull EarlyErrorState body) {
        params = params.enforceDuplicateLexicallyDeclaredNames();
        if (node.body instanceof FunctionBody) {
            body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
            if (isStrictFunctionBody((FunctionBody) node.body)) {
                params = params.enforceStrictErrors();
                body = body.enforceStrictErrors();
            }
        }
        body = body.addErrors(body.yieldExpressions.map(ErrorMessages.YIELD_IN_ARROW_BODY));
        params = params.addErrors(params.yieldExpressions.map(ErrorMessages.YIELD_IN_ARROW_PARAMS));
        params = params.addErrors(params.awaitExpressions.map(ErrorMessages.AWAIT_IN_ARROW_PARAMS));

        EarlyErrorState s = super.reduceArrowExpression(node, params, body);
        if (node.body instanceof FunctionBody && !isSimpleParameterList(node.params) && isStrictFunctionBody((FunctionBody) node.body)) {
            s = s.addError(ErrorMessages.COMPLEX_PARAMS_WITH_USE_STRICT.apply(node));
        }
        s = s.clearYieldExpressions();
        s = s.clearAwaitExpressions();
        return s.observeVarBoundary();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceAssignmentExpression(
            @Nonnull AssignmentExpression node,
            @Nonnull EarlyErrorState binding,
            @Nonnull EarlyErrorState expression) {
        return super.reduceAssignmentExpression(node, binding, expression).clearBoundNames();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceAwaitExpression(
            @Nonnull AwaitExpression node,
            @Nonnull EarlyErrorState expression) {
        return expression.observeAwaitExpression(node);
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
        EarlyErrorState s = new EarlyErrorState();
        if (Utils.isRestrictedWord(node.name) || Utils.isStrictModeReservedWord(node.name)) {
            s = s.addStrictError(ErrorMessages.TARGET_IDENTIFIER_STRICT.apply(node));
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
        EarlyErrorState s = new EarlyErrorState();
        if (Utils.isRestrictedWord(node.name) || Utils.isStrictModeReservedWord(node.name)) {
            s = s.addStrictError(ErrorMessages.BINDING_IDENTIFIER_STRICT.apply(node));
        }
        return s.bindName(node);
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<EarlyErrorState> statements) {
        EarlyErrorState s = super.reduceBlock(node, statements);
        s = s.functionDeclarationNamesAreLexical();
        s = s.enforceDuplicateLexicallyDeclaredNames();
        s = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames);
        s = s.observeLexicalBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceBreakStatement(@Nonnull BreakStatement node) {
        EarlyErrorState s = super.reduceBreakStatement(node);
        return node.label.maybe(s.addFreeBreakStatement(node), l -> s.addFreeLabeledBreakStatement(node));
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceCallExpression(
            @Nonnull CallExpression node,
            @Nonnull EarlyErrorState callee,
            @Nonnull ImmutableList<EarlyErrorState> arguments) {
        EarlyErrorState s = super.reduceCallExpression(node, callee, arguments);
        if (node.callee instanceof Super) {
            s = s.observeSuperCallExpression((Super) node.callee);
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceCatchClause(
            @Nonnull CatchClause node,
            @Nonnull EarlyErrorState binding,
            @Nonnull EarlyErrorState body) {
        binding = binding.observeLexicalDeclaration();
        binding = binding.enforceDuplicateLexicallyDeclaredNames();
        final EarlyErrorState finalBinding = binding.enforceConflictingLexicallyDeclaredNames(body.previousLexicallyDeclaredNames);
        ImmutableList<EarlyError> errors = binding.lexicallyDeclaredNames.gatherValues().flatMap(bi ->
                        body.forOfVarDeclaredNames.get(bi.name).map(ErrorMessages.DUPLICATE_BINDING)
        ); // TODO not quite the same as in JS, but should be correct
        EarlyErrorState s = super.reduceCatchClause(node, finalBinding, body);
        s = s.addErrors(errors);
        return s.observeLexicalBoundary();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull EarlyErrorState name, @Nonnull Maybe<EarlyErrorState> _super, @Nonnull ImmutableList<EarlyErrorState> elements) {
        EarlyErrorState s = name.enforceStrictErrors();
        EarlyErrorState sElements = fold(elements).enforceStrictErrors();
        if (node._super.isJust()) {
            s = append(s, _super.fromJust().enforceStrictErrors());
            sElements = sElements.clearSuperCallExpressionsInConstructorMethod(); // todo what
        }
        sElements = sElements.enforceSuperCallExpressions();
        sElements = sElements.enforceSuperPropertyExpressions();
        s = append(s, sElements);
        s = enforceDuplicateConstructorMethods(node.elements, s); // TODO why is this not an EarlyErrorState method
        return s.observeLexicalDeclaration();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceClassElement(@Nonnull ClassElement node, @Nonnull EarlyErrorState method) {
        EarlyErrorState s = super.reduceClassElement(node, method);
        if (!node.isStatic && isSpecialMethod(node.method)) {
            s = s.addError(ErrorMessages.CTOR_SPECIAL.apply(node));
        }
        if (node.isStatic && node.method.name instanceof StaticPropertyName && ((StaticPropertyName) node.method.name).value.equals("prototype")) {
            s = s.addError(ErrorMessages.PROTOTYPE_METHOD.apply(node));
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<EarlyErrorState> name, @Nonnull Maybe<EarlyErrorState> _super, @Nonnull ImmutableList<EarlyErrorState> elements) {
        EarlyErrorState s = name.orJust(new EarlyErrorState()).enforceStrictErrors(); // todo use `identity`?
        EarlyErrorState sElements = fold(elements).enforceStrictErrors();
        if (node._super.isJust()) {
            s = append(s, _super.fromJust().enforceStrictErrors());
            sElements = sElements.clearSuperCallExpressionsInConstructorMethod(); // todo what
        }
        sElements = sElements.enforceSuperCallExpressions();
        sElements = sElements.enforceSuperPropertyExpressions();
        s = append(s, sElements);
        s = enforceDuplicateConstructorMethods(node.elements, s); // TODO why is this not an EarlyErrorState method
        return s.clearBoundNames();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceCompoundAssignmentExpression(@Nonnull CompoundAssignmentExpression node, @Nonnull EarlyErrorState binding, @Nonnull EarlyErrorState expression) {
        return super.reduceCompoundAssignmentExpression(node, binding, expression).clearBoundNames();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceComputedMemberExpression(
            @Nonnull ComputedMemberExpression node,
            @Nonnull EarlyErrorState object,
            @Nonnull EarlyErrorState expression) {
        EarlyErrorState s = super.reduceComputedMemberExpression(node, object, expression);
        if (node.object instanceof Super) {
            s = s.observeSuperPropertyExpression(node);
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceContinueStatement(@Nonnull ContinueStatement node) {
        EarlyErrorState s = super.reduceContinueStatement(node);
        return node.label.maybe(s.addFreeContinueStatement(node), l -> s.addFreeLabeledContinueStatement(node));
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceDoWhileStatement(
            @Nonnull DoWhileStatement node,
            @Nonnull EarlyErrorState body,
            @Nonnull EarlyErrorState test) {
        EarlyErrorState s = super.reduceDoWhileStatement(node, body, test);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.DO_WHILE_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements();
        s = s.clearFreeBreakStatements();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceExport(@Nonnull Export node, @Nonnull EarlyErrorState declaration) {
        EarlyErrorState s = super.reduceExport(node, declaration);
        s = s.functionDeclarationNamesAreLexical();
        s = s.exportDeclaredNames();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceExportDefault(@Nonnull ExportDefault node, @Nonnull EarlyErrorState body) {
        EarlyErrorState s = super.reduceExportDefault(node, body);
        s = s.functionDeclarationNamesAreLexical();
        s = s.exportName("default", node);
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceExportFrom(@Nonnull ExportFrom node, @Nonnull ImmutableList<EarlyErrorState> namedExports) {
        EarlyErrorState s = super.reduceExportFrom(node, namedExports);
        s = s.clearExportedBindings();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceExportFromSpecifier(@Nonnull ExportFromSpecifier node) {
        return super.reduceExportFromSpecifier(node)
                .exportName(node.exportedName.orJust(node.name), node)
                .exportBinding(node.name, node);
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceExportLocalSpecifier(@Nonnull ExportLocalSpecifier node, @Nonnull EarlyErrorState name) {
        return super.reduceExportLocalSpecifier(node, name)
                .exportName(node.exportedName.orJust(node.name.name), node)
                .exportBinding(node.name.name, node);
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceForInStatement(@Nonnull ForInStatement node, @Nonnull EarlyErrorState left, @Nonnull EarlyErrorState right, @Nonnull EarlyErrorState body) {
        left = left.enforceDuplicateLexicallyDeclaredNames();
        left = left.enforceConflictingLexicallyDeclaredNames(body.varDeclaredNames);
        EarlyErrorState s = super.reduceForInStatement(node, left, right, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.FOR_IN_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements();
        s = s.clearFreeBreakStatements();
        s = s.observeLexicalBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull EarlyErrorState left, @Nonnull EarlyErrorState right, @Nonnull EarlyErrorState body) {
        left = left.recordForOfVars();
        left = left.enforceDuplicateLexicallyDeclaredNames();
        left = left.enforceConflictingLexicallyDeclaredNames(body.varDeclaredNames);
        EarlyErrorState s = super.reduceForOfStatement(node, left, right, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.FOR_OF_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements();
        s = s.clearFreeBreakStatements();
        s = s.observeLexicalBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceForStatement(@Nonnull ForStatement node, @Nonnull Maybe<EarlyErrorState> init, @Nonnull Maybe<EarlyErrorState> test, @Nonnull Maybe<EarlyErrorState> update, @Nonnull EarlyErrorState body) {
        init = init.map(i ->
                        i.enforceDuplicateLexicallyDeclaredNames()
                                .enforceConflictingLexicallyDeclaredNames(body.varDeclaredNames)
        );
        EarlyErrorState s = super.reduceForStatement(node, init, test, update, body);
        ImmutableList<EarlyError> constErrors = ImmutableList.empty();
        if (node.init.isJust()) {
            VariableDeclarationExpression i = node.init.fromJust();
            if (i instanceof VariableDeclaration) {
                VariableDeclaration i2 = (VariableDeclaration) i;
                if (i2.kind.equals(VariableDeclarationKind.Const)) {
                    constErrors = i2.declarators
                            .filter(d -> d.init.isNothing())
                            .map(ErrorMessages.CONST_WITHOUT_INIT::apply);
                }
            }
        }
        s = s.addErrors(constErrors);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.FOR_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements();
        s = s.clearFreeBreakStatements();
        s = s.observeLexicalBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceFormalParameters(@Nonnull FormalParameters node, @Nonnull ImmutableList<EarlyErrorState> items, @Nonnull Maybe<EarlyErrorState> rest) {
        return super.reduceFormalParameters(node, items, rest)
                .observeLexicalDeclaration();
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceFunctionBody(
            @Nonnull FunctionBody node,
            @Nonnull ImmutableList<EarlyErrorState> directives,
            @Nonnull ImmutableList<EarlyErrorState> statements) {
        EarlyErrorState s = super.reduceFunctionBody(node, directives, statements);
        s = s.enforceDuplicateLexicallyDeclaredNames();
        s = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames);
        s = s.enforceFreeContinueStatementErrors();
        s = s.enforceFreeLabeledContinueStatementErrors();
        s = s.enforceFreeBreakStatementErrors();
        s = s.enforceFreeLabeledBreakStatementErrors();
        s = s.clearUsedLabelNames();
        s = s.clearYieldExpressions();
        if (isStrictFunctionBody(node)) {
            s = s.enforceStrictErrors();
        }
        return s;
    }

    @Nonnull
    @Override // TODO de-dup code between this and below
    public EarlyErrorState reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull EarlyErrorState name, @Nonnull EarlyErrorState params, @Nonnull EarlyErrorState body) {
        boolean dupParamIsNonstrictError = !isSimpleParameterList(node.params) || node.isGenerator;

        ImmutableList<EarlyError> errors = params.lexicallyDeclaredNames.values().flatMap(nodes ->
                        nodes.length > 1 ?
                                nodes.maybeTail().fromJust().map(ErrorMessages.DUPLICATE_BINDING::apply)
                                : ImmutableList.empty()
        );
        params = dupParamIsNonstrictError ? params.addErrors(errors) : params.addStrictErrors(errors);
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        body = body.enforceSuperCallExpressions();
        body = body.enforceSuperPropertyExpressions();
        params = params.enforceSuperCallExpressions();
        params = params.enforceSuperPropertyExpressions();
        if (node.isGenerator) {
            params = params.addErrors(params.yieldExpressions.map(ErrorMessages.YIELD_IN_GENERATOR_PARAMS));
        }
        if (node.isAsync) {
            params = params.addErrors(params.awaitExpressions.map(ErrorMessages.AWAIT_IN_ASYNC_PARAMS));
        }
        params = params.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            params = params.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceFunctionDeclaration(node, name, params, body);
        if (!isSimpleParameterList(node.params) && isStrictFunctionBody(node.body)) {
            s = s.addError(ErrorMessages.COMPLEX_PARAMS_WITH_USE_STRICT.apply(node));
        }
        s = s.clearYieldExpressions();
        s = s.clearAwaitExpressions();
        s = s.observeFunctionDeclaration();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<EarlyErrorState> name, @Nonnull EarlyErrorState params, @Nonnull EarlyErrorState body) {
        boolean dupParamIsNonstrictError = !isSimpleParameterList(node.params) || node.isGenerator;

        ImmutableList<EarlyError> errors = params.lexicallyDeclaredNames.values().flatMap(nodes ->
                        nodes.length > 1 ?
                                nodes.maybeTail().fromJust().map(ErrorMessages.DUPLICATE_BINDING::apply)
                                : ImmutableList.empty()
        );
        params = dupParamIsNonstrictError ? params.addErrors(errors) : params.addStrictErrors(errors);
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        body = body.enforceSuperCallExpressions();
        body = body.enforceSuperPropertyExpressions();
        params = params.enforceSuperCallExpressions();
        params = params.enforceSuperPropertyExpressions();
        if (node.isGenerator) {
            params = params.addErrors(params.yieldExpressions.map(ErrorMessages.YIELD_IN_GENERATOR_PARAMS));
        }
        if (node.isAsync) {
            params = params.addErrors(params.awaitExpressions.map(ErrorMessages.AWAIT_IN_ASYNC_PARAMS));
        }
        params = params.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            params = params.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceFunctionExpression(node, name, params, body);
        if (!isSimpleParameterList(node.params) && isStrictFunctionBody(node.body)) {
            s = s.addError(ErrorMessages.COMPLEX_PARAMS_WITH_USE_STRICT.apply(node));
        }
        s = s.clearBoundNames();
        s = s.clearYieldExpressions();
        s = s.observeVarBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceGetter(@Nonnull Getter node, @Nonnull EarlyErrorState name, @Nonnull EarlyErrorState body) {
        body = body.enforceSuperCallExpressions();
        body = body.clearSuperPropertyExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceGetter(node, name, body);
        s = s.observeVarBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
        EarlyErrorState s = new EarlyErrorState(); // todo maybe should be `identity`
        if (Utils.isStrictModeReservedWord(node.name)) {
            s = s.addStrictError(ErrorMessages.IDENTIFIER_EXP_STRICT.apply(node));
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceIfStatement(
            @Nonnull IfStatement node,
            @Nonnull EarlyErrorState test,
            @Nonnull EarlyErrorState consequent,
            @Nonnull Maybe<EarlyErrorState> alternate) {
        if (isLabeledFunction(node.consequent)) {
            consequent = consequent.addError(ErrorMessages.CONSEQUENT_IS_LABELED_FN.apply(node.consequent));
        }
        if (node.alternate.isJust() && isLabeledFunction(node.alternate.fromJust())) {
            alternate = alternate.map(t -> t.addError(ErrorMessages.ALTERNATE_IS_LABELED_FN.apply(node.alternate.fromJust())));
        }
        if (node.consequent instanceof FunctionDeclaration) {
            consequent = consequent.addStrictError(ErrorMessages.IF_FNDECL_STRICT.apply(node.consequent));
            consequent = consequent.observeLexicalBoundary();
        }
        if (node.alternate.isJust() && node.alternate.fromJust() instanceof FunctionDeclaration) {
            alternate = alternate.map(t -> t.addStrictError(ErrorMessages.IF_FNDECL_STRICT.apply(node.alternate.fromJust())));
            alternate = alternate.map(EarlyErrorState::observeLexicalBoundary);
        }
        return super.reduceIfStatement(node, test, consequent, alternate);
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceImport(@Nonnull Import node, @Nonnull Maybe<EarlyErrorState> defaultBinding, @Nonnull ImmutableList<EarlyErrorState> namedImports) {
        EarlyErrorState s = super.reduceImport(node, defaultBinding, namedImports);
        s = s.observeLexicalDeclaration();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<EarlyErrorState> defaultBinding, @Nonnull EarlyErrorState namespaceBinding) {
        EarlyErrorState s = super.reduceImportNamespace(node, defaultBinding, namespaceBinding);
        s = s.observeLexicalDeclaration();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceLabeledStatement(@Nonnull LabeledStatement node, @Nonnull EarlyErrorState body) {
        EarlyErrorState s = super.reduceLabeledStatement(node, body);
        if (node.label.equals("yield")) {
            s = s.addStrictError(ErrorMessages.YIELD_LABEL.apply(node));
        }
        if (s.usedLabelNames.get(node.label).isJust()) {
            s = s.addError(ErrorMessages.DUPLICATE_LABEL.apply(node));
        }
        if (node.body instanceof FunctionDeclaration) {
            s = s.addStrictError(ErrorMessages.FN_LABEL_STRICT.apply(node));
        }
        s = isIterationStatement(node.body)
                ? s.observeIterationLabel(node)
                : s.observeNonIterationLabel(node);
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
        EarlyErrorState s = new EarlyErrorState(); // todo `identity`?
        // todo validate pattern
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceMethod(@Nonnull Method node, @Nonnull EarlyErrorState name, @Nonnull EarlyErrorState params, @Nonnull EarlyErrorState body) {
        params = params.enforceDuplicateLexicallyDeclaredNames();
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        if (node.name instanceof StaticPropertyName && ((StaticPropertyName) node.name).value.equals("constructor")) {
            body = body.observeConstructorMethod();
            params = params.observeConstructorMethod();
        } else {
            body = body.enforceSuperCallExpressions();
            params = params.enforceSuperCallExpressions();
        }
        if (node.isGenerator) {
            params = params.addErrors(params.yieldExpressions.map(ErrorMessages.YIELD_IN_GENERATOR_PARAMS));
        }
        if (node.isAsync) {
            params = params.addErrors(params.awaitExpressions.map(ErrorMessages.AWAIT_IN_ASYNC_PARAMS));
        }
        body = body.clearSuperPropertyExpressions();
        params = params.clearSuperPropertyExpressions();
        params = params.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            params = params.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceMethod(node, name, params, body);
        if (!isSimpleParameterList(node.params) && isStrictFunctionBody(node.body)) {
            s = s.addError(ErrorMessages.COMPLEX_PARAMS_WITH_USE_STRICT.apply(node));
        }
        s = s.clearYieldExpressions();
        s = s.clearAwaitExpressions();
        s = s.observeVarBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceModule(@Nonnull Module node, @Nonnull ImmutableList<EarlyErrorState> directives, @Nonnull ImmutableList<EarlyErrorState> items) {
        EarlyErrorState s = super.reduceModule(node, directives, items);
        s = s.functionDeclarationNamesAreLexical();
        s = s.enforceDuplicateLexicallyDeclaredNames();
        EarlyErrorState s2 = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames); // effectively final, for lambdas, because Java is godawful
        ImmutableList<EarlyError> errors = s2.exportedNames.entries().flatMap(p ->
                        p.right().length > 1 ?
                                p.right().maybeTail().fromJust().map(dupeNode -> ErrorMessages.DUPLICATE_EXPORT.apply(dupeNode, p.left()))
                                : ImmutableList.empty()
        );
        errors = errors.append(
                s2.exportedBindings.entries()
                        .filter(p -> !p.left().equals("*default*") && s2.lexicallyDeclaredNames.get(p.left()).isEmpty() && s2.varDeclaredNames.get(p.left()).isEmpty())
                        .flatMap(p -> p.right().map(undeclaredNode -> ErrorMessages.UNDECLARED_EXPORT.apply(undeclaredNode, p.left())))
        );
        errors = errors.append(
                s2.newTargetExpressions.map(ErrorMessages.NEW_TARGET_TOP::apply)
        ); // TODO is there a reason this isn't an EarlyErrorState method?
        s = s2.addErrors(errors);

        s = s.enforceFreeContinueStatementErrors();
        s = s.enforceFreeLabeledContinueStatementErrors();
        s = s.enforceFreeBreakStatementErrors();
        s = s.enforceFreeLabeledBreakStatementErrors();
        s = s.enforceSuperCallExpressions();
        s = s.enforceSuperPropertyExpressions();
        s = s.enforceStrictErrors();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceNewTargetExpression(@Nonnull NewTargetExpression node) {
        return new EarlyErrorState().observeNewTargetExpression(node); // todo `identity`?
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceObjectExpression(
            @Nonnull ObjectExpression node,
            @Nonnull ImmutableList<EarlyErrorState> properties) {
        EarlyErrorState s = super.reduceObjectExpression(node, properties);
        s = s.enforceSuperCallExpressionsInConstructorMethod();
        ImmutableList<ObjectProperty> protos = node.properties.filter(p -> p instanceof DataProperty && ((DataProperty) p).name instanceof StaticPropertyName && ((StaticPropertyName) ((DataProperty) p).name).value.equals("__proto__"));
        s = s.addErrors(
                protos.maybeTail().orJust(ImmutableList.empty()).map(ErrorMessages.DUPLICATE_PROTO::apply)
        );
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceScript(@Nonnull Script node, @Nonnull ImmutableList<EarlyErrorState> directives, @Nonnull ImmutableList<EarlyErrorState> statements) {
        EarlyErrorState s = super.reduceScript(node, directives, statements);
        s = s.enforceDuplicateLexicallyDeclaredNames();
        s = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames);
        s = s.addErrors(s.newTargetExpressions.map(ErrorMessages.NEW_TARGET_TOP::apply));
        s = s.enforceFreeContinueStatementErrors();
        s = s.enforceFreeLabeledContinueStatementErrors();
        s = s.enforceFreeBreakStatementErrors();
        s = s.enforceFreeLabeledBreakStatementErrors();
        s = s.enforceSuperCallExpressions();
        s = s.enforceSuperPropertyExpressions();
        if (isStrictDirectives(node.directives)) {
            s = s.enforceStrictErrors();
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceSetter(
            @Nonnull Setter node,
            @Nonnull EarlyErrorState name,
            @Nonnull EarlyErrorState param,
            @Nonnull EarlyErrorState body) {
        param = param.observeLexicalDeclaration();
        param = param.enforceDuplicateLexicallyDeclaredNames();
        body = body.enforceConflictingLexicallyDeclaredNames(param.lexicallyDeclaredNames);
        param = param.enforceSuperCallExpressions();
        body = body.enforceSuperCallExpressions();
        param = param.clearSuperPropertyExpressions();
        body = body.clearSuperPropertyExpressions();
        param = param.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            param = param.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceSetter(node, name, param, body);
        if (!(node.param instanceof BindingIdentifier) && isStrictFunctionBody(node.body)) {
            s = s.addError(ErrorMessages.COMPLEX_PARAMS_WITH_USE_STRICT.apply(node));
        }
        s = s.observeVarBoundary();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull EarlyErrorState object) {
        EarlyErrorState s = super.reduceStaticMemberExpression(node, object);
        if (node.object instanceof Super) {
            s = s.observeSuperPropertyExpression(node);
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceSwitchStatement(
            @Nonnull SwitchStatement node,
            @Nonnull EarlyErrorState discriminant,
            @Nonnull ImmutableList<EarlyErrorState> cases) {
        EarlyErrorState sCases = this.fold(cases);
        sCases = sCases.functionDeclarationNamesAreLexical();
        sCases = sCases.enforceDuplicateLexicallyDeclaredNames();
        sCases = sCases.enforceConflictingLexicallyDeclaredNames(sCases.varDeclaredNames);
        sCases = sCases.observeLexicalBoundary();
        EarlyErrorState s = this.append(discriminant, sCases);
        s = s.clearFreeBreakStatements();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceSwitchStatementWithDefault(
            @Nonnull SwitchStatementWithDefault node,
            @Nonnull EarlyErrorState discriminant,
            @Nonnull ImmutableList<EarlyErrorState> preDefaultCases,
            @Nonnull EarlyErrorState defaultCase,
            @Nonnull ImmutableList<EarlyErrorState> postDefaultCases) {
        EarlyErrorState sCases = this.append(defaultCase, this.append(this.fold(preDefaultCases), this.fold(postDefaultCases))); // TODO ensure this ordering is permissible
        sCases = sCases.functionDeclarationNamesAreLexical();
        sCases = sCases.enforceDuplicateLexicallyDeclaredNames();
        sCases = sCases.enforceConflictingLexicallyDeclaredNames(sCases.varDeclaredNames);
        sCases = sCases.observeLexicalBoundary();
        EarlyErrorState s = this.append(discriminant, sCases);
        s = s.clearFreeBreakStatements();
        return s;
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceUnaryExpression(@Nonnull UnaryExpression node, @Nonnull EarlyErrorState operand) {
        EarlyErrorState s = super.reduceUnaryExpression(node, operand);
        if (node.operator.equals(UnaryOperator.Delete) && node.operand instanceof IdentifierExpression) {
            s = s.addStrictError(ErrorMessages.DELETE_IDENTIFIER_EXP_STRICT.apply(node));
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceUpdateExpression(@Nonnull UpdateExpression node, @Nonnull EarlyErrorState operand) {
        EarlyErrorState s = super.reduceUpdateExpression(node, operand);
        s = s.clearBoundNames();
        return s;
    }


    @Nonnull
    @Override
    public EarlyErrorState reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull ImmutableList<EarlyErrorState> declarators) {
        EarlyErrorState s = super.reduceVariableDeclaration(node, declarators);
        switch (node.kind) {
            case Const:
            case Let:
                s = s.observeLexicalDeclaration();
                s = s.addErrors(
                        s.lexicallyDeclaredNames.get("let")
                                .map(ErrorMessages.LEXICAL_LET_BINDING::apply)
                );
                break;
            case Var:
                s = s.observeVarDeclaration();
                break;
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceVariableDeclarationStatement(
            @Nonnull VariableDeclarationStatement node,
            @Nonnull EarlyErrorState declaration) {
        EarlyErrorState s = super.reduceVariableDeclarationStatement(node, declaration);
        if (node.declaration.kind.equals(VariableDeclarationKind.Const)) {
            s = s.addErrors(
                    node.declaration.declarators.filter(d -> d.init.isNothing())
                            .map(ErrorMessages.CONST_WITHOUT_INIT::apply)
            );
        }
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceWhileStatement(
            @Nonnull WhileStatement node,
            @Nonnull EarlyErrorState test,
            @Nonnull EarlyErrorState body) {
        EarlyErrorState s = super.reduceWhileStatement(node, test, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.WHILE_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements().clearFreeBreakStatements();
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceWithStatement(
            @Nonnull WithStatement node,
            @Nonnull EarlyErrorState object,
            @Nonnull EarlyErrorState body) {
        EarlyErrorState s = super.reduceWithStatement(node, object, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.WITH_LABELED_FN.apply(node.body));
        }
        s = s.addStrictError(ErrorMessages.WITH_STRICT.apply(node));
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceYieldExpression(@Nonnull YieldExpression node, @Nonnull Maybe<EarlyErrorState> expression) {
        EarlyErrorState s = super.reduceYieldExpression(node, expression);
        s = s.observeYieldExpression(node);
        return s;
    }

    @Nonnull
    @Override
    public EarlyErrorState reduceYieldGeneratorExpression(@Nonnull YieldGeneratorExpression node, @Nonnull EarlyErrorState expression) {
        EarlyErrorState s = super.reduceYieldGeneratorExpression(node, expression);
        s = s.observeYieldExpression(node);
        return s;
    }

}
