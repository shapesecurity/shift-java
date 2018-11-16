package com.shapesecurity.shift.es2017.scope;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;

public class Declaration {
    /**
     * AST node representing the declaration of this node
     */
    @Nonnull
    public final BindingIdentifier node;
    /**
     * Declared Variable kind
     */
    @Nonnull
    public final Kind kind;

    public Declaration(@Nonnull BindingIdentifier node, @Nonnull Kind kind) {
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

        @Nonnull
        public static Kind fromVariableDeclarationKind(@Nonnull VariableDeclarationKind kind) {
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
