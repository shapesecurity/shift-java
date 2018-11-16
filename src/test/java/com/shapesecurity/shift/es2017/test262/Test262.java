package com.shapesecurity.shift.es2017.test262;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.ImmutableSet;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.parser.EarlyError;
import com.shapesecurity.shift.es2017.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.test262.expectations.XFailHelper;
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

	private static final String testsDir = "src/test/resources/test262/test/";

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
		Test262Info.Test262Negative negativeEnum = Test262Info.Test262Negative.NONE;
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
					negativeEnum = Test262Info.Test262Negative.PARSE;
					break;
				case "early":
					negativeEnum = Test262Info.Test262Negative.EARLY;
					break;
				case "runtime":
					negativeEnum = Test262Info.Test262Negative.RUNTIME;
					break;
				case "resolution":
					negativeEnum = Test262Info.Test262Negative.RESOLUTION;
					break;
				default:
					throw new RuntimeException("Invalid negative phase: " + phase);
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
		ImmutableSet<String> featureSet = ImmutableSet.emptyUsingEquality();
		Object rawFeatures = parsedYaml.get("features");
		if (rawFeatures != null) {
			ArrayList<String> features = (ArrayList<String>) rawFeatures;
			for (String feature : features) {
				featureSet = featureSet.put(feature);
			}
		}
		return new Test262Info(path, negativeEnum, noStrict, onlyStrict, async, module, featureSet);
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
		boolean shouldFail = info.negative == Test262Info.Test262Negative.PARSE || info.negative == Test262Info.Test262Negative.EARLY;
		boolean xfailed = XFailHelper.isXFailed(info, shouldFail);
		boolean failed;
		Throwable failureReason;
		try {
			Program program;
			if (info.module) {
				program = Parser.parseModule(source);
			} else if (strict) {
				program = Parser.parseScript("\"use strict\";\n" + source);
			} else {
				program = Parser.parseScript(source);
			}
			ImmutableList<EarlyError> earlyErrors = EarlyErrorChecker.validate(program);
			failed = earlyErrors.length > 0;
			failureReason = new RuntimeException("Early Errors: \n" + earlyErrors.foldLeft((str, error) -> error.message + ", " + str, ""));
		} catch (JsError e) {
			failed = true;
			failureReason = e;
		}
		if (xfailed && failed == shouldFail) {
			if (shouldFail) {
				throw new Test262Exception(info.name, "Expected test to parse, when it should fail, however it failed to parse: " + path.toString(), failureReason);
			} else {
				throw new Test262Exception(info.name, "Expected test to fail to parse, when it should parse, however it did parse: " + path.toString());
			}
		} else if (!xfailed && failed != shouldFail) {
			if (shouldFail) {
				throw new Test262Exception(info.name, "Expected test to fail to parse, but it did not: " + path.toString());
			} else {
				throw new Test262Exception(info.name, "Expected test to parse, but it did not: " + path.toString(), failureReason);
			}
		}
	}

	private void runTest(@Nonnull Path root, @Nonnull Path path) throws IOException {
		if (Files.isDirectory(path) || !path.toString().endsWith(".js") || path.toString().endsWith("_FIXTURE.js")) {
			return;
		}
		String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		Test262Info info = extractTest262Info(root.relativize(path).toString(), source);
		if (info == null) { // parse failure
			throw new Test262Exception(path.toString(), "Failed to parse frontmatter");
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
