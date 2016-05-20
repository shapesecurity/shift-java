package com.shapesecurity.shift.parser.destructuring.assignment;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ArrayBindingTest extends ParserTestCase {
    @Test
    public void testArrayBinding() throws JsError {
        testScript("[x] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x,] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x,,] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x")), Maybe.empty()), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[[x]] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.empty()))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x, y, ...z] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x")), Maybe.of(new BindingIdentifier("y"))), Maybe.of(new BindingIdentifier("z"))), new LiteralNumericExpression(0.0)));
        testScript("[, x,,] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.empty(), Maybe.of(new BindingIdentifier("x")), Maybe.empty()), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[...[x]] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.empty(), Maybe.of(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("[x, ...{0: y}] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyProperty(new StaticPropertyName("0"), new BindingIdentifier("y")))))), new LiteralNumericExpression(0.0)));
        testScript("[x, x] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x")), Maybe.of(new BindingIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x, ...x] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("x"))), Maybe.of(new BindingIdentifier("x"))), new LiteralNumericExpression(0.0)));
        testScript("[x.a=a] = b", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingWithDefault(new StaticMemberExpression("a", new IdentifierExpression("x")), new IdentifierExpression("a")))), Maybe.empty()), new IdentifierExpression("b")));
        testScript("[x[a]=a] = b", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingWithDefault(new ComputedMemberExpression(new IdentifierExpression("a"), new IdentifierExpression("x")), new IdentifierExpression("a")))), Maybe.empty()), new IdentifierExpression("b")));
        testScript("[...[...a[x]]] = b", new AssignmentExpression(new ArrayBinding(ImmutableList.empty(), Maybe.of(new ArrayBinding(ImmutableList.empty(), Maybe.of(new ComputedMemberExpression(new IdentifierExpression("x"), new IdentifierExpression("a")))))), new IdentifierExpression("b")));
        testScript("[] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[{a=0},{a=0}] = 0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.of(new ObjectBinding(ImmutableList.of(new BindingPropertyIdentifier(new BindingIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[,...a]=0", new AssignmentExpression(new ArrayBinding(ImmutableList.of(Maybe.empty()), Maybe.of(new BindingIdentifier("a"))), new LiteralNumericExpression(0.0)));

        testScriptFailure("[x] += 0", 4, "Invalid left-hand side in assignment");
        testScriptFailure("[, x, ...y,] = 0", 13, "Invalid left-hand side in assignment");
        testScriptFailure("[...x, ...y] = 0", 13, "Invalid left-hand side in assignment");
        testScriptFailure("[...x, y] = 0", 10, "Invalid left-hand side in assignment");
        testScriptFailure("[...x,,] = 0", 9, "Invalid left-hand side in assignment");
        testScriptFailure("[0,{a=0}] = 0", 8, "Invalid left-hand side in assignment");
        testScriptFailure("[{a=0},{b=0},0] = 0", 14, "Invalid left-hand side in assignment");
        testScriptFailure("[{a=0},...0]", 2, "Illegal property initializer");
        testScriptFailure("[...0,a]=0", 8, "Invalid left-hand side in assignment");
        testScriptFailure("[...0,{a=0}]=0", 11, "Invalid left-hand side in assignment");
        testScriptFailure("[...{a=0},]", 9, "Unexpected token \",\"");
        testScriptFailure("[...{a=0},]=0", 9, "Unexpected token \",\"");

        // TODO: new tests added
        testScriptFailure("[0] = 0", 4, "Invalid left-hand side in assignment");
        testScriptFailure("[a, ...b, {c=0}]", 11, "Illegal property initializer");
        testScriptFailure("{a = [...b, c]} = 0", 16, "Unexpected token \"=\"");
    }
}
