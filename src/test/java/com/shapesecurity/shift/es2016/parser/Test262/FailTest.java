package com.shapesecurity.shift.es2016.parser.Test262;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
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
public class FailTest {
	static final String testsDir = "test/test262-parser-tests/fail/";

	static final Set<String> xfail = new HashSet<>(Arrays.asList(
			"13ce2dd24993176a.js", // destructuring in for loop head requires initialization
			"29fb02620b662387.js", // destructuring in for loop head requires initialization
			"37cb7557997d4fd6.js", // destructuring in for loop head requires initialization
			"40449ddc6ec37b35.js", // arrow precedence
			"4ff4b78ff3e2de6e.js", // arrow precedence
			"6b9bc191e6f5ef69.js", // destructuring in for loop head requires initialization
			"a651ee9d0db08692.js", // destructuring in for loop head requires initialization
			"ca3dd7ea0b4626dd.js", // arrow precedence
			"d17d3aebb6a3cf43.js", // destructuring in for loop head requires initialization
			"" // empty line to make git diffs nicer
	));

	static void check(String name) throws IOException {
		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);

		try {
			if (name.endsWith(".module.js")) {
				Parser.parseModule(src);
			} else {
				Parser.parseScript(src);
			}
			throw new RuntimeException("Test parsed despite being marked as fail");
		} catch (JsError e) {
			// Good.
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
		XFailHelper.wrap(this.name, xfail, FailTest::check);
	}
}
