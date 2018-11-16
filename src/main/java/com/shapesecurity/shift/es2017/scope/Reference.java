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

package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.VariableReference;
import javax.annotation.Nonnull;

public final class Reference {
    @Nonnull
    public final VariableReference node;
    @Nonnull
    public final Accessibility accessibility;

    private Reference(@Nonnull VariableReference node, @Nonnull Accessibility accessibility) {
        this.node = node;
        this.accessibility = accessibility;
    }

    public Reference(@Nonnull BindingIdentifier node) {
        this.node = node;
        this.accessibility = Accessibility.Write;
    }

    public Reference(@Nonnull IdentifierExpression node) {
        this.node = node;
        this.accessibility = Accessibility.Read;
    }

    public Reference(@Nonnull AssignmentTargetIdentifier node, @Nonnull Accessibility accessibility) {
        this.node = node;
        this.accessibility = accessibility;
    }

    @Nonnull
    public final Reference withReadability() {
        return new Reference(this.node, this.accessibility.withReadability());
    }

    @Nonnull
    public final Reference withWritability() {
        return new Reference(this.node, this.accessibility.withWritability());
    }
}
