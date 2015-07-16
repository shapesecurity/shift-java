//package com.shapesecurity.shift.codegen;
//
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.Expression;
//import com.shapesecurity.shift.ast.operators.Precedence;
//
//import org.jetbrains.annotations.NotNull;
//
//public class CodeRepFactory {
//  protected static final CodeRep[] EMPTY = new CodeRep[0];
//
//  public CodeRepFactory() { }// CodeRep factory methods
//
//  @NotNull
//  public CodeRep empty() {
//    return new CodeRep.Empty();
//  }
//
//  @NotNull
//  public CodeRep expr(@NotNull Expression node, @NotNull Precedence precedence, @NotNull CodeRep a) {
//    return node.getPrecedence().ordinal() < precedence.ordinal() ? paren(a) : a;
//  }
//
//  @NotNull
//  public CodeRep token(@NotNull String token) {
//    return new CodeRep.Token(token);
//  }
//
//  @NotNull
//  public CodeRep num(double value) {
//    return new CodeRep.Number(value);
//  }
//
//  @NotNull
//  public CodeRep paren(@NotNull CodeRep rep) {
//    return new CodeRep.Paren(rep);
//  }
//
//  @NotNull
//  public CodeRep bracket(@NotNull CodeRep rep) {
//    return new CodeRep.Bracket(rep);
//  }
//
//  @NotNull
//  public CodeRep noIn(@NotNull CodeRep rep) {
//    return new CodeRep.NoIn(rep);
//  }
//
//  @NotNull
//  public CodeRep testIn(@NotNull CodeRep state) {
//    return state.containsIn ? new CodeRep.ContainsIn(state) : state;
//  }
//
//  @NotNull
//  public CodeRep seq(@NotNull CodeRep[] reps) {
//    return new CodeRep.Seq(reps);
//  }
//
//  @NotNull
//  public CodeRep init(@NotNull CodeRep id, @NotNull Maybe<CodeRep> i) {
//    return i.maybe(id, init -> new CodeRep.Init(id, init));
//  }
//
//  @NotNull
//  public CodeRep semi() {
//    return new CodeRep.Semi();
//  }
//
//  @NotNull
//  public CodeRep commaSep(@NotNull CodeRep[] pieces) {
//    return new CodeRep.CommaSep(pieces);
//  }
//
//  @NotNull
//  public CodeRep brace(@NotNull CodeRep rep) {
//    return new CodeRep.Brace(rep);
//  }
//
//  @NotNull
//  public CodeRep semiOp() {
//    return new CodeRep.SemiOp();
//  }
//
//  @NotNull
//  public final CodeRep commaSep(@NotNull ImmutableList<CodeRep> reps) {
//    return commaSep(reps.toArray(EMPTY));
//  }
//
//  @NotNull
//  public final CodeRep seq(@NotNull ImmutableList<CodeRep> reps) {
//    return seq(reps.toArray(EMPTY));
//  }
//}
