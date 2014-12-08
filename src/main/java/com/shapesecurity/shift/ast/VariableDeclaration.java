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

import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class VariableDeclaration extends Node {
  @NotNull
  public final VariableDeclarationKind kind;
  @NotNull
  public final NonEmptyList<VariableDeclarator> declarators;

  public VariableDeclaration(
      @NotNull VariableDeclarationKind kind,
      @NotNull NonEmptyList<VariableDeclarator> declarators) {
    super();
    this.kind = kind;
    this.declarators = declarators;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> DeclarationState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.VariableDeclaration;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VariableDeclaration &&
           ((VariableDeclaration) obj).declarators.equals(this.declarators) &&
           ((VariableDeclaration) obj).kind.equals(this.kind);
  }

  public static enum VariableDeclarationKind {
    Var("var"),
    Const("const"),
    Let("let");
    public final String name;

    private VariableDeclarationKind(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  @NotNull
  public VariableDeclarationKind getKind() {
    return kind;
  }

  @NotNull
  public NonEmptyList<VariableDeclarator> getDeclarators() {
    return declarators;
  }

  @NotNull
  public VariableDeclaration setKind(@NotNull VariableDeclarationKind kind) {
    return new VariableDeclaration(kind, declarators);
  }

  @NotNull
  public VariableDeclaration setDeclarators(@NotNull NonEmptyList<VariableDeclarator> declarators) {
    return new VariableDeclaration(kind, declarators);
  }
}
