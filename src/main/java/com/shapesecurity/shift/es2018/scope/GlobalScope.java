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

package com.shapesecurity.shift.es2018.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2018.ast.Node;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GlobalScope extends Scope {
    GlobalScope(
            @Nonnull ImmutableList<Scope> children,
            @Nonnull ImmutableList<Variable> variables,
            @Nonnull HashTable<String, NonEmptyImmutableList<Reference>> through,
            @Nonnull Node astNode) {
        super(children, variables, through, Type.Global, true, astNode);
        List<Pair<String, NonEmptyImmutableList<Reference>>> throughSorted = StreamSupport.stream(Spliterators.spliteratorUnknownSize(through.iterator(), Spliterator.ORDERED), false)
            .sorted(Comparator.comparing(o -> o.left)).collect(Collectors.toList());
        for (Pair<String, NonEmptyImmutableList<Reference>> var : throughSorted) {
            this.variables.put(var.left(), new Variable(var.left(), var.right(), ImmutableList.empty()));
        }
    }
}
