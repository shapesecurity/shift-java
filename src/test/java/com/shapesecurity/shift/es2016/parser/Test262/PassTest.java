package com.shapesecurity.shift.es2016.parser.Test262;

import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
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
			"05b849122b429743.js" // Java's unicode support appears to be out of date
	));
	// TODO have some test to ensure all xfail tests actually exist

	static void checkPass(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		// TODO check locations (requires deserializer subclass)

		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);
		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);

		if (name.endsWith(".module.js")) {
			Module actual = Parser.parseModule(src);
			Module expected = (Module) Deserializer.deserialize(expectedJSON);
			assertEquals(expected, actual);
		} else {
			Script actual = Parser.parseScript(src);
			Script expected = (Script) Deserializer.deserialize(expectedJSON);
			assertEquals(expected, actual);
		}
	}

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		File[] passFiles = (new File(testsDir)).listFiles();
		return Arrays.stream(passFiles)
				.map(f -> new Object[]{ f.getName() })
				.collect(Collectors.toList());
	}

	@Parameterized.Parameter
	public String name;

	@Test
	public void test() throws Exception {
		XFailHelper.wrap(this.name, xfail, PassTest::checkPass);
	}
}
