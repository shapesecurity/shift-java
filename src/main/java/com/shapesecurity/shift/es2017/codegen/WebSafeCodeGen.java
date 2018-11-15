package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.ExportAllFrom;
import com.shapesecurity.shift.es2017.ast.ExportFrom;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Import;
import com.shapesecurity.shift.es2017.ast.ImportNamespace;
import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.TemplateElement;
import com.shapesecurity.shift.es2017.ast.TemplateExpression;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.utils.Utils;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSafeCodeGen extends CodeGen {
	public WebSafeCodeGen(@Nonnull CodeRepFactory factory) {
		super(factory);
	}
	public WebSafeCodeGen(@Nonnull FormattedCodeRepFactory factory) { super(factory); }

	@Nonnull
	public static String prettyCodeGen(@Nonnull Script script) {
		StringBuilder sb = new StringBuilder();
		Director.reduceScript(new WebSafeCodeGen(new FormattedCodeRepFactory()), script).emit(new WebSafeTokenStream(sb), false);
		return sb.toString();
	}

	@Nonnull
	public static String codeGen(@Nonnull Script script) {
		StringBuilder sb = new StringBuilder();
		Director.reduceScript(new WebSafeCodeGen(new CodeRepFactory()), script).emit(new WebSafeTokenStream(sb), false);
		return sb.toString();
	}

	@Nonnull
	public static String codeGen(@Nonnull Module module) {
		StringBuilder sb = new StringBuilder();
		Director.reduceModule(new WebSafeCodeGen(new CodeRepFactory()), module).emit(new WebSafeTokenStream(sb), false);
		return sb.toString();
	}

	@Override
	@Nonnull
	public CodeRep reduceLiteralStringExpression(@Nonnull LiteralStringExpression node) {
		return factory.token(safe(Utils.escapeStringLiteral(node.value)));
	}

	@Override
	@Nonnull
	public CodeRep reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
		String safened = safe(node.pattern + "/");
		if (!safened.equals(node.pattern + "/")) {
			throw new RuntimeException("WebSafeCodegen cannot safely output ASTs containing regex literals which require escaping");
		}
		return factory.token("/" + safened + buildFlags(node));
	}

	@Nonnull
	@Override
	public CodeRep reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.setStartsWithLet(true);
		}
		return a;
	}

	@Nonnull
	@Override
	public CodeRep reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.setStartsWithLet(true);
		}
		return a;
	}

	@Nonnull
	@Override
	public CodeRep reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.setStartsWithLet(true);
		}
		return a;
	}

	@Nonnull
	@Override
	public CodeRep reduceDirective(@Nonnull Directive node) {
		String delim = node.rawValue.matches("^(?:[^\"]|\\\\.)*$") ? "\"" : "\'";
		return seqVA(factory.token(delim + safe(node.rawValue) + delim), factory.semiOp());
	}

	@Nonnull
	@Override
	public CodeRep reduceExportAllFrom(@Nonnull ExportAllFrom node) {
		return seqVA(factory.token("export"), factory.token("*"), factory.token("from"), factory.token(safe(Utils.escapeStringLiteral(node.moduleSpecifier))), factory.semiOp());

	}

	@Nonnull
	@Override
	public CodeRep reduceExportFrom(@Nonnull ExportFrom node, @Nonnull ImmutableList<CodeRep> namedExports) {
		return seqVA(
				factory.token("export"),
				factory.brace(factory.commaSep(namedExports)),
				seqVA(factory.token("from"), factory.token(safe(Utils.escapeStringLiteral(node.moduleSpecifier))), factory.semiOp())
		);
	}

	@Nonnull
	@Override
	public CodeRep reduceImport(@Nonnull Import node, @Nonnull Maybe<CodeRep> defaultBinding, @Nonnull ImmutableList<CodeRep> namedImports) {
		List<CodeRep> bindings = new ArrayList<>();
		if (defaultBinding.isJust()) {
			bindings.add(defaultBinding.fromJust());
		}
		if (namedImports.length > 0) {
			bindings.add(factory.brace(factory.commaSep(namedImports)));
		}
		if (bindings.size() == 0) {
			return seqVA(factory.token("import"), factory.token(safe(Utils.escapeStringLiteral(node.moduleSpecifier))), factory.semiOp());
		}
		return seqVA(factory.token("import"), factory.commaSep(ImmutableList.from(bindings)), factory.token("from"), factory.token(safe(Utils.escapeStringLiteral(node.moduleSpecifier))), factory.semiOp());
	}

	@Nonnull
	@Override
	public CodeRep reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<CodeRep> defaultBinding, @Nonnull CodeRep namespaceBinding) {
		return seqVA(
				factory.token("import"),
				defaultBinding.maybe(factory.empty(), b -> seqVA(b, factory.token(","))),
				factory.token("*"),
				factory.token("as"),
				namespaceBinding,
				factory.token("from"),
				factory.token(safe(Utils.escapeStringLiteral(node.moduleSpecifier))),
				factory.semiOp()
		);
	}

	@Nonnull
	@Override
	public CodeRep reduceTemplateExpression(@Nonnull TemplateExpression node, @Nonnull Maybe<CodeRep> tag, @Nonnull ImmutableList<CodeRep> elements) {
		boolean isTagged = node.tag.isJust();
		CodeRep state = node.tag.maybe(factory.empty(), t -> p(t, node.getPrecedence(), tag.fromJust()));
		state = seqVA(state, factory.token("`"));
		for (int i = 0, l = node.elements.length; i < l; ++i) {
			if (node.elements.index(i).fromJust() instanceof TemplateElement) {
				String d = "";
				if (i > 0) {
					d += "}";
				}
				String original = ((TemplateElement) node.elements.index(i).fromJust()).rawValue;
				String safened = safe(original);
				if (isTagged && !safened.equals(original)) {
					throw new RuntimeException("WebSafeCodegen cannot safely output ASTs containing tagged templates which require escaping");
				}
				d += safened;
				if (i < l - 1) {
					d += "${";
				}
				if (d.length() > 0) {
					state = seqVA(state, factory.token(d));
				}
			} else {
				state = seqVA(state, elements.index(i).fromJust());
			}
		}
		state = seqVA(state, factory.token("`"));
		if (node.tag.isJust()) {
			state.startsWithObjectCurly(tag.fromJust().startsWithObjectCurly());
			state.setStartsWithLetSquareBracket(tag.fromJust().startsWithLetSquareBracket());
			state.setStartsWithFunctionOrClass(tag.fromJust().startsWithFunctionOrClass());
		}
		return state;
	}

	private static Pattern NULL = Pattern.compile("\\x00");
	private static Pattern NONASCII = Pattern.compile("[^\\x00-\\x7F]", Pattern.UNICODE_CHARACTER_CLASS);
	private static Pattern SCRIPTTAG = Pattern.compile("<(/?)script([\\t\\r\\f />])");

	@Nonnull
	private static String safe(@Nonnull String unsafe) {
		unsafe = replaceAll(NULL, unsafe, "\\x00");
		unsafe = replaceAll(NONASCII, unsafe, mr -> {
			String s = mr.group();
			int cp = s.codePointAt(0);
			return cp > 0xFFFF
				? String.format("\\u%04X\\u%04X", (int) s.charAt(0), (int) s.charAt(1))
				: String.format("\\u%04X", (int) cp);
		});
		unsafe = replaceAll(SCRIPTTAG, unsafe, mr -> "<" + mr.group(1) + String.format("\\x%02X", (int) 's') + "cript" + mr.group(2));
		return unsafe;
	}

	private static Pattern DOLLAR_OR_BACKSLASH = Pattern.compile("[\\\\$]");

	// in order to treat replacement string as literal replacement, escape backslash and dollar sign
	@Nonnull
	private static String literally(@Nonnull String replacement) {
		return DOLLAR_OR_BACKSLASH.matcher(replacement).replaceAll("\\\\$0");
	}

	@Nonnull
	private static String replaceAll(@Nonnull Pattern pattern, @Nonnull String string, @Nonnull String replacement) {
		return pattern.matcher(string).replaceAll(literally(replacement));
	}

	@Nonnull
	private static String replaceAll(@Nonnull Pattern pattern, @Nonnull String string, @Nonnull F<MatchResult, String> replacer) {
		StringBuffer output = new StringBuffer();
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			matcher.appendReplacement(output, literally(replacer.apply(matcher.toMatchResult())));
		}
		matcher.appendTail(output);
		return output.toString();
	}
}
