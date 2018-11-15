package com.shapesecurity.shift.es2017.parser.destructuring.binding_pattern;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.CatchClause;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.TryCatchStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ArrayBindingTest extends ParserTestCase {
    @Test
    public void testArrayBinding() throws JsError {
        testScript("var [,a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.empty(), Maybe.of(new BindingIdentifier("a"))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("var [a]=[1];", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty()), Maybe.of(new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(1.0))))))))));
        testScript("var [[a]]=0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty()))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("var a, [a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()), new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("var [a, a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a")), Maybe.of(new BindingIdentifier("a"))), Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("var [a, ...a] = 0;", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.of(new BindingIdentifier("a"))), Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("try {} catch ([e]) {}", new TryCatchStatement(new Block(ImmutableList.empty()), new CatchClause(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("e"))), Maybe.empty()), new Block(ImmutableList.empty()))));
        testScript("try {} catch ([e, ...a]) {}", new TryCatchStatement(new Block(ImmutableList.empty()), new CatchClause(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("e"))), Maybe.of(new BindingIdentifier("a"))), new Block(ImmutableList.empty()))));
        testScript("for (let [a = b] of [0, c = 0]);");

        testScriptFailure("var [a.b] = 0", 6, "Unexpected token \".\"");
        testScriptFailure("var ([x]) = 0", 4, "Unexpected token \"(\"");
        testScriptFailure("([a.b]) => 0", 0, "Illegal arrow function parameter list");
        testScriptFailure("function a([a.b]) {}", 13, "Unexpected token \".\"");
        testScriptFailure("function* a([a.b]) {}", 14, "Unexpected token \".\"");
        testScriptFailure("(function ([a.b]) {})", 13, "Unexpected token \".\"");
        testScriptFailure("(function* ([a.b]) {})", 14, "Unexpected token \".\"");
        testScriptFailure("({a([a.b]){}})", 6, "Unexpected token \".\"");
        testScriptFailure("({*a([a.b]){}})", 7, "Unexpected token \".\"");
        testScriptFailure("({set a([a.b]){}})", 10, "Unexpected token \".\"");
    }
}
