package com.shapesecurity.shift.es2017.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.CatchClause;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.TryCatchStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class TryCatchStatementTest extends ParserTestCase {

    @Test
    public void testTryCatchStatement() throws JsError {
        testScript("try{}catch(a){}", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("a"), new Block(ImmutableList.empty())
                )));

        testScript("try{ } catch (e) { }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.empty())
                )));

        testScript("try { } catch (e) { let a; }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(new VariableDeclarator(
                                new BindingIdentifier("a"), Maybe.empty()
                        )))
                ))))));

        testScript("try{ } catch (eval) { }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("eval"), new Block(ImmutableList.empty())
                )));

        testScript("try{ } catch (arguments) { }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("arguments"), new Block(ImmutableList.empty())
                )));

        testScript("try { } catch (e) { say(e); }", new TryCatchStatement(new Block(ImmutableList.empty()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("say"), ImmutableList.of(new IdentifierExpression("e")))
                ))))));

        testScript("try { doThat(); } catch (e) { say(e) }", new TryCatchStatement(new Block(ImmutableList.of(
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"), ImmutableList.empty()))
        )), new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.of(new ExpressionStatement(
                new CallExpression(new IdentifierExpression("say"), ImmutableList.of(new IdentifierExpression("e")))
        ))))));

        testScriptFailure("try {} catch ((e)) {}", 14, "Unexpected token \"(\"");
    }
}
