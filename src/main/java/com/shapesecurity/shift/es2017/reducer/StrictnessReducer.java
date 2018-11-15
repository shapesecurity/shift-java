package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.es2017.ast.ArrowExpression;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.ClassExpression;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.Getter;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.Setter;

import javax.annotation.Nonnull;

// It is somewhat annoying (and unnecessarily slow) to have this be bottom-up instead of top-down. But it does work.
// Given a Script, the analyze method returns a set containing all ArrowExpression, FunctionDeclaration, FunctionExpression, and Script nodes which are sloppy mode. All other ArrowExpression, FunctionDeclaration, FunctionExpression, and Script nodes are strict.
public class StrictnessReducer extends MonoidalReducer<ImmutableSet<Node>> {
    public static final StrictnessReducer INSTANCE = new StrictnessReducer();

    private StrictnessReducer() {
        super(new Monoid.ImmutableSetIdentityUnion<>());
    }

    @Nonnull
    public static ImmutableSet<Node> analyze(@Nonnull Script script) {
        return Director.reduceScript(INSTANCE, script);
    }
    // Modules are always strict; it does not make sense to analyze one.

    private boolean hasStrict(@Nonnull ImmutableList<Directive> directives) {
        return directives.find(d -> d.rawValue.equals("use strict")).isJust();
    }


    @Nonnull
    @Override
    public ImmutableSet<Node> reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull ImmutableSet<Node> params, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceArrowExpression(node, params, body);
        if ((node.body instanceof FunctionBody) && hasStrict(((FunctionBody) node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }


    @Nonnull
    @Override
    public ImmutableSet<Node> reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull ImmutableSet<Node> name, @Nonnull Maybe<ImmutableSet<Node>> _super, @Nonnull ImmutableList<ImmutableSet<Node>> elements) {
        return this.monoidClass.identity();
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<ImmutableSet<Node>> name, @Nonnull Maybe<ImmutableSet<Node>> _super, @Nonnull ImmutableList<ImmutableSet<Node>> elements) {
        return this.monoidClass.identity();
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull ImmutableSet<Node> name, @Nonnull ImmutableSet<Node> params, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceFunctionDeclaration(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<ImmutableSet<Node>> name, @Nonnull ImmutableSet<Node> params, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceFunctionExpression(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceGetter(@Nonnull Getter node, @Nonnull ImmutableSet<Node> name, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceGetter(node, name, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceMethod(@Nonnull Method node, @Nonnull ImmutableSet<Node> name, @Nonnull ImmutableSet<Node> params, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceMethod(node, name, params, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceScript(@Nonnull Script node, @Nonnull ImmutableList<ImmutableSet<Node>> directives, @Nonnull ImmutableList<ImmutableSet<Node>> statements) {
        ImmutableSet<Node> state = super.reduceScript(node, directives, statements);
        if (hasStrict(node.directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

    @Nonnull
    @Override
    public ImmutableSet<Node> reduceSetter(@Nonnull Setter node, @Nonnull ImmutableSet<Node> name, @Nonnull ImmutableSet<Node> param, @Nonnull ImmutableSet<Node> body) {
        ImmutableSet<Node> state = super.reduceSetter(node, name, param, body);
        if (hasStrict((node.body).directives)) {
            return this.monoidClass.identity();
        }
        return state.put(node);
    }

}
