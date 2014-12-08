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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class FunctionBody extends Node {
  @NotNull
  public final List<Directive> directives;
  @NotNull
  public final List<Statement> statements;

  private final boolean isStrict;

  public FunctionBody(@NotNull List<Directive> directives, @NotNull List<Statement> statements) {
    super();
    this.directives = directives;
    this.statements = statements;
    this.isStrict = directives.exists(directive -> directive instanceof UseStrictDirective);
  }

  public boolean isStrict() {
    return this.isStrict;
  }

  @NotNull
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> ProgramBodyState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.FunctionBody;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof FunctionBody &&
        this.directives.equals(((FunctionBody) object).directives) &&
        this.statements.equals(((FunctionBody) object).statements) &&
        this.isStrict == ((FunctionBody) object).isStrict;
  }

  @NotNull
  public List<Directive> getDirectives() {
    return directives;
  }

  @NotNull
  public List<Statement> getStatements() {
    return statements;
  }

  @NotNull
  public FunctionBody setDirectives(@NotNull List<Directive> directives) {
    return new FunctionBody(directives, statements);
  }

  @NotNull
  public FunctionBody setStatements(@NotNull List<Statement> statements) {
    return new FunctionBody(directives, statements);
  }
}
