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

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.scope.Declaration.Kind;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;

import com.shapesecurity.shift.visitor.StrictnessReducer;
import org.jetbrains.annotations.NotNull;

public final class ScopeAnalyzer extends MonoidalReducer<ScopeAnalyzer.State> {
    private final ImmutableSet<Node> sloppySet;

    private ScopeAnalyzer(@NotNull Script script) {
        super(new StateMonoid());
        sloppySet = StrictnessReducer.analyze(script);
    }

    private ScopeAnalyzer(@NotNull Module module) {
        super(new StateMonoid());
        sloppySet = ImmutableSet.emptyUsingIdentity();
    }

    @NotNull
    public static GlobalScope analyze(@NotNull Script script) {
        return (GlobalScope) Director.reduceScript(new ScopeAnalyzer(script), script).children.maybeHead().fromJust();
    }

    @NotNull
    public static GlobalScope analyze(@NotNull Module module) {
        return (GlobalScope) Director.reduceModule(new ScopeAnalyzer(module), module).children.maybeHead().fromJust();
    }

    @NotNull
    private State finishFunction(@NotNull Node fnNode, @NotNull State params, @NotNull State body) {
        boolean isArrowFn = fnNode instanceof ArrowExpression;
        Scope.Type fnType = isArrowFn ? Scope.Type.ArrowFunction : Scope.Type.Function;
        if (params.hasParameterExpressions) {
            params = params.withoutParameterExpressions(); // no need to pass that information on
            return new State(params, body.finish(fnNode, fnType, !isArrowFn, this.sloppySet.contains(fnNode))).finish(fnNode, Scope.Type.Parameters);
        } else {
            return new State(params, body).finish(fnNode, fnType, !isArrowFn, this.sloppySet.contains(fnNode));
        }
    }

    @NotNull
    // TODO you'd think you'd need to do this for labelled function declarations too, but the spec actually doesn't say so...
    private ImmutableList<BindingIdentifier> getFunctionDeclarations(@NotNull ImmutableList<Statement> statements) { // get the names of functions declared in the statement list
        ImmutableList<BindingIdentifier> potentiallyVarScopedFunctionDeclarations = ImmutableList.empty();
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
        return finishFunction(node, params, body);
    }

