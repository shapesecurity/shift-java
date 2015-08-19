package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.BreakStatement;
import com.shapesecurity.shift.ast.ContinueStatement;
import com.shapesecurity.shift.ast.LabeledStatement;
import com.shapesecurity.shift.ast.MemberExpression;
import com.shapesecurity.shift.ast.NewTargetExpression;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Super;
import com.shapesecurity.shift.codegen.CodeGen;

import org.jetbrains.annotations.NotNull;

public class EarlyErrorState {
    public static final F<Super, EarlyError> SUPERCALL_ERROR = node -> new EarlyError(node, "Calls to super must be in the \"constructor\" method of a class expression or class declaration that has a superclass");
    public static final F<MemberExpression, EarlyError> SUPERPROPERTY_ERROR = node -> new EarlyError(node, "Member access on super must be in a method");
    public static final F<BindingIdentifier, EarlyError> DUPLICATE_BINDING = node -> new EarlyError(node, "Duplicate binding " + CodeGen.escapeStringLiteral(node.name));
    public static final F<ContinueStatement, EarlyError> FREE_CONTINUE = node -> new EarlyError(node, "Continue statement must be nested within an iteration statement");
    public static final F<ContinueStatement, EarlyError> UNBOUND_CONTINUE = node -> new EarlyError(node, "Continue statement must be nested within an iteration statement with label " + CodeGen.escapeStringLiteral(node.label.just()));
    public static final F<BreakStatement, EarlyError> FREE_BREAK = node -> new EarlyError(node, "Break statement must be nested within an iteration statement or a switch statement");
    public static final F<BreakStatement, EarlyError> UNBOUND_BREAK = node -> new EarlyError(node, "Break statement must be nested within a statement with label " + CodeGen.escapeStringLiteral(node.label.just()));

    public static final Monoid<EarlyErrorState> MONOID = new EarlyErrorContextMonoid();

    // Fully saturated constructor.
    public EarlyErrorState(@NotNull ImmutableList<EarlyError> errors, @NotNull ImmutableList<EarlyError> strictErrors, @NotNull HashTable<String, LabeledStatement> usedLabelNames, @NotNull ImmutableList<BreakStatement> freeBreakStatements, @NotNull ImmutableList<ContinueStatement> freeContinueStatements, @NotNull MultiHashTable<String, BreakStatement> freeLabeledBreakStatements, @NotNull MultiHashTable<String, ContinueStatement> freeLabeledContinueStatements, @NotNull ImmutableList<NewTargetExpression> newTargetExpressions, @NotNull MultiHashTable<String, BindingIdentifier> boundNames, @NotNull MultiHashTable<String, BindingIdentifier> previousLexicallyDeclaredNames, @NotNull MultiHashTable<String, BindingIdentifier> lexicallyDeclaredNames, @NotNull MultiHashTable<String, BindingIdentifier> functionDeclarationNames, @NotNull MultiHashTable<String, BindingIdentifier> varDeclaredNames, @NotNull MultiHashTable<String, BindingIdentifier> forOfVarDeclaredNames, @NotNull MultiHashTable<String, Node> exportedNames, @NotNull MultiHashTable<String, Node> exportedBindings, @NotNull ImmutableList<Super> superCallExpressions, @NotNull ImmutableList<Super> superCallExpressionsInConstructorMethod, @NotNull ImmutableList<MemberExpression> superPropertyExpressions) {
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
    }

