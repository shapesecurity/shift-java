package com.shapesecurity.shift.es2016.path;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.Node;

public class BranchGetter {
    private ImmutableList<Branch> directions;

    public BranchGetter() {
        this.directions = ImmutableList.empty();
    }

    private BranchGetter(ImmutableList<Branch> directions) {
        this.directions = directions;
    }

    public BranchGetter d(Branch branch) {
        return new BranchGetter(this.directions.cons(branch));
    }

    public Maybe<? extends Node> apply(Node node) {
        ImmutableList<Branch> directions = this.directions.reverse(); // a bit silly, but it works
        Maybe<? extends Node> n = Maybe.of(node);
        while (n.isJust() && directions.isNotEmpty()) {
            node = n.fromJust();
            n = directions.maybeHead().fromJust().step(node);
            directions = directions.maybeTail().fromJust();
        }
        return n;
    }
}
