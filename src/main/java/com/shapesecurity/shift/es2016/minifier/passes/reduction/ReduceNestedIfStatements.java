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
//package com.shapesecurity.shift.minifier.passes.reduction;
//
//import Statement;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import BinaryOperator;
//import com.shapesecurity.shift.ast.statement.IfStatement;
//import com.shapesecurity.shift.minifier.ReductionRule;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class ReduceNestedIfStatements extends ReductionRule {
//  /* combine tests (using &&) of nested IfStatement nodes */
//  public static final ReduceNestedIfStatements INSTANCE = new ReduceNestedIfStatements();
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull IfStatement node) {
//    if (node.alternate.isNothing() &&
//        node.consequent instanceof IfStatement) {
//      IfStatement consequent = (IfStatement) node.consequent;
//      if (consequent.alternate.isNothing()) {
//        return DirtyState.dirty(new IfStatement(new BinaryExpression(BinaryOperator.LogicalAnd, node.test,
//            consequent.test), consequent.consequent));
//      }
//    }
//    return DirtyState.clean(node);
//  }
//}
