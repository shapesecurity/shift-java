package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.parser.JsError;
import org.junit.Test;

import static com.shapesecurity.shift.es2017.parser.ParserTestCase.testModule;
import static com.shapesecurity.shift.es2017.parser.ParserTestCase.testScript;
import static com.shapesecurity.shift.es2017.parser.ParserTestCase.testScriptFailure;

public class AsyncAwaitTest {

	@Test
	public void testAsyncArrows() throws JsError {
		testScript("async (a, b) => 0", new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.empty()), new LiteralNumericExpression(0.0)));
		testScript("async (a, ...b) => 0", new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.of(new BindingIdentifier("b"))), new LiteralNumericExpression(0.0)));
		testScript("async a => {}", new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
		testScript("async () => {}", new ArrowExpression(true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
		testScript("(async a => {})()", new CallExpression(new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), ImmutableList.empty()));
		testScript("a, async () => b, c", new BinaryExpression(new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.Sequence, new ArrowExpression(true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new IdentifierExpression("b"))), BinaryOperator.Sequence, new IdentifierExpression("c")));
		testScript("async (a = await => {})", new CallExpression(new IdentifierExpression("async"), ImmutableList.of(new AssignmentExpression(new AssignmentTargetIdentifier("a"), new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("await")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));
		testScript("async (a = aw\\u{61}it => {})", new CallExpression(new IdentifierExpression("async"), ImmutableList.of(new AssignmentExpression(new AssignmentTargetIdentifier("a"), new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("await")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));
		testScript("async (a = b => await (0)) => {}", new ArrowExpression(true, new FormalParameters(ImmutableList.of(new BindingWithDefault(
				new BindingIdentifier("a"),
				new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new CallExpression(new IdentifierExpression("await"), ImmutableList.of(new LiteralNumericExpression(0.0))))
		)), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
	}

	@Test
	public void testAsyncFunctions() throws JsError {
		testScript("async function a(){}", new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
		testScript("(async function a(){})", new FunctionExpression(true, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
		testScript("async function a() { function b(c = await (0)) {} }", new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
				new FunctionBody(ImmutableList.empty(), ImmutableList.of(
						new FunctionDeclaration(false, false,
								new BindingIdentifier("b"),
								new FormalParameters(ImmutableList.of(
										new BindingWithDefault(new BindingIdentifier("c"), new CallExpression(new IdentifierExpression("await"), ImmutableList.of(new LiteralNumericExpression(0.0))))
								), Maybe.empty()),
								new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
						)
				)))
		);
		testScript("(function() {var async = 5;return async;})()", new CallExpression(new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(
				ImmutableList.empty(),
				ImmutableList.of(
						new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("async"), Maybe.of(new LiteralNumericExpression(5.0)))))),
						new ReturnStatement(Maybe.of(new IdentifierExpression("async")))
				)
		)), ImmutableList.empty()));
		testScript("(function() {'use strict';var async = 5;return async;})()", new CallExpression(new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(
				ImmutableList.of(new Directive("use strict")),
				ImmutableList.of(
						new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("async"), Maybe.of(new LiteralNumericExpression(5.0)))))),
						new ReturnStatement(Maybe.of(new IdentifierExpression("async")))
				)
		)), ImmutableList.empty()));
	}

	@Test
	public void testAsyncMethods() throws JsError {
		testScript("({ async })", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("async")))));
		testScript("({ async () {} })", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("async"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
		testScript("({ async a(){} })", new ObjectExpression(ImmutableList.of(new Method(true, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
		testScript("({ async get(){} })", new ObjectExpression(ImmutableList.of(new Method(true, false, new StaticPropertyName("get"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
		testScript("(class { async(){} })", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(new ClassElement(false, new Method(false, false, new StaticPropertyName("async"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));
		testScript("(class { async a(){} })", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(new ClassElement(false, new Method(true, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));
		testScript("(class { static async a(){} })", new ClassExpression(Maybe.empty(), Maybe.empty(), ImmutableList.of(new ClassElement(true, new Method(true, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))));
	}

	@Test
	public void testAwait() throws JsError {
		testScript("async function a() { await 0; }", new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new AwaitExpression(new LiteralNumericExpression(0.0)))))));
		testScript("(async function a() { await 0; })", new FunctionExpression(true, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new AwaitExpression(new LiteralNumericExpression(0.0)))))));
		testScript("async () => await 0", new ArrowExpression(true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new AwaitExpression(new LiteralNumericExpression(0.0))));
		testScript("({ async a(){ await 0; } })", new ObjectExpression(ImmutableList.of(new Method(true, false, new StaticPropertyName("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new AwaitExpression(new LiteralNumericExpression(0.0)))))))));
		testScriptFailure("await 0", 6, "Unexpected number");
	}

	@Test
	public void testAsyncExports() throws JsError {
		testModule("export async function a(){}", new Export(new FunctionDeclaration(true, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))));
		testModule("export default async function (){}", new ExportDefault(new FunctionDeclaration(true, false, new BindingIdentifier("*default*"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))));
		testModule("export default async\nfunction a(){}", new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(new IdentifierExpression("async")), new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
	}

	@Test
	public void testAsyncMisc() throws JsError {
		testScript("async;\n(a, b) => 0", new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("async")), new ExpressionStatement(new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.empty()), new LiteralNumericExpression(0.0))))));
		testScript("async\nfunction a(){}", new Script(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("async")), new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
		testScript("new async()", new NewExpression(new IdentifierExpression("async"), ImmutableList.empty()));
		testScript("async()``", new TemplateExpression(Maybe.of(new CallExpression(new IdentifierExpression("async"), ImmutableList.empty())), ImmutableList.of(new TemplateElement(""))));
		testScript("async ((a))", new CallExpression(new IdentifierExpression("async"), ImmutableList.of(new IdentifierExpression("a"))));
		testScript("async function a(){}(0)", new Script(ImmutableList.empty(), ImmutableList.of(new FunctionDeclaration(true, false, new BindingIdentifier("a"),  new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new ExpressionStatement(new LiteralNumericExpression(0.0)))));
		testScript("(async function a(){}(0))", new CallExpression(new FunctionExpression(true, false, Maybe.of(new BindingIdentifier("a")),  new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), ImmutableList.of(new LiteralNumericExpression(0.0))));
	}

	@Test
	public void testAsyncFailure() {
		testScriptFailure("async (a, ...b, ...c) => {}", 14, "Unexpected token \",\"");
		testScriptFailure("async\n(a, b) => {}", 2, 7, 13, "Unexpected token \"=>\"");
		testScriptFailure("new async() => {}", 12, "Unexpected token \"=>\"");
		testScriptFailure("({ async\nf(){} })", 2, 0, 9, "Unexpected identifier");
		testScriptFailure("async ((a)) => {}", 12, "Unexpected token \"=>\"");
		testScriptFailure("({ async get a(){} })", 13, "Unexpected identifier");
		testScriptFailure("async a => {} ()", 14, "Unexpected token \"(\"");
		testScriptFailure("a + async b => {}", 12, "Unexpected token \"=>\"");
		testScriptFailure("a + async () => {}", 13, "Unexpected token \"=>\"");
		testScriptFailure("with({}) async function f(){};", 15, "Unexpected token \"function\"");
		testScriptFailure("function* a(){ async yield => {}; }", 21, "\"yield\" may not be used as an identifier in this context");
		testScriptFailure("function* a(){ async (yield) => {}; }", 29, "Unexpected token \"=>\"");
		testScriptFailure("async function* a(){}", 14, "Unexpected token \"*\"");
		testScriptFailure("(async function* (){})", 15, "Unexpected token \"*\"");
		testScriptFailure("({ async *a(){} })", 9, "Unexpected token \"*\"");
		testScriptFailure("async await => 0", 6, "\"await\" may not be used as an identifier in this context");
		testScriptFailure("async (await) => 0", 7, "Async arrow parameters may not contain \"await\"");
		testScriptFailure("(class { async })", 15, "Only methods are allowed in classes");
		testScriptFailure("(class { async\na(){} })", 2, 0, 15, "Only methods are allowed in classes");
		testScriptFailure("(class { async get a(){} })", 19, "Unexpected identifier");
		testScriptFailure("async (a = await => {}) => {}", 11, "Async arrow parameters may not contain \"await\"");
		testScriptFailure("async (a = (await) => {}) => {}", 12, "Async arrow parameters may not contain \"await\"");
		testScriptFailure("async (a = aw\\u{61}it => {}) => {}", 11, "Async arrow parameters may not contain \"await\"");
		testScriptFailure("async (a = (b = await (0)) => {}) => {}", 16, "Async arrow parameters may not contain \"await\"");
	}
}
