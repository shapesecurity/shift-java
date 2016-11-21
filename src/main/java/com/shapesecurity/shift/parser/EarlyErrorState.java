package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.functional.data.MultiHashTable;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.BreakStatement;
import com.shapesecurity.shift.ast.ContinueStatement;
import com.shapesecurity.shift.ast.LabeledStatement;
import com.shapesecurity.shift.ast.MemberExpression;
import com.shapesecurity.shift.ast.NewTargetExpression;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Super;
import com.shapesecurity.shift.ast.YieldExpression;

import org.jetbrains.annotations.NotNull;

public class EarlyErrorState {

    public static final Monoid<EarlyErrorState> MONOID = new EarlyErrorContextMonoid();

    // Fully saturated constructor.
    public EarlyErrorState(
            @NotNull ImmutableList<EarlyError> errors,
            @NotNull ImmutableList<EarlyError> strictErrors,
            @NotNull HashTable<String, LabeledStatement> usedLabelNames,
            @NotNull ImmutableList<BreakStatement> freeBreakStatements,
            @NotNull ImmutableList<ContinueStatement> freeContinueStatements,
            @NotNull MultiHashTable<String, BreakStatement> freeLabeledBreakStatements,
            @NotNull MultiHashTable<String, ContinueStatement> freeLabeledContinueStatements,
            @NotNull ImmutableList<NewTargetExpression> newTargetExpressions,
            @NotNull MultiHashTable<String, BindingIdentifier> boundNames,
            @NotNull MultiHashTable<String, BindingIdentifier> previousLexicallyDeclaredNames,
            @NotNull MultiHashTable<String, BindingIdentifier> lexicallyDeclaredNames,
            @NotNull MultiHashTable<String, BindingIdentifier> functionDeclarationNames,
            @NotNull MultiHashTable<String, BindingIdentifier> varDeclaredNames,
            @NotNull MultiHashTable<String, BindingIdentifier> forOfVarDeclaredNames,
            @NotNull MultiHashTable<String, Node> exportedNames,
            @NotNull MultiHashTable<String, Node> exportedBindings,
            @NotNull ImmutableList<Super> superCallExpressions,
            @NotNull ImmutableList<Super> superCallExpressionsInConstructorMethod,
            @NotNull ImmutableList<MemberExpression> superPropertyExpressions,
            @NotNull ImmutableList<Node> yieldExpressions) {
        this.errors = errors;
        this.strictErrors = strictErrors;
        this.usedLabelNames = usedLabelNames;
        this.freeBreakStatements = freeBreakStatements;
        this.freeContinueStatements = freeContinueStatements;
        this.freeLabeledBreakStatements = freeLabeledBreakStatements;
        this.freeLabeledContinueStatements = freeLabeledContinueStatements;
        this.newTargetExpressions = newTargetExpressions;
        this.boundNames = boundNames;
        this.previousLexicallyDeclaredNames = previousLexicallyDeclaredNames;
        this.lexicallyDeclaredNames = lexicallyDeclaredNames;
        this.functionDeclarationNames = functionDeclarationNames;
        this.varDeclaredNames = varDeclaredNames;
        this.forOfVarDeclaredNames = forOfVarDeclaredNames;
        this.exportedNames = exportedNames;
        this.exportedBindings = exportedBindings;
        this.superCallExpressions = superCallExpressions;
        this.superCallExpressionsInConstructorMethod = superCallExpressionsInConstructorMethod;
        this.superPropertyExpressions = superPropertyExpressions;
        this.yieldExpressions = yieldExpressions;
    }

    // Identity
    public EarlyErrorState() {
        this.errors = ImmutableList.empty();
        this.strictErrors = ImmutableList.empty();
        this.usedLabelNames = HashTable.empty();
        this.freeBreakStatements = ImmutableList.empty();
        this.freeContinueStatements = ImmutableList.empty();
        this.freeLabeledBreakStatements = MultiHashTable.empty();
        this.freeLabeledContinueStatements = MultiHashTable.empty();
        this.newTargetExpressions = ImmutableList.empty();
        this.boundNames = MultiHashTable.empty();
        this.previousLexicallyDeclaredNames = MultiHashTable.empty();
        this.lexicallyDeclaredNames = MultiHashTable.empty();
        this.functionDeclarationNames = MultiHashTable.empty();
        this.varDeclaredNames = MultiHashTable.empty();
        this.forOfVarDeclaredNames = MultiHashTable.empty();
        this.exportedNames = MultiHashTable.empty();
        this.exportedBindings = MultiHashTable.empty();
        this.superCallExpressions = ImmutableList.empty();
        this.superCallExpressionsInConstructorMethod = ImmutableList.empty();
        this.superPropertyExpressions = ImmutableList.empty();
        this.yieldExpressions = ImmutableList.empty();
    }

