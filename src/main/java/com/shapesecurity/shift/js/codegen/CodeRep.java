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

package com.shapesecurity.shift.js.codegen;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;

import javax.annotation.Nonnull;

public abstract class CodeRep {
  protected CodeRep() {
  }

  public abstract void emit(@Nonnull TokenStream ts, boolean noIn);

  public static final class Empty extends CodeRep {
    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
    }
  }

  public static final class Token extends CodeRep {
    @Nonnull
    private final CharSequence token;

    public Token(@Nonnull CharSequence token) {
      super();
      this.token = token;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.put(this.token);
    }
  }

  public static final class Number extends CodeRep {
    private final double number;

    public Number(double number) {
      super();
      this.number = number;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.putNumber(this.number);
    }
  }

  public static final class Paren extends CodeRep {
    @Nonnull
    private final CodeRep expr;

    public Paren(@Nonnull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.put("(");
      this.expr.emit(ts, false);
      ts.put(")");
    }
  }

  public static final class ContainsIn extends CodeRep {
    @Nonnull
    private final CodeRep expr;

    public ContainsIn(@Nonnull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      if (noIn) {
        ts.put("(");
        this.expr.emit(ts, false);
        ts.put(")");
      } else {
        this.expr.emit(ts, false);
      }
    }
  }

  public static final class Brace extends CodeRep {
    @Nonnull
    private final CodeRep expr;

    public Brace(@Nonnull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.put("{");
      this.expr.emit(ts, false);
      ts.put("}");
    }
  }

  public static final class Bracket extends CodeRep {
    @Nonnull
    private final CodeRep expr;

    public Bracket(@Nonnull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.put("[");
      this.expr.emit(ts, false);
      ts.put("]");
    }
  }

  public static final class NoIn extends CodeRep {
    @Nonnull
    private final CodeRep expr;

    public NoIn(@Nonnull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      this.expr.emit(ts, true);
    }
  }

  public static final class Seq extends CodeRep {
    @Nonnull
    private final List<CodeRep> children;

    public Seq(@Nonnull List<CodeRep> children) {
      super();
      this.children = children;
    }

    @Override
    public void emit(@Nonnull TokenStream ts, final boolean noIn) {
      // Using fold instead of foreach to pass ts in the closure.
      this.children.foldLeft((ts1, codeRep) -> {
        codeRep.emit(ts1, noIn);
        return ts1;
      }, ts);
    }
  }

  public static final class Init extends CodeRep {
    @Nonnull
    private final CodeRep lhs;
    @Nonnull
    private final Maybe<CodeRep> rhs;

    public Init(@Nonnull CodeRep lhs, @Nonnull Maybe<CodeRep> rhs) {
      super();
      this.lhs = lhs;
      this.rhs = rhs;
    }

    @Override
    public void emit(@Nonnull final TokenStream ts, final boolean noIn) {
      this.lhs.emit(ts, false);
      if (this.rhs.isJust()) {
        ts.put("=");
        this.rhs.just().emit(ts, noIn);
      }
    }
  }

  public static final class CommaSep extends CodeRep {
    @Nonnull
    private final List<CodeRep> children;

    public CommaSep(@Nonnull List<CodeRep> children) {
      super();
      this.children = children;
    }

    @Override
    public void emit(@Nonnull final TokenStream ts, final boolean noIn) {
      if (this.children.isEmpty()) {
        return;
      }
      NonEmptyList<CodeRep> nel = (NonEmptyList<CodeRep>) this.children;
      nel.head.emit(ts, noIn);
      nel.tail().foldLeft((ts1, codeRep) -> {
        ts1.put(",");
        codeRep.emit(ts1, noIn);
        return ts1;
      }, ts);
    }
  }

  public static final class Semi extends CodeRep {
    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.put(";");
    }
  }

  public static final class SemiOp extends CodeRep {
    @Override
    public void emit(@Nonnull TokenStream ts, boolean noIn) {
      ts.putOptionalSemi();
    }
  }
}
