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

import com.shapesecurity.shift.path.Branch;

import org.jetbrains.annotations.NotNull;

public class ReplacementChild {
  @NotNull
  public final Branch branch; // branch to replace
  @NotNull
  public final Node child; // new child node for branch

  public ReplacementChild(@NotNull Branch branch, @NotNull Node child) {
    this.branch = branch;
    this.child = child;
  }
}
