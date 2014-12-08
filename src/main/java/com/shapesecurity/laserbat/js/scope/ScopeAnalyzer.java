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

package com.shapesecurity.laserbat.js.scope;

import com.shapesecurity.laserbat.functional.data.Either;
import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.Monoid;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.CatchClause;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Node;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.ast.VariableDeclarator;
import com.shapesecurity.laserbat.js.ast.expression.AssignmentExpression;
import com.shapesecurity.laserbat.js.ast.expression.BinaryExpression;
import com.shapesecurity.laserbat.js.ast.expression.CallExpression;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.PostfixExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.operators.Assignment;
import com.shapesecurity.laserbat.js.ast.operators.PrefixOperator;
import com.shapesecurity.laserbat.js.ast.property.Getter;
import com.shapesecurity.laserbat.js.ast.property.Setter;
import com.shapesecurity.laserbat.js.ast.statement.ForInStatement;
import com.shapesecurity.laserbat.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.scope.Declaration.Kind;
import com.shapesecurity.laserbat.js.visitor.MonoidalReducer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ScopeAnalyzer extends MonoidalReducer<ScopeAnalyzer.State, ScopeAnalyzer.StateMonoid> {
  public static final ScopeAnalyzer INSTANCE = new ScopeAnalyzer();

  private ScopeAnalyzer() {
    super(new StateMonoid());
  }

  @Nonnull
  public static GlobalScope analyze(@Nonnull Script script) {
    return (GlobalScope) script.reduce(INSTANCE, List.<Branch>nil()).children.maybeHead().just();
  }

  @Nonnull
  @Override
  public State reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return new State(new HashMap<>(), new HashMap<>(), new HashMap<>(), List.nil(), false, path, node);
  }

  @Nonnull
  @Override
  public State reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State identifier) {
    return identifier.addReference(Accessibility.Read);
  }

  @Nonnull
  @Override
  public State reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State left,
      @Nonnull State right) {
    return super.reduceBinaryExpression(node, path, left, right);
  }

  @Nonnull
  @Override
  public State reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State binding,
      @Nonnull State expression) {
    if (node.binding instanceof IdentifierExpression) {
      // TODO: Check if this is the actual intention.
      assert binding.lastIdentifier != null;
      assert binding.lastPath != null;
      return expression.addReference(binding.lastPath, binding.lastIdentifier,
          node.operator == Assignment.Assign ? Accessibility.Write : Accessibility.ReadWrite);
    }
    return super.reduceAssignmentExpression(node, path, binding, expression);
  }

  @Nonnull
  @Override
  public State reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State callee,
      @Nonnull List<State> arguments) {
    State s = super.reduceCallExpression(node, path, callee, arguments);
    if (node.callee instanceof IdentifierExpression && ((IdentifierExpression) node.callee).identifier.name.equals(
        "eval")) {
      return s.taint();
    }
    return s;
  }

  @Nonnull
  @Override
  public State reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<State, State> left,
      @Nonnull State right,
      @Nonnull State body) {
    if (node.left.isRight() && node.left.right().just() instanceof IdentifierExpression) {
      left = left.map(x -> x, x -> x.addReference(Accessibility.Write));
    } else if (node.left.isLeft() && node.left.left().just().declarators.head.init.isNothing()) {
      left = left.map(x -> x.addReference(Accessibility.Write), x -> x);
    }
    return super.reduceForInStatement(node, path, left, right, body);
  }

  @Nonnull
  @Override
  public State reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull State body) {
    return super.reduceScript(node, path, body).finish(node, Scope.Type.Global);
  }

  @Nonnull
  @Override
  public State reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull List<State> params,
      @Nonnull State programBody) {
    params = params.map(s -> s.addDeclaration(Kind.Param));
    List<Branch> lastPath = id.lastPath;
    Identifier lastIdentifier = id.lastIdentifier;
    assert lastPath != null;
    assert lastIdentifier != null;
    return super.reduceFunctionDeclaration(node, path, id, params, programBody).finish(node, Scope.Type.Function)
        .addDeclaration(lastPath, lastIdentifier, Kind.FunctionName);
  }

  @Nonnull
  @Override
  public State reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> id,
      @Nonnull List<State> params,
      @Nonnull State programBody) {
    params = params.map(s -> s.addDeclaration(Kind.Param));
    State s = super.reduceFunctionExpression(node, path, id, params, programBody).finish(node, Scope.Type.Function);
    if (id.isJust()) {
      s = s.target(id.just()).addDeclaration(Kind.FunctionName);
      s = s.finish(node, Scope.Type.FunctionName);
    }
    return s;
  }

  @Nonnull
  @Override
  public State reduceGetter(@Nonnull Getter node, @Nonnull List<Branch> path, @Nonnull State key, @Nonnull State body) {
    return body.finish(node, Scope.Type.Function);
  }

  @Nonnull
  @Override
  public State reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull State key,
      @Nonnull State param,
      @Nonnull State body) {
    return super.reduceSetter(node, path, key, param.addDeclaration(Kind.Param), body).finish(node,
        Scope.Type.Function);
  }

  @Nonnull
  @Override
  public State reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State body) {
    return super.reduceWithStatement(node, path, object, body.finish(node, Scope.Type.With));
  }

  @Nonnull
  @Override
  public State reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull State param,
      @Nonnull State body) {
    return super.reduceCatchClause(node, path, param.addDeclaration(Kind.CatchParam), body).finish(node,
        Scope.Type.Catch);
  }

  @Nonnull
  @Override
  public State reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path, @Nonnull List<State> statements) {
    State s = super.reduceBlock(node, path, statements);
    if (!s.blockScopedDeclarations.isEmpty()) {
      s = s.finish(node, Scope.Type.Block);
    }
    return s;
  }

  @Nonnull
  @Override
  public State reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    if (node.operand instanceof IdentifierExpression) {
      operand = operand.addReference(Accessibility.ReadWrite);
    }
    return super.reducePostfixExpression(node, path, operand);
  }

  @Nonnull
  @Override
  public State reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    if ((node.operator == PrefixOperator.Decrement || node.operator == PrefixOperator.Increment)
        && node.operand instanceof IdentifierExpression) {
      operand = operand.addReference(Accessibility.ReadWrite);
    }
    return super.reducePrefixExpression(node, path, operand);
  }

  @Nonnull
  @Override
  public State reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<State> declarators) {
    Kind kind = Kind.fromVariableDeclarationKind(node.kind);
    return super.reduceVariableDeclaration(node, path, declarators.map(d -> d.addDeclaration(kind))).target(
        declarators.head);
  }

  @Nonnull
  @Override
  public State reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull Maybe<State> init) {
    return super.reduceVariableDeclarator(node, path, init.isJust() ? id.addReference(Accessibility.Write) : id, init)
        .target(id);
  }

  @SuppressWarnings("ProtectedInnerClass")
  public static final class State {
    public final boolean dynamic;
    @Nonnull
    public final HashMap<String, ProjectionTree<Reference>> freeIdentifiers;
    @Nonnull
    public final HashMap<String, ProjectionTree<Declaration>> functionScopedDeclarations;
    @Nonnull
    public final HashMap<String, ProjectionTree<Declaration>> blockScopedDeclarations;
    @Nonnull
    public final List<Scope> children;
    @Nullable
    public final List<Branch> lastPath;
    @Nullable
    public final Identifier lastIdentifier;

    /*
     * Fully saturated constructor
     */
    private State(
        @Nonnull HashMap<String, ProjectionTree<Reference>> freeIdentifiers,
        @Nonnull HashMap<String, ProjectionTree<Declaration>> functionScopedDeclarations,
        @Nonnull HashMap<String, ProjectionTree<Declaration>> blockScopedDeclarations,
        @Nonnull List<Scope> children,
        boolean dynamic,
        @Nullable List<Branch> lastPath,
        @Nullable Identifier lastIdentifier) {
      this.freeIdentifiers = freeIdentifiers;
      this.functionScopedDeclarations = functionScopedDeclarations;
      this.blockScopedDeclarations = blockScopedDeclarations;
      this.children = children;
      this.dynamic = dynamic;
      this.lastPath = lastPath;
      this.lastIdentifier = lastIdentifier;
    }

    /*
     * Identity constructor
     */
    private State() {
      this.freeIdentifiers = new LinkedHashMap<>();
      this.functionScopedDeclarations = new LinkedHashMap<>();
      this.blockScopedDeclarations = new LinkedHashMap<>();
      this.children = List.nil();
      this.dynamic = false;
      this.lastPath = null;
      this.lastIdentifier = null;
    }

    /*
     * Monoidal append: merges the two states together
     */
    @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject", "ObjectEquality"})
    private State(@Nonnull State a, @Nonnull State b) {
      if (a == b) {
        this.freeIdentifiers = a.freeIdentifiers;
        this.functionScopedDeclarations = a.functionScopedDeclarations;
        this.blockScopedDeclarations = a.blockScopedDeclarations;
        this.children = a.children;
        this.dynamic = a.dynamic;
      } else {
        this.freeIdentifiers = merge(a.freeIdentifiers, b.freeIdentifiers);
        this.functionScopedDeclarations = merge(a.functionScopedDeclarations, b.functionScopedDeclarations);
        this.blockScopedDeclarations = merge(a.blockScopedDeclarations, b.blockScopedDeclarations);
        this.children = a.children.append(b.children);
        this.dynamic = a.dynamic || b.dynamic;
      }
      this.lastPath = null;
      this.lastIdentifier = null;
    }

    @Nonnull
    private static List<Variable> resolveArguments(
        @Nonnull HashMap<String, ProjectionTree<Reference>> freeIdentifiers,
        @Nonnull List<Variable> variables) {
      ProjectionTree<Reference> arguments = freeIdentifiers.remove("arguments");
      if (arguments == null) {
        arguments = ProjectionTree.<Reference>nil();
      }
      variables = List.cons(new Variable("arguments", arguments, ProjectionTree.<Declaration>nil()), variables);
      return variables;
    }

    @Nonnull
    private static List<Variable> resolveDeclarations(
        @Nonnull HashMap<String, ProjectionTree<Reference>> freeIdentifiers,
        @Nonnull HashMap<String, ProjectionTree<Declaration>> decls,
        @Nonnull List<Variable> variables) {
      for (Map.Entry<String, ProjectionTree<Declaration>> entry : decls.entrySet()) {
        String name = entry.getKey();
        ProjectionTree<Declaration> declarations = entry.getValue();
        ProjectionTree<Reference> references = freeIdentifiers.containsKey(name) ? freeIdentifiers.get(name) :
                                               ProjectionTree.<Reference>nil();
        variables = List.cons(new Variable(name, references, declarations), variables);
        freeIdentifiers.remove(name);
      }
      return variables;
    }

    /*
     * Utility method to merge MultiMaps
     */
    @Nonnull
    private static <T> HashMap<String, ProjectionTree<T>> merge(
        @Nonnull HashMap<String, ProjectionTree<T>> mapA,
        @Nonnull HashMap<String, ProjectionTree<T>> mapB) {
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

    /*
     * Used when a scope boundary is encountered. It resolves the free identifiers
     * and declarations found into variable objects. Any free identifiers remaining
     * are carried forward into the new state object.
     */
    private State finish(@Nonnull Node astNode, @Nonnull Scope.Type scopeType) {
      List<Variable> variables = List.nil();

      HashMap<String, ProjectionTree<Declaration>> functionScope = new LinkedHashMap<>();
      HashMap<String, ProjectionTree<Reference>> freeIdentifiers = new LinkedHashMap<>();
      freeIdentifiers.putAll(this.freeIdentifiers);

      switch (scopeType) {
      case Block:
      case Catch:
      case With:
        // resolve only block-scoped free declarations
        variables = resolveDeclarations(freeIdentifiers, this.blockScopedDeclarations, variables);
        functionScope.putAll(this.functionScopedDeclarations);
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

      Scope scope = scopeType == Scope.Type.Global ? new GlobalScope(this.children, variables, freeIdentifiers,
          astNode) : new Scope(this.children, variables, freeIdentifiers, scopeType, this.dynamic, astNode);

      return new State(freeIdentifiers, functionScope, new LinkedHashMap<>(), List.list(scope), false, this.lastPath,
          this.lastIdentifier);
    }

    /*
     * Observe a variable entering scope
     */
    @Nonnull
    private State addDeclaration(@Nonnull Kind kind) {
      List<Branch> path = this.lastPath;
      Identifier id = this.lastIdentifier;
      assert path != null;
      assert id != null;
      return addDeclaration(path, id, kind);
    }

    @Nonnull
    private State addDeclaration(@Nonnull List<Branch> path, @Nonnull Identifier id, @Nonnull Kind kind) {
      Declaration decl = new Declaration(id, path, kind);
      HashMap<String, ProjectionTree<Declaration>> declMap = new LinkedHashMap<>();
      declMap.putAll(kind.isBlockScoped ? this.blockScopedDeclarations : this.functionScopedDeclarations);
      ProjectionTree<Declaration> tree = declMap.containsKey(id.name) ? declMap.get(id.name).add(decl, path) :
                                         new ProjectionTree<>(decl, path);
      declMap.put(id.name, tree);
      return new State(this.freeIdentifiers, kind.isBlockScoped ? this.functionScopedDeclarations : declMap,
          kind.isBlockScoped ? declMap : this.blockScopedDeclarations, this.children, this.dynamic, this.lastPath,
          this.lastIdentifier);
    }

    /*
     * Observe a reference to a variable
     */
    @Nonnull
    public State addReference(@Nonnull Accessibility accessibility) {
      List<Branch> path = this.lastPath;
      Identifier id = this.lastIdentifier;
      assert path != null;
      assert id != null;
      return addReference(path, id, accessibility);
    }

    @Nonnull
    private State addReference(
        @Nonnull List<Branch> path,
        @Nonnull Identifier id,
        @Nonnull Accessibility accessibility) {
      Reference ref = new Reference(id, path, accessibility);
      HashMap<String, ProjectionTree<Reference>> free = new LinkedHashMap<>();
      free.putAll(this.freeIdentifiers);
      ProjectionTree<Reference> tree = free.containsKey(ref.node.name) ? free.get(ref.node.name).add(ref, ref.path) :
                                       new ProjectionTree<>(ref, ref.path);
      free.put(ref.node.name, tree);
      return new State(free, this.functionScopedDeclarations, this.blockScopedDeclarations, this.children, this.dynamic,
          this.lastPath, this.lastIdentifier);
    }

    @Nonnull
    public State taint() {
      return new State(this.freeIdentifiers, this.functionScopedDeclarations, this.blockScopedDeclarations,
          this.children, true, this.lastPath, this.lastIdentifier);
    }

    @Nonnull
    public State target(@Nullable State id) {
      assert id != null;
      return new State(freeIdentifiers, functionScopedDeclarations, blockScopedDeclarations, children, dynamic,
          id.lastPath, id.lastIdentifier);
    }
  }

  @SuppressWarnings("ProtectedInnerClass")
  public static final class StateMonoid implements Monoid<State> {
    @Override
    @Nonnull
    public State identity() {
      return new State();
    }

    @Override
    @Nonnull
    public State append(State a, State b) {
      return new State(a, b);
    }
  }
}
