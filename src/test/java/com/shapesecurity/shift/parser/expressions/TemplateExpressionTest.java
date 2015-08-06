package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class TemplateExpressionTest extends ParserTestCase {
    @Test
    public void testTemplateExpression() throws JsError {
        testScript("``", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement(""))));
        testScript("`abc`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("abc"))));
        testScript("`\n`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("\n"))));
        testScript("`\r\n\t\n`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("\r\n\t\n"))));
        testScript("`\\``", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("\\`"))));
        testScript("`$$$`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("$$$"))));
        testScript("`$$$${a}`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("$$$"), new IdentifierExpression("a"), new TemplateElement(""))));
        testScript("`${a}`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement(""))));
        testScript("`${a}$`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement("$"))));
        testScript("`${a}${b}`", new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement(""), new IdentifierExpression("a"), new TemplateElement(""), new IdentifierExpression("b"), new TemplateElement(""))));
        testScript("````", new TemplateExpression(Maybe.just(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("")))), ImmutableList.list(new TemplateElement(""))));
        testScript("``````", new TemplateExpression(Maybe.just(new TemplateExpression(Maybe.just(new TemplateExpression(Maybe.nothing(), ImmutableList.list(new TemplateElement("")))), ImmutableList.list(new TemplateElement("")))), ImmutableList.list(new TemplateElement(""))));
        testScript("a``", new TemplateExpression(Maybe.just(new IdentifierExpression("a")), ImmutableList.list(new TemplateElement(""))));
        testScript("a()``", new TemplateExpression(Maybe.just(new CallExpression(new IdentifierExpression("a"), ImmutableList.nil())), ImmutableList.list(new TemplateElement(""))));
        testScript("new a``", new NewExpression(new TemplateExpression(Maybe.just(new IdentifierExpression("a")), ImmutableList.list(new TemplateElement(""))), ImmutableList.nil()));
        testScript("new a()``", new TemplateExpression(Maybe.just(new NewExpression(new IdentifierExpression("a"), ImmutableList.nil())), ImmutableList.list(new TemplateElement(""))));

        testScriptFailure("`", 1, "Unexpected end of input");
        testScriptFailure("`${a", 4, "Unexpected end of input");
        testScriptFailure("`${a}a${b}", 10, "Unexpected end of input");
        testScriptFailure("`\\37`", 4, "Unexpected \"`\"");
    }
}
