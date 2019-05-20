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
import com.shapesecurity.functional.data.ImmutableList;

public class SwitchStatementWithDefault implements Statement {
    @Nonnull
    public final Expression discriminant;

    @Nonnull
    public final ImmutableList<SwitchCase> preDefaultCases;

    @Nonnull
    public final SwitchDefault defaultCase;

    @Nonnull
    public final ImmutableList<SwitchCase> postDefaultCases;


    public SwitchStatementWithDefault (@Nonnull Expression discriminant, @Nonnull ImmutableList<SwitchCase> preDefaultCases, @Nonnull SwitchDefault defaultCase, @Nonnull ImmutableList<SwitchCase> postDefaultCases) {
        this.discriminant = discriminant;
        this.preDefaultCases = preDefaultCases;
        this.defaultCase = defaultCase;
        this.postDefaultCases = postDefaultCases;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof SwitchStatementWithDefault && this.discriminant.equals(((SwitchStatementWithDefault) object).discriminant) && this.preDefaultCases.equals(((SwitchStatementWithDefault) object).preDefaultCases) && this.defaultCase.equals(((SwitchStatementWithDefault) object).defaultCase) && this.postDefaultCases.equals(((SwitchStatementWithDefault) object).postDefaultCases);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "SwitchStatementWithDefault");
        code = HashCodeBuilder.put(code, this.discriminant);
        code = HashCodeBuilder.put(code, this.preDefaultCases);
        code = HashCodeBuilder.put(code, this.defaultCase);
        code = HashCodeBuilder.put(code, this.postDefaultCases);
        return code;
    }

}
