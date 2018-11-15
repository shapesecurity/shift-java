package com.shapesecurity.shift.es2017.path;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2017.ast.Node;

import java.util.Objects;

public class BranchGetter {
    private ImmutableList<Branch> directions;

    public BranchGetter() {
        this.directions = ImmutableList.empty();
    }

    private BranchGetter(ImmutableList<Branch> directions) {
        this.directions = directions;
    }

    public static BranchGetter of(Branch... branches) {
        return new BranchGetter(ImmutableList.from(branches).reverse()); // extremely silly, but it works
    }

    public BranchGetter d(Branch branch) {
        return new BranchGetter(this.directions.cons(branch));
    }

    public Maybe<? extends Node> apply(Node node) {
        ImmutableList<Branch> directions = this.directions.reverse(); // a bit silly, but it works
        Maybe<? extends Node> n = Maybe.of(node);
        while (n.isJust() && directions.isNotEmpty()) {
            NonEmptyImmutableList<Branch> nonEmptyDirections = (NonEmptyImmutableList<Branch>) directions;
            n = n.flatMap(nonEmptyDirections.head::step);
            directions = nonEmptyDirections.tail;
        }
        return n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchGetter that = (BranchGetter) o;
        return Objects.equals(directions, that.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directions);
    }

    @Override
    public String toString() {
        // This is mostly intended for use in test output.
        return this.directions.foldRight((b, acc) -> acc + "." + b.propertyName(), ""); // right instead of left because directions are stored in reverse order internally
    }
}
