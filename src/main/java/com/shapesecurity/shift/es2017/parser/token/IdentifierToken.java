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

package com.shapesecurity.shift.es2017.parser.token;

import com.shapesecurity.shift.es2017.parser.TokenType;
import com.shapesecurity.shift.es2017.parser.SourceRange;

import javax.annotation.Nonnull;

public class IdentifierToken extends IdentifierLikeToken {

    @Nonnull
    private final CharSequence name;
    public final boolean escaped;

    public IdentifierToken(@Nonnull SourceRange slice, @Nonnull CharSequence name, boolean escaped) {
        super(TokenType.IDENTIFIER, slice);
        this.name = name;
        this.escaped = escaped;
    }

    @Override
    @Nonnull
    public String toString() {
        return String.valueOf(this.name);
    }
}
