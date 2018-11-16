package com.shapesecurity.shift.es2017.test262.parser;

import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
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

@RunWith(Parameterized.class)
public class EarlyTest {
	static final String testsDir = "src/test/resources/test262-parser-tests/early/";

	static final Set<String> xfail = new HashSet<>(Arrays.asList(
		// BUG: RegEx issues
		// https://github.com/shapesecurity/shift-parser-js/issues/130
		"0e631216f7271fce.js",
		"1447683fba196181.js",
		"4de83a7417cd30dd.js",

		// Early Errors which our AST format can't represent
		"024073814ce2cace.js",
		"033064204b44b686.js",
		"1c22bc1b20bdacf1.js",
		"21f3dc70492447d0.js",
		"0ed820b1748fb826.js",
		"21f3dc70492447d0.js",
		"24e27ce4ea8b1550.js",
		"2b4520facdc72696.js",
		"2c3cbce523ad436e.js",
		"3121a50c07bfa0aa.js",
		"3bc32f5a77e8468e.js",
		"5995f93582b8bd22.js",
		"59eb4e0a7c215b4c.js",
		"5e4a34251d0fc48d.js",
		"63452bbeb15314d6.js",
		"670343391d88a743.js",
		"78cb084c22573e4a.js",
		"79592a4804326355.js",
		"7a2bf91be132b22d.js",
		"80c2d2d1d35c8dcc.js",
		"869ac2b391a8c7cd.js",
		"8b659d2805837e98.js",
		"8c62442458de872f.js",
		"90c089b382d8aaf9.js",
		"9d030e1cf08f5d77.js",
		"a633b3217b5b8026.js",
		"b4cac065cfcbc658.js",
		"b6a72a718cb7ca67.js",
		"bd0a88a0f6b9250e.js",
		"cdea3406c440ecf3.js",
		"e9a40a98ec62f818.js",
		"ea84c60f3b157d35.js",
		"f0e47254d16fb114.js",
		"f933ec047b8a7c3d.js",
		"d52f769ab39372c7.js",
		"c9566d6dccc93ae5.js",
		"ef63ef2fa948d9b1.js",
		"228aa4eba2418335.js",
		"2e95646f9143563e.js",
		"37cb3282e0c7d516.js",
		"d008c8cd68d4446e.js",

		// Invalid tests
		// https://github.com/tc39/test262-parser-tests/issues/15
		"14eaa7e71c682461.js",
		"aca911e336954a5b.js",

		// functions with reserved names whose bodies are strict: https://github.com/tc39/ecma262/pull/1158
		"050a006ae573e260.js",
		"2c0f785914da9d0b.js",
		"574ea84fc61bdc31.js",
		"6c4fe38464c16309.js",
		"8643da76fe7e95c7.js",
		"e0c3d30b6fe96812.js",

		"" // empty line to make git diffs nicer
	));

	static void check(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);

		boolean parsed = false;
		boolean validated = false;
		try {
			Program tree = name.endsWith(".module.js") ? Parser.parseModule(src) : Parser.parseScript(src);
			parsed = true;
			validated = EarlyErrorChecker.validate(tree).isEmpty();
		} catch (JsError e) {
			// Just swallow it
		}

		if (!parsed) {
			throw new RuntimeException("Early test did not parse! " + name);
		}
		if (validated) {
			throw new RuntimeException("Early test failed to throw early error! " + name);
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
		XFailHelper.wrap(this.name, xfail, EarlyTest::check);
	}
}
