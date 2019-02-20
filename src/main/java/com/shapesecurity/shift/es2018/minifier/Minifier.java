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
//package com.shapesecurity.shift.minifier;
//
//import com.shapesecurity.functional.data.ImmutableList;
//import Script;
//import com.shapesecurity.shift.minifier.passes.expansion.ExpandBooleanLiterals;
//import com.shapesecurity.shift.minifier.passes.expansion.ReplaceStaticMemberAccessWithDynamicMemberAccess;
//import com.shapesecurity.shift.minifier.passes.expansion.TopLevelExpressionWithProhibitedFirstToken;
//import com.shapesecurity.shift.minifier.passes.reduction.FlattenBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.ReduceNestedIfStatements;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyStatements;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveEmptyTrailingDefault;
//import com.shapesecurity.shift.minifier.passes.reduction.RemoveSingleStatementBlocks;
//import com.shapesecurity.shift.minifier.passes.reduction.ReplaceWhileWithFor;
//import com.shapesecurity.shift.visitor.FixPointTransformer;
//
//import javax.annotation.Nonnull;
//
//public class Minifier {
//  private static final FixPointTransformer REDUCTION = new FixPointTransformer(new ComposedRule<>(
//      new ReductionRule[]{FlattenBlocks.INSTANCE, ReduceNestedIfStatements.INSTANCE, RemoveEmptyBlocks.INSTANCE,
//                          RemoveEmptyStatements.INSTANCE, RemoveEmptyTrailingDefault.INSTANCE,
//                          RemoveSingleStatementBlocks.INSTANCE, ReplaceWhileWithFor.INSTANCE,}));
//  private static final FixPointTransformer EXPANSION = new FixPointTransformer(new ComposedRule<>(
//      new ExpansionRule[]{ReplaceStaticMemberAccessWithDynamicMemberAccess.INSTANCE, ExpandBooleanLiterals.INSTANCE,
//                          TopLevelExpressionWithProhibitedFirstToken.INSTANCE,}));
//
//  @Nonnull
//  public static Script minify(@Nonnull Script script) {
//    return EXPANSION.transform(REDUCTION.transform(script, ImmutableList.empty()), ImmutableList.empty());
//  }
//
//  public static Script minify(
//      @Nonnull Script script,
//      @Nonnull ReductionRule[] reductionRules,
//      @Nonnull ExpansionRule[] expansionRules) {
//    FixPointTransformer reduction = new FixPointTransformer(new ComposedRule<>(reductionRules));
//    FixPointTransformer expansion = new FixPointTransformer(new ComposedRule<>(expansionRules));
//    return expansion.transform(reduction.transform(script, ImmutableList.empty()), ImmutableList.empty());
//  }
//
//  public static Script minify(
//      @Nonnull Script script,
//      @Nonnull ImmutableList<ReductionRule> reductionRules,
//      @Nonnull ImmutableList<ExpansionRule> expansionRules) {
//    ReductionRule[] reductionRulesArray = reductionRules.toArray(new ReductionRule[reductionRules.length]);
//    ExpansionRule[] expansionRulesArray = expansionRules.toArray(new ExpansionRule[expansionRules.length]);
//    FixPointTransformer reduction = new FixPointTransformer(new ComposedRule<>(reductionRulesArray));
//    FixPointTransformer expansion = new FixPointTransformer(new ComposedRule<>(expansionRulesArray));
//    return expansion.transform(reduction.transform(script, ImmutableList.empty()), ImmutableList.empty());
//  }
//}
