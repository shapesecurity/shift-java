package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/15/15.
 */
public class IdentifierExpressionTest extends Assertions {
  @Test
  public void testIdentifierExpression() throws JsError {
   testScript("x", new IdentifierExpression("x"));

   testScript("x;", new IdentifierExpression("x"));

   testScript("await", new IdentifierExpression("await"));

   testScript("let", new IdentifierExpression("let"));

   testScript("let()", new CallExpression(new IdentifierExpression("let"), ImmutableList.nil()));

//   testScript("let[let]", new ComputedMemberExpression(new IdentifierExpression("let"), new IdentifierExpression("let")));

   testScript("let.let", new StaticMemberExpression("let", new IdentifierExpression("let")));

   testScript("for(let;;);", new ForStatement(Maybe.just(new IdentifierExpression("let")), Maybe.nothing(), Maybe.nothing(), new EmptyStatement()));

   testScript("for(let();;);", new ForStatement(Maybe.just(new CallExpression(new IdentifierExpression("let"),
       ImmutableList.nil())), Maybe.nothing(), Maybe.nothing(), new EmptyStatement()));

//    testScript("for(let yield in 0);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("yield"), Maybe.nothing()))),
//        new LiteralNumericExpression(0.0), new EmptyStatement()));

    testScript("for(let.let in 0);", new ForInStatement(new StaticMemberExpression("let", new IdentifierExpression("let")),
        new LiteralNumericExpression(0.0), new EmptyStatement()));

    testScript("日本語", new IdentifierExpression("日本語"));

    testScript("\uD800\uDC00", new IdentifierExpression("\uD800\uDC00"));

//    testScript("\\u203F", new IdentifierExpression("\\u203F"));
//
//    testScript("T\\u200C", new IdentifierExpression("T\\u200C"));

//    testScript("T\\u200D", new IdentifierExpression("T\\u200D"));

//    testScript("\\u2163\\u2161", new IdentifierExpression("\\u2163\\u2161"));

//    testScript("\\u2163\\u2161\\u200A", new IdentifierExpression("\\u2163\\u2161\\u200A"));

//    testModuleFailure("await", 0, "Unexpected token \"await\"");
    testModuleFailure("function f() { var await }", 19, "Unexpected token \"await\"");
    testScriptFailure("for(let[a].b of 0);", 10, "Unexpected token \".\"");
    testScriptFailure("for(let[a]().b of 0);", 10, "Unexpected token \"(\"");
    testScriptFailure("for(let.a of 0);", 10, "Invalid left-hand side in for-of");
//    testScriptFailure("\\uD800\\uDC00", 0, "Unexpected \"\\\\\"");
  }
}
