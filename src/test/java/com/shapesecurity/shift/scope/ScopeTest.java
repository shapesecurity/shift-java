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

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.path.Getter;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import static com.shapesecurity.shift.path.Branch.*;

public class ScopeTest extends TestCase {

    private static final ImmutableList<Either<BindingIdentifier, IdentifierExpression>> NO_REFERENCES = ImmutableList.nil();
    private static final ImmutableList<BindingIdentifier> NO_DECLARATIONS = ImmutableList.nil();


    private static BindingIdentifier bi(Maybe<? extends Node> n) {
        assertTrue("Node not located!", n.isJust());
        return (BindingIdentifier) n.just();
    }

    private static IdentifierExpression ie(Maybe<? extends Node> n) {
        assertTrue("Node not located!", n.isJust());
        return (IdentifierExpression) n.just();
    }

    @Test
    public void testVariableDeclaration1() throws JsError {
        String js = "var v1; var v2 = 'hello';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(globalScope.children.maybeHead().just());

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Binding1), NO_REFERENCES));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration2() throws JsError {
        String js = "var v1, v2 = 'hello';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(1)).d(VariableDeclaratorBinding_())
                .apply(script));

        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(globalScope.children.maybeHead().just());

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Binding1), NO_REFERENCES));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration3() throws JsError {
        String js = "v1 = 'hello'; var v2 = v1 + ' world';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(globalScope.children.maybeHead().just());

            ImmutableList<String> through = ImmutableList.list("v1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(Either.left(v1Binding1), Either.right(v1Identifier1))));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v1Binding1), Accessibility.Write);
            referenceTypes.put(Either.right(v1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration4() throws JsError {
        String js = "var v2 = v1 + ' world'; var v1 = 'hello'; ";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(globalScope.children.maybeHead().just());

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Binding1), ImmutableList.list(Either.left(v1Binding1), Either.right(v1Identifier1))));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v1Binding1), Accessibility.Write);
            referenceTypes.put(Either.right(v1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testVariableDeclaration5() throws JsError {
        String js = "var v1; var v1 = 'world'; var v2 = v1 + ' world';";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v1Binding2 = bi(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new Getter().d(ScriptStatements_(2)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(2)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(globalScope.children.maybeHead().just());

            ImmutableList<String> through = ImmutableList.list();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Binding1, v1Binding2), ImmutableList.list(Either.left(v1Binding2), Either.right(v1Identifier1))));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v1Binding2), Accessibility.Write);
            referenceTypes.put(Either.right(v1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope f1Scope = topLevelLexicalScope.children.maybeHead().just();
        Scope f2Scope = topLevelLexicalScope.children.maybeHead().just().children.maybeHead().just();

        final BindingIdentifier f1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));

        final IdentifierExpression f1Identifier1 = ie(new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_())
                .d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(CallExpressionCallee_())
                .apply(script));

        final BindingIdentifier f2Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationName_())
                .apply(script));

        final IdentifierExpression f2Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(2)).d(ReturnStatementExpression_())
                .apply(script));

        final BindingIdentifier p1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationParams_())
                .d(FormalParametersItems_(0))
                .apply(script));

        final BindingIdentifier p1Binding2 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationParams_()).d(FormalParametersItems_(0))
                .apply(script));

        final IdentifierExpression p1Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0))
                .d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_()).d(BinaryExpressionLeft_())
                .apply(script));

        final BindingIdentifier p2Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationParams_())
                .d(FormalParametersItems_(1))
                .apply(script));

        final IdentifierExpression p2Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0))
                .d(VariableDeclaratorInit_()).d(BinaryExpressionRight_())
                .apply(script));

        final BindingIdentifier rBinding1 = bi(new Getter().d(ScriptStatements_(1))
                .d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final BindingIdentifier v1Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v1Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0))
                .d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_()).d(BinaryExpressionRight_())
                .apply(script));

        final BindingIdentifier v2Binding1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0))
                .d(VariableDeclaratorBinding_())
                .apply(script));

        final IdentifierExpression v2Identifier1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(FunctionDeclarationBody_())
                .d(FunctionBodyStatements_(1)).d(ReturnStatementExpression_())
                .apply(script));


        { // global scope
            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.list(f1Binding1), ImmutableList.list(Either.right(f1Identifier1))));
            variables.put("r", new Pair<>(ImmutableList.list(rBinding1), ImmutableList.list(Either.left(rBinding1))));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.right(f1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.left(rBinding1), Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f1 scope

            ImmutableList<Scope> children = ImmutableList.list(f2Scope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Binding1), ImmutableList.list(Either.left(v1Binding1), Either.right(v1Identifier1))));
            variables.put("p1", new Pair<>(ImmutableList.list(p1Binding1), NO_REFERENCES));
            variables.put("p2", new Pair<>(ImmutableList.list(p2Binding1), ImmutableList.list(Either.right(p2Identifier1))));
            variables.put("f2", new Pair<>(ImmutableList.list(f2Binding1), ImmutableList.list(Either.right(f2Identifier1))));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.left(v1Binding1), Accessibility.Write);
            referenceTypes.put(Either.right(v1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.right(p2Identifier1), Accessibility.Read);
            referenceTypes.put(Either.right(f2Identifier1), Accessibility.Read);

            checkScope(f1Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // f2 scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("v1", "p2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("p1", new Pair<>(ImmutableList.list(p1Binding2), ImmutableList.list(Either.right(p1Identifier1))));
            variables.put("v2", new Pair<>(ImmutableList.list(v2Binding1), ImmutableList.list(Either.left(v2Binding1), Either.right(v2Identifier1))));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(Either.right(p1Identifier1), Accessibility.Read);
            referenceTypes.put(Either.left(v2Binding1), Accessibility.Write);
            referenceTypes.put(Either.right(v2Identifier1), Accessibility.Read);

            checkScope(f2Scope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }

    }

    @Test
    public void testFunctionDeclaration2() throws JsError {
        String js = "function f() {f = 'hello';} f();";
        Script script = parse(js);

        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier fNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier fNode2 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression fNode3 = ie(new Getter().d(ScriptStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fNode1E = Either.left(fNode1);
        final Either<BindingIdentifier, IdentifierExpression> fNode2E = Either.left(fNode2);
        final Either<BindingIdentifier, IdentifierExpression> fNode3E = Either.right(fNode3);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.list(fNode1), ImmutableList.list(fNode2E, fNode3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fNode2E, Accessibility.Write);
            referenceTypes.put(fNode3E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("f");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testFunctionExpression1() throws JsError {
        String js = "var f = function() {f = 'hello';}; f();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fScope = topLevelLexicalScope.children.maybeHead().just();
        final BindingIdentifier fNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier fNode2 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression fNode3 = ie(new Getter().d(ScriptStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fNode1E = Either.left(fNode1);
        final Either<BindingIdentifier, IdentifierExpression> fNode2E = Either.left(fNode2);
        final Either<BindingIdentifier, IdentifierExpression> fNode3E = Either.right(fNode3);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.list(fNode1), ImmutableList.list(fNode1E, fNode2E, fNode3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fNode1E, Accessibility.Write);
            referenceTypes.put(fNode2E, Accessibility.Write);
            referenceTypes.put(fNode3E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // f scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("f");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testFunctionExpression2() throws JsError {
        String js = "var f2 = function f1() {f1 = 'hello';}; f1(); f2();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope functionNameScope = topLevelLexicalScope.children.maybeHead().just();
        Scope functionScope = functionNameScope.children.maybeHead().just();

        final BindingIdentifier f1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(FunctionExpressionName_())
                .apply(script));
        final BindingIdentifier f1Node2 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression f1Node3 = ie(new Getter().d(ScriptStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier f2Node1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression f2Node2 = ie(new Getter().d(ScriptStatements_(2)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> f1Node1E = Either.left(f1Node1);
        final Either<BindingIdentifier, IdentifierExpression> f1Node2E = Either.left(f1Node2);
        final Either<BindingIdentifier, IdentifierExpression> f1Node3E = Either.right(f1Node3);
        final Either<BindingIdentifier, IdentifierExpression> f2Node1E = Either.left(f2Node1);
        final Either<BindingIdentifier, IdentifierExpression> f2Node2E = Either.right(f2Node2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("f1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f2", new Pair<>(ImmutableList.list(f2Node1), ImmutableList.list(f2Node1E, f2Node2E)));
            variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(f1Node3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f2Node1E, Accessibility.Write);
            referenceTypes.put(f2Node2E, Accessibility.Read);
            referenceTypes.put(f1Node3E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function name scope

            ImmutableList<Scope> children = ImmutableList.list(functionScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.list(f1Node1), ImmutableList.list(f1Node2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Node2E, Accessibility.Write);

            checkScope(functionNameScope, Scope.Type.FunctionName, false, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("f1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().just();
        Scope ifBlockScope = functionScope.children.maybeHead().just(); // did not exist in ES5

        final BindingIdentifier fooNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression fooNode2 = ie(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(IfStatementTest_()).d(UnaryExpressionOperand_())
                .apply(script));
        final BindingIdentifier fooNode3 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(IfStatementConsequent_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression fooNode4 = ie(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));
        final BindingIdentifier barNode1 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fooNode1E = Either.left(fooNode1);
        final Either<BindingIdentifier, IdentifierExpression> fooNode2E = Either.right(fooNode2);
        final Either<BindingIdentifier, IdentifierExpression> fooNode3E = Either.left(fooNode3);
        final Either<BindingIdentifier, IdentifierExpression> fooNode4E = Either.right(fooNode4);
        final Either<BindingIdentifier, IdentifierExpression> barNode1E = Either.left(barNode1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), ImmutableList.list(fooNode1E)));
            variables.put("bar", new Pair<>(ImmutableList.list(barNode1), NO_REFERENCES));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fooNode1E, Accessibility.Write);
            referenceTypes.put(alertNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.list(ifBlockScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.list(fooNode3), ImmutableList.list(fooNode2E, fooNode3E, fooNode4E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fooNode2E, Accessibility.Read);
            referenceTypes.put(fooNode3E, Accessibility.Write);
            referenceTypes.put(fooNode4E, Accessibility.Read);

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope bScope = topLevelLexicalScope.children.maybeHead().just();
        Scope aScope = bScope.children.maybeHead().just();

        final BindingIdentifier aNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier bNode1 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier aNode2 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));
        final BindingIdentifier aNode3 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(2)).d(FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression bNode2 = ie(new Getter().d(ScriptStatements_(2)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(3)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression aNode4 = ie(new Getter().d(ScriptStatements_(3)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> aNode1E = Either.left(aNode1);
        final Either<BindingIdentifier, IdentifierExpression> bNode1E = Either.left(bNode1);
        final Either<BindingIdentifier, IdentifierExpression> aNode2E = Either.left(aNode2);
        final Either<BindingIdentifier, IdentifierExpression> aNode3E = Either.left(aNode3);
        final Either<BindingIdentifier, IdentifierExpression> bNode2E = Either.right(bNode2);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> aNode4E = Either.right(aNode4);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("a", new Pair<>(ImmutableList.list(aNode1), ImmutableList.list(aNode1E, aNode4E)));
            variables.put("b", new Pair<>(ImmutableList.list(bNode1), ImmutableList.list(bNode2E)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(aNode1E, Accessibility.Write);
            referenceTypes.put(aNode4E, Accessibility.Read);
            referenceTypes.put(bNode2E, Accessibility.Read);
            referenceTypes.put(alertNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // b scope

            ImmutableList<Scope> children = ImmutableList.list(aScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("a", new Pair<>(ImmutableList.list(aNode3), ImmutableList.list(aNode2E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(aNode2E, Accessibility.Write);

            checkScope(bScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // a scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().just();
        Scope barScope1 = fooScope.children.maybeHead().just();
        Scope barScope2 = fooScope.children.maybeTail().just().maybeHead().just();

        final BindingIdentifier fooNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier barNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression barNode2 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(1)).d(ReturnStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier barNode3 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(2)).d(FunctionDeclarationName_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fooNode1E = Either.left(fooNode1);
        final Either<BindingIdentifier, IdentifierExpression> barNode1E = Either.left(barNode1);
        final Either<BindingIdentifier, IdentifierExpression> barNode2E = Either.right(barNode2);
        final Either<BindingIdentifier, IdentifierExpression> barNode3E = Either.left(barNode3);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.list(barScope1, barScope2);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("bar", new Pair<>(ImmutableList.list(barNode1, barNode3), ImmutableList.list(barNode2E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(barNode2E, Accessibility.Read);

            checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar1 scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar2 scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testHoistDeclaration4() throws JsError {
        String js = "foo(); function foo() {}";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().just();
        final IdentifierExpression fooNode1 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier fooNode2 = bi(new Getter().d(ScriptStatements_(1)).d(FunctionDeclarationName_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fooNode1E = Either.right(fooNode1);
        final Either<BindingIdentifier, IdentifierExpression> fooNode2E = Either.left(fooNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.list(fooNode2), ImmutableList.list(fooNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(fooNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fooScope = topLevelLexicalScope.children.maybeHead().just();
        Scope barScope1 = fooScope.children.maybeHead().just();
        Scope barScope2 = fooScope.children.maybeTail().just().maybeHead().just();

        final BindingIdentifier fooNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final IdentifierExpression barNode1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(ReturnStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier barNode2 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier barNode3 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(2)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fooNode1E = Either.left(fooNode1);
        final Either<BindingIdentifier, IdentifierExpression> barNode1E = Either.right(barNode1);
        final Either<BindingIdentifier, IdentifierExpression> barNode2E = Either.left(barNode2);
        final Either<BindingIdentifier, IdentifierExpression> barNode3E = Either.left(barNode3);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("foo", new Pair<>(ImmutableList.list(fooNode1), NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // foo scope

            ImmutableList<Scope> children = ImmutableList.list(barScope1, barScope2);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("bar", new Pair<>(
                    ImmutableList.list(barNode2, barNode3), ImmutableList.list(
                    barNode1E,
                    barNode2E,
                    barNode3E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(barNode1E, Accessibility.Read);
            referenceTypes.put(barNode2E, Accessibility.Write);
            referenceTypes.put(barNode3E, Accessibility.Write);

            checkScope(fooScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar scope 1

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope1, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
        { // bar scope 2

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(barScope2, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testClosure1() throws JsError {
        String js = "(function() {f1 = 'hello'; alert(f1);})();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier f1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(0)).d(ExpressionStatementExpression_()).d(AssignmentExpressionBinding_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression f1Node2 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> f1Node1E = Either.left(f1Node1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> f1Node2E = Either.right(f1Node2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("f1", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(f1Node1E, f1Node2E)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Node1E, Accessibility.Write);
            referenceTypes.put(f1Node2E, Accessibility.Read);
            referenceTypes.put(alertNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("f1", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testClosure2() throws JsError {
        String js = "(function() {var f1 = 'hello'; alert(f1);})();";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope functionScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier f1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression f1Node2 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_()).d(FunctionExpressionBody_()).d(FunctionBodyStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> f1Node1E = Either.left(f1Node1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> f1Node2E = Either.right(f1Node2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f1", new Pair<>(ImmutableList.list(f1Node1), ImmutableList.list(f1Node1E, f1Node2E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(f1Node1E, Accessibility.Write);
            referenceTypes.put(f1Node2E, Accessibility.Read);

            checkScope(functionScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArgument1() throws JsError {
        String js = "function f(arg1, arg2) {var v1 = arg1 + arg2 + ' world';}";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier fNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier arg1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationParams_()).d(FormalParametersItems_(0))
                .apply(script));
        final BindingIdentifier arg2Node1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationParams_()).d(FormalParametersItems_(1))
                .apply(script));
        final BindingIdentifier v1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression arg1Node2 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_()).d(BinaryExpressionLeft_())
                .apply(script));
        final IdentifierExpression arg2Node2 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_()).d(BinaryExpressionRight_())
                .apply(script));


        final Either<BindingIdentifier, IdentifierExpression> fNode1E = Either.left(fNode1);
        final Either<BindingIdentifier, IdentifierExpression> arg1Node1E = Either.left(arg1Node1);
        final Either<BindingIdentifier, IdentifierExpression> arg2Node1E = Either.left(arg2Node1);
        final Either<BindingIdentifier, IdentifierExpression> v1Node1E = Either.left(v1Node1);
        final Either<BindingIdentifier, IdentifierExpression> arg1Node2E = Either.right(arg1Node2);
        final Either<BindingIdentifier, IdentifierExpression> arg2Node2E = Either.right(arg2Node2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.list(fNode1), NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arg1", new Pair<>(ImmutableList.list(arg1Node1), ImmutableList.list(arg1Node2E)));
            variables.put("arg2", new Pair<>(ImmutableList.list(arg2Node1), ImmutableList.list(arg2Node2E)));
            variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), ImmutableList.list(v1Node1E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(arg1Node2E, Accessibility.Read);
            referenceTypes.put(arg2Node2E, Accessibility.Read);
            referenceTypes.put(v1Node1E, Accessibility.Write);

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArgument2() throws JsError {
        String js = "function f() {var v1 = arguments[0] + ' world';}";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope fScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier fNode1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationName_())
                .apply(script));
        final BindingIdentifier v1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression argumentsNode1 = ie(new Getter().d(ScriptStatements_(0)).d(FunctionDeclarationBody_()).d(FunctionBodyStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(BinaryExpressionLeft_()).d(ComputedMemberExpressionObject_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> fNode1E = Either.left(fNode1);
        final Either<BindingIdentifier, IdentifierExpression> v1Node1E = Either.left(v1Node1);
        final Either<BindingIdentifier, IdentifierExpression> argumentsNode1E = Either.right(argumentsNode1);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("f", new Pair<>(ImmutableList.list(fNode1), NO_REFERENCES));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // function scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("v1", new Pair<>(ImmutableList.list(v1Node1), ImmutableList.list(v1Node1E)));
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.list(argumentsNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(v1Node1E, Accessibility.Write);
            referenceTypes.put(argumentsNode1E, Accessibility.Read);

            checkScope(fScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testWithStatement1() throws JsError {
        String js = "with (Math) {" + "  var x = cos(3 * PI);" + "  alert(x);" + '}';
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope withScope = topLevelLexicalScope.children.maybeHead().just();
        Scope withBlockScope = withScope.children.maybeHead().just(); // did not exist in ES5

        final IdentifierExpression mathNode1 = ie(new Getter().d(ScriptStatements_(0)).d(WithStatementObject_())
                .apply(script));
        final BindingIdentifier xNode1 = bi(new Getter().d(ScriptStatements_(0)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression cosNode1 = ie(new Getter().d(ScriptStatements_(0)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression piNode1 = ie(new Getter().d(ScriptStatements_(0)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(CallExpressionArguments_(0)).d(BinaryExpressionRight_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression xNode2 = ie(new Getter().d(ScriptStatements_(0)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> mathNode1E = Either.right(mathNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode1E = Either.left(xNode1);
        final Either<BindingIdentifier, IdentifierExpression> cosNode1E = Either.right(cosNode1);
        final Either<BindingIdentifier, IdentifierExpression> piNode1E = Either.right(piNode1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode2E = Either.right(xNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("Math", "cos", "PI", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("Math", new Pair<>(NO_DECLARATIONS, ImmutableList.list(mathNode1E)));
            variables.put("cos", new Pair<>(NO_DECLARATIONS, ImmutableList.list(cosNode1E)));
            variables.put("PI", new Pair<>(NO_DECLARATIONS, ImmutableList.list(piNode1E)));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));
            variables.put("x", new Pair<>(ImmutableList.list(xNode1), ImmutableList.list(xNode1E, xNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(mathNode1E, Accessibility.Read);
            referenceTypes.put(cosNode1E, Accessibility.Read);
            referenceTypes.put(piNode1E, Accessibility.Read);
            referenceTypes.put(alertNode1E, Accessibility.Read);
            referenceTypes.put(xNode1E, Accessibility.Write);
            referenceTypes.put(xNode2E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // with scope

            ImmutableList<Scope> children = ImmutableList.list(withBlockScope);

            ImmutableList<String> through = ImmutableList.list("x", "cos", "PI", "alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope withScope = topLevelLexicalScope.children.maybeHead().just();
        Scope withBlockScope = withScope.children.maybeHead().just(); // did not exist in ES5

        final BindingIdentifier oNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression oNode2 = ie(new Getter().d(ScriptStatements_(1)).d(WithStatementObject_()).d(StaticMemberExpressionObject_()).d(StaticMemberExpressionObject_())
                .apply(script));
        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(1)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression fld1Node1 = ie(new Getter().d(ScriptStatements_(1)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new Getter().d(ScriptStatements_(1)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression fld2Node1 = ie(new Getter().d(ScriptStatements_(1)).d(WithStatementBody_()).d(BlockStatementBlock_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> oNode1E = Either.left(oNode1);
        final Either<BindingIdentifier, IdentifierExpression> oNode2E = Either.right(oNode2);
        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> fld1Node1E = Either.right(fld1Node1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode2E = Either.right(alertNode2);
        final Either<BindingIdentifier, IdentifierExpression> fld2Node1E = Either.right(fld2Node1);


        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert", "fld1", "fld2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E, alertNode2E)));
            variables.put("fld1", new Pair<>(NO_DECLARATIONS, ImmutableList.list(fld1Node1E)));
            variables.put("fld2", new Pair<>(NO_DECLARATIONS, ImmutableList.list(fld2Node1E)));
            variables.put("o", new Pair<>(ImmutableList.list(oNode1), ImmutableList.list(oNode1E, oNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1E, Accessibility.Read);
            referenceTypes.put(alertNode2E, Accessibility.Read);
            referenceTypes.put(fld1Node1E, Accessibility.Read);
            referenceTypes.put(fld2Node1E, Accessibility.Read);
            referenceTypes.put(oNode1E, Accessibility.Write);
            referenceTypes.put(oNode2E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // with scope

            ImmutableList<Scope> children = ImmutableList.list(withBlockScope);

            ImmutableList<String> through = ImmutableList.list("alert", "fld1", "fld2");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(withScope, Scope.Type.With, true, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testTryCatchStatement1() throws JsError {
        String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  alert(err);" + '}';
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope tryBlockScope = topLevelLexicalScope.children.maybeHead().just(); // did not exist in ES5
        Scope catchScope = topLevelLexicalScope.children.index(1).just();
        Scope catchBlockScope = catchScope.children.maybeHead().just(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier errNode1 = bi(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression errNode2 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> errNode1E = Either.left(errNode1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode2E = Either.right(alertNode2);
        final Either<BindingIdentifier, IdentifierExpression> errNode2E = Either.right(errNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E, alertNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1E, Accessibility.Read);
            referenceTypes.put(alertNode2E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope

            ImmutableList<Scope> children = ImmutableList.list(catchBlockScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.list(errNode1), ImmutableList.list(errNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(errNode2E, Accessibility.Read);

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
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope tryBlockScope1 = topLevelLexicalScope.children.maybeHead().just(); // did not exist in ES5
        Scope catchScope1 = topLevelLexicalScope.children.index(1).just();
        Scope catchBlockScope1 = catchScope1.children.maybeHead().just(); // did not exist in ES5
        Scope tryBlockScope2 = catchBlockScope1.children.maybeHead().just(); // did not exist in ES5
        Scope catchScope2 = catchBlockScope1.children.index(1).just();
        Scope catchBlockScope2 = catchScope2.children.maybeHead().just(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier err1Node1 = bi(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression err1Node2 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementBody_()).d(BlockStatements_(0)).d(ThrowStatementExpression_()).d(StaticMemberExpressionObject_())
                .apply(script));
        final BindingIdentifier err2Node1 = bi(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBinding_())
                .apply(script));
        final IdentifierExpression alertNode2 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression err1Node3 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));
        final IdentifierExpression alertNode3 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final IdentifierExpression err2Node2 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(1)).d(ExpressionStatementExpression_()).d(CallExpressionArguments_(0))
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> err1Node1E = Either.left(err1Node1);
        final Either<BindingIdentifier, IdentifierExpression> err1Node2E = Either.right(err1Node2);
        final Either<BindingIdentifier, IdentifierExpression> err2Node1E = Either.left(err2Node1);
        final Either<BindingIdentifier, IdentifierExpression> alertNode2E = Either.right(alertNode2);
        final Either<BindingIdentifier, IdentifierExpression> err1Node3E = Either.right(err1Node3);
        final Either<BindingIdentifier, IdentifierExpression> alertNode3E = Either.right(alertNode3);
        final Either<BindingIdentifier, IdentifierExpression> err2Node2E = Either.right(err2Node2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E, alertNode2E, alertNode3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1E, Accessibility.Read);
            referenceTypes.put(alertNode2E, Accessibility.Read);
            referenceTypes.put(alertNode3E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope 1

            ImmutableList<Scope> children = ImmutableList.list(catchBlockScope1);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("err1", new Pair<>(ImmutableList.list(err1Node1), ImmutableList.list(err1Node2E, err1Node3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(err1Node2E, Accessibility.Read);
            referenceTypes.put(err1Node3E, Accessibility.Read);

            checkScope(catchScope1, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
        { // catch scope 2

            ImmutableList<Scope> children = ImmutableList.list(catchBlockScope2);

            ImmutableList<String> through = ImmutableList.list("alert", "err1");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("err2", new Pair<>(ImmutableList.list(err2Node1), ImmutableList.list(err2Node2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(err2Node2E, Accessibility.Read);

            checkScope(catchScope2, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testTryCatchStatement3() throws JsError {
        String js = "try {" + "  alert('Welcome guest!');" + "} catch(err) {" + "  var err = 1;" + '}';
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope tryBlockScope = topLevelLexicalScope.children.maybeHead().just(); // did not exist in ES5
        Scope catchScope = topLevelLexicalScope.children.index(1).just();
        Scope catchBlockScope = catchScope.children.maybeHead().just(); // did not exist in ES5

        final IdentifierExpression alertNode1 = ie(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementBody_()).d(BlockStatements_(0)).d(ExpressionStatementExpression_()).d(CallExpressionCallee_())
                .apply(script));
        final BindingIdentifier errNode1 = bi(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBinding_())
                .apply(script));
        final BindingIdentifier errNode2 = bi(new Getter().d(ScriptStatements_(0)).d(TryCatchStatementCatchClause_()).d(CatchClauseBody_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> alertNode1E = Either.right(alertNode1);
        final Either<BindingIdentifier, IdentifierExpression> errNode1E = Either.left(errNode1);
        final Either<BindingIdentifier, IdentifierExpression> errNode2E = Either.left(errNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("alert");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.list(errNode2), NO_REFERENCES));
            variables.put("alert", new Pair<>(NO_DECLARATIONS, ImmutableList.list(alertNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(alertNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // catch scope

            ImmutableList<Scope> children = ImmutableList.list(catchBlockScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("err", new Pair<>(ImmutableList.list(errNode1), ImmutableList.list(errNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(errNode2E, Accessibility.Write);

            checkScope(catchScope, Scope.Type.Catch, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArrow1() throws JsError {
        String js = "var x = x => ++x";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope aScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier xNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier xNode2 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(ArrowExpressionParams_()).d(FormalParametersItems_(0))
                .apply(script));
        final BindingIdentifier xNode3 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(ArrowExpressionBody_()).d(UpdateExpressionOperand_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> xNode1E = Either.left(xNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode2E = Either.left(xNode2);
        final Either<BindingIdentifier, IdentifierExpression> xNode3E = Either.left(xNode3);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode1), ImmutableList.list(xNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1E, Accessibility.Write);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // arrow scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode2), ImmutableList.list(xNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode2E, Accessibility.ReadWrite);

            checkScope(aScope, Scope.Type.ArrowFunction, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testArrowArguments() throws JsError {
        String js = "() => arguments";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope aScope = topLevelLexicalScope.children.maybeHead().just();

        final IdentifierExpression argumentsNode = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_()).d(ArrowExpressionBody_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> argumentsNodeE = Either.right(argumentsNode);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.list("arguments");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.list(argumentsNodeE)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(argumentsNodeE, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // arrow scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("arguments");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();

            checkScope(aScope, Scope.Type.ArrowFunction, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testGetter() throws JsError {
        String js = "var x = {get [x]() {return x + arguments;}};";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope gScope = topLevelLexicalScope.children.maybeHead().just();

        final BindingIdentifier xNode1 = bi(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression xNode2 = ie(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(ObjectExpressionProperties_(0)).d(GetterName_()).d(ComputedPropertyNameExpression_())
                .apply(script));
        final IdentifierExpression xNode3 = ie(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(ObjectExpressionProperties_(0)).d(GetterBody_()).d(FunctionBodyStatements_(0)).d(ReturnStatementExpression_()).d(BinaryExpressionLeft_())
                .apply(script));
        final IdentifierExpression argumentsNode1 = ie(new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_()).d(ObjectExpressionProperties_(0)).d(GetterBody_()).d(FunctionBodyStatements_(0)).d(ReturnStatementExpression_()).d(BinaryExpressionRight_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> xNode1E = Either.left(xNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode2E = Either.right(xNode2);
        final Either<BindingIdentifier, IdentifierExpression> xNode3E = Either.right(xNode3);
        final Either<BindingIdentifier, IdentifierExpression> argumentsNode1E = Either.right(argumentsNode1);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode1), ImmutableList.list(xNode1E, xNode2E, xNode3E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1E, Accessibility.Write);
            referenceTypes.put(xNode2E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // getter scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("x");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("arguments", new Pair<>(NO_DECLARATIONS, ImmutableList.list(argumentsNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(argumentsNode1E, Accessibility.Read);

            checkScope(gScope, Scope.Type.Function, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testBlockDeclarations() throws JsError {
        String js = "x; {const x = y;}; var x, y;";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();
        Scope blockScope = topLevelLexicalScope.children.maybeHead().just();

        final IdentifierExpression xNode1 = ie(new Getter().d(ScriptStatements_(0)).d(ExpressionStatementExpression_())
                .apply(script));
        final BindingIdentifier xNode2 = bi(new Getter().d(ScriptStatements_(1)).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final IdentifierExpression yNode1 = ie(new Getter().d(ScriptStatements_(1)).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_())
                .apply(script));
        final BindingIdentifier xNode3 = bi(new Getter().d(ScriptStatements_(1)).d(BlockStatementBlock_()).d(BlockStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_())
                .apply(script));
        final BindingIdentifier yNode2 = bi(new Getter().d(ScriptStatements_(3)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(1)).d(VariableDeclaratorBinding_())
                .apply(script));

        final Either<BindingIdentifier, IdentifierExpression> xNode1E = Either.right(xNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode2E = Either.left(xNode2);
        final Either<BindingIdentifier, IdentifierExpression> yNode1E = Either.right(yNode1);
        final Either<BindingIdentifier, IdentifierExpression> xNode3E = Either.left(xNode3);
        final Either<BindingIdentifier, IdentifierExpression> yNode2E = Either.left(yNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode3), ImmutableList.list(xNode1E)));
            variables.put("y", new Pair<>(ImmutableList.list(yNode2), ImmutableList.list(yNode1E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1E, Accessibility.Read);
            referenceTypes.put(yNode1E, Accessibility.Read);

            checkScope(globalScope, Scope.Type.Global, true, children, through, variables, referenceTypes);
        }
        { // block scope

            ImmutableList<Scope> children = ImmutableList.nil();

            ImmutableList<String> through = ImmutableList.list("y");

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode2), ImmutableList.list(xNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode2E, Accessibility.Write);

            checkScope(blockScope, Scope.Type.Block, false, children, through, variables, referenceTypes);
        }
    }

    @Test
    public void testDestructuring() throws JsError {
        String js = "var {x, a:{b:y = z}} = null; var [z] = y;";
        Script script = parse(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);
        Scope topLevelLexicalScope = globalScope.children.maybeHead().just();

        final BindingIdentifier xNode1 = bi( new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_()).d(ObjectBindingProperties_(0)).d(BindingPropertyIdentifierBinding_())
                .apply(script) );
        final BindingIdentifier yNode1 = bi( new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_()).d(ObjectBindingProperties_(1)).d(BindingPropertyPropertyBinding_()).d(ObjectBindingProperties_(0)).d(BindingPropertyPropertyBinding_()).d(BindingWithDefaultBinding_())
                .apply(script) );
        final IdentifierExpression zNode1 = ie( new Getter().d(ScriptStatements_(0)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_()).d(ObjectBindingProperties_(1)).d(BindingPropertyPropertyBinding_()).d(ObjectBindingProperties_(0)).d(BindingPropertyPropertyBinding_()).d(BindingWithDefaultInit_())
                .apply(script) );
        final BindingIdentifier zNode2 = bi( new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorBinding_()).d(ArrayBindingElements_(0))
                .apply(script) );
        final IdentifierExpression yNode2 = ie( new Getter().d(ScriptStatements_(1)).d(VariableDeclarationStatementDeclaration_()).d(VariableDeclarationDeclarators_(0)).d(VariableDeclaratorInit_())
                .apply(script) );

        final Either<BindingIdentifier, IdentifierExpression> xNode1E = Either.left(xNode1);
        final Either<BindingIdentifier, IdentifierExpression> yNode1E = Either.left(yNode1);
        final Either<BindingIdentifier, IdentifierExpression> zNode1E = Either.right(zNode1);
        final Either<BindingIdentifier, IdentifierExpression> zNode2E = Either.left(zNode2);
        final Either<BindingIdentifier, IdentifierExpression> yNode2E = Either.right(yNode2);

        { // global scope

            ImmutableList<Scope> children = ImmutableList.list(topLevelLexicalScope);

            ImmutableList<String> through = ImmutableList.nil();

            // mapping of variable names from this scope object to the list of their declarations and their references
            Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables = new HashMap<>();
            variables.put("x", new Pair<>(ImmutableList.list(xNode1), ImmutableList.list(xNode1E)));
            variables.put("y", new Pair<>(ImmutableList.list(yNode1), ImmutableList.list(yNode1E, yNode2E)));
            variables.put("z", new Pair<>(ImmutableList.list(zNode2), ImmutableList.list(zNode1E, zNode2E)));

            Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes = new HashMap<>();
            referenceTypes.put(xNode1E, Accessibility.Write);
            referenceTypes.put(yNode1E, Accessibility.Write);
            referenceTypes.put(yNode2E, Accessibility.Read);
            referenceTypes.put(zNode1E, Accessibility.Read);
            referenceTypes.put(zNode2E, Accessibility.Write);

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
    public void testFunctionDoubleDeclaration() throws JsError{
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
                "{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"FunctionB33\"}]}, {\"name\": \"g\", \"references\": [{\"node\": \"BindingIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(g)_15\", \"kind\": \"Var\"}]}, {\"name\": \"getOuter\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(getOuter)_7\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_6\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"Block_17\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"f\", \"references\": [{\"node\": \"BindingIdentifier(f)_20\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f)_29\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_22\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}"
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



    /**
     * Check the given scope is correct based on the information provided
     */
    private static void checkScope(
            @NotNull final Scope scope,
            @NotNull final Scope.Type scopeType,
            final boolean isDynamic,
            @NotNull final ImmutableList<Scope> children,
            @NotNull final ImmutableList<String> through,
            @NotNull final Map<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variables,
            @NotNull final Map<Either<BindingIdentifier, IdentifierExpression>, Accessibility> referenceTypes) {
        Assert.assertEquals(scope.type, scopeType);
        Assert.assertEquals(scope.dynamic, isDynamic);

        Assert.assertEquals(scope.children.length, children.length);
        children.foreach(child -> assertTrue(scope.children.exists(scope1 -> scope1 == child)));

        // scope.through.foreach(e -> System.out.println(e.a)); // TODO remove this

        Assert.assertEquals(scope.through.length, through.length);
        through.foreach(name -> {
            ImmutableList<Reference> references = scope.through.get(name).just();
            Assert.assertNotNull(references);
            assertTrue(references.find(ref -> ref.node.either(bi -> bi.name, ie -> ie.name).equals(name)).isJust());
        });

        Assert.assertEquals(scope.variables().size(), variables.size());
        for (Map.Entry<String, Pair<ImmutableList<BindingIdentifier>, ImmutableList<Either<BindingIdentifier, IdentifierExpression>>>> variableEntry : variables.entrySet()) {
            Maybe<Variable> maybeVariable = scope.lookupVariable(variableEntry.getKey());
            assertTrue(maybeVariable.isJust());
            Variable variable = maybeVariable.just();

            ImmutableList<BindingIdentifier> declarations = variableEntry.getValue().a;
            Assert.assertEquals(variable.declarations.length, declarations.length);
            for (final BindingIdentifier node : declarations) {
                assertTrue(variable.declarations.find(decl -> decl.node.equals(node)).isJust());
            }

            ImmutableList<Either<BindingIdentifier, IdentifierExpression>> refs = variableEntry.getValue().b;
            Assert.assertEquals(variable.references.length, refs.length);
            for (final Either<BindingIdentifier, IdentifierExpression> nodeE : refs) {
                Maybe<Reference> maybeRef = variable.references.find(
                        ref -> ref.node.equals(nodeE));
                assertTrue(maybeRef.isJust());
                Reference ref = maybeRef.just();
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
