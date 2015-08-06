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
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.scope.Declaration.Kind;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;

import org.jetbrains.annotations.NotNull;

public final class ScopeAnalyzer extends MonoidalReducer<ScopeAnalyzer.State> {
    private static final ScopeAnalyzer INSTANCE = new ScopeAnalyzer();

    private ScopeAnalyzer() {
        super(new StateMonoid());
    }

    @NotNull
    public static GlobalScope analyze(@NotNull Script script) {
        return (GlobalScope) Director.reduceScript(INSTANCE, script).children.maybeHead().just();
    }

    @NotNull
    public static GlobalScope analyze(@NotNull Module module) {
        return (GlobalScope) Director.reduceModule(INSTANCE, module).children.maybeHead().just();
    }

    @NotNull
    private State functionHelper(@NotNull Node fnNode, @NotNull State params, @NotNull State body, boolean isArrowFn) {
        Scope.Type fnType = isArrowFn ? Scope.Type.ArrowFunction : Scope.Type.Function;
        if (params.hasParameterExpressions) {
            params = params.withoutParameterExpressions(); // no need to pass that information on
            return new State(params, body.finish(fnNode, fnType)).addDeclarations(Kind.Param).finish(fnNode, Scope.Type.Parameters, !isArrowFn);
        } else {
            return new State(params.addDeclarations(Kind.Param), body).finish(fnNode, fnType, !isArrowFn);
        }
    }

    @NotNull
    // TODO you'd think you'd need to do this for labelled function declarations too, but the spec actually doesn't say so...
    private ImmutableList<BindingIdentifier> varScopedFunctionHelper(@NotNull ImmutableList<Statement> statements) { // get the names of functions declared in the statement list
        ImmutableList<BindingIdentifier> potentiallyVarScopedFunctionDeclarations = ImmutableList.nil();
        for (Statement statement : statements) { // TODO this is not a very clean way of doing this
            if (statement instanceof FunctionDeclaration) {
                FunctionDeclaration f = (FunctionDeclaration) statement;
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.cons(f.name);
            }
        }
        return potentiallyVarScopedFunctionDeclarations;
    }

    @NotNull
    @Override
    public State reduceArrowExpression(@NotNull ArrowExpression node, @NotNull State params, @NotNull State body) {
        return functionHelper(node, params, body, true);
    }

