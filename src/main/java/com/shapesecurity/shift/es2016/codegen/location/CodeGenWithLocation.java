package com.shapesecurity.shift.es2016.codegen.location;

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.ArrowExpression;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.codegen.CodeGen;
import com.shapesecurity.shift.es2016.codegen.CodeRep;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import com.shapesecurity.shift.es2016.reducer.Director;
import com.shapesecurity.shift.es2016.reducer.Reducer;
import com.shapesecurity.shift.es2016.reducer.WrappedReducer;
import com.shapesecurity.shift.es2016.utils.WithLocation;

import javax.annotation.Nonnull;

public class CodeGenWithLocation extends WrappedReducer<CodeRep> implements WithLocation {
	@Nonnull
	protected LocationMeta meta;

	private CodeGenWithLocation(@Nonnull CtorArgs args) {
		super(args.wrap, args.reducer);
		this.meta = args.meta;
	}

	private static class CtorArgs {
		// A helper class is necessary to construct `wrap` which refers to `meta` but still have access to `meta`.
		@Nonnull
		final LocationMeta meta;

		@Nonnull
		final F2<Node, CodeRep, CodeRep> wrap;

		@Nonnull
		final Reducer<CodeRep> reducer;

		CtorArgs(@Nonnull Reducer<CodeRep> codeGen) {
			this.meta = new LocationMeta();
			this.wrap = (node, codeRep) -> {
				if (node instanceof ArrowExpression && !CodeGen.isComplexArrowHead(((ArrowExpression) node).params)) {
					if (codeRep instanceof CodeRep.Seq && ((CodeRep.Seq) codeRep).children.length > 1) {
						CodeRep[] children = ((CodeRep.Seq) codeRep).children;
						CodeRep bindingRep = children[0];
						CodeRep arrowToken = children[1];
						if (bindingRep instanceof CodeRep.Token && arrowToken instanceof CodeRep.Token && "=>".equals(((CodeRep.Token) arrowToken).token)) {
							// The default CodeGen replaces the CodeRep for a simple FormalParameters node of an ArrowExpression with a CodeRep for just the sole BindingIdentifier, so that it doesn't include parentheses.
							// This test mostly confirms that the CodeRep looks like we expect in that case: a token for the BindingIdentifier, followed by a token for the arrow itself.
							// If so, we manually reconstruct `CodeRepWithLocation`s for the BindingIdentifier and FormalParameters nodes.
							CodeRepWithLocation bindingRepWithLocation = new CodeRepWithLocation(bindingRep, ((ArrowExpression) node).params.items.maybeHead().fromJust(), this.meta);
							CodeRepWithLocation paramsRepWithLocation = new CodeRepWithLocation(bindingRepWithLocation, ((ArrowExpression) node).params, this.meta);
							CodeRep[] newChildren = new CodeRep[children.length];
							newChildren[0] = paramsRepWithLocation;
							System.arraycopy(children, 1, newChildren, 1, children.length - 1);
							codeRep = new CodeRep.Seq(newChildren);
						}
					}
				}
				return new CodeRepWithLocation(codeRep, node, this.meta);
			};
			this.reducer = codeGen;
		}
	}

	public CodeGenWithLocation(@Nonnull Reducer<CodeRep> codeGen) {
		this(new CtorArgs(codeGen));
	}

	public String codeGen(@Nonnull Program program) {
		StringBuilder sb = new StringBuilder();
		TokenStreamWithLocation ts = new TokenStreamWithLocation(sb, this.meta);
		Director.reduceProgram(this, program).emit(ts, false);
		return sb.toString();
	}

	@Nonnull
	public Maybe<SourceSpan> getLocation(@Nonnull Node node) {
		SourceLocation start = this.meta.nodeToStart.get(node);
		SourceLocation end = this.meta.nodeToFinish.get(node);
		if (start == null || end == null) {
			return Maybe.empty();
		}
		return Maybe.of(new SourceSpan(Maybe.empty(), start, end));
	}
}
