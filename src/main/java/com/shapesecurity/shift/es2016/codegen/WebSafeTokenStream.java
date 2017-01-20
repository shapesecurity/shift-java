package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.shift.es2016.utils.Utils;
import org.jetbrains.annotations.NotNull;

class WebSafeTokenStream extends TokenStream {
	public WebSafeTokenStream(@NotNull StringBuilder writer) {
		super(writer);
	}

	@Override
	public void put(@NotNull String tokenStr) {
		if (
			this.lastChar == '<' && (tokenStr.startsWith("script") || tokenStr.startsWith("/script")) ||
            Utils.isIdentifierPart(this.lastChar) && (Utils.isIdentifierPart(tokenStr.charAt(0)) || tokenStr.charAt(0) == '\\')
		) {
			writer.append(this.lastChar = ' ');
		}
		super.put(tokenStr);
	}
}
