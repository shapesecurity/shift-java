package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.Unit;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import org.jetbrains.annotations.NotNull;

public class Parser extends GenericParser<Unit> {

	protected Parser(@NotNull String source, boolean isModule) throws JsError {
		super(source, isModule);
	}

	@NotNull
	public static Script parseScript(@NotNull String text) throws JsError {
		return new Parser(text, false).parseScript();
	}

	@NotNull
	public static Module parseModule(@NotNull String text) throws JsError {
		return new Parser(text, true).parseModule();
	}

	@NotNull
	@Override
	protected <T extends Node> T finishNode(@NotNull Unit startState, @NotNull T node) {
		return node;
	}

	@NotNull
	@Override
	protected Unit startNode() {
		return Unit.unit;
	}
}
