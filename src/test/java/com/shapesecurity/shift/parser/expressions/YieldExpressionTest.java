package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class YieldExpressionTest extends ParserTestCase {
    @Test
    public void testYieldExpression() throws JsError {
        testScript("function*a(){yield\na}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty())), new ExpressionStatement(
                        new IdentifierExpression("a"))))));

        testScript("({set a(yield){}})", new ObjectExpression(ImmutableList.of(new Setter(new BindingIdentifier("yield"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()), new StaticPropertyName("a")))));

        testScript("function *a(){yield 0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield null}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNullExpression())))))));

        testScript("function *a(){yield true}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralBooleanExpression(true))))))));

        testScript("function *a(){yield false}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralBooleanExpression(false))))))));

        testScript("function *a(){yield \"a\"}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralStringExpression("a"))))))));

        testScript("function *a(){yield+0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UnaryExpression(UnaryOperator.Plus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield-0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UnaryExpression(UnaryOperator.Minus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield 2e308}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralInfinityExpression())))))));

        testScript("function *a(){yield(0)}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield /a/}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralRegExpExpression("a", ""))))))));

        testScript("function *a(){yield /=3/}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralRegExpExpression("=3", ""))))))));

        testScript("function *a(){yield class{}}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ClassExpression(Maybe.empty(),
                        Maybe.empty(), ImmutableList.empty()))))))));

        testScript("function *a(){yield ++a}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UpdateExpression(true,
                        UpdateOperator.Increment, new BindingIdentifier("a")))))))));

        testScript("function *a(){yield --a}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UpdateExpression(true,
                        UpdateOperator.Decrement, new BindingIdentifier("a")))))))));
    }
}
