package com.shapesecurity.shift.es2016.Test262;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.codegen.CodeGen;
import com.shapesecurity.shift.es2016.parser.EarlyError;
import com.shapesecurity.shift.es2016.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import com.shapesecurity.shift.es2016.path.BranchGetter;
import com.shapesecurity.shift.es2016.path.BranchIterator;
import com.shapesecurity.shift.es2016.serialization.Deserializer;
import com.shapesecurity.shift.es2016.serialization.Serializer;
import com.shapesecurity.shift.es2016.validator.ValidationError;
import com.shapesecurity.shift.es2016.validator.Validator;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PassTest {
	static final String testsDir = "src/test/resources/test262-parser-tests/pass/";
	static final String expectationsDir = "src/test/resources/shift-parser-expectations/expectations/";

	static final Set<String> xfail = new HashSet<>(Arrays.asList(
		// BUG: Java's unicode support appears to be out of date
		"05b849122b429743.js",
		"3f44c09167d5753d.js",
		"431ecef8c85d4d24.js",
		"151d4db59b774864.js",
		"465b79616fdc9794.js",

		// BUG(s): codegen produces invalid or unequal output
		"08358cb4732d8ce1.js",
		"4d2c7020de650d40.js",
		"5c3d125ce5f032aa.js",
		"da9e16ac9fd5b61d.js",
		"dc6037a43bed9588.js",
		"f5b89028dfa29f27.js",
		"f7f611e6fdb5b9fc.js",

		// BUG: validator asserts parsed identifier is not an identifier
		"31237b174ba6047a.js",
		"58cb05d17f7ec010.js",
		"596746323492fbfd.js",
		"abd5e4aa1a9f99ba.js",
		"c85fbdb8c97e0534.js",
		"dafb7abe5b9b44f5.js",
		"c85fbdb8c97e0534.js",
		"dafb7abe5b9b44f5.js",
		"eaee2c64dfc46b6a.js",
		"ed32642380a6e603.js",
		"efb88a0b6e2e170e.js",

		// BUG: multiline templates are wrong somehow
		"45ed987996568823.js",
		"6d8c97119162ad95.js",
		"94be09b126b946b8.js",

		"" // empty line to make git diffs nicer
	));

	static void assertTreesEqual(Program expected, Program actual) {
		if (expected.equals(actual)) return;

		// To give a useful error message, we find a relatively low node present in both trees which differs.
		List<Pair<BranchGetter, Node>> locations = new ArrayList<>();
		(new BranchIterator(expected)).forEach(locations::add);
		for (int i = locations.size() - 1; i >= 0; --i) { // reverse order so we never report a parent when a child would do
			BranchGetter location = locations.get(i).left;
			Node expectedNode = locations.get(i).right;
			Maybe<? extends Node> maybeActual = location.apply(actual);

			if (maybeActual.isJust() && !maybeActual.fromJust().equals(expectedNode)) {
				Class actualClass = maybeActual.fromJust().getClass();
				if (actualClass.equals(expectedNode.getClass())) {
					throw new RuntimeException("Trees differ - nodes of the same type but differ structurally (at root" + location.toString() + ")");
				} else {
					throw new RuntimeException("Trees differ - expected " + expectedNode.getClass().getSimpleName() + " but got " + actualClass.getSimpleName() + " (at root" + location.toString() + ")");
				}
			}
		}
		throw new RuntimeException("Unreachable - trees unequal but have no unequal nodes");
	}

	static void check(String name) throws JsError, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, JSONException, IllegalAccessException {
		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);
		DeserializerWithLocation deserializer = new DeserializerWithLocation();
		Program expected = (Program) deserializer.deserializeNode(new JsonParser().parse(expectedJSON));

		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);
		ParserWithLocation parser = new ParserWithLocation();
		Program actual = name.endsWith(".module.js") ? parser.parseModule(src) : parser.parseScript(src);

		// check trees
		assertTreesEqual(expected, actual);

		// check locations
		for (Pair<BranchGetter, Node> p : new BranchIterator(actual)) {
			Node expectedNode = p.left.apply(expected).fromJust();
			Node actualNode = p.right;

			Maybe<SourceSpan> maybeExpectedLocation = deserializer.getLocation(expectedNode);
			Maybe<SourceSpan> maybeActualLocation = parser.getLocation(actualNode);

			if (maybeExpectedLocation.isNothing() && maybeActualLocation.isJust()) {
				throw new RuntimeException("Node unexpectedly has location (root" + p.left.toString() + ")");
			}
			if (maybeExpectedLocation.isJust() && maybeActualLocation.isNothing()) {
				throw new RuntimeException("Node unexpectedly lacks location (root" + p.left.toString() + ")");
			}
			if (maybeExpectedLocation.isJust() && maybeActualLocation.isJust()) {
				SourceSpan expectedLocation = maybeExpectedLocation.fromJust();
				SourceSpan actualLocation = maybeActualLocation.fromJust();

				assertEquals("start line (root" + p.left.toString() + ")", expectedLocation.start.line, actualLocation.start.line);
				assertEquals("start column (root" + p.left.toString() + ")", expectedLocation.start.column, actualLocation.start.column);
				assertEquals("start offset (root" + p.left.toString() + ")", expectedLocation.start.offset, actualLocation.start.offset);
				assertEquals("end line (root" + p.left.toString() + ")", expectedLocation.end.line, actualLocation.end.line);
				assertEquals("end column (root" + p.left.toString() + ")", expectedLocation.end.column, actualLocation.end.column);
				assertEquals("end offset (root" + p.left.toString() + ")", expectedLocation.end.offset, actualLocation.end.offset);
			}
		}

		// check comments
		String commentsJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-comments.json")), StandardCharsets.UTF_8);
		ImmutableList<ParserWithLocation.Comment> expectedComments = deserializeComments(new JsonParser().parse(commentsJSON));

		ImmutableList<ParserWithLocation.Comment> actualComments = parser.getComments();

		assertEquals(expectedComments.length, actualComments.length);

		for (Pair<ParserWithLocation.Comment, ParserWithLocation.Comment> p : expectedComments.zipWith(Pair::of, actualComments)) {
			ParserWithLocation.Comment expectedComment = p.left;
			ParserWithLocation.Comment actualComment = p.right;

			assertEquals(expectedComment.type, actualComment.type);
			assertEquals(expectedComment.text, actualComment.text);
			assertEquals(expectedComment.start.line, actualComment.start.line);
			assertEquals(expectedComment.start.column, actualComment.start.column);
			assertEquals(expectedComment.start.offset, actualComment.start.offset);
			assertEquals(expectedComment.end.line, actualComment.end.line);
			assertEquals(expectedComment.end.column, actualComment.end.column);
			assertEquals(expectedComment.end.offset, actualComment.end.offset);
		}

		// check early error checker
		ImmutableList<EarlyError> earlyErrors = EarlyErrorChecker.validate(actual);
		if (earlyErrors.isNotEmpty()) {
			throw new RuntimeException("Pass test throws early error: " + earlyErrors.maybeHead().fromJust().message);
		}

		// check validator
		ImmutableList<ValidationError> validationErrors = Validator.validate(actual);
		if (validationErrors.isNotEmpty()) {
			throw new RuntimeException("Pass test fails to validate: " + validationErrors.maybeHead().fromJust().message);
		}

		// check codegen
		String codegened = CodeGen.codeGen(actual);
		Program codegenedAndParsed = name.endsWith(".module.js") ? parser.parseModule(codegened) : parser.parseScript(codegened);
		assertTreesEqual(actual, codegenedAndParsed);

		// check serializer and deserializer
		String serialized = Serializer.serialize(actual);
		Program deserialized = (Program) Deserializer.deserialize(serialized);
		assertTreesEqual(actual, deserialized);
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

	static class DeserializerWithLocation extends Deserializer {
		protected HashTable<Node, SourceSpan> locations = HashTable.emptyUsingIdentity();

		@Nonnull
		public Maybe<SourceSpan> getLocation(@Nonnull Node node) {
			return this.locations.get(node);
		}

		public DeserializerWithLocation() {}

		@Override
		protected Node deserializeNode(JsonElement jsonElement) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
			Node result = super.deserializeNode(jsonElement);
			if (result != null) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				if (jsonObject.has("loc")) {
					JsonObject loc = jsonObject.getAsJsonObject("loc");
					JsonObject start = loc.getAsJsonObject("start");
					SourceLocation startLoc = new SourceLocation(
							start.getAsJsonPrimitive("line").getAsInt(),
							start.getAsJsonPrimitive("column").getAsInt(),
							start.getAsJsonPrimitive("offset").getAsInt()
					);

					JsonObject end = loc.getAsJsonObject("end");
					SourceLocation endLoc = new SourceLocation(
							end.getAsJsonPrimitive("line").getAsInt(),
							end.getAsJsonPrimitive("column").getAsInt(),
							end.getAsJsonPrimitive("offset").getAsInt()
					);

					locations = locations.put(result, new SourceSpan(Maybe.empty(), startLoc, endLoc));
				}
			}
			return result;
		}
	}

	public static ImmutableList<ParserWithLocation.Comment> deserializeComments(JsonElement comments) {
		return ImmutableList.from(
			StreamSupport.stream(comments.getAsJsonArray().spliterator(), false)
				.map(c -> {
					JsonObject comment = c.getAsJsonObject();
					ParserWithLocation.Comment.Type type;
					switch (comment.getAsJsonPrimitive("type").getAsString()) {
						case "SingleLine":
							type = ParserWithLocation.Comment.Type.SingleLine;
							break;
						case "MultiLine":
							type = ParserWithLocation.Comment.Type.MultiLine;
							break;
						case "HTMLOpen":
							type = ParserWithLocation.Comment.Type.HTMLOpen;
							break;
						case "HTMLClose":
							type = ParserWithLocation.Comment.Type.HTMLClose;
							break;
						default:
							throw new RuntimeException("Comment of unrecognized type");
					}
					String text = comment.getAsJsonPrimitive("text").getAsString();
					JsonObject start = comment.getAsJsonObject("start");
					JsonObject end = comment.getAsJsonObject("end");
					return new ParserWithLocation.Comment(
						type,
						text,
						new SourceLocation(start.getAsJsonPrimitive("line").getAsInt(), start.getAsJsonPrimitive("column").getAsInt(), start.getAsJsonPrimitive("offset").getAsInt()),
						new SourceLocation(end.getAsJsonPrimitive("line").getAsInt(), end.getAsJsonPrimitive("column").getAsInt(), end.getAsJsonPrimitive("offset").getAsInt())
					);
				})
				.collect(Collectors.toList())
		);
	}
}
