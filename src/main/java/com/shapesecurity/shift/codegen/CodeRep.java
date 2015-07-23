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

package com.shapesecurity.shift.codegen;

import org.jetbrains.annotations.NotNull;

public abstract class CodeRep {
  public boolean containsIn;
  public boolean containsGroup;
  public boolean startsWithCurly;
  public boolean startsWithFunctionOrClass;
  public boolean endsWithMissingElse;
  public boolean startsWithFunctionOrCurly;

  CodeRep() {
  }

  public abstract void emit(@NotNull TokenStream ts, boolean noIn);

  public static final class Empty extends CodeRep {
    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
    }
  }

  public static final class Token extends CodeRep {
    @NotNull
    private final String token;

    public Token(@NotNull String token) {
      super();
      this.token = token;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
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
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.putNumber(this.number);
    }
  }

  public static final class Paren extends CodeRep {
    @NotNull
    private final CodeRep expr;

    public Paren(@NotNull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.put("(");
      this.expr.emit(ts, false);
      ts.put(")");
    }
  }

  public static final class ContainsIn extends CodeRep {
    @NotNull
    private final CodeRep expr;

    public ContainsIn(@NotNull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      if (noIn) {
        ts.put("(");
        this.expr.emit(ts, false);
        ts.put(")");
      } else {
        this.expr.emit(ts, false);
      }
    }
  }

  public static final class NumberCodeRep extends CodeRep {

    private final double number;

    public NumberCodeRep(double number) {
      super();
      this.number = number;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.putNumber(this.number);
    }
  }

  public static final class Brace extends CodeRep {
    @NotNull
    private final CodeRep expr;

    public Brace(@NotNull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.put("{");
      this.expr.emit(ts, false);
      ts.put("}");
    }
  }

  public static final class Bracket extends CodeRep {
    @NotNull
    private final CodeRep expr;

    public Bracket(@NotNull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.put("[");
      this.expr.emit(ts, false);
      ts.put("]");
    }
  }

  public static final class NoIn extends CodeRep {
    @NotNull
    private final CodeRep expr;

    public NoIn(@NotNull CodeRep expr) {
      super();
      this.expr = expr;
    }

    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      this.expr.emit(ts, true);
    }
  }

  public static final class Seq extends CodeRep {
    @NotNull
    public final CodeRep[] children;

    public Seq(@NotNull CodeRep[] children) {
      super();
      this.children = children;
    }

    @Override
    public void emit(@NotNull TokenStream ts, final boolean noIn) {
      // Using fold instead of foreach to pass ts in the closure.
      for (CodeRep child : this.children) {
        child.emit(ts, noIn);
      }
    }
  }

  public static final class Init extends CodeRep {
    @NotNull
    private final CodeRep lhs;
    @NotNull
    private final CodeRep rhs;

    public Init(@NotNull CodeRep lhs, @NotNull CodeRep rhs) {
      super();
      this.lhs = lhs;
      this.rhs = rhs;
    }

    @Override
    public void emit(@NotNull final TokenStream ts, final boolean noIn) {
      this.lhs.emit(ts, false);
      ts.put("=");
      this.rhs.emit(ts, noIn);
    }
  }

  public static final class CommaSep extends CodeRep {
    @NotNull
    private final CodeRep[] children;

    public CommaSep(@NotNull CodeRep[] children) {
      super();
      this.children = children;
    }

    @Override
    public void emit(@NotNull final TokenStream ts, final boolean noIn) {
      if (this.children.length == 0) {
        return;
      }

      this.children[0].emit(ts, noIn);
      for (int i = 1; i < this.children.length; i++) {
        ts.put(",");
        this.children[i].emit(ts, noIn);
      }
    }
  }

  public static final class Semi extends CodeRep {
    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.put(";");
    }
  }

  public static final class SemiOp extends CodeRep {
    @Override
    public void emit(@NotNull TokenStream ts, boolean noIn) {
      ts.putOptionalSemi();
    }
  }
}
