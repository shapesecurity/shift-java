package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.NewExpression;
import com.shapesecurity.shift.es2017.ast.TemplateElement;
import com.shapesecurity.shift.es2017.ast.TemplateExpression;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class TemplateExpressionTest extends ParserTestCase {
    @Test
    public void testTemplateExpression() throws JsError {
        testScript("``", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement(""))));
        testScript("`abc`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("abc"))));
        testScript("`\n`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("\n"))));
        testScript("`\r\n\t\n`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("\r\n\t\n"))));
        testScript("`\\``", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("\\`"))));
        testScript("`$$$`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("$$$"))));
        testScript("`$$$${a}`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("$$$"), new IdentifierExpression("a"), new TemplateElement(""))));
        testScript("`${a}`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement(""))));
        testScript("`${a}$`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement("$"))));
        testScript("`${a}${b}`", new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement(""), new IdentifierExpression("b"), new TemplateElement(""))));
        testScript("````", new TemplateExpression(Maybe.of(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("")))), ImmutableList.of(new TemplateElement(""))));
        testScript("``````", new TemplateExpression(Maybe.of(new TemplateExpression(Maybe.of(new TemplateExpression(Maybe.empty(), ImmutableList.of(new TemplateElement("")))), ImmutableList.of(new TemplateElement("")))), ImmutableList.of(new TemplateElement(""))));
        testScript("a``", new TemplateExpression(Maybe.of(new IdentifierExpression("a")), ImmutableList.of(new TemplateElement(""))));
        testScript("a()``", new TemplateExpression(Maybe.of(new CallExpression(new IdentifierExpression("a"), ImmutableList.empty())), ImmutableList.of(new TemplateElement(""))));
        testScript("new a``", new NewExpression(new TemplateExpression(Maybe.of(new IdentifierExpression("a")), ImmutableList.of(new TemplateElement(""))), ImmutableList.empty()));
        testScript("new a()``", new TemplateExpression(Maybe.of(new NewExpression(new IdentifierExpression("a"), ImmutableList.empty())), ImmutableList.of(new TemplateElement(""))));

        testScriptFailure("`", 1, "Unexpected end of input");
        testScriptFailure("a++``", 3, "Unexpected template");
        testScriptFailure("`${a", 4, "Unexpected end of input");
        testScriptFailure("`${a}a${b}", 10, "Unexpected end of input");
        testScriptFailure("`\\37`", 4, "Unexpected \"`\"");
    }
}
