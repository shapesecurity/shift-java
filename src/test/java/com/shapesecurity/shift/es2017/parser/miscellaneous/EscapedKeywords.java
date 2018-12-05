package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.parser.JsError;
import org.junit.Test;

import static com.shapesecurity.shift.es2017.parser.ParserTestCase.*;

public class EscapedKeywords {

    @Test
    public void testNormalKeywords() throws JsError {
        testScriptFailure("i\\u0066 (0)",0, "Unexpected token \"i\\u0066\"");
        testScriptFailure("var i\\u0066",4, "Unexpected token \"i\\u0066\"");
        testScript("({i\\u0066: 0})", new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("if"), new LiteralNumericExpression(0.0)))));
    }

    @Test
    public void testLetKeyword() throws JsError {
        testScriptFailure("le\\u0074 a",9, "Unexpected identifier");
        testScript("var le\\u0074", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(new BindingIdentifier("let"), Maybe.empty())))));
        testScriptEarlyError("\"use strict\"; var le\\u0074", "The identifier \"let\" must not be in binding position in strict mode");
    }

    @Test
    public void testYieldKeyword() throws JsError {
        testScriptFailure("function *a(){yi\\u0065ld 0}",14, "\"yield\" may not be used as an identifier in this context");
        testScriptFailure("function *a(){var yi\\u0065ld}",18, "\"yield\" may not be used as an identifier in this context");
        testScript("function *a(){({yi\\u0065ld: 0})}", new FunctionDeclaration(false, true, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(
                ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new ObjectExpression(ImmutableList.of(new DataProperty(new StaticPropertyName("yield"), new LiteralNumericExpression(0.0)))))))
        ));
    }

    @Test
    public void testContextualKeywords() throws JsError {
        testScriptFailure("({ g\\u0065t x(){} });",12, "Unexpected identifier");
        testModuleFailure("export {a \\u0061s b} from \"\";",10, "Unexpected identifier");
        testModuleFailure("export {} fr\\u006fm \"\";",10, "Unexpected identifier");
        testScriptFailure("for (a o\\u0066 b);",7, "Unexpected identifier");
        testScriptFailure("class a { st\\u0061tic m(){} }",22, "Only methods are allowed in classes");
    }

}
