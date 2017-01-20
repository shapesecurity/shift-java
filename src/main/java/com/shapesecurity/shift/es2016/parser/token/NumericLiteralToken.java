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

package com.shapesecurity.shift.es2016.parser.token;

import com.shapesecurity.shift.es2016.parser.TokenType;
import com.shapesecurity.shift.es2016.utils.D2A;
import com.shapesecurity.shift.es2016.parser.SourceRange;
import com.shapesecurity.shift.es2016.parser.Token;

import org.jetbrains.annotations.NotNull;

public final class NumericLiteralToken extends Token {
    public final double value;
    public final boolean octal;
    public final boolean noctal;

    public NumericLiteralToken(@NotNull SourceRange slice, double value) {
        this(slice, value, false, false);
    }

    public NumericLiteralToken(@NotNull SourceRange slice, double value, boolean octal, boolean noctal) {
        super(TokenType.NUMBER, slice);
        this.value = value;
        this.octal = octal;
        this.noctal = octal && noctal;
    }

    @NotNull
    @Override
    public String getValueString() {
        return D2A.d2a(this.value);
    }
}
