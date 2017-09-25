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

import org.jetbrains.annotations.NotNull;
import com.shapesecurity.functional.data.HashCodeBuilder;

public class Method extends MethodDefinition {
    @NotNull
    public final boolean isAsync;
    public final boolean isGenerator;

    @NotNull
    public final FormalParameters params;


    public Method (boolean isAsync, boolean isGenerator, @NotNull PropertyName name, @NotNull FormalParameters params, @NotNull FunctionBody body) {
        super(name, body);
        this.isAsync = isGenerator;
        this.isGenerator = isGenerator;
        this.params = params;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof Method && this.isAsync == ((Method) object).isAsync && this.isGenerator == ((Method) object).isGenerator && this.name.equals(((Method) object).name) && this.params.equals(((Method) object).params) && this.body.equals(((Method) object).body);
    }


    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "Method");
        code = HashCodeBuilder.put(code, this.isAsync);
        code = HashCodeBuilder.put(code, this.isGenerator);
        code = HashCodeBuilder.put(code, this.name);
        code = HashCodeBuilder.put(code, this.params);
        code = HashCodeBuilder.put(code, this.body);
        return code;
    }

}
