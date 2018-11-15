package com.shapesecurity.shift.es2017.parser.expressions;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingPropertyIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingWithDefault;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.ArrowExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2017.ast.ObjectBinding;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class ArrowExpressionTest extends ParserTestCase {
    @Test
    public void testArrowExpression() throws JsError {
        testScript("(()=>0)", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("() => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("(...a) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.of(new BindingIdentifier("a"))), new LiteralNumericExpression(0.0)));
        testScript("() => {}", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty())));
        testScript("(a) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("([a]) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("a => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({a}) => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.empty())))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("() => () => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0))));
        testScript("() => 0, 1", new BinaryExpression(new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0)), BinaryOperator.Sequence, new LiteralNumericExpression(1.0)));
        testScript("() => 0 + 1", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new BinaryExpression(new LiteralNumericExpression(0.0), BinaryOperator.Plus, new LiteralNumericExpression(1.0))));
        testScript("(a,b) => 0 + 1", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.empty()), new BinaryExpression(new LiteralNumericExpression(0.0), BinaryOperator.Plus, new LiteralNumericExpression(1.0))));
        testScript("(a,b,...c) => 0 + 1", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new BindingIdentifier("b")), Maybe.of(new BindingIdentifier("c"))), new BinaryExpression(new LiteralNumericExpression(0.0), BinaryOperator.Plus, new LiteralNumericExpression(1.0))));
        testScript("() => (a) = 0", new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new AssignmentExpression(new AssignmentTargetIdentifier("a"), new LiteralNumericExpression(0.0))));
        testScript("a => b => c => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a")), Maybe.empty()), new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("b")), Maybe.empty()), new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("c")), Maybe.empty()), new LiteralNumericExpression(0.0)))));
        testScript("(x)=>{'use strict';}", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("x")), Maybe.empty()), new FunctionBody(ImmutableList.of(new Directive("use strict")), ImmutableList.empty())));
        testScript("eval => 'use strict'", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("eval")), Maybe.empty()), new LiteralStringExpression("use strict")));
        testScript("'use strict';(x)=>0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("x")), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x=0}, {})=>0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("([x=0], [])=>0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingWithDefault(new BindingIdentifier("x"), new LiteralNumericExpression(0.0)))), Maybe.empty()), new ArrayBinding(ImmutableList.empty(), Maybe.empty())), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("(a, {x = 0})=>0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("a"), new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("({x = 0}, {y = 0}, {z = 0})=>0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("x"), Maybe.of(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("y"), Maybe.of(new LiteralNumericExpression(0.0))))), new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("z"), Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("yield => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("yield")), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("let => 0", new ArrowExpression(false, new FormalParameters(ImmutableList.of(new BindingIdentifier("let")), Maybe.empty()), new LiteralNumericExpression(0.0)));

        testScriptFailure("[]=>0", 2, "Unexpected token \"=>\"");
        testScriptFailure("() + 1", 3, "Unexpected token \"+\"");
        testScriptFailure("1 + ()", 6, "Unexpected end of input");
        testScriptFailure("1 + ()", 6, "Unexpected end of input");
        testScriptFailure("(a)\n=> 0", 2, 0, 4, "Illegal newline after arrow parameters");
        testScriptFailure("a\n=> 0", 2, 0, 2, "Illegal newline after arrow parameters");
        testScriptFailure("((a)) => 1", 0, "Illegal arrow function parameter list");
        testScriptFailure("((a),...a) => 1", 5, "Unexpected token \"...\"");
        testScriptFailure("(a,...a)", 8, "Unexpected end of input");
        testScriptFailure("(a,...a)\n", 2, 0, 9, "Unexpected end of input");
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
