package com.shapesecurity.shift.es2016.parser.property_definitions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import org.junit.Test;

public class AsyncMethodTest extends ParserTestCase {
    @Test
    public void testAsyncMethod() throws JsError {
        testScript("({async a(){}})", new ObjectExpression(ImmutableList.of(new Method(true, false, new StaticPropertyName("a"), new FormalParameters(
                ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())
                ))));
    }
}
