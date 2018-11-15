package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.shift.es2017.utils.Utils;
import javax.annotation.Nonnull;

class WebSafeTokenStream extends TokenStream {
	public WebSafeTokenStream(@Nonnull StringBuilder writer) {
		super(writer);
	}

	@Override
	public void put(@Nonnull String tokenStr) {
		if (
			this.lastCodePoint == '<' && (tokenStr.startsWith("script") || tokenStr.startsWith("/script")) ||
            Utils.isIdentifierPart(this.lastCodePoint) && (Utils.isIdentifierPart(tokenStr.charAt(0)) || tokenStr.charAt(0) == '\\')
		) {
			this.lastCodePoint = ' ';
			writer.append(' ');
		}
		super.put(tokenStr);
	}
}
