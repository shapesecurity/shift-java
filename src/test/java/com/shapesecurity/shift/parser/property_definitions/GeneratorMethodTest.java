package com.shapesecurity.shift.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class GeneratorMethodTest extends ParserTestCase {
    @Test
    public void testGeneratorMethod() throws JsError {
        testScript("({*a(){}})", new ObjectExpression(ImmutableList.of(new Method(true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()),
                new StaticPropertyName("a")))));

        testScript("({*yield(){}})", new ObjectExpression(ImmutableList.of(new Method(true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()),
                new StaticPropertyName("yield")))));

        testScript("({*[yield](){}})", new ObjectExpression(ImmutableList.of(new Method(true, new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()),
                new ComputedPropertyName(new IdentifierExpression("yield"))))));
    }
}
