package com.shapesecurity.shift.es2016.codegen.location;

import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.codegen.CodeRep;
import com.shapesecurity.shift.es2016.codegen.TokenStream;
import org.jetbrains.annotations.NotNull;

public class CodeRepWithLocation extends CodeRep {
	@NotNull
	protected final CodeRep inner;

	@NotNull
	protected final Node node;

	@NotNull
	protected final LocationMeta meta;

	protected CodeRepWithLocation(@NotNull CodeRep inner, @NotNull Node node, @NotNull LocationMeta meta) {
		this.inner = inner;
		this.node = node;
		this.meta = meta;
	}

	@Override
	public void emit(@NotNull TokenStream ts, boolean noIn) {
		this.meta.startEmit(this.node, ts);
		this.inner.emit(ts, noIn);
		this.meta.finishEmit(this.node, ts);
	}

	@Override
	public boolean containsIn() {
		return this.inner.containsIn();
	}

	@Override
	public void setContainsIn(boolean containsIn) {
		this.inner.setContainsIn(containsIn);
	}

	@Override
	public boolean containsGroup() {
		return this.inner.containsGroup();
	}

	@Override
	public void setContainsGroup(boolean containsGroup) {
		this.inner.setContainsGroup(containsGroup);
	}

	@Override
	public boolean startsWithCurly() {
		return this.inner.startsWithCurly();
	}

	@Override
	public void setStartsWithCurly(boolean startsWithCurly) {
		this.inner.setStartsWithCurly(startsWithCurly);
	}

	@Override
	public boolean startsWithFunctionOrClass() {
		return this.inner.startsWithFunctionOrClass();
	}

	@Override
	public void setStartsWithFunctionOrClass(boolean startsWithFunctionOrClass) {
		this.inner.setStartsWithFunctionOrClass(startsWithFunctionOrClass);
	}

	@Override
	public boolean startsWithLet() {
		return this.inner.startsWithLet();
	}

	@Override
	public void setStartsWithLet(boolean startsWithLet) {
		this.inner.setStartsWithLet(startsWithLet);
	}

	@Override
	public boolean startsWithLetSquareBracket() {
		return this.inner.startsWithLetSquareBracket();
	}

	@Override
	public void setStartsWithLetSquareBracket(boolean startsWithLetSquareBracket) {
		this.inner.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
	}

	@Override
	public boolean endsWithMissingElse() {
		return this.inner.endsWithMissingElse();
	}

	@Override
	public void setEndsWithMissingElse(boolean endsWithMissingElse) {
		this.inner.setEndsWithMissingElse(endsWithMissingElse);
	}

	@Override
	public void markIsInDirectivePosition() {
		this.inner.markIsInDirectivePosition();
	}
}
