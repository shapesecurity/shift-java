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

package com.shapesecurity.laserbat.js.visitor;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Monoid;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.path.Branch;

import javax.annotation.Nonnull;

/**
 * This class is an example of using reducing. To make a counter of certain leaf type of
 * element, simply return 1 in the corresponding reduce method.
 */
public class Counter extends MonoidalReducer<Integer, Monoid.IntegerAdditive> {
  public Counter() {
    super(Monoid.INTEGER_ADDITIVE);
  }

  public static class IdentifierCounter extends Counter {
    @Nonnull
    @Override
    public Integer reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
      return 1;
    }
  }
}
