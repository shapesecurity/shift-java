package com.shapesecurity.shift.es2016.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.EmptyStatement;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2016.ast.IfStatement;
import com.shapesecurity.shift.es2016.ast.ReturnStatement;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.Export;
import com.shapesecurity.shift.es2016.ast.ExportLocals;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.Import;
import com.shapesecurity.shift.es2016.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.ParserTestCase;

import org.junit.Test;

public class SemicolonsTest extends ParserTestCase {
    @Test
    public void testStatements() throws JsError {
        testScript("function f() { return\n; }", new FunctionDeclaration(false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ReturnStatement(Maybe.empty())))));

        testScript("if(null) null\n;else null;", new IfStatement(
                new LiteralNullExpression(),
                new ExpressionStatement(new LiteralNullExpression()),
                Maybe.of(new ExpressionStatement(new LiteralNullExpression()))
        ));
    }

    @Test
    public void testImportExport() throws JsError {
        testModule("import \"a\"\n;", new Module(
                ImmutableList.empty(),
                ImmutableList.of(
                new Import(Maybe.empty(), ImmutableList.empty(), "a"))));

        testModule("export {}\n;", new Module(
                ImmutableList.empty(),
                ImmutableList.of(
                        new ExportLocals(ImmutableList.empty()))));

        testModule("export function f(){}\n;", new Module(
                ImmutableList.empty(),
                ImmutableList.of(
                        new Export(new FunctionDeclaration(false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))),
                        new EmptyStatement())));
    }
}
