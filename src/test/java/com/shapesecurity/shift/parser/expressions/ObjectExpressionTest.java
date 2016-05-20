package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ObjectExpressionTest extends ParserTestCase {
    @Test
    public void testObjectExpression() throws JsError {
        testScript("({})", new ObjectExpression(ImmutableList.empty()));

        testScript("+{}", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.empty())));

        testScript("+{ }", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.empty())));

        testScript("({ answer: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(
                new LiteralNumericExpression(0.0), new StaticPropertyName("answer")))));

        testScript("({ if: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0),
                new StaticPropertyName("if")))));

        testScript("({ true: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0),
                new StaticPropertyName("true")))));

        testScript("({ false: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0),
                new StaticPropertyName("false")))));

        testScript("({ null: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0),
                new StaticPropertyName("null")))));

        testScript("({ \"answer\": 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(
                new LiteralNumericExpression(0.0), new StaticPropertyName("answer")))));

        testScript("({ x: 1, x: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(
                new LiteralNumericExpression(1.0), new StaticPropertyName("x")), new DataProperty(
                new LiteralNumericExpression(2.0), new StaticPropertyName("x")))));

        testScript("({ get width() { return m_width } })", new ObjectExpression(ImmutableList.of(new Getter(
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(
                        new IdentifierExpression("m_width"))))), new StaticPropertyName("width")))));

        testScript("({ get undef() {} })", new ObjectExpression(ImmutableList.of(new Getter(
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("undef")))));

        testScript("({ get if() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("if")))));

        testScript("({ get true() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("true")))));

        testScript("({ get false() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("false")))));

        testScript("({ get null() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("null")))));

        testScript("({ get \"undef\"() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("undef")))));

        testScript("({ get 10() {} })", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("10")))));

        testScript("({ set width(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("width")))));

        testScript("({ set if(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("if")))));

        testScript("({ set true(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("true")))));

        testScript("({ set false(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("false")))));

        testScript("({ set null(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("null")))));

        testScript("({ set \"null\"(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("null")))));

        testScript("({ set 10(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w")))),
                new StaticPropertyName("10")))));

        testScript("({ get: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(2.0),
                new StaticPropertyName("get")))));

        testScript("({ set: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(2.0),
                new StaticPropertyName("set")))));

        testScript("({ __proto__: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(2.0),
                new StaticPropertyName("__proto__")))));

        testScript("({ \"__proto__\": 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(2.0),
                new StaticPropertyName("__proto__")))));

        testScript("({ get width() { return width }, set width(width) { return width; } })", new ObjectExpression(
                ImmutableList.of(new Getter(new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new IdentifierExpression("width"))))), new StaticPropertyName("width")), new Setter(
                        new BindingIdentifier("width"), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new IdentifierExpression("width"))))), new StaticPropertyName("width")))));

        testScript("({a:0, get 'b'(){}, set 3(d){}})", new ObjectExpression(ImmutableList.of(new DataProperty(
                new LiteralNumericExpression(0.0), new StaticPropertyName("a")), new Getter(new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()), new StaticPropertyName("b")), new Setter(new BindingIdentifier("d"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("3")))));

        testScript("({a})", new ObjectExpression(ImmutableList.of(new ShorthandProperty("a"))));

        testScript("({let})", new ObjectExpression(ImmutableList.of(new ShorthandProperty("let"))));

        testScript("({yield})", new ObjectExpression(ImmutableList.of(new ShorthandProperty("yield"))));

        testScript("({a, b: 0, c})", new ObjectExpression(ImmutableList.of(new ShorthandProperty("a"), new DataProperty(
                new LiteralNumericExpression(0.0), new StaticPropertyName("b")), new ShorthandProperty("c"))));

        testScript("({a, b})", new ObjectExpression(ImmutableList.of(new ShorthandProperty("a"), new ShorthandProperty("b"))));

        testScript("({a(){}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()),
                new StaticPropertyName("a")))));

        testScript("({a(){let a;}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))))), new StaticPropertyName("a")))));

        testScript("({a(b){}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()), new StaticPropertyName("a")))));

        testScript("({a(b,...c){}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b")), Maybe.of(new BindingIdentifier("c"))), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("a")))));

        testScript("({a(b,c){}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("a")))));

        testScript("({a(b,c){let d;}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("d"),
                Maybe.empty())))))), new StaticPropertyName("a")))));

        testScript("({set a(eval){}})", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("eval"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("a")))));

        testScript("({ set a([{b = 0}]){}, })", new ObjectExpression(ImmutableList.of(new Setter(new ArrayBinding(
                ImmutableList.of(Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(
                        new BindingIdentifier("b"), Maybe.of(new LiteralNumericExpression(0.0))))))), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("a")))));
    }
}
