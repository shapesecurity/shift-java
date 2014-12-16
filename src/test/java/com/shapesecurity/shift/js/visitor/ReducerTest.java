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

package com.shapesecurity.shift.js.visitor;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.TestBase;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.parser.JsError;
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.path.Branch;

import org.junit.Test;

import java.io.IOException;

import javax.annotation.Nonnull;

public class ReducerTest extends TestBase {
  private void count(String source, int expectedCount, Counter counter) throws JsError {
    Script script = new Parser(source).parse();
    assertEquals(expectedCount, script.reduce(counter, List.<Branch>nil()));
  }

  private void countLibrary(String fileName, int expectedCount, Counter counter) throws JsError, IOException {
    String source = readLibrary(fileName);
    Script script = Parser.parse(source);
    assertEquals(expectedCount, script.reduce(counter, List.<Branch>nil()));
  }

  @Test
  public void testSimpleCounter() throws JsError {
    count("({})", 1, new Counter() {
      @Nonnull
      @Override
      public Integer reduceObjectExpression(
          @Nonnull ObjectExpression node,
          @Nonnull List<Branch> path,
          @Nonnull List<Integer> properties) {
        return 1;
      }
    });
    count("({a:1,b:2})", 1, new Counter() {
      @Nonnull
      @Override
      public Integer reduceObjectExpression(
          @Nonnull ObjectExpression node,
          @Nonnull List<Branch> path,
          @Nonnull List<Integer> properties) {
        return 1;
      }
    });
    count("{a:1,b}", 0, new Counter() {
      @Nonnull
      @Override
      public Integer reduceObjectExpression(
          @Nonnull ObjectExpression node,
          @Nonnull List<Branch> path,
          @Nonnull List<Integer> properties) {
        return 1;
      }
    });
    count("{a:1,b}", 0, new Counter() {
      @Nonnull
      @Override
      public Integer reduceObjectExpression(
          @Nonnull ObjectExpression node,
          @Nonnull List<Branch> path,
          @Nonnull List<Integer> properties) {
        return 1;
      }
    });
    count("+{get a() { return 0; }, set a(v) { return v; } }", 2, new Counter() {
      @Nonnull
      @Override
      public Integer reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
        return 1;
      }
    });
    count("+{get a() { return 0; }, set a(v) { return v; } }", 2, new Counter() {
      @Nonnull
      @Override
      public Integer reduceReturnStatement(
          @Nonnull ReturnStatement node,
          @Nonnull List<Branch> path,
          @Nonnull Maybe<Integer> argument) {
        return argument.orJust(0) + 1;
      }
    });
  }

  @SuppressWarnings("MagicNumber")
  @Test
  public void testLibraryCounter() throws JsError, IOException {
    Counter counter = new Counter() {
      @Nonnull
      @Override
      public Integer reduceReturnStatement(
          @Nonnull ReturnStatement node,
          @Nonnull List<Branch> path,
          @Nonnull Maybe<Integer> argument) {
        return argument.orJust(0) + 1;
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
