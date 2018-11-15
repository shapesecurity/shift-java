
package com.shapesecurity.shift.es2017.utils;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.Maybe;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Objects;

// TODO port this to shape-functional-java
@CheckReturnValue
public final class Either3<A, B, C> {
	private final Object data;

	private enum Tag {
		LEFT, MIDDLE, RIGHT
	}

	private final Tag tag;

	private Either3(Object data, Tag tag) {
		super();
		this.data = data;
		this.tag = tag;
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> left(@Nonnull A a) {
		return new Either3<>(a, Tag.LEFT);
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> middle(@Nonnull B b) {
		return new Either3<>(b, Tag.MIDDLE);
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> right(@Nonnull C c) {
		return new Either3<>(c, Tag.RIGHT);
	}

	public final boolean isLeft() {
		return this.tag == Tag.LEFT;
	}

	public final boolean isMiddle() {
		return this.tag == Tag.MIDDLE;
	}

	public final boolean isRight() {
		return this.tag == Tag.RIGHT;
	}

	@SuppressWarnings("unchecked")
	public <X> X either(F<A, X> f1, F<B, X> f2, F<C, X> f3) {
		if (this.tag == Tag.LEFT) {
			return f1.apply((A) this.data);
		} else if (this.tag == Tag.MIDDLE) {
			return f2.apply((B) this.data);
		} else {
			return f3.apply((C) this.data);
		}
	}

	@Nonnull
	public <X, Y, Z> Either3<X, Y, Z> map(F<A, X> f1, F<B, Y> f2,  F<C, Z> f3) {
		return this.either(a -> Either3.<X, Y, Z>left(f1.apply(a)), b -> Either3.<X, Y, Z>middle(f2.apply(b)), c -> Either3.<X, Y, Z>right(f3.apply(c)));
	}

	@Nonnull
	public <X> Either3<X, B, C> mapLeft(@Nonnull F<A, X> f) {
		return this.map(f, b -> b, c -> c);
	}

	@Nonnull
	public <Y> Either3<A, Y, C> mapMiddle(@Nonnull F<B, Y> f) {
		return this.map(a -> a, f, c -> c);
	}

	@Nonnull
	public <Z> Either3<A, B, Z> mapRight(@Nonnull F<C, Z> f) {
		return this.map(a -> a, b -> b, f);
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public Maybe<A> left() {
		return this.tag == Tag.LEFT ? Maybe.of((A) this.data) : Maybe.empty();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public Maybe<B> middle() {
		return this.tag == Tag.MIDDLE ? Maybe.of((B) this.data) : Maybe.empty();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public Maybe<C> right() {
		return this.tag == Tag.RIGHT ? Maybe.of((C) this.data) : Maybe.empty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Either3<?, ?, ?> either3 = (Either3<?, ?, ?>) o;
		return tag == either3.tag && Objects.equals(data, either3.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, tag);
	}
}