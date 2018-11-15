package com.shapesecurity.shift.es2017.path;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.Unit;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.reducer.MonoidalReducer;
import com.shapesecurity.shift.es2017.reducer.VisitorTestCase;
import com.shapesecurity.shift.es2017.reducer.WrappedReducer;
import org.junit.Test;

import java.io.IOException;
import java.util.IdentityHashMap;

public class BranchIteratorTest extends VisitorTestCase {
	public static void assertSanity(Program program) {
		IdentityHashMap<Node, Unit> unseenNodes = new IdentityHashMap<>(); // Why is there no IdentityHashSet?
		for (Node node : NodeCollector.collect(program)) {
			unseenNodes.put(node, Unit.unit);
		}

		for (Pair<BranchGetter, Node> p : new BranchIterator(program)) {
			Maybe<? extends Node> result = p.left.apply(program);
			assertTrue(result.isJust());
			assertSame(p.right, result.fromJust()); // not just .equals, but actually identity-equal

			assertTrue(unseenNodes.containsKey(p.right));
			unseenNodes.remove(p.right);
		}

		assertEquals(0, unseenNodes.size());
	}

	@Test
	public void testSomeLibraries() throws JsError, IOException {
		assertSanity(Parser.parseScript(readFile("libraries/jquery-1.9.1.js")));
		assertSanity(Parser.parseScript(readFile("libraries/underscore-1.5.2.js")));
	}

	static class NodeCollector extends WrappedReducer<ImmutableList<Node>> {
		static final NodeCollector INSTANCE = new NodeCollector();

		static ImmutableList<Node> collect(Program program) {
			return Director.reduceProgram(INSTANCE, program);
		}

		public NodeCollector() {
			super((node, nodes) -> nodes.cons(node), new MonoidalReducer<>(new Monoid.ImmutableListAppend<>()));
		}
	}
}
