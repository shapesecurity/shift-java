package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;

import org.junit.Test;

public class DirectiveTest extends ParserTestCase {
    @Test
    public void testDirective() throws JsError {
        testScript("\"Hello\"", new Script(ImmutableList.of(new Directive("Hello")), ImmutableList.empty()));
        testScript("\"\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0\"", new Script(ImmutableList.of(new Directive("\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0")), ImmutableList.empty()));
        testScript("\"\\u0061\"", new Script(ImmutableList.of(new Directive("\\u0061")), ImmutableList.empty()));
        testScript("\"\\x61\"", new Script(ImmutableList.of(new Directive("\\x61")), ImmutableList.empty()));
        testScript("\"Hello\\nworld\"", new Script(ImmutableList.of(new Directive("Hello\\nworld")), ImmutableList.empty()));
        testScript("\"Hello\\\nworld\"", new Script(ImmutableList.of(new Directive("Hello\\\nworld")), ImmutableList.empty()));
        testScript("\"Hello\\02World\"", new Script(ImmutableList.of(new Directive("Hello\\02World")), ImmutableList.empty()));
        testScript("\"Hello\\012World\"", new Script(ImmutableList.of(new Directive("Hello\\012World")), ImmutableList.empty()));
        testScript("\"Hello\\122World\"", new Script(ImmutableList.of(new Directive("Hello\\122World")), ImmutableList.empty()));
        testScript("\"Hello\\0122World\"", new Script(ImmutableList.of(new Directive("Hello\\0122World")), ImmutableList.empty()));
        testScript("\"Hello\\312World\"", new Script(ImmutableList.of(new Directive("Hello\\312World")), ImmutableList.empty()));
        testScript("\"Hello\\412World\"", new Script(ImmutableList.of(new Directive("Hello\\412World")), ImmutableList.empty()));
        testScript("\"Hello\\712World\"", new Script(ImmutableList.of(new Directive("Hello\\712World")), ImmutableList.empty()));
        testScript("\"Hello\\0World\"", new Script(ImmutableList.of(new Directive("Hello\\0World")), ImmutableList.empty()));
        testScript("\"Hello\\\r\nworld\"", new Script(ImmutableList.of(new Directive("Hello\\\r\nworld")), ImmutableList.empty()));
        testScript("\"Hello\\1World\"", new Script(ImmutableList.of(new Directive("Hello\\1World")), ImmutableList.empty()));
        testScript("(function () { 'use\\x20strict'; with (i); })", new FunctionExpression(Maybe.empty(), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use\\x20strict")), ImmutableList.of(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
        testScript("(function () { 'use\\nstrict'; with (i); })", new FunctionExpression(Maybe.empty(), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use\\nstrict")), ImmutableList.of(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
        testScript("function a() {'use strict';return 0;};", new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("(function() {'use strict';return 0;});", new FunctionExpression(Maybe.empty(), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("(function a() {'use strict';return 0;});", new FunctionExpression(Maybe.of(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("\"use strict\" + 0", new BinaryExpression(BinaryOperator.Plus, new LiteralStringExpression("use strict"), new LiteralNumericExpression(0.0)));
        testModule("\"use strict\";", new Module(ImmutableList.of(new Directive("use strict")), ImmutableList.empty()));
    }
}
