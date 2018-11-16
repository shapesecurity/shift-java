package com.shapesecurity.shift.es2017.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ClassDeclarationTest extends ParserTestCase {
    @Test
    public void testClassDeclarations() throws JsError {
        testScript("class A{}", new ClassDeclaration(new BindingIdentifier("A"), Maybe.empty(), ImmutableList.empty()));

        testScriptFailure("class {}", 6, "Unexpected token \"{\"");
        testScriptFailure("class extends A{}", 6, "Unexpected token \"extends\"");
    }
}