    // Identity
    public EarlyErrorState() {
        this.errors = ImmutableList.nil();
        this.strictErrors = ImmutableList.nil();
        this.usedLabelNames = HashTable.empty();
        this.freeBreakStatements = ImmutableList.nil();
        this.freeContinueStatements = ImmutableList.nil();
        this.freeLabeledBreakStatements = MultiHashTable.empty();
        this.freeLabeledContinueStatements = MultiHashTable.empty();
        this.newTargetExpressions = ImmutableList.nil();
        this.boundNames = MultiHashTable.empty();
        this.previousLexicallyDeclaredNames = MultiHashTable.empty();
        this.lexicallyDeclaredNames = MultiHashTable.empty();
        this.varDeclaredNames = MultiHashTable.empty();
        this.functionDeclarationNames = MultiHashTable.empty();
        this.forOfVarDeclaredNames = MultiHashTable.empty();
        this.exportedNames = MultiHashTable.empty();
        this.exportedBindings = MultiHashTable.empty();
        this.superCallExpressions = ImmutableList.nil();
        this.superCallExpressionsInConstructorMethod = ImmutableList.nil();
        this.superPropertyExpressions = ImmutableList.nil();
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
        this.varDeclaredNames = a.varDeclaredNames.merge(b.varDeclaredNames);
        this.functionDeclarationNames = a.functionDeclarationNames.merge(b.functionDeclarationNames);
        this.forOfVarDeclaredNames = a.forOfVarDeclaredNames.merge(b.forOfVarDeclaredNames);
        this.exportedNames = a.exportedNames.merge(b.exportedNames);
        this.exportedBindings = a.exportedBindings.merge(b.exportedBindings);
        this.superCallExpressions = a.superCallExpressions.append(b.superCallExpressions);
        this.superCallExpressionsInConstructorMethod = a.superCallExpressionsInConstructorMethod.append(b.superCallExpressionsInConstructorMethod);
        this.superPropertyExpressions = a.superPropertyExpressions.append(b.superPropertyExpressions);
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.freeLabeledBreakStatements.put(breakStatement.label.just(), breakStatement),
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearFreeBreakStatements() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                ImmutableList.nil(),
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.freeLabeledContinueStatements.put(continueStatement.label.just(), continueStatement),
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState clearFreeContinueStatements() {
        return new EarlyErrorState(
                this.errors,
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                ImmutableList.nil(),
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeBreakStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeBreakStatements.map(FREE_BREAK)),
                this.strictErrors,
                this.usedLabelNames,
                ImmutableList.nil(),
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeLabeledBreakStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledBreakStatements.gatherValues().map(UNBOUND_BREAK)),
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeContinueStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeContinueStatements.map(FREE_CONTINUE)),
                this.strictErrors,
                this.usedLabelNames,
                this.freeBreakStatements,
                ImmutableList.nil(),
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceFreeLabeledContinueStatementErrors() {
        return new EarlyErrorState(
                this.errors.append(this.freeLabeledContinueStatements.gatherValues().map(UNBOUND_CONTINUE)),
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions.cons(node),
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                ImmutableList.nil(),
                this.superCallExpressions,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                ImmutableList.nil(),
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperCallExpressions() {
        return new EarlyErrorState(
                this.errors.append(this.superCallExpressions.map(SUPERCALL_ERROR)).append(this.superCallExpressionsInConstructorMethod.map(SUPERCALL_ERROR)),
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                ImmutableList.nil(),
                ImmutableList.nil(),
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperCallExpressionsInConstructorMethod() {
        return new EarlyErrorState(
                this.errors.append(this.superCallExpressionsInConstructorMethod.map(SUPERCALL_ERROR)),
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                ImmutableList.nil(),
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions.cons(memberExpression)
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                ImmutableList.nil()
        );
    }

    @NotNull
    public EarlyErrorState enforceSuperPropertyExpressions() {
        return new EarlyErrorState(
                this.errors.append(this.superPropertyExpressions.map(SUPERPROPERTY_ERROR)),
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                ImmutableList.nil()
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                ImmutableList.nil(),
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                MultiHashTable.empty(),
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceDuplicateLexicallyDeclaredNames() {
        HashTable<String, ImmutableList<EarlyError>> errorMap = this.lexicallyDeclaredNames.toHashTable(
                l -> ((l.length > 1) ? l.maybeTail().just().map(DUPLICATE_BINDING) : ImmutableList.nil())
        );
        ImmutableList<EarlyError> dupErrors = errorMap.entries().flatMap(p -> p.b); // apparently this is too much for the type inference to do in one step
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceConflictingLexicallyDeclaredNames(@NotNull MultiHashTable<String, BindingIdentifier> otherNames) {
        HashTable<String, ImmutableList<EarlyError>> errorMap = this.lexicallyDeclaredNames.toHashTable(
                (k, vs) -> (otherNames.get(k).isNotEmpty() ? vs.map(DUPLICATE_BINDING) : ImmutableList.nil())
        );
        ImmutableList<EarlyError> dupErrors = errorMap.entries().flatMap(p -> p.b); // apparently this is too much for the type inference to do in one step
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState observeFunctionDeclaration() {
        EarlyErrorState res = this.observeVarBoundary();
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
                res.varDeclaredNames,
                res.functionDeclarationNames.merge(res.boundNames),
                res.forOfVarDeclaredNames,
                res.exportedNames,
                res.exportedBindings,
                res.superCallExpressions,
                res.superCallExpressionsInConstructorMethod,
                res.superPropertyExpressions
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
                this.varDeclaredNames,
                MultiHashTable.empty(),
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames.merge(this.boundNames),
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames.merge(this.varDeclaredNames),
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames.put(name, node),
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames.merge(this.lexicallyDeclaredNames.mapValues(bi -> (Node) bi)).merge(this.varDeclaredNames.mapValues(bi -> (Node) bi)),
                this.exportedBindings.merge(this.lexicallyDeclaredNames.mapValues(bi -> (Node) bi)).merge(this.varDeclaredNames.mapValues(bi -> (Node) bi)),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings.put(name, node),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                MultiHashTable.empty(),
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
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
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }

    @NotNull
    public EarlyErrorState enforceStrictErrors() {
        return new EarlyErrorState(
                this.errors.append(this.strictErrors),
                ImmutableList.nil(),
                this.usedLabelNames,
                this.freeBreakStatements,
                this.freeContinueStatements,
                this.freeLabeledBreakStatements,
                this.freeLabeledContinueStatements,
                this.newTargetExpressions,
                this.boundNames,
                this.previousLexicallyDeclaredNames,
                this.lexicallyDeclaredNames,
                this.varDeclaredNames,
                this.functionDeclarationNames,
                this.forOfVarDeclaredNames,
                this.exportedNames,
                this.exportedBindings,
                this.superCallExpressions,
                this.superCallExpressionsInConstructorMethod,
                this.superPropertyExpressions
        );
    }


    // This class does not distinguish between "key is present, but associated with empty list" and "key is not present". If you need that, don't use this class.
    public static class MultiHashTable<K, V> { // TODO should be elsewhere... and better
        @NotNull
        private final HashTable<K, ImmutableList<V>> data;

        private MultiHashTable(@NotNull HashTable<K, ImmutableList<V>> data) {
            this.data = data;
        }

        @NotNull
        public static <K, V> MultiHashTable<K, V> empty() {
            return new MultiHashTable<>(HashTable.empty());
        }

        @NotNull
        public static <K, V> MultiHashTable<K, V> emptyP() {
            return new MultiHashTable<>(HashTable.emptyP());
        }

        @NotNull
        public MultiHashTable<K, V> put(@NotNull K key, @NotNull V value) {
            return new MultiHashTable<>(this.data.put(key, ImmutableList.cons(value, this.data.get(key).orJust(ImmutableList.nil()))));
        }

        @NotNull
        public MultiHashTable<K, V> remove(@NotNull K key) {
            return new MultiHashTable<>(this.data.remove(key));
        }

        @NotNull
        public ImmutableList<V> get(@NotNull K key) {
            return this.data.get(key).orJust(ImmutableList.nil());
        }

        @NotNull
        public MultiHashTable<K, V> merge(@NotNull MultiHashTable<K, V> tree) { // default merge strategy: append lists.
            return this.merge(tree, ImmutableList::append);
        }

        @NotNull
        public MultiHashTable<K, V> merge(@NotNull MultiHashTable<K, V> tree, @NotNull F2<ImmutableList<V>, ImmutableList<V>, ImmutableList<V>> merger) {
            return new MultiHashTable<>(this.data.merge(tree.data, merger));
        }

        @NotNull
        public ImmutableList<Pair<K, ImmutableList<V>>> entries() {
            return this.data.entries();
        }

        // version: key is irrelevant
        @NotNull
        public <B> HashTable<K, B> toHashTable(@NotNull F<ImmutableList<V>, B> conversion) {
            //return this.data.foldLeft((acc, p) -> acc.put(p.a, conversion.apply(p.b)), HashTable.empty(this.data.hasher));
            return this.toHashTable((k, vs) -> conversion.apply(vs));
        }

        // version: key is used
        @NotNull
        public <B> HashTable<K, B> toHashTable(@NotNull F2<K, ImmutableList<V>, B> conversion) {
            return this.data.foldLeft((acc, p) -> acc.put(p.a, conversion.apply(p.a, p.b)), HashTable.empty(this.data.hasher));
        }

        @NotNull
        public final ImmutableList<ImmutableList<V>> values() {
            return this.data.foldLeft((acc, p) -> acc.cons(p.b), ImmutableList.nil());
        }

        @NotNull
        public final ImmutableList<V> gatherValues() {
            return this.data.foldLeft((acc, p) -> acc.append(p.b), ImmutableList.nil());
        }

        @NotNull
        public final <B> MultiHashTable<K, B> mapValues(@NotNull F<V, B> f) {
            return new MultiHashTable<>(this.data.map(l -> l.map(f::apply)));
        }
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
