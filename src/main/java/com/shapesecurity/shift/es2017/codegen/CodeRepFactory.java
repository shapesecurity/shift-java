package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.Expression;
import com.shapesecurity.shift.es2017.ast.operators.Precedence;
import javax.annotation.Nonnull;

public class CodeRepFactory {
    protected static final CodeRep[] EMPTY = new CodeRep[0];

    public CodeRepFactory() {
    }// CodeRep factory methods

    @Nonnull
    public CodeRep empty() {
        return new CodeRep.Empty();
    }

    @Nonnull
    public CodeRep expr(@Nonnull Expression node, @Nonnull Precedence precedence, @Nonnull CodeRep a) {
        return node.getPrecedence().ordinal() < precedence.ordinal() ? paren(a) : a;
    }

    @Nonnull
    public CodeRep token(@Nonnull String token) {
        return new CodeRep.Token(token);
    }

    @Nonnull
    public CodeRep rawToken(@Nonnull String token) {
        return new CodeRep.RawToken(token);
    }

    @Nonnull
    public CodeRep num(double value) {
        return new CodeRep.Number(value);
    }

    @Nonnull
    public CodeRep paren(@Nonnull CodeRep rep) {
        return new CodeRep.Paren(rep);
    }

    @Nonnull
    public CodeRep bracket(@Nonnull CodeRep rep) {
        return new CodeRep.Bracket(rep);
    }

    @Nonnull
    public CodeRep noIn(@Nonnull CodeRep rep) {
        return new CodeRep.NoIn(rep);
    }

    @Nonnull
    public CodeRep testIn(@Nonnull CodeRep state) {
        return state.containsIn() ? new CodeRep.ContainsIn(state) : state;
    }

    @Nonnull
    public CodeRep seq(@Nonnull CodeRep[] reps) {
        return new CodeRep.Seq(reps);
    }

    @Nonnull
    public CodeRep init(@Nonnull CodeRep id, @Nonnull Maybe<CodeRep> i) {
        return i.maybe(id, init -> new CodeRep.Init(id, init));
    }

    @Nonnull
    public CodeRep semi() {
        return new CodeRep.Semi();
    }

    @Nonnull
    public CodeRep commaSep(@Nonnull CodeRep[] pieces) {
        return new CodeRep.CommaSep(pieces);
    }

    @Nonnull
    public CodeRep brace(@Nonnull CodeRep rep) {
        return new CodeRep.Brace(rep);
    }

    @Nonnull
    public CodeRep semiOp() {
        return new CodeRep.SemiOp();
    }

    @Nonnull
    public final CodeRep commaSep(@Nonnull ImmutableList<CodeRep> reps) {
        return commaSep(reps.toArray(EMPTY));
    }

    @Nonnull
    public final CodeRep seq(@Nonnull ImmutableList<CodeRep> reps) {
        return seq(reps.toArray(EMPTY));
    }

    @Nonnull
    public CodeRep markContainsIn(@Nonnull CodeRep state) {
        return state.containsIn() ? new CodeRep.ContainsIn(state) : state;
    }

    @Nonnull
    public CodeRep markStringLiteralExpressionStatement(@Nonnull CodeRep rep) {
        return new CodeRep.StringLiteralExpressionStatement(rep);
    }
}
