package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class VariableDeclarationStatementTest extends ParserTestCase {
    @Test
    public void testVariableDeclarationStatement() throws JsError {
        testScript("var x", new VariableDeclarationStatement(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())))));

        testScript("var a;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))));

        testScript("var x, y;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()),
                        new VariableDeclarator(new BindingIdentifier("y"), Maybe.empty())))));

        testScript("var x = 0", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testScript("var eval = 0, arguments = 1", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("eval"), Maybe.of(new LiteralNumericExpression(0.0))),
                        new VariableDeclarator(new BindingIdentifier("arguments"), Maybe.of(new LiteralNumericExpression(1.0)))))));

        testScript("var x = 0, y = 1, z = 2", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))),
                        new VariableDeclarator(new BindingIdentifier("y"), Maybe.of(new LiteralNumericExpression(1.0))),
                        new VariableDeclarator(new BindingIdentifier("z"), Maybe.of(new LiteralNumericExpression(2.0)))))));

        testScript("var implements, interface, package", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(
                new VariableDeclarator(new BindingIdentifier("implements"), Maybe.empty()),
                new VariableDeclarator(new BindingIdentifier("interface"), Maybe.empty()),
                new VariableDeclarator(new BindingIdentifier("package"), Maybe.empty())))));

        testScript("var private, protected, public", new VariableDeclarationStatement(new VariableDeclaration(
                VariableDeclarationKind.Var, ImmutableList.of(
                new VariableDeclarator(new BindingIdentifier("private"), Maybe.empty()),
                new VariableDeclarator(new BindingIdentifier("protected"), Maybe.empty()),
                new VariableDeclarator(new BindingIdentifier("public"), Maybe.empty())))));

        testScript("var yield;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("yield"), Maybe.empty())))));

        testScript("var let", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("let"), Maybe.empty())))));

        testScript("let x", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())))));

        testScript("{ let x }", new BlockStatement(new Block(ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())
                )))
        ))));

        testScript("{ let x = 0, y = 1, z = 2 }", new BlockStatement(new Block(ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))),
                        new VariableDeclarator(new BindingIdentifier("y"), Maybe.of(new LiteralNumericExpression(1.0))),
                        new VariableDeclarator(new BindingIdentifier("z"), Maybe.of(new LiteralNumericExpression(2.0)))
                )))
        ))));

        testScript("let x, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()),
                        new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.empty())))));

        testScript("let x, x\uDB40\uDDD5;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()),
                        new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.empty())))));

        testScript("let xǕ, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x\u01D5"), Maybe.empty()),
                        new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.empty())))));

        testScript("let x\u01D5, x\\u{E01D5}", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x\u01D5"), Maybe.empty()),
                        new VariableDeclarator(new BindingIdentifier("x\uDB40\uDDD5"), Maybe.empty())))));

        testScript("{ const x = 0 }", new BlockStatement(new Block(ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))
                )))
        ))));

        testScript("{ const x = 0, y = 1, z = 2 }", new BlockStatement(new Block(ImmutableList.of(
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))),
                        new VariableDeclarator(new BindingIdentifier("y"), Maybe.of(new LiteralNumericExpression(1.0))),
                        new VariableDeclarator(new BindingIdentifier("z"), Maybe.of(new LiteralNumericExpression(2.0)))
                )))
        ))));

        testScript("var static;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("static"), Maybe.empty())))));

        testScript("let[let]=0", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("let"))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));

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
