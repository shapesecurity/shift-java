package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.shift.es2016.ast.Script;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class Test262Test {
	static final String testsDir = "test/test262-parser-tests/";
	static final String expectationsDir = "test/shift-parser-expectations/expectations/";

	private static void checkPass(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		// TODO check locations (requires deserializer subclass)

		String src = new String(Files.readAllBytes(Paths.get(testsDir, "pass", name)), StandardCharsets.UTF_8);
		Script actual = Parser.parseScript(src);

		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);
		Script expected = (Script) Deserializer.deserialize(expectedJSON);

		assertEquals(expected, actual);
	}

	@Parameterized.Parameters(name = "{1} : {0}")
	public static Collection<Object[]> data() {
		File[] passFiles = (new File(testsDir + "pass")).listFiles();
		Stream<Object[]> pass = Arrays.stream(passFiles)
				.map(f -> new Object[]{ f.getName(), TestType.PASS });

		File[] earlyFiles = (new File(testsDir + "early")).listFiles();
		Stream<Object[]> early = Arrays.stream(earlyFiles)
				.map(f -> new Object[]{ f.getName(), TestType.EARLY });

		File[] failFiles = (new File(testsDir + "early")).listFiles();
		Stream<Object[]> fail = Arrays.stream(failFiles)
				.map(f -> new Object[]{ f.getName(), TestType.FAIL });

		return Stream.of(pass, early, fail)
				.flatMap(x -> x)
				.collect(Collectors.toList());
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public TestType type;

	@Test
	public void test() throws Exception {
		switch (this.type) {
			case PASS:
				checkPass(this.name);
				break;
			case EARLY:
				// TODO
			case FAIL:
				// TODO
		}
	}

	public enum TestType {
		PASS,
		EARLY,
		FAIL
	}
}
