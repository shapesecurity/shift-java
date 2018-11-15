package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.FreePairingMonoid;
import com.shapesecurity.functional.data.Monoid;

import javax.annotation.Nonnull;

public class MultiMonoidalReducer<A, B> extends MonoidalReducer<Pair<A, B>> {
    public MultiMonoidalReducer(@Nonnull Monoid<Pair<A, B>> stateMonoid) {
        super(stateMonoid);
    }

    public static <A, B> MultiMonoidalReducer<A, B> from(@Nonnull Monoid<A> monoidA, @Nonnull Monoid<B> monoidB) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, monoidB));
    }

    public static <A, B, C> MultiMonoidalReducer<A, Pair<B, C>> from(@Nonnull Monoid<A> monoidA, @Nonnull Monoid<B> monoidB, @Nonnull Monoid<C> monoidC) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, new FreePairingMonoid<>(monoidB, monoidC)));
    }

    public static <A, B, C, D> MultiMonoidalReducer<A, Pair<B, Pair<C, D>>> from(@Nonnull Monoid<A> monoidA, @Nonnull Monoid<B> monoidB, @Nonnull Monoid<C> monoidC, @Nonnull Monoid<D> monoidD) {
        return new MultiMonoidalReducer<>(new FreePairingMonoid<>(monoidA, new FreePairingMonoid<>(monoidB, new FreePairingMonoid<>(monoidC, monoidD))));
    }
}
