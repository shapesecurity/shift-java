package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingPropertyIdentifier;
import com.shapesecurity.shift.es2017.ast.DataProperty;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.Getter;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.ObjectBinding;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.ReturnStatement;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.UnaryExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2017.ast.Setter;
import com.shapesecurity.shift.es2017.ast.ShorthandProperty;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ObjectExpressionTest extends ParserTestCase {
    @Test
    public void testObjectExpression() throws JsError {
        testScript("({})", new ObjectExpression(ImmutableList.empty()));

        testScript("+{}", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.empty())));

        testScript("+{ }", new UnaryExpression(UnaryOperator.Plus, new ObjectExpression(ImmutableList.empty())));

        testScript("({ answer: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(
                new StaticPropertyName("answer"), new LiteralNumericExpression(0.0)))));

        testScript("({ if: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("if"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ true: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("true"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ false: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("false"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ null: 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("null"), new LiteralNumericExpression(0.0)
                ))));

        testScript("({ \"answer\": 0 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("answer"),
                new LiteralNumericExpression(0.0)))));

        testScript("({ x: 1, x: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("x"),
                new LiteralNumericExpression(1.0)), new DataProperty(new StaticPropertyName("x"),
                new LiteralNumericExpression(2.0)))));

        testScript("({ get width() { return m_width } })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("width"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.of(
                        new IdentifierExpression("m_width")))))))));

        testScript("({ get undef() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("undef"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get if() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("if"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get true() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("true"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get false() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("false"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get null() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("null"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get \"undef\"() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("undef"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ get 10() {} })", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("10"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ set width(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("width"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set if(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("if"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set true(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("true"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set false(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("false"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set null(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("null"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set \"null\"(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("null"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ set 10(w) { w } })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("10"), new BindingIdentifier("w"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new IdentifierExpression("w"))))
                ))));

        testScript("({ get: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("get"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ set: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("set"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ __proto__: 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("__proto__"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ \"__proto__\": 2 })", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("__proto__"), new LiteralNumericExpression(2.0)
                ))));

        testScript("({ get width() { return width }, set width(width) { return width; } })", new ObjectExpression(
                ImmutableList.of(new Getter(new StaticPropertyName("width"), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new IdentifierExpression("width")))))), new Setter(new StaticPropertyName("width"),
                        new BindingIdentifier("width"), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(
                        Maybe.of(new IdentifierExpression("width")))))))));

        testScript("({a:0, get 'b'(){}, set 3(d){}})", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("a"),
                new LiteralNumericExpression(0.0)), new Getter(new StaticPropertyName("b"), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())), new Setter(new StaticPropertyName("3"), new BindingIdentifier("d"), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({a})", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("a")))));

        testScript("({let})", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("let")))));

        testScript("({yield})", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("yield")))));

        testScript("({a, b: 0, c})", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("a")), new DataProperty(new StaticPropertyName("b"),
                new LiteralNumericExpression(0.0)), new ShorthandProperty(new IdentifierExpression("c")))));

        testScript("({a, b})", new ObjectExpression(ImmutableList.of(new ShorthandProperty(new IdentifierExpression("a")), new ShorthandProperty(new IdentifierExpression("b")))));

        testScript("({a(){}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                ))));

        testScript("({a(){let a;}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))))))));

        testScript("({a(b){}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty())))));

        testScript("({a(b,...c){}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b")), Maybe.of(new BindingIdentifier("c"))), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({a(b,c){}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({a(b,c){let d;}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.of(new BindingIdentifier("b"), new BindingIdentifier("c")), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("d"),
                Maybe.empty()))))))))));

        testScript("({set a(eval){}})", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("a"), new BindingIdentifier("eval"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("({ set a([{b = 0}]){}, })", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("a"), new ArrayBinding(
                ImmutableList.of(Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(
                        new BindingIdentifier("b"), Maybe.of(new LiteralNumericExpression(0.0))))))), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
    }
}
