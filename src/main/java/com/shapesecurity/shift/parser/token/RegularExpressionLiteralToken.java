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

package com.shapesecurity.shift.parser.token;

import com.shapesecurity.shift.parser.SourceRange;
import com.shapesecurity.shift.parser.Token;
import com.shapesecurity.shift.parser.TokenType;

import org.jetbrains.annotations.NotNull;

public class RegularExpressionLiteralToken extends Token {
  @NotNull
  private final String value;

  public RegularExpressionLiteralToken(@NotNull SourceRange slice, @NotNull String value) {
    super(TokenType.REGEXP, slice, false);
    this.value = value;
  }

  @NotNull
  @Override
  public String getValueString() {
    return this.value;
  }
}
