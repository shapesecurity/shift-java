package com.shapesecurity.shift.es2018.astpath;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;

import java.util.Iterator;
import java.util.Objects;

public interface ObjectPath<S, T> {
	Maybe<T> apply(Object source);

	// in principle, there's nothing wrong with having your S2 be a supertype of T, rather than a subtype
	// (just as `apply` takes an Object, the maximal supertype, and then checks that it is in fact an S)
	// but in our actual ASTPath use case, the sources are never supertypes
	// so we don't need to allow that case, and can express a more useful bound
	default <S2 extends T, T2> ObjectPath<S, T2> then(ObjectPath<S2, T2> next) {
		return new Composed<>(this, next);
	}

	static <S> Identity<S> identity() {
		return (Identity<S>) Identity.INSTANCE;
	}

	static <S> Get<S> get() {
		return (Get<S>) Get.INSTANCE;
	}

	static <S> Index<S> index(int index) {
		return new Index<>(index);
	}

	final class Identity<S> implements ObjectPath<S, S> {
		private Identity() {}
		private static Identity INSTANCE = new Identity();

		@Override
		public Maybe<S> apply(Object source) {
			return (Maybe<S>) Maybe.of(source);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o == INSTANCE) {
				return true;
			}
			if (o instanceof Composed) {
				return o.equals(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	final class Get<S> implements ObjectPath<Maybe<S>, S> {
		private Get() {}
		private static Get INSTANCE = new Get<>();

		@Override
		public Maybe<S> apply(Object source) {
			if (!(source instanceof Maybe)) {
				return Maybe.empty();
			}
			return (Maybe) source;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o == INSTANCE) {
				return true;
			}
			if (o instanceof Composed) {
				return o.equals(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	final class Index<T> implements ObjectPath<ImmutableList<T>, T> {
		final int index;

		private Index(int index) {
			this.index = index;
		}

		@Override
		public Maybe<T> apply(Object source) {
			if (!(source instanceof ImmutableList)) return Maybe.empty();
			return ((ImmutableList<T>) source).index(this.index);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o == this) {
				return true;
			}
			if (o instanceof Composed) {
				return o.equals(this);
			}
			if (getClass() != o.getClass()) {
				return false;
			}
			return this.index == ((Index<?>) o).index;
		}

		@Override
		public int hashCode() {
			return Objects.hash(index);
		}
	}

	final class Composed<S, MS, MT extends MS, T> implements ObjectPath<S, T> {
		private final ObjectPath<S, MS> first;
		private final ObjectPath<MT, T> second;

		// precompute to save time in .equals
		private final int hashCode;

		private Composed(ObjectPath<S, MS> first, ObjectPath<MT, T> second) {
			this.first = first;
			this.second = second;
			// any associative operation would work here
			// as long as Identity has as its hashCode the identity of that operation
			// a non-commutative operation is best, though
			// so use the one from https://www.jstor.org/stable/3613855
			int firstHash = this.first.hashCode();
			int secondHash = this.second.hashCode();
			this.hashCode = (firstHash & 1) == 0 ? firstHash + secondHash : firstHash - secondHash;
		}

		@Override
		public Maybe<T> apply(Object source) {
			return this.first.apply(source).flatMap(this.second::apply);
		}

		// we do some extra work here to ensure `.then` is associative
		// that is, `a.then(b).then(c)` is equal to `a.then(b.then(c))`
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == Identity.INSTANCE) {
				return !(new PartsIterator(this)).hasNext();
			}
			if (o == null || getClass() != o.getClass()) return false;
			Composed<?, ?, ?, ?> that = (Composed<?, ?, ?, ?>) o;

			if (this.hashCode != that.hashCode()) {
				return false;
			}

			PartsIterator thisParts = new PartsIterator(this);
			PartsIterator thatParts = new PartsIterator(that);

			while (thisParts.hasNext()) {
				if (!thatParts.hasNext()) {
					return false;
				}
				if (!thisParts.next().equals(thatParts.next())) {
					return false;
				}
			}
			return !thatParts.hasNext();
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		// this is just used for implementing .equals
		// it skips Identity parts, and so might be totally empty
		private static class PartsIterator implements Iterator<ObjectPath> {
			final Composed base;
			boolean doneFirst = false;
			boolean doneSecond = false;
			ObjectPath next = null;
			Iterator<ObjectPath> innerIterator = null;

			private PartsIterator(Composed base) {
				this.base = base;
				this.ensureNext();
			}

			@Override
			public boolean hasNext() {
				return this.next != null;
			}

			@Override
			public ObjectPath next() {
				ObjectPath next = this.next;
				this.next = null;
				this.ensureNext();
				return next;
			}

			private void ensureNext() {
				if (this.next != null) {
					return;
				}
				do {
					this.next = rawNext();
				} while (this.next != null && this.next == Identity.INSTANCE);
			}

			private ObjectPath rawNext() {
				if (!this.doneFirst) {
					if (!(this.base.first instanceof Composed)) {
						this.doneFirst = true;
						return this.base.first;
					}
					if (this.innerIterator == null) {
						this.innerIterator = new PartsIterator((Composed) this.base.first);
					}
					ObjectPath next = this.innerIterator.next();
					if (!this.innerIterator.hasNext()) {
						this.doneFirst = true;
						this.innerIterator = null;
					}
					return next;
				} else if (!this.doneSecond) {
					if (!(this.base.second instanceof Composed)) {
						this.doneSecond = true;
						return this.base.second;
					}
					if (this.innerIterator == null) {
						this.innerIterator = new PartsIterator((Composed) this.base.second);
					}
					ObjectPath next = this.innerIterator.next();
					if (!this.innerIterator.hasNext()) {
						this.doneSecond = true;
						this.innerIterator = null;
					}
					return next;
				} else {
					return null;
				}
			}
		}
	}
}