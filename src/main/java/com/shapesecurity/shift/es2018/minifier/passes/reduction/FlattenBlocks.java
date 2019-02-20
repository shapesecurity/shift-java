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
//import com.shapesecurity.functional.F;
//import com.shapesecurity.functional.data.ImmutableList;
//import Block;
//import FunctionBody;
//import Statement;
//import SwitchCase;
//import SwitchDefault;
//import com.shapesecurity.shift.ast.statement.BlockStatement;
//import com.shapesecurity.shift.minifier.ReductionRule;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class FlattenBlocks extends ReductionRule {
//  /* flatten blocks in statement position */
//  public static final FlattenBlocks INSTANCE = new FlattenBlocks();
//  private static final F<Statement, ImmutableList<Statement>> flattenBlockStatements =
//      iStatement -> iStatement instanceof BlockStatement ? ((BlockStatement) iStatement).block.statements : ImmutableList
//          .list(
//              iStatement);
//  private static final F<Statement, Boolean> isBlockStatement = iStatement -> iStatement instanceof BlockStatement;
//
//  @Nonnull
//  @Override
//  public DirtyState<Block> transform(@Nonnull Block node) {
//    ImmutableList<Statement> flattenedStatements = node.statements.bind(flattenBlockStatements);
//    return node.statements.exists(isBlockStatement) ? DirtyState.dirty(new Block(flattenedStatements)) :
//           DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
//    ImmutableList<Statement> flattenedStatements = node.statements.bind(flattenBlockStatements);
//    return node.statements.exists(isBlockStatement) ? DirtyState.dirty(new FunctionBody(node.directives,
//        flattenedStatements)) : DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
//    ImmutableList<Statement> flattenedStatements = node.consequent.bind(flattenBlockStatements);
//    return node.consequent.exists(isBlockStatement) ? DirtyState.dirty(new SwitchCase(node.test, flattenedStatements)) :
//           DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
//    ImmutableList<Statement> flattenedStatements = node.consequent.bind(flattenBlockStatements);
//    return node.consequent.exists(isBlockStatement) ? DirtyState.dirty(new SwitchDefault(flattenedStatements)) :
//           DirtyState.clean(node);
//  }
//}
