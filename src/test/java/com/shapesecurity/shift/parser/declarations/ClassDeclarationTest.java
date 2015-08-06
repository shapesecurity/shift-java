package com.shapesecurity.shift.parser.declarations;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.ClassDeclaration;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ClassDeclarationTest extends ParserTestCase {
    @Test
    public void testClassDeclarations() throws JsError {
        testScript("class A{}", new ClassDeclaration(new BindingIdentifier("A"), Maybe.nothing(), ImmutableList.nil()));

        testScriptFailure("class {}", 6, "Unexpected token \"{\"");
        testScriptFailure("class extends A{}", 6, "Unexpected token \"extends\"");
    }
}
