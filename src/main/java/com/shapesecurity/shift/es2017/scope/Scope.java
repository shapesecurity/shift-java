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

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.Node;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {

    @Nonnull
    public final Node astNode;
    @Nonnull
    public final HashTable<String, NonEmptyImmutableList<Reference>> through;
    @Nonnull
    public final ImmutableList<Scope> children;
    @Nonnull
    public final Type type;
    public final boolean dynamic;
    protected final Map<String, Variable> variables = new LinkedHashMap<>();

    Scope(
            @Nonnull ImmutableList<Scope> children,
            @Nonnull ImmutableList<Variable> variables,
            @Nonnull HashTable<String, NonEmptyImmutableList<Reference>> through,
            @Nonnull Type type,
            boolean isDynamic,
            @Nonnull Node astNode) {
        this.children = children;
        this.through = through;
        this.type = type;
        this.astNode = astNode;

        for (Variable var : variables) {
            this.variables.put(var.name, var);
        }

        this.dynamic = isDynamic || type == Type.With;
    }

    public boolean isGlobal() {
        return (this.type == Type.Global);
    }

    @Nonnull
    public final Maybe<Variable> lookupVariable(@Nonnull String name) {
        return Maybe.fromNullable(this.variables.get(name));
    }

    @Nonnull
    public final Collection<Variable> variables() {
        return this.variables.values();
    }


    public enum Type {
        Global,
        Module,
        Script,
        ArrowFunction,
        Function,
        FunctionName,
        ClassName,
        Parameters,
        ParameterExpression,
        With,
        Catch,
        Block
    }
}
