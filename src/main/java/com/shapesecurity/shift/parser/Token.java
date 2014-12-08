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

package com.shapesecurity.shift.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Token {
  @NotNull
  public final TokenType type;
  @NotNull
  public final SourceRange slice;
  public final boolean octal;
  @Nullable
  public SourceRange leadingWhitespace;

  protected Token(@NotNull TokenType type, @NotNull SourceRange slice, boolean octal) {
    this.octal = octal;
    this.type = type;
    this.slice = slice;
  }

  @NotNull
  public abstract CharSequence getValueString();

  @Override
  @NotNull
  public String toString() {
    return String.valueOf(this.slice.getString());
  }
}
