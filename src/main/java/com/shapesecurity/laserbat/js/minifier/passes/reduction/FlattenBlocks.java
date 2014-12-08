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

import com.shapesecurity.laserbat.functional.F;
import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.SwitchCase;
import com.shapesecurity.laserbat.js.ast.SwitchDefault;
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.minifier.ReductionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class FlattenBlocks extends ReductionRule {
  /* flatten blocks in statement position */
  public static final FlattenBlocks INSTANCE = new FlattenBlocks();
  private static final F<Statement, List<Statement>> flattenBlockStatements =
      iStatement -> iStatement instanceof BlockStatement ? ((BlockStatement) iStatement).block.statements : List.list(
          iStatement);
  private static final F<Statement, Boolean> isBlockStatement = iStatement -> iStatement instanceof BlockStatement;

  @Nonnull
  @Override
  public DirtyState<Block> transform(@Nonnull Block node) {
    List<Statement> flattenedStatements = node.statements.bind(flattenBlockStatements);
    return node.statements.exists(isBlockStatement) ? DirtyState.dirty(new Block(flattenedStatements)) :
           DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
    List<Statement> flattenedStatements = node.statements.bind(flattenBlockStatements);
    return node.statements.exists(isBlockStatement) ? DirtyState.dirty(new FunctionBody(node.directives,
        flattenedStatements)) : DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
    List<Statement> flattenedStatements = node.consequent.bind(flattenBlockStatements);
    return node.consequent.exists(isBlockStatement) ? DirtyState.dirty(new SwitchCase(node.test, flattenedStatements)) :
           DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
    List<Statement> flattenedStatements = node.consequent.bind(flattenBlockStatements);
    return node.consequent.exists(isBlockStatement) ? DirtyState.dirty(new SwitchDefault(flattenedStatements)) :
           DirtyState.clean(node);
  }
}
