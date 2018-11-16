package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.ComputedMemberExpression;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ForInStatement;
import com.shapesecurity.shift.es2017.ast.ForStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class IdentifierExpressionTest extends ParserTestCase {
    @Test
    public void testIdentifierExpression() throws JsError {
        testScript("x", new IdentifierExpression("x"));

        testScript("x;", new IdentifierExpression("x"));

        testScript("await", new IdentifierExpression("await"));

        testScript("let", new IdentifierExpression("let"));

        testScript("let()", new CallExpression(new IdentifierExpression("let"), ImmutableList.empty()));

        testScript("let.let", new StaticMemberExpression(new IdentifierExpression("let"), "let"));

        testScript("(let[let])", new ComputedMemberExpression(new IdentifierExpression("let"), new IdentifierExpression("let")));

        testScript("(let[a])", new ComputedMemberExpression(new IdentifierExpression("let"), new IdentifierExpression("a")));

        testScript("for(let;;);", new ForStatement(Maybe.of(new IdentifierExpression("let")), Maybe.empty(), Maybe.empty(), new EmptyStatement()));

        testScript("for(let();;);", new ForStatement(Maybe.of(new CallExpression(new IdentifierExpression("let"),
                ImmutableList.empty())), Maybe.empty(), Maybe.empty(), new EmptyStatement()));

        testScript("for(let yield in 0);", new ForInStatement(new VariableDeclaration(
			VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("yield"), Maybe.empty()))),
                new LiteralNumericExpression(0.0), new EmptyStatement()));

        testScript("for(let.let in 0);", new ForInStatement(new StaticMemberAssignmentTarget(new IdentifierExpression("let"), "let"),
                new LiteralNumericExpression(0.0), new EmptyStatement()));

        testScript("日本語", new IdentifierExpression("日本語"));

        testScript("\uD800\uDC00", new IdentifierExpression("\uD800\uDC00"));

        testScript("T\u203F", new IdentifierExpression("T\u203F"));

        testScript("T\u200C", new IdentifierExpression("T\u200C"));

        testScript("T\u200D", new IdentifierExpression("T\u200D"));

        testScript("\u2163\u2161", new IdentifierExpression("\u2163\u2161"));

        testScript("\u2163\u2161\u200A", new IdentifierExpression("\u2163\u2161"));

        testScriptFailure("a\u0007", 1, "Unexpected \"\u0007\"");
        testScriptFailure("a\u007F", 1, "Unexpected \"\u007F\"");
        testModuleFailure("await", 0, "Unexpected token \"await\"");
        testModuleFailure("function f() { var await }", 19, "Unexpected token \"await\"");
        testScriptFailure("for(let[a].b of 0);", 10, "Unexpected token \".\"");
        testScriptFailure("for(let[a]().b of 0);", 10, "Unexpected token \"(\"");
        testScriptFailure("for(let.a of 0);", 10, "Invalid left-hand side in for-of");
        testScriptFailure("\\uD800\\uDC00", 6, "Unexpected \"\\\\\"");
    }
}
