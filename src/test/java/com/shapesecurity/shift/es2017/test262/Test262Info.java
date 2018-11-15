package com.shapesecurity.shift.es2017.test262;

import com.shapesecurity.functional.data.ImmutableSet;

import javax.annotation.Nonnull;

public final class Test262Info {

	public enum Test262Negative {
		PARSE, EARLY, RUNTIME, RESOLUTION, NONE
	}

	@Nonnull
	public final String name;
	@Nonnull
	public final Test262Negative negative;
	public final boolean noStrict;
	public final boolean onlyStrict;
	public final boolean async;
	public final boolean module;
	@Nonnull
	public final ImmutableSet<String> features;

	Test262Info(@Nonnull String name, @Nonnull Test262Negative negative, boolean noStrict, boolean onlyStrict, boolean async, boolean module, @Nonnull ImmutableSet<String> features) {
		this.name = name;
		this.negative = negative;
		this.noStrict = noStrict;
		this.onlyStrict = onlyStrict;
		this.async = async;
		this.module = module;
		this.features = features;
	}
}