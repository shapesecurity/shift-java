package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import org.junit.Test;

public class DirectiveTest extends ParserTestCase {
  @Test
  public void testDirective() throws JsError {
    testScript("\"Hello\"", new Script(ImmutableList.list(new Directive("Hello")), ImmutableList.nil()));
    testScript("\"\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0\"", new Script(ImmutableList.list(new Directive("\\n\\r\\t\\v\\b\\f\\\\\\'\\\"\\0")), ImmutableList.nil()));
    testScript("\"\\u0061\"", new Script(ImmutableList.list(new Directive("\\u0061")), ImmutableList.nil()));
    testScript("\"\\x61\"", new Script(ImmutableList.list(new Directive("\\x61")), ImmutableList.nil()));
    testScript("\"Hello\\nworld\"", new Script(ImmutableList.list(new Directive("Hello\\nworld")), ImmutableList.nil()));
    testScript("\"Hello\\\nworld\"", new Script(ImmutableList.list(new Directive("Hello\\\nworld")), ImmutableList.nil()));
    testScript("\"Hello\\02World\"", new Script(ImmutableList.list(new Directive("Hello\\02World")), ImmutableList.nil()));
    testScript("\"Hello\\012World\"", new Script(ImmutableList.list(new Directive("Hello\\012World")), ImmutableList.nil()));
    testScript("\"Hello\\122World\"", new Script(ImmutableList.list(new Directive("Hello\\122World")), ImmutableList.nil()));
    testScript("\"Hello\\0122World\"", new Script(ImmutableList.list(new Directive("Hello\\0122World")), ImmutableList.nil()));
    testScript("\"Hello\\312World\"", new Script(ImmutableList.list(new Directive("Hello\\312World")), ImmutableList.nil()));
    testScript("\"Hello\\412World\"", new Script(ImmutableList.list(new Directive("Hello\\412World")), ImmutableList.nil()));
    testScript("\"Hello\\712World\"", new Script(ImmutableList.list(new Directive("Hello\\712World")), ImmutableList.nil()));
    testScript("\"Hello\\0World\"", new Script(ImmutableList.list(new Directive("Hello\\0World")), ImmutableList.nil()));
    testScript("\"Hello\\\r\nworld\"", new Script(ImmutableList.list(new Directive("Hello\\\r\nworld")), ImmutableList.nil()));
    testScript("\"Hello\\1World\"", new Script(ImmutableList.list(new Directive("Hello\\1World")), ImmutableList.nil()));
    testScript("(function () { 'use\\x20strict'; with (i); })", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use\\x20strict")), ImmutableList.list(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
    testScript("(function () { 'use\\nstrict'; with (i); })", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use\\nstrict")), ImmutableList.list(new WithStatement(new IdentifierExpression("i"), new EmptyStatement())))));
    testScript("function a() {'use strict';return 0;};", new FunctionDeclaration(new BindingIdentifier("a"), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use strict")), ImmutableList.list(new ReturnStatement(Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("(function() {'use strict';return 0;});", new FunctionExpression(Maybe.nothing(), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use strict")), ImmutableList.list(new ReturnStatement(Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("(function a() {'use strict';return 0;});", new FunctionExpression(Maybe.just(new BindingIdentifier("a")), false, new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use strict")), ImmutableList.list(new ReturnStatement(Maybe.just(new LiteralNumericExpression(0.0)))))));
    testScript("\"use strict\" + 0", new BinaryExpression(BinaryOperator.Plus, new LiteralStringExpression("\"use strict\""), new LiteralNumericExpression(0.0)));
    testModule("\"use strict\";", new Module(ImmutableList.list(new Directive("use strict")), ImmutableList.nil()));
  }
}
