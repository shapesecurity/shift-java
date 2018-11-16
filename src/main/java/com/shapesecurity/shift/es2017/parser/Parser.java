package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.Unit;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import javax.annotation.Nonnull;

public class Parser extends GenericParser<Unit> {

	protected Parser(@Nonnull String source, boolean isModule) throws JsError {
		super(source, isModule);
	}

	@Nonnull
	public static Script parseScript(@Nonnull String text) throws JsError {
		return new Parser(text, false).parseScript();
	}

	@Nonnull
	public static Module parseModule(@Nonnull String text) throws JsError {
		return new Parser(text, true).parseModule();
	}

	@Nonnull
	@Override
	protected <T extends Node> T finishNode(@Nonnull Unit startState, @Nonnull T node) {
		return node;
	}

	@Nonnull
	@Override
	protected Unit startNode() {
		return Unit.unit;
	}

	@Nonnull
	@Override
	protected <T extends Node> T copyNode(@Nonnull Node src, @Nonnull T dest) {
		return dest;
	}
}
