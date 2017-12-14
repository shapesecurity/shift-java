package com.shapesecurity.shift.es2016.parser.Test262;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import com.shapesecurity.shift.es2016.serialization.Deserializer;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PassTest {
	static final String testsDir = "src/test/resources/test262-parser-tests/pass/";
	static final String expectationsDir = "src/test/resources/shift-parser-expectations/expectations/";

	static final Set<String> xfail = new HashSet<>(Arrays.asList(
			// BUG: something about destructuring
			"00c79d09c52df3ec.js",
			"5dd65055dace49bc.js",

			// BUG: Java's unicode support appears to be out of date
			"05b849122b429743.js",
			"3f44c09167d5753d.js",
			"431ecef8c85d4d24.js",
			"151d4db59b774864.js",
			"465b79616fdc9794.js",

			// BUG: yield flag passes to nested functions
			"0d137e8a97ffe083.js",
			"177fef3d002eb873.js",
			"6b76b8761a049c19.js",
			"901fca17189cd709.js",

			// BUG: yield precedence issue
			"0f88c334715d2489.js",
			"7dab6e55461806c9.js",
			"cb211fadccb029c7.js",
			"ce968fcdf3a1987c.js",

			// BUG: something about destructuring parameters
			"1093d98f5fc0758d.js",
			"15d9592709b947a0.js",
			"4e1a0da46ca45afe.js",
			"99fceed987b8ec3d.js",
			"9bcae7c7f00b4e3c.js",
			"e1387fe892984e2b.js",

			// BUG: something about '&'
			"489e6113a41ef33f.js",
			"a43df1aea659fab8.js",
			"c3699b982b33926b.js",
			"cbc644a20893a549.js",
			"ec97990c2cc5e0e8.js",

			// BUG: deserializer breaks on **
			"72d79750e81ef03d.js",
			"988e362ed9ddcac5.js",
			"db3c01738aaf0b92.js",

			// BUG: for-in destructing containing in breaks doesn't work
			"c546a199e87abaad.js",

			// BUG: can't use 'in' as argument to 'new'
			"cd2f5476a739c80a.js",

			// BUG: exports are treated as declarations
			"e2470430b235b9bb.module.js",

			"" // empty line to make git diffs nicer
	));

	static void check(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		// TODO check locations (requires deserializer subclass)

		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);
		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);

		if (name.endsWith(".module.js")) {
			Module actual = Parser.parseModule(src);
			if (EarlyErrorChecker.validate(actual).isNotEmpty()) {
				throw new RuntimeException("Pass test throws early error!");
			}

			Module expected = (Module) Deserializer.deserialize(expectedJSON);
			if (!expected.equals(actual)) {
				// TODO: a more informative tree-equality check with a treewalker
				throw new RuntimeException("Trees don't match!");
			}
		} else {
			Script actual = Parser.parseScript(src);

			if (EarlyErrorChecker.validate(actual).isNotEmpty()) {
				throw new RuntimeException("Pass test throws early error!");
			}

			Script expected = (Script) Deserializer.deserialize(expectedJSON);
			if (!expected.equals(actual)) {
				throw new RuntimeException("Trees don't match!");
			}
		}
	}

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		File[] files = (new File(testsDir)).listFiles();
		return Arrays.stream(files)
				.map(f -> new Object[]{ f.getName() })
				.collect(Collectors.toList());
	}

	@Parameterized.Parameter
	public String name;

	@Test
	public void test() throws Exception {
		XFailHelper.wrap(this.name, xfail, PassTest::check);
	}
}
