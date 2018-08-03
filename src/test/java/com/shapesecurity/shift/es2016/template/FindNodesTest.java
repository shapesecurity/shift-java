package com.shapesecurity.shift.es2016.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.path.Branch;
import com.shapesecurity.shift.es2016.path.BranchGetter;
import junit.framework.TestCase;

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

	public void testWrongType() throws JsError {
		fails("a + /*# label # IdentifierExpression #*/ 0");
	}

	public void testNotAType() throws JsError {
		fails("a + /*# label # NotAType #*/ 0");
	}

	public void testTrailingLabel() throws JsError {
		fails("a /*# label #*/");
	}
}
