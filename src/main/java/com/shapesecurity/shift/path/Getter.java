package com.shapesecurity.shift.path;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Node;

public class Getter {
    private ImmutableList<Branch> directions;

    public Getter() {
        this.directions = ImmutableList.empty();
    }

    private Getter(ImmutableList<Branch> directions) {
        this.directions = directions;
    }

    public Getter d(Branch branch) {
        return new Getter(this.directions.cons(branch));
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
