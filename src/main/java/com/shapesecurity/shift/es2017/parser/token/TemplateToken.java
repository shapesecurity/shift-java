package com.shapesecurity.shift.es2017.parser.token;

import com.shapesecurity.shift.es2017.parser.TokenType;
import com.shapesecurity.shift.es2017.parser.SourceRange;
import com.shapesecurity.shift.es2017.parser.Token;

import javax.annotation.Nonnull;

public final class TemplateToken extends Token {

    public final boolean tail;

    public TemplateToken(@Nonnull SourceRange slice, boolean tail) {
        super(TokenType.TEMPLATE, slice);
        this.tail = tail;
    }

    @Nonnull
    @Override
    public CharSequence getValueString() {
        return this.slice;
    }
}
