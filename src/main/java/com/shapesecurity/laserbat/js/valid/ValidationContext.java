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

package com.shapesecurity.laserbat.js.valid;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Monoid;
import com.shapesecurity.laserbat.js.ast.Identifier;

import javax.annotation.Nonnull;

public class ValidationContext {
  public static final Monoid<ValidationContext> MONOID = new ValidationContextMonoid();
  @Nonnull
  public final ConcatList<ValidationError> freeBreakStatements;
  @Nonnull
  public final ConcatList<ValidationError> freeContinueStatements;
  @Nonnull
  public final List<String> usedLabelNames;
  @Nonnull
  public final List<Identifier> freeJumpTargets;
  @Nonnull
  public final ConcatList<ValidationError> errors;
  public final ConcatList<ValidationError> strictErrors;
  @Nonnull
  public final ConcatList<ValidationError> freeReturnStatements;

  public ValidationContext() {
    this(ConcatList.<ValidationError>nil(), ConcatList.<ValidationError>nil(), List.<String>nil(),
        List.<Identifier>nil(), ConcatList.<ValidationError>nil(), ConcatList.<ValidationError>nil(),
        ConcatList.<ValidationError>nil());
  }

  public ValidationContext(
      @Nonnull ConcatList<ValidationError> freeBreakStatements,
      @Nonnull ConcatList<ValidationError> freeContinueStatements,
      @Nonnull List<String> usedLabelNames,
      @Nonnull List<Identifier> freeJumpTargets,
      @Nonnull ConcatList<ValidationError> freeReturnStatements,
      @Nonnull ConcatList<ValidationError> errors,
      @Nonnull ConcatList<ValidationError> strictErrors) {
    this.freeBreakStatements = freeBreakStatements;
    this.freeContinueStatements = freeContinueStatements;
    this.usedLabelNames = usedLabelNames;
    this.freeJumpTargets = freeJumpTargets;
    this.freeReturnStatements = freeReturnStatements;
    this.errors = errors;
    this.strictErrors = strictErrors;
  }

  public ValidationContext addFreeBreakStatement(@Nonnull ValidationError statement) {
    return new ValidationContext(this.freeBreakStatements.append(ConcatList.single(statement)),
        this.freeContinueStatements, this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext clearFreeBreakStatements() {
    return new ValidationContext(ConcatList.<ValidationError>nil(), //empty freeBreakStatements
        this.freeContinueStatements, this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext addFreeContinueStatement(@Nonnull ValidationError statement) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements.append(ConcatList.single(
        statement)), this.usedLabelNames, this.freeJumpTargets, this.freeReturnStatements, this.errors,
        this.strictErrors);
  }

  public ValidationContext clearFreeContinueStatements() {
    return new ValidationContext(this.freeBreakStatements, ConcatList.<ValidationError>nil(), this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext observeLabelName(@Nonnull final Identifier labelName) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, List.cons(labelName.name,
        this.usedLabelNames), this.freeJumpTargets.filter(identifier -> !identifier.name.equals(labelName.name)),
        this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext clearUsedLabelNames() {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, List.<String>nil(),
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext addFreeJumpTarget(@Nonnull Identifier labelName) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames, List.cons(
        labelName, this.freeJumpTargets), this.freeReturnStatements, this.errors, this.strictErrors);
  }

  public ValidationContext addFreeReturnStatement(@Nonnull ValidationError r) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements.append(ConcatList.single(r)), this.errors, this.strictErrors);
  }

  public ValidationContext clearReturnStatements() {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, ConcatList.<ValidationError>nil(), this.errors, this.strictErrors);
  }

  public ValidationContext addError(@Nonnull ValidationError error) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors.append(ConcatList.single(error)),
        this.strictErrors);
  }

  public ValidationContext addErrors(@Nonnull ConcatList<ValidationError> errors) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors.append(errors), this.strictErrors);
  }

  public ValidationContext addStrictError(@Nonnull ValidationError error) {
    return new ValidationContext(this.freeBreakStatements, this.freeContinueStatements, this.usedLabelNames,
        this.freeJumpTargets, this.freeReturnStatements, this.errors, this.strictErrors.append(ConcatList.single(
        error)));
  }

  public ValidationContext append(@Nonnull ValidationContext context) {
    return new ValidationContext(this.freeBreakStatements.append(context.freeBreakStatements),
        this.freeContinueStatements.append(context.freeContinueStatements), this.usedLabelNames.append(
        context.usedLabelNames), this.freeJumpTargets.append(context.freeJumpTargets), this.freeReturnStatements.append(
        context.freeReturnStatements), this.errors.append(context.errors), this.strictErrors.append(
        context.strictErrors));
  }

  private static final class ValidationContextMonoid implements Monoid<ValidationContext> {
    @Nonnull
    @Override
    public ValidationContext identity() {
      return new ValidationContext();
    }

    @Nonnull
    @Override
    public ValidationContext append(ValidationContext a, ValidationContext b) {
      return a.append(b);
    }
  }
}
