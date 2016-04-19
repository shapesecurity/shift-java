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
        testScript("({})", new ObjectExpression(ImmutableList.nil()));

        testScript("+{}", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.nil())));

        testScript("+{ }", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.nil())));

        testScript("({ answer: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(
                new StaticPropertyName("answer"), new LiteralNumericExpression(0.0)))));

        testScript("({ if: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("if"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ true: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("true"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ false: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("false"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ null: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("null"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ \"answer\": 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("answer"),
                new LiteralNumericExpression(0.0)))));

        testScript("({ x: 1, x: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("x"),
                new LiteralNumericExpression(1.0)), new DataProperty(new StaticPropertyName("x"),
                new LiteralNumericExpression(2.0)))));

        testScript("({ get width() { return m_width } })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("width"),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(
                        new IdentifierExpression("m_width")))))))));

        testScript("({ get undef() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("undef"),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get if() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("if"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get true() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("true"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get false() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("false"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get null() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("null"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get \"undef\"() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("undef"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ get 10() {} })", new ObjectExpression(ImmutableList.list(new Getter(new StaticPropertyName("10"), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ set width(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("width"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set if(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("if"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set true(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("true"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set false(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("false"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set null(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("null"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set \"null\"(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("null"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set 10(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("10"), new Parameter(new BindingIdentifier("w"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ get: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("get"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ set: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("set"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ __proto__: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("__proto__"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ \"__proto__\": 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("__proto__"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ get width() { return width }, set width(width) { return width; } })", new ObjectExpression(
                ImmutableList.list(new Getter(new StaticPropertyName("width"), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(
                        Maybe.just(new IdentifierExpression("width")))))), new Setter(new StaticPropertyName("width"),
                        new Parameter(new BindingIdentifier("width"), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(
                        Maybe.just(new IdentifierExpression("width")))))))));

        testScript("({a:0, get 'b'(){}, set 3(d){}})", new ObjectExpression(ImmutableList.list(new DataProperty(new StaticPropertyName("a"),
                new LiteralNumericExpression(0.0)), new Getter(new StaticPropertyName("b"), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())), new Setter(new StaticPropertyName("3"), new Parameter(new BindingIdentifier("d"), Maybe.nothing()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({a})", new ObjectExpression(ImmutableList.list(new ShorthandProperty(new IdentifierExpression("a")))));

        testScript("({let})", new ObjectExpression(ImmutableList.list(new ShorthandProperty(new IdentifierExpression("let")))));

        testScript("({yield})", new ObjectExpression(ImmutableList.list(new ShorthandProperty(new IdentifierExpression("yield")))));

        testScript("({a, b: 0, c})", new ObjectExpression(ImmutableList.list(new ShorthandProperty(new IdentifierExpression("a")), new DataProperty(new StaticPropertyName("b"),
                new LiteralNumericExpression(0.0)), new ShorthandProperty(new IdentifierExpression("c")))));

        testScript("({a, b})", new ObjectExpression(ImmutableList.list(new ShorthandProperty(new IdentifierExpression("a")), new ShorthandProperty(new IdentifierExpression("b")))));

        testScript("({a(){}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())
                ))));

        testScript("({a(){let a;}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
                        new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))))))));

        testScript("({a(b){}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.list(new Parameter(new BindingIdentifier("b"), Maybe.nothing())), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.nil())))));

        testScript("({a(b,...c){}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.list(new Parameter(new BindingIdentifier("b"), Maybe.nothing())), Maybe.just(new BindingIdentifier("c"))), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({a(b,c){}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.list(new Parameter(new BindingIdentifier("b"), Maybe.nothing()), new Parameter(new BindingIdentifier("c"), Maybe.nothing())), Maybe.nothing()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({a(b,c){let d;}})", new ObjectExpression(ImmutableList.list(new Method(false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.list(new Parameter(new BindingIdentifier("b"), Maybe.nothing()), new Parameter(new BindingIdentifier("c"), Maybe.nothing())), Maybe.nothing()), new FunctionBody(
                ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("d"),
                Maybe.nothing()))))))))));

        testScript("({set a(eval){}})", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("a"), new Parameter(new BindingIdentifier("eval"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));

        testScript("({ set a([{b = 0}]){}, })", new ObjectExpression(ImmutableList.list(new Setter(new StaticPropertyName("a"), new Parameter(new ArrayBinding(
                ImmutableList.list(Maybe.just(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(
                        new BindingIdentifier("b"), Maybe.just(new LiteralNumericExpression(0.0))))))), Maybe.nothing()), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))));
    }
}
