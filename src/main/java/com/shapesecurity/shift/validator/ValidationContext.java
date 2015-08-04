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

import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.ReturnStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ValidationContext {

  public static final Monoid<ValidationContext> MONOID = new ValidationContextMonoid();

  @NotNull
  public final List<ValidationError> errors;
  @NotNull
  private final List<ReturnStatement> freeReturnStatements;

  protected boolean bindingIdentifierNameCanBeDefault;

  public ValidationContext() {
    this(
      new ArrayList<>(), // errors
      new ArrayList<>() // freeReturnStatements
    );
  }

  private ValidationContext(
    @NotNull List<ValidationError> errors,
    @NotNull List<ReturnStatement> freeReturnStatements
  ) {
    this.errors = errors;
    this.freeReturnStatements = freeReturnStatements;
    this.bindingIdentifierNameCanBeDefault = false;
  }

  public void addFreeReturnStatement(@NotNull ReturnStatement node) {
    this.freeReturnStatements.add(node);
  }

  public void enforceFreeReturnStatements(Function<ReturnStatement, ValidationError> createError) {
    this.freeReturnStatements.stream().map(createError::apply).forEach(this::addError);
    this.freeReturnStatements.clear();
  }

  public void clearFreeReturnStatements() {
    this.freeReturnStatements.clear();
  }

  public void addError(@NotNull ValidationError error) {
    this.errors.add(error);
  }

  ValidationContext append(@NotNull ValidationContext other) {
    this.errors.addAll(other.errors);
    this.freeReturnStatements.addAll(other.freeReturnStatements);
    return this;
  }

  private static final class ValidationContextMonoid implements Monoid<ValidationContext> {
    @NotNull
    @Override
    public ValidationContext identity() {
      return new ValidationContext();
    }

    @NotNull
    @Override
    public ValidationContext append(ValidationContext a, ValidationContext b) {
      return a.append(b);
    }
  }
}
