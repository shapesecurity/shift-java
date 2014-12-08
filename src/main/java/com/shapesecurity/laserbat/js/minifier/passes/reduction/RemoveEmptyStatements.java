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
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.operators.PrefixOperator;
import com.shapesecurity.laserbat.js.ast.statement.DoWhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.EmptyStatement;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.ast.statement.IfStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatement;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.minifier.ReductionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class RemoveEmptyStatements extends ReductionRule {
  /* remove empty statements */
  public static final RemoveEmptyStatements INSTANCE = new RemoveEmptyStatements();
  private static final F<Statement, Boolean> isNotEmptyStatement =
      iStatement -> !(iStatement instanceof EmptyStatement);

  private RemoveEmptyStatements() {
    super();
  }

  @Nonnull
  @Override
  public DirtyState<Block> transform(@Nonnull Block node) {
    List<Statement> filteredStatements = node.statements.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.statements.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new Block(filteredStatements));
  }

  @Nonnull
  @Override
  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
    List<Statement> filteredStatements = node.statements.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.statements.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new FunctionBody(node.directives, filteredStatements));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
    List<Statement> filteredStatements = node.consequent.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.consequent.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new SwitchCase(node.test, filteredStatements));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
    List<Statement> filteredStatements = node.consequent.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.consequent.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new SwitchDefault(filteredStatements));
  }

	/* replace statements that contain empty statements with reduced equivalents */

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull DoWhileStatement node) {
    return node.body instanceof EmptyStatement ? DirtyState.<Statement>dirty(new WhileStatement(node.test, node.body)) :
           DirtyState.<Statement>clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull IfStatement node) {
    boolean consequentEmpty = node.consequent instanceof EmptyStatement;
    boolean alternateEmpty = node.alternate.isJust() && node.alternate.just() instanceof EmptyStatement;
    if ((alternateEmpty || node.alternate.isNothing()) && consequentEmpty) {
      return DirtyState.dirty(new ExpressionStatement(node.test));
    }
    if (alternateEmpty) {
      return DirtyState.dirty(new IfStatement(node.test, node.consequent));
    }
    if (consequentEmpty) {
      return DirtyState.dirty(new IfStatement(new PrefixExpression(PrefixOperator.LogicalNot, node.test),
          node.alternate.just()));
    }
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull WithStatement node) {
    return node.body instanceof EmptyStatement ? DirtyState.dirty(new ExpressionStatement(node.object)) :
           DirtyState.clean(node);
  }

  // TODO(michael F.): switch with no preDefaultCases or only a default case
  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull SwitchStatement node) {
    return DirtyState.clean(node);
  }
}
