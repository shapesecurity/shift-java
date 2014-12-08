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

package com.shapesecurity.laserbat.js.minifier.passes.reduction;

import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.laserbat.js.minifier.ReductionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class RemoveEmptyTrailingDefault extends ReductionRule {
  /* remove SwitchDefault when its consequent is empty and it is the last SwitchCase */
  public static final RemoveEmptyTrailingDefault INSTANCE = new RemoveEmptyTrailingDefault();

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull final SwitchStatementWithDefault node) {
    if (node.defaultCase.consequent.isEmpty() && node.postDefaultCases.isEmpty()) {
      return DirtyState.<Statement>dirty(new SwitchStatement(node.discriminant, node.preDefaultCases));
    }
    return DirtyState.<Statement>clean(node);
  }
}
