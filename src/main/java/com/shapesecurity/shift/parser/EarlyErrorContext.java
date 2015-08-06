package com.shapesecurity.shift.parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.*;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EarlyErrorContext {
    public static final Monoid<EarlyErrorContext> MONOID = new EarlyErrorContextMonoid();

    @NotNull
    public final List<EarlyError> errors;
    // errors that are only errors in strict mode code
    @NotNull
    private final List<EarlyError> strictErrors;

    // Label values used in LabeledStatement nodes; cleared at function boundaries
    @NotNull
    private final Map<String, LabeledStatement> usedLabelNames;


    // BreakStatement nodes; cleared at iteration, switch, and function boundaries
    @NotNull
    private final List<BreakStatement> freeBreakStatements;
    // ContinueStatement nodes; cleared at iteration boundaries
    @NotNull
    private final List<ContinueStatement> freeContinueStatements;

    // labeled BreakStatement nodes; cleared at LabeledStatement with same Label and function boundaries
    @NotNull
    private final Map<String, BreakStatement> freeLabeledBreakStatements;
    // labeled ContinueStatement nodes; cleared at labeled iteration statement with same Label and function boundaries
    @NotNull
    private final Map<String, ContinueStatement> freeLabeledContinueStatements;

    // NewTargetExpression nodes; cleared at function (besides arrow expression) boundaries
    @NotNull
    private final List<NewTargetExpression> newTargetExpressions;

    // BindingIdentifier nodes; cleared at containing declaration node
    @NotNull
    private final Multimap<String, BindingIdentifier> boundNames;
    // BindingIdentifiers that were found to be in a lexical binding position
    @NotNull
    private final Multimap<String, BindingIdentifier> lexicallyDeclaredNames;
    // Previous BindingIdentifiers that were found to be in a lexical binding position
//  @NotNull
//  private Multimap<String, BindingIdentifier> previousLexicallyDeclaredNames;
    // BindingIdentifiers that were the name of a FunctionDeclaration
    @NotNull
    private final Multimap<String, BindingIdentifier> functionDeclarationNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @NotNull
    private final Multimap<String, BindingIdentifier> varDeclaredNames;
    // BindingIdentifiers that were found to be in a variable binding position
    @NotNull
    private final List<BindingIdentifier> forOfVarDeclaredNames;

    // Names that this module exports
    @NotNull
    private final Multimap<String, BindingIdentifier> exportedNames;
    // Locally declared names that are referenced in export declarations
    @NotNull
    private final Multimap<String, BindingIdentifier> exportedBindings;

    // CallExpressions with Super callee
    @NotNull
    private final List<Super> superCallExpressions;
    // SuperCall expressions in the context of a Method named "constructor"
    @NotNull
    private final List<Super> superCallExpressionsInConstructorMethod;
    // MemberExpressions with Super object
    @NotNull
    private final List<MemberExpression> superPropertyExpressions;

    public EarlyErrorContext() {
        this(
                new ArrayList<>(), // errors
                new ArrayList<>(), // strictErrors
                new HashMap<>(), // usedLabelNames
                new ArrayList<>(), // freeBreakStatements
                new ArrayList<>(), // freeContinueStatements
                new HashMap<>(), // freeLabeledBreakStatements
                new HashMap<>(), // freeLabeledContinueStatements
                new ArrayList<>(), // newTargetExpressions
                HashMultimap.create(), // boundNames
                HashMultimap.create(), // lexicallyDeclaredNames
                HashMultimap.create(), // functionDeclarationNames
                HashMultimap.create(), // varDeclaredNames
                new ArrayList<>(), // forOfVarDeclaredNames
                HashMultimap.create(), // exportedNames
                HashMultimap.create(), // exportedBindings
                new ArrayList<>(), // superCallExpressions
                new ArrayList<>(), // superCallExpressionsInConstructorMethod
                new ArrayList<>() // superPropertyExpressions
        );
    }

    public EarlyErrorContext(
            @NotNull List<EarlyError> errors,
            @NotNull List<EarlyError> strictErrors,
            @NotNull Map<String, LabeledStatement> usedLabelNames,
            @NotNull List<BreakStatement> freeBreakStatements,
            @NotNull List<ContinueStatement> freeContinueStatements,
            @NotNull Map<String, BreakStatement> freeLabeledBreakStatements,
            @NotNull Map<String, ContinueStatement> freeLabeledContinueStatements,
            @NotNull List<NewTargetExpression> newTargetExpressions,
            @NotNull Multimap<String, BindingIdentifier> boundNames,
            @NotNull Multimap<String, BindingIdentifier> lexicallyDeclaredNames,
            @NotNull Multimap<String, BindingIdentifier> functionDeclarationNames,
            @NotNull Multimap<String, BindingIdentifier> varDeclaredNames,
            @NotNull List<BindingIdentifier> forOfVarDeclaredNames,
            @NotNull Multimap<String, BindingIdentifier> exportedNames,
            @NotNull Multimap<String, BindingIdentifier> exportedBindings,
            @NotNull List<Super> superCallExpressions,
            @NotNull List<Super> superCallExpressionsInConstructorMethod,
            @NotNull List<MemberExpression> superPropertyExpressions
    ) {
        this.errors = errors;
        this.strictErrors = strictErrors;
        this.usedLabelNames = usedLabelNames;
        this.freeBreakStatements = freeBreakStatements;
        this.freeContinueStatements = freeContinueStatements;
        this.freeLabeledBreakStatements = freeLabeledBreakStatements;
        this.freeLabeledContinueStatements = freeLabeledContinueStatements;
        this.newTargetExpressions = newTargetExpressions;
        this.boundNames = boundNames;
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

    @NotNull
    public EarlyErrorContext append(EarlyErrorContext other) {
        this.errors.addAll(other.errors);
        this.strictErrors.addAll(other.strictErrors);
        this.usedLabelNames.putAll(other.usedLabelNames);
        this.freeBreakStatements.addAll(other.freeBreakStatements);
        this.freeContinueStatements.addAll(other.freeContinueStatements);
        this.freeLabeledBreakStatements.putAll(other.freeLabeledBreakStatements);
        this.freeLabeledContinueStatements.putAll(other.freeLabeledContinueStatements);
        this.newTargetExpressions.addAll(other.newTargetExpressions);
        this.boundNames.putAll(other.boundNames);
        this.lexicallyDeclaredNames.putAll(other.lexicallyDeclaredNames);
        this.functionDeclarationNames.putAll(other.functionDeclarationNames);
        this.varDeclaredNames.putAll(other.varDeclaredNames);
        this.forOfVarDeclaredNames.addAll(other.forOfVarDeclaredNames);
        this.exportedNames.putAll(other.exportedNames);
        this.exportedBindings.putAll(other.exportedBindings);
        this.superCallExpressions.addAll(other.superCallExpressions);
        this.superCallExpressionsInConstructorMethod.addAll(other.superCallExpressionsInConstructorMethod);
        this.superPropertyExpressions.addAll(other.superPropertyExpressions);
        return this;
    }

    public void addFreeBreakStatement(BreakStatement s) {
        this.freeBreakStatements.add(s);
    }

    public void addFreeLabeledBreakStatement(String string, BreakStatement s) {
        this.freeLabeledBreakStatements.put(string, s);
    }

    public void clearFreeBreakStatements() {
        this.freeBreakStatements.clear();
    }

    public void addFreeContinueStatement(ContinueStatement s) {
        this.freeContinueStatements.add(s);
    }

    public void addFreeLabeledContinueStatement(String string, ContinueStatement s) {
        this.freeLabeledContinueStatements.put(string, s);
    }

    public void clearFreeContinueStatements() {
        this.freeContinueStatements.clear();
    }

    public void enforceFreeBreakStatementErrors(Function<BreakStatement, EarlyError> createError) {
//  [].push.apply(this.errors, this.freeBreakStatements.map(createError));
        this.errors.addAll(this.freeBreakStatements.stream().map(createError::apply).collect(Collectors.toList()));
        this.freeBreakStatements.clear();
    }

    public void enforceFreeLabeledBreakStatementErrors(Function<BreakStatement, EarlyError> createError) {
//    [].push.apply(this.errors, this.freeLabeledBreakStatements.map(createError));
        this.errors.addAll(this.freeLabeledBreakStatements.values().stream().map(createError::apply).collect(Collectors.toList()));
        this.freeLabeledBreakStatements.clear();
    }

    public void enforceFreeContinueStatementErrors(Function<ContinueStatement, EarlyError> createError) {
//    [].push.apply(this.errors, this.freeContinueStatements.map(createError));
        this.errors.addAll(this.freeContinueStatements.stream().map(createError::apply).collect(Collectors.toList()));
        this.freeContinueStatements.clear();
    }

    public void enforceFreeLabeledContinueStatementErrors(Function<ContinueStatement, EarlyError> createError) {
//    [].push.apply(this.errors, this.freeLabeledContinueStatements.map(createError));
        this.errors.addAll(this.freeLabeledContinueStatements.values().stream().map(createError::apply).collect(Collectors.toList()));
        this.freeLabeledContinueStatements.clear();
    }


    public void observeIterationLabel(String s, LabeledStatement label) {
        this.usedLabelNames.put(s, label);
        this.freeLabeledBreakStatements.remove(s);
        this.freeLabeledContinueStatements.remove(s);
    }

    public void observeNonIterationLabel(String s, LabeledStatement label) {
        this.usedLabelNames.put(s, label);
        this.freeLabeledBreakStatements.remove(s);
    }

    public void clearUsedLabelNames() {
        this.usedLabelNames.clear();
    }


    public void observeSuperCallExpression(Super node) {
        this.superCallExpressions.add(node);
    }

    public void observeConstructorMethod() {
        this.superCallExpressionsInConstructorMethod.clear();
        this.superCallExpressionsInConstructorMethod.addAll(this.superCallExpressions);
        this.superCallExpressions.clear();
    }

    public void clearSuperCallExpressionsInConstructorMethod() {
        this.superCallExpressionsInConstructorMethod.clear();
    }

    public void enforceSuperCallExpressions(Function<Super, EarlyError> createError) {
//    [].push.apply(this.errors, this.superCallExpressions.map(createError));
//    [].push.apply(this.errors, this.superCallExpressionsInConstructorMethod.map(createError));
        this.errors.addAll(this.superCallExpressions.stream().map(createError::apply).collect(Collectors.toList()));
        this.errors.addAll(this.superCallExpressionsInConstructorMethod.stream().map(createError::apply).collect(Collectors.toList()));
        this.superCallExpressions.clear();
        this.superCallExpressionsInConstructorMethod.clear();
    }

    public void enforceSuperCallExpressionsInConstructorMethod(Function<Super, EarlyError> createError) {
//    [].push.apply(this.errors, this.superCallExpressionsInConstructorMethod.map(createError));
        this.errors.addAll(this.superCallExpressionsInConstructorMethod.stream().map(createError::apply).collect(Collectors.toList()));
        this.superCallExpressionsInConstructorMethod.clear();
    }


    public void observeSuperPropertyExpression(MemberExpression node) {
        this.superPropertyExpressions.add(node);
    }

    public void clearSuperPropertyExpressions() {
        this.superPropertyExpressions.clear();
    }

    public void enforceSuperPropertyExpressions(Function<MemberExpression, EarlyError> createError) {
//    [].push.apply(this.errors, this.superPropertyExpressions.map(createError));
        this.superPropertyExpressions.stream().map(createError::apply).forEach(this::addError);
        this.superPropertyExpressions.clear();
    }


    public void observeNewTargetExpression(NewTargetExpression node) {
        this.newTargetExpressions.add(node);
    }

    public void clearNewTargetExpressions() {
        this.newTargetExpressions.clear();
    }


    public void bindName(String name, BindingIdentifier node) {
        this.boundNames.put(name, node);
    }

    public void clearBoundNames() {
        this.boundNames.clear();
    }

    public void observeLexicalDeclaration() {
        this.lexicallyDeclaredNames.putAll(this.boundNames);
        this.boundNames.clear();
    }

    public void observeLexicalBoundary() {
//    this.previousLexicallyDeclaredNames = this.lexicallyDeclaredNames;
        this.lexicallyDeclaredNames.clear();
        this.functionDeclarationNames.clear();
    }

    public void enforceDuplicateLexicallyDeclaredNames(Function<BindingIdentifier, EarlyError> createError) {
//    this.lexicallyDeclaredNames.forEachEntry((nodes/*, bindingName*/) => {
//    if (nodes.length > 1) {
//      nodes.slice(1).forEach(dupeNode => {
//        this.addError(createError(dupeNode));
//      });
//    }
//    });

        for (String key : this.lexicallyDeclaredNames.keys()) {
            Collection<BindingIdentifier> nodes = this.lexicallyDeclaredNames.get(key);
            if (nodes.size() > 1) {
                for (BindingIdentifier node : nodes) {
                    this.addError(createError.apply(node));
                }
            }
        }
    }

    public void enforceConflictingLexicallyDeclaredNames(ArrayList<String> otherNames, Function<BindingIdentifier, EarlyError> createError) {
//    this.lexicallyDeclaredNames.forEachEntry((nodes, bindingName) => {
//      if (otherNames.has(bindingName)) {
//        nodes.forEach(conflictingNode => {
//          this.addError(createError(conflictingNode));
//        });
//      }
//    });

        for (Map.Entry<String, BindingIdentifier> entry : this.lexicallyDeclaredNames.entries()) {
            String bindingName = entry.getKey();
            BindingIdentifier node = entry.getValue();
            if (otherNames.contains(bindingName)) {
                this.addError(createError.apply(node));
            }
        }
    }

    public void observeFunctionDeclaration() {
        this.observeVarBoundary();
        this.functionDeclarationNames.putAll(this.boundNames);
        this.boundNames.clear();
    }

    public void functionDeclarationNamesAreLexical() {
        this.lexicallyDeclaredNames.putAll(this.functionDeclarationNames);
        this.functionDeclarationNames.clear();
    }

    public void observeVarDeclaration() {
        this.varDeclaredNames.putAll(this.boundNames);
        this.boundNames.clear();
    }

    public void recordForOfVars() {
//    this.varDeclaredNames.forEach((bindingIdentifier) => {
//      this.forOfVarDeclaredNames.push(bindingIdentifier);
//    });

        this.forOfVarDeclaredNames.addAll(this.varDeclaredNames.values());
    }

    public void observeVarBoundary() {
        this.lexicallyDeclaredNames.clear();
        this.functionDeclarationNames.clear();
        this.varDeclaredNames.clear();
        this.forOfVarDeclaredNames.clear();
    }


    public void exportName(String name, BindingIdentifier node) {
        this.exportedNames.put(name, node);
    }

    public void exportDeclaredNames() {
        this.exportedNames.putAll(this.lexicallyDeclaredNames);
        this.exportedNames.putAll(this.varDeclaredNames);
        this.exportedBindings.putAll(this.lexicallyDeclaredNames);
        this.exportedBindings.putAll(this.varDeclaredNames);
    }

    public void exportBinding(String name, BindingIdentifier node) {
        this.exportedBindings.put(name, node);
    }

    public void clearExportedBindings() {
        this.exportedBindings.clear();
    }


    public void addError(EarlyError e) {
        this.errors.add(e);
    }

    public void addStrictError(EarlyError e) {
        this.strictErrors.add(e);
    }

    public void enforceStrictErrors() {
//    [].push.apply(this.errors, this.strictErrors);
        this.errors.addAll(this.strictErrors);
        this.strictErrors.clear();
    }


    private static final class EarlyErrorContextMonoid implements Monoid<EarlyErrorContext> {
        @NotNull
        @Override
        public EarlyErrorContext identity() {
            return new EarlyErrorContext();
        }

        @NotNull
        @Override
        public EarlyErrorContext append(EarlyErrorContext a, EarlyErrorContext b) {
            return a.append(b);
        }
    }
}
