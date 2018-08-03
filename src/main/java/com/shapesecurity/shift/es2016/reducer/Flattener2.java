package com.shapesecurity.shift.es2016.reducer;

import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;

import javax.annotation.Nonnull;

public class Flattener2 extends WrappedReducer<ConcatList<Node>> {
	private static final Flattener2 INSTANCE = new Flattener2();

	private Flattener2() {
		super(
			(node, nodes) -> ConcatList.of(node).append(nodes),
			new MonoidalReducer<>(new Monoid.ConcatListAppend<>())
		);
	}

	@Nonnull
	public static ImmutableList<Node> flatten(@Nonnull Program program) {
		return Director.reduceProgram(INSTANCE, program).toList();
	}
}
