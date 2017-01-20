package com.shapesecurity.shift.es2016.codegen;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.Directive;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2016.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.TemplateElement;
import com.shapesecurity.shift.es2016.ast.TemplateExpression;
import com.shapesecurity.shift.es2016.reducer.Director;
import com.shapesecurity.shift.es2016.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSafeCodeGen extends CodeGen {
	private WebSafeCodeGen(@NotNull CodeRepFactory factory) {
		super(factory);
	}

	@NotNull
	public static String codeGen(@NotNull Script script) {
		StringBuilder sb = new StringBuilder();
		Director.reduceScript(new WebSafeCodeGen(new CodeRepFactory()), script).emit(new WebSafeTokenStream(sb), false);
		return sb.toString();
	}

	@NotNull
	public static String codeGen(@NotNull Module module) {
		StringBuilder sb = new StringBuilder();
		Director.reduceModule(new WebSafeCodeGen(new CodeRepFactory()), module).emit(new WebSafeTokenStream(sb), false);
		return sb.toString();
	}

	@Override
	@NotNull
	public CodeRep reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
		return factory.token(safe(Utils.escapeStringLiteral(node.value)));
	}

	@Override
	@NotNull
	public CodeRep reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
		return factory.token("/" + safe(node.pattern + "/") + buildFlags(node));
	}

	@NotNull
	@Override
	public CodeRep reduceIdentifierExpression(@NotNull IdentifierExpression node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.startsWithLet = true;
		}
		return a;
	}

	@NotNull
	@Override
	public CodeRep reduceAssignmentTargetIdentifier(@NotNull AssignmentTargetIdentifier node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.startsWithLet = true;
		}
		return a;
	}

	@NotNull
	@Override
	public CodeRep reduceBindingIdentifier(@NotNull BindingIdentifier node) {
		CodeRep a = factory.token(safe(node.name));
		if (node.name.equals("let")) {
			a.startsWithLet = true;
		}
		return a;
	}

	@NotNull
	@Override
	public CodeRep reduceDirective(@NotNull Directive node) {
		String delim = node.rawValue.matches("^(?:[^\"]|\\\\.)*$") ? "\"" : "\'";
		return seqVA(factory.token(delim + safe(node.rawValue) + delim), factory.semiOp());
	}

	@NotNull
	@Override
	public CodeRep reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<CodeRep> tag, @NotNull ImmutableList<CodeRep> elements) {
		CodeRep state = node.tag.maybe(factory.empty(), t -> p(t, node.getPrecedence(), tag.fromJust()));
		state = seqVA(state, factory.token("`"));
		for (int i = 0, l = node.elements.length; i < l; ++i) {
			if (node.elements.index(i).fromJust() instanceof TemplateElement) {
				String d = "";
				if (i > 0) {
					d += "}";
				}
				d += safe(((TemplateElement) node.elements.index(i).fromJust()).rawValue);
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
			state.startsWithCurly = tag.fromJust().startsWithCurly;
			state.startsWithLetSquareBracket = tag.fromJust().startsWithLetSquareBracket;
			state.startsWithFunctionOrClass = tag.fromJust().startsWithFunctionOrClass;
		}
		return state;
	}

	private static Pattern NULL = Pattern.compile("\\x00");
	private static Pattern NONASCII = Pattern.compile("[^\\x00-\\x7F]", Pattern.UNICODE_CHARACTER_CLASS);
	private static Pattern SCRIPTTAG = Pattern.compile("<(/?)script([\\t\\r\\f />])");

	@NotNull
	private static String safe(@NotNull String unsafe) {
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
	@NotNull
	private static String literally(@NotNull String replacement) {
		return DOLLAR_OR_BACKSLASH.matcher(replacement).replaceAll("\\\\$0");
	}

	@NotNull
	private static String replaceAll(@NotNull Pattern pattern, @NotNull String string, @NotNull String replacement) {
		return pattern.matcher(string).replaceAll(literally(replacement));
	}

	@NotNull
	private static String replaceAll(@NotNull Pattern pattern, @NotNull String string, @NotNull F<MatchResult, String> replacer) {
		StringBuffer output = new StringBuffer();
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			matcher.appendReplacement(output, literally(replacer.apply(matcher.toMatchResult())));
		}
		matcher.appendTail(output);
		return output.toString();
	}
}