    // Append
    public EarlyErrorState(@NotNull EarlyErrorState a, @NotNull EarlyErrorState b) {
        this.errors = a.errors.append(b.errors);
        this.strictErrors = a.strictErrors.append(b.strictErrors);
        this.usedLabelNames = a.usedLabelNames.merge(b.usedLabelNames);
        this.freeBreakStatements = a.freeBreakStatements.append(b.freeBreakStatements);
        this.freeContinueStatements = a.freeContinueStatements.append(b.freeContinueStatements);
        this.freeLabeledBreakStatements = a.freeLabeledBreakStatements.merge(b.freeLabeledBreakStatements);
        this.freeLabeledContinueStatements = a.freeLabeledContinueStatements.merge(b.freeLabeledContinueStatements);
        this.newTargetExpressions = a.newTargetExpressions.append(b.newTargetExpressions);
        this.boundNames = a.boundNames.merge(b.boundNames);
        this.previousLexicallyDeclaredNames = a.previousLexicallyDeclaredNames.merge(b.previousLexicallyDeclaredNames);
        this.lexicallyDeclaredNames = a.lexicallyDeclaredNames.merge(b.lexicallyDeclaredNames);
        this.functionDeclarationNames = a.functionDeclarationNames.merge(b.functionDeclarationNames);
        this.varDeclaredNames = a.varDeclaredNames.merge(b.varDeclaredNames);
        this.forOfVarDeclaredNames = a.forOfVarDeclaredNames.merge(b.forOfVarDeclaredNames);
        this.exportedNames = a.exportedNames.merge(b.exportedNames);
        this.exportedBindings = a.exportedBindings.merge(b.exportedBindings);
        this.superCallExpressions = a.superCallExpressions.append(b.superCallExpressions);
        this.superCallExpressionsInConstructorMethod = a.superCallExpressionsInConstructorMethod.append(b.superCallExpressionsInConstructorMethod);
        this.superPropertyExpressions = a.superPropertyExpressions.append(b.superPropertyExpressions);
        this.yieldExpressions = a.yieldExpressions.append(b.yieldExpressions);
    }


    @NotNull
    public final ImmutableList<EarlyError> errors;
    // errors that are only errors in strict mode code
    @NotNull
    public final ImmutableList<EarlyError> strictErrors;

    // Label values used in LabeledStatement nodes; cleared at function boundaries
    @NotNull
    public final HashTable<String, LabeledStatement> usedLabelNames; // TODO maybe just set of strings?


    // BreakStatement nodes; cleared at iteration, switch, and function boundaries
    @NotNull
    public final ImmutableList<BreakStatement> freeBreakStatements;
    // ContinueStatement nodes; cleared at iteration boundaries
    @NotNull
    public final ImmutableList<ContinueStatement> freeContinueStatements;

    // labeled BreakStatement nodes; cleared at LabeledStatement with same Label and function boundaries
    @NotNull
    public final MultiHashTable<String, BreakStatement> freeLabeledBreakStatements;
    // labeled ContinueStatement nodes; cleared at labeled iteration statement with same Label and function boundaries
    @NotNull
    public final MultiHashTable<String, ContinueStatement> freeLabeledContinueStatements;

    // NewTargetExpression nodes; cleared at function (besides arrow expression) boundaries
    @NotNull
    public final ImmutableList<NewTargetExpression> newTargetExpressions;

    // BindingIdentifier nodes; cleared at containing declaration node
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> boundNames;
    // BindingIdentifiers that were found to be in a lexical binding position
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> previousLexicallyDeclaredNames;
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> lexicallyDeclaredNames;
    // BindingIdentifiers that were the name of a FunctionDeclaration
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> functionDeclarationNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> varDeclaredNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @NotNull
    public final MultiHashTable<String, BindingIdentifier> forOfVarDeclaredNames;

    // Names that this module exports
    @NotNull
    public final MultiHashTable<String, Node> exportedNames;
    // Locally declared names that are referenced in export declarations
    @NotNull
    public final MultiHashTable<String, Node> exportedBindings;

