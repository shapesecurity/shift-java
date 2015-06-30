package com.shapesecurity.shift.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class PropertyNameTest extends Assertions {
  @Test
  public void testPropertyName() throws JsError {
    testScript("({0x0:0})", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("0")))));
    testScript("({2e308:0})", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("Infinity")))));
    testScript("({get b() {}})", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("b")))));
    testScript("({set c(x) {}})", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("c")))));
    testScript("({__proto__:0})", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0), new StaticPropertyName("__proto__")))));
    testScript("({get __proto__() {}})", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("__proto__")))));
    testScript("({set __proto__(x) {}})", new ObjectExpression(ImmutableList.list(new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("__proto__")))));
    testScript("({get __proto__() {}, set __proto__(x) {}})", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("__proto__")), new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("__proto__")))));
    testScript("({[\"nUmBeR\"+9]:\"nein\"})", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralStringExpression("\"nein\""), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralStringExpression("\"nUmBeR\""), new LiteralNumericExpression(9.0)))))));
    testScript("({[2*308]:0})", new ObjectExpression(ImmutableList.list(new DataProperty(new LiteralNumericExpression(0.0), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Mul, new LiteralNumericExpression(2.0), new LiteralNumericExpression(308.0)))))));
    testScript("({get [6+3]() {}, set [5/4](x) {}})", new ObjectExpression(ImmutableList.list(new Getter(new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(6.0), new LiteralNumericExpression(3.0)))), new Setter(new BindingIdentifier("x"), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Div, new LiteralNumericExpression(5.0), new LiteralNumericExpression(4.0)))))));
    testScript("({[6+3]() {}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new ComputedPropertyName(new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(6.0), new LiteralNumericExpression(3.0)))))));
    testScript("({3() {}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("3")))));
    testScript("({\"moo\"() {}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("\"moo\"")))));
    testScript("({\"oink\"(that, little, piggy) {}})", new ObjectExpression(ImmutableList.list(new Method(false, new FormalParameters(ImmutableList.list(new BindingIdentifier("that"), new BindingIdentifier("little"), new BindingIdentifier("piggy")), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("\"oink\"")))));

    testScriptFailure("({[1,2]:3})", 4, "Unexpected token \",\"");
    testScriptFailure("({ *a })", 6, "Unexpected token \"*\""); // TODO: changed error from unexpected }
    testScriptFailure("({ *a: 0 })", 5, "Unexpected token \":\"");
    testScriptFailure("({ *[0]: 0 })", 7, "Unexpected token \":\"");
  }
}
