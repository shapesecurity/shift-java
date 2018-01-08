// Generated by shift-java-gen/ast.js

/*
 * Copyright 2016 Shape Security, Inc.
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

package com.shapesecurity.shift.es2016.ast;

import javax.annotation.Nonnull;
import com.shapesecurity.functional.data.HashCodeBuilder;
import com.shapesecurity.functional.data.Maybe;

public class AssignmentTargetPropertyIdentifier implements AssignmentTargetProperty {
    @Nonnull
    public final AssignmentTargetIdentifier binding;

    @Nonnull
    public final Maybe<Expression> init;


    public AssignmentTargetPropertyIdentifier (@Nonnull AssignmentTargetIdentifier binding, @Nonnull Maybe<Expression> init) {
        this.binding = binding;
        this.init = init;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof AssignmentTargetPropertyIdentifier && this.binding.equals(((AssignmentTargetPropertyIdentifier) object).binding) && this.init.equals(((AssignmentTargetPropertyIdentifier) object).init);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "AssignmentTargetPropertyIdentifier");
        code = HashCodeBuilder.put(code, this.binding);
        code = HashCodeBuilder.put(code, this.init);
        return code;
    }

}
