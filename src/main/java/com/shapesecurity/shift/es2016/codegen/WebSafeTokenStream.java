package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.shift.es2016.utils.Utils;
import javax.annotation.Nonnull;

class WebSafeTokenStream extends TokenStream {
	public WebSafeTokenStream(@Nonnull StringBuilder writer) {
		super(writer);
	}

	@Override
	public void put(@Nonnull String tokenStr) {
		if (
			this.lastChar == '<' && (tokenStr.startsWith("script") || tokenStr.startsWith("/script")) ||
            Utils.isIdentifierPart(this.lastChar) && (Utils.isIdentifierPart(tokenStr.charAt(0)) || tokenStr.charAt(0) == '\\')
		) {
			writer.append(this.lastChar = ' ');
		}
		super.put(tokenStr);
	}
}
