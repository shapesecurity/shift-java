package com.shapesecurity.shift.parser.modules;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.parser.ParserTestCase;
import com.shapesecurity.shift.parser.JsError;

import org.junit.Test;

public class ExportTest extends ParserTestCase {
    @Test
    public void testExport() throws JsError {
        testModule("export * from 'a'", new ExportAllFrom("a"));

        testModule("export {} from 'a'", new ExportFrom(ImmutableList.empty(), Maybe.of("a")));

        testModule("export {a} from 'a'", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a")),
                Maybe.of("a")));

        testModule("export {a,} from 'a'", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a")),
                Maybe.of("a")));

        testModule("export {a,b} from 'a'", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a"),
                new ExportSpecifier(Maybe.empty(), "b")), Maybe.of("a")));

        testModule("export {a as b} from 'a'", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.of("a"), "b")),
                Maybe.of("a")));

        testModule("export {as as as} from 'as'", new ExportFrom(ImmutableList.of(new ExportSpecifier(
                Maybe.of("as"), "as")), Maybe.of("as")));

        testModule("export {as as function} from 'as'", new ExportFrom(ImmutableList.of(new ExportSpecifier(
                Maybe.of("as"), "function")), Maybe.of("as")));

        testModule("export {a} from 'm'", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a")),
                Maybe.of("m")));

        testModule("export {if as var} from 'a';", new ExportFrom(ImmutableList.of(new ExportSpecifier(
                Maybe.of("if"), "var")), Maybe.of("a")));

        testModule("export {a}\n var a;", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a")),
                Maybe.empty()));

        testModule("export {a,}\n var a;", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a")),
                Maybe.empty()));

        testModule("export {a,b,}\n var a,b;", new ExportFrom(ImmutableList.of(new ExportSpecifier(Maybe.empty(), "a"),
                new ExportSpecifier(Maybe.empty(), "b")), Maybe.empty()));

        testModule("export var a = 0, b;", new Export(new VariableDeclaration(VariableDeclarationKind.Var,
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

        testModule("export function A(){} /* no semi */ false", new Export(new FunctionDeclaration(new BindingIdentifier("A"),
                false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default function (){} /* no semi */ false", new ExportDefault(new FunctionDeclaration(
                new BindingIdentifier("*default*"), false, new FormalParameters(ImmutableList.empty(), Maybe.empty()),
                new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))));

        testModule("export default class {} /* no semi */ false", new ExportDefault(new ClassDeclaration(
                new BindingIdentifier("*default*"), Maybe.empty(), ImmutableList.empty())));

        testModule("export default 3 + 1", new ExportDefault(new BinaryExpression(BinaryOperator.Plus,
                new LiteralNumericExpression(3.0), new LiteralNumericExpression(1.0))));

        testModule("export default a", new ExportDefault(new IdentifierExpression("a")));

        testModule("export default function a(){}", new ExportDefault(new FunctionDeclaration(new BindingIdentifier("a"),
                false, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default class a{}", new ExportDefault(new ClassDeclaration(new BindingIdentifier("a"),
                Maybe.empty(), ImmutableList.empty())));

        testModule("export default function* a(){}", new ExportDefault(new FunctionDeclaration(new BindingIdentifier("a"),
                true, new FormalParameters(ImmutableList.empty(), Maybe.empty()), new FunctionBody(ImmutableList.empty(),
                ImmutableList.empty()))));

        testModule("export default 0;0", new Module(ImmutableList.empty(), ImmutableList.of(new ExportDefault(
                new LiteralNumericExpression(0.0)), new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export function f(){};0", new Module(ImmutableList.empty(), ImmutableList.of(new Export(
                        new FunctionDeclaration(new BindingIdentifier("f"), false, new FormalParameters(ImmutableList.empty(),
                                Maybe.empty()), new FunctionBody(ImmutableList.empty(), ImmutableList.empty()))), new EmptyStatement(),
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export class A{};0", new Module(ImmutableList.empty(), ImmutableList.of(new Export(new ClassDeclaration(
                        new BindingIdentifier("A"), Maybe.empty(), ImmutableList.empty())), new EmptyStatement(),
                new ExpressionStatement(new LiteralNumericExpression(0.0)))));

        testModule("export {};0", new Module(ImmutableList.empty(), ImmutableList.of(new ExportFrom(ImmutableList.empty(),
                Maybe.empty()), new EmptyStatement(), new ExpressionStatement(new LiteralNumericExpression(0.0)))));

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
