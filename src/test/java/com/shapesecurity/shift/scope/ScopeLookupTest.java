package com.shapesecurity.shift.scope;

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

import com.shapesecurity.functional.Effect;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.Unit;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.visitor.Flattener;
import junit.framework.TestCase;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ScopeLookupTest {
    private static void assertThrows(Effect<Unit> f) {
        try {
            f.apply(Unit.unit);
            TestCase.fail("Did not error!");
        } catch(Exception ignored) {
            // pass
        }
    }

    private static ImmutableList<Node> getNodes(Node node) {
        ImmutableList<Node> nodes;
        if (node instanceof Script) {
            nodes = Flattener.flatten((Script) node);
        } else if (node instanceof Module) {
            nodes = Flattener.flatten((Module) node);
        } else {
            throw new RuntimeException("Node does not correspond to script or module");
        }
        return nodes;
    }

    private static List<Variable> getVariables(Scope scope) {
        List<Variable> list = new LinkedList<>();
        getVariables(scope, list);
        return list;
    }

    private static void getVariables(Scope scope, final List<Variable> list) {
        list.addAll(scope.variables());
        scope.children.foreach(s -> getVariables(s, list));
    }

    private static List<Scope> getScopes(Scope scope) {
        List<Scope> list = new LinkedList<>();
        getScopes(scope, list);
        return list;
    }

    private static void getScopes(Scope scope, final List<Scope> list) {
        scope.children.forEach(list::add);
        scope.children.foreach(s -> getScopes(s, list));
    }

    private static boolean isDescendant(Scope parent, Scope child) {
        if (parent == child) {
            return true;
        }
        return parent.children.exists(c -> isDescendant(c, child));
    }

    private static void checkScopeLookupSanity(final GlobalScope scope, final ScopeLookup lookup) {
        ImmutableList<Node> nodes = getNodes(scope.astNode);
        List<Variable> variables = getVariables(scope);
        List<Scope> scopes = getScopes(scope);

        for (Node node : nodes) {
            if (node instanceof BindingIdentifier) {
                final BindingIdentifier bi = (BindingIdentifier) node;
                Maybe<Variable> mv = lookup.findVariableDeclaredBy(bi);
                TestCase.assertTrue(mv.isNothing() || mv.fromJust().declarations.exists(decl -> decl.node == bi));
                Maybe<Variable> mv2 = lookup.findVariableReferencedBy(bi);
                TestCase.assertTrue(mv2.isNothing() || mv2.fromJust().references.exists(ref -> ref.node.isLeft() && ref.node.left().fromJust() == bi));
                if (bi.name.equals("*default*")) {
                    TestCase.assertTrue(mv.isNothing() && mv2.isNothing());
                } else {
                    TestCase.assertTrue(mv.isJust() || mv2.isJust());
                }
            } else if (node instanceof IdentifierExpression) {
                final IdentifierExpression ie = (IdentifierExpression) node;
                Variable v = lookup.findVariableReferencedBy(ie);
                TestCase.assertTrue(v.references.exists(ref -> ref.node.isRight() && ref.node.right().fromJust() == ie));
            } else if (node instanceof FunctionDeclaration) {
                final FunctionDeclaration func = (FunctionDeclaration) node;
                if (func.name.name.equals("*default*")) {
                    assertThrows(Unit -> lookup.findVariablesForFuncDecl(func));
                } else {
                    Pair<Variable, Maybe<Variable>> vars = lookup.findVariablesForFuncDecl(func);
                    TestCase.assertTrue(vars.left.declarations.exists(decl -> decl.node == func.name));
                    if (vars.right.isJust()) {
                        TestCase.assertTrue(vars.right.fromJust().declarations.exists(decl -> decl.node == func.name));
                    }
                }
            } else if (node instanceof ClassDeclaration) {
                final ClassDeclaration cl = (ClassDeclaration) node;
                if (cl.name.name.equals("*default*")) {
                    assertThrows(Unit -> lookup.findVariablesForClassDecl(cl));
                } else {
                    Pair<Variable, Variable> vars = lookup.findVariablesForClassDecl(cl);
                    TestCase.assertTrue(vars.left.declarations.exists(decl -> decl.node == cl.name));
                    TestCase.assertTrue(vars.right.declarations.exists(decl -> decl.node == cl.name));
                }
            }

            Maybe<Scope> sc = lookup.findScopeFor(node);
            if (sc.isJust()) {
                TestCase.assertTrue(sc.fromJust().astNode == node);
            }
        }

        for (Variable variable : variables) {
            for (Declaration decl : variable.declarations) {
                if (decl.kind == Declaration.Kind.FunctionDeclaration || decl.kind == Declaration.Kind.FunctionB33) {
                    FunctionDeclaration func = null;
                    for (Node node : nodes) {
                        if (node instanceof FunctionDeclaration && ((FunctionDeclaration) node).name == decl.node) {
                            func = (FunctionDeclaration) node;
                            break;
                        }
                    }
                    TestCase.assertNotNull(func);

                    Pair<Variable, Maybe<Variable>> vars = lookup.findVariablesForFuncDecl(func);
                    if (decl.kind == Declaration.Kind.FunctionB33) {
                        TestCase.assertTrue(vars.right.isJust() && vars.right.fromJust() == variable);
                    } else {
                        TestCase.assertTrue(vars.left == variable);
                    }
                } else if (decl.kind == Declaration.Kind.ClassDeclaration || decl.kind == Declaration.Kind.ClassName) {
                    ClassDeclaration cl = null;
                    boolean isClassExpr = false;
                    for (Node node : nodes) {
                        if (node instanceof ClassDeclaration && ((ClassDeclaration) node).name == decl.node) {
                            cl = (ClassDeclaration) node;
                            break;
                        } else if (node instanceof ClassExpression && ((ClassExpression) node).name.isJust() && ((ClassExpression) node).name.fromJust() == decl.node) {
                            isClassExpr = true;
                            break;
                        }
                    }

                    if (isClassExpr) {
                        TestCase.assertTrue(variable == lookup.findVariableDeclaredBy(decl.node).fromJust());
                    } else {
                        TestCase.assertNotNull(cl);
                        Pair<Variable, Variable> vars = lookup.findVariablesForClassDecl(cl);
                        TestCase.assertTrue(vars.left == variable || vars.right == variable);
                    }
                } else {
                    TestCase.assertTrue(variable == lookup.findVariableDeclaredBy(decl.node).fromJust());
                }
            }

            for (Reference ref : variable.references) {
                if (ref.node.isLeft()) {
                    Maybe<Variable> mv = lookup.findVariableReferencedBy(ref.node.left().fromJust());
                    TestCase.assertTrue(mv.isJust() && variable == mv.fromJust());
                } else {
                    TestCase.assertTrue(variable == lookup.findVariableReferencedBy(ref.node.right().fromJust()));
                }
            }
        }

        for (Scope sc : scopes) {
            TestCase.assertTrue(isDescendant(lookup.findScopeFor(sc.astNode).fromJust(), sc));
            /*
            findScopeFor returns the outermost scope associated with a node, so it is not necessarily
            the case that sc.astNode == lookup.findScopeFor(sc.astNode).fromJust(). However, there should
            be a unique outermost scope, so sc should be a descendant of lookup.findScopeFor(sc.astNode).fromJust().
             */
        }
    }

    private static void checkScopeLookupSanity(String js) throws JsError {
        checkScopeLookupSanity(js, true);
    }

    private static void checkScopeLookupSanity(String js, boolean asScript) throws JsError {
        GlobalScope globalScope;
        if (asScript) {
            Script script = Parser.parseScript(js);
            globalScope = ScopeAnalyzer.analyze(script);
        } else {
            Module module = Parser.parseModule(js);
            globalScope = ScopeAnalyzer.analyze(module);
        }

        ScopeLookup lookup = new ScopeLookup(globalScope);
        checkScopeLookupSanity(globalScope, lookup);
    }

    @Test
    public void testVariables() throws JsError {
        checkScopeLookupSanity("var a, b = 1; let c, d = 1; const e, f = 1; a, b, c, d, e, f, g; a = b = c = d = e = f = g = 0;");
    }

    @Test
    public void testFunctions() throws JsError {
        checkScopeLookupSanity("!function(){};");
        checkScopeLookupSanity("!function f(){};");
        checkScopeLookupSanity("function g(){};");
        checkScopeLookupSanity("(() => { { function h(){}; h; } h; });");
    }

    @Test
    public void testClasses() throws JsError {
        checkScopeLookupSanity("class C{} C;");
        checkScopeLookupSanity("class C extends (C, null) { f(){C;} } C;");
        checkScopeLookupSanity("(class{});");
        checkScopeLookupSanity("(class C{}); C;");
        checkScopeLookupSanity("(class C extends (C, null) { f(){C;} }); C;");
    }

    @Test
    public void testExportDefault() throws JsError {
        checkScopeLookupSanity("export default class {}", false);
        checkScopeLookupSanity("export default class C{}", false);
        checkScopeLookupSanity("export default function(){}", false);
        checkScopeLookupSanity("export default function f(){}", false);
    }
}
