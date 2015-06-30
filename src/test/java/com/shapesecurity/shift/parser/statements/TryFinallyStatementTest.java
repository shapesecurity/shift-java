package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class TryFinallyStatementTest extends Assertions {

  @Test
  public void testTryFinallyStatement() throws JsError {
    testScript("try { } finally { cleanup(stuff) }", new TryFinallyStatement(new Block(ImmutableList.nil()),
        Maybe.nothing(), new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(
        new IdentifierExpression("cleanup"), ImmutableList.list(new IdentifierExpression("stuff"))))))));

    testScript("try{}catch(a){}finally{}", new TryFinallyStatement(new Block(ImmutableList.nil()),
        Maybe.just(new CatchClause(new BindingIdentifier("a"), new Block(ImmutableList.nil()))),
        new Block(ImmutableList.nil())));

    testScript("try { doThat(); } catch (e) { say(e) } finally { cleanup(stuff) }", new TryFinallyStatement(
        new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"),
            ImmutableList.nil())))),
        Maybe.just(new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new ExpressionStatement(
            new CallExpression(new IdentifierExpression("say"), ImmutableList.list(new IdentifierExpression("e")))))))),
        new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("cleanup"),
            ImmutableList.list(new IdentifierExpression("stuff"))))))));
  }
}
