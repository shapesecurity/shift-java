package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.Tuple3;
import com.shapesecurity.functional.Tuple4;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.FunctionDeclaration;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.IdentifierExpression;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ScopeLookup {
    @NotNull
    private final Map<BindingIdentifier, Variable> bindingIdentifierDeclarationCache = new IdentityHashMap<>();
    @NotNull
    private final Map<BindingIdentifier, Variable> bindingIdentifierReferenceCache = new IdentityHashMap<>(); // bi to referenced variable
    @NotNull
    private final Map<IdentifierExpression, Variable> identifierExpressionReferenceCache = new IdentityHashMap<>(); // ie to referenced variable
    @NotNull
    private final Map<Node, Pair<Variable, Maybe<Variable>>> functionDeclarationCache = new IdentityHashMap<>();
    @NotNull
    private final Map<Variable, Boolean> isGlobalCache = new IdentityHashMap<>();
    @NotNull
    private final Map<Node, Scope> nodeScopeCache = new IdentityHashMap<>(); // node to the *outermost* scope associated with it. depends on the assumption that there is a unique such.

    public ScopeLookup(@NotNull GlobalScope scope) {
        scopeHelper(scope);
    }

    private void scopeHelper(@NotNull Scope scope) {
        scope.children.foreach(this::scopeHelper); // logic depends on this occurring first.
        for (Variable v : scope.variables()) { // TODO make this functional
            variableHelper(v);
            isGlobalCache.put(v, scope instanceof GlobalScope);
        }
        nodeScopeCache.put(scope.astNode, scope);
    }

    private void variableHelper(@NotNull Variable variable) {
        variable.declarations.foreach(decl -> {
            if (bindingIdentifierDeclarationCache.containsKey(decl.node)) {
                functionDeclarationCache.put(decl.node, new Pair<>(bindingIdentifierDeclarationCache.get(decl.node), Maybe.just(variable)));
            } else {
                bindingIdentifierDeclarationCache.put(decl.node, variable);
            }
        });
        variable.references.foreach(ref -> {
            ref.node.foreach(
                    bi -> bindingIdentifierReferenceCache.put(bi, variable),
                    ie -> identifierExpressionReferenceCache.put(ie, variable)
            );
        });
    }

    public Variable findVariableDeclaredBy(@NotNull BindingIdentifier bindingIdentifier) { // NB: When used with function declarations, which can declare multiple variables under B.3.3, returns the lexical binding.
        return bindingIdentifierDeclarationCache.get(bindingIdentifier);
    }

    public Variable findVariableReferencedBy(@NotNull BindingIdentifier bindingIdentifier) {
        return bindingIdentifierReferenceCache.get(bindingIdentifier);
    }

    public Variable findVariableReferencedBy(@NotNull IdentifierExpression identifierExpression) {
        return identifierExpressionReferenceCache.get(identifierExpression);
    }

    // Because of annex B.3.3, in addition to a lexical binding (outside of scripts, which ???), functions may create a variable
    // binding for themselves. This helper gets both the (necessarily created) lexical binding and the (possible) variable binding.
    // Takes a FunctionDeclaration to ensure it is not misused, but only actually needs its binding identifier.
    // Assuming the function declaration occurs somewhere in the AST corresponding to this global scope,
    // there always will be at least one variable declared by the given function.
    // Returns (lexical, variable)
    public Pair<Variable, Maybe<Variable>> findVariablesForFuncDecl(@NotNull final FunctionDeclaration func) {
        if (functionDeclarationCache.containsKey(func.name)) {
            return functionDeclarationCache.get(func.name);
        } else {
            return new Pair<>(bindingIdentifierDeclarationCache.get(func.name), Maybe.nothing());
        }
    }

    public boolean isGlobal(@NotNull Variable variable) {
        return isGlobalCache.get(variable);
    }

    public Scope findScopeFor(@NotNull Node node) {
        return nodeScopeCache.get(node);
    }
}
