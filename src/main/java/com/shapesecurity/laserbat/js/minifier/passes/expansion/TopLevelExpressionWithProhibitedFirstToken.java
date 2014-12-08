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

package com.shapesecurity.laserbat.js.minifier.passes.expansion;

import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.expression.CallExpression;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.MemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ObjectExpression;
import com.shapesecurity.laserbat.js.ast.expression.PostfixExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.operators.PrefixOperator;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.minifier.ExpansionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class TopLevelExpressionWithProhibitedFirstToken extends ExpansionRule {
  /* prefixes function expressions and object literals in statement position with ! */
  public static final TopLevelExpressionWithProhibitedFirstToken INSTANCE =
      new TopLevelExpressionWithProhibitedFirstToken();

  private static boolean startsWithProhibitedToken(@Nonnull Expression expression) {
    if (expression instanceof FunctionExpression || expression instanceof ObjectExpression) {
      return true;
    }
    if (expression instanceof PostfixExpression) {
      return startsWithProhibitedToken(((PostfixExpression) expression).operand);
    }
    if (expression instanceof CallExpression) {
      return startsWithProhibitedToken(((CallExpression) expression).callee);
    }
    if (expression instanceof MemberExpression) {
      return startsWithProhibitedToken(((MemberExpression) expression).object);
    }
    return false;
  }

  @Nonnull
  @Override
  public DirtyState<Statement> transform(@Nonnull ExpressionStatement node) {
    if (startsWithProhibitedToken(node.expression)) {
      return DirtyState.dirty((Statement) new ExpressionStatement(new PrefixExpression(PrefixOperator.LogicalNot,
          node.expression)));
    }
    return DirtyState.clean((Statement) node);
  }
}
