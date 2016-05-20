package com.shapesecurity.shift.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class PropertyNameTest extends ParserTestCase {
    @Test
    public void testPropertyName() throws JsError {
        testScript("({0x0:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("0")))));
        testScript("({2e308:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("Infinity")))));
        testScript("({get b() {}})", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("b")))));
        testScript("({set c(x) {}})", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("c")))));
        testScript("({__proto__:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("__proto__")))));
        testScript("({get __proto__() {}})", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("__proto__")))));
        testScript("({set __proto__(x) {}})", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("__proto__")))));
        testScript("({get __proto__() {}, set __proto__(x) {}})", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("__proto__")), new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("__proto__")))));
        testScript("({[\"nUmBeR\"+9]:\"nein\"})", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralStringExpression("nein"), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralStringExpression("nUmBeR"), new LiteralNumericExpression(9.0)))))));
        testScript("({[2*308]:0})", new ObjectExpression(ImmutableList.of(new DataProperty(new LiteralNumericExpression(0.0), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Mul, new LiteralNumericExpression(2.0), new LiteralNumericExpression(308.0)))))));
        testScript("({get [6+3]() {}, set [5/4](x) {}})", new ObjectExpression(ImmutableList.of(new Getter(new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(6.0), new LiteralNumericExpression(3.0)))), new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Div, new LiteralNumericExpression(5.0), new LiteralNumericExpression(4.0)))))));
        testScript("({[6+3]() {}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(6.0), new LiteralNumericExpression(3.0)))))));
        testScript("({3() {}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("3")))));
        testScript("({\"moo\"() {}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("moo")))));
        testScript("({\"oink\"(that, little, piggy) {}})", new ObjectExpression(ImmutableList.of(new Method(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("that"), new BindingIdentifier("little"), new BindingIdentifier("piggy")), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("oink")))));

        testScriptFailure("({[1,2]:3})", 4, "Unexpected token \",\"");
        testScriptFailure("({ *a })", 6, "Unexpected token \"*\""); // TODO: changed error from unexpected }
        testScriptFailure("({ *a: 0 })", 5, "Unexpected token \":\"");
        testScriptFailure("({ *[0]: 0 })", 7, "Unexpected token \":\"");
    }
}
