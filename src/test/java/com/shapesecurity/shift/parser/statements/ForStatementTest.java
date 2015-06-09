package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.AssignmentOperator;
import com.shapesecurity.shift.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ForStatementTest extends Assertions {
  @Test
  public void testForStatement() throws JsError {
//    testScript("for(x, y;;);");

//    testScript("for(x = 0;;);", new ForStatement(new AssignmentExpression(new BindingIdentifier("x"),
//        new LiteralNumericExpression(0.0)), Maybe.nothing(), Maybe.nothing(), new EmptyStatement()));

//    testScript("for(var x = 0;;);", new ForStatement(
//        Maybe.just(
//            new VariableDeclaration(
//              VariableDeclarationKind.Var,
//              ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0)))))
//        ),
//        Maybe.nothing(), Maybe.nothing(), new EmptyStatement())
//    );
  }
}
