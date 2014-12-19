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
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.types.GenType;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.path.Branch;

public abstract class Node {
  private final Thunk<Integer> hashCodeThunk = Thunk.from(this::calcHashCode);

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
  public abstract Type type();

  @Nonnull
  public GenType genType() {
    return type();
  }

  @Nonnull
  public Maybe<Node> get(@Nonnull Branch branch) {
    return Maybe.fromNullable(branch.view(this));
  }

  @Nonnull
  public Node set(NonEmptyList<ReplacementChild> list) {
    return list.foldLeft((me, rep) -> rep.branch.set(me, rep.child), this);
  }
}
