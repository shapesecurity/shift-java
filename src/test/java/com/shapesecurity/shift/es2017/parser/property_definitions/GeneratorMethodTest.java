package com.shapesecurity.shift.es2017.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ComputedPropertyName;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class GeneratorMethodTest extends ParserTestCase {
    @Test
    public void testGeneratorMethod() throws JsError {
        testScript("({*a(){}})", new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                ))));

        testScript("({*yield(){}})", new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("yield"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                ))));

        testScript("({*[yield](){}})", new ObjectExpression(ImmutableList.of(new Method(false, true, new ComputedPropertyName(new IdentifierExpression("yield")), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                ))));
    }
}
