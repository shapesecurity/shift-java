package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class VariableDeclarationStatementTest extends Assertions {
  @Test
  public void testVariableDeclarationStatement() throws JsError {
    testScript("var x", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())))));

    testScript("var a;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing())))));

    testScript("var x, y;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing()),
            new VariableDeclarator(new BindingIdentifier("y"), Maybe.nothing())))));

    testScript("var x = 0", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0)))))));

    testScript("var eval = 0, arguments = 1", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("eval"), Maybe.just(new LiteralNumericExpression(0.0))),
            new VariableDeclarator(new BindingIdentifier("arguments"), Maybe.just(new LiteralNumericExpression(1.0)))))));

    testScript("var x = 0, y = 1, z = 2", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0))),
            new VariableDeclarator(new BindingIdentifier("y"), Maybe.just(new LiteralNumericExpression(1.0))),
            new VariableDeclarator(new BindingIdentifier("z"), Maybe.just(new LiteralNumericExpression(2.0)))))));

    testScript("var implements, interface, package", new VariableDeclarationStatement(new VariableDeclaration(
        VariableDeclarationKind.Var, ImmutableList.list(
        new VariableDeclarator(new BindingIdentifier("implements"), Maybe.nothing()),
        new VariableDeclarator(new BindingIdentifier("interface"), Maybe.nothing()),
        new VariableDeclarator(new BindingIdentifier("package"), Maybe.nothing())))));

    testScript("var private, protected, public", new VariableDeclarationStatement(new VariableDeclaration(
        VariableDeclarationKind.Var, ImmutableList.list(
        new VariableDeclarator(new BindingIdentifier("private"), Maybe.nothing()),
        new VariableDeclarator(new BindingIdentifier("protected"), Maybe.nothing()),
        new VariableDeclarator(new BindingIdentifier("public"), Maybe.nothing())))));

    testScript("var yield;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("yield"), Maybe.nothing())))));

    testScript("var let", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("let"), Maybe.nothing())))));

    testScript("let x", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())))));

    testScript("{ let x }", new BlockStatement(new Block(ImmutableList.list(
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())
        )))
    ))));

    testScript("{ let x = 0, y = 1, z = 2 }", new BlockStatement(new Block(ImmutableList.list(
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0))),
            new VariableDeclarator(new BindingIdentifier("y"), Maybe.just(new LiteralNumericExpression(1.0))),
            new VariableDeclarator(new BindingIdentifier("z"), Maybe.just(new LiteralNumericExpression(2.0)))
        )))
    ))));

//    testScript("let x, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing()),
//            new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.nothing())))));

    testScript("let x, x\uDB40\uDDD5;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing()),
            new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.nothing())))));

//    testScript("let xǕ, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x\u01D5"), Maybe.nothing()),
//            new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.nothing())))));

//    testScript("let x\u01D5, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
//        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x\u01D5"), Maybe.nothing()),
//            new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.nothing())))));

    testScript("{ const x = 0 }", new BlockStatement(new Block(ImmutableList.list(
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0)))
        )))
    ))));

    testScript("{ const x = 0, y = 1, z = 2 }", new BlockStatement(new Block(ImmutableList.list(
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0))),
            new VariableDeclarator(new BindingIdentifier("y"), Maybe.just(new LiteralNumericExpression(1.0))),
            new VariableDeclarator(new BindingIdentifier("z"), Maybe.just(new LiteralNumericExpression(2.0)))
        )))
    ))));

    testScript("var static;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("static"), Maybe.nothing())))));

    testScript("(let[a])", new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("let")));

    testScriptFailure("var const", 4, "Unexpected token \"const\"");
    testScriptFailure("var a[0]=0;", 5, "Unexpected token \"[\"");
    testScriptFailure("var (a)=0;", 4, "Unexpected token \"(\"");
    testScriptFailure("var new A = 0;", 4, "Unexpected token \"new\"");
    testScriptFailure("var (x)", 4, "Unexpected token \"(\"");
    testScriptFailure("var this", 4, "Unexpected token \"this\"");
    testScriptFailure("var a.b;", 5, "Unexpected token \".\"");
    testScriptFailure("var [a];", 7, "Unexpected token \";\"");
    testScriptFailure("var {a};", 7, "Unexpected token \";\"");
    testScriptFailure("var {a:a};", 9, "Unexpected token \";\"");
  }
}