    @NotNull
    @Override
    public State reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull State binding, @NotNull State expression) {
        return super.reduceAssignmentExpression(node, binding.addReferences(Accessibility.Write), expression);
    }

    @NotNull
    @Override
    public State reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        if (node.name.equals("*default*")) {
            return new State();
        }
        return new State(
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                ImmutableList.empty(),
                false,
                ImmutableList.of(node),
                HashTable.emptyUsingEquality(),
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
        return super.reduceBlock(node, statements).withPotentialVarFunctions(getFunctionDeclarations(node.statements)).finish(node, Scope.Type.Block);
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
    public State reduceCatchClause(@NotNull CatchClause node, @NotNull State binding, @NotNull State body) {
        return super.reduceCatchClause(node, binding.addDeclarations(Kind.CatchParameter), body).finish(node, Scope.Type.Catch);
    }

    @NotNull
    @Override
    public State reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull State name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        State s = super.reduceClassDeclaration(node, name, _super, elements).addDeclarations(Kind.ClassName).finish(node, Scope.Type.ClassName);
        return new State(s, name.addDeclarations(Kind.ClassDeclaration));
    }

    @NotNull
    @Override
    public State reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<State> name, @NotNull Maybe<State> _super, @NotNull ImmutableList<State> elements) {
        return super.reduceClassExpression(node, name, _super, elements).addDeclarations(Kind.ClassName).finish(node, Scope.Type.ClassName);
    }

    @NotNull
    @Override
    public State reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull State binding, @NotNull State expression) {
        return super.reduceCompoundAssignmentExpression(node, binding.addReferences(Accessibility.ReadWrite), expression);
    }

    @NotNull
    @Override
    public State reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull State object, @NotNull State expression) {
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
        return items.mapWithIndex((F2<Integer, State, Pair>) Pair::new)
                .foldLeft((x, y) ->
                        new State(x, ((State) y.right).hasParameterExpressions ? ((State) y.right).finish(node.items.index((Integer) y.left).fromJust(), Scope.Type.ParameterExpression) : ((State) y.right)),
                rest.orJust(new State()))
                .addDeclarations(Kind.Parameter);
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration?
    @NotNull
    @Override
    public State reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull State name, @NotNull State params, @NotNull State body) {
        return new State(name, finishFunction(node, params, body)).addFunctionDeclaration();
        // todo it is possible that this should sometimes add a write-reference per B.3.3
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration
    @NotNull
    @Override
    public State reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<State> name, @NotNull State params, @NotNull State body) {
        State primary = finishFunction(node, params, body);
        if (name.isJust()) {
            return new State(name.fromJust(), primary).addDeclarations(Kind.FunctionExpressionName).finish(node, Scope.Type.FunctionName);
        } else {
            return primary; // per spec, no function name scope is created for unnamed expressions.
        }
    }

    @NotNull
    @Override
    public State reduceGetter(@NotNull Getter node, @NotNull State name, @NotNull State body) {
        return new State(name, body.finish(node, Scope.Type.Function, true, this.sloppySet.contains(node)));
        // variables defined in body are not in scope when evaluating name (which may be computed)
    }

    @NotNull
    @Override
    public State reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        Reference ref = new Reference(node);
        return new State(
                HashTable.<String, ImmutableList<Reference>>emptyUsingEquality().put(node.name, ImmutableList.of(ref)),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                ImmutableList.empty(),
                false,
                ImmutableList.empty(),
                HashTable.emptyUsingEquality(),
                false
        );
    }

    @NotNull
    @Override
    public State reduceIfStatement(@NotNull IfStatement node, @NotNull State test, @NotNull State consequent, @NotNull Maybe<State> alternate) {
        ImmutableList<Statement> statements = ImmutableList.of(node.consequent);
        if (node.alternate.isJust()) {
            statements = statements.cons(node.alternate.fromJust());
        }
        return super.reduceIfStatement(node, test, consequent, alternate).withPotentialVarFunctions(getFunctionDeclarations(statements));
    }

    @NotNull
    public State reduceImport(@NotNull Import node, @NotNull Maybe<State> defaultBinding, @NotNull ImmutableList<State> namedImports) {
        return super.reduceImport(node, defaultBinding, namedImports).addDeclarations(Kind.Import);
    }

    @NotNull
    @Override
    public State reduceMethod(@NotNull Method node, @NotNull State name, @NotNull State params, @NotNull State body) {
        return new State(name, finishFunction(node, params, body));
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
    public State reduceSetter(@NotNull Setter node, @NotNull State name, @NotNull State param, @NotNull State body) {
        param = param.hasParameterExpressions ? param.finish(node, Scope.Type.ParameterExpression) : param;
        return new State(name, finishFunction(node, param.addDeclarations(Kind.Parameter), body));
        // TODO have the node associated with the parameter's scope be more precise
    }

    @NotNull
    @Override
    public State reduceSwitchCase(@NotNull SwitchCase node, @NotNull State test, @NotNull ImmutableList<State> consequent) {
        return super.reduceSwitchCase(node, test, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(getFunctionDeclarations(node.consequent));
    }

    @NotNull
    @Override
    public State reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<State> consequent) {
        return super.reduceSwitchDefault(node, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(getFunctionDeclarations(node.consequent));
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
        public final HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations; // for annex B.3.3, which says (essentially) that function declarations are *also* var-scoped if doing so is not an early error (although not at the top level; only within functions).

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
                @NotNull HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations,
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
            this.freeIdentifiers = HashTable.emptyUsingEquality();
            this.functionScopedDeclarations = HashTable.emptyUsingEquality();
            this.blockScopedDeclarations = HashTable.emptyUsingEquality();
            this.functionDeclarations = HashTable.emptyUsingEquality();
            this.children = ImmutableList.empty();
            this.dynamic = false;
            this.bindingsForParent = ImmutableList.empty();
            this.potentiallyVarScopedFunctionDeclarations = HashTable.emptyUsingEquality();
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
            this.potentiallyVarScopedFunctionDeclarations = a.potentiallyVarScopedFunctionDeclarations.merge(b.potentiallyVarScopedFunctionDeclarations, ImmutableList::append);
            this.hasParameterExpressions = a.hasParameterExpressions || b.hasParameterExpressions;
        }


        /*
         * Used when a scope boundary is encountered. It resolves the free identifiers
         * and declarations found into variable objects. Any free identifiers remaining
         * are carried forward into the new state object.
         */
        private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType) {
            return finish(astNode, scopeType, false, false);
        }

        private State finish(@NotNull Node astNode, @NotNull Scope.Type scopeType, boolean resolveArguments, boolean shouldB33) {
            ImmutableList<Variable> variables = ImmutableList.empty();

            HashTable<String, ImmutableList<Declaration>> functionScope = HashTable.emptyUsingEquality();
            HashTable<String, ImmutableList<Reference>> freeIdentifiers = this.freeIdentifiers;
            HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations = this.potentiallyVarScopedFunctionDeclarations;
            ImmutableList<Scope> children = this.children;

            for (Pair<String, ImmutableList<Declaration>> name :  this.blockScopedDeclarations.entries()) {
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(name.left);
            }
            for (Pair<String, ImmutableList<Declaration>> fdecl :  this.functionDeclarations.entries()) {
                Maybe<ImmutableList<Declaration>> maybeConflict = this.potentiallyVarScopedFunctionDeclarations.get(fdecl.left);
                if (maybeConflict.isJust()) {
                    ImmutableList<Declaration> existingDeclarations = maybeConflict.fromJust();
                    ImmutableList<Declaration> newDeclarations = fdecl.right;
                    if (existingDeclarations.length != 1 || existingDeclarations.maybeHead().fromJust().node != newDeclarations.maybeHead().fromJust().node) { // don't conflict with your own lexical declaration
                        potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(fdecl.left);
                    }
                }
            }


            switch (scopeType) {
                case Block:
                case Catch:
                case With:
                case FunctionName:
                case ClassName:
                case ParameterExpression:
                    // resolve only block-scoped free declarations
                    ImmutableList<Variable> variables3 = variables;
                    for (Pair<String, ImmutableList<Declaration>> entry2 : this.blockScopedDeclarations.merge(this.functionDeclarations, ImmutableList::append).entries()) {
                        String name2 = entry2.left;
                        ImmutableList<Declaration> declarations2 = entry2.right;
                        ImmutableList<Reference> references2 = freeIdentifiers.get(name2).orJust(ImmutableList.empty());
                        variables3 = ImmutableList.cons(new Variable(name2, references2, declarations2), variables3);
                        freeIdentifiers = freeIdentifiers.remove(name2);
                    }
                    variables = variables3;
                    functionScope = this.functionScopedDeclarations;
                    break;
                case Parameters:
                case ArrowFunction:
                case Function:
                case Module:
                case Script:
                    // resolve both block-scoped and function-scoped free declarations

                    // first, block-scope declarations
                    HashTable<String, ImmutableList<Declaration>> newDeclarations = this.blockScopedDeclarations;

                    // top-level lexical declarations in scripts are not globals, so create a separate scope for them
                    if (scopeType == Scope.Type.Script) {
                        for (Pair<String, ImmutableList<Declaration>> entry : newDeclarations.entries()) {
                            String name = entry.left;
                            ImmutableList<Declaration> declarations = entry.right;
                            ImmutableList<Reference> references = freeIdentifiers.get(name).orJust(ImmutableList.empty());
                            variables = ImmutableList.cons(new Variable(name, references, declarations), variables);
                            freeIdentifiers = freeIdentifiers.remove(name);
                        }
                        children = ImmutableList.of(
                                new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode)
                        );
                        variables = ImmutableList.empty();
                        newDeclarations = HashTable.emptyUsingEquality();
                    }


                    // then, var-scope declarations
                    if (resolveArguments) {
                        newDeclarations = newDeclarations.merge(HashTable.<String, ImmutableList<Declaration>>emptyUsingEquality().put("arguments", ImmutableList.empty()));
                    }
                    newDeclarations = newDeclarations.merge(this.functionScopedDeclarations, ImmutableList::append).merge(this.functionDeclarations, ImmutableList::append);


                    // B.3.3: create an additional var-scoped binding for functions in blocks
                    if (shouldB33) { // todo maybe also script? check bugzilla.
                        newDeclarations = newDeclarations.merge(potentiallyVarScopedFunctionDeclarations, ImmutableList::append);
                    }

                    for (Pair<String, ImmutableList<Declaration>> entry : newDeclarations.entries()) {
                        String name = entry.left;
                        ImmutableList<Declaration> declarations = entry.right;
                        ImmutableList<Reference> references = freeIdentifiers.get(name).orJust(ImmutableList.empty());
                        variables = ImmutableList.cons(new Variable(name, references, declarations), variables);
                        freeIdentifiers = freeIdentifiers.remove(name);
                    }

                    if (scopeType == Scope.Type.Module) { // no declarations in a module are global
                        children = ImmutableList.of(
                                new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode)
                        );
                        variables = ImmutableList.empty();
                    }

                    potentiallyVarScopedFunctionDeclarations = HashTable.emptyUsingEquality();
                    break;
                default:
                    throw new RuntimeException("Not reached");
            }

            Scope scope = (scopeType == Scope.Type.Script || scopeType == Scope.Type.Module) ?
                    new GlobalScope(children, variables, freeIdentifiers, astNode) :
                    new Scope(children, variables, freeIdentifiers, scopeType, this.dynamic, astNode);

            return new State(
                    freeIdentifiers, functionScope, HashTable.emptyUsingEquality(), HashTable.emptyUsingEquality(),
                    ImmutableList.of(scope), false, this.bindingsForParent, potentiallyVarScopedFunctionDeclarations, this.hasParameterExpressions);
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
                ImmutableList<Declaration> decls = declMap.get(binding.name).orJust(ImmutableList.empty());
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
                    keepBindingsForParent ? this.bindingsForParent : ImmutableList.empty(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @NotNull
        private State addFunctionDeclaration() {
            if (this.bindingsForParent.length == 0) { // i.e., this is `export default function () {...}`
                return this;
            }
            BindingIdentifier binding = this.bindingsForParent.index(0).fromJust();
            Declaration decl = new Declaration(binding, Kind.FunctionDeclaration);
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    HashTable.<String, ImmutableList<Declaration>>emptyUsingEquality().put(binding.name, ImmutableList.of(decl)),
                    this.children,
                    this.dynamic,
                    ImmutableList.empty(),
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
                ImmutableList<Reference> refs = free.get(binding.name).orJust(ImmutableList.empty());
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
                    keepBindingsForParent ? this.bindingsForParent : ImmutableList.empty(),
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
                    ImmutableList.empty(),
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
            HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations = this.potentiallyVarScopedFunctionDeclarations;
            for (BindingIdentifier bi : funcs) {
                ImmutableList<Declaration> existing = potentiallyVarScopedFunctionDeclarations.get(bi.name).orJust(ImmutableList.empty());
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.put(bi.name, existing.cons(new Declaration(bi, Kind.FunctionB33)));
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
