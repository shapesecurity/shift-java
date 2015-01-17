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

package com.shapesecurity.shift.ast;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class VariableDeclarator extends Node {
  @NotNull
  public final Identifier binding;
  @NotNull
  public final Maybe<Expression> init;

  public VariableDeclarator(@NotNull Identifier binding, @NotNull Maybe<Expression> init) {
    super();
    this.binding = binding;
    this.init = init;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.VariableDeclarator;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof VariableDeclarator && this.binding.equals(((VariableDeclarator) object).binding) &&
        this.init.equals(((VariableDeclarator) object).init);
  }

  @NotNull
  public Identifier getBinding() {
    return this.binding;
  }

  @NotNull
  public Maybe<Expression> getInit() {
    return this.init;
  }

  public VariableDeclarator setBinding(@NotNull Identifier binding) {
    return new VariableDeclarator(binding, this.init);
  }

  public VariableDeclarator setInit(@NotNull Maybe<Expression> init) {
    return new VariableDeclarator(this.binding, init);
  }
}
