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

package com.shapesecurity.functional.data;

import org.jetbrains.annotations.NotNull;

/**
 * HasCodeBuilder is a simple FNV hash builder for hash code generation of
 */
public final class HashCodeBuilder {
    //int is a 32 bit integer
    private static final int INITIAL_VALUE = -2128831035;
    private static final int MULT = 16777619;

    public static int init() {
        return INITIAL_VALUE;
    }

    public static int put(int hash, @NotNull Object os) {
        int p = os.hashCode();
        hash = hash * MULT ^ (p & 255);
        p >>>= 8;
        hash = hash * MULT ^ (p & 255);
        p >>>= 8;
        hash = hash * MULT ^ (p & 255);
        p >>>= 8;
        hash = hash * MULT ^ (p & 255);
        return hash;
    }

    public static int putChar(int hash, char p) {
        hash = hash * MULT ^ (p & 255);
        p >>>= 8;
        hash = hash * MULT ^ (p & 255);
        return hash;
    }
}