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
//import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.operators.PrefixOperator;
//import com.shapesecurity.shift.minifier.ExpansionRule;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class ExpandBooleanLiterals extends ExpansionRule {
//  /* expand true to !0 and false to !1 */
//  public static final ExpandBooleanLiterals INSTANCE = new ExpandBooleanLiterals();
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralBooleanExpression node) {
//    return DirtyState.dirty(new PrefixExpression(PrefixOperator.LogicalNot, new LiteralNumericExpression(
//        node.value ? 0 : 1)));
//  }
//}
