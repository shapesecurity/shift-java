package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.FreePairingMonoid;
import com.shapesecurity.functional.data.Monoid;
import org.jetbrains.annotations.NotNull;

public class MultiMonoidalReducer<A, B> extends MonoidalReducer<Pair<A, B>> {
    public MultiMonoidalReducer(@NotNull Monoid<Pair<A, B>> stateMonoid) {
        super(stateMonoid);
    }

    public static <A, B> MultiMonoidalReducer<A, B> from(@NotNull Monoid<A> monoidA, @NotNull Monoid<B> monoidB) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, monoidB));
    }

    public static <A, B, C> MultiMonoidalReducer<A, Pair<B, C>> from(@NotNull Monoid<A> monoidA, @NotNull Monoid<B> monoidB, @NotNull Monoid<C> monoidC) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, new FreePairingMonoid<>(monoidB, monoidC)));
    }

    public static <A, B, C, D> MultiMonoidalReducer<A, Pair<B, Pair<C, D>>> from(@NotNull Monoid<A> monoidA, @NotNull Monoid<B> monoidB, @NotNull Monoid<C> monoidC, @NotNull Monoid<D> monoidD) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, new FreePairingMonoid<>(monoidB, new FreePairingMonoid<>(monoidC, monoidD))));
    }
}
