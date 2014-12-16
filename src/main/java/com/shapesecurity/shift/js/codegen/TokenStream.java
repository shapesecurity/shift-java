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

package com.shapesecurity.shift.js.codegen;

import com.shapesecurity.shift.js.utils.D2A;
import com.shapesecurity.shift.js.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenStream {
  @Nonnull
  private final StringBuilder writer;
  private char lastChar = (char) -1;
  @Nullable
  private String lastNumber;
  private boolean optionalSemi;

  public TokenStream(@Nonnull StringBuilder writer) {
    this.writer = writer;
  }

  @Nonnull
  private static String numberDot(@Nonnull String fragment) {
    if (fragment.indexOf('.') < 0 && fragment.indexOf('e') < 0) {
      return "..";
    }
    return ".";
  }

  public void putNumber(double number) {
    String tokenStr = D2A.d2a(number);
    put(tokenStr);
    this.lastNumber = tokenStr;
  }

  public void putOptionalSemi() {
    this.optionalSemi = true;
  }

  @SuppressWarnings("LiteralAsArgToStringEquals")
  public void put(@Nonnull CharSequence tokenStr) {
    if (this.optionalSemi) {
      this.optionalSemi = false;
      if (!tokenStr.toString().equals("}")) {
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
        lastChar == '/' && rightChar == 'i') {
      this.writer.append(' ');
    }

    this.writer.append(tokenStr);
  }
}
