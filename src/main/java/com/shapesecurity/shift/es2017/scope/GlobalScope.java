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
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2017.ast.Node;

import javax.annotation.Nonnull;

public class GlobalScope extends Scope {
    GlobalScope(
            @Nonnull ImmutableList<Scope> children,
            @Nonnull ImmutableList<Variable> variables,
            @Nonnull HashTable<String, NonEmptyImmutableList<Reference>> through,
            @Nonnull Node astNode) {
        super(children, variables, through, Type.Global, true, astNode);
        for (Pair<String, NonEmptyImmutableList<Reference>> var : through.entries()) {
            this.variables.put(var.left(), new Variable(var.left(), var.right(), ImmutableList.empty()));
        }
    }
}
