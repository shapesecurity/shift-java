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

package com.shapesecurity.shift.js.minifier.passes.reduction;

import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.minifier.ReductionRule;
import com.shapesecurity.shift.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class ReplaceWhileWithFor extends ReductionRule {
  /* for any X and Y, replace while(X)Y with for(;X;)Y */
  public static final ReplaceWhileWithFor INSTANCE = new ReplaceWhileWithFor();

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull WhileStatement node) {
    return DirtyState.<Statement>dirty(new ForStatement(Maybe.nothing(), Maybe.just(node.test), Maybe.nothing(),
        node.body));
  }
}