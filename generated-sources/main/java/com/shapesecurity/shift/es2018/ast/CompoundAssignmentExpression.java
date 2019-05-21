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
import com.shapesecurity.shift.es2018.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es2018.ast.operators.Precedence;

public class CompoundAssignmentExpression implements Expression {
    @Nonnull
    public final SimpleAssignmentTarget binding;

    @Nonnull
    public final CompoundAssignmentOperator operator;

    @Nonnull
    public final Expression expression;


    public CompoundAssignmentExpression (@Nonnull SimpleAssignmentTarget binding, @Nonnull CompoundAssignmentOperator operator, @Nonnull Expression expression) {
        this.binding = binding;
        this.operator = operator;
        this.expression = expression;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof CompoundAssignmentExpression && this.binding.equals(((CompoundAssignmentExpression) object).binding) && this.operator.equals(((CompoundAssignmentExpression) object).operator) && this.expression.equals(((CompoundAssignmentExpression) object).expression);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "CompoundAssignmentExpression");
        code = HashCodeBuilder.put(code, this.binding);
        code = HashCodeBuilder.put(code, this.operator);
        code = HashCodeBuilder.put(code, this.expression);
        return code;
    }

    @Override
    @Nonnull
    public Precedence getPrecedence() {
        return Precedence.ASSIGNMENT;
    }

}