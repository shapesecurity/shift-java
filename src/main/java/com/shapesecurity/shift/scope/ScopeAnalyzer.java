/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.BinaryExpression;
import com.shapesecurity.shift.ast.expression.CallExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.PostfixExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.operators.Assignment;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
import com.shapesecurity.shift.ast.property.Getter;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.ForInStatement;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.path.Branch;
import com.shapesecurity.shift.scope.Declaration.Kind;
import com.shapesecurity.shift.visitor.MonoidalReducer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ScopeAnalyzer extends MonoidalReducer<ScopeAnalyzer.State> {
  private static final ScopeAnalyzer INSTANCE = new ScopeAnalyzer();

  private ScopeAnalyzer() {
    super(new StateMonoid());
  }

  @NotNull
  public static GlobalScope analyze(@NotNull Script script) {
    return (GlobalScope) script.reduce(INSTANCE).children.maybeHead().just();
  }

  @NotNull
  @Override
  public State reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return new State(new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashSet<>(),
        List.nil(),
        List.nil(),
        false,
        path,
        node,
        false);
  }

  @NotNull
  @Override
  public State reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull State identifier) {
    return identifier.addReference(Accessibility.Read);
  }

  @NotNull
  @Override
  public State reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull State left,
      @NotNull State right) {
    return super.reduceBinaryExpression(node, path, left, right);
  }

  @NotNull
  @Override
  public State reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull State binding,
      @NotNull State expression) {
    if (node.binding instanceof IdentifierExpression) {
      // TODO: Check if this is the actual intention.
      assert binding.lastIdentifier != null;
      assert binding.lastPath != null;
      return expression.addReference(binding.lastPath,
          binding.lastIdentifier,
          node.operator == Assignment.Assign ? Accessibility.Write : Accessibility.ReadWrite);
    }
    return super.reduceAssignmentExpression(node, path, binding, expression);
  }

  @NotNull
  @Override
  public State reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull State callee,
      @NotNull List<State> arguments) {
    State s = super.reduceCallExpression(node, path, callee, arguments);
    if (node.callee instanceof IdentifierExpression &&
        ((IdentifierExpression) node.callee).identifier.name.equals("eval")) {
      return s.taint();
    }
    return s;
  }

  @NotNull
  @Override
  public State reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<State, State> left,
      @NotNull State right,
      @NotNull State body) {
    if (node.left.isRight() && node.left.right().just() instanceof IdentifierExpression) {
      left = left.map(x -> x, x -> x.addReference(Accessibility.Write));
    } else if (node.left.isLeft() && node.left.left().just().declarators.head.init.isNothing()) {
      left = left.map(x -> x.addReference(Accessibility.Write), x -> x);
    }
    return super.reduceForInStatement(node, path, left, right, body);
  }

  @NotNull
  @Override
  public State reduceScript(@NotNull Script node, @NotNull List<Branch> path, @NotNull State body) {
    return super.reduceScript(node, path, body).finish(node, Scope.Type.Global);
  }

  @NotNull
  @Override
  public State reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull State id,
      @NotNull List<State> params,
      @NotNull State programBody) {
    params = params.map(s -> s.addDeclaration(Kind.Param));
    List<Branch> lastPath = id.lastPath;
    Identifier lastIdentifier = id.lastIdentifier;
    assert lastPath != null;
    assert lastIdentifier != null;
    return super.reduceFunctionDeclaration(node, path, id, params, programBody).finish(node, Scope.Type.Function)
        .addDeclaration(lastPath, lastIdentifier, Kind.FunctionName);
  }

  @NotNull
  @Override
  public State reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<State> id,
      @NotNull List<State> params,
      @NotNull State programBody) {
    params = params.map(s -> s.addDeclaration(Kind.Param));
    State s = super.reduceFunctionExpression(node, path, id, params, programBody)
        .finish(node, Scope.Type.Function);
    if (id.isJust()) {
      s = s.target(id.just()).addDeclaration(Kind.FunctionName);
      s = s.finish(node, Scope.Type.FunctionName);
    }
    return s;
  }

  @NotNull
  @Override
  public State reduceGetter(@NotNull Getter node, @NotNull List<Branch> path, @NotNull State key, @NotNull State body) {
    return body.finish(node, Scope.Type.Function);
  }

  @NotNull
  @Override
  public State reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull State key,
      @NotNull State param,
      @NotNull State body) {
    return super.reduceSetter(node, path, key, param.addDeclaration(Kind.Param), body).finish(node,
        Scope.Type.Function);
  }

  @NotNull
  @Override
  public State reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull State object,
      @NotNull State body) {
    return super.reduceWithStatement(node, path, object, body.finish(node, Scope.Type.With));
  }

  @NotNull
  @Override
  public State reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull State param,
      @NotNull State body) {
    return super.reduceCatchClause(node, path, param.addDeclaration(Kind.CatchParam), body).finish(node,
        Scope.Type.Catch);
  }

  @NotNull
  @Override
  public State reduceBlock(@NotNull Block node, @NotNull List<Branch> path, @NotNull List<State> statements) {
    State s = super.reduceBlock(node, path, statements);
    if (!s.blockScopedDeclarations.isEmpty()) {
      s = s.finish(node, Scope.Type.Block);
    }
    return s;
  }

  @NotNull
  @Override
  public State reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull State operand) {
    if (node.operand instanceof IdentifierExpression) {
      operand = operand.addReference(Accessibility.ReadWrite);
    }
    return super.reducePostfixExpression(node, path, operand);
  }

  @NotNull
  @Override
  public State reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull State operand) {
    if ((node.operator == PrefixOperator.Decrement || node.operator == PrefixOperator.Increment) &&
        node.operand instanceof IdentifierExpression) {
      operand = operand.addReference(Accessibility.ReadWrite);
    }
    return super.reducePrefixExpression(node, path, operand);
  }

  @NotNull
  @Override
  public State reduceVariableDeclaration(
      @NotNull VariableDeclaration node, @NotNull List<Branch> path, @NotNull NonEmptyList<State> declarators) {
    Kind kind = Kind.fromVariableDeclarationKind(node.kind);
    List<State> l = declarators;
    while (!l.isEmpty()) {
      l = l.maybeTail().just();
    }
    return super.reduceVariableDeclaration(
        node,
        path,
        declarators.map(d -> d.addDeclaration(kind, d.lastDeclaratorWasInit)))
        .target(declarators.head); // cached id of VariableDeclaration for ForVarInStatement where only one declarator is allowed
  }

  @NotNull
  @Override
  public State reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull State id,
      @NotNull Maybe<State> init) {
    if (init.isJust()) {
      id = id.addReference(Accessibility.Write, true);
    }
    return super.reduceVariableDeclarator(node, path, id, init).target(id);
  }

  @SuppressWarnings("ProtectedInnerClass")
  public static final class State {
    public final boolean dynamic;
    @NotNull
    public final HashMap<String, ProjectionTree<Reference>> freeIdentifiers;
    @NotNull
    public final HashMap<String, ProjectionTree<Declaration>> functionScopedDeclarations;
    @NotNull
    public final HashMap<String, ProjectionTree<Declaration>> blockScopedDeclarations;
    @NotNull
    public final Set<String> functionScopedInit; // function scoped variables with initializers
    @NotNull
    public final List<Variable> blockScopedTiedVar; // function scoped init vars captured by block scope
    @NotNull
    public final List<Scope> children;
    @Nullable
    public final List<Branch> lastPath;
    @Nullable
    public final Identifier lastIdentifier;
    public final boolean lastDeclaratorWasInit;
    // cached status indicating that declarator below declaration was initialized

    /*
     * Fully saturated constructor
     */
    private State(
        @NotNull HashMap<String, ProjectionTree<Reference>> freeIdentifiers,
        @NotNull HashMap<String, ProjectionTree<Declaration>> functionScopedDeclarations,
        @NotNull HashMap<String, ProjectionTree<Declaration>> blockScopedDeclarations,
        @NotNull Set<String> functionScopedInit,
        @NotNull List<Variable> blockScopedTiedVar,
        @NotNull List<Scope> children,
        boolean dynamic,
        @Nullable List<Branch> lastPath,
        @Nullable Identifier lastIdentifier,
        boolean lastDeclaratorWasInit
    ) {
      this.freeIdentifiers = freeIdentifiers;
      this.functionScopedDeclarations = functionScopedDeclarations;
      this.blockScopedDeclarations = blockScopedDeclarations;
      this.functionScopedInit = functionScopedInit;
      this.blockScopedTiedVar = blockScopedTiedVar;
      this.children = children;
      this.dynamic = dynamic;
      this.lastPath = lastPath;
      this.lastIdentifier = lastIdentifier;
      this.lastDeclaratorWasInit = lastDeclaratorWasInit;
    }

    /*
     * Identity constructor
     */
    private State() {
      this.freeIdentifiers = new LinkedHashMap<>();
      this.functionScopedDeclarations = new LinkedHashMap<>();
      this.blockScopedDeclarations = new LinkedHashMap<>();
      this.functionScopedInit = new HashSet<>();
      this.blockScopedTiedVar = List.nil();
      this.children = List.nil();
      this.dynamic = false;
      this.lastPath = null;
      this.lastIdentifier = null;
      this.lastDeclaratorWasInit = false;
    }

    /*
     * Monoidal append: merges the two states together
     */
    @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject", "ObjectEquality"})
    private State(@NotNull State a, @NotNull State b) {
      if (a == b) {
        this.freeIdentifiers = a.freeIdentifiers;
        this.functionScopedDeclarations = a.functionScopedDeclarations;
        this.blockScopedDeclarations = a.blockScopedDeclarations;
        this.functionScopedInit = a.functionScopedInit;
        this.blockScopedTiedVar = a.blockScopedTiedVar;
        this.children = a.children;
        this.dynamic = a.dynamic;
      } else {
        this.freeIdentifiers = merge(a.freeIdentifiers, b.freeIdentifiers);
        this.functionScopedDeclarations = merge(a.functionScopedDeclarations, b.functionScopedDeclarations);
        this.blockScopedDeclarations = merge(a.blockScopedDeclarations, b.blockScopedDeclarations);
        this.functionScopedInit = mergeSet(a.functionScopedInit, b.functionScopedInit);
        this.blockScopedTiedVar = a.blockScopedTiedVar.append(b.blockScopedTiedVar);
        this.children = a.children.append(b.children);
        this.dynamic = a.dynamic || b.dynamic;
      }
      this.lastPath = null;
      this.lastIdentifier = null;
      this.lastDeclaratorWasInit = false;
    }

    @NotNull
    private static List<Variable> resolveArguments(
        @NotNull HashMap<String, ProjectionTree<Reference>> freeIdentifiers, @NotNull List<Variable> variables
    ) {
      ProjectionTree<Reference> arguments = freeIdentifiers.remove("arguments");
      if (arguments == null) {
        arguments = ProjectionTree.nil();
      }
      variables = List.cons(new Variable("arguments", arguments, ProjectionTree.nil()), variables);
      return variables;
    }

    @NotNull
    private static List<Variable> resolveDeclarations(
        @NotNull HashMap<String, ProjectionTree<Reference>> freeIdentifiers,
        @NotNull HashMap<String, ProjectionTree<Declaration>> decls,
        @NotNull List<Variable> variables) {
      for (Map.Entry<String, ProjectionTree<Declaration>> entry : decls.entrySet()) {
        String name = entry.getKey();
        ProjectionTree<Declaration> declarations = entry.getValue();
        ProjectionTree<Reference> references =
            freeIdentifiers.containsKey(name) ? freeIdentifiers.get(name) : ProjectionTree.nil();
        variables = List.cons(new Variable(name, references, declarations), variables);
        freeIdentifiers.remove(name);
      }
      return variables;
    }

    /*
     * Utility method to merge MultiMaps
     */
    @NotNull
    private static <T> HashMap<String, ProjectionTree<T>> merge(
        @NotNull HashMap<String, ProjectionTree<T>> mapA,
        @NotNull HashMap<String, ProjectionTree<T>> mapB) {
      if (mapB.isEmpty()) {
        return mapA;
      }
      if (mapA.isEmpty()) {
        return mapB;
      }

      HashMap<String, ProjectionTree<T>> mapC = new LinkedHashMap<>();
      mapC.putAll(mapA);
      for (Map.Entry<String, ProjectionTree<T>> entry : mapB.entrySet()) {
        if (mapA.containsKey(entry.getKey())) {
          mapC.put(entry.getKey(), entry.getValue().append(mapC.get(entry.getKey())));
        } else {
          mapC.put(entry.getKey(), entry.getValue());
        }
      }

      return mapC;
    }

    @NotNull
    private static Set<String> mergeSet(@NotNull Set<String> setA, @NotNull Set<String> setB) {
      if (setB.isEmpty()) {
        return setA;
      }
      if (setA.isEmpty()) {
        return setB;
      }

      Set<String> setC = new HashSet<>();
      setC.addAll(setA);
      setC.addAll(setB);
      return setC;
    }

    /*
     * Used when a scope boundary is encountered. It resolves the free identifiers
     * and declarations found into variable objects. Any free identifiers remaining
     * are carried forward into the new state object.
     */
    private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType) {
      List<Variable> variables = List.nil();

      HashMap<String, ProjectionTree<Declaration>> functionScope = new LinkedHashMap<>();
      HashMap<String, ProjectionTree<Reference>> freeIdentifiers = new LinkedHashMap<>();
      freeIdentifiers.putAll(this.freeIdentifiers);
      Set<String> functionScopedInit = new HashSet<>();
      List<Variable> blockScopedTiedVar = List.nil();

      switch (scopeType) {
      case Block:
      case Catch:
      case With:
        // resolve only block-scoped free declarations
        variables = resolveDeclarations(freeIdentifiers, this.blockScopedDeclarations, variables);
        functionScope.putAll(this.functionScopedDeclarations);
        functionScopedInit.addAll(this.functionScopedInit);
        List<Variable> vptr = variables;
        blockScopedTiedVar = this.blockScopedTiedVar;
        while (!vptr.isEmpty()) {
          Variable v = vptr.maybeHead().just();
          if (functionScopedInit.contains(v.name)) {
            blockScopedTiedVar = blockScopedTiedVar.cons(v);
            functionScopedInit.remove(v.name);
          }
          vptr = vptr.maybeTail().just();
        }
        break;
      default:
        // resolve both block-scoped and function-scoped free declarations
        if (scopeType == Scope.Type.Function) {
          variables = resolveArguments(freeIdentifiers, variables);
        }
        variables = resolveDeclarations(freeIdentifiers, this.blockScopedDeclarations, variables);
        variables = resolveDeclarations(freeIdentifiers, this.functionScopedDeclarations, variables);
        break;
      }

      Scope scope = scopeType == Scope.Type.Global ?
          new GlobalScope(this.children, variables, blockScopedTiedVar, freeIdentifiers, astNode) :
          new Scope(this.children, variables, blockScopedTiedVar, freeIdentifiers, scopeType, this.dynamic, astNode);

      return new State(
          freeIdentifiers, functionScope, new LinkedHashMap<>(), functionScopedInit, blockScopedTiedVar,
          List.list(scope), false, this.lastPath, this.lastIdentifier, this.lastDeclaratorWasInit);
    }

    /*
     * Observe a variable entering scope
     */
    @NotNull
    private State addDeclaration(@NotNull Kind kind) {
      return addDeclaration(kind, false);
    }

    @NotNull
    private State addDeclaration(@NotNull Kind kind, boolean hasInit) {
      List<Branch> path = this.lastPath;
      Identifier id = this.lastIdentifier;
      hasInit = this.lastDeclaratorWasInit;
      assert path != null;
      assert id != null;
      return addDeclaration(path, id, kind, hasInit);
    }

    @NotNull
    private State addDeclaration(@NotNull List<Branch> path, @NotNull Identifier id, @NotNull Kind kind) {
      return addDeclaration(path, id, kind, false);
    }

    @NotNull
    private State addDeclaration(@NotNull List<Branch> path, @NotNull Identifier id, @NotNull Kind kind,
                                 boolean hasInit) {
      Declaration decl = new Declaration(id, path, kind);
      HashMap<String, ProjectionTree<Declaration>> declMap = new LinkedHashMap<>();
      declMap.putAll(kind.isBlockScoped ? this.blockScopedDeclarations : this.functionScopedDeclarations);
      ProjectionTree<Declaration> tree =
          declMap.containsKey(id.name) ?
              declMap.get(id.name).add(decl, path) :
              new ProjectionTree<>(decl, path);
      declMap.put(id.name, tree);
      Set<String> functionScopedInit = this.functionScopedInit;
      if (hasInit && kind.isFunctionScoped) {
        functionScopedInit = new HashSet<>();
        functionScopedInit.addAll(this.functionScopedInit);
        functionScopedInit.add(id.name);
      }
      return new State(
          this.freeIdentifiers,
          kind.isBlockScoped ? this.functionScopedDeclarations : declMap,
          kind.isBlockScoped ? declMap : this.blockScopedDeclarations,
          functionScopedInit, this.blockScopedTiedVar, this.children,
          this.dynamic,
          this.lastPath,
          this.lastIdentifier,
          false
      );
    }

    /*
     * Observe a reference to a variable
     */
    @NotNull
    public State addReference(@NotNull Accessibility accessibility) {
      return addReference(accessibility, false);
    }

    @NotNull
    public State addReference(@NotNull Accessibility accessibility, boolean hasInit) {
      List<Branch> path = this.lastPath;
      Identifier id = this.lastIdentifier;
      assert path != null;
      assert id != null;
      return addReference(path, id, accessibility, hasInit);
    }

    @NotNull
    private State addReference(@NotNull List<Branch> path, @NotNull Identifier id,
                               @NotNull Accessibility accessibility) {
      return addReference(path, id, accessibility, false);
    }

    @NotNull
    private State addReference(@NotNull List<Branch> path, @NotNull Identifier id, @NotNull Accessibility accessibility,
                               boolean hasInit) {
      Reference ref = new Reference(id, path, accessibility);
      HashMap<String, ProjectionTree<Reference>> free = new LinkedHashMap<>();
      free.putAll(this.freeIdentifiers);
      ProjectionTree<Reference> tree =
          free.containsKey(ref.node.name) ?
              free.get(ref.node.name).add(ref, ref.path) :
              new ProjectionTree<>(ref, ref.path);
      free.put(ref.node.name, tree);
      return new State(
          free,
          this.functionScopedDeclarations,
          this.blockScopedDeclarations,
          this.functionScopedInit,
          this.blockScopedTiedVar,
          this.children,
          this.dynamic,
          this.lastPath,
          this.lastIdentifier,
          hasInit
      );
    }

    @NotNull
    public State taint() {
      return new State(
          this.freeIdentifiers
          , this.functionScopedDeclarations,
          this.blockScopedDeclarations,
          this.functionScopedInit,
          this.blockScopedTiedVar,
          this.children,
          true,
          this.lastPath,
          this.lastIdentifier,
          this.lastDeclaratorWasInit
      );
    }

    @NotNull
    public State target(@Nullable State id) {
      assert id != null;
      return new State(freeIdentifiers, functionScopedDeclarations, blockScopedDeclarations,
          functionScopedInit, blockScopedTiedVar, children, dynamic,
          id.lastPath, id.lastIdentifier, id.lastDeclaratorWasInit);
    }
  }

  @SuppressWarnings("ProtectedInnerClass")
  public static final class StateMonoid implements Monoid<State> {
    @Override
    @NotNull
    public State identity() {
      return new State();
    }

    @Override
    @NotNull
    public State append(State a, State b) {
      return new State(a, b);
    }
  }
}
