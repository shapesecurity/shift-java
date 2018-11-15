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

package com.shapesecurity.shift.es2017.parser;

public class JsError extends Exception {
    private static final long serialVersionUID = -5526903161079226322L;
    private final int index, line, column;
    private final String description;

    public JsError(int index, int line, int column, String description) {
        super();
        this.index = index;
        this.line = line;
        this.column = column;
        this.description = description;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String getMessage() {
        return String.format("JavaScript error: Line %d Column %d (Index = %d), %s.", this.line, this.column, this.index,
                this.description);
    }
}
