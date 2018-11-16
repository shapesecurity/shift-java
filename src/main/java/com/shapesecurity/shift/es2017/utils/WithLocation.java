package com.shapesecurity.shift.es2017.utils;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.parser.SourceSpan;

import javax.annotation.Nonnull;

public interface WithLocation {
	@Nonnull
	Maybe<SourceSpan> getLocation(@Nonnull Node node);
}
