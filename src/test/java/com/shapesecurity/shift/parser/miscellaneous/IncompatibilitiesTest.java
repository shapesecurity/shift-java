package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class IncompatibilitiesTest extends Assertions {

  @Test
  // programs that parse according to ES3 but either fail or parse differently according to ES5
  public void testES5BackwardIncompatibilities() throws JsError {
    // ES3: zero-width non-breaking space is allowed in an identifier
    // ES5: zero-width non-breaking space is a whitespace character
    testScriptFailure("_\uFEFF_", 2, "Unexpected identifier");

    // ES3: a slash in a regexp character class will terminate the regexp
    // ES5: a slash is allowed within a regexp character class
//    testScriptFailure("[/[/]", 1, "Invalid regular expression: missing /"); // TODO
  }

  @Test
  // programs where we choose to diverge from the ES5 specification
  public void testES5Divergences() throws JsError {
    // ES5: assignment to computed member expression
    // ES6: variable declaration statement

    testScript("let[a] = b",
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
            new VariableDeclarator(
                new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()),
                Maybe.just(new IdentifierExpression("b")))
            )))
    );

    testScript("const[a] = b",
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Const, ImmutableList.list(
            new VariableDeclarator(
                new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing()),
                Maybe.just(new IdentifierExpression("b")))
            )))
    );

    testScript("{ function f() {} }",
        new BlockStatement(new Block(ImmutableList.list(new FunctionDeclaration(new BindingIdentifier("f"), false,
            new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())))))
    );
  }

  @Test
  // programs that parse according to ES5 but either fail or parse differently according to ES6
  public void testES6BackwardIncompatibilities() throws JsError {
    // ES5: in sloppy mode, future reserved words (including yield) are regular identifiers
    // ES6: yield has been moved from the future reserved words list to the keywords list
    testScript("var yield = function yield(){};",
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("yield"), Maybe.just(new FunctionExpression(
                Maybe.just(new BindingIdentifier("yield")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))))
        ))));

    // ES5: this declares a function-scoped variable while at the same time assigning to the block-scoped variable
    // ES6: this particular construction is explicitly disallowed
    testScript("try {} catch(e) { var e = 0; }",
        new TryCatchStatement(new Block(ImmutableList.nil()), new CatchClause(new BindingIdentifier("e"), new Block(
            ImmutableList.list(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("e"), Maybe.just(new LiteralNumericExpression(0.0)))))))
        )))
    );

    // TODO
//    testScriptFailure("for(var x=1 in [1,2,3]) 0", 0, "Invalid variable declaration in for-in statement");
//    testScriptFailure("for(let x=1 in [1,2,3]) 0", 0, "Invalid variable declaration in for-in statement");
//    testScriptFailure("for(var x=1 of [1,2,3]) 0", 0, "Invalid variable declaration in for-of statement");
//    testScriptFailure("for(let x=1 of [1,2,3]) 0", 0, "Invalid variable declaration in for-of statement");

//    testScript("for(var x in [1,2]) 0",
//        new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(
//            new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())
//        )), new ArrayExpression(ImmutableList.list(
//            Maybe.just(new LiteralNumericExpression(1.0)), Maybe.just(new LiteralNumericExpression(2.0))
//        )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
//    );
//
//    testScript("for(let x in [1,2]) 0",
//        new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
//            new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())
//        )), new ArrayExpression(ImmutableList.list(
//            Maybe.just(new LiteralNumericExpression(1.0)), Maybe.just(new LiteralNumericExpression(2.0))
//        )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
//    );
//
//    testScript("for(var x of [1,2]) 0",
//        new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(
//            new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())
//        )), new ArrayExpression(ImmutableList.list(
//            Maybe.just(new LiteralNumericExpression(1.0)), Maybe.just(new LiteralNumericExpression(2.0))
//        )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
//    );
//
//    testScript("for(let x of [1,2]) 0",
//        new ForOfStatement(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(
//            new VariableDeclarator(new BindingIdentifier("x"), Maybe.nothing())
//        )), new ArrayExpression(ImmutableList.list(
//            Maybe.just(new LiteralNumericExpression(1.0)), Maybe.just(new LiteralNumericExpression(2.0))
//        )), new ExpressionStatement(new LiteralNumericExpression(0.0)))
//    );
//
//    testScript("<!--");
//    testScript("-->");
//
//    testScriptFailure("a -->", 0, "Unexpected end of input");
//    testScriptFailure(";/**/-->", 0, "Unexpected token \">\"");
//
//    testScript("\n  -->");
//    testScript("/*\n*/-->");
//
//    testScript("a<!--b", new IdentifierExpression("a"));
//
//    testModuleFailure("<!--", 0, "Unexpected token \"<\"");
//    testModuleFailure("-->", 0, "Unexpected token \">\"");
//
//    testModule("a<!--b",
//        new BinaryExpression(BinaryOperator.LessThan, new IdentifierExpression("a"),
//            new UnaryExpression(UnaryOperator.LogicalNot, new UpdateExpression(true, UpdateOperator.Decrement, new BindingIdentifier("b")))));
  }
}
