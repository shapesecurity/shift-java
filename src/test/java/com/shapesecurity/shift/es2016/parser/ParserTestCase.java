package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;

import com.shapesecurity.shift.es2016.ast.Expression;
import com.shapesecurity.shift.es2016.ast.ExpressionStatement;
import com.shapesecurity.shift.es2016.ast.FormalParameters;
import com.shapesecurity.shift.es2016.ast.ImportDeclarationExportDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.Statement;
import com.shapesecurity.shift.es2016.reducer.Director;
import junit.framework.TestCase;

import org.jetbrains.annotations.NotNull;

public abstract class ParserTestCase extends TestCase {

    protected static FormalParameters NO_PARAMETERS = new FormalParameters(ImmutableList.empty(), Maybe.empty());

    public static void testScript(String source) throws JsError {
        Parser.parseScript(source);
    }

    public static void testScript(String source, Script expected) throws JsError {
        Script node = Parser.parseScript(source);
        assertEquals(expected, node);

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseScript(source);
        assertEquals(expected, node);
        Director.reduceScript(new RangeCheckerReducer(parserWithLocation), node);
    }

    public static void testScript(String source, Statement expected) throws JsError {
        Script node = Parser.parseScript(source);
        assert (node.statements.isNotEmpty());
        assertEquals(expected, node.statements.maybeHead().fromJust());

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseScript(source);
        assert (node.statements.isNotEmpty());
        assertEquals(expected, node.statements.maybeHead().fromJust());
        Director.reduceScript(new RangeCheckerReducer(parserWithLocation), node);
    }

    public static void testScript(String source, Expression expected) throws JsError {
        Script node = Parser.parseScript(source);
        assert (node.statements.isNotEmpty());
        Statement stmt = node.statements.maybeHead().fromJust();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseScript(source);
        assert (node.statements.isNotEmpty());
        stmt = node.statements.maybeHead().fromJust();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);
        Director.reduceScript(new RangeCheckerReducer(parserWithLocation), node);
    }


    public static void testModule(String source) throws JsError {
        Parser.parseModule(source);
    }

    public static void testModule(String source, Module expected) throws JsError {
        Module node = Parser.parseModule(source);
        assertEquals(expected, node);

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseModule(source);
        assertEquals(expected, node);
        Director.reduceModule(new RangeCheckerReducer(parserWithLocation), node);
    }

    public static void testModule(String source, ImportDeclarationExportDeclarationStatement expected) throws JsError {
        Module node = Parser.parseModule(source);
        assert (node.items.isNotEmpty());
        assertEquals(expected, node.items.maybeHead().fromJust());

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseModule(source);
        assert (node.items.isNotEmpty());
        assertEquals(expected, node.items.maybeHead().fromJust());
        Director.reduceModule(new RangeCheckerReducer(parserWithLocation), node);
    }

    public static void testModule(String source, Expression expected) throws JsError {
        Module node = Parser.parseModule(source);
        assert (node.items.isNotEmpty());
        ImportDeclarationExportDeclarationStatement stmt = node.items.maybeHead().fromJust();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);

        ParserWithLocation parserWithLocation = new ParserWithLocation();
        node = parserWithLocation.parseModule(source);
        assert (node.items.isNotEmpty());
        stmt = node.items.maybeHead().fromJust();
        assert (stmt instanceof ExpressionStatement);
        assertEquals(expected, ((ExpressionStatement) stmt).expression);
        Director.reduceModule(new RangeCheckerReducer(parserWithLocation), node);
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


    public static void testScriptEarlyError(@NotNull String source, @NotNull String error) throws JsError {
        Script script = Parser.parseScript(source);
        ImmutableList<EarlyError> errors = EarlyErrorChecker.validate(script);
        assertEquals(1, errors.length);
        assertEquals(error, errors.maybeHead().fromJust().message);
    }


    public static void testModuleEarlyError(@NotNull String source, @NotNull String error) throws JsError {
        Module module = Parser.parseModule(source);
        ImmutableList<EarlyError> errors = EarlyErrorChecker.validate(module);
        assertEquals(1, errors.length);
        assertEquals(error, errors.maybeHead().fromJust().message);
    }
}
