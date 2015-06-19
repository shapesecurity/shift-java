package com.shapesecurity.shift.parser.destructuring.binding_pattern;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class ArrayBindingTest extends Assertions {
  @Test
  public void testArrayBinding() throws JsError {
    testScript("var [,a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.nothing(), Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()), Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("var [a]=[1];", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()), Maybe.just(new ArrayExpression(ImmutableList.list(Maybe.just(new LiteralNumericExpression(1.0))))))))));
    testScript("var [[a]]=0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()))), Maybe.nothing()), Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("var a, [a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()), new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()), Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("var [a, a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a")), Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()), Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("var [a, ...a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.just(new BindingIdentifier("a"))), Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("try {} catch ([e]) {}", new TryCatchStatement(new Block(ImmutableList.nil()), new CatchClause(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("e"))), Maybe.nothing()), new Block(ImmutableList.nil()))));
    testScript("try {} catch ([e, ...a]) {}", new TryCatchStatement(new Block(ImmutableList.nil()), new CatchClause(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("e"))), Maybe.just(new BindingIdentifier("a"))), new Block(ImmutableList.nil()))));

    testScriptFailure("var [a.b] = 0", 6, "Unexpected token \".\"");
    testScriptFailure("var ([x]) = 0", 4, "Unexpected token \"(\"");
//    testScriptFailure("([a.b]) => 0", 0, "Illegal arrow function parameter list"); // TODO wrong error msg
    testScriptFailure("function a([a.b]) {}", 13, "Unexpected token \".\"");
    testScriptFailure("function* a([a.b]) {}", 14, "Unexpected token \".\"");
    testScriptFailure("(function ([a.b]) {})", 13, "Unexpected token \".\"");
    testScriptFailure("(function* ([a.b]) {})", 14, "Unexpected token \".\"");
    testScriptFailure("({a([a.b]){}})", 6, "Unexpected token \".\"");
    testScriptFailure("({*a([a.b]){}})", 7, "Unexpected token \".\"");
    testScriptFailure("({set a([a.b]){}})", 10, "Unexpected token \".\"");
  }
}
