package com.shapesecurity.shift.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class InteractionsTest extends ParserTestCase {
    @Test
    public void testInteractions() throws JsError {
        testScript("0 .toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("0.0.toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("0..toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("01.toString", new StaticMemberExpression(new LiteralNumericExpression(1.0), "toString"));

        testScript("a.b(b, c)", new CallExpression(new StaticMemberExpression(new IdentifierExpression("a"), "b"),
                ImmutableList.list(new IdentifierExpression("b"), new IdentifierExpression("c"))));

        testScript("a[b](b,c)", new CallExpression(new ComputedMemberExpression(new IdentifierExpression("a"),
                new IdentifierExpression("b")), ImmutableList.list(new IdentifierExpression("b"),
                new IdentifierExpression("c"))));

        testScript("new foo().bar()", new CallExpression(new StaticMemberExpression(new NewExpression(
                new IdentifierExpression("foo"), ImmutableList.nil()), "bar"), ImmutableList.nil()));

        testScript("new foo[bar]", new NewExpression(new ComputedMemberExpression(new IdentifierExpression("foo"),
                new IdentifierExpression("bar")), ImmutableList.nil()));

        testScript("new foo.bar()", new NewExpression(new StaticMemberExpression(new IdentifierExpression("foo"), "bar"),
                ImmutableList.nil()));

        testScript("(new foo).bar()", new CallExpression(new StaticMemberExpression(new NewExpression(
                new IdentifierExpression("foo"), ImmutableList.nil()), "bar"), ImmutableList.nil()));

        testScript("a[0].b", new StaticMemberExpression(new ComputedMemberExpression(
                new IdentifierExpression("a"), new LiteralNumericExpression(0.0)), "b"));

        testScript("a(0).b", new StaticMemberExpression(new CallExpression(new IdentifierExpression("a"),
                ImmutableList.list(new LiteralNumericExpression(0.0))), "b"));

        testScript("a(0).b(14, 3, 77).c", new StaticMemberExpression(new CallExpression(new StaticMemberExpression(
                new CallExpression(new IdentifierExpression("a"), ImmutableList.list(new LiteralNumericExpression(0.0))), "b"),
                ImmutableList.list(new LiteralNumericExpression(14.0), new LiteralNumericExpression(3.0),
                        new LiteralNumericExpression(77.0))), "c"));

        testScript("a.b.c(2014)", new CallExpression(new StaticMemberExpression(new StaticMemberExpression(
                new IdentifierExpression("a"), "b"), "c"), ImmutableList.list(new LiteralNumericExpression(2014.0))));

        testScript("a || b && c | d ^ e & f == g < h >>> i + j * k", new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.LogicalOr, new BinaryExpression(new IdentifierExpression("b"), BinaryOperator.LogicalAnd,
                new BinaryExpression(new IdentifierExpression("c"), BinaryOperator.BitwiseOr, new BinaryExpression(
                        new IdentifierExpression("d"), BinaryOperator.BitwiseXor, new BinaryExpression(
                        new IdentifierExpression("e"), BinaryOperator.BitwiseAnd, new BinaryExpression(new IdentifierExpression("f"), BinaryOperator.Equal,
                        new BinaryExpression(new IdentifierExpression("g"), BinaryOperator.LessThan, new BinaryExpression(
                                new IdentifierExpression("h"), BinaryOperator.UnsignedRight, new BinaryExpression(
                                new IdentifierExpression("i"), BinaryOperator.Plus, new BinaryExpression(new IdentifierExpression("j"), BinaryOperator.Mul,
                                new IdentifierExpression("k"))))))))))));

        testScript("//\n;a;", new Script(ImmutableList.nil(), ImmutableList.list(new EmptyStatement(),
                new ExpressionStatement(new IdentifierExpression("a")))));

        testScript("/* block comment */ 0", new LiteralNumericExpression(0.0));

        testScript("0 /* block comment 1 */ /* block comment 2 */", new Script(ImmutableList.nil(), ImmutableList.list(
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testScript("(a + /* assignment */b ) * c", new BinaryExpression(new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.Plus, new IdentifierExpression("b")), BinaryOperator.Mul,
                new IdentifierExpression("c")));

        testScript("/* assignment */\n a = b", new AssignmentExpression(new AssignmentTargetIdentifier("a"),
                new IdentifierExpression("b")));

        testScript("0 /*The*/ /*Answer*/", new LiteralNumericExpression(0.0));

        testScript("0 /*the*/ /*answer*/", new LiteralNumericExpression(0.0));

        testScript("0 /* the * answer */", new LiteralNumericExpression(0.0));

        testScript("0 /* The * answer */", new LiteralNumericExpression(0.0));

        testScript("/* multiline\ncomment\nshould\nbe\nignored */ 0", new LiteralNumericExpression(0.0));

        testScript("/*a\r\nb*/ 0", new LiteralNumericExpression(0.0));

        testScript("/*a\rb*/ 0", new LiteralNumericExpression(0.0));

        testScript("/*a\nb*/ 0", new LiteralNumericExpression(0.0));

        testScript("/*a\nc*/ 0", new LiteralNumericExpression(0.0));

        testScript("// line comment\n0", new LiteralNumericExpression(0.0));

        testScript("0 // line comment", new LiteralNumericExpression(0.0));

        testScript("// Hello, world!\n0", new LiteralNumericExpression(0.0));

        testScript("//\n0", new LiteralNumericExpression(0.0));

        testScript("/**/0", new LiteralNumericExpression(0.0));

        testScript("0/**/", new LiteralNumericExpression(0.0));

        testScript("// Hello, world!\n\n//   Another hello\n0", new LiteralNumericExpression(0.0));

        testScript("/**/0", new LiteralNumericExpression(0.0));

        testScript("0/**/", new LiteralNumericExpression(0.0));

        testScript("// Hello, world!\n");

        testScript("// Hallo, world!\n");

        testScript("//");

        testScript("// ");

        testScript("if (x) { doThat() // Some comment\n }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.nil()))))), Maybe.nothing()));

        testScript("if (x) { // Some comment\ndoThat(); }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.nil()))))), Maybe.nothing()));

        testScript("if (x) { /* Some comment */ doThat() }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.nil()))))), Maybe.nothing()));

        testScript("if (x) { doThat() /* Some comment */ }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.list(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.nil()))))), Maybe.nothing()));

        testScript("switch (answer) { case 0: /* perfect */ bingo() }", new SwitchStatement(
                new IdentifierExpression("answer"), ImmutableList.list(new SwitchCase(new LiteralNumericExpression(0.0),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("bingo"),
                        ImmutableList.nil())))))));

        testScript("switch (answer) { case 0: bingo() /* perfect */ }", new SwitchStatement(
                new IdentifierExpression("answer"), ImmutableList.list(new SwitchCase(new LiteralNumericExpression(0.0),
                ImmutableList.list(new ExpressionStatement(new CallExpression(new IdentifierExpression("bingo"),
                        ImmutableList.nil())))))));

        testScript("/* header */ (function(){ var version = 1; }).call(this)", new CallExpression(
                new StaticMemberExpression(new FunctionExpression(false, Maybe.nothing(), new FormalParameters(
                        ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(), ImmutableList.list(
                        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(
                                new VariableDeclarator(new BindingIdentifier("version"), Maybe.just(
                                        new LiteralNumericExpression(1.0))))))))), "call"), ImmutableList.list(new ThisExpression())));

        testScript("(function(){ var version = 1; /* sync */ }).call(this)", new CallExpression(new StaticMemberExpression(
                new FunctionExpression(false, Maybe.nothing(), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(
                                new BindingIdentifier("version"), Maybe.just(new LiteralNumericExpression(1.0))))))))), "call"),
                ImmutableList.list(new ThisExpression())));

        testScript("function f() { /* infinite */ while (true) { } /* bar */ var each; }", new FunctionDeclaration(
                false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.nil(), Maybe.nothing()),
                new FunctionBody(ImmutableList.nil(), ImmutableList.list(new WhileStatement(new LiteralBooleanExpression(true),
                        new BlockStatement(new Block(ImmutableList.nil()))), new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.list(new VariableDeclarator(
                                new BindingIdentifier("each"), Maybe.nothing()))))))));

        testScript("while (i-->0) {}", new WhileStatement(new BinaryExpression(
                new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("i")), BinaryOperator.GreaterThan,
                new LiteralNumericExpression(0.0)), new BlockStatement(new Block(ImmutableList.nil()))));

        testScript("var x = 1<!--foo", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.list(new VariableDeclarator(new BindingIdentifier("x"), Maybe.just(
                        new LiteralNumericExpression(1.0)))))));

        testScript("/* not comment*/; i-->0", new Script(ImmutableList.nil(), ImmutableList.list(new EmptyStatement(),
                new ExpressionStatement(new BinaryExpression(new UpdateExpression(false,
                        UpdateOperator.Decrement, new AssignmentTargetIdentifier("i")), BinaryOperator.GreaterThan, new LiteralNumericExpression(0.0))))));

        testScript("class A extends B { a() { [super.b] = c } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.list(
                        Maybe.just(new StaticMemberAssignmentTarget(new Super(), "b"))), Maybe.nothing()),
                        new IdentifierExpression("c"))))))))));

        testScript("class A extends B { a() { ({b: super[c]}) = d } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.just(new IdentifierExpression("B")), ImmutableList.list(new ClassElement(false, new Method(false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.nil(), Maybe.nothing()), new FunctionBody(ImmutableList.nil(),
                ImmutableList.list(new ExpressionStatement(new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.list(
                        new AssignmentTargetPropertyProperty(new StaticPropertyName("b"), new ComputedMemberAssignmentTarget(
                                new Super(), new IdentifierExpression("c"))))), new IdentifierExpression("d")))))
                )))));

        testScriptFailure("0.toString", 2, "Unexpected \"t\"");
    }
}
