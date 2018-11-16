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

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.reducer.MonoidalReducer;
import com.shapesecurity.shift.es2017.reducer.StrictnessReducer;
import com.shapesecurity.shift.es2017.scope.Declaration.Kind;
import com.shapesecurity.shift.es2017.scope.ScopeAnalyzer.State;

import javax.annotation.Nonnull;

public final class ScopeAnalyzer extends MonoidalReducer<State> {
    private final ImmutableSet<Node> sloppySet;

    private ScopeAnalyzer(@Nonnull Script script) {
        super(new StateMonoid());
        sloppySet = StrictnessReducer.analyze(script);
    }

    private ScopeAnalyzer(@Nonnull Module module) {
        super(new StateMonoid());
        sloppySet = ImmutableSet.emptyUsingIdentity();
    }

    @Nonnull
    public static GlobalScope analyze(@Nonnull Script script) {
        return (GlobalScope) Director.reduceScript(new ScopeAnalyzer(script), script).children.maybeHead().fromJust();
    }

    @Nonnull
    public static GlobalScope analyze(@Nonnull Module module) {
        return (GlobalScope) Director.reduceModule(new ScopeAnalyzer(module), module).children.maybeHead().fromJust();
    }

    @Nonnull
    private State finishFunction(@Nonnull Node fnNode, @Nonnull State params, @Nonnull State body) {
        boolean isArrowFn = fnNode instanceof ArrowExpression;
        Scope.Type fnType = isArrowFn ? Scope.Type.ArrowFunction : Scope.Type.Function;
        if (params.hasParameterExpressions) {
            params = params.withoutParameterExpressions(); // no need to pass that information on
            return new State(params, body.finish(fnNode, fnType, !isArrowFn, this.sloppySet.contains(fnNode))).finish(fnNode, Scope.Type.Parameters);
        } else {
            return new State(params, body).finish(fnNode, fnType, !isArrowFn, this.sloppySet.contains(fnNode));
        }
    }

    @Nonnull
    // TODO you'd think you'd need to do this for labelled function declarations too, but the spec actually doesn't say so...
    private ImmutableList<BindingIdentifier> getFunctionDeclarations(@Nonnull ImmutableList<Statement> statements) { // get the names of functions declared in the statement list
        ImmutableList<BindingIdentifier> potentiallyVarScopedFunctionDeclarations = ImmutableList.empty();
        for (Statement statement : statements) { // TODO this is not a very clean way of doing this
            if (statement instanceof FunctionDeclaration) {
                FunctionDeclaration f = (FunctionDeclaration) statement;
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.cons(f.name);
            }
        }
        return potentiallyVarScopedFunctionDeclarations;
    }

