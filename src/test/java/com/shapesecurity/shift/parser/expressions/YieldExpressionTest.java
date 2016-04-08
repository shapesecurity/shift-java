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
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.nothing())), new ExpressionStatement(
                        new IdentifierExpression("a"))))));

        testScript("({set a(yield){}})", new ObjectExpression(ImmutableList.list(new Setter(new Parameter(new BindingIdentifier("yield"), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil()), new StaticPropertyName("a")))));

        testScript("function *a(){yield 0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield null}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralNullExpression())))))));

        testScript("function *a(){yield true}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralBooleanExpression(true))))))));

        testScript("function *a(){yield false}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralBooleanExpression(false))))))));

        testScript("function *a(){yield \"a\"}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralStringExpression("a"))))))));

        testScript("function *a(){yield+0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new UnaryExpression(UnaryOperator.Plus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield-0}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new UnaryExpression(UnaryOperator.Minus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield 2e308}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralInfinityExpression())))))));

        testScript("function *a(){yield(0)}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield /a/}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralRegExpExpression("a", false, false, false, false, false))))))));

        testScript("function *a(){yield /=3/}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new LiteralRegExpExpression("=3", false, false, false, false, false))))))));

        testScript("function *a(){yield class{}}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new ClassExpression(Maybe.nothing(),
                        Maybe.nothing(), ImmutableList.nil()))))))));

        testScript("function *a(){yield ++a}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new UpdateExpression(true,
                        UpdateOperator.Increment, new BindingIdentifier("a")))))))));

        testScript("function *a(){yield --a}", new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new YieldExpression(Maybe.just(new UpdateExpression(true,
                        UpdateOperator.Decrement, new BindingIdentifier("a")))))))));
    }
}
