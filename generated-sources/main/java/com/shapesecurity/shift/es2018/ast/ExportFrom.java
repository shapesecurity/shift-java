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

public class ExportFrom implements ExportDeclaration {
    @Nonnull
    public final ImmutableList<ExportFromSpecifier> namedExports;

    @Nonnull
    public final String moduleSpecifier;


    public ExportFrom (@Nonnull ImmutableList<ExportFromSpecifier> namedExports, @Nonnull String moduleSpecifier) {
        this.namedExports = namedExports;
        this.moduleSpecifier = moduleSpecifier;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof ExportFrom && this.namedExports.equals(((ExportFrom) object).namedExports) && this.moduleSpecifier.equals(((ExportFrom) object).moduleSpecifier);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "ExportFrom");
        code = HashCodeBuilder.put(code, this.namedExports);
        code = HashCodeBuilder.put(code, this.moduleSpecifier);
        return code;
    }

}
