package com.shapesecurity.shift.es2017.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import com.shapesecurity.shift.es2017.path.Branch;
import com.shapesecurity.shift.es2017.path.BranchGetter;
import junit.framework.TestCase;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindNodesTest extends TestCase {
	private final BranchGetter firstStmt = new BranchGetter().d(Branch.ScriptStatements_(0));
	private final BranchGetter firstExpr = firstStmt.d(Branch.ExpressionStatementExpression_());

	void checkNode(String source, BranchGetter path) throws JsError {
		checkNodes(source, ImmutableList.of(Pair.of("label", path)));
	}

	void checkNodes(String source, ImmutableList<Pair<String, BranchGetter>> nodes) throws JsError {
		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Program tree = parserWithLocation.parseScript(source);
		ImmutableList<Template.NodeInfo> result = Template.findNodes(tree, parserWithLocation, parserWithLocation.getComments());
		if (result.length < nodes.length) {
			throw new RuntimeException("Too few labels found");
		}
		if (result.length > nodes.length) {
			throw new RuntimeException("Too many labels found");
		}
		outer: for (Pair<String, BranchGetter> label : nodes) {
			Node node = label.right.apply(tree).fromJust();
			for (Template.NodeInfo info : result) {
				if (info.name.equals(label.left)) {
					if (!info.node.equals(node)) {
						throw new RuntimeException("mismatched labels: expected " + node + ", got " + info.node);
					}
					continue outer;
				}
			}
			throw new RuntimeException("couldn't find label \"" + label.left + "\"");
		}

		// assert sorted
		int prev = -1;
		for (Template.NodeInfo info : result) {
			assertTrue(prev < info.comment.start.offset);
			prev = info.comment.start.offset;
		}
	}

	void fails(String source) throws JsError {
		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Program tree = parserWithLocation.parseScript(source);
		try {
			ImmutableList<Template.NodeInfo> result = Template.findNodes(tree, parserWithLocation, parserWithLocation.getComments());
			throw new RuntimeException("Unexpectedly succeeded");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	public void testSimple() throws JsError {
		String source = "" +
			"a + /*# label #*/ b * c; // whee\n";
		checkNode(source, firstExpr.d(Branch.BinaryExpressionRight_()));
	}

	public void testSingleLineComment() throws JsError {
		String source = "\n" +
			"//# label #\n" +
			"class Foo {}";
		checkNode(source, firstStmt);
	}

	public void testLabelType() throws JsError {
		String source = "" +
			"a + /*# label # IdentifierExpression #*/ b * c;\n";
		checkNode(source, firstExpr.d(Branch.BinaryExpressionRight_()).d(Branch.BinaryExpressionLeft_()));
	}

	public void testLabelIsOutermost() throws JsError {
		F<String, String> source = type -> "" +
			" /*# label " + type + "#*/ a + c + d;\n";

		checkNode(source.apply(""), firstStmt);
		checkNode(source.apply("# BinaryExpression "), firstExpr);
		checkNode(source.apply("# IdentifierExpression "), firstExpr.d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionLeft_()));
	}

	public void testMultipleLabels() throws JsError {
		String source = "" +
			"a + /*# foo # IdentifierExpression #*/ b;\n" +
			"0 + /*# bar #*/ 1;\n";
		checkNodes(source, ImmutableList.of(
			Pair.of("foo", firstExpr.d(Branch.BinaryExpressionRight_())),
			Pair.of("bar", (new BranchGetter()).d(Branch.ScriptStatements_(1)).d(Branch.ExpressionStatementExpression_()).d(Branch.BinaryExpressionRight_()))
		));
	}

	public void testMultipleLabelsOnOneNode() throws JsError {
		String source = "" +
			"a + /*# foo #*/ /*# bar #*/ b;\n";
		BranchGetter branchGetter = firstExpr.d(Branch.BinaryExpressionRight_());
		checkNodes(source, ImmutableList.of(
			Pair.of("foo", branchGetter),
			Pair.of("bar", branchGetter)
		));
	}

	public void testCustomMatcher() throws JsError {
		String source = "a + /*$ label $*/ b;";
		F<String, Maybe<Pair<String, Predicate<Node>>>> commentMatcher = string -> {
			Matcher matcher = Pattern.compile("^\\$ ([^$]+) \\$$").matcher(string);
			if (!matcher.matches()) {
				return Maybe.empty();
			}
			return Maybe.of(Pair.of(matcher.group(1), node -> true));
		};

		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Program tree = parserWithLocation.parseScript(source);
		ImmutableList<Template.NodeInfo> result = Template.findNodes(tree, parserWithLocation, parserWithLocation.getComments(), commentMatcher);
		assertEquals(1, result.length);
		Template.NodeInfo info = result.maybeHead().fromJust();

		assertEquals(info.name, "label");
		assertEquals(info.comment, parserWithLocation.getComments().maybeHead().fromJust());
		assertEquals(info.node, firstExpr.d(Branch.BinaryExpressionRight_()).apply(tree).fromJust());
	}

	public void testCustomPredicate() throws JsError {
		String source = "a + /*$ b $*/ b + /*$ c $*/ c + /*$ d $*/ (d + e);";

		F<String, Maybe<Pair<String, Predicate<Node>>>> commentMatcher = string -> {
			Matcher matcher = Pattern.compile("^\\$ ([^$]+) \\$$").matcher(string);
			if (!matcher.matches()) {
				return Maybe.empty();
			}
			String name = matcher.group(1);
			return Maybe.of(Pair.of(name, node -> node instanceof IdentifierExpression && ((IdentifierExpression) node).name.equals(name)));
		};

		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Program tree = parserWithLocation.parseScript(source);
		ImmutableList<ParserWithLocation.Comment> comments = parserWithLocation.getComments();
		ImmutableList<Template.NodeInfo> result = Template.findNodes(tree, parserWithLocation, comments, commentMatcher);
		assertEquals(3, result.length);
		Template.NodeInfo b = result.index(0).fromJust();
		assertEquals(b.name, "b");
		assertEquals(b.comment, comments.index(0).fromJust());
		assertEquals(b.node, firstExpr.d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionRight_()).apply(tree).fromJust());

		Template.NodeInfo c = result.index(1).fromJust();
		assertEquals(c.name, "c");
		assertEquals(c.comment, comments.index(1).fromJust());
		assertEquals(c.node, firstExpr.d(Branch.BinaryExpressionLeft_()).d(Branch.BinaryExpressionRight_()).apply(tree).fromJust());

		Template.NodeInfo d = result.index(2).fromJust();
		assertEquals(d.name, "d");
		assertEquals(d.comment, comments.index(2).fromJust());
		assertEquals(d.node, firstExpr.d(Branch.BinaryExpressionRight_()).d(Branch.BinaryExpressionLeft_()).apply(tree).fromJust());
	}

	public void testWrongType() throws JsError {
		fails("a + /*# label # IdentifierExpression #*/ 0");
	}

	public void testInterfaceType() throws JsError {
		fails("a + /*# label # SpreadElementExpression #*/ 0");
	}

	public void testNotAType() throws JsError {
		fails("a + /*# label # NotAType #*/ 0");
	}

	public void testNotATypeName() throws JsError {
		fails("a + /*# label # Not A Type #*/ 0");
	}

	public void testTrailingLabel() throws JsError {
		fails("a /*# label #*/");
	}

	public void testAmbiguous() throws JsError {
		fails(" /*# label #*/ a"); // can be either the ExpressionStatement or the Expression
	}

	public void testMissingLocationInfo() throws JsError {
		String source = "a + /*# label #*/ b;";
		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Program tree = parserWithLocation.parseScript(source);

		try {
			Template.findNodes(tree, new ParserWithLocation(), parserWithLocation.getComments());
			throw new RuntimeException("should not have succeeded without location info");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
}
