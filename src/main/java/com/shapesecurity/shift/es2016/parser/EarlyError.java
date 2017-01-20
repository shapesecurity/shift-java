package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.shift.es2016.ast.Node;

import org.jetbrains.annotations.NotNull;

public class EarlyError {
    @NotNull
    public final Node node;
    @NotNull
    public final String message;

    public EarlyError(@NotNull Node node, @NotNull String message) {
        this.node = node;
        this.message = message;
    }
}
