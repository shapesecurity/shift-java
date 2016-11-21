package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import org.jetbrains.annotations.NotNull;

public class ParserWithLocation {
	protected HashTable<Node, SourceSpan> locations = HashTable.emptyP();

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
		protected <T extends Node> T finishNode(@NotNull SourceLocation startLocation, @NotNull T node) {
			SourceLocation endLocation = this.getLocation();
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
