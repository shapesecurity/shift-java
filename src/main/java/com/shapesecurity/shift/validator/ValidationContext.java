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

import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.Identifier;

import org.jetbrains.annotations.NotNull;

public class ValidationContext {
  public static final Monoid<ValidationContext> MONOID = new ValidationContextMonoid();
  @NotNull
  public final ConcatList<ValidationError> freeBreakStatements;
  @NotNull
  public final ConcatList<ValidationError> freeContinueStatements;
  @NotNull
  public final List<String> usedLabelNames;
  @NotNull
  public final List<Identifier> freeJumpTargets;
  @NotNull
  public final ConcatList<ValidationError> errors;
  public final ConcatList<ValidationError> strictErrors;
  @NotNull
  public final ConcatList<ValidationError> freeReturnStatements;

  public ValidationContext() {
    this(
        ConcatList.<ValidationError>empty(), ConcatList.<ValidationError>empty(), List.<String>nil(),
        List.<Identifier>nil(), ConcatList.<ValidationError>empty(), ConcatList.<ValidationError>empty(),
        ConcatList.<ValidationError>empty());
  }

  private ValidationContext(@NotNull ConcatList<ValidationError> freeBreakStatements,
                            @NotNull ConcatList<ValidationError> freeContinueStatements,
                            @NotNull List<String> usedLabelNames,
                            @NotNull List<Identifier> freeJumpTargets,
                            @NotNull ConcatList<ValidationError> freeReturnStatements,
                            @NotNull ConcatList<ValidationError> errors,
                            @NotNull ConcatList<ValidationError> strictErrors) {
    this.freeBreakStatements = freeBreakStatements;
    this.freeContinueStatements = freeContinueStatements;
    this.usedLabelNames = usedLabelNames;
    this.freeJumpTargets = freeJumpTargets;
    this.freeReturnStatements = freeReturnStatements;
    this.errors = errors;
    this.strictErrors = strictErrors;
  }

  public ValidationContext addFreeBreakStatement(@NotNull ValidationError statement) {
    return new ValidationContext(
        this.freeBreakStatements.append(ConcatList.single(statement)),
        this.freeContinueStatements, this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext clearFreeBreakStatements() {
    return new ValidationContext(
        ConcatList.<ValidationError>empty(), //empty freeBreakStatements
        this.freeContinueStatements, this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext addFreeContinueStatement(@NotNull ValidationError statement) {
    return new ValidationContext(
        this.freeBreakStatements, this.freeContinueStatements.append(
        ConcatList.single(
            statement)), this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext clearFreeContinueStatements() {
    return new ValidationContext(
        this.freeBreakStatements, ConcatList.<ValidationError>empty(), this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext observeLabelName(@NotNull final Identifier labelName) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, List.cons(labelName.name,
        this.usedLabelNames), this.freeJumpTargets.filter(identifier -> !identifier.name.equals(labelName.name)),
        this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext clearUsedLabelNames() {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, List.<String>nil(),
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext addFreeJumpTarget(@NotNull Identifier labelName) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames, List.cons(
        labelName, this.freeJumpTargets), this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext addFreeReturnStatement(@NotNull ValidationError r) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements.append(ConcatList.single(r)), this.errors, this.strictErrors);
  }

  public ValidationContext clearReturnStatements() {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, ConcatList.<ValidationError>empty(), this.errors, this.strictErrors);
  }

  public ValidationContext addError(@NotNull ValidationError error) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors.append(ConcatList.single(error)),
        this.strictErrors);
  }

  public ValidationContext addErrors(@NotNull ConcatList<ValidationError> errors) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors.append(errors), this.strictErrors);
  }

  public ValidationContext addStrictError(@NotNull ValidationError error) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors.append(
        ConcatList.single(
            error)));
  }

  ValidationContext append(@NotNull ValidationContext context) {
    return new ValidationContext(this.freeBreakStatements.append(context.freeBreakStatements),
        this.freeContinueStatements.append(context.freeContinueStatements), this.usedLabelNames.append(
        context.usedLabelNames), this.freeJumpTargets.append(context.freeJumpTargets), this.freeReturnStatements.append(
        context.freeReturnStatements), this.errors.append(context.errors), this.strictErrors.append(
        context.strictErrors));
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