    @NotNull
    @Override
    public State reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull State binding, @NotNull State expression) {
        return super.reduceAssignmentExpression(node, binding.addReferences(Accessibility.Write), expression);
    }

    @NotNull
    @Override
    public State reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        return new State(
                HashTable.empty(),
                HashTable.empty(),
                HashTable.empty(),
                HashTable.empty(),
                ImmutableList.nil(),
                false,
                ImmutableList.list(node),
                HashTable.empty(),
                false
        );
    }

    @NotNull
    @Override
    public State reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull State binding, @NotNull Maybe<State> init) {
        State s = super.reduceBindingPropertyIdentifier(node, binding, init);
        if (init.isJust()) {
            return s.withParameterExpressions();
        }
        return s;
    }

    @NotNull
    @Override
    public State reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull State binding, @NotNull State init) {
        return super.reduceBindingWithDefault(node, binding, init).withParameterExpressions();
    }

    @NotNull
    @Override
    public State reduceBlock(@NotNull Block node, @NotNull ImmutableList<State> statements) {
        return super.reduceBlock(node, statements).withPotentialVarFunctions(varScopedFunctionHelper(node.statements)).finish(node, Scope.Type.Block);
    }

    @NotNull
    @Override
    public State reduceCallExpression(@NotNull CallExpression node, @NotNull State callee, @NotNull ImmutableList<State> arguments) {
        State s = super.reduceCallExpression(node, callee, arguments);
        if (node.callee instanceof IdentifierExpression && ((IdentifierExpression) node.callee).name.equals("eval")) {
            return s.taint();
        }
        return s;
    }

    @NotNull
    @Override
    public State reduceCatchClause(@NotNull CatchClause node, @NotNull State param, @NotNull State body) {
        return super.reduceCatchClause(node, param.addDeclarations(Kind.CatchParam), body).finish(node, Scope.Type.Catch);
    }

    @NotNull
    @Override
    public State reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull State name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        return super.reduceClassDeclaration(node, name.addDeclarations(Kind.ClassName), _super, elements);
    }

    @NotNull
    @Override
    public State reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<State> name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        return super.reduceClassExpression(node, name, _super, elements).addDeclarations(Kind.ClassName).finish(node, Scope.Type.FunctionName);
    }

    @NotNull
    @Override
    public State reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull State binding, @NotNull State expression) {
        return super.reduceCompoundAssignmentExpression(node, binding.addReferences(Accessibility.ReadWrite), expression);
    }

    @NotNull
    @Override
    public State reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull State expression, @NotNull State object) {
        return super.reduceComputedMemberExpression(node, object, expression).withParameterExpressions();
    }

    @NotNull
    @Override
    public State reduceForInStatement(@NotNull ForInStatement node, @NotNull State left, @NotNull State right, @NotNull State body) {
        return super.reduceForInStatement(node, left.addReferences(Accessibility.Write), right, body).finish(node, Scope.Type.Block);
    }

    @NotNull
    @Override
    public State reduceForOfStatement(@NotNull ForOfStatement node, @NotNull State left, @NotNull State right, @NotNull State body) {
        return super.reduceForOfStatement(node, left.addReferences(Accessibility.Write), right, body).finish(node, Scope.Type.Block);
    }

    @NotNull
    @Override
    public State reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<State> init, @NotNull Maybe<State> test, @NotNull Maybe<State> update, @NotNull State body) {
        return super.reduceForStatement(node, init.map(State::withoutBindingsForParent), test, update, body).finish(node, Scope.Type.Block);
    }

    @NotNull
    @Override
    public State reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<State> items, @NotNull Maybe<State> rest) {
        return items.foldLeft((x, y) ->
                        new State(x.hasParameterExpressions ? x.finish(node, Scope.Type.ParameterExpression) : x, y),
                rest.orJust(new State()))
                .addDeclarations(Kind.Param);
        // TODO have the node associated with a parameter's scope be more precise than the full list of parameters
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration
    @NotNull
    @Override
    public State reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull State name, @NotNull State params, @NotNull State body) {
        return new State(name, functionHelper(node, params, body, false)).addFunctionDeclaration();
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration
    @NotNull
    @Override
    public State reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<State> name, @NotNull State parameters, @NotNull State body) {
        State primary = functionHelper(node, parameters, body, false);
        if (name.isJust()) {
            return new State(name.just(), primary).addDeclarations(Kind.FunctionName).finish(node, Scope.Type.FunctionName);
        } else {
            return primary; // per spec, no function name scope is created for unnamed expressions.
        }
    }

    @NotNull
    @Override
    public State reduceGetter(@NotNull Getter node, @NotNull State body, @NotNull State name) {
        return new State(name, body.finish(node, Scope.Type.Function, true));
        // variables defined in body are not in scope when evaluating name (which may be computed)
    }

    @NotNull
    @Override
    public State reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        Reference ref = new Reference(node);
        return new State(
                HashTable.<String, ImmutableList<Reference>>empty().put(node.name, ImmutableList.list(ref)),
                HashTable.empty(),
                HashTable.empty(),
                HashTable.empty(),
                ImmutableList.nil(),
                false,
                ImmutableList.nil(),
                HashTable.empty(),
                false
        );
    }

    @NotNull
    @Override
    public State reduceIfStatement(@NotNull IfStatement node, @NotNull State test, @NotNull State consequent, @NotNull Maybe<State> alternate) {
        ImmutableList<Statement> statements = ImmutableList.list(node.consequent);
        if (node.alternate.isJust()) {
            statements = statements.cons(node.alternate.just());
        }
        return super.reduceIfStatement(node, test, consequent, alternate).withPotentialVarFunctions(varScopedFunctionHelper(statements));
    }


    @NotNull
    @Override
    public State reduceMethod(@NotNull Method node, @NotNull State params, @NotNull State body, @NotNull State name) {
        return new State(name, functionHelper(node, params, body, false));
    }

    @NotNull
    @Override
    public State reduceModule(@NotNull Module node, @NotNull ImmutableList<State> directives, @NotNull ImmutableList<State> statements) {
        return super.reduceModule(node, directives, statements).finish(node, Scope.Type.Module);
    }

    @NotNull
    @Override
    public State reduceScript(@NotNull Script node, @NotNull ImmutableList<State> directives, @NotNull ImmutableList<State> statements) {
        return super.reduceScript(node, directives, statements).finish(node, Scope.Type.Script);
    }

    @NotNull
    @Override
    public State reduceSetter(@NotNull Setter node, @NotNull State parameter, @NotNull State body, @NotNull State name) {
        parameter = parameter.hasParameterExpressions ? parameter.finish(node, Scope.Type.ParameterExpression) : parameter;
        return new State(name, functionHelper(node, parameter.addDeclarations(Kind.Param), body, false));
        // TODO have the node associated with the parameter's scope be more precise
    }


    @NotNull
    @Override
    public State reduceSwitchCase(@NotNull SwitchCase node, @NotNull State test, @NotNull ImmutableList<State> consequent) {
        return super.reduceSwitchCase(node, test, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(varScopedFunctionHelper(node.consequent));
    }

    @NotNull
    @Override
    public State reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<State> consequent) {
        return super.reduceSwitchDefault(node, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(varScopedFunctionHelper(node.consequent));
    }

    @NotNull
    @Override
    public State reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull State operand) {
        return operand.addReferences(Accessibility.ReadWrite);
        // no-op if operand is a member expression (which will have no bindingsForParent)
    }

    @NotNull
    @Override
    public State reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<State> declarators) {
        return super.reduceVariableDeclaration(node, declarators).addDeclarations(Kind.fromVariableDeclarationKind(node.kind), true);
        // passes bindingsForParent up, for for-in and for-of to add their write-references
    }

    @NotNull
    @Override
    public State reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull State declaration) {
        return declaration.withoutBindingsForParent();
    }

    @NotNull
    @Override
    public State reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull State binding, @NotNull Maybe<State> init) {
        State res = super.reduceVariableDeclarator(node, binding, init);
        if (init.isJust()) {
            return res.addReferences(Accessibility.Write, true);
            // passes bindingsForParent up, for variableDeclaration to add the appropriate type of declaration
        } else {
            return res;
        }
    }

    @NotNull
    @Override
    public State reduceWithStatement(@NotNull WithStatement node, @NotNull State object, @NotNull State body) {
        return super.reduceWithStatement(node, object, body.finish(node, Scope.Type.With));
    }


    @SuppressWarnings("ProtectedInnerClass")
    public static final class State {
        public final boolean dynamic;
        public final boolean hasParameterExpressions; // to decide if function parameters are in a different scope than function variables. only meaningful on `params` states and their children. true iff `params` has any default values or computed member accesses among its children.
        @NotNull
        public final HashTable<String, ImmutableList<Reference>> freeIdentifiers;
        @NotNull
        public final HashTable<String, ImmutableList<Declaration>> functionScopedDeclarations;
        @NotNull
        public final HashTable<String, ImmutableList<Declaration>> blockScopedDeclarations;
        @NotNull
        public final HashTable<String, ImmutableList<Declaration>> functionDeclarations; // function declarations are special: they are lexical in blocks and var at the top level of functions and scripts. In particular, at the top of scripts they go in global scope.
        @NotNull
        public final ImmutableList<Scope> children;
        @NotNull
        public final ImmutableList<BindingIdentifier> bindingsForParent; // either references bubbling up to the AssignmentExpression, ForOfStatement, or ForInStatement which writes to them or declarations bubbling up to the VariableDeclaration, FunctionDeclaration, ClassDeclaration, FormalParameters, Setter, Method, or CatchClause which declares them
        @NotNull
        public final HashTable<String, BindingIdentifier> potentiallyVarScopedFunctionDeclarations; // for annex B.3.3, which says (essentially) that function declarations are *also* var-scoped if doing so is not an early error (although not at the top level; only within functions).

        /*
         * Fully saturated constructor
         */
        private State(
                @NotNull HashTable<String, ImmutableList<Reference>> freeIdentifiers,
                @NotNull HashTable<String, ImmutableList<Declaration>> functionScopedDeclarations,
                @NotNull HashTable<String, ImmutableList<Declaration>> blockScopedDeclarations,
                @NotNull HashTable<String, ImmutableList<Declaration>> functionDeclarations,
                @NotNull ImmutableList<Scope> children,
                boolean dynamic,
                @NotNull ImmutableList<BindingIdentifier> bindingsForParent,
                @NotNull HashTable<String, BindingIdentifier> potentiallyVarScopedFunctionDeclarations,
                boolean hasParameterExpressions
        ) {
            this.freeIdentifiers = freeIdentifiers;
            this.functionScopedDeclarations = functionScopedDeclarations;
            this.blockScopedDeclarations = blockScopedDeclarations;
            this.functionDeclarations = functionDeclarations;
            this.children = children;
            this.dynamic = dynamic;
            this.bindingsForParent = bindingsForParent;
            this.potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations;
            this.hasParameterExpressions = hasParameterExpressions;
        }

        /*
         * Identity constructor
         */
        private State() {
            this.freeIdentifiers = HashTable.empty();
            this.functionScopedDeclarations = HashTable.empty();
            this.blockScopedDeclarations = HashTable.empty();
            this.functionDeclarations = HashTable.empty();
            this.children = ImmutableList.nil();
            this.dynamic = false;
            this.bindingsForParent = ImmutableList.nil();
            this.potentiallyVarScopedFunctionDeclarations = HashTable.empty();
            this.hasParameterExpressions = false;
        }

        /*
         * Monoidal append: merges the two states together
         */
        @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject", "ObjectEquality"})
        private State(@NotNull State a, @NotNull State b) {
            this.freeIdentifiers = a.freeIdentifiers.merge(b.freeIdentifiers, ImmutableList::append);
            this.functionScopedDeclarations = a.functionScopedDeclarations.merge(b.functionScopedDeclarations, ImmutableList::append);
            this.blockScopedDeclarations = a.blockScopedDeclarations.merge(b.blockScopedDeclarations, ImmutableList::append);
            this.functionDeclarations = a.functionDeclarations.merge(b.functionDeclarations, ImmutableList::append);
            this.children = a.children.append(b.children);
            this.dynamic = a.dynamic || b.dynamic;
            this.bindingsForParent = a.bindingsForParent.append(b.bindingsForParent);
            this.potentiallyVarScopedFunctionDeclarations = a.potentiallyVarScopedFunctionDeclarations.merge(b.potentiallyVarScopedFunctionDeclarations);
            this.hasParameterExpressions = a.hasParameterExpressions || b.hasParameterExpressions;
        }


        /*
         * Used when a scope boundary is encountered. It resolves the free identifiers
         * and declarations found into variable objects. Any free identifiers remaining
         * are carried forward into the new state object.
         */
        private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType) {
            return finish(astNode, scopeType, false);
        }

        private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType, boolean resolveArguments) {
            ImmutableList<Variable> variables = ImmutableList.nil();

            HashTable<String, ImmutableList<Declaration>> functionScope = HashTable.empty();
            HashTable<String, ImmutableList<Reference>> freeIdentifiers = this.freeIdentifiers;
            HashTable<String, BindingIdentifier> potentiallyVarScopedFunctionDeclarations = this.potentiallyVarScopedFunctionDeclarations;
            ImmutableList<Scope> children = this.children;

            switch (scopeType) {
                case Block:
                case Catch:
                case With:
                case FunctionName:
                case Parameters:
                case ParameterExpression:
                    // resolve only block-scoped free declarations
                    ImmutableList<Variable> variables3 = variables;
                    for (Pair<String, ImmutableList<Declaration>> entry2 : this.blockScopedDeclarations.entries().append(this.functionDeclarations.entries())) {
                        String name2 = entry2.a;
                        ImmutableList<Declaration> declarations2 = entry2.b;
                        ImmutableList<Reference> references2 = freeIdentifiers.get(name2).orJust(ImmutableList.nil());
                        variables3 = ImmutableList.cons(new Variable(name2, references2, declarations2), variables3);
                        freeIdentifiers = freeIdentifiers.remove(name2);

                        // we do not create var-scoped bindings for functions if doing so would cause an early error, ie, if a containing block has a block-scoped declaration of the same name
                        Maybe<BindingIdentifier> maybeConflict = this.potentiallyVarScopedFunctionDeclarations.get(name2);
                        if (maybeConflict.isJust()) {
                            BindingIdentifier conflict = maybeConflict.just();
                            if (declarations2.length != 1 || declarations2.maybeHead().just().node != conflict) { // don't conflict with your own lexical declaration
                                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(name2);
                            }
                        }
                    }
                    variables = variables3;
                    functionScope = this.functionScopedDeclarations;
                    break;
                case ArrowFunction:
                case Function:
                case Module:
                case Script:
                    // resolve both block-scoped and function-scoped free declarations
                    if (resolveArguments) {
                        ImmutableList<Variable> variables1 = variables;
                        ImmutableList<Reference> arguments = freeIdentifiers.get("arguments").orJust(ImmutableList.nil());
                        freeIdentifiers = freeIdentifiers.remove("arguments");
                        variables1 = ImmutableList.cons(new Variable("arguments", arguments, ImmutableList.nil()), variables1);
                        variables = variables1;
                    }
                    ImmutableList<Variable> variables2 = variables;
                    for (Pair<String, ImmutableList<Declaration>> entry1 : this.blockScopedDeclarations.entries()) {
                        String name1 = entry1.a;
                        ImmutableList<Declaration> declarations1 = entry1.b;
                        ImmutableList<Reference> references1 = freeIdentifiers.get(name1).orJust(ImmutableList.nil());
                        variables2 = ImmutableList.cons(new Variable(name1, references1, declarations1), variables2);
                        freeIdentifiers = freeIdentifiers.remove(name1);

                        // we do not create var-scoped bindings for functions if doing so would cause an early error, ie, if a containing block has a block-scoped declaration of the same name
                        potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(name1);
                    }

                    // B.3.3: create an additional var-scoped binding for functions in blocks
                    // TODO this doesn't necessarily happen in the right order (but may not matter...?)
                    HashTable<String, ImmutableList<Declaration>> funcScopedDeclarationsOrFunctions = this.functionScopedDeclarations;
                    if (scopeType == Scope.Type.Function || scopeType == Scope.Type.ArrowFunction) {
                        for (Pair<String, BindingIdentifier> fEntry : potentiallyVarScopedFunctionDeclarations.entries()) {
                            String fName = fEntry.a;
                            BindingIdentifier fBinding = fEntry.b;
                            ImmutableList<Declaration> declarations = funcScopedDeclarationsOrFunctions.get(fName).orJust(ImmutableList.nil());
                            if (declarations.isEmpty()) { // do not create bindings for already-instantiated names
                                funcScopedDeclarationsOrFunctions = funcScopedDeclarationsOrFunctions.put(fName, declarations.cons(new Declaration(fBinding, Kind.Var))); // TODO ensure this is the correct kind.
                            }
                        }
                    }

                    variables = variables2;
                    if (scopeType == Scope.Type.Script) { // top-level lexical declarations in scripts are not globals
                        children = ImmutableList.list(
                                new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode)
                        );
                        variables = ImmutableList.nil();
                    }

                    ImmutableList<Variable> variables1 = variables;
                    for (Pair<String, ImmutableList<Declaration>> entry : funcScopedDeclarationsOrFunctions.entries().append(this.functionDeclarations.entries())) {
                        String name = entry.a;
                        ImmutableList<Declaration> declarations = entry.b;
                        ImmutableList<Reference> references = freeIdentifiers.get(name).orJust(ImmutableList.nil());
                        variables1 = ImmutableList.cons(new Variable(name, references, declarations), variables1);
                        freeIdentifiers = freeIdentifiers.remove(name);
                    }
                    variables = variables1;

                    if (scopeType == Scope.Type.Module) { // no declarations in a module are global
                        children = ImmutableList.list(
                                new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode)
                        );
                        variables = ImmutableList.nil();
                    }
                    break;
                default:
                    throw new RuntimeException("Not reached");
            }

            Scope scope = (scopeType == Scope.Type.Script || scopeType == Scope.Type.Module) ?
                    new GlobalScope(children, variables, freeIdentifiers, astNode) :
                    new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode);

            return new State(
                    freeIdentifiers, functionScope, HashTable.empty(), HashTable.empty(),
                    ImmutableList.list(scope), false, this.bindingsForParent, potentiallyVarScopedFunctionDeclarations, false);
        }

        /*
         * Observe variables entering scope
         */
        @NotNull
        private State addDeclarations(@NotNull Kind kind) {
            return addDeclarations(kind, false);
        }

        @NotNull
        private State addDeclarations(@NotNull Kind kind, boolean keepBindingsForParent) {
            HashTable<String, ImmutableList<Declaration>> declMap =
                    kind.isBlockScoped ? this.blockScopedDeclarations : this.functionScopedDeclarations;

            for (BindingIdentifier binding : this.bindingsForParent) {
                Declaration decl = new Declaration(binding, kind);
                ImmutableList<Declaration> decls = declMap.get(binding.name).orJust(ImmutableList.nil());
                decls = decls.cons(decl);
                declMap = declMap.put(binding.name, decls);
            }
            return new State(
                    this.freeIdentifiers,
                    kind.isBlockScoped ? this.functionScopedDeclarations : declMap,
                    kind.isBlockScoped ? declMap : this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    keepBindingsForParent ? this.bindingsForParent : ImmutableList.nil(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @NotNull
        private State addFunctionDeclaration() {
            assert this.bindingsForParent.length == 1;
            BindingIdentifier binding = this.bindingsForParent.index(0).just();
            Declaration decl = new Declaration(binding, Kind.FunctionName);
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    HashTable.<String, ImmutableList<Declaration>>empty().put(binding.name, ImmutableList.list(decl)),
                    this.children,
                    this.dynamic,
                    ImmutableList.nil(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        /*
         * Observe references
         */
        @NotNull
        public State addReferences(@NotNull Accessibility accessibility) {
            return addReferences(accessibility, false);
        }

        @NotNull
        private State addReferences(@NotNull Accessibility accessibility, boolean keepBindingsForParent) {
            HashTable<String, ImmutableList<Reference>> free = this.freeIdentifiers;
            for (BindingIdentifier binding : this.bindingsForParent) {
                Reference ref = new Reference(binding, accessibility);
                ImmutableList<Reference> refs = free.get(binding.name).orJust(ImmutableList.nil());
                refs = refs.cons(ref);
                free = free.put(binding.name, refs);
            }
            return new State(
                    free,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    keepBindingsForParent ? this.bindingsForParent : ImmutableList.nil(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @NotNull
        public State taint() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    true,
                    this.bindingsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @NotNull
        public State withoutBindingsForParent() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    ImmutableList.nil(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @NotNull
        public State withParameterExpressions() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    this.bindingsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    true
            );
        }

        @NotNull
        public State withoutParameterExpressions() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    this.bindingsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    false
            );
        }

        @NotNull
        public State withPotentialVarFunctions(@NotNull ImmutableList<BindingIdentifier> funcs) {
            HashTable<String, BindingIdentifier> potentiallyVarScopedFunctionDeclarations = this.potentiallyVarScopedFunctionDeclarations;
            for (BindingIdentifier bi : funcs) {
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.put(bi.name, bi);
            }
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    this.bindingsForParent,
                    potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }
    }

    @SuppressWarnings("ProtectedInnerClass")
    private static final class StateMonoid implements Monoid<State> {
        @Override
        @NotNull
        public State identity() {
            return new State();
        }

        @Override
        @NotNull
        public State append(State a, State b) {
            if (a == b) {
                return a;
            }
            return new State(a, b);
        }
    }
}
