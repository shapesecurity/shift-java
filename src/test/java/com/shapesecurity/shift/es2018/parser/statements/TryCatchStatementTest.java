package com.shapesecurity.shift.es2018.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.BindingIdentifier;
import com.shapesecurity.shift.es2018.ast.Block;
import com.shapesecurity.shift.es2018.ast.CallExpression;
import com.shapesecurity.shift.es2018.ast.CatchClause;
import com.shapesecurity.shift.es2018.ast.ExpressionStatement;
import com.shapesecurity.shift.es2018.ast.IdentifierExpression;
import com.shapesecurity.shift.es2018.ast.TryCatchStatement;
import com.shapesecurity.shift.es2018.ast.VariableDeclaration;
import com.shapesecurity.shift.es2018.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2018.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2018.ast.VariableDeclarator;
import com.shapesecurity.shift.es2018.parser.ParserTestCase;
import com.shapesecurity.shift.es2018.parser.JsError;

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
