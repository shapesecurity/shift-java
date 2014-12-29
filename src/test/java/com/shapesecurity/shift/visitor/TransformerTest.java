/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.shift.visitor;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.TestBase;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.path.Branch;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransformerTest extends TestBase {
  public static final double NANOS_TO_SECONDS = 1e-9;

  private void testClone(String text) throws JsError {
    Script script = Parser.parse(text);
    Script script2 = script.reduce(CloneReducer.INSTANCE);
    assertEquals(script, script2);
    Script script3 = script.reduce(LazyCloner.INSTANCE).node;
    assertTrue(script3 == script);
  }

  private void testTransform(String expected, String text, CloneReducer transformer) throws JsError {
    Script script = Parser.parse(text).reduce(transformer);
    Script script2 = Parser.parse(expected);
    assertEquals(script, script2);
  }

  private void testLibrary(String fileName) throws IOException, JsError {
    String source = readLibrary(fileName);
    Script script = Parser.parse(source);
    Script script2 = script.reduce(new CloneReducer());
    assertEquals(script, script2);
  }

  @Test
  public void testTransformSimple() throws JsError {
    testTransform(
        "3", "2", new CloneReducer() {
          @NotNull
          @Override
          public Expression reduceLiteralNumericExpression(
              @NotNull LiteralNumericExpression node,
              @NotNull List<Branch> path) {
            return new LiteralNumericExpression(node.value + 1);
          }
        });
    testTransform(
        "3+5", "2+4", new CloneReducer() {
          @NotNull
          @Override
          public Expression reduceLiteralNumericExpression(
              @NotNull LiteralNumericExpression node,
              @NotNull List<Branch> path) {
            return new LiteralNumericExpression(node.value + 1);
          }
        });
  }

  @Test
  public void testCloneSimple() throws JsError {
    testClone("");
    testClone("a");
    testClone("a+b");
    testClone("a+b=c");
    testClone("var a,b=c");
    testClone("for(var a,b;;);");
    testClone("with(a);");
    testClone("debugger;");
    testClone("+{get a() { 'hello'; }, set a(param) { 'world'; } }");
  }

  @Test
  public void testCloneLibrary() throws IOException, JsError {
    List<String> jsFiles = List.nil();
    setFatal(false); // Collect the failures in an ErrorCollector

    // Get a list of the js files within the resources directory to process
    File[] files = new File(getPath("libraries").toString()).listFiles();
    if (files == null) {
      System.out.println("Error retrieving list of javascript libraries.");
      return;
    }
    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(".js")) {
        jsFiles = List.cons(file.getName(), jsFiles);
      }
    }

    // Test the hell out of it... ": )
    long start = System.nanoTime();
    System.out.println("Testing " + jsFiles.length + " javascript libraries.");
    int i = 0;
    for (String jsLib : jsFiles) {
      System.out.print(".");
      if (++i == 80) {
        i = 0;
        System.out.println();
      }
      testLibrary(jsLib);
    }
    System.out.println("");
    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
    System.out.printf("Library testing time: %.1fsec\n", elapsed);
    setFatal(true); // Revert back to the default behavior
  }
}
