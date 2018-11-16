package com.shapesecurity.shift.es2017.parser.destructuring.assignment;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetPropertyIdentifier;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetPropertyProperty;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetWithDefault;
import com.shapesecurity.shift.es2017.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.ObjectAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ArrayAssignmentTargetTest extends ParserTestCase {
    @Test
    public void testArrayAssignmentTarget() throws JsError {
        testScript("[x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x,,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x")), Maybe.empty()), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[[x]] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.empty()))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x, y, ...z] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x")), Maybe.of(new AssignmentTargetIdentifier("y"))), Maybe.of(new AssignmentTargetIdentifier("z"))), new LiteralNumericExpression(0.0)));
        testScript("[, x,,] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.empty(), Maybe.of(new AssignmentTargetIdentifier("x")), Maybe.empty()), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[...[x]] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.empty(), Maybe.of(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.empty()))), new LiteralNumericExpression(0.0)));
        testScript("[x, ...{0: y}] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.of(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyProperty(new StaticPropertyName("0"), new AssignmentTargetIdentifier("y")))))), new LiteralNumericExpression(0.0)));
        testScript("[x, x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x")), Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[x, ...x] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("x"))), Maybe.of(new AssignmentTargetIdentifier("x"))), new LiteralNumericExpression(0.0)));
        testScript("[x.a=a] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetWithDefault(new StaticMemberAssignmentTarget(new IdentifierExpression("x"), "a"), new IdentifierExpression("a")))), Maybe.empty()), new IdentifierExpression("b")));
        testScript("[x[a]=a] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetWithDefault(new ComputedMemberAssignmentTarget(new IdentifierExpression("x"), new IdentifierExpression("a")), new IdentifierExpression("a")))), Maybe.empty()), new IdentifierExpression("b")));
        testScript("[...[...a[x]]] = b", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.empty(), Maybe.of(new ArrayAssignmentTarget(ImmutableList.empty(), Maybe.of(new ComputedMemberAssignmentTarget(new IdentifierExpression("a"), new IdentifierExpression("x")))))), new IdentifierExpression("b")));
        testScript("[] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.empty(), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[{a=0},{a=0}] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0)))))), Maybe.of(new ObjectAssignmentTarget(ImmutableList.of(new AssignmentTargetPropertyIdentifier(new AssignmentTargetIdentifier("a"), Maybe.of(new LiteralNumericExpression(0.0))))))), Maybe.empty()), new LiteralNumericExpression(0.0)));
        testScript("[,...a]=0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.empty()), Maybe.of(new AssignmentTargetIdentifier("a"))), new LiteralNumericExpression(0.0)));

        testScript("[(a)] = 0", new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(Maybe.of(new AssignmentTargetIdentifier("a"))), Maybe.empty()), new LiteralNumericExpression(0.0)));

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
        testScriptFailure("([a]) = 0", 6, "Invalid left-hand side in assignment");

        // TODO: new tests added
        testScriptFailure("[0] = 0", 4, "Invalid left-hand side in assignment");
        testScriptFailure("[a, ...b, {c=0}]", 11, "Illegal property initializer");
        testScriptFailure("{a = [...b, c]} = 0", 16, "Unexpected token \"=\"");
    }
}
