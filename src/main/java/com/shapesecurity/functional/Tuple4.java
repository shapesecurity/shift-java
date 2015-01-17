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

import com.shapesecurity.functional.data.HashCodeBuilder;

import org.jetbrains.annotations.NotNull;

public final class Tuple4<A, B, C, D> {
  @NotNull
  public final A a;
  @NotNull
  public final B b;
  @NotNull
  public final C c;
  @NotNull
  public final D d;

  public Tuple4(@NotNull A a, @NotNull B b, @NotNull C c, @NotNull D d) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  @NotNull
  public <A1> Tuple4<A1, B, C, D> mapA(@NotNull F<A, A1> f) {
    return new Tuple4<>(f.apply(this.a), this.b, this.c, this.d);
  }

  @NotNull
  public <B1> Tuple4<A, B1, C, D> mapB(@NotNull F<B, B1> f) {
    return new Tuple4<>(this.a, f.apply(this.b), this.c, this.d);
  }

  @NotNull
  public <C1> Tuple4<A, B, C1, D> mapC(@NotNull F<C, C1> f) {
    return new Tuple4<>(this.a, this.b, f.apply(this.c), this.d);
  }
  @NotNull
  public <D1> Tuple4<A, B, C, D1> mapD(@NotNull F<D, D1> f) {
    return new Tuple4<>(this.a, this.b, this.c, f.apply(this.d));
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof Tuple4 &&
        ((Tuple4<A, B, C, D>) obj).a.equals(this.a) &&
        ((Tuple4<A, B, C, D>) obj).b.equals(this.b) &&
        ((Tuple4<A, B, C, D>) obj).c.equals(this.c) &&
        ((Tuple4<A, B, C, D>) obj).d.equals(this.d);
  }

  @Override
  public int hashCode() {
    int hash = HashCodeBuilder.put(HashCodeBuilder.init(), "Tuple4");
    hash = HashCodeBuilder.put(hash, this.a);
    hash = HashCodeBuilder.put(hash, this.b);
    hash = HashCodeBuilder.put(hash, this.c);
    return HashCodeBuilder.put(hash, this.d);
  }
}

