package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralInfinityExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.UnaryExpression;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.YieldExpression;
import com.shapesecurity.shift.es2017.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.ClassExpression;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.ObjectExpression;
import com.shapesecurity.shift.es2017.ast.Setter;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class YieldExpressionTest extends ParserTestCase {
    @Test
    public void testYieldExpression() throws JsError {
        testScript("function*a(){yield\na}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.empty())), new ExpressionStatement(
                        new IdentifierExpression("a"))))));

        testScript("({set a(yield){}})", new ObjectExpression(ImmutableList.of(new Setter(new StaticPropertyName("a"), new BindingIdentifier("yield"),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))));

        testScript("function *a(){yield 0}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield null}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNullExpression())))))));

        testScript("function *a(){yield true}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralBooleanExpression(true))))))));

        testScript("function *a(){yield false}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralBooleanExpression(false))))))));

        testScript("function *a(){yield \"a\"}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralStringExpression("a"))))))));

        testScript("function *a(){yield+0}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UnaryExpression(
					UnaryOperator.Plus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield-0}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UnaryExpression(UnaryOperator.Minus,
                        new LiteralNumericExpression(0.0)))))))));

        testScript("function *a(){yield 2e308}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralInfinityExpression())))))));

        testScript("function *a(){yield(0)}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralNumericExpression(0.0))))))));

        testScript("function *a(){yield /a/}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralRegExpExpression("a", false, false, false, false, false))))))));

        testScript("function *a(){yield /=3/}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new LiteralRegExpExpression("=3", false, false, false, false, false))))))));

        testScript("function *a(){yield class{}}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new ClassExpression(Maybe.empty(),
                        Maybe.empty(), ImmutableList.empty()))))))));

        testScript("function *a(){yield ++a}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UpdateExpression(true,
                        UpdateOperator.Increment, new AssignmentTargetIdentifier("a")))))))));

        testScript("function *a(){yield --a}", new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new YieldExpression(Maybe.of(new UpdateExpression(true,
                        UpdateOperator.Decrement, new AssignmentTargetIdentifier("a")))))))));
    }
}
