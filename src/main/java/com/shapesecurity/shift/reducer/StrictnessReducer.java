package com.shapesecurity.shift.reducer;

import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.ArrowExpression;
import com.shapesecurity.shift.ast.ClassDeclaration;
import com.shapesecurity.shift.ast.ClassExpression;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.FunctionDeclaration;
import com.shapesecurity.shift.ast.FunctionExpression;
import com.shapesecurity.shift.ast.Getter;
import com.shapesecurity.shift.ast.Method;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Setter;
import com.shapesecurity.shift.reducer.Director;
import com.shapesecurity.shift.reducer.MonoidalReducer;

import org.jetbrains.annotations.NotNull;

// It is somewhat annoying (and unnecessarily slow) to have this be bottom-up instead of top-down. But it does work.
// Given a Script, the analyze method returns a set containing all ArrowExpression, FunctionDeclaration, FunctionExpression, and Script nodes which are sloppy mode. All other ArrowExpression, FunctionDeclaration, FunctionExpression, and Script nodes are strict.
public class StrictnessReducer extends MonoidalReducer<ImmutableSet<Node>> {
    public static final StrictnessReducer INSTANCE = new StrictnessReducer();

    private StrictnessReducer() {
        super(new Monoid.ImmutableSetIdentityUnion<>());
    }

    @NotNull
    public static ImmutableSet<Node> analyze(@NotNull Script script) {
        return Director.reduceScript(INSTANCE, script);
    }
    // Modules are always strict; it does not make sense to analyze one.

    private boolean hasStrict(@NotNull ImmutableList<Directive> directives) {
        return directives.find(d -> d.rawValue.equals("use strict")).isJust();
    }


    @NotNull
    @Override
    public ImmutableSet<Node> reduceArrowExpression(@NotNull ArrowExpression node, @NotNull ImmutableSet<Node> params, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceArrowExpression(node, params, body);
        if ((node.body instanceof FunctionBody) && hasStrict(((FunctionBody) node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }


    @NotNull
    @Override
    public ImmutableSet<Node> reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull ImmutableSet<Node> name, @NotNull Maybe<ImmutableSet<Node>> _super, @NotNull ImmutableList<ImmutableSet<Node>> elements) {
        return this.monoidClass.identity();
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<ImmutableSet<Node>> name, @NotNull Maybe<ImmutableSet<Node>> _super, @NotNull ImmutableList<ImmutableSet<Node>> elements) {
        return this.monoidClass.identity();
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull ImmutableSet<Node> name, @NotNull ImmutableSet<Node> params, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceFunctionDeclaration(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<ImmutableSet<Node>> name, @NotNull ImmutableSet<Node> params, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceFunctionExpression(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceGetter(@NotNull Getter node, @NotNull ImmutableSet<Node> name, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceGetter(node, name, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceMethod(@NotNull Method node, @NotNull ImmutableSet<Node> name, @NotNull ImmutableSet<Node> params, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceMethod(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceScript(@NotNull Script node, @NotNull ImmutableList<ImmutableSet<Node>> directives, @NotNull ImmutableList<ImmutableSet<Node>> statements) {
        ImmutableSet<Node> state = super.reduceScript(node, directives, statements);
        if (hasStrict(node.directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @NotNull
    @Override
    public ImmutableSet<Node> reduceSetter(@NotNull Setter node, @NotNull ImmutableSet<Node> name, @NotNull ImmutableSet<Node> param, @NotNull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceSetter(node, name, param, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

}
