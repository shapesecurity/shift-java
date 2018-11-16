package com.shapesecurity.shift.es2017.parser.modules;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Import;
import com.shapesecurity.shift.es2017.ast.ImportNamespace;
import com.shapesecurity.shift.es2017.ast.ImportSpecifier;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;
import com.shapesecurity.shift.es2017.parser.JsError;

import org.junit.Test;

public class ImportTest extends ParserTestCase {
    @Test
    public void testImport() throws JsError {
        testModule("import * as a from 'a'", new ImportNamespace(Maybe.empty(), new BindingIdentifier("a"), "a"));

        testModule("import * as a from 'c'", new ImportNamespace(Maybe.empty(), new BindingIdentifier("a"), "c"));

        testModule("import a, {} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.empty(), "c"));

        testModule("import a, {} from 'a'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.empty(), "a"));

        testModule("import a, * as b from 'a'", new ImportNamespace(Maybe.of(new BindingIdentifier("a")),
                new BindingIdentifier("b"), "a"));

        testModule("import a, {b} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.empty(), new BindingIdentifier("b"))), "c"));

        testModule("import a, {b as c} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.of("b"), new BindingIdentifier("c"))), "c"));

        testModule("import a, {function as c} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")),
                ImmutableList.of(new ImportSpecifier(Maybe.of("function"), new BindingIdentifier("c"))), "c"));

        testModule("import a, {as} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.empty(), new BindingIdentifier("as"))), "c"));

        testModule("import a, {as as c} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.of("as"), new BindingIdentifier("c"))), "c"));

        testModule("import {as as as} from 'as'", new Import(Maybe.empty(), ImmutableList.of(new ImportSpecifier(
                Maybe.of("as"), new BindingIdentifier("as"))), "as"));

        testModule("import a, {b,} from 'c'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.empty(), new BindingIdentifier("b"))), "c"));

        testModule("import a, {b,c} from 'd'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.empty(), new BindingIdentifier("b")), new ImportSpecifier(Maybe.empty(),
                        new BindingIdentifier("c"))), "d"));

        testModule("import a, {b,c,} from 'd'", new Import(Maybe.of(new BindingIdentifier("a")), ImmutableList.of(
                new ImportSpecifier(Maybe.empty(), new BindingIdentifier("b")), new ImportSpecifier(Maybe.empty(),
                        new BindingIdentifier("c"))), "d"));

        testScriptFailure("import 'a'", 0, "Unexpected token \"import\"");
        testModuleFailure("{import a from 'b';}", 1, "Unexpected token \"import\"");
        testModuleFailure("import", 6, "Unexpected end of input");
        testModuleFailure("import;", 6, "Unexpected token \";\"");
        testModuleFailure("import {}", 9, "Unexpected end of input");
        testModuleFailure("import {};", 9, "Unexpected token \";\"");
        testModuleFailure("import {} from;", 14, "Unexpected token \";\"");
        testModuleFailure("import {,} from 'a';", 8, "Unexpected token \",\"");
        testModuleFailure("import {b,,} from 'a';", 10, "Unexpected token \",\"");
        testModuleFailure("import {b as,} from 'a';", 12, "Unexpected token \",\"");
        testModuleFailure("import {function} from 'a';", 16, "Unexpected token \"}\"");
        testModuleFailure("import {a as function} from 'a';", 13, "Unexpected token \"function\"");
        testModuleFailure("import {b,,c} from 'a';", 10, "Unexpected token \",\"");
        testModuleFailure("import {b,c,,} from 'a';", 12, "Unexpected token \",\"");
        testModuleFailure("import * As a from 'a'", 9, "Unexpected identifier");
        testModuleFailure("import / as a from 'a'", 7, "Unexpected token \"/\"");
        testModuleFailure("import * as b, a from 'a'", 13, "Unexpected token \",\"");
        testModuleFailure("import a as b from 'a'", 9, "Unexpected identifier");
        testModuleFailure("import a, b from 'a'", 10, "Unexpected identifier");
    }
}
