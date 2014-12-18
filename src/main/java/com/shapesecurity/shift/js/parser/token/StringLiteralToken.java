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

package com.shapesecurity.shift.js.parser.token;

import com.shapesecurity.shift.js.parser.SourceRange;
import com.shapesecurity.shift.js.parser.Token;
import com.shapesecurity.shift.js.parser.TokenType;

import javax.annotation.Nonnull;

public class StringLiteralToken extends Token {
  private final String value;

  public StringLiteralToken(@Nonnull SourceRange slice, @Nonnull String value, boolean octal) {
    super(TokenType.STRING, slice, octal);
    this.value = value;
  }

  @Nonnull
  @Override
  public String getValueString() {
    return this.value;
  }
}