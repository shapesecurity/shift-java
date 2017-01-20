package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import org.jetbrains.annotations.NotNull;

public abstract class PrettyCodeGen extends CodeGen {
	private PrettyCodeGen(@NotNull CodeRepFactory factory) {
		super(factory);
	}

	@NotNull
	public static String codeGen(@NotNull Script script) {
		return codeGen(script, PRETTY);
	}

	@NotNull
	public static String codeGen(@NotNull Module module) {
		return codeGen(module, PRETTY);
	}
}
