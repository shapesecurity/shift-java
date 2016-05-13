package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class SemicolonsTest extends ParserTestCase {
    @Test
    public void testStatements() throws JsError {
        testScript("function f() { return\n; }", new FunctionDeclaration(false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new ReturnStatement(Maybe.nothing())))));

        testScript("if(null) null\n;else null;", new IfStatement(
                new LiteralNullExpression(),
                new ExpressionStatement(new LiteralNullExpression()),
                Maybe.just(new ExpressionStatement(new LiteralNullExpression()))
        ));
    }

    @Test
    public void testImportExport() throws JsError {
        testModule("import \"a\"\n;", new Module(
                ImmutableList.nil(),
                ImmutableList.list(
                new Import(Maybe.nothing(), ImmutableList.nil(), "a"))));

        testModule("export {}\n;", new Module(
                ImmutableList.nil(),
                ImmutableList.list(
                        new ExportLocals(ImmutableList.nil()))));

        testModule("export function f(){}\n;", new Module(
                ImmutableList.nil(),
                ImmutableList.list(
                        new Export(new FunctionDeclaration(false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                                new FunctionBody(ImmutableList.nil(), ImmutableList.nil()))),
                        new EmptyStatement())));
    }
}
