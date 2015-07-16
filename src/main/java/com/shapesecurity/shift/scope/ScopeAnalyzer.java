///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.scope;
//
//import com.shapesecurity.functional.Pair;
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.HashTable;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.functional.data.Monoid;
//import com.shapesecurity.functional.data.NonEmptyImmutableList;
//import com.shapesecurity.shift.ast.Block;
//import com.shapesecurity.shift.ast.CatchClause;
//import com.shapesecurity.shift.ast.Identifier;
//import com.shapesecurity.shift.ast.Node;
//import com.shapesecurity.shift.ast.Script;
//import com.shapesecurity.shift.ast.VariableDeclaration;
//import com.shapesecurity.shift.ast.VariableDeclarator;
//import com.shapesecurity.shift.ast.expression.AssignmentExpression;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.CallExpression;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.PostfixExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.operators.AssignmentOperator;
//import com.shapesecurity.shift.ast.operators.PrefixOperator;
//import com.shapesecurity.shift.ast.property.Getter;
//import com.shapesecurity.shift.ast.property.Setter;
//import com.shapesecurity.shift.ast.statement.ForInStatement;
//import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
//import com.shapesecurity.shift.ast.statement.WithStatement;
//import com.shapesecurity.shift.path.Branch;
//import com.shapesecurity.shift.scope.Declaration.Kind;
//import com.shapesecurity.shift.visitor.MonoidalReducer;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public final class ScopeAnalyzer extends MonoidalReducer<ScopeAnalyzer.State> {
//  private static final ScopeAnalyzer INSTANCE = new ScopeAnalyzer();
//
//  private ScopeAnalyzer() {
//    super(new StateMonoid());
//  }
//
//  @NotNull
//  public static GlobalScope analyze(@NotNull Script script) {
//    return (GlobalScope) script.reduce(INSTANCE).children.maybeHead().just();
//  }
//
//  @NotNull
//  @Override
//  public State reduceIdentifier(@NotNull Identifier node, @NotNull ImmutableList<Branch> path) {
//    return new State(HashTable.empty(),
//        HashTable.empty(),
//        HashTable.empty(),
//        new HashSet<>(),
//        ImmutableList.nil(),
//        ImmutableList.nil(),
//        false,
//        path,
//        node,
//        false);
//  }
//
//  @NotNull
//  @Override
//  public State reduceIdentifierExpression(
//      @NotNull IdentifierExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State identifier) {
//    return identifier.addReference(Accessibility.Read);
//  }
//
//  @NotNull
//  @Override
//  public State reduceBinaryExpression(
//      @NotNull BinaryExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State left,
//      @NotNull State right) {
//    return super.reduceBinaryExpression(node, path, left, right);
//  }
//
//  @NotNull
//  @Override
//  public State reduceAssignmentExpression(
//      @NotNull AssignmentExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State binding,
//      @NotNull State expression) {
//    if (node.binding instanceof IdentifierExpression) {
//      // TODO: Check if this is the actual intention.
//      assert binding.lastIdentifier != null;
//      assert binding.lastPath != null;
//      return expression.addReference(binding.lastPath,
//          binding.lastIdentifier,
//          node.operator == AssignmentOperator.Assign ? Accessibility.Write : Accessibility.ReadWrite);
//    }
//    return super.reduceAssignmentExpression(node, path, binding, expression);
//  }
//
//  @NotNull
//  @Override
//  public State reduceCallExpression(
//      @NotNull CallExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State callee,
//      @NotNull ImmutableList<State> arguments) {
//    State s = super.reduceCallExpression(node, path, callee, arguments);
//    if (node.callee instanceof IdentifierExpression &&
//        ((IdentifierExpression) node.callee).identifier.name.equals("eval")) {
//      return s.taint();
//    }
//    return s;
//  }
//
//  @NotNull
//  @Override
//  public State reduceForInStatement(
//      @NotNull ForInStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Either<State, State> left,
//      @NotNull State right,
//      @NotNull State body) {
//    if (node.left.isRight() && node.left.right().just() instanceof IdentifierExpression) {
//      left = left.map(x -> x, x -> x.addReference(Accessibility.Write));
//    } else if (node.left.isLeft() && node.left.left().just().declarators.head.init.isNothing()) {
//      left = left.map(x -> x.addReference(Accessibility.Write), x -> x);
//    }
//    return super.reduceForInStatement(node, path, left, right, body);
//  }
//
//  @NotNull
//  @Override
//  public State reduceScript(@NotNull Script node, @NotNull ImmutableList<Branch> path, @NotNull State body) {
//    return super.reduceScript(node, path, body).finish(node, Scope.Type.Global);
//  }
//
//  @NotNull
//  @Override
//  public State reduceFunctionDeclaration(
//      @NotNull FunctionDeclaration node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State id,
//      @NotNull ImmutableList<State> params,
//      @NotNull State programBody) {
//    params = params.map(s -> s.addDeclaration(Kind.Param));
//    ImmutableList<Branch> lastPath = id.lastPath;
//    Identifier lastIdentifier = id.lastIdentifier;
//    assert lastPath != null;
//    assert lastIdentifier != null;
//    return super.reduceFunctionDeclaration(node, path, id, params, programBody).finish(node, Scope.Type.Function)
//        .addDeclaration(lastPath, lastIdentifier, Kind.FunctionName);
//  }
//
//  @NotNull
//  @Override
//  public State reduceFunctionExpression(
//      @NotNull FunctionExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull Maybe<State> id,
//      @NotNull ImmutableList<State> params,
//      @NotNull State programBody) {
//    params = params.map(s -> s.addDeclaration(Kind.Param));
//    State s = super.reduceFunctionExpression(node, path, id, params, programBody)
//        .finish(node, Scope.Type.Function);
//    if (id.isJust()) {
//      s = s.target(id.just()).addDeclaration(Kind.FunctionName);
//      s = s.finish(node, Scope.Type.FunctionName);
//    }
//    return s;
//  }
//
//  @NotNull
//  @Override
//  public State reduceGetter(@NotNull Getter node, @NotNull ImmutableList<Branch> path, @NotNull State key, @NotNull State body) {
//    return body.finish(node, Scope.Type.Function);
//  }
//
//  @NotNull
//  @Override
//  public State reduceSetter(
//      @NotNull Setter node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State key,
//      @NotNull State param,
//      @NotNull State body) {
//    return super.reduceSetter(node, path, key, param.addDeclaration(Kind.Param), body).finish(node,
//        Scope.Type.Function);
//  }
//
//  @NotNull
//  @Override
//  public State reduceWithStatement(
//      @NotNull WithStatement node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State object,
//      @NotNull State body) {
//    return super.reduceWithStatement(node, path, object, body.finish(node, Scope.Type.With));
//  }
//
//  @NotNull
//  @Override
//  public State reduceCatchClause(
//      @NotNull CatchClause node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State param,
//      @NotNull State body) {
//    return super.reduceCatchClause(node, path, param.addDeclaration(Kind.CatchParam), body).finish(node,
//        Scope.Type.Catch);
//  }
//
//  @NotNull
//  @Override
//  public State reduceBlock(@NotNull Block node, @NotNull ImmutableList<Branch> path, @NotNull ImmutableList<State> statements) {
//    State s = super.reduceBlock(node, path, statements);
//    if (s.blockScopedDeclarations.length > 0) {
//      s = s.finish(node, Scope.Type.Block);
//    }
//    return s;
//  }
//
//  @NotNull
//  @Override
//  public State reducePostfixExpression(
//      @NotNull PostfixExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State operand) {
//    if (node.operand instanceof IdentifierExpression) {
//      operand = operand.addReference(Accessibility.ReadWrite);
//    }
//    return super.reducePostfixExpression(node, path, operand);
//  }
//
//  @NotNull
//  @Override
//  public State reducePrefixExpression(
//      @NotNull PrefixExpression node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State operand) {
//    if ((node.operator == PrefixOperator.Decrement || node.operator == PrefixOperator.Increment) &&
//        node.operand instanceof IdentifierExpression) {
//      operand = operand.addReference(Accessibility.ReadWrite);
//    }
//    return super.reducePrefixExpression(node, path, operand);
//  }
//
//  @NotNull
//  @Override
//  public State reduceVariableDeclaration(
//      @NotNull VariableDeclaration node, @NotNull ImmutableList<Branch> path, @NotNull NonEmptyImmutableList<State> declarators) {
//    Kind kind = Kind.fromVariableDeclarationKind(node.kind);
//    ImmutableList<State> l = declarators;
//    while (!l.isEmpty()) {
//      l = l.maybeTail().just();
//    }
//    return super.reduceVariableDeclaration(
//        node,
//        path,
//        declarators.map(d -> d.addDeclaration(kind, d.lastDeclaratorWasInit)))
//        .target(declarators.head); // cached id of VariableDeclaration for ForVarInStatement where only one declarator is allowed
//  }
//
//  @NotNull
//  @Override
//  public State reduceVariableDeclarator(
//      @NotNull VariableDeclarator node,
//      @NotNull ImmutableList<Branch> path,
//      @NotNull State id,
//      @NotNull Maybe<State> init) {
//    if (init.isJust()) {
//      id = id.addReference(Accessibility.Write, true);
//    }
//    return super.reduceVariableDeclarator(node, path, id, init).target(id);
//  }
//
//  @SuppressWarnings("ProtectedInnerClass")
//  public static final class State {
//    public final boolean dynamic;
//    @NotNull
//    public final HashTable<String, HashTable<ImmutableList<Branch>, Reference>> freeIdentifiers;
//    @NotNull
//    public final HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> functionScopedDeclarations;
//    @NotNull
//    public final HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> blockScopedDeclarations;
//    @NotNull
//    public final Set<String> functionScopedInit; // function scoped variables with initializers
//    @NotNull
//    public final ImmutableList<Variable> blockScopedTiedVar; // function scoped init vars captured by block scope
//    @NotNull
//    public final ImmutableList<Scope> children;
//    @Nullable
//    public final ImmutableList<Branch> lastPath;
//    @Nullable
//    public final Identifier lastIdentifier;
//    public final boolean lastDeclaratorWasInit;
//    // cached status indicating that declarator below declaration was initialized
//
//    /*
//     * Fully saturated constructor
//     */
//    private State(
//        @NotNull HashTable<String, HashTable<ImmutableList<Branch>, Reference>> freeIdentifiers,
//        @NotNull HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> functionScopedDeclarations,
//        @NotNull HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> blockScopedDeclarations,
//        @NotNull Set<String> functionScopedInit,
//        @NotNull ImmutableList<Variable> blockScopedTiedVar,
//        @NotNull ImmutableList<Scope> children,
//        boolean dynamic,
//        @Nullable ImmutableList<Branch> lastPath,
//        @Nullable Identifier lastIdentifier,
//        boolean lastDeclaratorWasInit
//    ) {
//      this.freeIdentifiers = freeIdentifiers;
//      this.functionScopedDeclarations = functionScopedDeclarations;
//      this.blockScopedDeclarations = blockScopedDeclarations;
//      this.functionScopedInit = functionScopedInit;
//      this.blockScopedTiedVar = blockScopedTiedVar;
//      this.children = children;
//      this.dynamic = dynamic;
//      this.lastPath = lastPath;
//      this.lastIdentifier = lastIdentifier;
//      this.lastDeclaratorWasInit = lastDeclaratorWasInit;
//    }
//
//    /*
//     * Identity constructor
//     */
//    private State() {
//      this.freeIdentifiers = HashTable.empty();
//      this.functionScopedDeclarations = HashTable.empty();
//      this.blockScopedDeclarations = HashTable.empty();
//      this.functionScopedInit = new HashSet<>();
//      this.blockScopedTiedVar = ImmutableList.nil();
//      this.children = ImmutableList.nil();
//      this.dynamic = false;
//      this.lastPath = null;
//      this.lastIdentifier = null;
//      this.lastDeclaratorWasInit = false;
//    }
//
//    /*
//     * Monoidal append: merges the two states together
//     */
//    @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject", "ObjectEquality"})
//    private State(@NotNull State a, @NotNull State b) {
//      this.freeIdentifiers = merge(a.freeIdentifiers, b.freeIdentifiers);
//      this.functionScopedDeclarations = merge(a.functionScopedDeclarations, b.functionScopedDeclarations);
//      this.blockScopedDeclarations = merge(a.blockScopedDeclarations, b.blockScopedDeclarations);
//      this.functionScopedInit = mergeSet(a.functionScopedInit, b.functionScopedInit);
//      this.blockScopedTiedVar = a.blockScopedTiedVar.append(b.blockScopedTiedVar);
//      this.children = a.children.append(b.children);
//      this.dynamic = a.dynamic || b.dynamic;
//      this.lastPath = null;
//      this.lastIdentifier = null;
//      this.lastDeclaratorWasInit = false;
//    }
//
//    /*
//     * Utility method to merge MultiMaps
//     */
//    @NotNull
//    private static <T> HashTable<String, HashTable<ImmutableList<Branch>, T>> merge(
//        @NotNull HashTable<String, HashTable<ImmutableList<Branch>, T>> mapA,
//        @NotNull HashTable<String, HashTable<ImmutableList<Branch>, T>> mapB) {
//      return mapA.merge(mapB, HashTable::merge);
//    }
//
//    @NotNull
//    private static Set<String> mergeSet(@NotNull Set<String> setA, @NotNull Set<String> setB) {
//      if (setB.isEmpty()) {
//        return setA;
//      }
//      if (setA.isEmpty()) {
//        return setB;
//      }
//
//      Set<String> setC = new HashSet<>();
//      setC.addAll(setA);
//      setC.addAll(setB);
//      return setC;
//    }
//
//    /*
//     * Used when a scope boundary is encountered. It resolves the free identifiers
//     * and declarations found into variable objects. Any free identifiers remaining
//     * are carried forward into the new state object.
//     */
//    private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType) {
//      ImmutableList<Variable> variables = ImmutableList.nil();
//
//      HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> functionScope = HashTable.empty();
//      HashTable<String, HashTable<ImmutableList<Branch>, Reference>> freeIdentifiers = this.freeIdentifiers;
//      Set<String> functionScopedInit = new HashSet<>();
//      ImmutableList<Variable> blockScopedTiedVar = ImmutableList.nil();
//
//      switch (scopeType) {
//      case Block:
//      case Catch:
//      case With:
//        // resolve only block-scoped free declarations
//        ImmutableList<Variable> variables3 = variables;
//        for (Pair<String, HashTable<ImmutableList<Branch>, Declaration>> entry2 : this.blockScopedDeclarations.entries()) {
//          String name2 = entry2.a;
//          HashTable<ImmutableList<Branch>, Declaration> declarations2 = entry2.b;
//          HashTable<ImmutableList<Branch>, Reference> references2 = freeIdentifiers.get(name2).orJust(HashTable.empty());
//          variables3 = ImmutableList.cons(new Variable(name2, references2, declarations2), variables3);
//          freeIdentifiers = freeIdentifiers.remove(name2);
//        }
//        variables = variables3;
//        functionScope = this.functionScopedDeclarations;
//        functionScopedInit.addAll(this.functionScopedInit);
//        ImmutableList<Variable> vptr = variables;
//        blockScopedTiedVar = this.blockScopedTiedVar;
//        while (!vptr.isEmpty()) {
//          Variable v = vptr.maybeHead().just();
//          if (functionScopedInit.contains(v.name)) {
//            blockScopedTiedVar = blockScopedTiedVar.cons(v);
//            functionScopedInit.remove(v.name);
//          }
//          vptr = vptr.maybeTail().just();
//        }
//        break;
//      default:
//        // resolve both block-scoped and function-scoped free declarations
//        if (scopeType == Scope.Type.Function) {
//          ImmutableList<Variable> variables1 = variables;
//          HashTable<ImmutableList<Branch>, Reference> arguments = freeIdentifiers.get("arguments").orJust(HashTable.empty());
//          freeIdentifiers = freeIdentifiers.remove("arguments");
//          variables1 = ImmutableList.cons(new Variable("arguments", arguments, HashTable.empty()), variables1);
//          variables = variables1;
//        }
//        ImmutableList<Variable> variables2 = variables;
//        for (Pair<String, HashTable<ImmutableList<Branch>, Declaration>> entry1 : this.blockScopedDeclarations.entries()) {
//          String name1 = entry1.a;
//          HashTable<ImmutableList<Branch>, Declaration> declarations1 = entry1.b;
//          HashTable<ImmutableList<Branch>, Reference> references1 = freeIdentifiers.get(name1).orJust(HashTable.empty());
//          variables2 = ImmutableList.cons(new Variable(name1, references1, declarations1), variables2);
//          freeIdentifiers = freeIdentifiers.remove(name1);
//        }
//        variables = variables2;
//        ImmutableList<Variable> variables1 = variables;
//        for (Pair<String, HashTable<ImmutableList<Branch>, Declaration>> entry : this.functionScopedDeclarations.entries()) {
//          String name = entry.a;
//          HashTable<ImmutableList<Branch>, Declaration> declarations = entry.b;
//          HashTable<ImmutableList<Branch>, Reference> references =
//              freeIdentifiers.get(name).orJust(HashTable.empty());
//          variables1 = ImmutableList.cons(new Variable(name, references, declarations), variables1);
//          freeIdentifiers = freeIdentifiers.remove(name);
//        }
//        variables = variables1;
//        break;
//      }
//
//      Scope scope = scopeType == Scope.Type.Global ?
//          new GlobalScope(this.children, variables, blockScopedTiedVar, freeIdentifiers, astNode) :
//          new Scope(this.children, variables, blockScopedTiedVar, freeIdentifiers, scopeType, this.dynamic, astNode);
//
//      return new State(
//          freeIdentifiers, functionScope, HashTable.empty(), functionScopedInit, blockScopedTiedVar,
//          ImmutableList.list(scope), false, this.lastPath, this.lastIdentifier, this.lastDeclaratorWasInit);
//    }
//
//    /*
//     * Observe a variable entering scope
//     */
//    @NotNull
//    private State addDeclaration(@NotNull Kind kind) {
//      return addDeclaration(kind, false);
//    }
//
//    @NotNull
//    private State addDeclaration(@NotNull Kind kind, boolean hasInit) {
//      assert this.lastPath != null;
//      assert this.lastIdentifier != null;
//      return addDeclaration(this.lastPath, this.lastIdentifier, kind, this.lastDeclaratorWasInit);
//    }
//
//    @NotNull
//    private State addDeclaration(@NotNull ImmutableList<Branch> path, @NotNull Identifier id, @NotNull Kind kind) {
//      return addDeclaration(path, id, kind, false);
//    }
//
//    @NotNull
//    private State addDeclaration(@NotNull ImmutableList<Branch> path, @NotNull Identifier id, @NotNull Kind kind,
//                                 boolean hasInit) {
//      Declaration decl = new Declaration(id, path, kind);
//      HashTable<String, HashTable<ImmutableList<Branch>, Declaration>> declMap =
//          kind.isBlockScoped ? this.blockScopedDeclarations : this.functionScopedDeclarations;
//      HashTable<ImmutableList<Branch>, Declaration> tree = declMap.get(id.name).orJust(HashTable.empty()).put(decl.path, decl);
//      declMap = declMap.put(id.name, tree);
//      Set<String> functionScopedInit = this.functionScopedInit;
//      if (hasInit && kind.isFunctionScoped) {
//        functionScopedInit = new HashSet<>();
//        functionScopedInit.addAll(this.functionScopedInit);
//        functionScopedInit.add(id.name);
//      }
//      return new State(
//          this.freeIdentifiers,
//          kind.isBlockScoped ? this.functionScopedDeclarations : declMap,
//          kind.isBlockScoped ? declMap : this.blockScopedDeclarations,
//          functionScopedInit, this.blockScopedTiedVar, this.children,
//          this.dynamic,
//          this.lastPath,
//          this.lastIdentifier,
//          false
//      );
//    }
//
//    /*
//     * Observe a reference to a variable
//     */
//    @NotNull
//    public State addReference(@NotNull Accessibility accessibility) {
//      return addReference(accessibility, false);
//    }
//
//    @NotNull
//    public State addReference(@NotNull Accessibility accessibility, boolean hasInit) {
//      ImmutableList<Branch> path = this.lastPath;
//      Identifier id = this.lastIdentifier;
//      assert path != null;
//      assert id != null;
//      return addReference(path, id, accessibility, hasInit);
//    }
//
//    @NotNull
//    private State addReference(@NotNull ImmutableList<Branch> path, @NotNull Identifier id,
//                               @NotNull Accessibility accessibility) {
//      return addReference(path, id, accessibility, false);
//    }
//
//    @NotNull
//    private State addReference(@NotNull ImmutableList<Branch> path, @NotNull Identifier id, @NotNull Accessibility accessibility,
//                               boolean hasInit) {
//      Reference ref = new Reference(id, path, accessibility);
//      HashTable<String, HashTable<ImmutableList<Branch>, Reference>> free = this.freeIdentifiers;
//      HashTable<ImmutableList<Branch>, Reference> tree = free.get(ref.node.name).orJust(HashTable.empty()).put(ref.path, ref);
//      free = free.put(ref.node.name, tree);
//      return new State(
//          free,
//          this.functionScopedDeclarations,
//          this.blockScopedDeclarations,
//          this.functionScopedInit,
//          this.blockScopedTiedVar,
//          this.children,
//          this.dynamic,
//          this.lastPath,
//          this.lastIdentifier,
//          hasInit
//      );
//    }
//
//    @NotNull
//    public State taint() {
//      return new State(
//          this.freeIdentifiers,
//          this.functionScopedDeclarations,
//          this.blockScopedDeclarations,
//          this.functionScopedInit,
//          this.blockScopedTiedVar,
//          this.children,
//          true,
//          this.lastPath,
//          this.lastIdentifier,
//          this.lastDeclaratorWasInit
//      );
//    }
//
//    @NotNull
//    public State target(@Nullable State id) {
//      assert id != null;
//      return new State(this.freeIdentifiers, this.functionScopedDeclarations, this.blockScopedDeclarations,
//          this.functionScopedInit, this.blockScopedTiedVar, this.children, this.dynamic,
//          id.lastPath, id.lastIdentifier, id.lastDeclaratorWasInit);
//    }
//  }
//
//  @SuppressWarnings("ProtectedInnerClass")
//  public static final class StateMonoid implements Monoid<State> {
//    @Override
//    @NotNull
//    public State identity() {
//      return new State();
//    }
//
//    @Override
//    @NotNull
//    public State append(State a, State b) {
//      if (a == b) {
//        return a;
//      }
//      return new State(a, b);
//    }
//  }
//}
