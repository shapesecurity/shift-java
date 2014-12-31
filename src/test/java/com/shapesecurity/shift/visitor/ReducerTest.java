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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.TestBase;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.statement.ReturnStatement;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.path.Branch;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ReducerTest extends TestBase {
  private void count(String source, int expectedCount, Counter counter) throws JsError {
    Script script = Parser.parse(source);
    assertEquals(expectedCount, script.reduce(counter));
  }

  private void countLibrary(String fileName, int expectedCount, Counter counter) throws JsError, IOException {
    String source = readLibrary(fileName);
    Script script = Parser.parse(source);
    assertEquals(expectedCount, script.reduce(counter));
  }

  @Test
  public void testSimpleCounter() throws JsError {
    count("({})", 1, new Counter() {
      @NotNull
      @Override
      public Integer reduceObjectExpression(
          @NotNull ObjectExpression node,
          @NotNull List<Branch> path,
          @NotNull List<Integer> properties) {
        return 1;
      }
    });
    count("({a:1,b:2})", 1, new Counter() {
      @NotNull
      @Override
      public Integer reduceObjectExpression(
          @NotNull ObjectExpression node,
          @NotNull List<Branch> path,
          @NotNull List<Integer> properties) {
        return 1;
      }
    });
    count("{a:1,b}", 0, new Counter() {
      @NotNull
      @Override
      public Integer reduceObjectExpression(
          @NotNull ObjectExpression node,
          @NotNull List<Branch> path,
          @NotNull List<Integer> properties) {
        return 1;
      }
    });
    count("{a:1,b}", 0, new Counter() {
      @NotNull
      @Override
      public Integer reduceObjectExpression(
          @NotNull ObjectExpression node,
          @NotNull List<Branch> path,
          @NotNull List<Integer> properties) {
        return 1;
      }
    });
    count("+{get a() { return 0; }, set a(v) { return v; } }", 2, new Counter() {
      @NotNull
      @Override
      public Integer reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
        return 1;
      }
    });
    count("+{get a() { return 0; }, set a(v) { return v; } }", 2, new Counter() {
      @NotNull
      @Override
      public Integer reduceReturnStatement(
          @NotNull ReturnStatement node,
          @NotNull List<Branch> path,
          @NotNull Maybe<Integer> expression) {
        return expression.orJust(0) + 1;
      }
    });
  }

  @SuppressWarnings("MagicNumber")
  @Test
  public void testLibraryCounter() throws JsError, IOException {
    Counter counter = new Counter() {
      @NotNull
      @Override
      public Integer reduceReturnStatement(
          @NotNull ReturnStatement node,
          @NotNull List<Branch> path,
          @NotNull Maybe<Integer> expression) {
        return expression.orJust(0) + 1;
      }
    };
    countLibrary("backbone-1.1.0.js", 123, counter);
    countLibrary("underscore-1.5.2.js", 185, counter);
    countLibrary("jquery-1.9.1.js", 568, counter);
    countLibrary("angular-1.2.5.js", 719, counter);
    countLibrary("mootools-1.4.5.js", 696, counter);
    countLibrary("yui-3.12.0.js", 205, counter);
  }
}
