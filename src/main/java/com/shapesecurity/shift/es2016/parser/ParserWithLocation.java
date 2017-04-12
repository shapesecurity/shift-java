package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.ExpressionTemplateElement;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.TemplateElement;
import com.shapesecurity.shift.es2016.ast.TemplateExpression;
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
			} else if (node instanceof FunctionBody) {
				FunctionBody body = (FunctionBody) node;
				if (body.directives.isEmpty() && body.statements.isEmpty()) {
					// Special case: a function body which contains no nodes spans no tokens, so the usual logic of "start of first contained token through end of last contained token" doesn't work. We choose to define it to start and end immediately after the opening brace.
					SourceLocation endLocation = this.getLastTokenEndLocation();
					locations = locations.put(node, new SourceSpan(Maybe.empty(), endLocation, endLocation));
					return node;
				}
			} else if (node instanceof BindingIdentifier && ((BindingIdentifier) node).name.equals("*default*")) {
				// Special case: synthetic BindingIdentifier for export-default declarations should not have a location
				return node;
			} else if (node instanceof TemplateExpression) {
				// Special case: adjust the locations of TemplateElement to not include surrounding backticks or braces
				ImmutableList<ExpressionTemplateElement> elements = ((TemplateExpression) node).elements;
				for (int i = 0; i < elements.length; i += 2) {
					int endAdjustment = (i < elements.length - 1) ? 2 : 1; // discard '${' or '`' respectively

					TemplateElement element = (TemplateElement) elements.index(i).fromJust();
					SourceSpan oldLocation = locations.get(element).fromJust();
					SourceLocation newStart = new SourceLocation(oldLocation.start.line, oldLocation.start.column + 1, oldLocation.start.offset + 1); // discard '}' or '`'
					SourceLocation newEnd = new SourceLocation(oldLocation.end.line, oldLocation.end.column - endAdjustment, oldLocation.end.offset - endAdjustment);
					locations = locations.put(element, new SourceSpan(Maybe.empty(), newStart, newEnd));
				}
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
