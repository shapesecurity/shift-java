package com.shapesecurity.shift.es2016.test262;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.parser.EarlyError;
import com.shapesecurity.shift.es2016.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test262 {

	private static final Yaml yamlParser = new Yaml();

	private static final HashSet<String> xfailParse = new HashSet<>(Arrays.asList(
			// shift-java#198
			"annexB/language/statements/for-in/nonstrict-initializer.js",
			"annexB/language/statements/for-in/bare-initializer.js",
			"annexB/language/statements/for-in/strict-initializer.js",
			"annexB/language/statements/for-in/var-objectbindingpattern-initializer.js",
			"annexB/language/statements/for-in/var-arraybindingpattern-initializer.js",

			// async/await not implemented
			"language/statements/class/definition/methods-async-super-call-body.js",
			"language/statements/class/definition/class-method-returns-promise.js",
			"language/statements/class/definition/methods-async-super-call-param.js",
			"built-ins/Function/prototype/toString/async-function-expression.js",
			"built-ins/Function/prototype/toString/async-method.js",
			"built-ins/Function/prototype/toString/AsyncFunction.js",
			"built-ins/Function/prototype/toString/async-function-declaration.js",
			"language/expressions/object/method-definition/async-super-call-body.js",
			"language/expressions/object/method-definition/async-super-call-param.js",
			"language/expressions/object/method-definition/object-method-returns-promise.js",

			"built-ins/AsyncFunction/AsyncFunction-is-extensible.js",
			"built-ins/AsyncFunction/AsyncFunctionPrototype-prototype.js",
			"built-ins/AsyncFunction/instance-prototype-property.js",
			"built-ins/AsyncFunction/AsyncFunction-length.js",
			"built-ins/AsyncFunction/AsyncFunctionPrototype-to-string.js",
			"built-ins/AsyncFunction/AsyncFunction.js",
			"built-ins/AsyncFunction/AsyncFunctionPrototype-is-extensible.js",
			"built-ins/AsyncFunction/instance-has-name.js",
			"built-ins/AsyncFunction/instance-construct.js",
			"built-ins/AsyncFunction/AsyncFunction-is-subclass.js",
			"built-ins/AsyncFunction/AsyncFunction-construct.js",
			"built-ins/AsyncFunction/AsyncFunction-name.js",
			"built-ins/AsyncFunction/AsyncFunction-prototype.js",
			"built-ins/AsyncFunction/instance-length.js",
			"language/statements/async-function/evaluation-this-value-passed.js",
			"language/statements/async-function/evaluation-default-that-throws.js",
			"language/statements/async-function/syntax-declaration.js",
			"language/statements/async-function/declaration-returns-promise.js",
			"language/statements/async-function/evaluation-this-value-global.js",
			"language/statements/async-function/evaluation-body.js",
			"language/statements/async-function/evaluation-body-that-returns.js",
			"language/statements/async-function/evaluation-body-that-throws.js",
			"language/statements/async-function/evaluation-unmapped-arguments.js",
			"language/statements/async-function/evaluation-body-that-returns-after-await.js",
			"language/statements/async-function/evaluation-mapped-arguments.js",
			"language/statements/async-function/evaluation-body-that-throws-after-await.js",
			"language/statements/async-function/syntax-declaration-line-terminators-allowed.js",
			"language/expressions/async-function/syntax-expression-is-PrimaryExpression.js",
			"language/expressions/async-function/expression-returns-promise.js",
			"language/expressions/await/await-awaits-thenable-not-callable.js",
			"language/expressions/await/await-in-nested-function.js",
			"language/expressions/await/await-awaits-thenables.js",
			"language/expressions/await/await-BindingIdentifier-in-global.js",
			"language/expressions/await/syntax-await-has-UnaryExpression.js",
			"language/expressions/await/await-throws-rejections.js",
			"language/expressions/await/await-in-nested-generator.js",
			"language/expressions/await/await-awaits-thenables-that-throw.js",
			"language/expressions/await/syntax-await-has-UnaryExpression-with-MultiplicativeExpression.js",
			"language/expressions/async-arrow-function/arrow-returns-promise.js",

			// yield bugs #212
			"language/statements/class/definition/methods-gen-yield-as-label.js",
			"language/statements/class/definition/methods-gen-yield-as-binding-identifier.js",
			"language/statements/class/definition/methods-gen-yield-as-logical-or-expression.js",
			"language/statements/class/definition/methods-gen-yield-as-parameter.js",
			"language/statements/class/definition/methods-gen-yield-star-after-newline.js",
			"language/statements/class/definition/methods-gen-yield-weak-binding.js",
			"language/expressions/object/method-definition/yield-as-logical-or-expression.js",
			"language/expressions/object/method-definition/yield-weak-binding.js",
			"language/expressions/object/method-definition/yield-as-parameter.js",
			"language/expressions/object/method-definition/yield-as-label.js",
			"language/expressions/object/method-definition/yield-star-after-newline.js",
			"language/expressions/object/method-definition/yield-as-binding-identifier.js",

			// trailing commas not in es2016
			"language/statements/class/definition/params-trailing-comma.js",
			"language/statements/class/definition/params-trailing-comma-length.js",
			"language/statements/function/params-trailing-comma.js",
			"language/statements/function/params-trailing-comma-length.js",
			"language/expressions/object/method-definition/params-trailing-comma-length.js",
			"language/expressions/object/method-definition/params-trailing-comma.js",
			"language/expressions/arrow-function/params-trailing-comma-length.js",
			"language/expressions/arrow-function/params-trailing-comma.js",
			"language/expressions/function/params-trailing-comma-length.js",
			"language/expressions/function/params-trailing-comma.js",
			"language/statements/generators/params-trailing-comma-length.js",
			"language/statements/generators/params-trailing-comma.js",
			"language/expressions/generators/params-trailing-comma-length.js",
			"language/expressions/generators/params-trailing-comma.js",

			// not in es2016
			"language/module-code/namespace/internals/set-prototype-of-null.js",

			// shift-java#207
			"language/expressions/tagged-template/invalid-escape-sequences.js"

	));

	private static final HashSet<String> xfailEarlyErrors = new HashSet<>(Arrays.asList(

			// exponentiation early errors not implemented
			"language/expressions/exponentiation/exp-operator-syntax-error-delete-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-typeof-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-logical-not-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-void-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-negate-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-plus-unary-expression-base.js",
			"language/expressions/exponentiation/exp-operator-syntax-error-bitnot-unary-expression-base.js",

			// yield bugs #212
			"language/expressions/assignment/dstr-obj-id-identifier-yield-expr.js",
			"language/statements/for-in/dstr-obj-id-identifier-yield-expr.js",
			"language/statements/for-of/dstr-obj-id-identifier-yield-expr.js",

			// shift-java#208
			"language/global-code/decl-lex-restricted-global.js",

			// no regex acceptor
			"language/literals/regexp/u-invalid-non-empty-class-ranges.js",
			"language/literals/regexp/u-unicode-esc-non-hex.js",
			"language/literals/regexp/invalid-braced-quantifier-range.js",
			"language/literals/regexp/u-invalid-oob-decimal-escape.js",
			"language/literals/regexp/u-invalid-non-empty-class-ranges-no-dash-a.js",
			"language/literals/regexp/u-invalid-identity-escape.js",
			"language/literals/regexp/u-invalid-legacy-octal-escape.js",
			"language/literals/regexp/u-unicode-esc-bounds.js",
			"language/literals/regexp/u-invalid-non-empty-class-ranges-no-dash-b.js",
			"language/literals/regexp/invalid-braced-quantifier-lower.js",
			"language/literals/regexp/u-invalid-class-escape.js",
			"language/literals/regexp/u-invalid-non-empty-class-ranges-no-dash-ab.js",
			"language/literals/regexp/invalid-braced-quantifier-exact.js",
			"language/literals/regexp/u-invalid-quantifiable-assertion.js",
			"language/literals/regexp/early-err-pattern.js",
			"language/literals/regexp/u-invalid-extended-pattern-char.js",
			"language/literals/regexp/u-dec-esc.js",

			// no module early errors
			"language/module-code/instn-star-star-cycle.js",
			"language/module-code/instn-iee-err-circular.js",
			"language/module-code/instn-resolve-empty-import.js",
			"language/module-code/instn-iee-err-dflt-thru-star.js",
			"language/module-code/instn-resolve-err-syntax.js",
			"language/module-code/instn-named-err-not-found-as.js",
			"language/module-code/instn-iee-err-ambiguous.js",
			"language/module-code/instn-named-err-ambiguous.js",
			"language/module-code/instn-resolve-empty-export.js",
			"language/module-code/instn-iee-err-circular-as.js",
			"language/module-code/instn-iee-err-not-found-as.js",
			"language/module-code/instn-resolve-err-reference.js",
			"language/module-code/instn-named-err-ambiguous-as.js",
			"language/module-code/instn-named-err-dflt-thru-star-dflt.js",
			"language/module-code/instn-named-err-dflt-thru-star-as.js",
			"language/module-code/instn-named-err-not-found.js",
			"language/module-code/instn-iee-err-ambiguous-as.js",
			"language/module-code/eval-rqstd-abrupt.js",
			"language/module-code/instn-named-star-cycle.js",
			"language/module-code/instn-iee-star-cycle.js",
			"language/module-code/instn-iee-err-dflt-thru-star-as.js",
			"language/module-code/instn-named-err-not-found-dflt.js",
			"language/module-code/instn-resolve-order-src.js",
			"language/module-code/instn-iee-err-not-found.js",
			"language/module-code/instn-resolve-order-depth.js",
			"language/module-code/instn-star-err-not-found.js"
	));

	private static final String testsDir = "src/test/resources/test262/test/";

	private enum Test262Negative {
		PARSE, EARLY, EXECUTE, NONE
	}

	private static final class Test262Info {
		@Nonnull
		public final String name;
		public final Test262Negative negative;
		public final boolean noStrict;
		public final boolean onlyStrict;
		public final boolean async;
		public final boolean module;

		public Test262Info(@Nonnull String name, @Nonnull Test262Negative negative, boolean noStrict, boolean onlyStrict, boolean async, boolean module) {
			this.name = name;
			this.negative = negative;
			this.noStrict = noStrict;
			this.onlyStrict = onlyStrict;
			this.async = async;
			this.module = module;
		}
	}

	@Nullable
	private static Test262Info extractTest262Info(@Nonnull String path, @Nonnull String source) {
		// extract comment block
		int test262CommentBegin = source.indexOf("/*---");
		if (test262CommentBegin < 0) {
			return null;
		}
		test262CommentBegin += 5;
		int test262CommentEnd = source.indexOf("---*/", test262CommentBegin);
		if (test262CommentEnd < 0) {
			return null;
		}
		String yaml = source.substring(test262CommentBegin, test262CommentEnd);
		Object rawParsedYaml = yamlParser.load(yaml);
		if (!(rawParsedYaml instanceof Map)) {
			return null;
		}
		Map<String, Object> parsedYaml = (Map<String, Object>) rawParsedYaml;
		// extract flags and negative
		Object rawNegative = parsedYaml.get("negative");
		Test262Negative negativeEnum = Test262Negative.NONE;
		if (rawNegative != null) {
			if (!(rawNegative instanceof Map)) {
				return null;
			}
			Map<String, Object> negative = (Map<String, Object>) rawNegative;
			String phase = (String) negative.get("phase");
			if (phase == null) {
				return null;
			}
			switch (phase) {
				case "parse":
					negativeEnum = Test262Negative.PARSE;
					break;
				case "early":
					negativeEnum = Test262Negative.EARLY;
					break;
				default:
					negativeEnum = Test262Negative.EXECUTE;
					break;
			}
		}
		Object rawFlags = parsedYaml.get("flags");
		boolean noStrict = false;
		boolean onlyStrict = false;
		boolean async = false;
		boolean module = false;
		if (rawFlags != null) {
			ArrayList<String> flags = (ArrayList<String>) rawFlags;
			for (String flag : flags) {
				switch (flag) {
					case "noStrict":
						noStrict = true;
						break;
					case "onlyStrict":
						onlyStrict = true;
						break;
					case "async":
						async = true;
						break;
					case "module":
						module = true;
						break;
				}
			}
		}
		return new Test262Info(path, negativeEnum, noStrict, onlyStrict, async, module);
	}

	private static final class Test262Exception extends RuntimeException {

		@Nonnull
		public final String name;

		public Test262Exception(@Nonnull String name, @Nonnull String message) {
			super(message);
			this.name = name;
		}

		public Test262Exception(@Nonnull String name, @Nonnull String message, @Nonnull Throwable caused) {
			super(message, caused);
			this.name = name;
		}
	}

	private void runTest262Test(@Nonnull String source, @Nonnull Path path, @Nonnull Test262Info info, boolean strict) {
		boolean xfailedParse = xfailParse.contains(info.name);
		try {
			Program program;
			if (info.module) {
				program = Parser.parseModule(source);
			} else if (strict) {
				program = Parser.parseScript("\"use strict\";\n" + source);
			} else {
				program = Parser.parseScript(source);
			}
			if ((info.negative == Test262Negative.PARSE) != xfailedParse) {
				throw new Test262Exception(info.name, "Parsed and should not have: " + path.toString());
			}
			boolean xfailedEarly = xfailEarlyErrors.contains(info.name);
			ImmutableList<EarlyError> earlyErrors = EarlyErrorChecker.validate(program);
			boolean passEarlyError = earlyErrors.length == 0;
			if (passEarlyError && (info.negative == Test262Negative.EARLY) != xfailedEarly) {
				throw new Test262Exception(info.name, "Passed early errors and should not have: " + path.toString());
			} else if (!passEarlyError && ((info.negative == Test262Negative.EARLY) == xfailedEarly)) {
				throw new Test262Exception(info.name, "Failed early errors and should not have: " + path.toString(),
						new RuntimeException(earlyErrors.foldLeft((acc, error) -> error.message + "\n" + acc, "")));
			}
		} catch (JsError e) {
			if ((info.negative == Test262Negative.PARSE) == xfailedParse && info.negative != Test262Negative.EARLY) { // we classify some early errors as parse errors
				throw new Test262Exception(info.name, "Did not parse and should have: " + path.toString(), e);
			}
		}
	}

	private void runTest(@Nonnull Path root, @Nonnull Path path) throws IOException {
		if (Files.isDirectory(path) || !path.toString().endsWith(".js")) {
			return;
		}
		String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		Test262Info info = extractTest262Info(root.relativize(path).toString(), source);
		if (info == null) { // parse failure, probably a fixture
			return;
		}
		if (!info.onlyStrict && !info.module) {
			runTest262Test(source, path, info, false);
		}
		if (!info.noStrict) {
			runTest262Test(source, path, info, true);
		}
	}



	@Test
	public void testTest262() throws Exception {
		LinkedList<Test262Exception> exceptions = new LinkedList<>();
		Path root = Paths.get(testsDir);
		Files.walk(root).forEach(path -> {
			try {
				runTest(root, path);
			} catch (IOException e) {
				Assert.fail(e.toString());
			} catch (Test262Exception e) {
				exceptions.add(e);
			}
		});
		if (exceptions.size() > 0) {
			for (Test262Exception exception : exceptions) {
				exception.printStackTrace();
			}
			System.out.println(exceptions.size() + " test262 tests failed:");
			for (Test262Exception exception : exceptions) {
				System.out.println("    " + exception.name + ": " + exception.getMessage());
			}
			Assert.fail();
		}
	}
}
