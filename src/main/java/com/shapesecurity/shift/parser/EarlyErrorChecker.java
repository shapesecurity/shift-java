package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.Unit;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.ArrowExpression;
import com.shapesecurity.shift.ast.AssignmentExpression;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.BindingIdentifierMemberExpression;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.BreakStatement;
import com.shapesecurity.shift.ast.CallExpression;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.ClassDeclaration;
import com.shapesecurity.shift.ast.ClassElement;
import com.shapesecurity.shift.ast.ClassExpression;
import com.shapesecurity.shift.ast.CompoundAssignmentExpression;
import com.shapesecurity.shift.ast.ComputedMemberExpression;
import com.shapesecurity.shift.ast.ContinueStatement;
import com.shapesecurity.shift.ast.DataProperty;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.DoWhileStatement;
import com.shapesecurity.shift.ast.Export;
import com.shapesecurity.shift.ast.ExportDefault;
import com.shapesecurity.shift.ast.ExportFrom;
import com.shapesecurity.shift.ast.ExportSpecifier;
import com.shapesecurity.shift.ast.ForInStatement;
import com.shapesecurity.shift.ast.ForOfStatement;
import com.shapesecurity.shift.ast.ForStatement;
import com.shapesecurity.shift.ast.FormalParameters;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.FunctionDeclaration;
import com.shapesecurity.shift.ast.FunctionExpression;
import com.shapesecurity.shift.ast.Getter;
import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.IfStatement;
import com.shapesecurity.shift.ast.Import;
import com.shapesecurity.shift.ast.ImportNamespace;
import com.shapesecurity.shift.ast.LabeledStatement;
import com.shapesecurity.shift.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.ast.Method;
import com.shapesecurity.shift.ast.MethodDefinition;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.NewTargetExpression;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.ObjectExpression;
import com.shapesecurity.shift.ast.ObjectProperty;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Setter;
import com.shapesecurity.shift.ast.StaticMemberExpression;
import com.shapesecurity.shift.ast.StaticPropertyName;
import com.shapesecurity.shift.ast.Super;
import com.shapesecurity.shift.ast.SwitchStatement;
import com.shapesecurity.shift.ast.SwitchStatementWithDefault;
import com.shapesecurity.shift.ast.UnaryExpression;
import com.shapesecurity.shift.ast.UpdateExpression;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarationExpression;
import com.shapesecurity.shift.ast.VariableDeclarationKind;
import com.shapesecurity.shift.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.ast.WhileStatement;
import com.shapesecurity.shift.ast.WithStatement;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EarlyErrorChecker extends MonoidalReducer<EarlyErrorState> {
    public EarlyErrorChecker() {
        super(EarlyErrorState.MONOID);
    }

    public static ImmutableList<EarlyError> extract(EarlyErrorState state) {
        return state.errors;
    }

    public static ImmutableList<EarlyError> validate(Script script) {
        return EarlyErrorChecker.extract(Director.reduceScript(new EarlyErrorChecker(), script));
    }

    public static ImmutableList<EarlyError> validate(Module module) {
        return EarlyErrorChecker.extract(Director.reduceModule(new EarlyErrorChecker(), module));
    }

    private boolean isStrictFunctionBody(@NotNull FunctionBody functionBody) {
        return isStrictDirectives(functionBody.directives);
    }

    private boolean isStrictDirectives(@NotNull ImmutableList<Directive> directives) {
        return directives.exists(d -> d.rawValue.equals("use strict"));
    }

    private boolean containsDuplicates(@NotNull String arr) { // TODO maybe should go elsewhere
        HashTable<Character, Unit> seen = HashTable.empty(); // aka set
        for (int i = 0, l = arr.length(); i < l; ++i) {
            if (seen.get(arr.charAt(i)).isJust()) {
                return true;
            }
            seen = seen.put(arr.charAt(i), Unit.unit);
        }
        return false;
    }

    private boolean isLabeledFunction(@NotNull Node node) {
        if (!(node instanceof LabeledStatement)) {
            return false;
        }
        LabeledStatement labeledStatement = (LabeledStatement) node;
        return labeledStatement.body instanceof FunctionDeclaration || isLabeledFunction(labeledStatement.body);
    }

    private boolean isIterationStatement(@NotNull Node node) {
        if (node instanceof LabeledStatement) {
            return isIterationStatement(((LabeledStatement) node).body);
        }
        return (node instanceof DoWhileStatement
                || node instanceof ForInStatement
                || node instanceof ForOfStatement
                || node instanceof ForStatement
                || node instanceof WhileStatement);
    }

    private boolean isSpecialMethod(@NotNull MethodDefinition methodDefinition) {
        if (!(methodDefinition.name instanceof StaticPropertyName) || !((StaticPropertyName) methodDefinition.name).value.equals("constructor")) {
            return false;
        }
        if (methodDefinition instanceof Getter || methodDefinition instanceof Setter) {
            return true;
        }
        return ((Method) methodDefinition).isGenerator;
    }

    @NotNull
    private EarlyErrorState enforceDuplicateConstructorMethods(@NotNull ImmutableList<ClassElement> elements, @NotNull EarlyErrorState s) {
        elements = elements.filter(e ->
                        !e.isStatic
                                && e.method instanceof Method
                                && !((Method) e.method).isGenerator
                                && e.method.name instanceof StaticPropertyName
                                && ((StaticPropertyName) e.method.name).value.equals("constructor")
        );

        ImmutableList<EarlyError> errors = elements.length > 1 ? elements.maybeTail().just().map(ErrorMessages.DUPLICATE_CTOR::apply) : ImmutableList.nil();
        return s.addErrors(errors);
    }


    @NotNull
    @Override
    public EarlyErrorState reduceArrowExpression(@NotNull ArrowExpression node, @NotNull EarlyErrorState params, @NotNull EarlyErrorState body) {
        params = params.enforceDuplicateLexicallyDeclaredNames();
        if (node.body instanceof FunctionBody) {
            body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
            if (isStrictFunctionBody((FunctionBody) node.body)) {
                params = params.enforceStrictErrors();
                body = body.enforceStrictErrors();
            }
        }

        EarlyErrorState s = super.reduceArrowExpression(node, params, body);
        return s.observeVarBoundary();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceAssignmentExpression(
            @NotNull AssignmentExpression node,
            @NotNull EarlyErrorState binding,
            @NotNull EarlyErrorState expression) {
        return super.reduceAssignmentExpression(node, binding, expression).clearBoundNames();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        EarlyErrorState s = new EarlyErrorState();
        if (Utils.isRestrictedWord(node.name) || Utils.isStrictModeReservedWord(node.name)) {
            s = s.addStrictError(ErrorMessages.BINDING_IDENTIFIER_STRICT.apply(node));
        }
        return s.bindName(node);
    }


    @NotNull
    @Override
    public EarlyErrorState reduceBlock(@NotNull Block node, @NotNull ImmutableList<EarlyErrorState> statements) {
        EarlyErrorState s = super.reduceBlock(node, statements);
        s = s.functionDeclarationNamesAreLexical();
        s = s.enforceDuplicateLexicallyDeclaredNames();
        s = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames);
        s = s.observeLexicalBoundary();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceBreakStatement(@NotNull BreakStatement node) {
        EarlyErrorState s = super.reduceBreakStatement(node);
        return node.label.maybe(s.addFreeBreakStatement(node), l -> s.addFreeLabeledBreakStatement(node));
    }

    @NotNull
    @Override
    public EarlyErrorState reduceCallExpression(
            @NotNull CallExpression node,
            @NotNull EarlyErrorState callee,
            @NotNull ImmutableList<EarlyErrorState> arguments) {
        EarlyErrorState s = super.reduceCallExpression(node, callee, arguments);
        if (node.callee instanceof Super) {
            s = s.observeSuperCallExpression((Super) node.callee);
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceCatchClause(
            @NotNull CatchClause node,
            @NotNull EarlyErrorState binding,
            @NotNull EarlyErrorState body) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull EarlyErrorState name, @NotNull Maybe<EarlyErrorState> _super, @NotNull ImmutableList<EarlyErrorState> elements) {
        EarlyErrorState s = name;
        EarlyErrorState sElements = fold(elements).enforceStrictErrors();
        if (node._super.isJust()) {
            s = append(s, _super.just().enforceStrictErrors());
            sElements = sElements.clearSuperCallExpressionsInConstructorMethod(); // todo what
        }
        sElements = sElements.enforceSuperCallExpressions();
        sElements = sElements.enforceSuperPropertyExpressions();
        s = append(s, sElements);
        s = enforceDuplicateConstructorMethods(node.elements, s); // TODO why is this not an EarlyErrorState method
        return s.observeLexicalDeclaration();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceClassElement(@NotNull ClassElement node, @NotNull EarlyErrorState method) {
        EarlyErrorState s = super.reduceClassElement(node, method);
        if (!node.isStatic && isSpecialMethod(node.method)) {
            s = s.addError(ErrorMessages.CTOR_SPECIAL.apply(node));
        }
        if (node.isStatic && node.method.name instanceof StaticPropertyName && ((StaticPropertyName) node.method.name).value.equals("prototype")) {
            s = s.addError(ErrorMessages.PROTOTYPE_METHOD.apply(node));
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<EarlyErrorState> name, @NotNull Maybe<EarlyErrorState> _super, @NotNull ImmutableList<EarlyErrorState> elements) {
        EarlyErrorState s = name.orJust(new EarlyErrorState()); // todo use `identity`?
        EarlyErrorState sElements = fold(elements).enforceStrictErrors();
        if (node._super.isJust()) {
            s = append(s, _super.just().enforceStrictErrors());
            sElements = sElements.clearSuperCallExpressionsInConstructorMethod(); // todo what
        }
        sElements = sElements.enforceSuperCallExpressions();
        sElements = sElements.enforceSuperPropertyExpressions();
        s = append(s, sElements);
        s = enforceDuplicateConstructorMethods(node.elements, s); // TODO why is this not an EarlyErrorState method
        return s.clearBoundNames();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull EarlyErrorState binding, @NotNull EarlyErrorState expression) {
        return super.reduceCompoundAssignmentExpression(node, binding, expression).clearBoundNames();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceComputedMemberExpression(
            @NotNull ComputedMemberExpression node,
            @NotNull EarlyErrorState expression,
            @NotNull EarlyErrorState object) {
        EarlyErrorState s = super.reduceComputedMemberExpression(node, expression, object);
        if (node._object instanceof Super) {
            s = s.observeSuperPropertyExpression(node);
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceContinueStatement(@NotNull ContinueStatement node) {
        EarlyErrorState s = super.reduceContinueStatement(node);
        return node.label.maybe(s.addFreeContinueStatement(node), l -> s.addFreeLabeledContinueStatement(node));
    }


    @NotNull
    @Override
    public EarlyErrorState reduceDoWhileStatement(
            @NotNull DoWhileStatement node,
            @NotNull EarlyErrorState body,
            @NotNull EarlyErrorState test) {
        EarlyErrorState s = super.reduceDoWhileStatement(node, body, test);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.DO_WHILE_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements();
        s = s.clearFreeBreakStatements();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceExport(@NotNull Export node, @NotNull EarlyErrorState declaration) {
        EarlyErrorState s = super.reduceExport(node, declaration);
        s = s.functionDeclarationNamesAreLexical();
        s = s.exportDeclaredNames();
        return s;
    }

    // TODO no exportAllFrom?

    @NotNull
    @Override
    public EarlyErrorState reduceExportDefault(@NotNull ExportDefault node, @NotNull EarlyErrorState body) {
        EarlyErrorState s = super.reduceExportDefault(node, body);
        s = s.functionDeclarationNamesAreLexical();
        if ((node.body instanceof FunctionDeclaration && !((FunctionDeclaration) node.body).name.name.equals("*default*"))
                || (node.body instanceof ClassDeclaration && !((ClassDeclaration) node.body).name.name.equals("*default*"))) {
            s = s.exportDeclaredNames();
        }
        s = s.exportName("*default*", node);
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<EarlyErrorState> namedExports) {
        EarlyErrorState s = super.reduceExportFrom(node, namedExports);
        if (node.moduleSpecifier.isJust()) {
            s = s.clearExportedBindings();
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceExportSpecifier(@NotNull ExportSpecifier node) {
        return super.reduceExportSpecifier(node)
                .exportName(node.exportedName, node)
                .exportBinding(node.name.orJust(node.exportedName), node);
    }

    @NotNull
    @Override
    public EarlyErrorState reduceForInStatement(@NotNull ForInStatement node, @NotNull EarlyErrorState left, @NotNull EarlyErrorState right, @NotNull EarlyErrorState body) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceForOfStatement(@NotNull ForOfStatement node, @NotNull EarlyErrorState left, @NotNull EarlyErrorState right, @NotNull EarlyErrorState body) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<EarlyErrorState> init, @NotNull Maybe<EarlyErrorState> test, @NotNull Maybe<EarlyErrorState> update, @NotNull EarlyErrorState body) {
        init = init.map(i ->
                        i.enforceDuplicateLexicallyDeclaredNames()
                                .enforceConflictingLexicallyDeclaredNames(body.varDeclaredNames)
        );
        EarlyErrorState s = super.reduceForStatement(node, init, test, update, body);
        ImmutableList<EarlyError> constErrors = ImmutableList.nil();
        if (node.init.isJust()) {
            VariableDeclarationExpression i = node.init.just();
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

    @NotNull
    @Override
    public EarlyErrorState reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<EarlyErrorState> items, @NotNull Maybe<EarlyErrorState> rest) {
        return super.reduceFormalParameters(node, items, rest)
                .observeLexicalDeclaration();
    }

    @NotNull
    @Override
    public EarlyErrorState reduceFunctionBody(
            @NotNull FunctionBody node,
            @NotNull ImmutableList<EarlyErrorState> directives,
            @NotNull ImmutableList<EarlyErrorState> statements) {
        EarlyErrorState s = super.reduceFunctionBody(node, directives, statements);
        s = s.enforceDuplicateLexicallyDeclaredNames();
        s = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames);
        s = s.enforceFreeContinueStatementErrors();
        s = s.enforceFreeLabeledContinueStatementErrors();
        s = s.enforceFreeBreakStatementErrors();
        s = s.enforceFreeLabeledBreakStatementErrors();
        s = s.clearUsedLabelNames();
        if (isStrictFunctionBody(node)) {
            s = s.enforceStrictErrors();
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull EarlyErrorState name, @NotNull EarlyErrorState params, @NotNull EarlyErrorState body) {
        boolean isSimpleParameterList = node.params.rest.isNothing() && !node.params.items.exists(i -> !(i instanceof BindingIdentifier));
        boolean dupParamIsNonstrictError = !isSimpleParameterList || node.isGenerator;

        ImmutableList<EarlyError> errors = params.lexicallyDeclaredNames.values().flatMap(nodes ->
                        nodes.length > 1 ?
                                nodes.maybeTail().just().map(ErrorMessages.DUPLICATE_BINDING::apply)
                                : ImmutableList.nil()
        );
        params = dupParamIsNonstrictError ? params.addErrors(errors) : params.addStrictErrors(errors);
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        body = body.enforceSuperCallExpressions();
        body = body.enforceSuperPropertyExpressions();
        params = params.enforceSuperCallExpressions();
        params = params.enforceSuperPropertyExpressions();
        params = params.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            params = params.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceFunctionDeclaration(node, name, params, body);
        s = s.observeFunctionDeclaration();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<EarlyErrorState> name, @NotNull EarlyErrorState params, @NotNull EarlyErrorState body) {
        boolean isSimpleParameterList = node.params.rest.isNothing() && !node.params.items.exists(i -> !(i instanceof BindingIdentifier));
        boolean dupParamIsNonstrictError = !isSimpleParameterList || node.isGenerator;

        ImmutableList<EarlyError> errors = params.lexicallyDeclaredNames.values().flatMap(nodes ->
                        nodes.length > 1 ?
                                nodes.maybeTail().just().map(ErrorMessages.DUPLICATE_BINDING::apply)
                                : ImmutableList.nil()
        );
        params = dupParamIsNonstrictError ? params.addErrors(errors) : params.addStrictErrors(errors);
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        body = body.enforceSuperCallExpressions();
        body = body.enforceSuperPropertyExpressions();
        params = params.enforceSuperCallExpressions();
        params = params.enforceSuperPropertyExpressions();
        params = params.clearNewTargetExpressions();
        body = body.clearNewTargetExpressions();
        if (isStrictFunctionBody(node.body)) {
            params = params.enforceStrictErrors();
            body = body.enforceStrictErrors();
        }
        EarlyErrorState s = super.reduceFunctionExpression(node, name, params, body);
        s = s.clearBoundNames();
        s = s.observeVarBoundary();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceGetter(@NotNull Getter node, @NotNull EarlyErrorState body, @NotNull EarlyErrorState name) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        EarlyErrorState s = new EarlyErrorState(); // todo maybe should be `identity`
        if (Utils.isStrictModeReservedWord(node.name)) {
            s = s.addStrictError(ErrorMessages.IDENTIFIER_EXP_STRICT.apply(node));
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceIfStatement(
            @NotNull IfStatement node,
            @NotNull EarlyErrorState test,
            @NotNull EarlyErrorState consequent,
            @NotNull Maybe<EarlyErrorState> alternate) {
        if (isLabeledFunction(node.consequent)) {
            consequent = consequent.addError(ErrorMessages.CONSEQUENT_IS_LABELED_FN.apply(node.consequent));
        }
        if (node.alternate.isJust() && isLabeledFunction(node.alternate.just())) {
            alternate = alternate.map(t -> t.addError(ErrorMessages.ALTERNATE_IS_LABELED_FN.apply(node.alternate.just())));
        }
        if (node.consequent instanceof FunctionDeclaration) {
            consequent = consequent.addStrictError(ErrorMessages.IF_FNDECL_STRICT.apply(node.consequent));
            consequent = consequent.observeLexicalBoundary();
        }
        if (node.alternate.isJust() && node.alternate.just() instanceof FunctionDeclaration) {
            alternate = alternate.map(t -> t.addStrictError(ErrorMessages.IF_FNDECL_STRICT.apply(node.alternate.just())));
            alternate = alternate.map(EarlyErrorState::observeLexicalBoundary);
        }
        return super.reduceIfStatement(node, test, consequent, alternate);
    }

    @NotNull
    @Override
    public EarlyErrorState reduceImport(@NotNull Import node, @NotNull Maybe<EarlyErrorState> defaultBinding, @NotNull ImmutableList<EarlyErrorState> namedImports) {
        EarlyErrorState s = super.reduceImport(node, defaultBinding, namedImports);
        s = s.observeLexicalDeclaration();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<EarlyErrorState> defaultBinding, @NotNull EarlyErrorState namespaceBinding) {
        EarlyErrorState s = super.reduceImportNamespace(node, defaultBinding, namespaceBinding);
        s = s.observeLexicalDeclaration();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull EarlyErrorState body) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        EarlyErrorState s = new EarlyErrorState(); // todo `identity`?
        if (!node.flags.matches("[^igmyu]*") || containsDuplicates(node.flags)) {
            s = s.addError(ErrorMessages.INVALID_REGEX_FLAG_MACHINE.apply(node));
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceMethod(@NotNull Method node, @NotNull EarlyErrorState params, @NotNull EarlyErrorState body, @NotNull EarlyErrorState name) {
        params = params.enforceDuplicateLexicallyDeclaredNames();
        body = body.enforceConflictingLexicallyDeclaredNames(params.lexicallyDeclaredNames);
        if (node.name instanceof StaticPropertyName && ((StaticPropertyName) node.name).value.equals("constructor")) {
            body = body.observeConstructorMethod();
            params = params.observeConstructorMethod();
        } else {
            body = body.enforceSuperCallExpressions();
            params = params.enforceSuperCallExpressions();
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
        s = s.observeVarBoundary();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceModule(@NotNull Module node, @NotNull ImmutableList<EarlyErrorState> directives, @NotNull ImmutableList<EarlyErrorState> items) {
        EarlyErrorState s = super.reduceModule(node, directives, items);
        s = s.functionDeclarationNamesAreLexical();
        s = s.enforceDuplicateLexicallyDeclaredNames();
        EarlyErrorState s2 = s.enforceConflictingLexicallyDeclaredNames(s.varDeclaredNames); // effectively final, for lambdas, because Java is godawful
        ImmutableList<EarlyError> errors = s2.exportedNames.entries().flatMap(p ->
                        p.b.length > 1 ?
                                p.b.maybeTail().just().map(dupeNode -> ErrorMessages.DUPLICATE_EXPORT.apply(dupeNode, p.a))
                                : ImmutableList.nil()
        );
        errors = errors.append(
                s2.exportedBindings.entries()
                        .filter(p -> !p.a.equals("*default*") && s2.lexicallyDeclaredNames.get(p.a).isEmpty() && s2.varDeclaredNames.get(p.a).isEmpty())
                        .flatMap(p -> p.b.map(undeclaredNode -> ErrorMessages.UNDECLARED_EXPORT.apply(undeclaredNode, p.a)))
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

    @NotNull
    @Override
    public EarlyErrorState reduceNewTargetExpression(@NotNull NewTargetExpression node) {
        return new EarlyErrorState().observeNewTargetExpression(node); // todo `identity`?
    }


    @NotNull
    @Override
    public EarlyErrorState reduceObjectExpression(
            @NotNull ObjectExpression node,
            @NotNull ImmutableList<EarlyErrorState> properties) {
        EarlyErrorState s = super.reduceObjectExpression(node, properties);
        s = s.enforceSuperCallExpressionsInConstructorMethod();
        ImmutableList<ObjectProperty> protos = node.properties.filter(p -> p instanceof DataProperty && ((DataProperty) p).name instanceof StaticPropertyName && ((StaticPropertyName) ((DataProperty) p).name).value.equals("__proto__"));
        s = s.addErrors(
                protos.maybeTail().orJust(ImmutableList.nil()).map(ErrorMessages.DUPLICATE_PROTO::apply)
        );
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceScript(@NotNull Script node, @NotNull ImmutableList<EarlyErrorState> directives, @NotNull ImmutableList<EarlyErrorState> statements) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceSetter(
            @NotNull Setter node,
            @NotNull EarlyErrorState param,
            @NotNull EarlyErrorState body,
            @NotNull EarlyErrorState name) {
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
        s = s.observeVarBoundary();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull EarlyErrorState object) {
        EarlyErrorState s = super.reduceStaticMemberExpression(node, object);
        if (node._object instanceof Super) {
            s = s.observeSuperPropertyExpression(node);
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceSwitchStatement(
            @NotNull SwitchStatement node,
            @NotNull EarlyErrorState discriminant,
            @NotNull ImmutableList<EarlyErrorState> cases) {
        EarlyErrorState sCases = this.fold(cases);
        sCases = sCases.functionDeclarationNamesAreLexical();
        sCases = sCases.enforceDuplicateLexicallyDeclaredNames();
        sCases = sCases.enforceConflictingLexicallyDeclaredNames(sCases.varDeclaredNames);
        sCases = sCases.observeLexicalBoundary();
        EarlyErrorState s = this.append(discriminant, sCases);
        s = s.clearFreeBreakStatements();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceSwitchStatementWithDefault(
            @NotNull SwitchStatementWithDefault node,
            @NotNull EarlyErrorState discriminant,
            @NotNull ImmutableList<EarlyErrorState> preDefaultCases,
            @NotNull EarlyErrorState defaultCase,
            @NotNull ImmutableList<EarlyErrorState> postDefaultCases) {
        EarlyErrorState sCases = this.append(defaultCase, this.append(this.fold(preDefaultCases), this.fold(postDefaultCases))); // TODO ensure this ordering is permissible
        sCases = sCases.functionDeclarationNamesAreLexical();
        sCases = sCases.enforceDuplicateLexicallyDeclaredNames();
        sCases = sCases.enforceConflictingLexicallyDeclaredNames(sCases.varDeclaredNames);
        sCases = sCases.observeLexicalBoundary();
        EarlyErrorState s = this.append(discriminant, sCases);
        s = s.clearFreeBreakStatements();
        return s;
    }


    @NotNull
    @Override
    public EarlyErrorState reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull EarlyErrorState operand) {
        EarlyErrorState s = super.reduceUnaryExpression(node, operand);
        if (node.operator.equals(UnaryOperator.Delete) && node.operand instanceof IdentifierExpression) {
            s = s.addStrictError(ErrorMessages.DELETE_IDENTIFIER_EXP_STRICT.apply(node));
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull EarlyErrorState operand) {
        EarlyErrorState s = super.reduceUpdateExpression(node, operand);
        s = s.clearBoundNames();
        return s;
    }


    @NotNull
    @Override
    public EarlyErrorState reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<EarlyErrorState> declarators) {
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

    @NotNull
    @Override
    public EarlyErrorState reduceVariableDeclarationStatement(
            @NotNull VariableDeclarationStatement node,
            @NotNull EarlyErrorState declaration) {
        EarlyErrorState s = super.reduceVariableDeclarationStatement(node, declaration);
        if (node.declaration.kind.equals(VariableDeclarationKind.Const)) {
            s = s.addErrors(
                    node.declaration.declarators.filter(d -> d.init.isNothing())
                            .map(ErrorMessages.CONST_WITHOUT_INIT::apply)
            );
        }
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceWhileStatement(
            @NotNull WhileStatement node,
            @NotNull EarlyErrorState test,
            @NotNull EarlyErrorState body) {
        EarlyErrorState s = super.reduceWhileStatement(node, test, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.WHILE_LABELED_FN.apply(node.body));
        }
        s = s.clearFreeContinueStatements().clearFreeBreakStatements();
        return s;
    }

    @NotNull
    @Override
    public EarlyErrorState reduceWithStatement(
            @NotNull WithStatement node,
            @NotNull EarlyErrorState object,
            @NotNull EarlyErrorState body) {
        EarlyErrorState s = super.reduceWithStatement(node, object, body);
        if (isLabeledFunction(node.body)) {
            s = s.addError(ErrorMessages.WITH_LABELED_FN.apply(node.body));
        }
        s = s.addStrictError(ErrorMessages.WITH_STRICT.apply(node));
        return s;
    }
}
