package com.shapesecurity.shift.es2016.parser.miscellaneous;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.BinaryExpression;
import com.shapesecurity.shift.es2016.ast.Expression;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.ExpressionSuper;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.Statement;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class LocationTest extends TestCase  {
	private String source;

	private ParserWithLocation parserWithLocation;

	private Script tree;

	private void init(@NotNull String source) throws JsError {
		this.source = source;
		this.parserWithLocation = new ParserWithLocation();
		this.tree = this.parserWithLocation.parseScript(this.source);
	}

	private void checkText(@NotNull Node node, @NotNull String expected) {
		Maybe<SourceSpan> maybeLocation = this.parserWithLocation.getLocation(node);
		assertTrue(maybeLocation.isJust());

		SourceSpan location = maybeLocation.fromJust();

		String actual = this.source.substring(location.start.offset, location.end.offset);

		assertEquals(expected, actual);
	}

	@Test
	public void testSimple() throws JsError {
		init(" a  + 1.  .b ;   ");

		checkText(this.tree, this.source);

		Statement statement = this.tree.statements.maybeHead().fromJust();
		checkText(statement, "a  + 1.  .b ;");

		Expression expression = ((ExpressionStatement) statement).expression;
		checkText(expression, "a  + 1.  .b");

		Expression left = ((BinaryExpression) expression).left;
		checkText(left, "a");

		Expression right = ((BinaryExpression) expression).right;
		checkText(right, "1.  .b");

		ExpressionSuper object = ((StaticMemberExpression) right).object;
		checkText(object, "1.");
	}

}
