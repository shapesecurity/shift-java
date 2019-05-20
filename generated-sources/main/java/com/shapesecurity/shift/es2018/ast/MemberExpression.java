// Generated by ast.js
/**
 * Copyright 2018 Shape Security, Inc.
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


package com.shapesecurity.shift.es2018.ast;

import javax.annotation.Nonnull;
import com.shapesecurity.functional.data.HashCodeBuilder;
import com.shapesecurity.shift.es2018.ast.operators.Precedence;

public abstract class MemberExpression implements Expression {
    @Nonnull
    public final ExpressionSuper object;


    public MemberExpression (@Nonnull ExpressionSuper object) {
        this.object = object;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof MemberExpression && this.object.equals(((MemberExpression) object).object);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "MemberExpression");
        code = HashCodeBuilder.put(code, this.object);
        return code;
    }

    @Override
    @Nonnull
    public Precedence getPrecedence() {
        if (this.object instanceof Super) {
          return Precedence.MEMBER;
        }
        Precedence p = ((Expression) this.object).getPrecedence();
        if (p == Precedence.CALL) {
            return Precedence.CALL;
        }
        return Precedence.MEMBER;
    }

}
