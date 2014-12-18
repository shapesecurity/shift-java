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

package com.shapesecurity.shift.js.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.Thunk;
import com.shapesecurity.shift.functional.data.HashCodeBuilder;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.path.Branch;

public abstract class Node {
  private final Thunk<Integer> hashCodeThunk = Thunk.from(this::calcHashCode);

  // rebuild list with changed elements stored in an array, sharing the unchanged portion of the list
  @Nonnull
  private static <N> List<N> internalReplaceIndex(@Nonnull List<N> lst, int slotsLeft, int max, @Nonnull N[] changed) {
    if (slotsLeft == -1) {
      return lst;
    } else {
      NonEmptyList<N> lstNE = (NonEmptyList<N>) lst;
      int index = max - slotsLeft;
      N replacement = (changed[index] == null) ? lstNE.head : changed[index];
      return internalReplaceIndex(lstNE.tail(), slotsLeft - 1, max, changed).cons(replacement);
    }
  }

  @Nonnull
  public static <N> List<N> replaceIndex(@Nonnull List<N> lst, int max, @Nonnull N[] changed) {
    return internalReplaceIndex(lst, max, max, changed);
  }

  private int calcHashCode() {
    int start = 0;
    start = HashCodeBuilder.put(start, this.getClass().getName());
    Field[] fields = this.getClass().getFields();
    for (Field field : fields) {
      if (field.getModifiers() == (Modifier.FINAL | Modifier.PUBLIC)) {
        try {
          start = HashCodeBuilder.put(start, field.get(this));
        } catch (IllegalAccessException ignored) {
          start = HashCodeBuilder.put(start, -1);
        }
      }
    }
    return start;
  }

  // hashCode has to be overwritten in order to maintain contract between with Object.equals and Object.hashCode
  @Override
  public final int hashCode() {
    return this.hashCodeThunk.get();
  }

  @Nonnull
  public abstract Maybe<? extends Node> branchChild(@Nonnull Branch branch);

  @Nonnull
  public abstract Node replicate(@Nonnull List<? extends ReplacementChild> children);

  @Nonnull
  public abstract Type type();
}
