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

package com.shapesecurity.shift.validator;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class Validator extends MonoidalReducer<ValidationContext> {

  public Validator() {
    super(ValidationContext.MONOID);
  }

  public static ImmutableList<ValidationError> validate(Script script) {
    return ImmutableList.from(Director.reduceScript(new Validator(), script).errors);
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionBody(
    @NotNull FunctionBody node,
    @NotNull ImmutableList<ValidationContext> directives,
    @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceFunctionBody(node, directives, statements);
    s.clearFreeReturnStatements();
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<ValidationContext> expression
  ) {
    ValidationContext s = super.reduceReturnStatement(node, expression);
    s.addFreeReturnStatement(node);
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<ValidationContext> directives,
    @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceScript(node, directives, statements);
    s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, "return statements must be within a function body"));
    return s;
  }
}
