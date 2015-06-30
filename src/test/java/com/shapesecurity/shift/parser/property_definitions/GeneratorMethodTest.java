package com.shapesecurity.shift.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class GeneratorMethodTest extends Assertions {
  @Test
  public void testGeneratorMethod() throws JsError {
    testScript("({*a(){}})", new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(
        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()),
        new StaticPropertyName("a")))));

    testScript("({*yield(){}})", new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(
        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()),
        new StaticPropertyName("yield")))));

    testScript("({*[yield](){}})", new ObjectExpression(ImmutableList.list(new Method(true, new FormalParameters(
        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil()),
        new ComputedPropertyName(new IdentifierExpression("yield"))))));
  }
}
