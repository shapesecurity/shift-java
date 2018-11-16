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

public class FormattedCodeRepFactory extends CodeRepFactory {
    @Nonnull
    public CodeRep semi() {
        return new FormattedCodeRep.Semi();
    }

    @Nonnull
    @Override
    public CodeRep brace(@Nonnull CodeRep rep) {
        return new FormattedCodeRep.Brace(rep);
    }

    @Nonnull
    @Override
    public CodeRep semiOp() {
        return new FormattedCodeRep.SemiOp();
    }
}
