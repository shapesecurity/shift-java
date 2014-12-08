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

package com.shapesecurity.shift.minifier.passes.reduction;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
import com.shapesecurity.shift.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.ast.statement.EmptyStatement;
import com.shapesecurity.shift.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.ast.statement.IfStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatement;
import com.shapesecurity.shift.ast.statement.WhileStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.minifier.ReductionRule;
import com.shapesecurity.shift.visitor.DirtyState;

import org.jetbrains.annotations.NotNull;

public class RemoveEmptyStatements extends ReductionRule {
  /* remove empty statements */
  public static final RemoveEmptyStatements INSTANCE = new RemoveEmptyStatements();
  private static final F<Statement, Boolean> isNotEmptyStatement =
      iStatement -> !(iStatement instanceof EmptyStatement);

  private RemoveEmptyStatements() {
    super();
  }

  @NotNull
  @Override
  public DirtyState<Block> transform(@NotNull Block node) {
    List<Statement> filteredStatements = node.statements.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.statements.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new Block(filteredStatements));
  }

  @NotNull
  @Override
  public DirtyState<FunctionBody> transform(@NotNull FunctionBody node) {
    List<Statement> filteredStatements = node.statements.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.statements.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new FunctionBody(node.directives, filteredStatements));
  }

  @NotNull
  @Override
  public DirtyState<SwitchCase> transform(@NotNull SwitchCase node) {
    List<Statement> filteredStatements = node.consequent.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.consequent.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new SwitchCase(node.test, filteredStatements));
  }

  @NotNull
  @Override
  public DirtyState<SwitchDefault> transform(@NotNull SwitchDefault node) {
    List<Statement> filteredStatements = node.consequent.filter(isNotEmptyStatement);
    return filteredStatements.length() == node.consequent.length() ? DirtyState.clean(node) : DirtyState.dirty(
        new SwitchDefault(filteredStatements));
  }

	/* replace statements that contain empty statements with reduced equivalents */

  @NotNull
  @Override
  public DirtyState<Statement> transform(@NotNull DoWhileStatement node) {
    return node.body instanceof EmptyStatement ? DirtyState.<Statement>dirty(new WhileStatement(node.test, node.body)) :
           DirtyState.<Statement>clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> transform(@NotNull IfStatement node) {
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

  @NotNull
  @Override
  public DirtyState<Statement> transform(@NotNull WithStatement node) {
    return node.body instanceof EmptyStatement ? DirtyState.dirty(new ExpressionStatement(node.object)) :
           DirtyState.clean(node);
  }

  // TODO(michael F.): switch with no preDefaultCases or only a default case
  @NotNull
  @Override
  public DirtyState<Statement> transform(@NotNull SwitchStatement node) {
    return DirtyState.clean(node);
  }
}
