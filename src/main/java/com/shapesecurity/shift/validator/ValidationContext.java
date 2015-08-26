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
import com.shapesecurity.shift.ast.BindingIdentifier;
import com.shapesecurity.shift.ast.ReturnStatement;

import com.shapesecurity.shift.ast.YieldExpression;
import com.shapesecurity.shift.ast.YieldGeneratorExpression;

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
    @NotNull
    private final List<BindingIdentifier> bindingIdentifiersCalledDefault;
    @NotNull
    private final List<YieldExpression> yieldExpressionsNotInGeneratorContext;
    @NotNull
    private final List<YieldGeneratorExpression> yieldGeneratorExpressionsNotInGeneratorContext;


    public ValidationContext() {
        this(
                new ArrayList<>(), // errors
                new ArrayList<>(), // freeReturnStatements
                new ArrayList<>(), // bindingIdentifiersCalledDefault
                new ArrayList<>(), // yieldExpressionsNotInGeneratorContext
                new ArrayList<>()  // yieldGeneratorExpressionsNotInGeneratorContext
        );
    }

    private ValidationContext(
            @NotNull List<ValidationError> errors,
            @NotNull List<ReturnStatement> freeReturnStatements,
            @NotNull List<BindingIdentifier> bindingIdentifiersCalledDefault,
            @NotNull List<YieldExpression> yieldExpressionsNotInGeneratorContext,
            @NotNull List<YieldGeneratorExpression> yieldGeneratorExpressionsNotInGeneratorContext
    ) {
        this.errors = errors;
        this.freeReturnStatements = freeReturnStatements;
        this.bindingIdentifiersCalledDefault = bindingIdentifiersCalledDefault;
        this.yieldExpressionsNotInGeneratorContext = yieldExpressionsNotInGeneratorContext;
        this.yieldGeneratorExpressionsNotInGeneratorContext = yieldGeneratorExpressionsNotInGeneratorContext;
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

    public void addBindingIdentifierCalledDefault(@NotNull BindingIdentifier node) {
        this.bindingIdentifiersCalledDefault.add(node);
    }

    public void enforceBindingIdentifiersCalledDefault(Function<BindingIdentifier, ValidationError> createError) {
        this.bindingIdentifiersCalledDefault.stream().map(createError::apply).forEach(this::addError);
        this.bindingIdentifiersCalledDefault.clear();
    }

    public void clearBindingIdentifiersCalledDefault() {
        this.bindingIdentifiersCalledDefault.clear();
    }

    public void addYieldExpressionsNotInGeneratorContext(@NotNull YieldExpression node) {
        this.yieldExpressionsNotInGeneratorContext.add(node);
    }

    public void enforceYieldExpressionsNotInGeneratorContext(Function<YieldExpression, ValidationError> createError) {
        this.yieldExpressionsNotInGeneratorContext.stream().map(createError::apply).forEach(this::addError);
        this.yieldExpressionsNotInGeneratorContext.clear();
    }

    public void clearYieldExpressionsNotInGeneratorContext() {
        this.yieldExpressionsNotInGeneratorContext.clear();
    }

    public void addYieldGeneratorExpressionsNotInGeneratorContext(@NotNull YieldGeneratorExpression node) {
        this.yieldGeneratorExpressionsNotInGeneratorContext.add(node);
    }

    public void enforceYieldGeneratorExpressionsNotInGeneratorContext(Function<YieldGeneratorExpression, ValidationError> createError) {
        this.yieldGeneratorExpressionsNotInGeneratorContext.stream().map(createError::apply).forEach(this::addError);
        this.yieldGeneratorExpressionsNotInGeneratorContext.clear();
    }

    public void clearYieldGeneratorExpressionsNotInGeneratorContext() {
        this.yieldGeneratorExpressionsNotInGeneratorContext.clear();
    }

    public void addError(@NotNull ValidationError error) {
        this.errors.add(error);
    }

    ValidationContext append(@NotNull ValidationContext other) {
        this.errors.addAll(other.errors);
        this.freeReturnStatements.addAll(other.freeReturnStatements);
        this.bindingIdentifiersCalledDefault.addAll(other.bindingIdentifiersCalledDefault);
        this.yieldExpressionsNotInGeneratorContext.addAll(other.yieldExpressionsNotInGeneratorContext);
        this.yieldGeneratorExpressionsNotInGeneratorContext.addAll(other.yieldGeneratorExpressionsNotInGeneratorContext);
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
