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

package com.shapesecurity.functional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface F3<A, B, C, R> {
    @NotNull
    public abstract R apply(@NotNull A a, @NotNull B b, @NotNull C c);

    @NotNull
    public default R applyTuple(@NotNull Tuple3<A, B, C> args) {
        return apply(args.a, args.b, args.c);
    }

    @NotNull
    public default F2<B, C, R> curry(@NotNull final A a) {
        return (b, c) -> apply(a, b, c);
    }

    @NotNull
    public default F<A, F<B, F<C, R>>> curry() {
        return a -> b -> c -> apply(a, b, c);
    }
}
