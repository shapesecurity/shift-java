package com.shapesecurity.shift.js.codegen;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.operators.Precedence;

public class CodeRepFactory {
  public CodeRepFactory() { }// CodeRep factory methods

  @Nonnull
  public CodeRep empty() {
    return new CodeRep.Empty();
  }

  @Nonnull
  public CodeRep expr(@Nonnull Expression node, @Nonnull Precedence precedence, @Nonnull CodeRep a) {
    return node.getPrecedence().ordinal() < precedence.ordinal() ? paren(a) : a;
  }

  @Nonnull
  public CodeRep token(@Nonnull String token) {
    return new CodeRep.Token(token);
  }

  @Nonnull
  public CodeRep num(double value) {
    return new CodeRep.Number(value);
  }

  @Nonnull
  public CodeRep paren(@Nonnull CodeRep rep) {
    return new CodeRep.Paren(rep);
  }

  @Nonnull
  public CodeRep bracket(@Nonnull CodeRep rep) {
    return new CodeRep.Bracket(rep);
  }

  @Nonnull
  public CodeRep noIn(@Nonnull CodeRep rep) {
    return new CodeRep.NoIn(rep);
  }

  @Nonnull
  public CodeRep testIn(@Nonnull CodeRep state) {
    return state.containsIn ? new CodeRep.ContainsIn(state) : state;
  }

  @Nonnull
  public CodeRep seq(@Nonnull List<CodeRep> reps) {
    return new CodeRep.Seq(reps);
  }

  @Nonnull
  public CodeRep init(@Nonnull CodeRep id, @Nonnull Maybe<CodeRep> i) {
    return i.maybe(id, init -> new CodeRep.Init(id, init));
  }

  @Nonnull
  public CodeRep semi() {
    return new CodeRep.Semi();
  }

  @Nonnull
  public CodeRep commaSep(@Nonnull List<CodeRep> pieces) {
    return new CodeRep.CommaSep(pieces);
  }

  @Nonnull
  public CodeRep brace(@Nonnull CodeRep rep) {
    return new CodeRep.Brace(rep);
  }

  @Nonnull
  public CodeRep semiOp() {
    return new CodeRep.SemiOp();
  }
}