    // CallExpressions with Super callee
    @NotNull
    public final ImmutableList<Super> superCallExpressions;
    // SuperCall expressions in the context of a Method named "constructor"
    @NotNull
    public final ImmutableList<Super> superCallExpressionsInConstructorMethod;
    // MemberExpressions with Super object
    @NotNull
    public final ImmutableList<MemberExpression> superPropertyExpressions;
    // YieldExpressions which may be within parameters / concise arrow bodies
    @NotNull
    public final ImmutableList<Node> yieldExpressions;


    @NotNull
    public EarlyErrorState addFreeBreakStatement(@NotNull BreakStatement breakStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements.cons(breakStatement),
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addFreeLabeledBreakStatement(@NotNull BreakStatement breakStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements.put(breakStatement.label.fromJust(), breakStatement),
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearFreeBreakStatements() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                ImmutableList.empty(),
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addFreeContinueStatement(@NotNull ContinueStatement continueStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements.cons(continueStatement),
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addFreeLabeledContinueStatement(@NotNull ContinueStatement continueStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements.put(continueStatement.label.fromJust(), continueStatement),
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearFreeContinueStatements() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                ImmutableList.empty(),
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeBreakStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeBreakStatements.map(ErrorMessages.FREE_BREAK)),
                this.strictErrors,
                this.usedLabelNames,
                ImmutableList.empty(),
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeLabeledBreakStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledBreakStatements.gatherValues().map(ErrorMessages.UNBOUND_BREAK)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                MultiHashTable.empty(),
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeContinueStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeContinueStatements.map(ErrorMessages.FREE_CONTINUE)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                ImmutableList.empty(),
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeLabeledContinueStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledContinueStatements.gatherValues().map(ErrorMessages.UNBOUND_CONTINUE)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                MultiHashTable.empty(),
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeIterationLabel(@NotNull LabeledStatement labeledStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames.put(labeledStatement.label, labeledStatement),
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements.remove(labeledStatement.label),
                this.freeLabeledContinueStatements.remove(labeledStatement.label),
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeNonIterationLabel(@NotNull LabeledStatement labeledStatement) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames.put(labeledStatement.label, labeledStatement),
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements.remove(labeledStatement.label),
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearUsedLabelNames() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                HashTable.empty(),
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeSuperCallExpression(@NotNull Super node) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions.cons(node),
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeConstructorMethod() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                ImmutableList.empty(),
                this.superCallExpressions,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearSuperCallExpressionsInConstructorMethod() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                ImmutableList.empty(),
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperCallExpressions() {
        return new EarlyErrorState(
                this.errors.append(this.superCallExpressions.map(ErrorMessages.SUPERCALL_ERROR)).append(this.superCallExpressionsInConstructorMethod.map(ErrorMessages.SUPERCALL_ERROR)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                ImmutableList.empty(),
                ImmutableList.empty(),
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperCallExpressionsInConstructorMethod() {
        return new EarlyErrorState(
                this.errors.append(this.superCallExpressionsInConstructorMethod.map(ErrorMessages.SUPERCALL_ERROR)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                ImmutableList.empty(),
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeSuperPropertyExpression(@NotNull MemberExpression memberExpression) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions.cons(memberExpression),
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearSuperPropertyExpressions() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                ImmutableList.empty(),
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperPropertyExpressions() {
        return new EarlyErrorState(
                this.errors.append(this.superPropertyExpressions.map(ErrorMessages.SUPERPROPERTY_ERROR)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                ImmutableList.empty(),
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeNewTargetExpression(@NotNull NewTargetExpression newTargetExpression) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions.cons(newTargetExpression),
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearNewTargetExpressions() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                ImmutableList.empty(),
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState bindName(@NotNull BindingIdentifier bindingIdentifier) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames.put(bindingIdentifier.name, bindingIdentifier),
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearBoundNames() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                MultiHashTable.empty(),
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeLexicalDeclaration() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                MultiHashTable.empty(),
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames.merge(this.boundNames),
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeLexicalBoundary() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.lexicallyDeclaredNames,
                MultiHashTable.empty(),
                MultiHashTable.empty(),
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceDuplicateLexicallyDeclaredNames() {
        HashTable<String, ImmutableList<EarlyError>> errorMap = this.lexicallyDeclaredNames.toHashTable(
                l -> ((l.length > 1) ? l.maybeTail().fromJust().map(ErrorMessages.DUPLICATE_BINDING) : ImmutableList.empty())
        );
        ImmutableList<EarlyError> dupErrors = errorMap.entries().flatMap(p -> p.right()); // apparently this is too much for the type inference to do in one step
        return new EarlyErrorState(
                this.errors.append(dupErrors),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames, // TODO ensure this shouldn't be cleared per JS early error checker
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceConflictingLexicallyDeclaredNames(@NotNull MultiHashTable<String, BindingIdentifier> otherNames) {
        HashTable<String, ImmutableList<EarlyError>> errorMap = this.lexicallyDeclaredNames.toHashTable(
                (k, vs) -> (otherNames.get(k).isNotEmpty() ? vs.map(ErrorMessages.DUPLICATE_BINDING) : ImmutableList.empty())
        );
        ImmutableList<EarlyError> dupErrors = errorMap.entries().flatMap(p -> p.right()); // apparently this is too much for the type inference to do in one step
        return new EarlyErrorState(
                this.errors.append(dupErrors),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames, // TODO ensure this shouldn't be cleared per JS early error checker
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeFunctionDeclaration() {
        EarlyErrorState res = this.observeVarBoundary();
        MultiHashTable<String, BindingIdentifier> newFnDeclaredNames = res.functionDeclarationNames.merge(res.boundNames);
        return new EarlyErrorState(
                res.errors,
                res.strictErrors,
                res.usedLabelNames,
                res.freeBreakStatements,
                res.freeContinueStatements,
                res.freeLabeledBreakStatements,
                res.freeLabeledContinueStatements,
                res.newTargetExpressions,
                MultiHashTable.empty(),
                res.previousLexicallyDeclaredNames,
                res.lexicallyDeclaredNames,
                newFnDeclaredNames,
                res.varDeclaredNames,
                res.forOfVarDeclaredNames,
                res.exportedNames,
                res.exportedBindings,
                res.superCallExpressions,
                res.superCallExpressionsInConstructorMethod,
                res.superPropertyExpressions,
                res.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState functionDeclarationNamesAreLexical() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames.merge(this.functionDeclarationNames),
                MultiHashTable.empty(),
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeVarDeclaration() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                MultiHashTable.empty(),
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames.merge(this.boundNames),
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState recordForOfVars() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames.merge(this.varDeclaredNames),
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeVarBoundary() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                MultiHashTable.empty(),
                MultiHashTable.empty(),
                MultiHashTable.empty(),
                MultiHashTable.empty(),
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState exportName(@NotNull String name, @NotNull Node node) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames.put(name, node),
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState exportDeclaredNames() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames.merge(this.lexicallyDeclaredNames.mapValues(bi -> (Node) bi)).merge(this.varDeclaredNames.mapValues(bi -> (Node) bi)),
                this.exportedBindings.merge(this.lexicallyDeclaredNames.mapValues(bi -> (Node) bi)).merge(this.varDeclaredNames.mapValues(bi -> (Node) bi)),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState exportBinding(@NotNull String name, @NotNull Node node) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings.put(name, node),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearExportedBindings() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                MultiHashTable.empty(),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeYieldExpression(@NotNull Node yieldExpression) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions.cons(yieldExpression)
        );
    }

    @NotNull
    public EarlyErrorState clearYieldExpressions() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                ImmutableList.empty()
        );
    }

    @NotNull
    public EarlyErrorState addError(@NotNull EarlyError error) {
        return new EarlyErrorState(
                this.errors.cons(error),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addErrors(@NotNull ImmutableList<EarlyError> errors) {
        return new EarlyErrorState(
                this.errors.append(errors),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addStrictError(@NotNull EarlyError error) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors.cons(error),
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState addStrictErrors(@NotNull ImmutableList<EarlyError> errors) {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors.append(errors),
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceStrictErrors() {
        return new EarlyErrorState(
                this.errors.append(this.strictErrors),
                ImmutableList.empty(),
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.functionDeclarationNames,
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions
        );
    }

    private static final class EarlyErrorContextMonoid implements Monoid<EarlyErrorState> {
        @NotNull
        @Override
        public EarlyErrorState identity() {
            return new EarlyErrorState();
        }

        @NotNull
        @Override
        public EarlyErrorState append(EarlyErrorState a, EarlyErrorState b) {
            return new EarlyErrorState(a, b);
        }
    }
}
