package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.Maybe;

import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import javax.annotation.Nonnull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class ScopeLookup {
    @Nonnull
    private final Map<BindingIdentifier, Variable> bindingIdentifierDeclarationCache = new IdentityHashMap<>();
    @Nonnull
    private final Map<BindingIdentifier, Variable> bindingIdentifierReferenceCache = new IdentityHashMap<>(); // bi to referenced variable
    @Nonnull
    private final Map<AssignmentTargetIdentifier, Variable> assignmentTargetIdentifierReferenceCache = new IdentityHashMap<>(); // ati to referenced variable
    @Nonnull
    private final Map<IdentifierExpression, Variable> identifierExpressionReferenceCache = new IdentityHashMap<>(); // ie to referenced variable
    @Nonnull
    private final Map<Node, Pair<Variable, Maybe<Variable>>> functionDeclarationCache = new IdentityHashMap<>();
    @Nonnull
    private final Map<Variable, Boolean> isGlobalCache = new IdentityHashMap<>();
    @Nonnull
    private final Map<Node, Scope> nodeScopeCache = new IdentityHashMap<>(); // node to the *outermost* scope associated with it. depends on the assumption that there is a unique such.

    public ScopeLookup(@Nonnull GlobalScope scope) {
        scopeHelper(scope);
    }

    private void scopeHelper(@Nonnull Scope scope) {
        scope.children.forEach(this::scopeHelper); // logic depends on this occurring first.
        for (Variable v : scope.variables()) { // TODO make this functional
            variableHelper(v);
            isGlobalCache.put(v, scope instanceof GlobalScope);
        }
        nodeScopeCache.put(scope.astNode, scope);
    }

    private void variableHelper(@Nonnull Variable variable) {
        variable.declarations.forEach(decl -> {
            if (bindingIdentifierDeclarationCache.containsKey(decl.node)) {
                functionDeclarationCache.put(decl.node, new Pair<>(bindingIdentifierDeclarationCache.get(decl.node), Maybe.of(variable)));
            } else {
                bindingIdentifierDeclarationCache.put(decl.node, variable);
            }
        });
        variable.references.forEach(ref -> {
            if (ref.node instanceof AssignmentTargetIdentifier) {
                assignmentTargetIdentifierReferenceCache.put((AssignmentTargetIdentifier) ref.node, variable);
            } else if (ref.node instanceof BindingIdentifier) {
                bindingIdentifierReferenceCache.put((BindingIdentifier) ref.node, variable);
            } else if (ref.node instanceof IdentifierExpression) {
                identifierExpressionReferenceCache.put((IdentifierExpression) ref.node, variable);
            } else {
                throw new RuntimeException("Not reached");
            }
        });
    }

    @Nonnull
    public Maybe<Variable> findVariableDeclaredBy(@Nonnull BindingIdentifier bindingIdentifier) { // NB: When used with function declarations, which can declare multiple variables under B.3.3, returns the lexical binding. When used with class declarations, returns the class-local binding.
        return Maybe.fromNullable(bindingIdentifierDeclarationCache.get(bindingIdentifier));
    }

    @Nonnull
    public Variable findVariableReferencedBy(@Nonnull AssignmentTargetIdentifier assignmentTargetIdentifier) {
        Variable v = assignmentTargetIdentifierReferenceCache.get(assignmentTargetIdentifier);
        if (v == null) {
            throw new NoSuchElementException("AssignmentTargetIdentifier not present in AST");
        }
        return v;
    }

    @Nonnull
    public Maybe<Variable> findVariableReferencedBy(@Nonnull BindingIdentifier bindingIdentifier) {
        return Maybe.fromNullable(bindingIdentifierReferenceCache.get(bindingIdentifier));
    }

    @Nonnull
    public Variable findVariableReferencedBy(@Nonnull IdentifierExpression identifierExpression) {
        Variable v = identifierExpressionReferenceCache.get(identifierExpression);
        if (v == null) {
            throw new NoSuchElementException("IdentifierExpression not present in AST");
        }
        return v;
    }

    // Because of annex B.3.3, in addition to a lexical binding (outside of scripts, which ???), functions may create a variable
    // binding for themselves. This helper gets both the (necessarily created) lexical binding and the (possible) variable binding.
    // Takes a FunctionDeclaration to ensure it is not misused, but only actually needs its binding identifier.
    // Assuming the function declaration occurs somewhere in the AST corresponding to this global scope,
    // there always will be at least one variable declared by the given function.
    // Returns (lexical, variable)
    @Nonnull
    public Pair<Variable, Maybe<Variable>> findVariablesForFuncDecl(@Nonnull final FunctionDeclaration func) {
        if (func.name.name.equals("*default*")) {
            throw new IllegalArgumentException("Can't lookup default exports");
        }
        if (functionDeclarationCache.containsKey(func.name)) {
            return functionDeclarationCache.get(func.name);
        } else {
            Variable v = bindingIdentifierDeclarationCache.get(func.name);
            if (v == null) {
                throw new NoSuchElementException("Function declaration present in AST");
            }
            return new Pair<>(v, Maybe.empty());
        }
    }

    // Class declarations always create two variables. This function returns both: (class-local, outer).
    @Nonnull
    public Pair<Variable, Variable> findVariablesForClassDecl(@Nonnull final ClassDeclaration cl) {
        if (cl.name.name.equals("*default*")) {
            throw new IllegalArgumentException("Can't lookup default exports");
        }
        Pair<Variable, Maybe<Variable>> vs = functionDeclarationCache.get(cl.name);
        if (vs == null) {
            throw new NoSuchElementException("Class declaration not present in AST");
        }
        return new Pair<>(vs.left(), vs.right().fromJust());
    }

    public boolean isGlobal(@Nonnull Variable variable) {
        return isGlobalCache.get(variable);
    }

    @Nonnull
    public Maybe<Scope> findScopeFor(@Nonnull Node node) {
        return Maybe.fromNullable(nodeScopeCache.get(node));
    }
}
