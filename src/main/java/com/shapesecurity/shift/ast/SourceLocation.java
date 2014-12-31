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

package com.shapesecurity.shift.ast;

import com.shapesecurity.shift.parser.SourceRange;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceLocation {
  @Nullable
  public final SourceRange source;

  public final int line, column, offset;

  public SourceLocation(int line, int column, int offset) {
    this.line = line;
    this.column = column;
    this.offset = offset;
    this.source = null;
  }

  private SourceLocation(@Nullable SourceRange source, int line, int column, int offset) {
    this.line = line;
    this.column = column;
    this.offset = offset;
    this.source = source;
  }

  @NotNull
  public SourceLocation withSourceRange(@NotNull SourceRange sourceRange) {
    return new SourceLocation(sourceRange, this.line, this.column, this.offset);
  }
}
