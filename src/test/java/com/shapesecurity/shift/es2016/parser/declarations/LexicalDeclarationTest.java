package com.shapesecurity.shift.es2016.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.Block;
import com.shapesecurity.shift.es2016.ast.BlockStatement;
import com.shapesecurity.shift.es2016.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2016.ast.VariableDeclaration;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.VariableDeclarator;
import com.shapesecurity.shift.es2016.ast.WhileStatement;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;
import com.shapesecurity.shift.es2016.parser.JsError;

import org.junit.Test;

public class LexicalDeclarationTest extends ParserTestCase {
    @Test
    public void testLexicalDeclaration() throws JsError {
        testScript("let a", new VariableDeclarationStatement(new VariableDeclaration(
			VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty())))));

        testScript("{ let a; }", new BlockStatement(new Block(ImmutableList.of(new VariableDeclarationStatement(
                new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(
                        new BindingIdentifier("a"), Maybe.empty()))))))));

        testScript("while(true) var a", new WhileStatement(new LiteralBooleanExpression(true),
                new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                        new VariableDeclarator(new BindingIdentifier("a"), Maybe.empty()))))));

        testScriptFailure("while(true) let a", 12, "Unexpected token \"let\"");
        testScriptFailure("while(true) const a", 12, "Unexpected token \"const\"");
        testScriptFailure("with(true) let a", 11, "Unexpected token \"let\"");
        testScriptFailure("with(true) class a {}", 11, "Unexpected token \"class\"");
        testScriptFailure("a: let a", 3, "Unexpected token \"let\"");
    }
}
