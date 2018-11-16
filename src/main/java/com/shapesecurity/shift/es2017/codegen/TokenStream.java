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

package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.shift.es2017.utils.D2A;
import com.shapesecurity.shift.es2017.utils.Utils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.regex.Pattern;

public class TokenStream {
    @Nonnull
    protected final StringBuilder writer;
    protected int lastCodePoint = 0;
    @Nullable
    protected String lastNumber;
    protected boolean optionalSemi;

    protected static final Pattern digitPattern = Pattern.compile("\\d+");

    public TokenStream(@Nonnull StringBuilder writer) {
        this.writer = writer;
    }

    @Nonnull
    protected static boolean numberNeedsDoubleDot(@Nonnull String fragment) {
        return digitPattern.matcher(fragment).matches();
    }

    public void putNumber(double number) {
        String tokenStr = D2A.shortD2a(number);
        put(tokenStr);
        this.lastNumber = tokenStr;
    }

    public void putOptionalSemi() {
        this.optionalSemi = true;
    }

    public void putRaw(@Nonnull String tokenStr) {
        this.writer.append(tokenStr);
    }

    public void put(@Nonnull String tokenStr) {
        if (this.optionalSemi) {
            this.optionalSemi = false;
            if (!tokenStr.equals("}")) {
                this.put(";");
            }
        }
        if (this.lastNumber != null && tokenStr.length() == 1) {
            if (String.valueOf(tokenStr).equals(".")) {
                this.writer.append(numberNeedsDoubleDot(this.lastNumber) ? ".." : ".");
                this.lastNumber = null;
                this.lastCodePoint = '.';
                return;
            }
        }
        this.lastNumber = null;
        int rightCodePoint = tokenStr.codePointAt(0);
        int lastCodePoint = this.lastCodePoint;
        char lastChar = tokenStr.charAt(tokenStr.length() - 1);
        if (lastChar >= 0xDC00 && lastChar <= 0xDFFF) {
            this.lastCodePoint = tokenStr.codePointAt(tokenStr.length() - 2);
        } else {
            this.lastCodePoint = lastChar;
        }
        if ((lastCodePoint == '+' || lastCodePoint == '-') && lastCodePoint == rightCodePoint ||
                Utils.isIdentifierPart(lastCodePoint) && Utils.isIdentifierPart(rightCodePoint) ||
                lastCodePoint == '/' && (rightCodePoint == 'i' || rightCodePoint == '/')) {
            this.writer.append(' ');
        }
        if (this.writer.length() >= 2 && tokenStr.equals("--") && this.writer.substring(this.writer.length() - 2,
                this.writer.length()).equals("<!")) {
            this.writer.append(' ');
        }

        this.writer.append(tokenStr);
    }
}
