package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.functional.data.MultiHashTable;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.ContinueStatement;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.MemberExpression;
import com.shapesecurity.shift.es2017.ast.NewTargetExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Super;

import javax.annotation.Nonnull;

public class EarlyErrorState {

    public static final Monoid<EarlyErrorState> MONOID = new EarlyErrorContextMonoid();

    // Fully saturated constructor.
    public EarlyErrorState(
            @Nonnull ImmutableList<EarlyError> errors,
            @Nonnull ImmutableList<EarlyError> strictErrors,
            @Nonnull HashTable<String, LabeledStatement> usedLabelNames,
            @Nonnull ImmutableList<BreakStatement> freeBreakStatements,
            @Nonnull ImmutableList<ContinueStatement> freeContinueStatements,
            @Nonnull MultiHashTable<String, BreakStatement> freeLabeledBreakStatements,
            @Nonnull MultiHashTable<String, ContinueStatement> freeLabeledContinueStatements,
            @Nonnull ImmutableList<NewTargetExpression> newTargetExpressions,
            @Nonnull MultiHashTable<String, BindingIdentifier> boundNames,
            @Nonnull MultiHashTable<String, BindingIdentifier> previousLexicallyDeclaredNames,
            @Nonnull MultiHashTable<String, BindingIdentifier> lexicallyDeclaredNames,
            @Nonnull MultiHashTable<String, BindingIdentifier> functionDeclarationNames,
            @Nonnull MultiHashTable<String, BindingIdentifier> varDeclaredNames,
            @Nonnull MultiHashTable<String, BindingIdentifier> forOfVarDeclaredNames,
            @Nonnull MultiHashTable<String, Node> exportedNames,
            @Nonnull MultiHashTable<String, Node> exportedBindings,
            @Nonnull ImmutableList<Super> superCallExpressions,
            @Nonnull ImmutableList<Super> superCallExpressionsInConstructorMethod,
            @Nonnull ImmutableList<MemberExpression> superPropertyExpressions,
            @Nonnull ImmutableList<Node> yieldExpressions,
            @Nonnull ImmutableList<Node> awaitExpressions) {
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
        this.awaitExpressions = awaitExpressions;
    }

    // Identity
    public EarlyErrorState() {
        this.errors = ImmutableList.empty();
        this.strictErrors = ImmutableList.empty();
        this.usedLabelNames = HashTable.emptyUsingEquality();
        this.freeBreakStatements = ImmutableList.empty();
        this.freeContinueStatements = ImmutableList.empty();
        this.freeLabeledBreakStatements = MultiHashTable.emptyUsingEquality();
        this.freeLabeledContinueStatements = MultiHashTable.emptyUsingEquality();
        this.newTargetExpressions = ImmutableList.empty();
        this.boundNames = MultiHashTable.emptyUsingEquality();
        this.previousLexicallyDeclaredNames = MultiHashTable.emptyUsingEquality();
        this.lexicallyDeclaredNames = MultiHashTable.emptyUsingEquality();
        this.functionDeclarationNames = MultiHashTable.emptyUsingEquality();
        this.varDeclaredNames = MultiHashTable.emptyUsingEquality();
        this.forOfVarDeclaredNames = MultiHashTable.emptyUsingEquality();
        this.exportedNames = MultiHashTable.emptyUsingEquality();
        this.exportedBindings = MultiHashTable.emptyUsingEquality();
        this.superCallExpressions = ImmutableList.empty();
        this.superCallExpressionsInConstructorMethod = ImmutableList.empty();
        this.superPropertyExpressions = ImmutableList.empty();
        this.yieldExpressions = ImmutableList.empty();
        this.awaitExpressions = ImmutableList.empty();
    }

    // Append
    public EarlyErrorState(@Nonnull EarlyErrorState a, @Nonnull EarlyErrorState b) {
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
        this.awaitExpressions = a.awaitExpressions.append(b.awaitExpressions);
    }


    @Nonnull
    public final ImmutableList<EarlyError> errors;
    // errors that are only errors in strict mode code
    @Nonnull
    public final ImmutableList<EarlyError> strictErrors;

    // Label values used in LabeledStatement nodes; cleared at function boundaries
    @Nonnull
    public final HashTable<String, LabeledStatement> usedLabelNames; // TODO maybe just set of strings?


    // BreakStatement nodes; cleared at iteration, switch, and function boundaries
    @Nonnull
    public final ImmutableList<BreakStatement> freeBreakStatements;
    // ContinueStatement nodes; cleared at iteration boundaries
    @Nonnull
    public final ImmutableList<ContinueStatement> freeContinueStatements;

    // labeled BreakStatement nodes; cleared at LabeledStatement with same Label and function boundaries
    @Nonnull
    public final MultiHashTable<String, BreakStatement> freeLabeledBreakStatements;
    // labeled ContinueStatement nodes; cleared at labeled iteration statement with same Label and function boundaries
    @Nonnull
    public final MultiHashTable<String, ContinueStatement> freeLabeledContinueStatements;

