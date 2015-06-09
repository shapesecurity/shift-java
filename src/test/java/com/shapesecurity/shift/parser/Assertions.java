package com.shapesecurity.shift.parser;

import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.Script;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Assertions {

  public static void testScript(String source) throws JsError {
    Parser.parseScript(source);
  }

  public static void testScript(String source, Script expected) throws JsError {
    {
      Script node = Parser.parseScript(source);
      assertEquals(expected, node);
    }
    {
      //Script node = Parser.parseScriptWithLocation(source);
      //assertEquals(expected, jsonString);
      //node.reduce(new RangeCheckerReducer());
    }
  }

  public static void testModule(String source) throws JsError {
    Parser.parseModule(source);
  }

  public static void testModule(String source, Module expected) throws JsError {
    {
      Module node = Parser.parseModule(source);
      assertEquals(expected, node);
    }
    {
      //Module node = Parser.parseModuleWithLocation(source);
      //assertEquals(expected, jsonString);
      //node.reduce(new RangeCheckerReducer());
    }
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
