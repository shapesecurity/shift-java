package com.shapesecurity.shift.es2016.parser.token;

import com.shapesecurity.shift.es2016.parser.TokenType;
import com.shapesecurity.shift.es2016.parser.SourceRange;
import com.shapesecurity.shift.es2016.parser.Token;

import org.jetbrains.annotations.NotNull;

public final class TemplateToken extends Token {

    public final boolean tail;

    public TemplateToken(@NotNull SourceRange slice, boolean tail) {
        super(TokenType.TEMPLATE, slice);
        this.tail = tail;
    }

    @NotNull
    @Override
    public CharSequence getValueString() {
        return this.slice;
    }
}
