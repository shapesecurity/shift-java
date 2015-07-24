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
        new LiteralNumericExpression(0.0), new StaticPropertyName("answer")))));

    testScript("({ if: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0),
        new StaticPropertyName("if")))));

    testScript("({ true: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0),
        new StaticPropertyName("true")))));

    testScript("({ false: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0),
        new StaticPropertyName("false")))));

    testScript("({ null: 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0),
        new StaticPropertyName("null")))));

    testScript("({ \"answer\": 0 })", new ObjectExpression(ImmutableList.list(new DataProperty(
        new LiteralNumericExpression(0.0), new StaticPropertyName("answer")))));

    testScript("({ x: 1, x: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(
        new LiteralNumericExpression(1.0), new StaticPropertyName("x")), new DataProperty(
        new LiteralNumericExpression(2.0), new StaticPropertyName("x")))));

    testScript("({ get width() { return m_width } })", new ObjectExpression(ImmutableList.list(new Getter(
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.just(
            new IdentifierExpression("m_width"))))), new StaticPropertyName("width")))));

    testScript("({ get undef() {} })", new ObjectExpression(ImmutableList.list(new Getter(
        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("undef")))));

    testScript("({ get if() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("if")))));

    testScript("({ get true() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("true")))));

    testScript("({ get false() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("false")))));

    testScript("({ get null() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("null")))));

    testScript("({ get \"undef\"() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("undef")))));

    testScript("({ get 10() {} })", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()) ,new StaticPropertyName("10")))));

    testScript("({ set width(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("width")))));

    testScript("({ set if(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("if")))));

    testScript("({ set true(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("true")))));

    testScript("({ set false(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("false")))));

    testScript("({ set null(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("null")))));

    testScript("({ set \"null\"(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("null")))));

    testScript("({ set 10(w) { w } })", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("w"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ExpressionStatement(new IdentifierExpression("w")))),
        new StaticPropertyName("10")))));

    testScript("({ get: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(2.0),
        new StaticPropertyName("get")))));

    testScript("({ set: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(2.0),
        new StaticPropertyName("set")))));

    testScript("({ __proto__: 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(2.0),
        new StaticPropertyName("__proto__")))));

    testScript("({ \"__proto__\": 2 })", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(2.0),
        new StaticPropertyName("__proto__")))));

    testScript("({ get width() { return width }, set width(width) { return width; } })", new ObjectExpression(
        ImmutableList.list(new Getter(new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(
            Maybe.just(new IdentifierExpression("width"))))), new StaticPropertyName("width")), new Setter(
            new BindingIdentifier("width"), new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(
            Maybe.just(new IdentifierExpression("width"))))), new StaticPropertyName("width")))));

    testScript("({a:0, get 'b'(){}, set 3(d){}})", new ObjectExpression(ImmutableList.list(new DataProperty(
        new LiteralNumericExpression(0.0), new StaticPropertyName("a")), new Getter(new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil()), new StaticPropertyName("b")), new Setter(new BindingIdentifier("d"), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("3")))));

    testScript("({a})", new ObjectExpression(ImmutableList.list(new ShorthandProperty("a"))));

    testScript("({let})", new ObjectExpression(ImmutableList.list(new ShorthandProperty("let"))));

    testScript("({yield})", new ObjectExpression(ImmutableList.list(new ShorthandProperty("yield"))));

    testScript("({a, b: 0, c})", new ObjectExpression(ImmutableList.list(new ShorthandProperty("a"), new DataProperty(
        new LiteralNumericExpression(0.0), new StaticPropertyName("b")), new ShorthandProperty("c"))));

    testScript("({a, b})", new ObjectExpression(ImmutableList.list(new ShorthandProperty("a"), new ShorthandProperty("b"))));

    testScript("({a(){}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()),
        new StaticPropertyName("a")))));

    testScript("({a(){let a;}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing())))))), new StaticPropertyName("a")))));

    testScript("({a(b){}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.list(new BindingIdentifier("b")), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
        ImmutableList.nil()), new StaticPropertyName("a")))));

    testScript("({a(b,...c){}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.list(new BindingIdentifier("b")), Maybe.just(new BindingIdentifier("c"))), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("a")))));

    testScript("({a(b,c){}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.list(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.nothing()), new FunctionBody(
        ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("a")))));

    testScript("({a(b,c){let d;}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(
        ImmutableList.list(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.nothing()), new FunctionBody(
        ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(
        VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("d"),
        Maybe.nothing())))))), new StaticPropertyName("a")))));

    testScript("({set a(eval){}})", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("eval"),
        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("a")))));

    testScript("({ set a([{b = 0}]){}, })", new ObjectExpression(ImmutableList.list(new Setter(new ArrayBinding(
        ImmutableList.list(Maybe.just(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(
            new BindingIdentifier("b"), Maybe.just(new LiteralNumericExpression(0.0))))))), Maybe.nothing()),
        new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("a")))));
  }
}
