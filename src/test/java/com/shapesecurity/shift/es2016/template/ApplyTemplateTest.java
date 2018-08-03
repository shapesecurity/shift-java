package com.shapesecurity.shift.es2016.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.shift.es2016.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Parser;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

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
