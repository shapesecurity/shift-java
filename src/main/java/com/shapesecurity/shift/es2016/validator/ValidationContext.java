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

package com.shapesecurity.shift.es2016.validator;

import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es2016.ast.*;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ValidationContext {

    public static final Monoid<ValidationContext> MONOID = new ValidationContextMonoid();

    @Nonnull
    public final List<ValidationError> errors;
    @Nonnull
    private final List<ReturnStatement> freeReturnStatements;
    @Nonnull
    private final List<BindingIdentifier> bindingIdentifiersCalledDefault;
    @Nonnull
    private final List<AwaitExpression> awaitExpressionsNotInAsyncContext;
    @Nonnull
    private final List<YieldExpression> yieldExpressionsNotInGeneratorContext;
    @Nonnull
    private final List<YieldGeneratorExpression> yieldGeneratorExpressionsNotInGeneratorContext;


    public ValidationContext() {
        this(
                new ArrayList<>(), // errors
                new ArrayList<>(), // freeReturnStatements
                new ArrayList<>(), // bindingIdentifiersCalledDefault
                new ArrayList<>(), // awaitExpressionsNotInAsyncContext
                new ArrayList<>(), // yieldExpressionsNotInGeneratorContext
                new ArrayList<>()  // yieldGeneratorExpressionsNotInGeneratorContext
        );
    }

    private ValidationContext(
            @Nonnull List<ValidationError> errors,
            @Nonnull List<ReturnStatement> freeReturnStatements,
            @Nonnull List<BindingIdentifier> bindingIdentifiersCalledDefault,
            @Nonnull List<AwaitExpression> awaitExpressionsNotInAsyncContext,
            @Nonnull List<YieldExpression> yieldExpressionsNotInGeneratorContext,
            @Nonnull List<YieldGeneratorExpression> yieldGeneratorExpressionsNotInGeneratorContext
    ) {
        this.errors = errors;
        this.freeReturnStatements = freeReturnStatements;
        this.bindingIdentifiersCalledDefault = bindingIdentifiersCalledDefault;
        this.awaitExpressionsNotInAsyncContext = awaitExpressionsNotInAsyncContext;
        this.yieldExpressionsNotInGeneratorContext = yieldExpressionsNotInGeneratorContext;
        this.yieldGeneratorExpressionsNotInGeneratorContext = yieldGeneratorExpressionsNotInGeneratorContext;
    }

    public void addFreeReturnStatement(@Nonnull ReturnStatement node) {
        this.freeReturnStatements.add(node);
    }

    public void enforceFreeReturnStatements(Function<ReturnStatement, ValidationError> createError) {
        this.freeReturnStatements.stream().map(createError::apply).forEach(this::addError);
        this.freeReturnStatements.clear();
    }

    public void clearFreeReturnStatements() {
        this.freeReturnStatements.clear();
    }

    public void addBindingIdentifierCalledDefault(@Nonnull BindingIdentifier node) {
        this.bindingIdentifiersCalledDefault.add(node);
    }

    public void enforceBindingIdentifiersCalledDefault(Function<BindingIdentifier, ValidationError> createError) {
        this.bindingIdentifiersCalledDefault.stream().map(createError::apply).forEach(this::addError);
        this.bindingIdentifiersCalledDefault.clear();
    }

    public void clearBindingIdentifiersCalledDefault() {
        this.bindingIdentifiersCalledDefault.clear();
    }

    public void addAwaitExpressionsNotInAsyncContext(@Nonnull AwaitExpression node) {
        this.awaitExpressionsNotInAsyncContext.add(node);
    }

    public void addYieldExpressionsNotInGeneratorContext(@Nonnull YieldExpression node) {
        this.yieldExpressionsNotInGeneratorContext.add(node);
    }

    public void enforceAwaitExpressionsNotInAsyncContext(Function<AwaitExpression, ValidationError> createError) {
        this.awaitExpressionsNotInAsyncContext.stream().map(createError::apply).forEach(this::addError);
        this.awaitExpressionsNotInAsyncContext.clear();

    }

    public void enforceYieldExpressionsNotInGeneratorContext(Function<YieldExpression, ValidationError> createError) {
        this.yieldExpressionsNotInGeneratorContext.stream().map(createError::apply).forEach(this::addError);
        this.yieldExpressionsNotInGeneratorContext.clear();
    }

    public void clearAwaitExpressionsNotInAsyncContext() {
        this.awaitExpressionsNotInAsyncContext.clear();
    }

    public void clearYieldExpressionsNotInGeneratorContext() {
        this.yieldExpressionsNotInGeneratorContext.clear();
    }

    public void addYieldGeneratorExpressionsNotInGeneratorContext(@Nonnull YieldGeneratorExpression node) {
        this.yieldGeneratorExpressionsNotInGeneratorContext.add(node);
    }

    public void enforceYieldGeneratorExpressionsNotInGeneratorContext(Function<YieldGeneratorExpression, ValidationError> createError) {
        this.yieldGeneratorExpressionsNotInGeneratorContext.stream().map(createError::apply).forEach(this::addError);
        this.yieldGeneratorExpressionsNotInGeneratorContext.clear();
    }

    public void clearYieldGeneratorExpressionsNotInGeneratorContext() {
        this.yieldGeneratorExpressionsNotInGeneratorContext.clear();
    }

    public void addError(@Nonnull ValidationError error) {
        this.errors.add(error);
    }

    ValidationContext append(@Nonnull ValidationContext other) {
        this.errors.addAll(other.errors);
        this.freeReturnStatements.addAll(other.freeReturnStatements);
        this.bindingIdentifiersCalledDefault.addAll(other.bindingIdentifiersCalledDefault);
        this.awaitExpressionsNotInAsyncContext.addAll(other.awaitExpressionsNotInAsyncContext);
        this.yieldExpressionsNotInGeneratorContext.addAll(other.yieldExpressionsNotInGeneratorContext);
        this.yieldGeneratorExpressionsNotInGeneratorContext.addAll(other.yieldGeneratorExpressionsNotInGeneratorContext);
        return this;
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
