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
//import com.shapesecurity.functional.data.Maybe;
//import Statement;
//import com.shapesecurity.shift.ast.statement.ForStatement;
//import com.shapesecurity.shift.ast.statement.WhileStatement;
//import com.shapesecurity.shift.minifier.ReductionRule;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class ReplaceWhileWithFor extends ReductionRule {
//  /* for any X and Y, replace while(X)Y with for(;X;)Y */
//  public static final ReplaceWhileWithFor INSTANCE = new ReplaceWhileWithFor();
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull WhileStatement node) {
//    return DirtyState.dirty(new ForStatement(Maybe.empty(), Maybe.of(node.test), Maybe.empty(),
//        node.body));
//  }
//}
