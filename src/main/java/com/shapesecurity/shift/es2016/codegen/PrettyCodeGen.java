package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import javax.annotation.Nonnull;

public abstract class PrettyCodeGen extends CodeGen {
	public PrettyCodeGen(@Nonnull CodeRepFactory factory) {
		super(factory);
	}

	@Nonnull
	public static String codeGen(@Nonnull Script script) {
		return codeGen(script, PRETTY);
	}

	@Nonnull
	public static String codeGen(@Nonnull Module module) {
		return codeGen(module, PRETTY);
	}
}
