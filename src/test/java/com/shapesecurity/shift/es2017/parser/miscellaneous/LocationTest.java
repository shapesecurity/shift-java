package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import com.shapesecurity.shift.es2017.parser.SourceLocation;
import com.shapesecurity.shift.es2017.parser.SourceSpan;
import com.shapesecurity.shift.es2017.ast.Module;
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
				new SourceLocation(1, 0, 0),
				new SourceLocation(2, 3, 6)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 0, 0),
				new SourceLocation(2, 2, 5)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 1, 1),
				new SourceLocation(2, 1, 4)
		));
	}

	@Test
	public void testTemplateWindowsLinebreak() throws JsError {
		init("`a\r\nb`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkLocation(statement, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 0, 0),
				new SourceLocation(2, 3, 7)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 0, 0),
				new SourceLocation(2, 2, 6)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 1, 1),
				new SourceLocation(2, 1, 5)
		));
	}

	@Test
	public void testTemplateMultiLinebreak() throws JsError {
		init("`a\n\r\u2028\u2029b`;");
		checkText(this.tree, this.source);

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		checkLocation(statement, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 0, 0),
				new SourceLocation(5, 3, 9)
		));

		Expression expression = ((ExpressionStatement) statement).expression;
		checkLocation(expression, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 0, 0),
				new SourceLocation(5, 2, 8)
		));

		ExpressionTemplateElement element = ((TemplateExpression) expression).elements.index(0).fromJust();
		checkLocation(element, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 1, 1),
				new SourceLocation(5, 1, 7)
		));
	}

	@Test
	public void testArrow() throws JsError {
		init("(a,b)=>{}");

		Statement statement = (Statement) this.tree.items.maybeHead().fromJust();
		Expression expression = ((ExpressionStatement) statement).expression;
		FormalParameters params = ((ArrowExpression) expression).params;
		checkText(params, "(a,b)");

		FunctionBody body = (FunctionBody) ((ArrowExpression) expression).body;
		checkLocation(body, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(1, 7, 7),
				new SourceLocation(1, 9, 9) // i.e. including the braces.
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
				new SourceLocation(1, 23, 23),
				new SourceLocation(2, 1, 26) // i.e. around the parentheses, including internal whitespace.
		));

		FunctionBody body = functionDeclaration.body;
		checkLocation(body, new SourceSpan(
				Maybe.empty(),
				new SourceLocation(2, 1, 26),
				new SourceLocation(3, 1, 29) // i.e. around the braces, including internal whitespace.
		));
	}

	@Test
	public void testStatic() throws JsError {
		init("  class A {  static  method  ()   {} }  ");

		ClassDeclaration classDeclaration = (ClassDeclaration) this.tree.items.maybeHead().fromJust();
		ClassElement element = classDeclaration.elements.maybeHead().fromJust();
		checkText(element, "static  method  ()   {}");

		MethodDefinition method = element.method;
		checkText(method, "method  ()   {}");
	}

	@Test
	public void testAsync() throws JsError {
		init(" async a => 0 ");
		ArrowExpression arrowExpression = (ArrowExpression) ((ExpressionStatement) this.tree.items.maybeHead().fromJust()).expression;
		checkText(arrowExpression, "async a => 0");
		checkText(arrowExpression.params, "a");

		init(" async (a) => 0 ");
		arrowExpression = (ArrowExpression) ((ExpressionStatement) this.tree.items.maybeHead().fromJust()).expression;
		checkText(arrowExpression, "async (a) => 0");
		checkText(arrowExpression.params, "(a)");

		init(" async function f() {} ; ");
		FunctionDeclaration functionDeclaration = ((FunctionDeclaration) this.tree.items.maybeHead().fromJust());
		checkText(functionDeclaration, "async function f() {}");

		init(" (async function f() {}) ; ");
		FunctionExpression functionExpression = (FunctionExpression) ((ExpressionStatement) this.tree.items.maybeHead().fromJust()).expression;
		checkText(functionExpression, "async function f() {}");

		init(" export async function f() {} ; ");
		functionDeclaration = (FunctionDeclaration) ((Export) this.tree.items.maybeHead().fromJust()).declaration;
		checkText(functionDeclaration, "async function f() {}");

		init(" export default async function f() {} ; ");
		functionDeclaration = (FunctionDeclaration) ((ExportDefault) this.tree.items.maybeHead().fromJust()).body;
		checkText(functionDeclaration, "async function f() {}");

		init(" class A { async m () {} } ");
		ClassDeclaration classDeclaration = ((ClassDeclaration) this.tree.items.maybeHead().fromJust());
		ClassElement element = classDeclaration.elements.maybeHead().fromJust();
		Method method = (Method) element.method;
		checkText(element, "async m () {}");
		checkText(method, "async m () {}");

		init(" class A { static async m () {} } ");
		classDeclaration = ((ClassDeclaration) this.tree.items.maybeHead().fromJust());
		element = classDeclaration.elements.maybeHead().fromJust();
		method = (Method) element.method;
		checkText(element, "static async m () {}");
		checkText(method, "async m () {}");

		init(" ({ async m () {} }) ");
		method = (Method)  ((ObjectExpression) ((ExpressionStatement) this.tree.items.maybeHead().fromJust()).expression).properties.maybeHead().fromJust();
		checkText(method, "async m () {}");
	}
}
