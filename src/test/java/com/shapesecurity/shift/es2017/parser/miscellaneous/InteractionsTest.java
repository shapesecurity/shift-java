package com.shapesecurity.shift.es2017.parser.miscellaneous;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrowExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentExpression;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetPropertyProperty;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Block;
import com.shapesecurity.shift.es2017.ast.BlockStatement;
import com.shapesecurity.shift.es2017.ast.CallExpression;
import com.shapesecurity.shift.es2017.ast.CompoundAssignmentExpression;
import com.shapesecurity.shift.es2017.ast.ComputedMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.IfStatement;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.NewExpression;
import com.shapesecurity.shift.es2017.ast.ObjectAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.StaticMemberAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.StaticPropertyName;
import com.shapesecurity.shift.es2017.ast.Super;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.WhileStatement;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es2017.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2017.ast.ArrayAssignmentTarget;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.ClassElement;
import com.shapesecurity.shift.es2017.ast.ComputedMemberExpression;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.LiteralBooleanExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNullExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.ast.SwitchCase;
import com.shapesecurity.shift.es2017.ast.SwitchStatement;
import com.shapesecurity.shift.es2017.ast.ThisExpression;
import com.shapesecurity.shift.es2017.ast.UpdateExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class InteractionsTest extends ParserTestCase {
    @Test
    public void testInteractions() throws JsError {
        testScript("0 .toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("0.0.toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("0..toString", new StaticMemberExpression(new LiteralNumericExpression(0.0), "toString"));

        testScript("01.toString", new StaticMemberExpression(new LiteralNumericExpression(1.0), "toString"));

        testScript("a.b(b, c)", new CallExpression(new StaticMemberExpression(new IdentifierExpression("a"), "b"),
                ImmutableList.of(new IdentifierExpression("b"), new IdentifierExpression("c"))));

        testScript("a[b](b,c)", new CallExpression(new ComputedMemberExpression(new IdentifierExpression("a"),
                new IdentifierExpression("b")), ImmutableList.of(new IdentifierExpression("b"),
                new IdentifierExpression("c"))));

        testScript("new foo().bar()", new CallExpression(new StaticMemberExpression(new NewExpression(
                new IdentifierExpression("foo"), ImmutableList.empty()), "bar"), ImmutableList.empty()));

        testScript("new foo[bar]", new NewExpression(new ComputedMemberExpression(new IdentifierExpression("foo"),
                new IdentifierExpression("bar")), ImmutableList.empty()));

        testScript("new foo.bar()", new NewExpression(new StaticMemberExpression(new IdentifierExpression("foo"), "bar"),
                ImmutableList.empty()));

        testScript("(new foo).bar()", new CallExpression(new StaticMemberExpression(new NewExpression(
                new IdentifierExpression("foo"), ImmutableList.empty()), "bar"), ImmutableList.empty()));

        testScript("a[0].b", new StaticMemberExpression(new ComputedMemberExpression(
                new IdentifierExpression("a"), new LiteralNumericExpression(0.0)), "b"));

        testScript("a(0).b", new StaticMemberExpression(new CallExpression(new IdentifierExpression("a"),
                ImmutableList.of(new LiteralNumericExpression(0.0))), "b"));

        testScript("a(0).b(14, 3, 77).c", new StaticMemberExpression(new CallExpression(new StaticMemberExpression(
                new CallExpression(new IdentifierExpression("a"), ImmutableList.of(new LiteralNumericExpression(0.0))), "b"),
                ImmutableList.of(new LiteralNumericExpression(14.0), new LiteralNumericExpression(3.0),
                        new LiteralNumericExpression(77.0))), "c"));

        testScript("a.b.c(2014)", new CallExpression(new StaticMemberExpression(new StaticMemberExpression(
                new IdentifierExpression("a"), "b"), "c"), ImmutableList.of(new LiteralNumericExpression(2014.0))));

        testScript("a || b && c | d ^ e & f == g < h >>> i + j * k", new BinaryExpression(
                new IdentifierExpression("a"), BinaryOperator.LogicalOr, new BinaryExpression(new IdentifierExpression("b"), BinaryOperator.LogicalAnd,
                new BinaryExpression(new IdentifierExpression("c"), BinaryOperator.BitwiseOr, new BinaryExpression(
                        new IdentifierExpression("d"), BinaryOperator.BitwiseXor, new BinaryExpression(
                        new IdentifierExpression("e"), BinaryOperator.BitwiseAnd, new BinaryExpression(new IdentifierExpression("f"), BinaryOperator.Equal,
                        new BinaryExpression(new IdentifierExpression("g"), BinaryOperator.LessThan, new BinaryExpression(
                                new IdentifierExpression("h"), BinaryOperator.UnsignedRight, new BinaryExpression(
                                new IdentifierExpression("i"), BinaryOperator.Plus, new BinaryExpression(new IdentifierExpression("j"), BinaryOperator.Mul,
                                new IdentifierExpression("k"))))))))))));

        testScript("//\n;a;", new Script(ImmutableList.empty(), ImmutableList.of(new EmptyStatement(),
                new ExpressionStatement(new IdentifierExpression("a")))));

        testScript("/* block comment */ 0", new LiteralNumericExpression(0.0));

        testScript("0 /* block comment 1 */ /* block comment 2 */", new Script(ImmutableList.empty(), ImmutableList.of(
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
                new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.empty()))))), Maybe.empty()));

        testScript("if (x) { // Some comment\ndoThat(); }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.empty()))))), Maybe.empty()));

        testScript("if (x) { /* Some comment */ doThat() }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.empty()))))), Maybe.empty()));

        testScript("if (x) { doThat() /* Some comment */ }", new IfStatement(new IdentifierExpression("x"),
                new BlockStatement(new Block(ImmutableList.of(new ExpressionStatement(new CallExpression(
                        new IdentifierExpression("doThat"), ImmutableList.empty()))))), Maybe.empty()));

        testScript("switch (answer) { case 0: /* perfect */ bingo() }", new SwitchStatement(
                new IdentifierExpression("answer"), ImmutableList.of(new SwitchCase(new LiteralNumericExpression(0.0),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("bingo"),
                        ImmutableList.empty())))))));

        testScript("switch (answer) { case 0: bingo() /* perfect */ }", new SwitchStatement(
                new IdentifierExpression("answer"), ImmutableList.of(new SwitchCase(new LiteralNumericExpression(0.0),
                ImmutableList.of(new ExpressionStatement(new CallExpression(new IdentifierExpression("bingo"),
                        ImmutableList.empty())))))));

        testScript("/* header */ (function(){ var version = 1; }).call(this)", new CallExpression(
                new StaticMemberExpression(new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(
                        ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.of(
                        new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(
                                new VariableDeclarator(new BindingIdentifier("version"), Maybe.of(
                                        new LiteralNumericExpression(1.0))))))))), "call"), ImmutableList.of(new ThisExpression())));

        testScript("(function(){ var version = 1; /* sync */ }).call(this)", new CallExpression(new StaticMemberExpression(
                new FunctionExpression(false, false, Maybe.empty(), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(
                                new BindingIdentifier("version"), Maybe.of(new LiteralNumericExpression(1.0))))))))), "call"),
                ImmutableList.of(new ThisExpression())));

        testScript("function f() { /* infinite */ while (true) { } /* bar */ var each; }", new FunctionDeclaration(
                false, false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new WhileStatement(new LiteralBooleanExpression(true),
                        new BlockStatement(new Block(ImmutableList.empty()))), new VariableDeclarationStatement(
                        new VariableDeclaration(VariableDeclarationKind.Var, ImmutableList.of(new VariableDeclarator(
                                new BindingIdentifier("each"), Maybe.empty()))))))));

        testScript("while (i-->0) {}", new WhileStatement(new BinaryExpression(
                new UpdateExpression(false, UpdateOperator.Decrement, new AssignmentTargetIdentifier("i")), BinaryOperator.GreaterThan,
                new LiteralNumericExpression(0.0)), new BlockStatement(new Block(ImmutableList.empty()))));

        testScript("var x = 1<!--foo", new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("x"), Maybe.of(
                        new LiteralNumericExpression(1.0)))))));

        testScript("/* not comment*/; i-->0", new Script(ImmutableList.empty(), ImmutableList.of(new EmptyStatement(),
                new ExpressionStatement(new BinaryExpression(new UpdateExpression(false,
                        UpdateOperator.Decrement, new AssignmentTargetIdentifier("i")), BinaryOperator.GreaterThan, new LiteralNumericExpression(0.0))))));

        testScript("class A extends B { a() { [super.b] = c } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new ArrayAssignmentTarget(ImmutableList.of(
                        Maybe.of(new StaticMemberAssignmentTarget(new Super(), "b"))), Maybe.empty()),
                        new IdentifierExpression("c"))))))))));

        testScript("class A extends B { a() { ({b: super[c]} = d) } }", new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.of(new IdentifierExpression("B")), ImmutableList.of(new ClassElement(false, new Method(false, false, new StaticPropertyName("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.of(new ExpressionStatement(new AssignmentExpression(new ObjectAssignmentTarget(ImmutableList.of(
                        new AssignmentTargetPropertyProperty(new StaticPropertyName("b"), new ComputedMemberAssignmentTarget(
                                new Super(), new IdentifierExpression("c"))))), new IdentifierExpression("d")))))
                )))));

        // Consise arrow bodies may contain yield as an identifier even in generators.
        testScript("function* f(){ () => yield; }", new FunctionDeclaration(false, true, new BindingIdentifier("f"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.of(new ExpressionStatement(
                        new ArrowExpression(false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new IdentifierExpression("yield"))
                )))));

        // CompoundAssignmentExpressions are not valid binding targets
        testScript("null && (x += null)", new ExpressionStatement(new BinaryExpression(
                new LiteralNullExpression(),
                BinaryOperator.LogicalAnd,
                new CompoundAssignmentExpression(new AssignmentTargetIdentifier("x"), CompoundAssignmentOperator.AssignPlus, new LiteralNullExpression())
                )));

        testScriptFailure("0.toString", 2, "Unexpected \"t\"");
    }
}
