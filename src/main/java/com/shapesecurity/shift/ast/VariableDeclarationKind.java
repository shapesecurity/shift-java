package com.shapesecurity.shift.ast;

public enum VariableDeclarationKind {
    Var("var"),
    Const("const"),
    Let("let");
    public final String name;

    private VariableDeclarationKind(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
