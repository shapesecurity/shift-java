package com.shapesecurity.shift.es2016.parser.Test262;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.parser.EarlyErrorChecker;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import com.shapesecurity.shift.es2016.path.BranchGetter;
import com.shapesecurity.shift.es2016.path.BranchIterator;
import com.shapesecurity.shift.es2016.serialization.Deserializer;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
		String src = new String(Files.readAllBytes(Paths.get(testsDir, name)), StandardCharsets.UTF_8);
		ParserWithLocation parser = new ParserWithLocation();
		Program actual = name.endsWith(".module.js") ? parser.parseModule(src) : parser.parseScript(src);

		if (EarlyErrorChecker.validate(actual).isNotEmpty()) {
			throw new RuntimeException("Pass test throws early error!");
		}

		String expectedJSON = new String(Files.readAllBytes(Paths.get(expectationsDir, name + "-tree.json")), StandardCharsets.UTF_8);
		DeserializerWithLocation deserializer = new DeserializerWithLocation();
		Program expected = (Program) deserializer.deserializeNode(new JsonParser().parse(expectedJSON));

		if (expected.equals(actual)) {
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
			ImmutableList<ParserWithLocation.Comment> expectedComments = deserializeComments(commentsJSON);

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
		} else {
			// Trees don't match. To give a useful error message, we find a relatively low node present in both trees which differs.
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

	public static ImmutableList<ParserWithLocation.Comment> deserializeComments(String toDeserialize) {
		JsonElement comments = new JsonParser().parse(toDeserialize);
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
