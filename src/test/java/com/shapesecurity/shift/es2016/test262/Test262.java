package com.shapesecurity.shift.es2016.test262;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.parser.EarlyError;
import com.shapesecurity.shift.es2016.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test262 {

	private static final HashSet<String> xfailParse = new HashSet<>(Arrays.asList(
			// shift-java#198
			"src/test/resources/test262/test/annexB/language/statements/for-in/nonstrict-initializer.js",
			"src/test/resources/test262/test/annexB/language/statements/for-in/bare-initializer.js",
			"src/test/resources/test262/test/annexB/language/statements/for-in/strict-initializer.js",
			"src/test/resources/test262/test/annexB/language/statements/for-in/var-objectbindingpattern-initializer.js",
			"src/test/resources/test262/test/annexB/language/statements/for-in/var-arraybindingpattern-initializer.js",

			// async/await not implemented
			"src/test/resources/test262/test/language/statements/class/definition/methods-async-super-call-body.js",
			"src/test/resources/test262/test/language/statements/class/definition/class-method-returns-promise.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-async-super-call-param.js",
			"src/test/resources/test262/test/built-ins/Function/prototype/toString/async-function-expression.js",
			"src/test/resources/test262/test/built-ins/Function/prototype/toString/async-method.js",
			"src/test/resources/test262/test/built-ins/Function/prototype/toString/AsyncFunction.js",
			"src/test/resources/test262/test/built-ins/Function/prototype/toString/async-function-declaration.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/async-super-call-body.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/async-super-call-param.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/object-method-returns-promise.js",

			// yield not implemented
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-as-label.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-as-binding-identifier.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-as-logical-or-expression.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-as-parameter.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-star-after-newline.js",
			"src/test/resources/test262/test/language/statements/class/definition/methods-gen-yield-weak-binding.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-as-logical-or-expression.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-weak-binding.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-as-parameter.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-as-label.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-star-after-newline.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/yield-as-binding-identifier.js",

			// trailing commas not in ES6
			"src/test/resources/test262/test/language/statements/class/definition/params-trailing-comma.js",
			"src/test/resources/test262/test/language/statements/class/definition/params-trailing-comma-length.js",
			"src/test/resources/test262/test/language/statements/function/params-trailing-comma.js",
			"src/test/resources/test262/test/language/statements/function/params-trailing-comma-length.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/params-trailing-comma-length.js",
			"src/test/resources/test262/test/language/expressions/object/method-definition/params-trailing-comma.js",
			"src/test/resources/test262/test/language/expressions/arrow-function/params-trailing-comma-length.js",
			"src/test/resources/test262/test/language/expressions/arrow-function/params-trailing-comma.js",
			"src/test/resources/test262/test/language/expressions/function/params-trailing-comma-length.js",
			"src/test/resources/test262/test/language/expressions/function/params-trailing-comma.js",

			// broken test
			"src/test/resources/test262/test/language/module-code/namespace/internals/set-prototype-of-null.js",

			// shift-java#207
			"src/test/resources/test262/test/language/expressions/tagged-template/invalid-escape-sequences.js"

	));

	private static final HashSet<String> xfailEarlyErrors = new HashSet<>(Arrays.asList(

			// exponentiation early errors not implemented
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-delete-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-typeof-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-logical-not-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-void-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-negate-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-plus-unary-expression-base.js",
			"src/test/resources/test262/test/language/expressions/exponentiation/exp-operator-syntax-error-bitnot-unary-expression-base.js",

			// yield not implemented
			"src/test/resources/test262/test/language/expressions/assignment/dstr-obj-id-identifier-yield-expr.js",
			"src/test/resources/test262/test/language/statements/for-in/dstr-obj-id-identifier-yield-expr.js",
			"src/test/resources/test262/test/language/statements/for-of/dstr-obj-id-identifier-yield-expr.js",

			// shift-java#208
			"src/test/resources/test262/test/language/global-code/decl-lex-restricted-global.js"
	));

	private static final HashSet<String> skip = new HashSet<>(Arrays.asList(
			// async/await not implemented
			"src/test/resources/test262/test/language/expressions/async-arrow-function",
			"src/test/resources/test262/test/language/expressions/async-function",
			"src/test/resources/test262/test/language/statements/async-function",
			"src/test/resources/test262/test/built-ins/AsyncFunction",
			"src/test/resources/test262/test/language/expressions/await",
			// no regex acceptor
			"src/test/resources/test262/test/language/literals/regexp",
			// generators not implemented
			"src/test/resources/test262/test/language/expressions/generators",
			"src/test/resources/test262/test/language/statements/generators",

			// no module early errors
			"src/test/resources/test262/test/language/module-code"
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

		// extract categories -- accounting for various authors indentations.
		Scanner scanner = new Scanner(source.substring(test262CommentBegin, test262CommentEnd));
		int baseIndentation = -10;
		StringBuilder currentBlock = new StringBuilder();
		String currentName = null;
		HashMap<String, String> categories = new HashMap<>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			int indentation = 0;
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == ' ') {
					indentation++;
				} else if (c == '\t') {
					indentation += 2;
				} else {
					break;
				}
			}

			if (baseIndentation == -10 || indentation <= baseIndentation) {
				line = line.substring(indentation);
				int colonOffset = line.indexOf(":");
				if (colonOffset > 0) {
					if (baseIndentation == -10) {
						baseIndentation = indentation;
					}
					if (currentName != null) {
						categories.put(currentName, currentBlock.toString());
					}
					currentBlock = new StringBuilder();
					currentName = line.substring(0, colonOffset);
					currentBlock.append(line.substring(colonOffset + 1)).append("\n");
				}
			} else {
				currentBlock.append(line).append("\n");
			}
		}
		if (currentName != null) {
			categories.put(currentName, currentBlock.toString());
		}
		// extract flags and negative
		String negative = categories.get("negative");
		Test262Negative negativeEnum = Test262Negative.NONE;
		if (negative != null) {
			if (negative.contains("phase: parse")) {
				negativeEnum = Test262Negative.PARSE;
			} else if (negative.contains("phase: early")) {
				negativeEnum = Test262Negative.EARLY;
			} else {
				negativeEnum = Test262Negative.EXECUTE;
			}
		}
		String flags = categories.get("flags");
		boolean noStrict = false;
		boolean onlyStrict = false;
		boolean async = false;
		boolean module = false;
		if (flags != null) {
			int flagStart = flags.indexOf("[");
			if (flagStart < 0) {
				return null;
			}
			int flagEnd = flags.indexOf("]", flagStart);
			if (flagEnd < 0) {
				return null;
			}
			String[] flagArray = flags.substring(flagStart + 1, flagEnd).split(", *");
			for (String flag : flagArray) {
				if (flag.equals("noStrict")) {
					noStrict = true;
				} else if (flag.equals("onlyStrict")) {
					onlyStrict = true;
				} else if (flag.equals("async")) {
					async = true;
				} else if (flag.equals("module")) {
					module = true;
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

	private void runTest(@Nonnull Path path) throws IOException {
		if (Files.isDirectory(path) || !path.toString().endsWith(".js")) {
			return;
		}
		if (skip.contains(path.getParent().toString())) {
			return;
		}
		String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		Test262Info info = extractTest262Info(path.toString(), source);
		if (info == null) { // parse failure, probably a fixture
			return;
		}
		if (!info.onlyStrict && !info.module) {
			boolean xfailedParse = xfailParse.contains(info.name);
			try {
				Script script = Parser.parseScript(source);
				if ((info.negative == Test262Negative.PARSE) != xfailedParse) {
					throw new Test262Exception(info.name, "Parsed and should not have: " + path.toString());
				}
				boolean xfailedEarly = xfailEarlyErrors.contains(info.name);
				ImmutableList<EarlyError> earlyErrors = EarlyErrorChecker.validate(script);
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
		if (!info.noStrict) {
			boolean xfailedParse = xfailParse.contains(info.name);
			try {
				Program program;
				if (info.module) {
					program = Parser.parseModule(source);
				} else {
					program = Parser.parseScript("\"use strict\";\n" + source);
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
	}

	@Test
	public void testTest262() throws Exception {
		LinkedList<Test262Exception> exceptions = new LinkedList<>();
		Files.walk(Paths.get(testsDir)).forEach(path -> {
			try {
				runTest(path);
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
