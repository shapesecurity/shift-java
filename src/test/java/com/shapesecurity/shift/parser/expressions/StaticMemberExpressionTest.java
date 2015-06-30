package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.shift.ast.IdentifierExpression;
import com.shapesecurity.shift.ast.StaticMemberExpression;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class StaticMemberExpressionTest extends Assertions {
  @Test
  public void testStaticMemberExpression() throws JsError {
    testScript("a.b", new StaticMemberExpression("b", new IdentifierExpression("a")));

    testScript("a.b.c", new StaticMemberExpression("c", new StaticMemberExpression("b", new IdentifierExpression("a"))));

    testScript("a.$._.B0", new StaticMemberExpression("B0", new StaticMemberExpression("_",
            new StaticMemberExpression("$", new IdentifierExpression("a")))));

    testScript("a.if", new StaticMemberExpression("if", new IdentifierExpression("a")));

    testScript("a.true", new StaticMemberExpression("true", new IdentifierExpression("a")));

    testScript("a.false", new StaticMemberExpression("false", new IdentifierExpression("a")));

    testScript("a.null", new StaticMemberExpression("null", new IdentifierExpression("a")));
  }
}
