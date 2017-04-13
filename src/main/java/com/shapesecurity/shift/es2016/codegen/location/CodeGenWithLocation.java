package com.shapesecurity.shift.es2016.codegen.location;

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.codegen.CodeRep;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import com.shapesecurity.shift.es2016.reducer.Director;
import com.shapesecurity.shift.es2016.reducer.Reducer;
import com.shapesecurity.shift.es2016.reducer.WrappedReducer;
import org.jetbrains.annotations.NotNull;

public class CodeGenWithLocation extends WrappedReducer<CodeRep> {
	@NotNull
	protected LocationMeta meta;

	private CodeGenWithLocation(@NotNull CtorArgs args) {
		super(args.wrap, args.reducer);
		this.meta = args.meta;
	}

	private static class CtorArgs {
		// A helper class is necessary to construct `wrap` which refers to `meta` but still have access to `meta`.
		@NotNull
		final LocationMeta meta;

		@NotNull
		final F2<Node, CodeRep, CodeRep> wrap;

		@NotNull
		final Reducer<CodeRep> reducer;

		CtorArgs(@NotNull Reducer<CodeRep> codeGen) {
			this.meta = new LocationMeta();
			this.wrap = (node, codeRep) -> new CodeRepWithLocation(codeRep, node, this.meta);
			this.reducer = codeGen;
		}
	}

	public CodeGenWithLocation(@NotNull Reducer<CodeRep> codeGen) {
		this(new CtorArgs(codeGen));
	}

	public String codeGen(@NotNull Script script) {
		StringBuilder sb = new StringBuilder();
		TokenStreamWithLocation ts = new TokenStreamWithLocation(sb, this.meta);
		Director.reduceScript(this, script).emit(ts, false);
		return sb.toString();
	}

	public String codeGen(@NotNull Module module) {
		StringBuilder sb = new StringBuilder();
		TokenStreamWithLocation ts = new TokenStreamWithLocation(sb, this.meta);
		Director.reduceModule(this, module).emit(ts, false);
		return sb.toString();
	}

	@NotNull
	public Maybe<SourceSpan> getLocation(@NotNull Node node) {
		SourceLocation start = this.meta.nodeToStart.get(node);
		SourceLocation end = this.meta.nodeToFinish.get(node);
		if (start == null || end == null) {
			return Maybe.empty();
		}
		return Maybe.of(new SourceSpan(Maybe.empty(), start, end));
	}
}
