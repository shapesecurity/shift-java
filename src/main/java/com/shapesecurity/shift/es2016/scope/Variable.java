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

package com.shapesecurity.shift.es2016.scope;

import com.shapesecurity.functional.data.ImmutableList;

import org.jetbrains.annotations.NotNull;

public class Variable {
    /**
     * Variable name *
     */
    @NotNull
    public final String name;
    /**
     * references of this variable *
     */
    @NotNull
    public final ImmutableList<Reference> references;
    /**
     * declarations of this variable *
     */
    @NotNull
    public final ImmutableList<Declaration> declarations;

    public Variable(
            @NotNull String name,
            @NotNull ImmutableList<Reference> references,
            @NotNull ImmutableList<Declaration> declarations) {
        this.name = name;
        this.references = references;
        this.declarations = declarations;
    }
}