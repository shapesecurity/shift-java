package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import org.jetbrains.annotations.NotNull;

public class ParserWithLocation {
	protected HashTable<Node, SourceSpan> locations = HashTable.empty();

	public ParserWithLocation() {}

	@NotNull
	public Script parseScript(@NotNull String text) throws JsError {
		ParserWithLocationInternal p = new ParserWithLocationInternal(text, false);
		return p.parseTopLevel(p::parseStatementListItem, Script::new);
	}

	@NotNull
	public Module parseModule(@NotNull String text) throws JsError {
		ParserWithLocationInternal p = new ParserWithLocationInternal(text, true);
		return p.parseTopLevel(p::parseModuleItem, Module::new);
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
			SourceLocation endLocation = new SourceLocation(this.lastLine + 1, this.lastIndex - this.lastLineStart, this.lastIndex);
			locations = locations.put(node, new SourceSpan(Maybe.nothing(), startLocation, endLocation));
			return node;
		}

		@NotNull
		@Override
		protected SourceLocation startNode() {
			return this.getLocation();
		}
	}
}
