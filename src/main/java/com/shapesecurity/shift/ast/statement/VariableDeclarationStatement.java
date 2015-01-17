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

package com.shapesecurity.shift.ast.statement;

import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class VariableDeclarationStatement extends Statement {
  @NotNull
  public final VariableDeclaration declaration;

  public VariableDeclarationStatement(@NotNull VariableDeclaration declaration) {
    super();
    this.declaration = declaration;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.VariableDeclarationStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof VariableDeclarationStatement && this.declaration.equals(
        ((VariableDeclarationStatement) object).declaration);
  }

  @NotNull
  public VariableDeclaration getDeclaration() {
    return this.declaration;
  }

  @NotNull
  public VariableDeclarationStatement setDeclaration(@NotNull VariableDeclaration declaration) {
    return new VariableDeclarationStatement(declaration);
  }
}
