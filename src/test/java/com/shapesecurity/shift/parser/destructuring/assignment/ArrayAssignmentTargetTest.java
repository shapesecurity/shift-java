package com.shapesecurity.shift.parser.destructuring.assignment;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ArrayAssignmentTargetTest extends ParserTestCase {
    @Test
    public void testArrayAssignmentTarget() throws JsError {
        testScript("[x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[x,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[x,,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x")), Maybe.nothing()), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[[x]] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[x, y, ...z] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x")), Maybe.just(new BindingIdentifier("y"))), Maybe.just(new BindingIdentifier("z"))), new LiteralNumericExpression(0.0)));
        testScript("[, x,,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.nothing(), Maybe.just(new BindingIdentifier("x")), Maybe.nothing()), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[...[x]] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.nil(), Maybe.just(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()))), new LiteralNumericExpression(0.0)));
        testScript("[x, ...{0: y}] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.just(new ObjectAssignmentTarget(ImmutableList.list(new AssignmentTargetPropertyProperty(new StaticPropertyName("0"), new BindingIdentifier("y")))))), new LiteralNumericExpression(0.0)));
        testScript("[x, x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x")), Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[x, ...x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.just(new BindingIdentifier("x"))), new LiteralNumericExpression(0.0)));
        testScript("[x.a=a] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new AssignmentTargetWithDefault(new StaticMemberAssignmentTarget("a", new IdentifierExpression("x")), new IdentifierExpression("a")))), Maybe.nothing()), new IdentifierExpression("b")));
        testScript("[x[a]=a] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new AssignmentTargetWithDefault(new ComputedMemberAssignmentTarget(new IdentifierExpression("a"), new IdentifierExpression("x")), new IdentifierExpression("a")))), Maybe.nothing()), new IdentifierExpression("b")));
        testScript("[...[...a[x]]] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.nil(), Maybe.just(new ArrayAssignmentTarget(ImmutableList.nil(), Maybe.just(new ComputedMemberAssignmentTarget(new IdentifierExpression("x"), new IdentifierExpression("a")))))), new IdentifierExpression("b")));
        testScript("[] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.nil(), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[{a=0},{a=0}] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.just(new ObjectAssignmentTarget(ImmutableList.list(new AssignmentTargetPropertyIdentifier(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0)))))), Maybe.just(new ObjectAssignmentTarget(ImmutableList.list(new AssignmentTargetPropertyIdentifier(new BindingIdentifier("a"), Maybe.just(new LiteralNumericExpression(0.0))))))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
        testScript("[,...a]=0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(Maybe.nothing()), Maybe.just(new BindingIdentifier("a"))), new LiteralNumericExpression(0.0)));

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
