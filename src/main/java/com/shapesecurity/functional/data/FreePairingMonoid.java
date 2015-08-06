package com.shapesecurity.functional.data;

import com.shapesecurity.functional.Pair;

import org.jetbrains.annotations.NotNull;

public final class FreePairingMonoid<A, B> implements Monoid<Pair<A, B>> {
    @NotNull
    private final Monoid<A> monoidA;
    @NotNull
    private final Monoid<B> monoidB;

    public FreePairingMonoid(@NotNull Monoid<A> monoidA, @NotNull Monoid<B> monoidB) {
        this.monoidA = monoidA;
        this.monoidB = monoidB;
    }

    @NotNull
    @Override
    public Pair<A, B> identity() {
        return new Pair<>(this.monoidA.identity(), this.monoidB.identity());
    }

    @NotNull
    @Override
    public Pair<A, B> append(Pair<A, B> a, Pair<A, B> b) {
        return new Pair<>(monoidA.append(a.a, b.a), monoidB.append(a.b, b.b));
    }
}