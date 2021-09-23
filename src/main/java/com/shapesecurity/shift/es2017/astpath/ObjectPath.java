package com.shapesecurity.shift.es2017.astpath;

import com.shapesecurity.functional.data.Maybe;

import java.util.Iterator;

public abstract class ObjectPath<S, T> {
	abstract Maybe<T> apply(Object source);

	abstract public boolean equals(Object o);

	abstract public int hashCode();

	// in principle, there's nothing wrong with having your S2 be a supertype of T, rather than a subtype
	// (just as `apply` takes an Object, the maximal supertype, and then checks that it is in fact an S)
	// but in our actual ASTPath use case, the sources are never supertypes
	// so we don't need to allow that case, and can express a more useful bound
	public <S2 extends T, T2> ObjectPath<S, T2> then(ObjectPath<S2, T2> next) {
		return new Composed<>(this, next);
	}

	public static class Composed<S, MS, MT extends MS, T> extends ObjectPath<S, T> {
		private final ObjectPath<S, MS> first;
		private final ObjectPath<MT, T> second;

		// precompute to save time in .equals
		private final int hashCode;

		public Composed(ObjectPath<S, MS> first, ObjectPath<MT, T> second) {
			this.first = first;
			this.second = second;
			// any associative operation would work here
			this.hashCode = this.first.hashCode() + this.second.hashCode();
		}

		@Override
		Maybe<T> apply(Object source) {
			return this.first.apply(source).flatMap(this.second::apply);
		}

		// we do some extra work here to ensure `.then` is associative
		// that is, `a.then(b).then(c)` is equal to `a.then(b.then(c))`
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
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
		private static class PartsIterator implements Iterator<ObjectPath> {
			final Composed base;
			boolean doneFirst = false;
			boolean doneSecond = false;
			Iterator<ObjectPath> innerIterator = null;

			private PartsIterator(Composed base) {
				this.base = base;
			}

			@Override
			public boolean hasNext() {
				return !this.doneSecond;
			}

			@Override
			public ObjectPath next() {
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
				} else {
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
				}
			}
		}
	}
}
