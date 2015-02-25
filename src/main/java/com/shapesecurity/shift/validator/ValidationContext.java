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
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.statement.ContinueStatement;
import com.shapesecurity.shift.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class ValidationContext {
  public static final Monoid<ValidationContext> MONOID = new ValidationContextMonoid();
  @NotNull
  private final ConcatList<ValidationError> freeBreakStatements;
  @NotNull
  private final ConcatList<ValidationError> freeContinueStatements;
  @NotNull
  private final ImmutableList<String> usedLabelNames;
  @NotNull
  private final ImmutableList<Identifier> freeJumpTargets;
  @NotNull
  public final ConcatList<ValidationError> errors;
  @NotNull
  private final ConcatList<ValidationError> strictErrors;
  @NotNull
  private final ConcatList<ValidationError> freeReturnStatements;

  public ValidationContext() {
    this(ConcatList.empty(),
        ConcatList.empty(),
        ImmutableList.nil(),
        ImmutableList.nil(),
        ConcatList.empty(),
        ConcatList.empty(),
        ConcatList.empty());
  }

  private ValidationContext(@NotNull ConcatList<ValidationError> freeBreakStatements,
                            @NotNull ConcatList<ValidationError> freeContinueStatements,
                            @NotNull ImmutableList<String> usedLabelNames,
                            @NotNull ImmutableList<Identifier> freeJumpTargets,
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
        this.freeBreakStatements.append1(statement),
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext clearFreeBreakStatements() {
    return new ValidationContext(
        ConcatList.empty(), //empty freeBreakStatements
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext addFreeContinueStatement(@NotNull ContinueStatement statement) {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements.append1(new ValidationError(statement,
            "Continue statement must be inside an iteration statement.")),
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext clearFreeContinueStatements() {
    return new ValidationContext(
        this.freeBreakStatements,
        ConcatList.empty(),
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext observeLabelName(@NotNull final Identifier labelName) {
    ConcatList<ValidationError> errors = this.errors;
    if (this.usedLabelNames.exists(s -> s.equals(labelName.name))) {
      errors = errors.append1(new ValidationError(labelName, "Duplicate label name."));
    }
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        ImmutableList.cons(labelName.name, this.usedLabelNames),
        this.freeJumpTargets.filter(identifier -> !identifier.name.equals(labelName.name)),
        this.freeReturnStatements,
        errors,
        this.strictErrors
    );
  }

  public ValidationContext clearUsedLabelNames() {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        ImmutableList.nil(),
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext addFreeJumpTarget(@NotNull Identifier labelName) {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        ImmutableList.cons(labelName, this.freeJumpTargets),
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext addFreeReturnStatement(@NotNull Node node) {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements.append1(new ValidationError(node, "Return statement must be inside of a function")),
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext clearReturnStatements() {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        ConcatList.empty(),
        this.errors,
        this.strictErrors
    );
  }

  public ValidationContext addError(@NotNull ValidationError error) {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors.append1(error),
        this.strictErrors
    );
  }

  public ValidationContext invalidateStrictErrors() {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors.append(this.strictErrors),
        ConcatList.empty()
    );
  }

  public ValidationContext invalidateFreeReturnErrors() {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        ConcatList.empty(),
        this.errors.append(this.freeReturnStatements),
        this.strictErrors
    );
  }

  public ValidationContext invalidateFreeContinueAndBreakErrors() {
    return new ValidationContext(
        ConcatList.empty(),
        ConcatList.empty(),
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors.append(this.freeContinueStatements).append(this.freeBreakStatements),
        this.strictErrors
    );
  }

  public ValidationContext addStrictError(@NotNull ValidationError error) {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors.append1(error)
    );
  }

  public ValidationContext clearIdentifierNameError() {
    return new ValidationContext(
        this.freeBreakStatements,
        this.freeContinueStatements,
        this.usedLabelNames,
        this.freeJumpTargets,
        this.freeReturnStatements,
        this.errors,
        this.strictErrors
    );
  }


  public ValidationContext checkReserved(@NotNull Identifier identifier) {
    if (Utils.isStrictModeReservedWordES5(identifier.name)) {
      if (Utils.isReservedWordES5(identifier.name)) {
        return this.addError(new ValidationError(identifier, "Identifier must not be reserved word in this position"));
      }
      return this.addStrictError(new ValidationError(identifier,
          "Identifier must not be strict mode reserved word in this position"));
    }
    return this;
  }

  public ValidationContext checkRestricted(@NotNull Identifier identifier) {
    ValidationContext v = this.checkReserved(identifier);
    if (Utils.isRestrictedWord(identifier.name)) {
      return v.addStrictError(new ValidationError(identifier,
          "Identifier must not be restricted word in this position in strict mode"));
    }
    return v;
  }

  public ValidationContext checkFreeJumpTargets() {
    if (this.freeJumpTargets.isEmpty()) {
      return this;
    }
    return this.freeJumpTargets.map(ident -> new ValidationError(ident, "Unbound break/continue label")).foldLeft(
        ValidationContext::addError,
        this);
  }

  ValidationContext append(@NotNull ValidationContext context) {
    return new ValidationContext(
        this.freeBreakStatements.append(context.freeBreakStatements),
        this.freeContinueStatements.append(context.freeContinueStatements),
        this.usedLabelNames.append(context.usedLabelNames),
        this.freeJumpTargets.append(context.freeJumpTargets),
        this.freeReturnStatements.append(context.freeReturnStatements),
        this.errors.append(context.errors),
        this.strictErrors.append(context.strictErrors)
    );
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
