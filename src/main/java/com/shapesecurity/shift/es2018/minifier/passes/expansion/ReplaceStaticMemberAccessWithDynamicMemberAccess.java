///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.minifier.passes.expansion;
//
//import Expression;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
//import BinaryOperator;
//import com.shapesecurity.shift.ast.operators.PrefixOperator;
//import com.shapesecurity.shift.minifier.ExpansionRule;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class ReplaceStaticMemberAccessWithDynamicMemberAccess extends ExpansionRule {
//  /* replace some special static member accesses with a computed member access */
//  public static final ReplaceStaticMemberAccessWithDynamicMemberAccess INSTANCE =
//      new ReplaceStaticMemberAccessWithDynamicMemberAccess();
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull StaticMemberExpression node) {
//    switch (node.property.name) {
//    case "undefined":
//      return DirtyState.dirty(new ComputedMemberExpression(node.object, new PrefixExpression(
//          PrefixOperator.Void, new LiteralNumericExpression(0))));
//    case "true":
//    case "false":
//      return DirtyState.dirty(new ComputedMemberExpression(node.object, new PrefixExpression(
//          PrefixOperator.LogicalNot, new LiteralNumericExpression("true".equals(node.property.name) ? 0 : 1))));
//    case "Infinity":
//      return DirtyState.dirty(new ComputedMemberExpression(node.object, new BinaryExpression(
//          BinaryOperator.Div, new LiteralNumericExpression(1), new LiteralNumericExpression(0))));
//    }
//    return DirtyState.clean(node);
//  }
//}
