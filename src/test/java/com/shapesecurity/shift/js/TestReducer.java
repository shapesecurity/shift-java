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

package com.shapesecurity.shift.js;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.path.Branch;

public abstract class TestReducer extends TestReducerWithPath {

  protected abstract void accept(@Nonnull Node node);

  @Override
  protected void accept(@Nonnull Node node, @Nonnull List<Branch> path) {
    accept(node);
  }
}
