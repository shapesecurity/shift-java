package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class TryFinallyStatementTest extends ParserTestCase {

    @Test
    public void testTryFinallyStatement() throws JsError {
        testScript("try { } finally { cleanup(stuff) }", new TryFinallyStatement(new Block(ImmutableList.empty()),
                Maybe.empty(), new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(
                new IdentifierExpression("cleanup"), ImmutableList.of(new IdentifierExpression("stuff"))))))));

        testScript("try{}catch(a){}finally{}", new TryFinallyStatement(new Block(ImmutableList.empty()),
                Maybe.of(new CatchClause(new BindingIdentifier("a"), new Block(ImmutableList.empty()))),
                new Block(ImmutableList.empty())));

        testScript("try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }", new TryFinallyStatement(
                new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"),
                        ImmutableList.empty())))),
                Maybe.of(new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("say"), ImmutableList.of(new IdentifierExpression("e")))))))),
                new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("cleanup"),
                        ImmutableList.of(new IdentifierExpression("stuff"))))))));
    }
}
