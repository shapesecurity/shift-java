package com.shapesecurity.shift.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/16/15.
 */
public class LexicalDeclarationTest extends Assertions {
  @Test
  public void testLexicalDeclaration() throws JsError {
    testScript("let a", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Let,
        ImmutableList.list(new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing())))));

    testScript("{ let a; }", new BlockStatement(new Block(ImmutableList.list(new VariableDeclarationStatement(
        new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(
            new BindingIdentifier("a"), Maybe.nothing()))))))));

    testScript("while(true) var a", new WhileStatement(new LiteralBooleanExpression(true),
        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(
            new VariableDeclarator(new BindingIdentifier("a"), Maybe.nothing()))))));

    testScriptFailure("while(true) let a", 12, "Unexpected token \"let\"");
    testScriptFailure("while(true) const a", 12, "Unexpected token \"const\"");
    testScriptFailure("with(true) let a", 11, "Unexpected token \"let\"");
    testScriptFailure("with(true) class a {}", 11, "Unexpected token \"class\"");
    testScriptFailure("a: let a", 3, "Unexpected token \"let\"");
  }
}
