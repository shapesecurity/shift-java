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

public abstract class FormattedCodeRep extends CodeRep {
    private FormattedCodeRep() {
        super();
    }

    public static final class Brace extends FormattedCodeRep {
        @Nonnull
        private final CodeRep expr;

        public Brace(@Nonnull CodeRep expr) {
            super();
            this.expr = expr;
        }

        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            ts.put("{\n");
            this.expr.emit(ts, false);
            ts.put("}");
        }
    }

    public static final class Semi extends FormattedCodeRep {
        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            ts.put(";\n");
        }
    }

    public static final class SemiOp extends FormattedCodeRep {
        @Override
        public void emit(@Nonnull TokenStream ts, boolean noIn) {
            ts.putOptionalSemi();
            ts.put("\n");
        }
    }
}
