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

package com.shapesecurity.shift.es2017.codegen;

import javax.annotation.Nonnull;

public abstract class CodeRep {
    protected boolean containsIn = false;
    protected boolean containsGroup = false;
    protected boolean startsWithObjectCurly = false;
    protected boolean startsWithFunctionOrClass = false;
    protected boolean startsWithLet = false;
    protected boolean startsWithLetSquareBracket = false;
    protected boolean endsWithMissingElse = false;

    public CodeRep() {
    }

    public abstract void emit(@Nonnull TokenStream ts, boolean noIn);

    public boolean containsIn() {
        return this.containsIn;
    }

    public void setContainsIn(boolean containsIn) {
        this.containsIn = containsIn;
    }

    public boolean containsGroup() {
        return this.containsGroup;
    }

    public void setContainsGroup(boolean containsGroup) {
        this.containsGroup = containsGroup;
    }

    public boolean startsWithObjectCurly() {
        return this.startsWithObjectCurly;
    }

    public void startsWithObjectCurly(boolean startsWithObjectCurly) {
        this.startsWithObjectCurly = startsWithObjectCurly;
    }

    public boolean startsWithFunctionOrClass() {
        return this.startsWithFunctionOrClass;
    }

    public void setStartsWithFunctionOrClass(boolean startsWithFunctionOrClass) {
        this.startsWithFunctionOrClass = startsWithFunctionOrClass;
    }

    public boolean startsWithLet() {
        return this.startsWithLet;
    }

    public void setStartsWithLet(boolean startsWithLet) {
        this.startsWithLet = startsWithLet;
    }

    public boolean startsWithLetSquareBracket() {
        return this.startsWithLetSquareBracket;
    }

    public void setStartsWithLetSquareBracket(boolean startsWithLetSquareBracket) {
        this.startsWithLetSquareBracket = startsWithLetSquareBracket;
    }

    public boolean endsWithMissingElse() {
        return this.endsWithMissingElse;
    }

    public void setEndsWithMissingElse(boolean endsWithMissingElse) {
        this.endsWithMissingElse = endsWithMissingElse;
    }

    public void markIsInDirectivePosition() {
        // Does nothing by default; this exists purely to be overridden by StringLiteralExpressionStatement
    }

    public static final class Empty extends CodeRep {
        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
        }
    }

    public static final class Token extends CodeRep {
        @Nonnull
        public final String token;

        public Token(@Nonnull String token) {
            super();
            this.token = token;
        }

        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            ts.put(this.token);
        }
    }

    public static final class RawToken extends CodeRep {
        @Nonnull
        private final String token;

        public RawToken(@Nonnull String token) {
            super();
            this.token = token;
        }

        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            ts.putRaw(this.token);
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
        public final CodeRep[] children;

        public Seq(@Nonnull CodeRep[] children) {
            super();
            this.children = children;
        }

        @Override
        public void emit(@Nonnull TokenStream ts, final boolean noIn) {
            // Using fold instead of foreach to pass ts in the closure.
            for (CodeRep child : this.children) {
                child.emit(ts, noIn);
            }
        }
    }

    public static final class Init extends CodeRep {
        @Nonnull
        private final CodeRep lhs;
        @Nonnull
        private final CodeRep rhs;

        public Init(@Nonnull CodeRep lhs, @Nonnull CodeRep rhs) {
            super();
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void emit(@Nonnull final TokenStream ts, final boolean noIn) {
            this.lhs.emit(ts, false);
            ts.put("=");
            this.rhs.emit(ts, noIn);
        }
    }

    public static final class CommaSep extends CodeRep {
        @Nonnull
        private final CodeRep[] children;

        public CommaSep(@Nonnull CodeRep[] children) {
            super();
            this.children = children;
        }

        @Override
        public void emit(@Nonnull final TokenStream ts, final boolean noIn) {
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

    public static final class StringLiteralExpressionStatement extends CodeRep {
        protected boolean isInDirectivePosition = false;

        @Nonnull
        private final CodeRep rep;

        public StringLiteralExpressionStatement(@Nonnull CodeRep rep) {
            super();
            this.rep = rep;
        }

        @Override
        public void markIsInDirectivePosition() {
            this.isInDirectivePosition = true;
        }

        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            if (this.isInDirectivePosition) {
                ts.put("(");
                this.rep.emit(ts, noIn);
                ts.put(")");
            } else {
                this.rep.emit(ts, noIn);
            }
            ts.putOptionalSemi();
        }
    }
}
