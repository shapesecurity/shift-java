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

package com.shapesecurity.laserbat.js.path;

import javax.annotation.Nonnull;

public class Branch {
  @Nonnull
  public final BranchType branchType;
  public final int index;

  public Branch(@Nonnull BranchType branchType) {
    this(branchType, 0);
  }

  public Branch(@Nonnull BranchType branchType, int index) {
    this.branchType = branchType;
    this.index = index;
  }

  @Override
  public int hashCode() {
    return branchType.hashCode() ^ (index << 15) ^ (index >>> (32 - 15));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Branch) {
      Branch br = (Branch) obj;
      return br.branchType == this.branchType && br.index == this.index;
    }
    return false;
  }
}