    @Nonnull
    @Override
    public State reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull State params, @Nonnull State body) {
        return finishFunction(node, params, body);
    }

    @Nonnull
    @Override
    public State reduceAssignmentExpression(@Nonnull AssignmentExpression node, @Nonnull State binding, @Nonnull State expression) {
        return super.reduceAssignmentExpression(node, binding.addReferences(Accessibility.Write), expression);
    }

    @Nonnull
    @Override
    public State reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
        return new State(
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                ImmutableList.empty(),
                false,
                ImmutableList.empty(),
                ImmutableList.of(node),
                HashTable.emptyUsingEquality(),
                false
        );
    }

    @Nonnull
    @Override
    public State reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
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
                ImmutableList.empty(),
                HashTable.emptyUsingEquality(),
                false
        );
    }

    @Nonnull
    @Override
    public State reduceBindingPropertyIdentifier(@Nonnull BindingPropertyIdentifier node, @Nonnull State binding, @Nonnull Maybe<State> init) {
        State s = super.reduceBindingPropertyIdentifier(node, binding, init);
        if (init.isJust()) {
            return s.withParameterExpressions();
        }
        return s;
    }

    @Nonnull
    @Override
    public State reduceBindingWithDefault(@Nonnull BindingWithDefault node, @Nonnull State binding, @Nonnull State init) {
        return super.reduceBindingWithDefault(node, binding, init).withParameterExpressions();
    }

    @Nonnull
    @Override
    public State reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<State> statements) {
        return super.reduceBlock(node, statements).withPotentialVarFunctions(getFunctionDeclarations(node.statements)).finish(node, Scope.Type.Block);
    }

    @Nonnull
    @Override
    public State reduceCallExpression(@Nonnull CallExpression node, @Nonnull State callee, @Nonnull ImmutableList<State> arguments) {
        State s = super.reduceCallExpression(node, callee, arguments);
        if (node.callee instanceof IdentifierExpression && ((IdentifierExpression) node.callee).name.equals("eval")) {
            return s.taint();
        }
        return s;
    }

    @Nonnull
    @Override
    public State reduceCatchClause(@Nonnull CatchClause node, @Nonnull State binding, @Nonnull State body) {
        return super.reduceCatchClause(node, binding.addDeclarations(Kind.CatchParameter), body).finish(node, Scope.Type.Catch);
    }

    @Nonnull
    @Override
    public State reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull State name, @Nonnull Maybe<State> _super, @Nonnull ImmutableList<State> elements) {
        State s = super.reduceClassDeclaration(node, name, _super, elements).addDeclarations(Kind.ClassName).finish(node, Scope.Type.ClassName);
        return new State(s, name.addDeclarations(Kind.ClassDeclaration));
    }

    @Nonnull
    @Override
    public State reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<State> name, @Nonnull Maybe<State> _super, @Nonnull ImmutableList<State> elements) {
        return super.reduceClassExpression(node, name, _super, elements).addDeclarations(Kind.ClassName).finish(node, Scope.Type.ClassName);
    }

    @Nonnull
    @Override
    public State reduceCompoundAssignmentExpression(@Nonnull CompoundAssignmentExpression node, @Nonnull State binding, @Nonnull State expression) {
        return super.reduceCompoundAssignmentExpression(node, binding.addReferences(Accessibility.ReadWrite), expression);
    }

    @Nonnull
    @Override
    public State reduceComputedMemberExpression(@Nonnull ComputedMemberExpression node, @Nonnull State object, @Nonnull State expression) {
        return super.reduceComputedMemberExpression(node, object, expression).withParameterExpressions();
    }

    @Nonnull
    @Override
    public State reduceForInStatement(@Nonnull ForInStatement node, @Nonnull State left, @Nonnull State right, @Nonnull State body) {
        return super.reduceForInStatement(node, left.addReferences(Accessibility.Write), right, body).finish(node, Scope.Type.Block);
    }

    @Nonnull
    @Override
    public State reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull State left, @Nonnull State right, @Nonnull State body) {
        return super.reduceForOfStatement(node, left.addReferences(Accessibility.Write), right, body).finish(node, Scope.Type.Block);
    }

    @Nonnull
    @Override
    public State reduceForStatement(@Nonnull ForStatement node, @Nonnull Maybe<State> init, @Nonnull Maybe<State> test, @Nonnull Maybe<State> update, @Nonnull State body) {
        return super.reduceForStatement(node, init.map(State::withoutBindingsForParent), test, update, body).finish(node, Scope.Type.Block);
    }

    @Nonnull
    @Override
    public State reduceFormalParameters(@Nonnull FormalParameters node, @Nonnull ImmutableList<State> items, @Nonnull Maybe<State> rest) {
        return items.mapWithIndex((F2<Integer, State, Pair>) Pair::new)
                .foldLeft((x, y) ->
                        new State(x, ((State) y.right()).hasParameterExpressions ? ((State) y.right()).finish(node.items.index((Integer) y.left()).fromJust(), Scope.Type.ParameterExpression) : ((State) y.right())),
                rest.orJust(new State()))
                .addDeclarations(Kind.Parameter);
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration?
    @Nonnull
    @Override
    public State reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull State name, @Nonnull State params, @Nonnull State body) {
        return new State(name, finishFunction(node, params, body)).addFunctionDeclaration();
        // todo it is possible that this should sometimes add a write-reference per B.3.3
    }

    // TODO should defining a function count as writing to its name, for symmetry with initialized variable declaration
    @Nonnull
    @Override
    public State reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<State> name, @Nonnull State params, @Nonnull State body) {
        State primary = finishFunction(node, params, body);
        if (name.isJust()) {
            return new State(name.fromJust(), primary).addDeclarations(Kind.FunctionExpressionName).finish(node, Scope.Type.FunctionName);
        } else {
            return primary; // per spec, no function name scope is created for unnamed expressions.
        }
    }

    @Nonnull
    @Override
    public State reduceGetter(@Nonnull Getter node, @Nonnull State name, @Nonnull State body) {
        return new State(name, body.finish(node, Scope.Type.Function, true, this.sloppySet.contains(node)));
        // variables defined in body are not in scope when evaluating name (which may be computed)
    }

    @Nonnull
    @Override
    public State reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
        Reference ref = new Reference(node);
        return new State(
                HashTable.<String, NonEmptyImmutableList<Reference>>emptyUsingEquality().put(node.name, ImmutableList.of(ref)),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                HashTable.emptyUsingEquality(),
                ImmutableList.empty(),
                false,
                ImmutableList.empty(),
                ImmutableList.empty(),
                HashTable.emptyUsingEquality(),
                false
        );
    }

    @Nonnull
    @Override
    public State reduceIfStatement(@Nonnull IfStatement node, @Nonnull State test, @Nonnull State consequent, @Nonnull Maybe<State> alternate) {
        ImmutableList<Statement> statements = ImmutableList.of(node.consequent);
        if (node.alternate.isJust()) {
            statements = statements.cons(node.alternate.fromJust());
        }
        return super.reduceIfStatement(node, test, consequent, alternate).withPotentialVarFunctions(getFunctionDeclarations(statements));
    }

    @Nonnull
    @Override
    public State reduceImport(@Nonnull Import node, @Nonnull Maybe<State> defaultBinding, @Nonnull ImmutableList<State> namedImports) {
        return super.reduceImport(node, defaultBinding, namedImports).addDeclarations(Kind.Import);
    }

    @Nonnull
    @Override
    public State reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<State> defaultBinding, @Nonnull State namespaceBinding) {
        return super.reduceImportNamespace(node, defaultBinding, namespaceBinding).addDeclarations(Kind.Import);
    }

    @Nonnull
    @Override
    public State reduceMethod(@Nonnull Method node, @Nonnull State name, @Nonnull State params, @Nonnull State body) {
        return new State(name, finishFunction(node, params, body));
    }

    @Nonnull
    @Override
    public State reduceModule(@Nonnull Module node, @Nonnull ImmutableList<State> directives, @Nonnull ImmutableList<State> statements) {
        return super.reduceModule(node, directives, statements).finish(node, Scope.Type.Module);
    }

    @Nonnull
    @Override
    public State reduceScript(@Nonnull Script node, @Nonnull ImmutableList<State> directives, @Nonnull ImmutableList<State> statements) {
        return super.reduceScript(node, directives, statements).finish(node, Scope.Type.Script);
    }

    @Nonnull
    @Override
    public State reduceSetter(@Nonnull Setter node, @Nonnull State name, @Nonnull State param, @Nonnull State body) {
        param = param.hasParameterExpressions ? param.finish(node, Scope.Type.ParameterExpression) : param;
        return new State(name, finishFunction(node, param.addDeclarations(Kind.Parameter), body));
        // TODO have the node associated with the parameter's scope be more precise
    }

    @Nonnull
    @Override
    public State reduceSwitchCase(@Nonnull SwitchCase node, @Nonnull State test, @Nonnull ImmutableList<State> consequent) {
        return super.reduceSwitchCase(node, test, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(getFunctionDeclarations(node.consequent));
    }

    @Nonnull
    @Override
    public State reduceSwitchDefault(@Nonnull SwitchDefault node, @Nonnull ImmutableList<State> consequent) {
        return super.reduceSwitchDefault(node, consequent).finish(node, Scope.Type.Block).withPotentialVarFunctions(getFunctionDeclarations(node.consequent));
    }

    @Nonnull
    @Override
    public State reduceUpdateExpression(@Nonnull UpdateExpression node, @Nonnull State operand) {
        return operand.addReferences(Accessibility.ReadWrite);
        // no-op if operand is a member expression (which will have no bindingsForParent)
    }

    @Nonnull
    @Override
    public State reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull ImmutableList<State> declarators) {
        return super.reduceVariableDeclaration(node, declarators).addDeclarations(Kind.fromVariableDeclarationKind(node.kind), true);
        // passes bindingsForParent up, for for-in and for-of to add their write-references
    }

    @Nonnull
    @Override
    public State reduceVariableDeclarationStatement(@Nonnull VariableDeclarationStatement node, @Nonnull State declaration) {
        return declaration.withoutBindingsForParent();
    }

    @Nonnull
    @Override
    public State reduceVariableDeclarator(@Nonnull VariableDeclarator node, @Nonnull State binding, @Nonnull Maybe<State> init) {
        State res = super.reduceVariableDeclarator(node, binding, init);
        if (init.isJust()) {
            return res.addReferences(Accessibility.Write, true);
            // passes bindingsForParent up, for variableDeclaration to add the appropriate type of declaration
        } else {
            return res;
        }
    }

    @Nonnull
    @Override
    public State reduceWithStatement(@Nonnull WithStatement node, @Nonnull State object, @Nonnull State body) {
        return super.reduceWithStatement(node, object, body.finish(node, Scope.Type.With));
    }


    @SuppressWarnings("ProtectedInnerClass")
    public static final class State {
        public final boolean dynamic;
        public final boolean hasParameterExpressions; // to decide if function parameters are in a different scope than function variables. only meaningful on `params` states and their children. true iff `params` has any default values or computed member accesses among its children.
        @Nonnull
        public final HashTable<String, NonEmptyImmutableList<Reference>> freeIdentifiers;
        @Nonnull
        public final HashTable<String, ImmutableList<Declaration>> functionScopedDeclarations;
        @Nonnull
        public final HashTable<String, ImmutableList<Declaration>> blockScopedDeclarations;
        @Nonnull
        public final HashTable<String, ImmutableList<Declaration>> functionDeclarations; // function declarations are special: they are lexical in blocks and var at the top level of functions and scripts. In particular, at the top of scripts they go in global scope.
        @Nonnull
        public final ImmutableList<Scope> children;
        @Nonnull
        public final ImmutableList<BindingIdentifier> bindingsForParent; // either references bubbling up to the AssignmentExpression, ForOfStatement, or ForInStatement which writes to them or declarations bubbling up to the VariableDeclaration, FunctionDeclaration, ClassDeclaration, FormalParameters, Setter, Method, or CatchClause which declares them
        @Nonnull
        public final ImmutableList<AssignmentTargetIdentifier> atsForParent; // references bubbling up to the AssignmentExpression, ForOfStatement, or ForInStatement which writes to them
        @Nonnull
        public final HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations; // for annex B.3.3, which says (essentially) that function declarations are *also* var-scoped if doing so is not an early error (although not at the top level; only within functions).

        /*
         * Fully saturated constructor
         */
        private State(
                @Nonnull HashTable<String, NonEmptyImmutableList<Reference>> freeIdentifiers,
                @Nonnull HashTable<String, ImmutableList<Declaration>> functionScopedDeclarations,
                @Nonnull HashTable<String, ImmutableList<Declaration>> blockScopedDeclarations,
                @Nonnull HashTable<String, ImmutableList<Declaration>> functionDeclarations,
                @Nonnull ImmutableList<Scope> children,
                boolean dynamic,
                @Nonnull ImmutableList<BindingIdentifier> bindingsForParent,
                @Nonnull ImmutableList<AssignmentTargetIdentifier> atsForParent,
                @Nonnull HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations,
                boolean hasParameterExpressions
        ) {
            this.freeIdentifiers = freeIdentifiers;
            this.functionScopedDeclarations = functionScopedDeclarations;
            this.blockScopedDeclarations = blockScopedDeclarations;
            this.functionDeclarations = functionDeclarations;
            this.children = children;
            this.dynamic = dynamic;
            this.bindingsForParent = bindingsForParent;
            this.atsForParent = atsForParent;
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
            this.atsForParent = ImmutableList.empty();
            this.potentiallyVarScopedFunctionDeclarations = HashTable.emptyUsingEquality();
            this.hasParameterExpressions = false;
        }

        /*
         * Monoidal append: merges the two states together
         */
        @SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject", "ObjectEquality"})
        private State(@Nonnull State a, @Nonnull State b) {
            this.freeIdentifiers = a.freeIdentifiers.merge(b.freeIdentifiers, (NonEmptyImmutableList<Reference> list1, NonEmptyImmutableList<Reference> list2) -> (NonEmptyImmutableList<Reference>)list1.append(list2));
            this.functionScopedDeclarations = a.functionScopedDeclarations.merge(b.functionScopedDeclarations, ImmutableList::append);
            this.blockScopedDeclarations = a.blockScopedDeclarations.merge(b.blockScopedDeclarations, ImmutableList::append);
            this.functionDeclarations = a.functionDeclarations.merge(b.functionDeclarations, ImmutableList::append);
            this.children = a.children.append(b.children);
            this.dynamic = a.dynamic || b.dynamic;
            this.bindingsForParent = a.bindingsForParent.append(b.bindingsForParent);
            this.atsForParent = a.atsForParent.append(b.atsForParent);
            this.potentiallyVarScopedFunctionDeclarations = a.potentiallyVarScopedFunctionDeclarations.merge(b.potentiallyVarScopedFunctionDeclarations, ImmutableList::append);
            this.hasParameterExpressions = a.hasParameterExpressions || b.hasParameterExpressions;
        }


        /*
         * Used when a scope boundary is encountered. It resolves the free identifiers
         * and declarations found into variable objects. Any free identifiers remaining
         * are carried forward into the new state object.
         */
        private State finish(@Nonnull Node astNode, @Nonnull Scope.Type scopeType) {
            return finish(astNode, scopeType, false, false);
        }

        private State finish(@Nonnull Node astNode, @Nonnull Scope.Type scopeType, boolean resolveArguments, boolean shouldB33) {
            ImmutableList<Variable> variables = ImmutableList.empty();

            HashTable<String, ImmutableList<Declaration>> functionScope = HashTable.emptyUsingEquality();
            HashTable<String, NonEmptyImmutableList<Reference>> freeIdentifiers = this.freeIdentifiers;
            HashTable<String, ImmutableList<Declaration>> potentiallyVarScopedFunctionDeclarations = this.potentiallyVarScopedFunctionDeclarations;
            ImmutableList<Scope> children = this.children;

            for (Pair<String, ImmutableList<Declaration>> name :  this.blockScopedDeclarations.entries()) {
                potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(name.left());
            }
            for (Pair<String, ImmutableList<Declaration>> fdecl :  this.functionDeclarations.entries()) {
                Maybe<ImmutableList<Declaration>> maybeConflict = this.potentiallyVarScopedFunctionDeclarations.get(fdecl.left());
                if (maybeConflict.isJust()) {
                    ImmutableList<Declaration> existingDeclarations = maybeConflict.fromJust();
                    ImmutableList<Declaration> newDeclarations = fdecl.right();
                    if (existingDeclarations.length != 1 || existingDeclarations.maybeHead().fromJust().node != newDeclarations.maybeHead().fromJust().node) { // don't conflict with your own lexical declaration
                        potentiallyVarScopedFunctionDeclarations = potentiallyVarScopedFunctionDeclarations.remove(fdecl.left());
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
                        String name2 = entry2.left();
                        ImmutableList<Declaration> declarations2 = entry2.right();
                        ImmutableList<Reference> references2 = freeIdentifiers.get(name2).map(referenceList -> (ImmutableList<Reference>)referenceList).orJust(ImmutableList.empty());
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
                            String name = entry.left();
                            ImmutableList<Declaration> declarations = entry.right();
                            ImmutableList<Reference> references = freeIdentifiers.get(name).map(referenceList -> (ImmutableList<Reference>)referenceList).orJust(ImmutableList.empty());
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
                        String name = entry.left();
                        ImmutableList<Declaration> declarations = entry.right();
                        ImmutableList<Reference> references = freeIdentifiers.get(name).map(referenceList -> (ImmutableList<Reference>)referenceList).orJust(ImmutableList.empty());
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
                    ImmutableList.of(scope), false, this.bindingsForParent, this.atsForParent, potentiallyVarScopedFunctionDeclarations, this.hasParameterExpressions);
        }

        /*
         * Observe variables entering scope
         */
        @Nonnull
        private State addDeclarations(@Nonnull Kind kind) {
            return addDeclarations(kind, false);
        }

        @Nonnull
        private State addDeclarations(@Nonnull Kind kind, boolean keepBindingsForParent) {
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
                    keepBindingsForParent ? this.atsForParent : ImmutableList.empty(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @Nonnull
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
                    ImmutableList.empty(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        /*
         * Observe references
         */
        @Nonnull
        public State addReferences(@Nonnull Accessibility accessibility) {
            return addReferences(accessibility, false);
        }

        @Nonnull
        private State addReferences(@Nonnull Accessibility accessibility, boolean keepBindingsForParent) {
            HashTable<String, NonEmptyImmutableList<Reference>> free = this.freeIdentifiers;
            for (BindingIdentifier binding : this.bindingsForParent) {
                assert accessibility.isWrite(); // todo confirm and remove
                Reference ref = new Reference(binding);
                ImmutableList<Reference> refs = free.get(binding.name).map(referenceList -> (ImmutableList<Reference>)referenceList).orJust(ImmutableList.empty());
                NonEmptyImmutableList<Reference> refsNonEmpty = refs.cons(ref);
                free = free.put(binding.name, refsNonEmpty);
            }
            for (AssignmentTargetIdentifier ati : this.atsForParent) {
                Reference ref = new Reference(ati, accessibility);
                ImmutableList<Reference> refs = free.get(ati.name).map(referenceList -> (ImmutableList<Reference>)referenceList).orJust(ImmutableList.empty());
                NonEmptyImmutableList<Reference> refsNonEmpty = refs.cons(ref);
                free = free.put(ati.name, refsNonEmpty);
            }
            return new State(
                    free,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    keepBindingsForParent ? this.bindingsForParent : ImmutableList.empty(),
                    keepBindingsForParent ? this.atsForParent : ImmutableList.empty(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @Nonnull
        public State taint() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    true,
                    this.bindingsForParent,
                    this.atsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @Nonnull
        public State withoutBindingsForParent() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    ImmutableList.empty(),
                    ImmutableList.empty(),
                    this.potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }

        @Nonnull
        public State withParameterExpressions() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    this.bindingsForParent,
                    this.atsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    true
            );
        }

        @Nonnull
        public State withoutParameterExpressions() {
            return new State(
                    this.freeIdentifiers,
                    this.functionScopedDeclarations,
                    this.blockScopedDeclarations,
                    this.functionDeclarations,
                    this.children,
                    this.dynamic,
                    this.bindingsForParent,
                    this.atsForParent,
                    this.potentiallyVarScopedFunctionDeclarations,
                    false
            );
        }

        @Nonnull
        public State withPotentialVarFunctions(@Nonnull ImmutableList<BindingIdentifier> funcs) {
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
                    this.atsForParent,
                    potentiallyVarScopedFunctionDeclarations,
                    this.hasParameterExpressions
            );
        }
    }

    @SuppressWarnings("ProtectedInnerClass")
    private static final class StateMonoid implements Monoid<State> {
        @Override
        @Nonnull
        public State identity() {
            return new State();
        }

        @Override
        @Nonnull
        public State append(State a, State b) {
            if (a == b) {
                return a;
            }
            return new State(a, b);
        }
    }
}
