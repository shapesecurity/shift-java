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
public interface F4<A, B, C, D, R> {
    @NotNull
    public abstract R apply(@NotNull A a, @NotNull B b, @NotNull C c, @NotNull D d);

    @NotNull
    public default R applyTuple(@NotNull Tuple4<A, B, C, D> args) {
        return apply(args.a, args.b, args.c, args.d);
    }

    @NotNull
    public default F3<B, C, D, R> curry(@NotNull final A a) {
        return (b, c, d) -> apply(a, b, c, d);
    }

    @NotNull
    public default F<A, F<B, F<C, F<D, R>>>> curry() {
        return a -> b -> c -> d -> apply(a, b, c, d);
    }
}
