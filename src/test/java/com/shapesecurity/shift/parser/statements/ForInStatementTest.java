package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ForInStatementTest extends Assertions {
  @Test
  public void testForInStatement() throws JsError {
//    testScript("for(x in list) process(x);", new ForInStatement(new BindingIdentifier("x"),
//            new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
//            new IdentifierExpression("process"), ImmutableList.list(new IdentifierExpression("x"))))));
//
//    testScript("for (var x in list) process(x);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var,
//            ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing()))),
//            new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
//            new IdentifierExpression("process"), ImmutableList.list(new IdentifierExpression("x"))))));
//
//    testScript("for (let x in list) process(x);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//            ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing()))),
//            new IdentifierExpression("list"), new ExpressionStatement(new CallExpression(
//            new IdentifierExpression("process"), ImmutableList.list(new IdentifierExpression("x"))))));
//
//    testScript("for(var a in b);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var,
//            ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))),
//            new IdentifierExpression("b"), new EmptyStatement()));
//
//    testScript("for(a in b);", new ForInStatement(new BindingIdentifier("a"), new IdentifierExpression("b"),
//        new EmptyStatement()));
//
//    testScript("for(a.b in c);", new ForInStatement(new StaticMemberExpression("b", new IdentifierExpression("a")),
//            new IdentifierExpression("c"), new EmptyStatement()));
//
//    testScript("for(let of in of);", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//            ImmutableList.list(new VariableDeclarator(new BindingIdentifier("of"), Maybe.nothing()))),
//            new IdentifierExpression("of"), new EmptyStatement()));
//
//    testScript("for(const a in b)", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Const,
//            ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))),
//            new IdentifierExpression("b"), new EmptyStatement()));
//
//    testScriptFailure("for(let a = 0 in b);", 0, "Invalid variable declaration in for-in statement");
//    testScriptFailure("for(const a = 0 in b);", 0, "Invalid variable declaration in for-in statement");
//    testScriptFailure("for(let ? b : c in 0);", 0, "Invalid left-hand side in for-in");

  }
}
