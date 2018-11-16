package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.CatchClause;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.TryFinallyStatement;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

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
