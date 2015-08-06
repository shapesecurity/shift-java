package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

public abstract class ParserTestCase extends TestCase {

    protected static FormalParameters NO_PARAMETERS = new FormalParameters(ImmutableList.nil(), Maybe.nothing());

    public static void testScript(String source) throws JsError {
        Parser.parseScript(source);
    }

    public static void testScript(String source, Script expected) throws JsError {
        Script node = Parser.parseScript(source);
        assertEquals(expected, node);
    }

    public static void testScript(String source, Statement expected) throws JsError {
        Script node = Parser.parseScript(source);
        assert (node.statements.isNotEmpty());
        assertEquals(expected, node.statements.maybeHead().just());
    }

    public static void testScript(String source, Expression expected) throws JsError {
        Script node = Parser.parseScript(source);
        assert (node.statements.isNotEmpty());
        Statement stmt = node.statements.maybeHead().just();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);
    }


    public static void testModule(String source) throws JsError {
        Parser.parseModule(source);
    }

    public static void testModule(String source, Module expected) throws JsError {
        Module node = Parser.parseModule(source);
        assertEquals(expected, node);
    }

    public static void testModule(String source, ImportDeclarationExportDeclarationStatement expected) throws JsError {
        Module node = Parser.parseModule(source);
        assert (node.items.isNotEmpty());
        assertEquals(expected, node.items.maybeHead().just());
    }

    public static void testModule(String source, Expression expected) throws JsError {
        Module node = Parser.parseModule(source);
        assert (node.items.isNotEmpty());
        ImportDeclarationExportDeclarationStatement stmt = node.items.maybeHead().just();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);
    }

    public static void testScriptFailureML(@NotNull String source, int line, int column, int index, @NotNull String error) {
        try {
            Parser.parseScript(source);
        } catch (JsError jsError) {
            assertEquals(error, jsError.getDescription());
            assertEquals(line, jsError.getLine());
            assertEquals(column, jsError.getColumn());
            assertEquals(index, jsError.getIndex());
            return;
        }
        fail("Parsing error not found");
    }

    public static void testScriptFailure(@NotNull String source, int index, @NotNull String error) {
        testScriptFailureML(source, 1, index, index, error);
    }

    public static void testScriptFailure(@NotNull String source, int line, int column, int index, @NotNull String error) {
        testScriptFailureML(source, line, column, index, error);
    }

    public static void testModuleFailureML(@NotNull String source, int line, int column, int index, @NotNull String error) {
        try {
            Parser.parseModule(source);
        } catch (JsError jsError) {
            assertEquals(error, jsError.getDescription());
            assertEquals(line, jsError.getLine());
            assertEquals(column, jsError.getColumn());
            assertEquals(index, jsError.getIndex());
            return;
        }
        fail("Parsing error not found");
    }

    public static void testModuleFailure(@NotNull String source, int index, @NotNull String error) {
        testModuleFailureML(source, 1, index, index, error);
    }
}
