package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.shift.es2017.ast.Program;
import javax.annotation.Nonnull;

public abstract class PrettyCodeGen extends CodeGen {
	public PrettyCodeGen(@Nonnull CodeRepFactory factory) {
		super(factory);
	}

	@Nonnull
	public static String codeGen(@Nonnull Program program) {
		return codeGen(program, PRETTY);
	}
}
