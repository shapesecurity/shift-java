package com.shapesecurity.shift.es2017.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.DataProperty;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.Getter;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.ComputedPropertyName;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.Setter;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class PropertyNameTest extends ParserTestCase {
    @Test
    public void testPropertyName() throws JsError {
        testScript("({0x0:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("0"), new LiteralNumericExpression(0.0)))));
        testScript("({2e308:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("Infinity"), new LiteralNumericExpression(0.0)))));
        testScript("({get b() {}})", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("b"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({set c(x) {}})", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("c"), new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({__proto__:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("__proto__"), new LiteralNumericExpression(0.0)))));
        testScript("({get __proto__() {}})", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("__proto__"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({set __proto__(x) {}})", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("__proto__"), new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({get __proto__() {}, set __proto__(x) {}})", new ObjectExpression(ImmutableList.of(new Getter(new StaticPropertyName("__proto__"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new Setter(new StaticPropertyName("__proto__"), new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({[\"nUmBeR\"+9]:\"nein\"})", new ObjectExpression(ImmutableList.of(new DataProperty(new ComputedPropertyName(new BinaryExpression(new LiteralStringExpression("nUmBeR"), BinaryOperator.Plus, new LiteralNumericExpression(9.0))), new LiteralStringExpression("nein")))));
        testScript("({[2*308]:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new ComputedPropertyName(new BinaryExpression(new LiteralNumericExpression(2.0), BinaryOperator.Mul, new LiteralNumericExpression(308.0))), new LiteralNumericExpression(0.0)))));
        testScript("({get [6+3]() {}, set [5/4](x) {}})", new ObjectExpression(ImmutableList.of(new Getter(new ComputedPropertyName(new BinaryExpression(new LiteralNumericExpression(6.0), BinaryOperator.Plus, new LiteralNumericExpression(3.0))), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())), new Setter(new ComputedPropertyName(new BinaryExpression(new LiteralNumericExpression(5.0), BinaryOperator.Div, new LiteralNumericExpression(4.0))), new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({[6+3]() {}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new ComputedPropertyName(new BinaryExpression(new LiteralNumericExpression(6.0), BinaryOperator.Plus, new LiteralNumericExpression(3.0))), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({3() {}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("3"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({\"moo\"() {}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("moo"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));
        testScript("({\"oink\"(that, little, piggy) {}})", new ObjectExpression(ImmutableList.of(new Method(false, false, new StaticPropertyName("oink"), new FormalParameters(ImmutableList.of(new BindingIdentifier("that"), new BindingIdentifier("little"), new BindingIdentifier("piggy")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScriptFailure("({[1,2]:3})", 4, "Unexpected token \",\"");
        testScriptFailure("({ *a })", 6, "Unexpected token \"*\""); // TODO: changed error from unexpected }
        testScriptFailure("({ *a: 0 })", 5, "Unexpected token \":\"");
        testScriptFailure("({ *[0]: 0 })", 7, "Unexpected token \":\"");
    }
}
