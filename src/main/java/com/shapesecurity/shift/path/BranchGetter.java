package com.shapesecurity.shift.path;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Node;

public class BranchGetter {
    private ImmutableList<Branch> directions;

    public BranchGetter() {
        this.directions = ImmutableList.nil();
    }

    private BranchGetter(ImmutableList<Branch> directions) {
        this.directions = directions;
    }

    public BranchGetter d(Branch branch) {
        return new BranchGetter(this.directions.cons(branch));
    }

    public Maybe<? extends Node> apply(Node node) {
        ImmutableList<Branch> directions = this.directions.reverse(); // a bit silly, but it works
        Maybe<? extends Node> n = Maybe.just(node);
        while (n.isJust() && directions.isNotEmpty()) {
            node = n.just();
            n = directions.maybeHead().just().step(node);
            directions = directions.maybeTail().just();
        }
        return n;
    }
}
