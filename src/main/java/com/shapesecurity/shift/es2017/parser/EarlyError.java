package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.shift.es2017.ast.Node;

import javax.annotation.Nonnull;

public class EarlyError {
    @Nonnull
    public final Node node;
    @Nonnull
    public final String message;

    public EarlyError(@Nonnull Node node, @Nonnull String message) {
        this.node = node;
        this.message = message;
    }
}
