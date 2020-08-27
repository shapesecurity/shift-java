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

package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.VariableReference;
import com.shapesecurity.shift.es2017.path.Branch;
import com.shapesecurity.shift.es2017.path.BranchGetter;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Test;

public class ScopeTest extends TestCase {

    private static final ImmutableList<VariableReference> NO_REFERENCES = ImmutableList.empty();
    private static final ImmutableList<BindingIdentifier> NO_DECLARATIONS = ImmutableList.empty();


    private static AssignmentTargetIdentifier ati(Maybe<? extends Node> n) {
        assertTrue("Node not located!", n.isJust());
        return (AssignmentTargetIdentifier) n.fromJust();
    }

    private static BindingIdentifier bi(Maybe<? extends Node> n) {
        assertTrue("Node not located!", n.isJust());
        return (BindingIdentifier) n.fromJust();
    }

    private static IdentifierExpression ie(Maybe<? extends Node> n) {
        assertTrue("Node not located!", n.isJust());
        return (IdentifierExpression) n.fromJust();
    }

    @Test
    public void testVariableDeclaration1() throws JsError {
        String js = "var v1; var v2 = 'hello';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(globalScope.children.maybeHead().fromJust());

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Binding1), NO_REFERENCES));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of(v2Binding1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(v2Binding1, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration2() throws JsError {
        String js = "var v1, v2 = 'hello';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(1)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(globalScope.children.maybeHead().fromJust());

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Binding1), NO_REFERENCES));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of(v2Binding1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(v2Binding1, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration3() throws JsError {
        String js = "v1 = 'hello'; var v2 = v1 + ' world';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final AssignmentTargetIdentifier v1Binding1 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.AssignmentExpressionBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(globalScope.children.maybeHead().fromJust());

            ImmutableList<String> through = ImmutableList.of("v1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(NO_DECLARATIONS, ImmutableList.of(v1Binding1, v1Identifier1)));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of(v2Binding1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(v1Binding1, Accessibility.Write);
            referenceTypes.put(v1Identifier1, Accessibility.Read);
            referenceTypes.put(v2Binding1, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration4() throws JsError {
        String js = "var v2 = v1 + ' world'; var v1 = 'hello'; ";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(globalScope.children.maybeHead().fromJust());

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Binding1), ImmutableList.of(v1Binding1, v1Identifier1)));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of(v2Binding1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put((v1Binding1), Accessibility.Write);
            referenceTypes.put((v1Identifier1), Accessibility.Read);
            referenceTypes.put((v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration5() throws JsError {
        String js = "var v1; var v1 = 'world'; var v2 = v1 + ' world';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v1Binding2 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(2)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(2)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(globalScope.children.maybeHead().fromJust());

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Binding1, v1Binding2), ImmutableList.of((v1Binding2), (v1Identifier1))));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of((v2Binding1))));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put((v1Binding2), Accessibility.Write);
            referenceTypes.put((v1Identifier1), Accessibility.Read);
            referenceTypes.put((v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testFunctionDeclarations1() throws JsError {
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope f1Scope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope f2Scope = topLevelLexicalScope.children.maybeHead().fromJust().children.maybeHead().fromJust();

        final BindingIdentifier f1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));

        final IdentifierExpression f1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_())
                .d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.CallExpressionCallee_())
                .apply(script));

        final BindingIdentifier f2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationName_())
                .apply(script));

        final IdentifierExpression f2Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(2)).d(Branch.ReturnStatementExpression_())
                .apply(script));

        final BindingIdentifier p1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationParams_())
                .d(Branch.FormalParametersItems_(0))
                .apply(script));

        final BindingIdentifier p1Binding2 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationParams_()).d(Branch.FormalParametersItems_(0))
                .apply(script));

        final IdentifierExpression p1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0))
                .d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier p2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationParams_())
                .d(Branch.FormalParametersItems_(1))
                .apply(script));

        final IdentifierExpression p2Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0))
                .d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionRight_())
                .apply(script));

        final BindingIdentifier rBinding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1))
                .d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v1Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
				Branch.VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0))
                .d(Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionRight_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0))
                .d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v2Identifier1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.FunctionDeclarationBody_())
                .d(Branch.FunctionBodyStatements_(1)).d(Branch.ReturnStatementExpression_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.of(f1Binding1), ImmutableList.of(f1Identifier1)));
            variables.put("r", new Pair<>(ImmutableList.of(rBinding1), ImmutableList.of((rBinding1))));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Identifier1, Accessibility.Read);
            referenceTypes.put(rBinding1, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f1 scope

            ImmutableList<Scope> children = ImmutableList.of(f2Scope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Binding1), ImmutableList.of(v1Binding1, v1Identifier1)));
            variables.put("p1", new Pair<>(ImmutableList.of(p1Binding1), NO_REFERENCES));
            variables.put("p2", new Pair<>(ImmutableList.of(p2Binding1), ImmutableList.of(p2Identifier1)));
            variables.put("f2", new Pair<>(ImmutableList.of(f2Binding1), ImmutableList.of(f2Identifier1)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(v1Binding1, Accessibility.Write);
            referenceTypes.put(v1Identifier1, Accessibility.Read);
            referenceTypes.put(p2Identifier1, Accessibility.Read);
            referenceTypes.put(f2Identifier1, Accessibility.Read);

            checkScope(f1Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // f2 scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("v1", "p2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("p1", new Pair<>(ImmutableList.of(p1Binding2), ImmutableList.of(p1Identifier1)));
            variables.put("v2", new Pair<>(ImmutableList.of(v2Binding1), ImmutableList.of(v2Binding1, v2Identifier1)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(p1Identifier1, Accessibility.Read);
            referenceTypes.put(v2Binding1, Accessibility.Write);
            referenceTypes.put(v2Identifier1, Accessibility.Read);

            checkScope(f2Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }

    }

    @Test
    public void testFunctionDeclaration2() throws JsError {
        String js = "function f() {f = 'hello';} f();";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier fNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final AssignmentTargetIdentifier fNode2 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression fNode3 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.of(fNode1), ImmutableList.of(fNode2, fNode3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fNode2, Accessibility.Write);
            referenceTypes.put(fNode3, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("f");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testFunctionExpression1() throws JsError {
        String js = "var f = function() {f = 'hello';}; f();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fScope = topLevelLexicalScope.children.maybeHead().fromJust();
        final BindingIdentifier fNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final AssignmentTargetIdentifier fNode2 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.FunctionExpressionBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression fNode3 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.of(fNode1), ImmutableList.of(fNode1, fNode2, fNode3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fNode1, Accessibility.Write);
            referenceTypes.put(fNode2, Accessibility.Write);
            referenceTypes.put(fNode3, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("f");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testFunctionExpression2() throws JsError {
        String js = "var f2 = function f1() {f1 = 'hello';}; f1(); f2();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope functionNameScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope functionScope = functionNameScope.children.maybeHead().fromJust();

        final BindingIdentifier f1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.FunctionExpressionName_())
                .apply(script));
        final AssignmentTargetIdentifier f1Node2 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.FunctionExpressionBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression f1Node3 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier f2Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression f2Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(2)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("f1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f2", new Pair<>(ImmutableList.of(f2Node1), ImmutableList.of(f2Node1, f2Node2)));
            variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.of(f1Node3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f2Node1, Accessibility.Write);
            referenceTypes.put(f2Node2, Accessibility.Read);
            referenceTypes.put(f1Node3, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function name scope

            ImmutableList<Scope> children = ImmutableList.of(functionScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.of(f1Node1), ImmutableList.of(f1Node2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Node2, Accessibility.Write);

            checkScope(functionNameScope, Scope.Type.FunctionName, false, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("f1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope ifBlockScope = functionScope.children.maybeHead().fromJust(); // did not exist in ES5

        final BindingIdentifier fooNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression fooNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.IfStatementTest_()).d(Branch.UnaryExpressionOperand_())
                .apply(script));
        final BindingIdentifier fooNode3 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.IfStatementConsequent_()).d(Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(
			Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression fooNode4 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));
        final BindingIdentifier barNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.of(fooNode1), ImmutableList.of(fooNode1)));
            variables.put("bar", new Pair<>(ImmutableList.of(barNode1), NO_REFERENCES));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fooNode1, Accessibility.Write);
            referenceTypes.put(alertNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.of(ifBlockScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.of(fooNode3), ImmutableList.of(fooNode2, fooNode3, fooNode4)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope bScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope aScope = bScope.children.maybeHead().fromJust();

        final BindingIdentifier aNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier bNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final AssignmentTargetIdentifier aNode2 = ati(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.AssignmentExpressionBinding_())
                .apply(script));
        final BindingIdentifier aNode3 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(2)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression bNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(2)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(3)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression aNode4 = ie(new BranchGetter().d(Branch.ScriptStatements_(3)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("a", new Pair<>(ImmutableList.of(aNode1), ImmutableList.of(aNode1, aNode4)));
            variables.put("b", new Pair<>(ImmutableList.of(bNode1), ImmutableList.of(bNode2)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(aNode1, Accessibility.Write);
            referenceTypes.put(aNode4, Accessibility.Read);
            referenceTypes.put(bNode2, Accessibility.Read);
            referenceTypes.put(alertNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // b scope

            ImmutableList<Scope> children = ImmutableList.of(aScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("a", new Pair<>(ImmutableList.of(aNode3), ImmutableList.of(aNode2)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(aNode2, Accessibility.Write);

            checkScope(bScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // a scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope barScope1 = fooScope.children.maybeHead().fromJust();
        Scope barScope2 = fooScope.children.maybeTail().fromJust().maybeHead().fromJust();

        final BindingIdentifier fooNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier barNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression barNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(1)).d(Branch.ReturnStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier barNode3 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(2)).d(Branch.FunctionDeclarationName_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.of(fooNode1), NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.of(barScope1, barScope2);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("bar", new Pair<>(ImmutableList.of(barNode1, barNode3), ImmutableList.of(barNode2)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(barNode2, Accessibility.Read);

            checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar1 scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar2 scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testHoistDeclaration4() throws JsError {
        String js = "foo(); function foo() {}";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().fromJust();
        final IdentifierExpression fooNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier fooNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.FunctionDeclarationName_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.of(fooNode2), ImmutableList.of(fooNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fooNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope barScope1 = fooScope.children.maybeHead().fromJust();
        Scope barScope2 = fooScope.children.maybeTail().fromJust().maybeHead().fromJust();

        final BindingIdentifier fooNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression barNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.ReturnStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier barNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier barNode3 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(2)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.of(fooNode1), NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.of(barScope1, barScope2);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("bar", new Pair<>(
                    ImmutableList.of(barNode2, barNode3), ImmutableList.of(
                    barNode1,
                    barNode2,
                    barNode3)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(barNode1, Accessibility.Read);
            referenceTypes.put(barNode2, Accessibility.Write);
            referenceTypes.put(barNode3, Accessibility.Write);

            checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar scope 1

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar scope 2

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testClosure1() throws JsError {
        String js = "(function() {f1 = 'hello'; alert(f1);})();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final AssignmentTargetIdentifier f1Node1 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression f1Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("f1", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.of(f1Node1, f1Node2)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Node1, Accessibility.Write);
            referenceTypes.put(f1Node2, Accessibility.Read);
            referenceTypes.put(alertNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("f1", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testClosure2() throws JsError {
        String js = "(function() {var f1 = 'hello'; alert(f1);})();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier f1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression f1Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionCallee_()).d(Branch.FunctionExpressionBody_()).d(Branch.FunctionBodyStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.of(f1Node1), ImmutableList.of(f1Node1, f1Node2)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier fNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier arg1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationParams_()).d(
			Branch.FormalParametersItems_(0))
                .apply(script));
        final BindingIdentifier arg2Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationParams_()).d(
			Branch.FormalParametersItems_(1))
                .apply(script));
        final BindingIdentifier v1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression arg1Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));
        final IdentifierExpression arg2Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionRight_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.of(fNode1), NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arg1", new Pair<>(ImmutableList.of(arg1Node1), ImmutableList.of(arg1Node2)));
            variables.put("arg2", new Pair<>(ImmutableList.of(arg2Node1), ImmutableList.of(arg2Node2)));
            variables.put("v1", new Pair<>(ImmutableList.of(v1Node1), ImmutableList.of(v1Node1)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope fScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier fNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier v1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression argumentsNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.FunctionDeclarationBody_()).d(
			Branch.FunctionBodyStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorInit_()).d(Branch.BinaryExpressionLeft_()).d(Branch.ComputedMemberExpressionObject_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.of(fNode1), NO_REFERENCES));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.of(v1Node1), ImmutableList.of(v1Node1)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.of(argumentsNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope withScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope withBlockScope = withScope.children.maybeHead().fromJust(); // did not exist in ES5

        final IdentifierExpression mathNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementObject_())
                .apply(script));
        final BindingIdentifier xNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression cosNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression piNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.CallExpressionArguments_(0)).d(
			Branch.BinaryExpressionRight_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression xNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("Math", "cos", "PI", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("Math", new Pair<>(NO_DECLARATIONS, ImmutableList.of(mathNode1)));
            variables.put("cos", new Pair<>(NO_DECLARATIONS, ImmutableList.of(cosNode1)));
            variables.put("PI", new Pair<>(NO_DECLARATIONS, ImmutableList.of(piNode1)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));
            variables.put("x", new Pair<>(ImmutableList.of(xNode1), ImmutableList.of(xNode1, xNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(mathNode1, Accessibility.Read);
            referenceTypes.put(cosNode1, Accessibility.Read);
            referenceTypes.put(piNode1, Accessibility.Read);
            referenceTypes.put(alertNode1, Accessibility.Read);
            referenceTypes.put(xNode1, Accessibility.Write);
            referenceTypes.put(xNode2, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // with scope

            ImmutableList<Scope> children = ImmutableList.of(withBlockScope);

            ImmutableList<String> through = ImmutableList.of("x", "cos", "PI", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope withScope = topLevelLexicalScope.children.maybeHead().fromJust();
        Scope withBlockScope = withScope.children.maybeHead().fromJust(); // did not exist in ES5

        final BindingIdentifier oNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression oNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.WithStatementObject_()).d(
			Branch.StaticMemberExpressionObject_()).d(Branch.StaticMemberExpressionObject_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression fld1Node1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression fld2Node1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.WithStatementBody_()).d(
			Branch.BlockStatementBlock_()).d(Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert", "fld1", "fld2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1, alertNode2)));
            variables.put("fld1", new Pair<>(NO_DECLARATIONS, ImmutableList.of(fld1Node1)));
            variables.put("fld2", new Pair<>(NO_DECLARATIONS, ImmutableList.of(fld2Node1)));
            variables.put("o", new Pair<>(ImmutableList.of(oNode1), ImmutableList.of(oNode1, oNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1, Accessibility.Read);
            referenceTypes.put(alertNode2, Accessibility.Read);
            referenceTypes.put(fld1Node1, Accessibility.Read);
            referenceTypes.put(fld2Node1, Accessibility.Read);
            referenceTypes.put(oNode1, Accessibility.Write);
            referenceTypes.put(oNode2, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // with scope

            ImmutableList<Scope> children = ImmutableList.of(withBlockScope);

            ImmutableList<String> through = ImmutableList.of("alert", "fld1", "fld2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(withScope, Scope.Type.With, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testTryCatchStatement1() throws JsError {
        String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  alert(err);" + '}';
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope tryBlockScope = topLevelLexicalScope.children.maybeHead().fromJust(); // did not exist in ES5
        Scope catchScope = topLevelLexicalScope.children.index(1).fromJust();
        Scope catchBlockScope = catchScope.children.maybeHead().fromJust(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementBody_()).d(
			Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier errNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression errNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1, alertNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1, Accessibility.Read);
            referenceTypes.put(alertNode2, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope

            ImmutableList<Scope> children = ImmutableList.of(catchBlockScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.of(errNode1), ImmutableList.of(errNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope tryBlockScope1 = topLevelLexicalScope.children.maybeHead().fromJust(); // did not exist in ES5
        Scope catchScope1 = topLevelLexicalScope.children.index(1).fromJust();
        Scope catchBlockScope1 = catchScope1.children.maybeHead().fromJust(); // did not exist in ES5
        Scope tryBlockScope2 = catchBlockScope1.children.maybeHead().fromJust(); // did not exist in ES5
        Scope catchScope2 = catchBlockScope1.children.index(1).fromJust();
        Scope catchBlockScope2 = catchScope2.children.maybeHead().fromJust(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementBody_()).d(
			Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier err1Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression err1Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementBody_()).d(Branch.BlockStatements_(0)).d(
			Branch.ThrowStatementExpression_()).d(Branch.StaticMemberExpressionObject_())
                .apply(script));
        final BindingIdentifier err2Node1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(Branch.CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(Branch.CatchClauseBody_()).d(
			Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression err1Node3 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(Branch.CatchClauseBody_()).d(
			Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));
        final IdentifierExpression alertNode3 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(Branch.CatchClauseBody_()).d(
			Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression err2Node2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(Branch.CatchClauseBody_()).d(
			Branch.BlockStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionArguments_(0))
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1, alertNode2, alertNode3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1, Accessibility.Read);
            referenceTypes.put(alertNode2, Accessibility.Read);
            referenceTypes.put(alertNode3, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope 1

            ImmutableList<Scope> children = ImmutableList.of(catchBlockScope1);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("err1", new Pair<>(ImmutableList.of(err1Node1), ImmutableList.of(err1Node2, err1Node3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(err1Node2, Accessibility.Read);
            referenceTypes.put(err1Node3, Accessibility.Read);

            checkScope(catchScope1, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
        { // catch scope 2

            ImmutableList<Scope> children = ImmutableList.of(catchBlockScope2);

            ImmutableList<String> through = ImmutableList.of("alert", "err1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("err2", new Pair<>(ImmutableList.of(err2Node1), ImmutableList.of(err2Node2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(err2Node2, Accessibility.Read);

            checkScope(catchScope2, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testTryCatchStatement3() throws JsError {
        String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  var err = 1;" + '}';
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope tryBlockScope = topLevelLexicalScope.children.maybeHead().fromJust(); // did not exist in ES5
        Scope catchScope = topLevelLexicalScope.children.index(1).fromJust();
        Scope catchBlockScope = catchScope.children.maybeHead().fromJust(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementBody_()).d(
			Branch.BlockStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(Branch.CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier errNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBinding_())
                .apply(script));
        final BindingIdentifier errNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.TryCatchStatementCatchClause_()).d(
			Branch.CatchClauseBody_()).d(Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch
			.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.of(errNode2), NO_REFERENCES));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.of(alertNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope

            ImmutableList<Scope> children = ImmutableList.of(catchBlockScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.of(errNode1), ImmutableList.of(errNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(errNode2, Accessibility.Write);

            checkScope(catchScope, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArrow1() throws JsError {
        String js = "var x = x => ++x";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope aScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier xNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier xNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.ArrowExpressionParams_()).d(
			Branch.FormalParametersItems_(0))
                .apply(script));
        final AssignmentTargetIdentifier xNode3 = ati(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.ArrowExpressionBody_()).d(
			Branch.UpdateExpressionOperand_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode1), ImmutableList.of(xNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // arrow scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode2), ImmutableList.of(xNode3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode3, Accessibility.ReadWrite);

            checkScope(aScope, Scope.Type.ArrowFunction, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArrowArguments() throws JsError {
        String js = "() => arguments";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope aScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final IdentifierExpression argumentsNode = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_()).d(
			Branch.ArrowExpressionBody_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.of("arguments");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.of(argumentsNode)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(argumentsNode, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // arrow scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("arguments");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();

            checkScope(aScope, Scope.Type.ArrowFunction, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testGetter() throws JsError {
        String js = "var x = {get [x]() {return x + arguments;}};";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope gScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final BindingIdentifier xNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression xNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.ObjectExpressionProperties_(0)).d(
			Branch.GetterName_()).d(Branch.ComputedPropertyNameExpression_())
                .apply(script));
        final IdentifierExpression xNode3 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.ObjectExpressionProperties_(0)).d(
			Branch.GetterBody_()).d(Branch.FunctionBodyStatements_(0)).d(Branch.ReturnStatementExpression_()).d(Branch.BinaryExpressionLeft_())
                .apply(script));
        final IdentifierExpression argumentsNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_()).d(Branch.ObjectExpressionProperties_(0)).d(
			Branch.GetterBody_()).d(Branch.FunctionBodyStatements_(0)).d(Branch.ReturnStatementExpression_()).d(Branch.BinaryExpressionRight_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode1), ImmutableList.of(xNode1, xNode2, xNode3)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1, Accessibility.Write);
            referenceTypes.put(xNode2, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // getter scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("x");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.of(argumentsNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(argumentsNode1, Accessibility.Read);

            checkScope(gScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testBlockDeclarations() throws JsError {
        String js = "x; {const x = y;}; var x, y;";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();
        Scope blockScope = topLevelLexicalScope.children.maybeHead().fromJust();

        final IdentifierExpression xNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.ExpressionStatementExpression_())
                .apply(script));
        final BindingIdentifier xNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.BlockStatementBlock_()).d(
			Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression yNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.BlockStatementBlock_()).d(
			Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorInit_())
                .apply(script));
        final BindingIdentifier xNode3 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.BlockStatementBlock_()).d(
			Branch.BlockStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(Branch.VariableDeclarationDeclarators_(0)).d(
			Branch.VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier yNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(3)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(1)).d(Branch.VariableDeclaratorBinding_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode3), ImmutableList.of(xNode1)));
            variables.put("y", new Pair<>(ImmutableList.of(yNode2), ImmutableList.of(yNode1)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1, Accessibility.Read);
            referenceTypes.put(yNode1, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // block scope

            ImmutableList<Scope> children = ImmutableList.empty();

            ImmutableList<String> through = ImmutableList.of("y");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode2), ImmutableList.of(xNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode2, Accessibility.Write);

            checkScope(blockScope, Scope.Type.Block, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testDestructuring() throws JsError {
        String js = "var {x, a:{b:y = z}} = null; var [z] = y;";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().fromJust();

        final BindingIdentifier xNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_()).d(Branch.ObjectBindingProperties_(0)).d(
			Branch.BindingPropertyIdentifierBinding_())
                .apply(script));
        final BindingIdentifier yNode1 = bi(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_()).d(Branch.ObjectBindingProperties_(1)).d(
			Branch.BindingPropertyPropertyBinding_()).d(Branch.ObjectBindingProperties_(0)).d(Branch.BindingPropertyPropertyBinding_()).d(
			Branch.BindingWithDefaultBinding_())
                .apply(script));
        final IdentifierExpression zNode1 = ie(new BranchGetter().d(Branch.ScriptStatements_(0)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_()).d(Branch.ObjectBindingProperties_(1)).d(
			Branch.BindingPropertyPropertyBinding_()).d(Branch.ObjectBindingProperties_(0)).d(Branch.BindingPropertyPropertyBinding_()).d(
			Branch.BindingWithDefaultInit_())
                .apply(script));
        final BindingIdentifier zNode2 = bi(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorBinding_()).d(Branch.ArrayBindingElements_(0))
                .apply(script));
        final IdentifierExpression yNode2 = ie(new BranchGetter().d(Branch.ScriptStatements_(1)).d(Branch.VariableDeclarationStatementDeclaration_()).d(
			Branch.VariableDeclarationDeclarators_(0)).d(Branch.VariableDeclaratorInit_())
                .apply(script));

        { // global scope

            ImmutableList<Scope> children = ImmutableList.of(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.empty();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.of(xNode1), ImmutableList.of(xNode1)));
            variables.put("y", new Pair<>(ImmutableList.of(yNode1), ImmutableList.of(yNode1, yNode2)));
            variables.put("z", new Pair<>(ImmutableList.of(zNode2), ImmutableList.of(zNode1, zNode2)));

            Map<VariableReference, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1, Accessibility.Write);
            referenceTypes.put(yNode1, Accessibility.Write);
            referenceTypes.put(yNode2, Accessibility.Read);
            referenceTypes.put(zNode1, Accessibility.Read);
            referenceTypes.put(zNode2, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }


    @Test
    public void testScope_binding() throws JsError {
        checkScopeSerialization(
                "function foo(b){function r(){for(var b=0;;);}}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"foo\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(foo)_2\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"b\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(b)_4\", \"kind\": \"Parameter\"}]}, {\"name\": \"r\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(r)_7\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_6\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"b\", \"references\": [{\"node\": \"BindingIdentifier(b)_13\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(b)_13\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"ForStatement_10\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(b)_13\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}]}]}]}"
        );
    }

    @Test
    public void testScope_shorthand() throws JsError {
        checkScopeSerialization(
                "({x})",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(x)_4\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"IdentifierExpression(x)_4\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(x)_4\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}]}"
        );
    }

    @Test
    public void testFunctionDoubleDeclaration() throws JsError {
        checkScopeSerialization(
                "{let x; function x(){}}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Block_2\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"x\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(x)_6\", \"kind\": \"Let\"}, {\"node\": \"BindingIdentifier(x)_8\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_7\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}"
        );

        checkScopeSerialization(
                "function f1(x){return x; function x(){}}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"f1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_2\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"x\", \"references\": [{\"node\": \"IdentifierExpression(x)_7\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(x)_4\", \"kind\": \"Parameter\"}, {\"node\": \"BindingIdentifier(x)_9\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_8\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}"
        );

        checkScopeSerialization(
                "function x(){}; var x = 1; function x(){}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"BindingIdentifier(x)_9\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(x)_9\", \"kind\": \"Var\"}, {\"node\": \"BindingIdentifier(x)_2\", \"kind\": \"FunctionDeclaration\"}, {\"node\": \"BindingIdentifier(x)_12\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(x)_9\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"FunctionDeclaration_11\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}"
        );

        checkScopeSerialization(
                "function f3() {return arguments; function arguments(){}}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"f3\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f3)_2\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [{\"node\": \"IdentifierExpression(arguments)_6\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(arguments)_8\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_7\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}"
        );
    }

    @Test
    public void testParameterScope() throws JsError {
        checkScopeSerialization(
                "!function(x){let y;};",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"x\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(x)_5\", \"kind\": \"Parameter\"}]}, {\"name\": \"y\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(y)_10\", \"kind\": \"Let\"}]}], \"children\": []}]}]}"
        );

        checkScopeSerialization(
                "!function(x = 1){let y;};",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Parameters\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"x\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(x)_6\", \"kind\": \"Parameter\"}]}], \"children\": [{\"node\": \"BindingWithDefault_5\", \"type\": \"ParameterExpression\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": []}, {\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"y\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(y)_12\", \"kind\": \"Let\"}]}], \"children\": []}]}]}]}"
        );

        checkScopeSerialization(
                "!function(x, y = () => (x,y,z)){let z;};",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"z\", \"references\": [{\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Parameters\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"IdentifierExpression(x)_12\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(x)_5\", \"kind\": \"Parameter\"}]}, {\"name\": \"y\", \"references\": [{\"node\": \"IdentifierExpression(y)_13\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(y)_7\", \"kind\": \"Parameter\"}]}], \"children\": [{\"node\": \"BindingWithDefault_6\", \"type\": \"ParameterExpression\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(x)_12\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(y)_13\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"ArrowExpression_8\", \"type\": \"ArrowFunction\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(x)_12\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(y)_13\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(z)_14\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}]}, {\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"z\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(z)_19\", \"kind\": \"Let\"}]}], \"children\": []}]}]}]}"
        );
    }

    @Test
    public void testB33() throws JsError {
        checkScopeSerialization(
                "(function() {" +
                        "function getOuter(){return f;}" +
                        "var g;" +
                        "{" +
                        "   f = 1;" +
                        "   function f(){}" +
                        "   g = f;" +
                        "}" +
                        "})();",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"FunctionB33\"}]}, {\"name\": \"g\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(g)_15\", \"kind\": \"Var\"}]}, {\"name\": \"getOuter\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(getOuter)_7\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_6\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"Block_17\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"f\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(f)_20\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f)_29\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_22\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}"
        );

        checkScopeSerialization(
                "!function f() {\n" +
                        "  {\n" +
                        "    function f(){}\n" +
                        "  }\n" +
                        "  {\n" +
                        "    function f(){}\n" +
                        "  }\n" +
                        "  f;\n" +
                        "}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"FunctionName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_4\", \"kind\": \"FunctionExpressionName\"}]}], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_20\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_10\", \"kind\": \"FunctionB33\"}, {\"node\": \"BindingIdentifier(f)_16\", \"kind\": \"FunctionB33\"}]}], \"children\": [{\"node\": \"Block_8\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_10\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_9\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}, {\"node\": \"Block_14\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_16\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_15\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}]}"
        );

        checkScopeSerialization( // As above, but as a module. Because B.3.3 only applies in strict mode, this case is substantially different from the previous.
                "!function f() {\n" +
                        "  {\n" +
                        "    function f(){}\n" +
                        "  }\n" +
                        "  {\n" +
                        "    function f(){}\n" +
                        "  }\n" +
                        "  f;\n" +
                        "}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"FunctionName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_20\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_4\", \"kind\": \"FunctionExpressionName\"}]}], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f)_20\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": [{\"node\": \"Block_8\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_10\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_9\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}, {\"node\": \"Block_14\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_16\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_15\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}]}",
                false
        );

        checkScopeSerialization(
                "!function f() {\n" +
                        "  if (0)\n" +
                        "    function f(){}\n" +
                        "  else\n" +
                        "    function f(){}\n" +
                        "  f;\n" +
                        "}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"FunctionName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_4\", \"kind\": \"FunctionExpressionName\"}]}], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_18\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_10\", \"kind\": \"FunctionDeclaration\"}, {\"node\": \"BindingIdentifier(f)_14\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_9\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"FunctionDeclaration_13\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}"
        );

        checkScopeSerialization(
                "!function(){\n" +
                        "  {\n" +
                        "    {\n" +
                        "      let f;\n" +
                        "      {\n" +
                        "        function f(){}\n" +
                        "      }\n" +
                        "    }\n" +
                        "    function f(){}\n" +
                        "  }\n" +
                        "  f;\n" +
                        "}",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_25\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_21\", \"kind\": \"FunctionB33\"}]}], \"children\": [{\"node\": \"Block_7\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_21\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Block_9\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_13\", \"kind\": \"Let\"}]}], \"children\": [{\"node\": \"Block_15\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_17\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_16\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}, {\"node\": \"FunctionDeclaration_20\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}"
        );
    }

    @Test
    public void testImport() throws JsError {
        checkScopeSerialization(
                "import a, {b} from \"\"",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"a\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(a)_2\", \"kind\": \"Import\"}]}, {\"name\": \"b\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(b)_4\", \"kind\": \"Import\"}]}], \"children\": []}]}",
                false
        );
    }

    @Test
    public void testImportNamespace() throws JsError {
        checkScopeSerialization(
                "import * as ns from \"test.js\"",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"ns\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(ns)_2\", \"kind\": \"Import\"}]}], \"children\": []}]}",
                false
        );
    }

    @Test
    public void testClass() throws JsError {
        checkScopeSerialization(
                "class C{}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(C)_2\", \"kind\": \"ClassDeclaration\"}]}], \"children\": [{\"node\": \"ClassDeclaration_1\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(C)_2\", \"kind\": \"ClassName\"}]}], \"children\": []}]}]}",
                false
        );

        checkScopeSerialization(
                "class C extends (()=>C, C, null) {f(){return C;}} C;",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_18\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(C)_2\", \"kind\": \"ClassDeclaration\"}]}], \"children\": [{\"node\": \"ClassDeclaration_1\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_7\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_8\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_16\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(C)_2\", \"kind\": \"ClassName\"}]}], \"children\": [{\"node\": \"ArrowExpression_5\", \"type\": \"ArrowFunction\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_7\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}, {\"node\": \"Method_11\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_16\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}",
                false
        );

        checkScopeSerialization(
                "(class{})",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"ClassExpression_2\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": []}]}]}",
                false
        );

        checkScopeSerialization(
                "(class C{})",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"ClassExpression_2\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(C)_3\", \"kind\": \"ClassName\"}]}], \"children\": []}]}]}",
                false
        );

        checkScopeSerialization(
                "(class C extends (()=>C, C, null) {f(){return C;}}); C;",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(C)_19\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_19\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_19\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"ClassExpression_2\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_8\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_9\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_17\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(C)_3\", \"kind\": \"ClassName\"}]}], \"children\": [{\"node\": \"ArrowExpression_6\", \"type\": \"ArrowFunction\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_8\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}, {\"node\": \"Method_12\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_17\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}",
                false
        );
    }

    @Test
    public void testExportDefault() throws JsError {
        checkScopeSerialization(
                "export default class {}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"ClassDeclaration_2\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": []}]}]}",
                false
        );

        checkScopeSerialization(
                "export default class C extends (()=>C, C, null) {f(){return C;}} C;",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_19\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(C)_3\", \"kind\": \"ClassDeclaration\"}]}], \"children\": [{\"node\": \"ClassDeclaration_2\", \"type\": \"ClassName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"C\", \"references\": [{\"node\": \"IdentifierExpression(C)_8\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_9\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(C)_17\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(C)_3\", \"kind\": \"ClassName\"}]}], \"children\": [{\"node\": \"ArrowExpression_6\", \"type\": \"ArrowFunction\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_8\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}, {\"node\": \"Method_12\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(C)_17\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}",
                false
        );

        checkScopeSerialization(
                "export default function() {}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_2\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}",
                false
        );

        checkScopeSerialization(
                "export default function f() {}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(f)_3\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_2\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}",
                false
        );
    }

    @Test
    public void testExport() throws JsError {
        checkScopeSerialization(
                "export {a}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"a\", \"references\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}]}",
                false
        );

        checkScopeSerialization(
                "export {a as b}",
                "{\"node\": \"Module_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"a\", \"references\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Module_0\", \"type\": \"Module\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(a)_3\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": []}]}",
                false
        );
    }

    @Test
    public void testAssignmentTarget() throws JsError {
        checkScopeSerialization(
                "x = 0",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_3\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(x)_3\", \"accessibility\": \"Write\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_3\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}"
        );

        checkScopeSerialization(
                "[x] = 0",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_4\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(x)_4\", \"accessibility\": \"Write\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_4\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}"
        );

        checkScopeSerialization(
                "({x} = 0)",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}"
        );

        checkScopeSerialization(
                "[x = x] = 0",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(x)_6\", \"accessibility\": \"Read\"}, {\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(x)_6\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(x)_6\", \"accessibility\": \"Read\"}, {\"node\": \"AssignmentTargetIdentifier(x)_5\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}"
        );

        checkScopeSerialization(
                "for (x in 0) ;",
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_2\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"x\", \"references\": [{\"node\": \"AssignmentTargetIdentifier(x)_2\", \"accessibility\": \"Write\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_2\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": [{\"node\": \"ForInStatement_1\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [{\"node\": \"AssignmentTargetIdentifier(x)_2\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}]}"
        );
    }

    private static String getIdentifierName(@Nonnull Node node) {
        if (node instanceof AssignmentTargetIdentifier) {
            return ((AssignmentTargetIdentifier) node).name;
        } else if (node instanceof BindingIdentifier) {
            return ((BindingIdentifier) node).name;
        } else if (node instanceof IdentifierExpression) {
            return ((IdentifierExpression) node).name;
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    /**
     * Check the given scope is correct based on the information provided
     */
    private static void checkScope(
            @Nonnull final Scope scope,
            @Nonnull final Scope.Type scopeType,
            final boolean isDynamic,
            @Nonnull final ImmutableList<Scope> children,
            @Nonnull final ImmutableList<String> through,
            @Nonnull final Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variables,
            @Nonnull final Map<VariableReference, Accessibility> referenceTypes) {
        Assert.assertEquals(scope.type, scopeType);
        Assert.assertEquals(scope.dynamic, isDynamic);

        Assert.assertEquals(scope.children.length, children.length);
        children.forEach(child -> assertTrue(scope.children.exists(scope1 -> scope1 == child)));

        // scope.through.foreach(e -> System.out.println(e.a)); // TODO remove this

        Assert.assertEquals(scope.through.length, through.length);
        through.forEach(name -> {
            ImmutableList<Reference> references = scope.through.get(name).fromJust();
            Assert.assertNotNull(references);
            assertTrue(references.find(ref -> getIdentifierName(ref.node).equals(name)).isJust());
        });

        Assert.assertEquals(scope.variables().size(), variables.size());
        for (Map.Entry<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<VariableReference>>> variableEntry : variables.entrySet()) {
            Maybe<Variable> maybeVariable = scope.lookupVariable(variableEntry.getKey());
            assertTrue(maybeVariable.isJust());
            Variable variable = maybeVariable.fromJust();

            ImmutableList<BindingIdentifier> declarations = variableEntry.getValue().left();
            Assert.assertEquals(variable.declarations.length, declarations.length);
            for (final BindingIdentifier node : declarations) {
                assertTrue(variable.declarations.find(decl -> decl.node.equals(node)).isJust());
            }

            ImmutableList<VariableReference> refs = variableEntry.getValue().right();
            Assert.assertEquals(variable.references.length, refs.length);
            for (final VariableReference nodeE : refs) {
                Maybe<Reference> maybeRef = variable.references.find(
                        ref -> ref.node.equals(nodeE));
                assertTrue(maybeRef.isJust());
                Reference ref = maybeRef.fromJust();
                Assert.assertEquals(ref.node, nodeE); // comparing Eithers does the correct thing
                Accessibility type = referenceTypes.get(nodeE);
                Assert.assertNotNull(type);
                Assert.assertEquals(ref.accessibility, type);
            }
        }
    }

    private void checkScopeSerialization(String js, String serialization) throws JsError {
        checkScopeSerialization(js, serialization, true);
    }

    private void checkScopeSerialization(String js, String serialization, boolean asScript) throws JsError {
        // get scope tree
        GlobalScope globalScope;
        if (asScript) {
            Script script = Parser.parseScript(js);
            globalScope = ScopeAnalyzer.analyze(script);
        } else {
            Module module = Parser.parseModule(js);
            globalScope = ScopeAnalyzer.analyze(module);
        }

        // get serialization of scope tree
        String serialized = ScopeSerializer.serialize(globalScope);

        // check
        TestCase.assertEquals(serialization, serialized);
    }

    private Script parse(String source) throws JsError {
        return Parser.parseScript(source);
    }
}
