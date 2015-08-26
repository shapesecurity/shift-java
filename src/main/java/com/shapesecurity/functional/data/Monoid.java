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

public interface Monoid<T> extends Semigroup<T> {
    public static final UnitIdentity UNIT = new UnitIdentity();
    public static final IntegerAdditive INTEGER_ADDITIVE = new IntegerAdditive();
    public static final IntegerMultiplicative INTEGER_MULTIPLICATIVE = new IntegerMultiplicative();
    public static final StringConcat STRING_CONCAT = new StringConcat();
    public static final BooleanOr BOOLEAN_OR = new BooleanOr();
    public static final BooleanAnd BOOLEAN_AND = new BooleanAnd();

    @NotNull
    T identity();

    public static class UnitIdentity extends Semigroup.UnitIdentity implements Monoid<Unit> {
        protected UnitIdentity() {
            super();
        }

        @NotNull
        @Override
        public final Unit identity() {
            return Unit.unit;
        }
    }

    public static class IntegerAdditive extends Semigroup.IntegerAdditive implements Monoid<Integer> {
        protected IntegerAdditive() {
            super();
        }

        @NotNull
        @Override
        public final Integer identity() {
            return 0;
        }
    }

    public static class IntegerMultiplicative extends Semigroup.IntegerMultiplicative implements Monoid<Integer> {
        protected IntegerMultiplicative() {
            super();
        }

        @NotNull
        @Override
        public final Integer identity() {
            return 1;
        }
    }

    public static class StringConcat extends Semigroup.StringConcat implements Monoid<String> {
        protected StringConcat() {
            super();
        }

        @NotNull
        @Override
        public final String identity() {
            return "";
        }
    }

    public static class BooleanOr extends Semigroup.BooleanOr implements Monoid<Boolean> {
        protected BooleanOr() {
            super();
        }

        @NotNull
        @Override
        public final Boolean identity() {
            return false;
        }
    }

    public static class BooleanAnd extends Semigroup.BooleanAnd implements Monoid<Boolean> {
        protected BooleanAnd() {
            super();
        }

        @NotNull
        @Override
        public final Boolean identity() {
            return true;
        }
    }
}
