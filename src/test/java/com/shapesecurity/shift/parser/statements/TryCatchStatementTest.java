package com.shapesecurity.shift.parser.statements;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class TryCatchStatementTest extends ParserTestCase {

    @Test
    public void testTryCatchStatement() throws JsError {
        testScript("try{}catch(a){}", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("a"), new Block(ImmutableList.nil())
                )));

        testScript("try{ } catch (e) { }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.nil())
                )));

        testScript("try { } catch (e) { let a; }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.list(new VariableDeclarator(
                                new BindingIdentifier("a"), Maybe.nothing()
                        )))
                ))))));

        testScript("try{ } catch (eval) { }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("eval"), new Block(ImmutableList.nil())
                )));

        testScript("try{ } catch (arguments) { }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("arguments"), new Block(ImmutableList.nil())
                )));

        testScript("try { } catch (e) { say(e); }", new TryCatchStatement(new Block(ImmutableList.nil()),
                new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new ExpressionStatement(
                        new CallExpression(new IdentifierExpression("say"), ImmutableList.list(new IdentifierExpression("e")))
                ))))));

        testScript("try { doThat(); } catch (e) { say(e) }", new TryCatchStatement(new Block(ImmutableList.list(
                new ExpressionStatement(new CallExpression(new IdentifierExpression("doThat"), ImmutableList.nil()))
        )), new CatchClause(new BindingIdentifier("e"), new Block(ImmutableList.list(new ExpressionStatement(
                new CallExpression(new IdentifierExpression("say"), ImmutableList.list(new IdentifierExpression("e")))
        ))))));

        testScriptFailure("try {} catch ((e)) {}", 14, "Unexpected token \"(\"");
    }
}
