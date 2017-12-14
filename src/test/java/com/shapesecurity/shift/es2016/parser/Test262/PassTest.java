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
	static final String testsDir = "test/test262-parser-tests/pass/";
	static final String expectationsDir = "test/shift-parser-expectations/expectations/";

	static final Set<String> xfail = new HashSet<>(Arrays.asList(
			"00c79d09c52df3ec.js", // something about destructuring
			"05b849122b429743.js", // Java's unicode support appears to be out of date
			"0d137e8a97ffe083.js", // yield flag passes to nested functions
			"0f88c334715d2489.js", // yield precedence issue
			"1093d98f5fc0758d.js", // something about destructuring parameters
			"151d4db59b774864.js", // Java's unicode support appears to be out of date
			"15d9592709b947a0.js", // something about destructuring parameters
			"177fef3d002eb873.js", // yield flag passes to nested functions
			"3f44c09167d5753d.js", // Java's unicode support appears to be out of date
			"431ecef8c85d4d24.js", // Java's unicode support appears to be out of date
			"465b79616fdc9794.js", // Java's unicode support appears to be out of date
			"489e6113a41ef33f.js", // '&' and '|' have the wrong relative precedence
			"4e1a0da46ca45afe.js", // something about destructuring parameters
			"5dd65055dace49bc.js", // something about destructuring
			"6b76b8761a049c19.js", // yield flag passes to nested functions
			"72d79750e81ef03d.js", // deserializer breaks on **
			"7dab6e55461806c9.js", // yield precedence issue
			"901fca17189cd709.js", // yield flag passes to nested functions
			"988e362ed9ddcac5.js", // deserializer breaks on **
			"99fceed987b8ec3d.js", // something about destructuring parameters
			"9bcae7c7f00b4e3c.js", // something about destructuring parameters
			"a43df1aea659fab8.js", // '&' and '|' have the wrong relative precedence
			"c3699b982b33926b.js", // '&' and '|' have the wrong relative precedence
			"c546a199e87abaad.js", // for-in destructing containing in breaks
			"cb211fadccb029c7.js", // yield precedence issue
			"cbc644a20893a549.js", // '&' and '|' have the wrong relative precedence
			"cd2f5476a739c80a.js", // can't use 'in' as argument to 'new'
			"ce968fcdf3a1987c.js", // yield precedence issue
			"db3c01738aaf0b92.js", // deserializer breaks on **
			"e1387fe892984e2b.js", // something about destructuring parameters
			"ec97990c2cc5e0e8.js", // '&' and '|' have the wrong relative precedence
			"" // empty line to make git diffs nicer
	));

	static void check(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		// TODO check locations (requires deserializer subclass)

		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);
		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);

		if (name.endsWith(".module.js")) {
			Module actual = Parser.parseModule(src);
			Module expected = (Module) Deserializer.deserialize(expectedJSON);
			if (!expected.equals(actual)) {
				// TODO: a more informative tree-equality check with a treewalker
				throw new RuntimeException("Trees don't match!");
			}
			EarlyErrorChecker.validate(actual);
		} else {
			Script actual = Parser.parseScript(src);
			Script expected = (Script) Deserializer.deserialize(expectedJSON);
			if (!expected.equals(actual)) {
				throw new RuntimeException("Trees don't match!");
			}
			EarlyErrorChecker.validate(actual);
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
