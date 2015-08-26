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

package com.shapesecurity.functional.data;

import com.shapesecurity.functional.Unit;

import org.jetbrains.annotations.NotNull;

public interface Semigroup<T> {
    public static final UnitIdentity UNIT_IDENTITY = new UnitIdentity();
    public static final IntegerAdditive INTEGER_ADDITIVE = new IntegerAdditive();
    public static final IntegerMultiplicative INTEGER_MULTIPLICATIVE = new IntegerMultiplicative();
    public static final StringConcat STRING_CONCAT = new StringConcat();
    public static final BooleanOr BOOLEAN_OR = new BooleanOr();
    public static final BooleanAnd BOOLEAN_AND = new BooleanAnd();

    @NotNull
    T append(T a, T b);

    public static class UnitIdentity implements Semigroup<Unit> {
        protected UnitIdentity() {
        }

        @NotNull
        @Override
        public final Unit append(Unit a, Unit b) {
            return Unit.unit;
        }
    }

    public static class IntegerAdditive implements Semigroup<Integer> {
        protected IntegerAdditive() {
        }

        @NotNull
        @Override
        public final Integer append(Integer a, Integer b) {
            return a + b;
        }
    }

    public static class IntegerMultiplicative implements Semigroup<Integer> {
        protected IntegerMultiplicative() {
        }

        @NotNull
        @Override
        public final Integer append(Integer a, Integer b) {
            return a * b;
        }
    }

    public static class StringConcat implements Semigroup<String> {
        protected StringConcat() {
        }

        @NotNull
        @Override
        public final String append(String a, String b) {
            return a + b;
        }
    }

    public static class BooleanOr implements Semigroup<Boolean> {
        protected BooleanOr() {
        }

        @NotNull
        @Override
        public final Boolean append(Boolean a, Boolean b) {
            return a || b;
        }
    }

    public static class BooleanAnd implements Semigroup<Boolean> {
        protected BooleanAnd() {
        }

        @NotNull
        @Override
        public final Boolean append(Boolean a, Boolean b) {
            return a && b;
        }
    }
}

