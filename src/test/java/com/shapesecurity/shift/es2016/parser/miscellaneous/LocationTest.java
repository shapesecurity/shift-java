package com.shapesecurity.shift.es2016.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.ArrayExpression;
import com.shapesecurity.shift.es2016.ast.ArrowExpression;
import com.shapesecurity.shift.es2016.ast.BinaryExpression;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.CallExpression;
import com.shapesecurity.shift.es2016.ast.ExportDeclaration;
import com.shapesecurity.shift.es2016.ast.ExportDefault;
import com.shapesecurity.shift.es2016.ast.Expression;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.ExpressionSuper;
import com.shapesecurity.shift.es2016.ast.ExpressionTemplateElement;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.SpreadElementExpression;
import com.shapesecurity.shift.es2016.ast.Statement;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.ast.TemplateElement;
import com.shapesecurity.shift.es2016.ast.TemplateExpression;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import junit.framework.TestCase;
import javax.annotation.Nonnull;
import org.junit.Test;

public class LocationTest extends TestCase {
	private String source;

	private ParserWithLocation parserWithLocation;

	private Module tree;

	private void init(@Nonnull String source) throws JsError {
		this.source = source;
		this.parserWithLocation = new ParserWithLocation();
		this.tree = this.parserWithLocation.parseModule(this.source);
	}

	private void checkText(@Nonnull Node node, @Nonnull String expected) {
		Maybe<SourceSpan> maybeLocation = this.parserWithLocation.getLocation(node);
		assertTrue(maybeLocation.isJust());

		SourceSpan location = maybeLocation.fromJust();

		String actual = this.source.substring(location.start.offset, location.end.offset);

		assertEquals(expected, actual);
	}

	private void checkLocation(@Nonnull Node node, @Nonnull SourceSpan expected) {
		Maybe<SourceSpan> maybeLocation = this.parserWithLocation.getLocation(node);
		assertTrue(maybeLocation.isJust());

		SourceSpan location = maybeLocation.fromJust();
		// Manually checking equality gives better error messages.
		assertEquals(expected.start.offset, location.start.offset);
		assertEquals(expected.start.line, location.start.line);
		assertEquals(expected.start.column, location.start.column);
		assertEquals(expected.end.offset, location.end.offset);
		assertEquals(expected.end.line, location.end.line);
		assertEquals(expected.end.column, location.end.column);
	}

	@Test
	public void testSimple() throws JsError {
		init(" a  + 1.  .b ;   ");

		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
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

	@Test
	public void testSimpleTemplate() throws JsError {
		init("`foo`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkText(statement, "`foo`;");

		Expression expression = ((ExpressionStatement) statement).expression;
		checkText(expression, "`foo`");

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.maybeHead().fromJust();
		checkText(element, "foo");
	}

	@Test
	public void testComplexTemplate() throws JsError {
		init("`foo ${ 0 } bar ${ 1 } baz`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkText(statement, "`foo ${ 0 } bar ${ 1 } baz`;");

		Expression expression = ((ExpressionStatement) statement).expression;
		checkText(expression, "`foo ${ 0 } bar ${ 1 } baz`");

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkText(element, "foo ");

		element = ((TemplateExpression) expression).elements.index(1).fromJust();
		checkText(element, "0");

		element = ((TemplateExpression) expression).elements.index(2).fromJust();
		checkText(element, " bar ");

		element = ((TemplateExpression) expression).elements.index(3).fromJust();
		checkText(element, "1");

		element = ((TemplateExpression) expression).elements.index(4).fromJust();
		checkText(element, " baz");
	}

	@Test
	public void testTemplateSimpleLinebreak() throws JsError {
		init("`a\nb`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkLocation(statement, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(1, 3, 6)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(1, 2, 5)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 1, 1),
				new SourceLocation(1, 1, 4)
		));
	}

	@Test
	public void testTemplateWindowsLinebreak() throws JsError {
		init("`a\r\nb`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkLocation(statement, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(1, 3, 7)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(1, 2, 6)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 1, 1),
				new SourceLocation(1, 1, 5)
		));
	}

	@Test
	public void testTemplateMultiLinebreak() throws JsError {
		init("`a\n\r\u2028\u2029b`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkLocation(statement, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(4, 3, 9)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 0, 0),
				new SourceLocation(4, 2, 8)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 1, 1),
				new SourceLocation(4, 1, 7)
		));
	}

	@Test
	public void testArrow() throws JsError {
		init("(a,b)=>{}");

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		Expression expression = ((ExpressionStatement) statement).expression;
		FormalParameters params = ((ArrowExpression) expression).params;
		checkText(params, "a,b");

		FunctionBody body = (FunctionBody) ((ArrowExpression) expression).body;
		checkLocation(body, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 8, 8),
				new SourceLocation(0, 8, 8) // i.e. not including the braces.
		));
	}

	@Test
	public void testGroup() throws JsError {
		init("(a,b)");

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		Expression expression = ((ExpressionStatement) statement).expression;
		checkText(expression, "a,b");
	}

	@Test
	public void testSpread() throws JsError {
		init("f(...a);[...b]");

		Statement statement = (Statement) this.tree.items.index(0).fromJust();
		Expression expression = ((ExpressionStatement) statement).expression;
		ImmutableList<SpreadElementExpression> args = ((CallExpression) expression).arguments;
		checkText(args.maybeHead().fromJust(), "...a");

		statement = (Statement) this.tree.items.index(1).fromJust();
		expression = ((ExpressionStatement) statement).expression;
		ImmutableList<Maybe<SpreadElementExpression>> elements = ((ArrayExpression) expression).elements;
		checkText(elements.maybeHead().fromJust().fromJust(), "...b");
	}

	@Test
	public void testExportDefaultBindingIdentifier() throws JsError {
		init("export default function(\n){\n}");

		ExportDefault exportDefault = (ExportDefault) this.tree.items.index(0).fromJust();
		checkText(exportDefault, "export default function(\n){\n}");

		FunctionDeclaration functionDeclaration = (FunctionDeclaration) exportDefault.body;
		checkText(functionDeclaration, "function(\n){\n}");

		BindingIdentifier name = functionDeclaration.name;
		assertTrue(this.parserWithLocation.getLocation(name).isNothing());

		FormalParameters params = functionDeclaration.params;
		checkLocation(params, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(0, 24, 24),
				new SourceLocation(0, 24, 24) // i.e. immediately after the opening parenthesis, not including any whitespace.
		));

		FunctionBody body = functionDeclaration.body;
		checkLocation(body, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 2, 27),
				new SourceLocation(1, 2, 27) // i.e. immediately after the brace, not including any whitespace.
		));
	}
}
