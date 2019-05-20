package com.shapesecurity.shift.es2018.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.BindingIdentifier;
import com.shapesecurity.shift.es2018.ast.BindingWithDefault;
import com.shapesecurity.shift.es2018.ast.ComputedPropertyName;
import com.shapesecurity.shift.es2018.ast.ExpressionStatement;
import com.shapesecurity.shift.es2018.ast.FormalParameters;
import com.shapesecurity.shift.es2018.ast.FunctionBody;
import com.shapesecurity.shift.es2018.ast.IdentifierExpression;
import com.shapesecurity.shift.es2018.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2018.ast.Method;
import com.shapesecurity.shift.es2018.ast.ObjectExpression;
import com.shapesecurity.shift.es2018.ast.StaticPropertyName;
import com.shapesecurity.shift.es2018.ast.YieldExpression;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

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

        testScript("({async *a(b = ({*x(){}})){yield 5;}})", new ObjectExpression(ImmutableList.of(new Method(true, true, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.of(new BindingWithDefault(new BindingIdentifier("b"),
                        new ObjectExpression(ImmutableList.of(new Method(false, true, new StaticPropertyName("x"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))
                )), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNumericExpression(5.0))))))
        ))));
    }
}
