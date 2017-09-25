package com.shapesecurity.shift.es2016.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import org.junit.Test;

public class AwaitExpressionTest extends ParserTestCase {
    @Test
    public void testAwaitExpression() throws JsError {
        testScript("async function a(){await a}", new FunctionDeclaration(true, false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AwaitExpression(new IdentifierExpression("a")))))));

        testScriptFailure("async function a(){await}", 2, 0, 20, "Unexpected token \"}\"");
    }
}
