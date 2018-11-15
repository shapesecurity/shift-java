package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ExpressionTemplateElement;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.TemplateElement;
import com.shapesecurity.shift.es2017.ast.TemplateExpression;
import com.shapesecurity.shift.es2017.utils.WithLocation;

import javax.annotation.Nonnull;

import static com.shapesecurity.shift.es2017.utils.Utils.isLineTerminator;

public class ParserWithLocation implements WithLocation {
	public static class Comment {
		public enum Type {
			SingleLine,
			MultiLine,
			HTMLOpen,
			HTMLClose,
		}

		public final Type type;
		public final String text;
		public final SourceLocation start;
		public final SourceLocation end;

		public Comment(Type type, String text, SourceLocation start, SourceLocation end) {
			this.type = type;
			this.text = text;
			this.start = start;
			this.end = end;
		}
	}

	protected HashTable<Node, SourceSpan> locations = HashTable.emptyUsingIdentity();

	protected ImmutableList<Comment> comments = ImmutableList.empty();

	public ParserWithLocation() {}

	@Nonnull
	public Script parseScript(@Nonnull String text) throws JsError {
		return new ParserWithLocationInternal(text, false).parseScript();
	}

	@Nonnull
	public Module parseModule(@Nonnull String text) throws JsError {
		return new ParserWithLocationInternal(text, true).parseModule();
	}

	@Nonnull
	public Maybe<SourceSpan> getLocation(@Nonnull Node node) {
		return this.locations.get(node);
	}

	@Nonnull
	public ImmutableList<Comment> getComments() {
		return this.comments.reverse();
	}

	private class ParserWithLocationInternal extends GenericParser<SourceLocation> {
		protected ParserWithLocationInternal(@Nonnull String source, boolean isModule) throws JsError {
			super(source, isModule);
		}

		@Nonnull
		@Override
		protected <T extends Node> T finishNode(@Nonnull SourceLocation startLocation, @Nonnull T node) {
			if (node instanceof Script || node instanceof Module) {
				// Special case: the start/end of the whole-program node is the whole text including leading and trailing whitespace.
				locations = locations.put(node, new SourceSpan(Maybe.empty(), new SourceLocation(1, 0, 0), new SourceLocation(this.startLine + 1, this.startIndex - this.startLineStart, this.startIndex)));
				return node;
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

		@Nonnull
		@Override
		protected SourceLocation startNode() {
			return this.getLocation();
		}

		@Nonnull
		@Override
		protected <T extends Node> T copyNode(@Nonnull Node src, @Nonnull T dest) {
			locations.get(src).foreach(srcSpan -> {
				locations = locations.put(dest, srcSpan);
			});
			return dest;
		}

		@Override
		protected void skipSingleLineComment(int offset) {
			char c = this.source.charAt(this.index);
			Comment.Type type = c == '/' ? Comment.Type.SingleLine : c == '<' ? Comment.Type.HTMLOpen : Comment.Type.HTMLClose;
			SourceLocation start = new SourceLocation(this.line + 1, this.index - this.lineStart, this.index);
			super.skipSingleLineComment(offset);
			SourceLocation end = new SourceLocation(this.line + 1, this.index - this.lineStart, this.index);
			int trailingLineTerminatorCharacters = this.source.charAt(this.index - 2) == '\r' ? 2 : isLineTerminator(this.source.charAt(this.index - 1)) ? 1 : 0;
			String text = this.source.substring(start.offset + offset, end.offset - trailingLineTerminatorCharacters);
			comments = comments.cons(new Comment(type, text, start, end));
		}

		@Override
		protected boolean skipMultiLineComment() throws JsError {
			SourceLocation start = new SourceLocation(this.line + 1, this.index - this.lineStart, this.index);
			boolean isLineStart = super.skipMultiLineComment();
			SourceLocation end = new SourceLocation(this.line + 1, this.index - this.lineStart, this.index);
			String text = this.source.substring(start.offset + 2, end.offset - 2);
			comments = comments.cons(new Comment(Comment.Type.MultiLine, text, start, end));
			return isLineStart;
		}
	}
}
