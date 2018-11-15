package com.shapesecurity.shift.es2017.ast;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;

import javax.annotation.Nonnull;

public class FormalParametersWithAsync extends FormalParameters {

	public final boolean isAsync;

	public FormalParametersWithAsync(boolean isAsync, @Nonnull ImmutableList<Parameter> items, @Nonnull Maybe<Binding> rest) {
		super(items, rest);
		this.isAsync = isAsync;
	}

}
