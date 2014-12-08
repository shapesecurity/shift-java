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
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.minifier.ReductionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class RemoveSingleStatementBlocks extends ReductionRule {
  /* replace single-statement blocks with the contained statement */
  public static final RemoveSingleStatementBlocks INSTANCE = new RemoveSingleStatementBlocks();

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull BlockStatement node) {
    return node.block.statements.isNotEmpty() && node.block.statements.maybeTail().just().isEmpty() ? DirtyState.dirty(
        node.block.statements.maybeHead().just()) : DirtyState.<Statement>clean(node);
  }
}
