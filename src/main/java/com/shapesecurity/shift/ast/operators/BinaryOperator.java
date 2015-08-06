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

package com.shapesecurity.shift.ast.operators;

import org.jetbrains.annotations.NotNull;

public enum BinaryOperator implements Operator {
    Sequence(",", Precedence.SEQUENCE),
    LogicalOr("||", Precedence.LOGICAL_OR),
    LogicalAnd("&&", Precedence.LOGICAL_AND),
    BitwiseOr("|", Precedence.BITWISE_OR),
    BitwiseXor("^", Precedence.BITWISE_XOR),
    BitwiseAnd("&", Precedence.BITWISE_AND),

    Plus("+", Precedence.ADDITIVE),
    Minus("-", Precedence.ADDITIVE),

    Equal("==", Precedence.EQUALITY),
    NotEqual("!=", Precedence.EQUALITY),
    StrictEqual("===", Precedence.EQUALITY),
    StrictNotEqual("!==", Precedence.EQUALITY),

    Mul("*", Precedence.MULTIPLICATIVE),
    Div("/", Precedence.MULTIPLICATIVE),
    Rem("%", Precedence.MULTIPLICATIVE),

    LessThan("<", Precedence.RELATIONAL),
    LessThanEqual("<=", Precedence.RELATIONAL),
    GreaterThan(">", Precedence.RELATIONAL),
    GreaterThanEqual(">=", Precedence.RELATIONAL),
    In("in", Precedence.RELATIONAL),
    Instanceof("instanceof", Precedence.RELATIONAL),

    Left("<<", Precedence.SHIFT),
    Right(">>", Precedence.SHIFT),
    UnsignedRight(">>>", Precedence.SHIFT);

    @NotNull
    private final String name;

    @NotNull
    private final Precedence precedence;

    private BinaryOperator(@NotNull String name, @NotNull Precedence precedence) {
        this.name = name;
        this.precedence = precedence;
    }

    @NotNull
    public Precedence getPrecedence() {
        return this.precedence;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }
}
