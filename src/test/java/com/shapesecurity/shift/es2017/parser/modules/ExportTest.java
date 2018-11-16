package com.shapesecurity.shift.es2017.parser.modules;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.ArrayBinding;
import com.shapesecurity.shift.es2017.ast.BinaryExpression;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.EmptyStatement;
import com.shapesecurity.shift.es2017.ast.ExportAllFrom;
import com.shapesecurity.shift.es2017.ast.ExportDefault;
import com.shapesecurity.shift.es2017.ast.ExportFromSpecifier;
import com.shapesecurity.shift.es2017.ast.ExpressionStatement;
import com.shapesecurity.shift.es2017.ast.FormalParameters;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.Export;
import com.shapesecurity.shift.es2017.ast.ExportFrom;
import com.shapesecurity.shift.es2017.ast.ExportLocalSpecifier;
import com.shapesecurity.shift.es2017.ast.ExportLocals;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarator;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ExportTest extends ParserTestCase {
    @Test
    public void testExport() throws JsError {
        testModule("export * from 'a'", new ExportAllFrom("a"));

        testModule("export {} from 'a'", new ExportFrom(ImmutableList.empty(), "a"));

        testModule("export {a} from 'a'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.empty())),
                "a"));

        testModule("export {a,} from 'a'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.empty())),
                "a"));

        testModule("export {a,b} from 'a'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.empty()),
                new ExportFromSpecifier("b", Maybe.empty())), "a"));

        testModule("export {a as b} from 'a'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.of("b"))),
                "a"));

        testModule("export {as as as} from 'as'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier(
                "as", Maybe.of("as"))), "as"));

        testModule("export {as as function} from 'as'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier(
                "as", Maybe.of("function"))), "as"));

        testModule("export {a} from 'm'", new ExportFrom(ImmutableList.of(new ExportFromSpecifier("a", Maybe.empty())),
                "m"));

        testModule("export {if as var} from 'a';", new ExportFrom(ImmutableList.of(new ExportFromSpecifier(
                "if", Maybe.of("var"))), "a"));

        testModule("export {a}\n var a;", new ExportLocals(ImmutableList.of(new ExportLocalSpecifier(new IdentifierExpression("a"), Maybe.empty()))));

        testModule("export {a,}\n var a;", new ExportLocals(ImmutableList.of(new ExportLocalSpecifier(new IdentifierExpression("a"), Maybe.empty()))));

        testModule("export {a,b,}\n var a,b;", new ExportLocals(ImmutableList.of(new ExportLocalSpecifier(new IdentifierExpression("a"), Maybe.empty()),
                new ExportLocalSpecifier(new IdentifierExpression("b"), Maybe.empty()))));

        testModule("export var a = 0, b;", new Export(new VariableDeclaration(
			VariableDeclarationKind.Var,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(
                        new LiteralNumericExpression(0.0))), new VariableDeclarator(new BindingIdentifier("b"), Maybe.empty())))));

        testModule("export const a = 0, b = 0;", new Export(new VariableDeclaration(VariableDeclarationKind.Const,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(
                        new LiteralNumericExpression(0.0))), new VariableDeclarator(new BindingIdentifier("b"),
                        Maybe.of(new LiteralNumericExpression(0.0)))))));

        testModule("export let a = 0, b = 0;", new Export(new VariableDeclaration(VariableDeclarationKind.Let,
                ImmutableList.of(new VariableDeclarator(new BindingIdentifier("a"), Maybe.of(
                        new LiteralNumericExpression(0.0))), new VariableDeclarator(new BindingIdentifier("b"), Maybe.of(
                        new LiteralNumericExpression(0.0)))))));

        testModule("export let[a] = 0;", new Export(new VariableDeclaration(VariableDeclarationKind.Let, ImmutableList.of(
                new VariableDeclarator(new ArrayBinding(ImmutableList.of(Maybe.of(new BindingIdentifier("a"))),
                        Maybe.empty()), Maybe.of(new LiteralNumericExpression(0.0)))))));

        testModule("export class A{} /* no semi */ false", new Export(new ClassDeclaration(new BindingIdentifier("A"),
                Maybe.empty(), ImmutableList.empty())));

        testModule("export function A(){} /* no semi */ false", new Export(new FunctionDeclaration(false, false, new BindingIdentifier("A"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default function (){} /* no semi */ false", new ExportDefault(new FunctionDeclaration(false, false,
                new BindingIdentifier("*default*"), new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))));

        testModule("export default class {} /* no semi */ false", new ExportDefault(new ClassDeclaration(
                new BindingIdentifier("*default*"), Maybe.empty(), ImmutableList.empty())));

        testModule("export default 3 + 1", new ExportDefault(new BinaryExpression(
                new LiteralNumericExpression(3.0), BinaryOperator.Plus, new LiteralNumericExpression(1.0))));

        testModule("export default a", new ExportDefault(new IdentifierExpression("a")));

        testModule("export default function a(){}", new ExportDefault(new FunctionDeclaration(false, false, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default class a{}", new ExportDefault(new ClassDeclaration(new BindingIdentifier("a"),
                Maybe.empty(), ImmutableList.empty())));

        testModule("export default function* a(){}", new ExportDefault(new FunctionDeclaration(false, true, new BindingIdentifier("a"),
                new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default 0;0", new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(
                new LiteralNumericExpression(0.0)), new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export function f(){};0", new Module(ImmutableList.empty(), ImmutableList.of(new Export(
                        new FunctionDeclaration(false, false, new BindingIdentifier("f"), new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))), new EmptyStatement(),
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export class A{};0", new Module(ImmutableList.empty(), ImmutableList.of(new Export(new ClassDeclaration(
                        new BindingIdentifier("A"), Maybe.empty(), ImmutableList.empty())), new EmptyStatement(),
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export {};0", new Module(ImmutableList.empty(), ImmutableList.of(new ExportLocals(ImmutableList.empty()),
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testScriptFailure("export * from \"a\"", 0, "Unexpected token \"export\"");
        testModuleFailure("{export default 3;}", 1, "Unexpected token \"export\"");
        testModuleFailure("{export {a};}", 1, "Unexpected token \"export\"");
        testModuleFailure("while (1) export default 3", 10, "Unexpected token \"export\"");
        testModuleFailure("export", 6, "Unexpected end of input");
        testModuleFailure("export ", 7, "Unexpected end of input");
        testModuleFailure("export;", 6, "Unexpected token \";\"");
        testModuleFailure("export {,,}", 8, "Unexpected token \",\"");
        testModuleFailure("export {a,,}", 10, "Unexpected token \",\"");
        testModuleFailure("export {a,,b}", 10, "Unexpected token \",\"");
        testModuleFailure("export {a,b} from", 17, "Unexpected end of input");
        testModuleFailure("export {a,b} from a", 18, "Unexpected identifier");
        testModuleFailure("export {a as} from a", 12, "Unexpected token \"}\"");
        testModuleFailure("export {as b} from a", 11, "Unexpected identifier");
        testModuleFailure("export * from a", 14, "Unexpected identifier");
        testModuleFailure("export / from a", 7, "Unexpected token \"/\"");
        testModuleFailure("export * From \"a\"", 9, "Unexpected identifier");
        testModuleFailure("export let[a] = 0 export let[b] = 0", 18, "Unexpected token \"export\"");
        testModuleFailure("export 3", 7, "Unexpected number");
        testModuleFailure("export function () {}", 16, "Unexpected token \"(\"");
        testModuleFailure("export default default", 15, "Unexpected token \"default\"");
        testModuleFailure("export default function", 23, "Unexpected end of input");
    }
}
