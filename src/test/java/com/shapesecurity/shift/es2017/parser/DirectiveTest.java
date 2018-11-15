package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.ReturnStatement;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;

import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.WithStatement;
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
        testScript("(function () { 'use\\x20strict'; with (i); })", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use\\x20strict")), ImmutableList.of(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
        testScript("(function () { 'use\\nstrict'; with (i); })", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use\\nstrict")), ImmutableList.of(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
        testScript("function a() {'use strict';return 0;};", new FunctionDeclaration(false, false, new BindingIdentifier("a"), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("(function() {'use strict';return 0;});", new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("(function a() {'use strict';return 0;});", new FunctionExpression(false, false, Maybe.of(new BindingIdentifier("a")), new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.of(new ReturnStatement(Maybe.of(new LiteralNumericExpression(0.0)))))));
        testScript("\"use strict\" + 0", new BinaryExpression(new LiteralStringExpression("use strict"), BinaryOperator.Plus, new LiteralNumericExpression(0.0)));
        testModule("\"use strict\";", new Module(ImmutableList.of(new Directive("use strict")), ImmutableList.empty()));

        testScriptFailure("\"\\1\"; \"use strict\";", 6, "Unexpected legacy octal escape sequence: \\1");
        testScriptFailure("\"\\1\"; \"use strict\"; null;", 6, "Unexpected legacy octal escape sequence: \\1");
        testScriptFailure("\"use strict\"; \"\\1\";", 14, "Unexpected legacy octal escape sequence: \\1");
        testScriptFailure("\"use strict\"; \"\\1\"; null;", 14, "Unexpected legacy octal escape sequence: \\1");

        testScriptFailure("\"use strict\"; function f(){\"\\1\";}", 27, "Unexpected legacy octal escape sequence: \\1");
    }
}
