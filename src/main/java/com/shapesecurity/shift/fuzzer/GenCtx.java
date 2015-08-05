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

package com.shapesecurity.shift.fuzzer;
import com.shapesecurity.functional.data.ImmutableList;
import java.util.Random;
import com.shapesecurity.shift.ast.IdentifierExpression;
import org.jetbrains.annotations.NotNull;

class GenCtx {
  @NotNull
  final Random random;
  @NotNull
  final ImmutableList<IdentifierExpression> labels;
  @NotNull
  final ImmutableList<IdentifierExpression> iterationLabels;
  @NotNull
  final ImmutableList<IdentifierExpression> labelsInFunctionBoundary;
  @NotNull
  final ImmutableList<IdentifierExpression> iterationLabelsInFunctionBoundary;

  final boolean inIteration;
  final boolean inSwitch;
  final boolean inStrictMode;
  final boolean inFunctional;
  final boolean allowMissingElse;
  final boolean allowReturn;


  GenCtx(@NotNull Random random) {
    this(random, ImmutableList.nil(), ImmutableList.nil(), ImmutableList.nil(), ImmutableList.nil(), false, false, false, false, true, false);
  }

  private GenCtx(@NotNull Random random,
                 @NotNull ImmutableList<IdentifierExpression> labels,
                 @NotNull ImmutableList<IdentifierExpression> iterationLabels,
                 @NotNull ImmutableList<IdentifierExpression> labelsInFunctionBoundary,
                 @NotNull ImmutableList<IdentifierExpression> iterationLabelsInFunctionBoundary,
                 boolean inIteration, boolean inSwitch, boolean inStrictMode,
                 boolean inFunctional,
                 boolean allowMissingElse,
                 boolean allowReturn) {
    this.random = random;
    this.labels = labels;
    this.iterationLabels = iterationLabels;
    this.labelsInFunctionBoundary = labelsInFunctionBoundary;
    this.iterationLabelsInFunctionBoundary = iterationLabelsInFunctionBoundary;
    this.inIteration = inIteration;
    this.inSwitch = inSwitch;
    this.inStrictMode = inStrictMode;
    this.inFunctional = inFunctional;
    this.allowMissingElse = allowMissingElse;
    this.allowReturn = allowReturn;
  }

  @NotNull
  GenCtx withLabel(@NotNull IdentifierExpression identifier) {
    return new GenCtx(this.random,
        this.labels.cons(identifier),
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary,
        this.inIteration,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx withIterationLabel(@NotNull IdentifierExpression identifier) {
    return new GenCtx(this.random,
        this.labels.cons(identifier),
        this.iterationLabels.cons(identifier),
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary, this.inIteration,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx enterIteration() {
    return new GenCtx(
        this.random,
        this.labels,
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary, true,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx enterStrictMode() {
    return new GenCtx(this.random,
        this.labels,
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary, this.inIteration,
        this.inSwitch,
        true,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx enterSwitch() {
    return new GenCtx(this.random,
        this.labels,
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary, this.inIteration,
        true,
        this.inStrictMode,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx enterFunctional() {
    return new GenCtx(
        this.random,
        this.labels,
        this.iterationLabels,
        ImmutableList.nil(),
        ImmutableList.nil(),
        false,
        false,
        this.inStrictMode,
        true,
        true,
        true);
  }

  @NotNull
  GenCtx clearLabels() {
    return new GenCtx(
        this.random,
        ImmutableList.nil(),
        ImmutableList.nil(),
        ImmutableList.nil(),
        ImmutableList.nil(),
        this.inIteration,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        this.allowMissingElse,
        this.allowReturn);
  }

  @NotNull
  GenCtx allowMissingElse() {
    return new GenCtx(this.random,
        this.labels,
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary,
        this.inIteration,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        true,
        this.allowReturn);
  }

  @NotNull
  GenCtx forbidMissingElse() {
    return new GenCtx(this.random,
        this.labels,
        this.iterationLabels,
        this.labelsInFunctionBoundary,
        this.iterationLabelsInFunctionBoundary,
        this.inIteration,
        this.inSwitch,
        this.inStrictMode,
        this.inFunctional,
        false,
        this.allowReturn);
  }
}
