package com.shapesecurity.shift.es2017.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import static com.shapesecurity.shift.es2017.template.Template.findNodes;

public class ApplyTemplateTest extends TestCase {
	void checkSimpleApplication(String source, Node replacement, String expectedSource) throws JsError {
		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("label", node -> replacement);
		checkApplication(source, newNodes, expectedSource);
	}

	void checkApplication(String source, Map<String, F<Node, Node>> newNodes, String expectedSource) throws JsError {
		Program expected = Parser.parseScript(expectedSource);
		Program result = Template.applyTemplate(source, newNodes);
		assertEquals(expected, result);
	}

	void checkFails(String source, Map<String, F<Node, Node>> newNodes) throws JsError {
		try {
			Template.applyTemplate(source, newNodes);
			throw new RuntimeException("Unexpectedly succeeded");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}


	public void testSimple() throws JsError {
		String source = "a + /*# label #*/ b";
		String expected = "a + null";

		checkSimpleApplication(source, new LiteralNullExpression(), expected);
	}

	public void testReusableAPI() throws JsError {
		String source = "a + /*# label #*/ b";

		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Script tree = parserWithLocation.parseScript(source);
		ImmutableList<Template.NodeInfo> namePairs = findNodes(tree, parserWithLocation, parserWithLocation.getComments());

		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("label", node -> new LiteralNumericExpression(1));
		Program result = Template.applyTemplate(tree, namePairs, newNodes);
		assertEquals(Parser.parseScript("a + 1"), result);

		newNodes.put("label", node -> new LiteralNumericExpression(2));
		result = Template.applyTemplate(tree, namePairs, newNodes);
		assertEquals(Parser.parseScript("a + 2"), result);
	}

	public void testReusableAPI2() throws JsError {
		String source = "a + /*# label #*/ b";
		Template builtTemplate = new Template(source);

		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("label", node -> new LiteralNumericExpression(1));
		Program result = Template.applyTemplate(builtTemplate, newNodes);
		assertEquals(Parser.parseScript("a + 1"), result);

		newNodes.put("label", node -> new LiteralNumericExpression(2));
		result = Template.applyTemplate(builtTemplate, newNodes);
		assertEquals(Parser.parseScript("a + 2"), result);
	}

	public void testReusableAPI3() throws JsError {
		String source = "a + /*# label #*/ b";
		Template builtTemplate = new Template(source);

		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("label", node -> new LiteralNumericExpression(1));
		Program result = builtTemplate.apply(newNodes);
		assertEquals(Parser.parseScript("a + 1"), result);

		newNodes.put("label", node -> new LiteralNumericExpression(2));
		result = builtTemplate.apply(newNodes);
		assertEquals(Parser.parseScript("a + 2"), result);
	}


	public void testNodeBased() throws JsError {
		String source = " /*# increment # LiteralNumericExpression #*/ 42 + /*# increment #*/ 128";
		String expected = "43 + 129";
		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("increment", node -> new LiteralNumericExpression(((LiteralNumericExpression) node).value + 1));

		checkApplication(source, newNodes, expected);
	}

	public void testMultipleNames() throws JsError {
		String source = " /*# one # LiteralNumericExpression #*/ /*# two # LiteralNumericExpression #*/ 42";
		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("one", node -> new LiteralNullExpression());
		newNodes.put("two", node -> new LiteralNullExpression());

		checkFails(source, newNodes);
	}

	public void testExtraNames() throws JsError {
		String source = " /*# one # LiteralNumericExpression #*/ /*# two # LiteralNumericExpression #*/ 42";
		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("one", node -> new LiteralNullExpression());
		newNodes.put("two", node -> new LiteralNullExpression());
		newNodes.put("three", node -> new LiteralNullExpression());

		checkFails(source, newNodes);
	}

	public void testMissingNames() throws JsError {
		String source = " /*# one # LiteralNumericExpression #*/ /*# two # LiteralNumericExpression #*/ 42";
		HashMap<String, F<Node, Node>> newNodes = new HashMap<>();
		newNodes.put("one", node -> new LiteralNullExpression());

		checkFails(source, newNodes);
	}
}
