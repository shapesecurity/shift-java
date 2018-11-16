package com.shapesecurity.shift.es2017.codegen.location;

import com.shapesecurity.shift.es2017.codegen.TokenStream;
import com.shapesecurity.shift.es2017.parser.SourceLocation;
import com.shapesecurity.shift.es2017.utils.Utils;
import javax.annotation.Nonnull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenStreamWithLocation extends TokenStream {
	@Nonnull
	public LocationMeta meta;

	protected int line = 0;
	protected int lineStart = 0;
	private SourceLocation cachedSourceLocation;
	private int lastCachedSourceLocation = -1;

	protected static final Pattern linebreakPattern = Pattern.compile("\r\n?|[\n\u2028\u2029]");

	@Nonnull
	public SourceLocation getLocation() {
		int index = this.writer.length();
		if (this.lastCachedSourceLocation != this.writer.length()) {
			this.cachedSourceLocation = new SourceLocation(this.line + 1, index - this.lineStart, index);
			this.lastCachedSourceLocation = index;
		}
		return this.cachedSourceLocation;
	}


	public TokenStreamWithLocation(@Nonnull StringBuilder writer, @Nonnull LocationMeta meta) {
		super(writer);
		this.meta = meta;
	}

	@Override
	public void putRaw(@Nonnull String tokenStr) {
		this.meta.startNodes(this.getLocation());

		Matcher linebreakMatcher = linebreakPattern.matcher(tokenStr);
		while (linebreakMatcher.find()) {
			this.line++;
			this.lineStart = this.writer.length() + linebreakMatcher.end();
		}

		this.writer.append(tokenStr);
	}

	@Override
	public void put(@Nonnull String tokenStr) {
		if (tokenStr.length() == 0) return;

		if (this.optionalSemi) {
			this.optionalSemi = false;
			if (!tokenStr.equals("}")) {
				this.meta.incrementStatements();
				this.writer.append(";");
				this.lastCodePoint = '}';
			}
		}
		this.meta.finishingStatements.clear();

		if (this.lastNumber != null && tokenStr.length() == 1) {
			if (String.valueOf(tokenStr).equals(".")) {
				assert this.meta.startingNodes.isEmpty(); // so it's safe to not call startNodes

				boolean needsDoubleDot = numberNeedsDoubleDot(this.lastNumber);
				if (needsDoubleDot) this.meta.incrementNumber();
				this.writer.append(needsDoubleDot ? ".." : ".");
				this.lastNumber = null;
				this.lastCodePoint = '.';
				return;
			}
		}
		this.lastNumber = null;
		this.meta.lastNumberNode = null;

		int rightCodePoint = tokenStr.codePointAt(0);
		int lastCodePoint = this.lastCodePoint;
		char lastChar = tokenStr.charAt(tokenStr.length() - 1);
		if (lastChar >= 0xDC00 && lastChar <= 0xDFFF) {
			this.lastCodePoint = tokenStr.codePointAt(tokenStr.length() - 2);
		} else {
			this.lastCodePoint = lastChar;
		}
		if ((lastCodePoint == '+' || lastCodePoint == '-') && lastCodePoint == rightCodePoint ||
				Utils.isIdentifierPart(lastCodePoint) && Utils.isIdentifierPart(rightCodePoint) ||
				lastCodePoint == '/' && (rightCodePoint == 'i' || rightCodePoint == '/')) {
			this.writer.append(' ');
		}
		if (this.writer.length() >= 2 && tokenStr.equals("--") && this.writer.substring(this.writer.length() - 2,
				this.writer.length()).equals("<!")) {
			this.writer.append(' ');
		}

		this.meta.startNodes(this.getLocation());

		Matcher linebreakMatcher = linebreakPattern.matcher(tokenStr);
		while (linebreakMatcher.find()) {
			this.line++;
			this.lineStart = this.writer.length() + linebreakMatcher.end();
		}

		this.writer.append(tokenStr);
	}
}
