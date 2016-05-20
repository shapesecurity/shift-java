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

package com.shapesecurity.shift.codegen;

import com.shapesecurity.shift.utils.D2A;
import com.shapesecurity.shift.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TokenStream {
    @NotNull
    protected final StringBuilder writer;
    protected char lastChar = (char) -1;
    @Nullable
    protected String lastNumber;
    protected boolean optionalSemi;

    public TokenStream(@NotNull StringBuilder writer) {
        this.writer = writer;
    }

    @NotNull
    private static String numberDot(@NotNull String fragment) {
        if (fragment.indexOf('.') < 0 && fragment.indexOf('e') < 0) {
            return "..";
        }
        return ".";
    }

    public void putNumber(double number) {
        String tokenStr = D2A.shortD2a(number);
        put(tokenStr);
        this.lastNumber = tokenStr;
    }

    public void putOptionalSemi() {
        this.optionalSemi = true;
    }

    @SuppressWarnings("LiteralAsArgToStringEquals")
    public void put(@NotNull String tokenStr) {
        if (this.optionalSemi) {
            this.optionalSemi = false;
            if (!tokenStr.equals("}")) {
                this.put(";");
            }
        }
        if (this.lastNumber != null && tokenStr.length() == 1) {
            if (String.valueOf(tokenStr).equals(".")) {
                this.writer.append(numberDot(this.lastNumber));
                this.lastNumber = null;
                this.lastChar = '.';
                return;
            }
        }
        this.lastNumber = null;
        char rightChar = tokenStr.charAt(0);
        char lastChar = this.lastChar;
        this.lastChar = tokenStr.charAt(tokenStr.length() - 1);
        if ((lastChar == '+' || lastChar == '-') && lastChar == rightChar ||
                Utils.isIdentifierPart(lastChar) && Utils.isIdentifierPart(rightChar) ||
                lastChar == '/' && (rightChar == 'i' || rightChar == '/')) {
            this.writer.append(' ');
        }
        if (this.writer.length() >= 2 && tokenStr.equals("--") && this.writer.substring(this.writer.length() - 2,
                this.writer.length()).equals("<!")) {
            this.writer.append(' ');
        }

        this.writer.append(tokenStr);
    }
}
