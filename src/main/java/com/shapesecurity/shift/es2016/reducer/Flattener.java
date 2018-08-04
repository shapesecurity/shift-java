package com.shapesecurity.shift.es2016.reducer;

import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;

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
