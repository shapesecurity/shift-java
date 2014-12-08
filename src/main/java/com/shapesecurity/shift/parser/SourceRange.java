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

public class SourceRange implements CharSequence {
  public final int start;
  public final int end;
  @NotNull
  private final CharSequence source;

  public SourceRange(int start, int end, @NotNull CharSequence source) {
    this.start = start;
    this.end = end;
    this.source = source;
  }

  @Override
  public int length() {
    return this.end - this.start;
  }

  @Override
  public char charAt(int index) {
    if (index > this.end - this.start) {
      throw new IndexOutOfBoundsException();
    }
    return this.source.charAt(index + this.start);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return new SourceRange(this.start + start, end + this.start, this.source);
  }

  @NotNull
  public CharSequence getString() {
    return this.source.subSequence(this.start, this.end);
  }

  @NotNull
  @Override
  public String toString() {
    return String.valueOf(this.getString());
  }
}
