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

		// BUG: serializer outputs malformed escape
		"0b1fc7208759253b.js",
		"e5a7d56b798ec7e6.js",

		// BUG: validator asserts parsed regex is invalid
		"027abe815032df72.js",
		"046b1012ef9b0e26.js",
		"06981f39d0844079.js",
		"0ffdc03e2ffcb5dc.js",
		"111668493e3e0823.js",
		"14f95b3c9a9e7480.js",
		"17cc7c10e02028be.js",
		"18f05b95a72dffa1.js",
		"1908280b73954ef7.js",
		"1a1c717109ab67e1.js",
		"1de765c987733026.js",
		"206ebb4e67a6daa9.js",
		"24e299720285b6c1.js",
		"26a4b2dddf53ab39.js",
		"27ac24465c731ff9.js",
		"29ef8a7a1cbfda7f.js",
		"2e8a88da875f40c7.js",
		"31237b174ba6047a.js",
		"31ad88cae27258b7.js",
		"3b1fca65828182ab.js",
		"3f8b15109761ea65.js",
		"4412172b5dc13cd6.js",
		"45ed987996568823.js",
		"47fce5046a1b2098.js",
		"495c05812d179d67.js",
		"4ad6e3a59e27e9b1.js",
		"4b6559716b2f7b21.js",
		"4dc600d5ae71e8eb.js",
		"4f805a43cc2e8854.js",
		"52f2f30356750b9b.js",
		"56fd564979894636.js",
		"58cb05d17f7ec010.js",
		"596746323492fbfd.js",
		"5bae374be95382c6.js",
		"645e8cce491528cd.js",
		"66e383bfd18e66ab.js",
		"680880af107834e8.js",
		"6823058797ddd563.js",
		"6861bb23b186f65a.js",
		"6d8c97119162ad95.js",
		"6db7dbc9b1365dfa.module.js",
		"6f6e870785069487.js",
		"739bef73b11c87de.js",
		"779e65d6349f1616.js",
		"784cbc06d5ade346.js",
		"78c215fabdf13bae.js",
		"78ea6e4e98c18f91.js",
		"7c027cdbc7f493b2.js",
		"7e094109208fc749.js",
		"8290412f79ac2bb6.js",
		"8411f3c15e3e8529.js",
		"94846b0ae1cac1a2.js",
		"94be09b126b946b8.js",
		"982835d8c977075c.js",
		"a0079146ab045c26.js",
		"a0b7bf790311b763.js",
		"a6b7dab7088e5269.js",
		"a8b832d61af9cdc4.js",
		"abd5e4aa1a9f99ba.js",
		"b3717dd9314332d2.js",
		"b9a5f5c8c12525c7.js",
		"bc89b2b2f1e19f9e.js",
		"bdfc6c05edd19925.js",
		"bf49ec8d96884562.js",
		"c1914072e996ddbe.js",
		"c78c8fbfbd3e779e.js",
		"c83a2dcf75fa419a.js",
		"c87859666bd18c8c.js",
		"c88c5d1e7e9574b6.js",
		"caf6539007d41b5e.js",
		"cc561e319220c789.js",
		"cdf43a987840ece8.js",
		"d3f70f4410bb8346.js",
		"d53aef16fe683218.js",
		"d55a93310a309c43.js",
		"d59a168fe5b7c787.js",
		"d59a6667e160c0b3.js",
		"dad51383642e0d27.js",
		"dadccefeaae19dbf.js",
		"dec1ae80150e1664.js",
		"c78c8fbfbd3e779e.js",
		"c83a2dcf75fa419a.js",
		"c87859666bd18c8c.js",
		"c88c5d1e7e9574b6.js",
		"caf6539007d41b5e.js",
		"cc561e319220c789.js",
		"cdf43a987840ece8.js",
		"d3f70f4410bb8346.js",
		"d53aef16fe683218.js",
		"d55a93310a309c43.js",
		"d59a168fe5b7c787.js",
		"d59a6667e160c0b3.js",
		"dad51383642e0d27.js",
		"dadccefeaae19dbf.js",
		"dec1ae80150e1664.js",
		"e4a43066905a597b.js",
		"e6ac25f6aa73a2be.js",
		"ec782937135d4f32.js",
		"f0f9e218a70eba5c.js",
		"f1643d0e6c7fde9a.js",
		"f2d394b74219a023.js",
		"f3219596b50bb381.js",
		"f80f30fbdd7e7b19.js",
		"fb7c5656640f6ec7.js",
		"fc020c065098cbd5.js",
		"ff902593b25092d1.js",
		"fffe7e78a7ce9f9a.js",

		// BUG: validator asserts parsed identifier is not an identifier
		"c85fbdb8c97e0534.js",
		"dafb7abe5b9b44f5.js",
		"c85fbdb8c97e0534.js",
		"dafb7abe5b9b44f5.js",
		"eaee2c64dfc46b6a.js",
		"ed32642380a6e603.js",
		"efb88a0b6e2e170e.js",

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
