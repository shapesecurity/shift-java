package com.shapesecurity.shift.es2016.scope;

import org.jetbrains.annotations.NotNull;

import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationKind;

public class Declaration {
    /**
     * AST node representing the declaration of this node
     */
    @NotNull
    public final BindingIdentifier node;
    /**
     * Declared Variable kind
     */
    @NotNull
    public final Kind kind;

    public Declaration(@NotNull BindingIdentifier node, @NotNull Kind kind) {
        this.node = node;
        this.kind = kind;
    }

    public enum Kind {
        Var(false),
        Const(true),
        Let(true),
        FunctionDeclaration(true),
        FunctionB33(false),
        FunctionExpressionName(true),
        ClassDeclaration(true),
        ClassName(true),
        Parameter(false),
        CatchParameter(true),
        Import(true);

        public final boolean isFunctionScoped;
        public final boolean isBlockScoped;

        Kind(boolean isBlockScoped) {
            this.isFunctionScoped = !isBlockScoped;
            this.isBlockScoped = isBlockScoped;
        }

        @NotNull
        public static Kind fromVariableDeclarationKind(@NotNull VariableDeclarationKind kind) {
            switch (kind) {
                case Var:
                    return Kind.Var;
                case Const:
                    return Kind.Const;
                case Let:
                    return Kind.Let;
                default:
                    throw new RuntimeException("not reached");
            }
        }
    }
}
