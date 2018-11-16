package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.TryCatchStatement;
import com.shapesecurity.shift.es2017.ast.UnaryExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.ArrayExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.CatchClause;
import com.shapesecurity.shift.es2017.ast.ForInStatement;
import com.shapesecurity.shift.es2017.ast.ForOfStatement;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class IncompatibilitiesTest extends ParserTestCase {

    @Test
    // programs that parse according to ES3 but either fail or parse differently according to ES5
    public void testES5BackwardIncompatibilities() throws JsError {
        // ES3: zero-width non-breaking space is allowed in an identifier
        // ES5: zero-width non-breaking space is a whitespace character
        testScriptFailure("_\uFEFF_", 2, "Unexpected identifier");

        // ES3: a slash in a regexp character class will terminate the regexp
        // ES5: a slash is allowed within a regexp character class
        testScriptFailure("[/[/]", 1, "Invalid regular expression: missing /");
    }

    @Test
    // programs where we choose to diverge from the ES5 specification
    public void testES5Divergences() throws JsError {
        // ES5: assignment to computed member expression
        // ES6: variable declaration statement

        testScript("let[a] = b",
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(
                                new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty()),
                                Maybe.of(new IdentifierExpression("b")))
                )))
        );

        testScript("const[a] = b",
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.of(
                        new VariableDeclarator(
                                new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty()),
                                Maybe.of(new IdentifierExpression("b")))
                )))
        );

        testScript("{ function f() {} }",
                new BlockStatement(new Block(ImmutableList.of(new FunctionDeclaration(false, false, new BindingIdentifier("f"),
                        new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())))))
        );
    }

    @Test
    // programs that parse according to ES5 but either fail or parse differently according to ES6
    public void testES6BackwardIncompatibilities() throws JsError {
        // ES5: in sloppy mode, future reserved words (including yield) are regular identifiers
        // ES6: yield has been moved from the future reserved words list to the keywords list
        testScript("var yield = function yield(){};",
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("yield"), Maybe.of(new FunctionExpression(false, false,
                                Maybe.of(new BindingIdentifier("yield")), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))))
                ))));

        // ES5: this declares a function-scoped variable while at the same time assigning to the block-scoped variable
        // ES6: this particular construction is explicitly disallowed
        testScript("try {} catch(e) { var e = 0; }",
                new TryCatchStatement(new Block(ImmutableList.empty()), new CatchClause(new BindingIdentifier("e"), new Block(
                        ImmutableList.of(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("e"), Maybe.of(new LiteralNumericExpression(0.0)))))))
                )))
        );

        testScriptFailure("for(var x=1 in [1,2,3]) 0", 12, "Invalid variable declaration in for-in statement");
        testScriptFailure("for(let x=1 in [1,2,3]) 0", 12, "Invalid variable declaration in for-in statement");
        testScriptFailure("for(var x=1 of [1,2,3]) 0", 12, "Invalid variable declaration in for-of statement");
        testScriptFailure("for(let x=1 of [1,2,3]) 0", 12, "Invalid variable declaration in for-of statement");

        testScript("for(var x in [1,2]) 0", new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty()))),
                new ArrayExpression(ImmutableList.of(Maybe.of(new LiteralNumericExpression(1.0)), Maybe.of(
                    new LiteralNumericExpression(2.0)))), new ExpressionStatement(new LiteralNumericExpression(0.0))));

        testScript("for(let x in [1,2]) 0",
                new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())
                )), new ArrayExpression(ImmutableList.of(
                        Maybe.of(new LiteralNumericExpression(1.0)), Maybe.of(new LiteralNumericExpression(2.0))
                )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
        );

        testScript("for(var x of [1,2]) 0",
                new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())
                )), new ArrayExpression(ImmutableList.of(
                        Maybe.of(new LiteralNumericExpression(1.0)), Maybe.of(new LiteralNumericExpression(2.0))
                )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
        );

        testScript("for(let x of [1,2]) 0",
                new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("x"), Maybe.empty())
                )), new ArrayExpression(ImmutableList.of(
                        Maybe.of(new LiteralNumericExpression(1.0)), Maybe.of(new LiteralNumericExpression(2.0))
                )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
        );

        testScript("<!--");
        testScript("-->");

        testScriptFailure("a -->", 5, "Unexpected end of input");
        testScriptFailure(";/**/-->", 7, "Unexpected token \">\"");

        testScript("\n  -->");
        testScript("/*\n*/-->");

        testScript("a<!--b", new IdentifierExpression("a"));

        testModuleFailure("<!--", 0, "Unexpected token \"<\"");
        testModuleFailureML("function a(){\n<!--\n}", 2, 0, 14, "Unexpected token \"<\"");
        testModuleFailure("-->", 2, "Unexpected token \">\"");
        testModuleFailureML("function a(){\n-->\n}", 2, 2, 16, "Unexpected token \">\"");

        testModule("a<!--b",
                new BinaryExpression(new IdentifierExpression("a"), BinaryOperator.LessThan,
                        new UnaryExpression(UnaryOperator.LogicalNot, new UpdateExpression(true, UpdateOperator.Decrement, new AssignmentTargetIdentifier("b")))));
    }
}
