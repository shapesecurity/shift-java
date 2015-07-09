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
  public final Map<String, BreakStatement> freeLabeledBreakStatements;
  // labeled ContinueStatement nodes; cleared at labeled iteration statement with same Label and function boundaries
  @NotNull
  public final Map<String, ContinueStatement> freeLabeledContinueStatements;

  // NewTargetExpression nodes; cleared at function (besides arrow expression) boundaries
  @NotNull
  public final List<NewTargetExpression> newTargetExpressions;

  // BindingIdentifier nodes; cleared at containing declaration node
  @NotNull
  public final Multimap<String, BindingIdentifier> boundNames;
  // BindingIdentifiers that were found to be in a lexical binding position
  @NotNull
  public final Multimap<String, BindingIdentifier> lexicallyDeclaredNames;
  // Previous BindingIdentifiers that were found to be in a lexical binding position
  @NotNull
  public Multimap<String, BindingIdentifier> previousLexicallyDeclaredNames;
  // BindingIdentifiers that were the name of a FunctionDeclaration
  @NotNull
  public final Multimap<String, BindingIdentifier> functionDeclarationNames;
  // BindingIdentifiers that were found to be in a variable binding position
  @NotNull
  public final Multimap<String, BindingIdentifier> varDeclaredNames;
  // BindingIdentifiers that were found to be in a variable binding position
  @NotNull
  public final List<BindingIdentifier> forOfVarDeclaredNames;

  // Names that this module exports
  @NotNull
  public final Multimap<String, Export> exportedNames;
  // Locally declared names that are referenced in export declarations
  @NotNull
  public final Multimap<String, ExportDeclaration> exportedBindings;

  // CallExpressions with Super callee
  @NotNull
  public final List<Super> superCallExpressions;
  // SuperCall expressions in the context of a Method named "constructor"
  @NotNull
  public final List<Super> superCallExpressionsInConstructorMethod;
  // MemberExpressions with Super object
  @NotNull
  public final List<MemberExpression> superPropertyExpressions;

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
    @NotNull Multimap<String, Export> exportedNames,
    @NotNull Multimap<String, ExportDeclaration> exportedBindings,
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

  public EarlyErrorContext addFreeBreakStatement(BreakStatement s) {
    this.freeBreakStatements.add(s);
    return this;
  }

  public EarlyErrorContext addFreeLabeledBreakStatement(String string, BreakStatement s) {
    this.freeLabeledBreakStatements.put(string, s);
    return this;
  }

  public EarlyErrorContext clearFreeBreakStatements() {
    this.freeBreakStatements.clear();
    return this;
  }

  public EarlyErrorContext addFreeContinueStatement(ContinueStatement s) {
    this.freeContinueStatements.add(s);
    return this;
  }

  public EarlyErrorContext addFreeLabeledContinueStatement(String string, ContinueStatement s) {
    this.freeLabeledContinueStatements.put(string, s);
    return this;
  }

  public EarlyErrorContext clearFreeContinueStatements() {
    this.freeContinueStatements.clear();
    return this;
  }

  public EarlyErrorContext enforceFreeBreakStatementErrors(java.lang.reflect.Method createError) {
//    [].push.apply(this.errors, this.freeBreakStatements.map(createError));
      this.errors.addAll(this.freeBreakStatements.stream().map(x -> {
        try {
          return createError.invoke(x);
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
      })); // TODO trying to pass method createError as parameter
      this.freeBreakStatements.clear();
      return this;
  }

  public EarlyErrorContext enforceFreeLabeledBreakStatementErrors(createError) {
//    [].push.apply(this.errors, this.freeLabeledBreakStatements.map(createError));
    this.errors.addAll(this.freeLabeledBreakStatements.values().stream().map(x -> createError(x))); // TODO
    this.freeLabeledBreakStatements.clear();
    return this;
  }

  public EarlyErrorContext enforceFreeContinueStatementErrors(createError) {
//    [].push.apply(this.errors, this.freeContinueStatements.map(createError));
    this.errors.addAll(this.freeContinueStatements.stream().map(x -> createError(x))); // TODO
    this.freeContinueStatements.clear();
    return this;
  }

  public EarlyErrorContext enforceFreeLabeledContinueStatementErrors(createError) {
//    [].push.apply(this.errors, this.freeLabeledContinueStatements.map(createError));
    this.errors.addAll(this.freeLabeledContinueStatements.values().stream().map(x -> createError(x))); // TODO
    this.freeLabeledContinueStatements.clear();
    return this;
  }


  public EarlyErrorContext observeIterationLabel(String s, LabeledStatement label) {
    this.usedLabelNames.put(s, label);
    this.freeLabeledBreakStatements = this.freeLabeledBreakStatements.filter(s => s.label !== label);
    this.freeLabeledContinueStatements = this.freeLabeledContinueStatements.filter(s => s.label !== label);
    return this;
  }

  public EarlyErrorContext observeNonIterationLabel(String s, LabeledStatement label) {
    this.usedLabelNames.put(s, label);
    this.freeLabeledBreakStatements = this.freeLabeledBreakStatements.filter(s => s.label != label);
    return this;
  }

  public EarlyErrorContext clearUsedLabelNames() {
    this.usedLabelNames.clear();
    return this;
  }


  public EarlyErrorContext observeSuperCallExpression(Super node) {
    this.superCallExpressions.add(node);
    return this;
  }

  public EarlyErrorContext observeConstructorMethod() {
    this.superCallExpressionsInConstructorMethod.clear();
    this.superCallExpressionsInConstructorMethod.addAll(this.superCallExpressions);
    this.superCallExpressions.clear();
    return this;
  }

  public EarlyErrorContext clearSuperCallExpressionsInConstructorMethod() {
    this.superCallExpressionsInConstructorMethod.clear();
    return this;
  }

  public EarlyErrorContext enforceSuperCallExpressions(createError) {
//    [].push.apply(this.errors, this.superCallExpressions.map(createError));
//    [].push.apply(this.errors, this.superCallExpressionsInConstructorMethod.map(createError));
    this.errors.addAll(this.superCallExpressions.stream().map(x -> createError(x))); // TODO
    this.errors.addAll(this.superCallExpressionsInConstructorMethod.stream().map(x -> createError(x))); // TODO
    this.superCallExpressions.clear();
    this.superCallExpressionsInConstructorMethod.clear();
    return this;
  }

  public EarlyErrorContext enforceSuperCallExpressionsInConstructorMethod(createError) {
//    [].push.apply(this.errors, this.superCallExpressionsInConstructorMethod.map(createError));
    this.errors.addAll(this.superCallExpressionsInConstructorMethod.stream().map(x -> createError(x))); // TODO
    this.superCallExpressionsInConstructorMethod.clear();
    return this;
  }


  public EarlyErrorContext observeSuperPropertyExpression(MemberExpression node) {
    this.superPropertyExpressions.add(node);
    return this;
  }

  public EarlyErrorContext clearSuperPropertyExpressions() {
    this.superPropertyExpressions.clear();
    return this;
  }

  public EarlyErrorContext enforceSuperPropertyExpressions(createError) {
//    [].push.apply(this.errors, this.superPropertyExpressions.map(createError));
    this.errors.addAll(this.superPropertyExpressions.stream().map(x -> createError(x))); // TODO
    this.superPropertyExpressions.clear();
    return this;
  }


  public EarlyErrorContext observeNewTargetExpression(NewTargetExpression node) {
    this.newTargetExpressions.add(node);
    return this;
  }

  public EarlyErrorContext clearNewTargetExpressions() {
    this.newTargetExpressions.clear();
    return this;
  }


  public EarlyErrorContext bindName(String name, BindingIdentifier node) {
    this.boundNames.put(name, node);
    return this;
  }

  public EarlyErrorContext clearBoundNames() {
    this.boundNames.clear();
    return this;
  }

  public EarlyErrorContext observeLexicalDeclaration() {
    this.lexicallyDeclaredNames.putAll(this.boundNames);
    this.boundNames.clear();
    return this;
  }

  public EarlyErrorContext observeLexicalBoundary() {
    this.previousLexicallyDeclaredNames = this.lexicallyDeclaredNames;
    this.lexicallyDeclaredNames.clear();
    this.functionDeclarationNames.clear();
    return this;
  }

  public EarlyErrorContext enforceDuplicateLexicallyDeclaredNames(createError) {
//    this.lexicallyDeclaredNames.forEachEntry((nodes/*, bindingName*/) => {
//    if (nodes.length > 1) {
//      nodes.slice(1).forEach(dupeNode => {
//        this.addError(createError(dupeNode));
//      });
//    }
//    });

    for (String key: this.lexicallyDeclaredNames.keys()) {
      Collection<BindingIdentifier> nodes = this.lexicallyDeclaredNames.get(key);
      if (nodes.size() > 1) {
        for (BindingIdentifier node : nodes) {
          this.addError(createError(node)); // TODO
        }
      }
    }
    return this;
  }

  public EarlyErrorContext enforceConflictingLexicallyDeclaredNames(ArrayList<String> otherNames, createError) {
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
        this.addError(createError(node)); // TODO
      }
    }
    return this;
  }

  public EarlyErrorContext observeFunctionDeclaration() {
    this.observeVarBoundary();
    this.functionDeclarationNames.putAll(this.boundNames);
    this.boundNames.clear();
    return this;
  }

  public EarlyErrorContext functionDeclarationNamesAreLexical() {
    this.lexicallyDeclaredNames.putAll(this.functionDeclarationNames);
    this.functionDeclarationNames.clear();
    return this;
  }

  public EarlyErrorContext observeVarDeclaration() {
    this.varDeclaredNames.putAll(this.boundNames);
    this.boundNames.clear();
    return this;
  }

  public EarlyErrorContext recordForOfVars() {
//    this.varDeclaredNames.forEach((bindingIdentifier) => {
//      this.forOfVarDeclaredNames.push(bindingIdentifier);
//    });

    this.forOfVarDeclaredNames.addAll(this.varDeclaredNames.values());
    return this;
  }

  public EarlyErrorContext observeVarBoundary() {
    this.lexicallyDeclaredNames.clear();
    this.functionDeclarationNames.clear();
    this.varDeclaredNames.clear();
    this.forOfVarDeclaredNames.clear();
    return this;
  }


  public EarlyErrorContext exportName(String name, Export node) {
    this.exportedNames.put(name, node);
    return this;
  }

  public EarlyErrorContext exportDeclaredNames() {
    this.exportedNames.putAll(this.lexicallyDeclaredNames).addEach(this.varDeclaredNames); // TODO cannot add wrong type
    this.exportedBindings.putAll(this.lexicallyDeclaredNames).addEach(this.varDeclaredNames); // TODO cannot add wrong type
    return this;
  }

  public EarlyErrorContext exportBinding(String name, ExportDeclaration node) {
    this.exportedBindings.put(name, node);
    return this;
  }

  public EarlyErrorContext clearExportedBindings() {
    this.exportedBindings.clear();
    return this;
  }


  public EarlyErrorContext addError(EarlyError e) {
    this.errors.add(e);
    return this;
  }

  public EarlyErrorContext addStrictError(EarlyError e) {
    this.strictErrors.add(e);
    return this;
  }

  public EarlyErrorContext enforceStrictErrors() {
//    [].push.apply(this.errors, this.strictErrors);
    this.errors.addAll(this.strictErrors);
    this.strictErrors.clear();
    return this;
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
