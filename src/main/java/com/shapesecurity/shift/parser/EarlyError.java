package com.shapesecurity.shift.parser;

import com.shapesecurity.shift.ast.Node;

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
