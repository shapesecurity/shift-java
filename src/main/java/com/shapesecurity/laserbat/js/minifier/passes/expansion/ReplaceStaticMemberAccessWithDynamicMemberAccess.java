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
import com.shapesecurity.laserbat.js.ast.expression.BinaryExpression;
import com.shapesecurity.laserbat.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.laserbat.js.ast.operators.Multiplicative;
import com.shapesecurity.laserbat.js.ast.operators.PrefixOperator;
import com.shapesecurity.laserbat.js.minifier.ExpansionRule;
import com.shapesecurity.laserbat.js.visitor.DirtyState;

import javax.annotation.Nonnull;

public class ReplaceStaticMemberAccessWithDynamicMemberAccess extends ExpansionRule {
  /* replace some special static member accesses with a computed member access */
  public static final ReplaceStaticMemberAccessWithDynamicMemberAccess INSTANCE =
      new ReplaceStaticMemberAccessWithDynamicMemberAccess();

  @Nonnull
  @Override
  public DirtyState<Expression> transform(@Nonnull StaticMemberExpression node) {
    switch (node.property.name) {
    case "undefined":
      return DirtyState.<Expression>dirty(new ComputedMemberExpression(node.object, new PrefixExpression(
          PrefixOperator.Void, new LiteralNumericExpression(0))));
    case "true":
    case "false":
      return DirtyState.<Expression>dirty(new ComputedMemberExpression(node.object, new PrefixExpression(
          PrefixOperator.LogicalNot, new LiteralNumericExpression("true".equals(node.property.name) ? 0 : 1))));
    case "Infinity":
      return DirtyState.<Expression>dirty(new ComputedMemberExpression(node.object, new BinaryExpression(
          Multiplicative.Div, new LiteralNumericExpression(1), new LiteralNumericExpression(0))));
    }
    return DirtyState.<Expression>clean(node);
  }
}
