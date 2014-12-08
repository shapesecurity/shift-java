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

package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.path.Branch;

import org.jetbrains.annotations.NotNull;

public final class Reference {
  @NotNull
  public final Identifier node;
  @NotNull
  public final List<Branch> path;
  @NotNull
  public final Accessibility accessibility;

  public Reference(@NotNull Identifier node, @NotNull List<Branch> path, @NotNull Accessibility accessibility) {
    this.node = node;
    this.path = path;
    this.accessibility = accessibility;
  }

  @NotNull
  public final Reference withReadability() {
    return new Reference(this.node, this.path, this.accessibility.withReadability());
  }

  @NotNull
  public final Reference withWritability() {
    return new Reference(this.node, this.path, this.accessibility.withWritability());
  }
}
