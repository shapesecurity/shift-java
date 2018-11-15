/**
 * Copyright 2018 Shape Security, Inc.
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


package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;

import javax.annotation.Nonnull;

public class Flattener {
    private static final Reducer<ConcatList<Node>> INSTANCE = new WrappedReducer<>(
        (node, nodes) -> ConcatList.of(node).append(nodes),
        new MonoidalReducer<>(new Monoid.ConcatListAppend<>())
    );

    private Flattener() {}

    @Nonnull
    public static ImmutableList<Node> flatten(@Nonnull Program program) {
        return Director.reduceProgram(INSTANCE, program).toList();
    }
}
