package com.shapesecurity.shift.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ArrowExpressionTest extends ParserTestCase {
  @Test
  public void testArrowExpression() throws JsError {
    testScript("(()=>0)", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("() => 0", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("(...a) => 0", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.just(new BindingIdentifier("a"))), new LiteralNumericExpression(0.0)));
    testScript("() => {}", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.nil())));
    testScript("(a) => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a")), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("([a]) => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("a"))), Maybe.nothing())), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("a => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a")), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("({a}) => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.nothing())))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("() => () => 0", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new LiteralNumericExpression(0.0))));
    testScript("() => 0, 1", new BinaryExpression(BinaryOperator.Sequence, new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new LiteralNumericExpression(0.0)), new LiteralNumericExpression(1.0)));
    testScript("() => 0 + 1", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(0.0), new LiteralNumericExpression(1.0))));
    testScript("(a,b) => 0 + 1", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.nothing()), new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(0.0), new LiteralNumericExpression(1.0))));
    testScript("(a,b,...c) => 0 + 1", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.just(new BindingIdentifier("c"))), new BinaryExpression(BinaryOperator.Plus, new LiteralNumericExpression(0.0), new LiteralNumericExpression(1.0))));
    testScript("() => (a) = 0", new ArrowExpression(new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new AssignmentExpression(new BindingIdentifier("a"), new LiteralNumericExpression(0.0))));
    testScript("a => b => c => 0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a")), Maybe.nothing()), new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("b")), Maybe.nothing()), new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("c")), Maybe.nothing()), new LiteralNumericExpression(0.0)))));
    testScript("(x)=>{'use strict';}", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("x")), Maybe.nothing()), new FunctionBody(ImmutableList.list(new Directive("use strict")), ImmutableList.nil())));
    testScript("eval => 'use strict'", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("eval")), Maybe.nothing()), new LiteralStringExpression("use strict")));
    testScript("'use strict';(x)=>0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("x")), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("({x=0}, {})=>0", new ArrowExpression(new FormalParameters(ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.nil())), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("([x=0], [])=>0", new ArrowExpression(new FormalParameters(ImmutableList.list(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingWithDefault(new BindingIdentifier("x"), new LiteralNumericExpression(0.0)))), Maybe.nothing()), new ArrayBinding(ImmutableList.nil(), Maybe.nothing())), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("(a, {x = 0})=>0", new ArrowExpression(new FormalParameters(ImmutableList.list(new BindingIdentifier("a"), new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
    testScript("({x = 0}, {y = 0}, {z = 0})=>0", new ArrowExpression(new FormalParameters(ImmutableList.list(new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.just(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("y"), Maybe.just(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.list(new BindingPropertyIdentifier(new BindingIdentifier("z"), Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.nothing()), new LiteralNumericExpression(0.0)));

    testScriptFailure("[]=>0", 2, "Unexpected token \"=>\"");
    testScriptFailure("() + 1", 3, "Unexpected token \"+\"");
    testScriptFailure("1 + ()", 6, "Unexpected end of input");
    testScriptFailure("1 + ()", 6, "Unexpected end of input");
    testScriptFailure("(a)\n=> 0", 2, 0, 4, "Unexpected token \"=>\"");
    testScriptFailure("a\n=> 0", 2, 0, 2, "Unexpected token \"=>\"");
    testScriptFailure("((a)) => 1", 0, "Illegal arrow function parameter list");
    testScriptFailure("((a),...a) => 1", 5, "Unexpected token \"...\"");
    testScriptFailure("(a,...a)", 8, "Unexpected end of input");
    testScriptFailure("(a,...a)\n", 2, 0, 9, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\r\n*/ => 0", 2, 3, 15, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\u2028*/ => 0", 2, 3, 14, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\u2029*/ => 0", 2, 3, 14, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\n*/ => 0", 2, 3, 14, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\r*/ => 0", 2, 3, 14, "Illegal newline after arrow parameters");
    testScriptFailure("(a,...a)/*\u202a*/", 13, "Unexpected end of input");
    testScriptFailure("() <= 0", 3, "Unexpected token \"<=\"");
    testScriptFailure("() ? 0", 3, "Unexpected token \"?\"");
    testScriptFailure("() + 0", 3, "Unexpected token \"+\"");
    testScriptFailure("(10) => 0", 0, "Illegal arrow function parameter list");
    testScriptFailure("(10, 0, 20) => 0", 0, "Illegal arrow function parameter list");
  }
}