    // NewTargetExpression nodes; cleared at function (besides arrow expression) boundaries
    @Nonnull
    public final ImmutableList<NewTargetExpression> newTargetExpressions;

    // BindingIdentifier nodes; cleared at containing declaration node
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> boundNames;
    // BindingIdentifiers that were found to be in a lexical binding position
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> previousLexicallyDeclaredNames;
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> lexicallyDeclaredNames;
    // BindingIdentifiers that were the name of a FunctionDeclaration
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> functionDeclarationNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> varDeclaredNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @Nonnull
    public final MultiHashTable<String, BindingIdentifier> forOfVarDeclaredNames;

    // Names that this module exports
    @Nonnull
    public final MultiHashTable<String, Node> exportedNames;
    // Locally declared names that are referenced in export declarations
    @Nonnull
    public final MultiHashTable<String, Node> exportedBindings;

    // CallExpressions with Super callee
    @Nonnull
    public final ImmutableList<Super> superCallExpressions;
    // SuperCall expressions in the context of a Method named "constructor"
    @Nonnull
    public final ImmutableList<Super> superCallExpressionsInConstructorMethod;
    // MemberExpressions with Super object
    @Nonnull
    public final ImmutableList<MemberExpression> superPropertyExpressions;
    // YieldExpressions which may be within parameters / concise arrow bodies
    @Nonnull
    public final ImmutableList<Node> yieldExpressions;
    // AwaitExpressions which may be outside generator functions
    @Nonnull
    public final ImmutableList<Node> awaitExpressions;


    @Nonnull
    public EarlyErrorState addFreeBreakStatement(@Nonnull BreakStatement breakStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addFreeLabeledBreakStatement(@Nonnull BreakStatement breakStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addFreeContinueStatement(@Nonnull ContinueStatement continueStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addFreeLabeledContinueStatement(@Nonnull ContinueStatement continueStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState enforceFreeLabeledBreakStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledBreakStatements.gatherValues().map(ErrorMessages.UNBOUND_BREAK)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                MultiHashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState enforceFreeLabeledContinueStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledContinueStatements.gatherValues().map(ErrorMessages.UNBOUND_CONTINUE)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                MultiHashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeIterationLabel(@Nonnull LabeledStatement labeledStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeNonIterationLabel(@Nonnull LabeledStatement labeledStatement) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState clearUsedLabelNames() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                HashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeSuperCallExpression(@Nonnull Super node) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeSuperPropertyExpression(@Nonnull MemberExpression memberExpression) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeNewTargetExpression(@Nonnull NewTargetExpression newTargetExpression) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState bindName(@Nonnull BindingIdentifier bindingIdentifier) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
                MultiHashTable.emptyUsingEquality(),
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState enforceConflictingLexicallyDeclaredNames(@Nonnull MultiHashTable<String, BindingIdentifier> otherNames) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
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
                res.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
                this.varDeclaredNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
                MultiHashTable.emptyUsingEquality(),
                MultiHashTable.emptyUsingEquality(),
                MultiHashTable.emptyUsingEquality(),
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState exportName(@Nonnull String name, @Nonnull Node node) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState exportBinding(@Nonnull String name, @Nonnull Node node) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                MultiHashTable.emptyUsingEquality(),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions,
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeYieldExpression(@Nonnull Node yieldExpression) {
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
                this.yieldExpressions.cons(yieldExpression),
                this.awaitExpressions
        );
    }

    @Nonnull
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
                ImmutableList.empty(),
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState observeAwaitExpression(@Nonnull Node awaitExpression) {
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
                this.yieldExpressions,
                this.awaitExpressions.cons(awaitExpression)
        );
    }

    @Nonnull
    public EarlyErrorState clearAwaitExpressions() {
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
                this.yieldExpressions,
                ImmutableList.empty()
        );
    }

    @Nonnull
    public EarlyErrorState addError(@Nonnull EarlyError error) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addErrors(@Nonnull ImmutableList<EarlyError> errors) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addStrictError(@Nonnull EarlyError error) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
    public EarlyErrorState addStrictErrors(@Nonnull ImmutableList<EarlyError> errors) {
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    @Nonnull
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
                this.yieldExpressions,
                this.awaitExpressions
        );
    }

    private static final class EarlyErrorContextMonoid implements Monoid<EarlyErrorState> {
        @Nonnull
        @Override
        public EarlyErrorState identity() {
            return new EarlyErrorState();
        }

        @Nonnull
        @Override
        public EarlyErrorState append(EarlyErrorState a, EarlyErrorState b) {
            return new EarlyErrorState(a, b);
        }
    }
}
