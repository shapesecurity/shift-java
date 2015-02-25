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

import static com.shapesecurity.shift.path.StaticBranch.ARGUMENTS;
import static com.shapesecurity.shift.path.StaticBranch.BINDING;
import static com.shapesecurity.shift.path.StaticBranch.BLOCK;
import static com.shapesecurity.shift.path.StaticBranch.BODY;
import static com.shapesecurity.shift.path.StaticBranch.CALLEE;
import static com.shapesecurity.shift.path.StaticBranch.CATCHCLAUSE;
import static com.shapesecurity.shift.path.StaticBranch.CONSEQUENT;
import static com.shapesecurity.shift.path.StaticBranch.DECLARATION;
import static com.shapesecurity.shift.path.StaticBranch.DECLARATORS;
import static com.shapesecurity.shift.path.StaticBranch.EXPRESSION;
import static com.shapesecurity.shift.path.StaticBranch.IDENTIFIER;
import static com.shapesecurity.shift.path.StaticBranch.INIT;
import static com.shapesecurity.shift.path.StaticBranch.JUST;
import static com.shapesecurity.shift.path.StaticBranch.LEFT;
import static com.shapesecurity.shift.path.StaticBranch.NAME;
import static com.shapesecurity.shift.path.StaticBranch.OBJECT;
import static com.shapesecurity.shift.path.StaticBranch.OPERAND;
import static com.shapesecurity.shift.path.StaticBranch.PARAMETERS;
import static com.shapesecurity.shift.path.StaticBranch.RIGHT;
import static com.shapesecurity.shift.path.StaticBranch.STATEMENTS;
import static com.shapesecurity.shift.path.StaticBranch.TEST;
import static org.junit.Assert.assertTrue;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.TestBase;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.path.Branch;
import com.shapesecurity.shift.path.IndexedBranch;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class ScopeTest extends TestBase {

  private static class IdentifierP {
    final ImmutableList<Branch> from;
    final Identifier node;

    public IdentifierP(ImmutableList<Branch> from, Identifier node) {
      this.from = from;
      this.node = node;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == this || obj instanceof IdentifierP && ((IdentifierP) obj).from.equals(this.from) &&
          ((IdentifierP) obj).node.equals(this.node);
    }

    @Override
    public int hashCode() {
      return this.from.hashCode() ^ (this.node.hashCode() << 3) ^ (this.node.hashCode() >>> 29);
    }
  }

  private static final ImmutableList<IdentifierP> NO_REFERENCES = ImmutableList.nil();
  private static final ImmutableList<IdentifierP> NO_DECLARATIONS = NO_REFERENCES;

  private static class Getter {
    @NotNull
    public final Node node;
    @NotNull
    public final ImmutableList<Branch> from;

    private Getter(@NotNull Node node, @NotNull ImmutableList<Branch> from) {
      this.node = node;
      this.from = from;
    }

    public Getter(@NotNull Node node) {
      this(node, ImmutableList.nil());
    }

    @NotNull
    private Getter d(@NotNull Branch branch) {
      Maybe<? extends Node> maybe = node.get(branch);
      assertTrue("Failed to follow branches.", maybe.isJust());
      return new Getter(maybe.just(), from.cons(branch));
    }

    @NotNull
    public Getter d(@NotNull Branch staticBranch, int index) {
      return d(staticBranch).d(IndexedBranch.from(index));
    }

    @NotNull
    IdentifierP done() {
      assertTrue("The endpoint is not identifier.", this.node instanceof Identifier);
      return new IdentifierP(this.from, (Identifier) this.node);
    }
  }

  @Test
  public void testVariableDeclaration1() throws JsError {
    String js = "var v1; var v2 = 'hello';";
    Script script = parse(js);

    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();

    { // global scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), NO_REFERENCES));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v2Node1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testVariableDeclaration2() throws JsError {
    String js = "var v1, v2 = 'hello';";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 1).d(BINDING)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), NO_REFERENCES));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v2Node1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testVariableDeclaration3() throws JsError {
    String js = "v1 = 'hello'; var v2 = v1 + ' world';";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(BINDING).d(IDENTIFIER)
        .done();
    final IdentifierP v1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(LEFT).d(IDENTIFIER).done();
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("v1");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(v1Node1, v1Node2)));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v1Node1, Accessibility.Write);
      referenceTypes.put(v1Node2, Accessibility.Read);
      referenceTypes.put(v2Node1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testVariableDeclaration4() throws JsError {
    String js = "var v2 = v1 + ' world'; var v1 = 'hello'; ";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(LEFT).d(IDENTIFIER).done();
    final IdentifierP v1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node2), ImmutableList.list(v1Node1, v1Node2)));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v1Node1, Accessibility.Read);
      referenceTypes.put(v1Node2, Accessibility.Write);
      referenceTypes.put(v2Node1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testVariableDeclaration5() throws JsError {
    String js = "var v1; var v1 = 'world'; var v2 = v1 + ' world';";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v1Node3 = new Getter(script).d(BODY).d(STATEMENTS, 2).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(LEFT).d(IDENTIFIER).done();
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 2).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1, v1Node2), ImmutableList.list(v1Node2, v1Node3)));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v1Node2, Accessibility.Write);
      referenceTypes.put(v1Node3, Accessibility.Read);
      referenceTypes.put(v2Node1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testFunctionDeclaration1() throws JsError {
    String js = "function f1(p1, p2) {" +
        "  var v1 = 1;" +
        "  function f2(p1) {" +
        "    var v2 = p1 + v1 + p2;" +
        "    return v2;" +
        "  }" +
        "  return f2;" +
        '}' +
        "var r = f1(2, 3);";
    Script script = parse(js);

    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope f1Scope = globalScope.children.maybeHead().just();
    Scope f2Scope = globalScope.children.maybeHead().just().children.maybeHead().just();

    final IdentifierP f1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP f1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP f2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(NAME).done();
    final IdentifierP f2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 2).d(EXPRESSION).d(
        JUST).d(IDENTIFIER).done();
    final IdentifierP p1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(PARAMETERS, 0).done();
    final IdentifierP p1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(PARAMETERS, 0)
        .done();
    final IdentifierP p1Node3 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(BODY).d(
        STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(LEFT).d(LEFT).d(IDENTIFIER).done();
    final IdentifierP p2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(PARAMETERS, 1).done();
    final IdentifierP p2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(BODY).d(
        STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(RIGHT).d(IDENTIFIER).done();
    final IdentifierP rNode1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(
        DECLARATORS, 0).d(BINDING).done();
    final IdentifierP v1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(BODY).d(
        STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(LEFT).d(RIGHT).d(IDENTIFIER).done();
    final IdentifierP v2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(BODY).d(
        STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING).done();
    final IdentifierP v2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(BODY).d(
        STATEMENTS, 1).d(EXPRESSION).d(JUST).d(IDENTIFIER).done();

    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(f1Scope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f1", new Pair<>(ImmutableList.list(f1Node1), ImmutableList.list(f1Node2)));
      variables.put("r", new Pair<>(ImmutableList.list(rNode1), ImmutableList.list(rNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(f1Node2, Accessibility.Read);
      referenceTypes.put(rNode1, Accessibility.Write);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }

    { // f1 scope

      ImmutableList<Scope> children = ImmutableList.list(f2Scope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), ImmutableList.list(v1Node1, v1Node2)));
      variables.put("p1", new Pair<>(ImmutableList.list(p1Node1), NO_REFERENCES));
      variables.put("p2", new Pair<>(ImmutableList.list(p2Node1), ImmutableList.list(p2Node2)));
      variables.put("f2", new Pair<>(ImmutableList.list(f2Node1), ImmutableList.list(f2Node2)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v1Node1, Accessibility.Write);
      referenceTypes.put(v1Node2, Accessibility.Read);
      referenceTypes.put(p2Node2, Accessibility.Read);
      referenceTypes.put(f2Node2, Accessibility.Read);

      checkScope(f1Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }

    { // f2 scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("v1", "p2");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("p1", new Pair<>(ImmutableList.list(p1Node2), ImmutableList.list(p1Node3)));
      variables.put("v2", new Pair<>(ImmutableList.list(v2Node1), ImmutableList.list(v2Node1, v2Node2)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(p1Node3, Accessibility.Read);
      referenceTypes.put(v2Node1, Accessibility.Write);
      referenceTypes.put(v2Node2, Accessibility.Read);

      checkScope(f2Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testFunctionDeclaration2() throws JsError {
    String js = "function f() {f = 'hello';} f();";
    Script script = parse(js);

    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fScope = globalScope.children.maybeHead().just();

    final IdentifierP fNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP fNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        BINDING).d(IDENTIFIER).done();
    final IdentifierP fNode3 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f", new Pair<>(ImmutableList.list(fNode1), ImmutableList.list(fNode2, fNode3)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(fNode2, Accessibility.Write);
      referenceTypes.put(fNode3, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // f scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("f");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testFunctionExpression1() throws JsError {
    String js = "var f = function() {f = 'hello';}; f();";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fScope = globalScope.children.maybeHead().just();
    final IdentifierP fNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP fNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(
        BODY).d(STATEMENTS, 0).d(EXPRESSION).d(BINDING).d(IDENTIFIER).done();
    final IdentifierP fNode3 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f", new Pair<>(ImmutableList.list(fNode1), ImmutableList.list(fNode1, fNode2, fNode3)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(fNode1, Accessibility.Write);
      referenceTypes.put(fNode2, Accessibility.Write);
      referenceTypes.put(fNode3, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // f scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("f");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testFunctionExpression2() throws JsError {
    String js = "var f2 = function f1() {f1 = 'hello';}; f1(); f2();";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope functionNameScope = globalScope.children.maybeHead().just();
    Scope functionScope = functionNameScope.children.maybeHead().just();
    final IdentifierP f1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(NAME).d(JUST).done();
    final IdentifierP f1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(INIT).d(
        JUST).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(BINDING).d(IDENTIFIER).done();
    final IdentifierP f1Node3 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(CALLEE).d(IDENTIFIER)
        .done();
    final IdentifierP f2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP f2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 2).d(EXPRESSION).d(CALLEE).d(IDENTIFIER)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(functionNameScope);

      ImmutableList<String> through = ImmutableList.list("f1");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f2", new Pair<>(ImmutableList.list(f2Node1), ImmutableList.list(f2Node1, f2Node2)));
      variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(f1Node3)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(f2Node1, Accessibility.Write);
      referenceTypes.put(f2Node2, Accessibility.Read);
      referenceTypes.put(f1Node3, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function name scope

      ImmutableList<Scope> children = ImmutableList.list(functionScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f1", new Pair<>(ImmutableList.list(f1Node1), ImmutableList.list(f1Node2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(f1Node2, Accessibility.Write);

      checkScope(functionNameScope, Scope.Type.FunctionName, false, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("f1");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testHoistDeclaration1() throws JsError {
    String js = "var foo = 1;" +
        "function bar() {" +
        "  if (!foo) {" +
        "    var foo = 'hello';" +
        "  }" +
        "  alert(foo);" +
        '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope functionScope = globalScope.children.maybeHead().just();
    final IdentifierP fooNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP fooNode2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 0).d(TEST).d(OPERAND)
        .d(IDENTIFIER).done();
    final IdentifierP fooNode3 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 0).d(CONSEQUENT).d(
        BLOCK).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING).done();
    final IdentifierP fooNode4 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(
        ARGUMENTS, 0).d(IDENTIFIER).done();
    final IdentifierP barNode1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(NAME).done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(
        CALLEE).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(functionScope);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), ImmutableList.list(fooNode1)));
      variables.put("bar", new Pair<>(ImmutableList.list(barNode1), NO_REFERENCES));
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(fooNode1, Accessibility.Write);
      referenceTypes.put(alertNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("foo", new Pair<>(ImmutableList.list(fooNode3), ImmutableList.list(fooNode2, fooNode3, fooNode4)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(fooNode2, Accessibility.Read);
      referenceTypes.put(fooNode3, Accessibility.Write);
      referenceTypes.put(fooNode4, Accessibility.Read);

      checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testHoistDeclaration2() throws JsError {
    String js = "var a = 1;" +
        "function b() {" +
        "  a = 10;" +
        "  return;" +
        "  function a(){}" +
        '}' +
        "b();" +
        "alert(a);";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope bScope = globalScope.children.maybeHead().just();
    Scope aScope = bScope.children.maybeHead().just();
    final IdentifierP aNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP bNode1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(NAME).done();
    final IdentifierP aNode2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        BINDING).d(IDENTIFIER).done();
    final IdentifierP aNode3 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(STATEMENTS, 2).d(NAME).done();
    final IdentifierP bNode2 = new Getter(script).d(BODY).d(STATEMENTS, 2).d(EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 3).d(EXPRESSION).d(CALLEE).d(IDENTIFIER)
        .done();
    final IdentifierP aNode4 = new Getter(script).d(BODY).d(STATEMENTS, 3).d(EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER)
        .done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(bScope);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("a", new Pair<>(ImmutableList.list(aNode1), ImmutableList.list(aNode1, aNode4)));
      variables.put("b", new Pair<>(ImmutableList.list(bNode1), ImmutableList.list(bNode2)));
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(aNode1, Accessibility.Write);
      referenceTypes.put(aNode4, Accessibility.Read);
      referenceTypes.put(bNode2, Accessibility.Read);
      referenceTypes.put(alertNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // b scope

      ImmutableList<Scope> children = ImmutableList.list(aScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("a", new Pair<>(ImmutableList.list(aNode3), ImmutableList.list(aNode2)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(aNode2, Accessibility.Write);

      checkScope(bScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
    { // a scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(aScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testHoistDeclaration3() throws JsError {
    String js = "function foo() {" +
        "  function bar() {" +
        "    return 3;" +
        "  }" +
        "  return bar();" +
        "  function bar() {" +
        "    return 'hello';" +
        "  }" +
        '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fooScope = globalScope.children.maybeHead().just();
    Scope barScope1 = fooScope.children.maybeHead().just();
    Scope barScope2 = fooScope.children.maybeTail().just().maybeHead().just();
    final IdentifierP fooNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP barNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP barNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(
        JUST).d(
        CALLEE).d(IDENTIFIER).done();
    final IdentifierP barNode3 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 2).d(NAME).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fooScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // foo scope

      ImmutableList<Scope> children = ImmutableList.list(barScope1, barScope2);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("bar", new Pair<>(ImmutableList.list(barNode1, barNode3), ImmutableList.list(barNode2)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(barNode2, Accessibility.Read);

      checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
    { // bar1 scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
    { // bar2 scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testHoistDeclaration4() throws JsError {
    String js = "foo(); function foo() {}";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fooScope = globalScope.children.maybeHead().just();
    final IdentifierP fooNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(IDENTIFIER)
        .done();
    final IdentifierP fooNode2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(NAME).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fooScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("foo", new Pair<>(ImmutableList.list(fooNode2), ImmutableList.list(fooNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(fooNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // foo scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testHoistDeclaration5() throws JsError {
    String js = "function foo() {" +
        "  return bar();" +
        "  var bar = function() {" +
        "    return 3;" +
        "  };" +
        "  var bar = function() {" +
        "   return 'hello';" +
        "  };" +
        '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fooScope = globalScope.children.maybeHead().just();
    Scope barScope1 = fooScope.children.maybeHead().just();
    Scope barScope2 = fooScope.children.maybeTail().just().maybeHead().just();
    final IdentifierP fooNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP barNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        JUST).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP barNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 1).d(DECLARATION).d(
        DECLARATORS, 0).d(BINDING).done();
    final IdentifierP barNode3 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 2).d(DECLARATION).d(
        DECLARATORS, 0).d(BINDING).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fooScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // foo scope

      ImmutableList<Scope> children = ImmutableList.list(barScope1, barScope2);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("bar", new Pair<>(
          ImmutableList.list(barNode2, barNode3), ImmutableList.list(
          barNode1,
          barNode2,
          barNode3)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(barNode1, Accessibility.Read);
      referenceTypes.put(barNode2, Accessibility.Write);
      referenceTypes.put(barNode3, Accessibility.Write);

      checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
    { // bar scope 1

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
    { // bar scope 2

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testClosure1() throws JsError {
    String js = "(function() {f1 = 'hello'; alert(f1);})();";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope functionScope = globalScope.children.maybeHead().just();
    final IdentifierP f1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 0).d(EXPRESSION).d(BINDING).d(IDENTIFIER).done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 1).d(EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP f1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 1).d(EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(functionScope);

      ImmutableList<String> through = ImmutableList.list("f1", "alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(f1Node1, f1Node2)));
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(f1Node1, Accessibility.Write);
      referenceTypes.put(f1Node2, Accessibility.Read);
      referenceTypes.put(alertNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("f1", "alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testClosure2() throws JsError {
    String js = "(function() {var f1 = 'hello'; alert(f1);})();";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope functionScope = globalScope.children.maybeHead().just();
    final IdentifierP f1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING).done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 1).d(EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP f1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(CALLEE).d(BODY).d(
        STATEMENTS, 1).d(EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(functionScope);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(alertNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f1", new Pair<>(ImmutableList.list(f1Node1), ImmutableList.list(f1Node1, f1Node2)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(f1Node1, Accessibility.Write);
      referenceTypes.put(f1Node2, Accessibility.Read);

      checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testArgument1() throws JsError {
    String js = "function f(arg1, arg2) {var v1 = arg1 + arg2 + ' world';}";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fScope = globalScope.children.maybeHead().just();
    final IdentifierP fNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP arg1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(PARAMETERS, 0).done();
    final IdentifierP arg2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(PARAMETERS, 1).done();
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(
        DECLARATORS, 0).d(BINDING).done();
    final IdentifierP arg1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(
        DECLARATORS, 0).d(INIT).d(JUST).d(LEFT).d(LEFT).d(IDENTIFIER).done();
    final IdentifierP arg2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(
        DECLARATORS, 0).d(INIT).d(JUST).d(LEFT).d(RIGHT).d(IDENTIFIER).done();

    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f", new Pair<>(ImmutableList.list(fNode1), NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("arg1", new Pair<>(ImmutableList.list(arg1Node1), ImmutableList.list(arg1Node2)));
      variables.put("arg2", new Pair<>(ImmutableList.list(arg2Node1), ImmutableList.list(arg2Node2)));
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), ImmutableList.list(v1Node1)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(arg1Node2, Accessibility.Read);
      referenceTypes.put(arg2Node2, Accessibility.Read);
      referenceTypes.put(v1Node1, Accessibility.Write);

      checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testArgument2() throws JsError {
    String js = "function f() {var v1 = arguments[0] + ' world';}";
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope fScope = globalScope.children.maybeHead().just();
    final IdentifierP fNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(NAME).done();
    final IdentifierP v1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(
        DECLARATORS, 0).d(BINDING).done();
    final IdentifierP argumentsNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(
        DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(LEFT).d(OBJECT).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(fScope);

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("f", new Pair<>(ImmutableList.list(fNode1), NO_REFERENCES));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // function scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), ImmutableList.list(v1Node1)));
      variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.list(argumentsNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(v1Node1, Accessibility.Write);
      referenceTypes.put(argumentsNode1, Accessibility.Read);

      checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testWithStatement1() throws JsError {
    String js = "with (Math) {" + "  var x = cos(3 * PI);" + "  alert(x);" + '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope withScope = globalScope.children.maybeHead().just();
    final IdentifierP mathNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(OBJECT).d(IDENTIFIER).done();
    final IdentifierP xNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(BLOCK).d(STATEMENTS, 0).d(
        DECLARATION).d(DECLARATORS, 0).d(BINDING).done();
    final IdentifierP cosNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(BLOCK).d(STATEMENTS, 0).d(
        DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP piNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(BLOCK).d(STATEMENTS, 0).d(
        DECLARATION).d(DECLARATORS, 0).d(INIT).d(JUST).d(ARGUMENTS, 0).d(RIGHT).d(IDENTIFIER).done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(BLOCK).d(STATEMENTS, 1).d(
        EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP xNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(BLOCK).d(STATEMENTS, 1).d(
        EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();

    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(withScope);

      ImmutableList<String> through = ImmutableList.list("Math", "cos", "PI", "alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("Math", new Pair<>(NO_DECLARATIONS, ImmutableList.list(mathNode1)));
      variables.put("cos", new Pair<>(NO_DECLARATIONS, ImmutableList.list(cosNode1)));
      variables.put("PI", new Pair<>(NO_DECLARATIONS, ImmutableList.list(piNode1)));
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));
      variables.put("x", new Pair<>(ImmutableList.list(xNode1), ImmutableList.list(xNode1, xNode2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(mathNode1, Accessibility.Read);
      referenceTypes.put(cosNode1, Accessibility.Read);
      referenceTypes.put(piNode1, Accessibility.Read);
      referenceTypes.put(alertNode1, Accessibility.Read);
      referenceTypes.put(xNode1, Accessibility.Write);
      referenceTypes.put(xNode2, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // with scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("x", "cos", "PI", "alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(withScope, Scope.Type.With, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testWithStatement2() throws JsError {
    String js = "var o = {" +
        "  l1 : {" +
        "    l2 : {" +
        "      fld1 : 'hello'," +
        "      fld2 : 'world'," +
        "    }" +
        "  }" +
        "};" +
        "with (o.l1.l2) {" +
        "  alert(fld1);" +
        "  alert(fld2);" +
        '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope withScope = globalScope.children.maybeHead().just();
    final IdentifierP oNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(DECLARATION).d(DECLARATORS, 0).d(BINDING)
        .done();
    final IdentifierP oNode2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(OBJECT).d(OBJECT).d(OBJECT).d(IDENTIFIER)
        .done();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(BLOCK).d(STATEMENTS, 0).d(
        EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP fld1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(BLOCK).d(STATEMENTS, 0).d(
        EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    final IdentifierP alertNode2 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(BLOCK).d(STATEMENTS, 1).d(
        EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP fld2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 1).d(BODY).d(BLOCK).d(STATEMENTS, 1).d(
        EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(withScope);

      ImmutableList<String> through = ImmutableList.list("alert", "fld1", "fld2");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1, alertNode2)));
      variables.put("fld1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(fld1Node1)));
      variables.put("fld2", new Pair<>(NO_DECLARATIONS, ImmutableList.list(fld2Node1)));
      variables.put("o", new Pair<>(ImmutableList.list(oNode1), ImmutableList.list(oNode1, oNode2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(alertNode1, Accessibility.Read);
      referenceTypes.put(alertNode2, Accessibility.Read);
      referenceTypes.put(fld1Node1, Accessibility.Read);
      referenceTypes.put(fld2Node1, Accessibility.Read);
      referenceTypes.put(oNode1, Accessibility.Write);
      referenceTypes.put(oNode2, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // with scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("alert", "fld1", "fld2");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();

      checkScope(withScope, Scope.Type.With, true, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testTryCatchStatement1() throws JsError {
    String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  alert(err);" + '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope catchScope = globalScope.children.maybeHead().just();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        CALLEE).d(IDENTIFIER).done();
    final IdentifierP errNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BINDING).done();
    final IdentifierP alertNode2 = new Getter(script).d(BODY)
        .d(STATEMENTS, 0)
        .d(CATCHCLAUSE)
        .d(BODY)
        .d(STATEMENTS, 0)
        .d(
            EXPRESSION).d(CALLEE).d(IDENTIFIER).done();
    final IdentifierP errNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(catchScope);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1, alertNode2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(alertNode1, Accessibility.Read);
      referenceTypes.put(alertNode2, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // catch scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("err", new Pair<>(ImmutableList.list(errNode1), ImmutableList.list(errNode2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(errNode2, Accessibility.Read);

      checkScope(catchScope, Scope.Type.Catch, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testTryCatchStatement2() throws JsError {
    String js = "try {" +
        "  alert('Welcome guest!');" +
        "} catch(err1) {" +
        "  try {" +
        "    throw err1.message;" +
        "  } catch(err2) {" +
        "    alert(err1);" +
        "    alert(err2);" +
        "  }" +
        '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope catchScope1 = globalScope.children.maybeHead().just();
    Scope catchScope2 = catchScope1.children.maybeHead().just();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        CALLEE).d(IDENTIFIER).done();
    final IdentifierP err1Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BINDING).done();
    final IdentifierP err1Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        BODY)
        .d(STATEMENTS, 0).d(EXPRESSION).d(OBJECT).d(IDENTIFIER).done();
    final IdentifierP err2Node1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        CATCHCLAUSE)
        .d(BINDING).done();
    final IdentifierP alertNode2 = new Getter(script).d(BODY)
        .d(STATEMENTS, 0)
        .d(CATCHCLAUSE)
        .d(BODY)
        .d(STATEMENTS, 0)
        .d(
            CATCHCLAUSE)
        .d(BODY)
        .d(STATEMENTS, 0)
        .d(EXPRESSION)
        .d(CALLEE)
        .d(IDENTIFIER)
        .done();
    final IdentifierP err1Node3 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        CATCHCLAUSE)
        .d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    final IdentifierP alertNode3 = new Getter(script).d(BODY)
        .d(STATEMENTS, 0)
        .d(CATCHCLAUSE)
        .d(BODY)
        .d(STATEMENTS, 0)
        .d(
            CATCHCLAUSE)
        .d(BODY)
        .d(STATEMENTS, 1)
        .d(EXPRESSION)
        .d(CALLEE)
        .d(IDENTIFIER)
        .done();
    final IdentifierP err2Node2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        CATCHCLAUSE)
        .d(BODY).d(STATEMENTS, 1).d(EXPRESSION).d(ARGUMENTS, 0).d(IDENTIFIER).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(catchScope1);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1, alertNode2, alertNode3)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(alertNode1, Accessibility.Read);
      referenceTypes.put(alertNode2, Accessibility.Read);
      referenceTypes.put(alertNode3, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // catch scope 1

      ImmutableList<Scope> children = ImmutableList.list(catchScope2);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("err1", new Pair<>(ImmutableList.list(err1Node1), ImmutableList.list(err1Node2, err1Node3)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(err1Node2, Accessibility.Read);
      referenceTypes.put(err1Node3, Accessibility.Read);

      checkScope(catchScope1, Scope.Type.Catch, false, children, through, variables, referenceTypes);
    }
    { // catch scope 2

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.list("alert", "err1");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("err2", new Pair<>(ImmutableList.list(err2Node1), ImmutableList.list(err2Node2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(err2Node2, Accessibility.Read);

      checkScope(catchScope2, Scope.Type.Catch, false, children, through, variables, referenceTypes);
    }
  }

  @Test
  public void testTryCatchStatement3() throws JsError {
    String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  var err = 1;" + '}';
    Script script = parse(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);
    Scope catchScope = globalScope.children.maybeHead().just();
    final IdentifierP alertNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(BODY).d(STATEMENTS, 0).d(EXPRESSION).d(
        CALLEE).d(IDENTIFIER).done();
    final IdentifierP errNode1 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BINDING).done();
    final IdentifierP errNode2 = new Getter(script).d(BODY).d(STATEMENTS, 0).d(CATCHCLAUSE).d(BODY).d(STATEMENTS, 0).d(
        DECLARATION).d(DECLARATORS, 0).d(BINDING).done();
    { // global scope

      ImmutableList<Scope> children = ImmutableList.list(catchScope);

      ImmutableList<String> through = ImmutableList.list("alert");

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("err", new Pair<>(ImmutableList.list(errNode2), NO_REFERENCES));
      variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(alertNode1, Accessibility.Read);

      checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
    }
    { // catch scope

      ImmutableList<Scope> children = ImmutableList.nil();

      ImmutableList<String> through = ImmutableList.nil();

      // mapping of variable names from this scope object to the list of their declarations and their references
      Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables = new HashMap<>();
      variables.put("err", new Pair<>(ImmutableList.list(errNode1), ImmutableList.list(errNode2)));

      Map<IdentifierP, Accessibility> referenceTypes = new HashMap<>();
      referenceTypes.put(errNode2, Accessibility.Write);

      checkScope(catchScope, Scope.Type.Catch, false, children, through, variables, referenceTypes);
    }
  }

  /**
   * Check the given scope is correct based on the information provided
   */
  private static void checkScope(
      @NotNull final Scope scope,
      @NotNull final Scope.Type scopeType,
      final boolean isDynamic,
      @NotNull final ImmutableList<Scope> children,
      @NotNull final ImmutableList<String> through,
      @NotNull final Map<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variables,
      @NotNull final Map<IdentifierP, Accessibility> referenceTypes) {
    Assert.assertEquals(scope.type, scopeType);
    Assert.assertEquals(scope.dynamic, isDynamic);

    Assert.assertEquals(scope.children.length, children.length);
    children.foreach(child -> assertTrue(scope.children.exists(scope1 -> scope1 == child)));

    Assert.assertEquals(scope.through.length, through.length);
    through.foreach(name -> {
      HashTable<ImmutableList<Branch>, Reference> references = scope.through.get(name).just();
      Assert.assertNotNull(references);
      assertTrue(references.find(pair -> pair.b.node.name.equals(name)).isJust());
    });

    Assert.assertEquals(scope.variables().size(), variables.size());
    for (Map.Entry<String, Pair<ImmutableList<IdentifierP>, ImmutableList<IdentifierP>>> variableEntry : variables.entrySet()) {
      Maybe<Variable> maybeVariable = scope.lookupVariable(variableEntry.getKey());
      assertTrue(maybeVariable.isJust());
      Variable variable = maybeVariable.just();

      ImmutableList<IdentifierP> declarations = variableEntry.getValue().a;
      Assert.assertEquals(variable.declarations.length, declarations.length);
      for (final IdentifierP node : declarations) {
        assertTrue(variable.declarations.find(pair -> pair.b.path.equals(node.from) && pair.b.node
            .equals(node.node)).isJust());
      }

      ImmutableList<IdentifierP> refs = variableEntry.getValue().b;
      Assert.assertEquals(variable.references.length, refs.length);
      for (final IdentifierP node : refs) {
        Maybe<Reference> maybeRef = variable.references.find(
            pair -> pair.b.path.equals(node.from)
                && pair.b.node.equals(node.node)).map(pair -> pair.b);
        assertTrue(maybeRef.isJust());
        Reference ref = maybeRef.just();
        Assert.assertEquals(ref.node, node.node);
        assertTrue(ref.path.equals(node.from));
        Accessibility type = referenceTypes.get(new IdentifierP(ref.path, ref.node));
        Assert.assertNotNull(type);
        Assert.assertEquals(ref.accessibility, type);
      }
    }
  }

  private Script parse(String source) throws JsError {
    return Parser.parse(source);
  }
}
