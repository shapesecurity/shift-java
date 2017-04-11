package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import org.jetbrains.annotations.NotNull;

public class ParserWithLocation {
	protected HashTable<Node, SourceSpan> locations = HashTable.emptyUsingIdentity();

	public ParserWithLocation() {}

	@NotNull
	public Script parseScript(@NotNull String text) throws JsError {
		return new ParserWithLocationInternal(text, false).parseScript();
	}

	@NotNull
	public Module parseModule(@NotNull String text) throws JsError {
		return new ParserWithLocationInternal(text, true).parseModule();
	}

	@NotNull
	public Maybe<SourceSpan> getLocation(@NotNull Node node) {
		return this.locations.get(node);
	}

	private class ParserWithLocationInternal extends GenericParser<SourceLocation> {
		protected ParserWithLocationInternal(@NotNull String source, boolean isModule) throws JsError {
			super(source, isModule);
		}

		@NotNull
		@Override
		protected <T extends Node> T finishNode(@NotNull SourceLocation startLocation, @NotNull T node) {
			if (node instanceof Script || node instanceof Module) {
				// Special case: the start/end of the whole-program node is the whole text including leading and trailing whitespace.
				locations = locations.put(node, new SourceSpan(Maybe.empty(), new SourceLocation(0, 0, 0), new SourceLocation(this.startLine, this.startIndex - this.startLineStart, this.startIndex)));
				return node;
			}
			SourceLocation endLocation = this.getLastTokenEndLocation();
			locations = locations.put(node, new SourceSpan(Maybe.empty(), startLocation, endLocation));
			return node;
		}

		@NotNull
		@Override
		protected SourceLocation startNode() {
			return this.getLocation();
		}

		@NotNull
		@Override
		protected <T extends Node> T copyNode(@NotNull Node src, @NotNull T dest) {
			locations.get(src).foreach(srcSpan -> {
				locations = locations.put(dest, srcSpan);
			});
			return dest;
		}

	}
}
