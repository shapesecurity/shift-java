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

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.types.Type;
import com.shapesecurity.shift.visitor.TransformerP;

import org.jetbrains.annotations.NotNull;

public class SwitchStatementWithDefault extends Statement {
  @NotNull
  public final Expression discriminant;
  @NotNull
  public final List<SwitchCase> preDefaultCases;
  @NotNull
  public final SwitchDefault defaultCase;
  @NotNull
  public final List<SwitchCase> postDefaultCases;

  public SwitchStatementWithDefault(
      @NotNull Expression discriminant,
      @NotNull List<SwitchCase> preDefaultCases,
      @NotNull SwitchDefault defaultCase,
      @NotNull List<SwitchCase> postDefaultCases) {
    super();
    this.discriminant = discriminant;
    this.preDefaultCases = preDefaultCases;
    this.defaultCase = defaultCase;
    this.postDefaultCases = postDefaultCases;
  }

  @NotNull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @NotNull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @NotNull
  @Override
  public Type type() {
    return Type.SwitchStatementWithDefault;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatementWithDefault &&
        this.discriminant.equals(((SwitchStatementWithDefault) object).discriminant) &&
        this.preDefaultCases.equals(((SwitchStatementWithDefault) object).preDefaultCases) &&
        this.defaultCase.equals(((SwitchStatementWithDefault) object).defaultCase) &&
        this.postDefaultCases.equals(((SwitchStatementWithDefault) object).postDefaultCases);
  }

  @NotNull
  public Expression getDiscriminant() {
    return discriminant;
  }

  @NotNull
  public List<SwitchCase> getPreDefaultCases() {
    return preDefaultCases;
  }

  @NotNull
  public SwitchDefault getDefaultCase() {
    return defaultCase;
  }

  @NotNull
  public List<SwitchCase> getPostDefaultCases() {
    return postDefaultCases;
  }

  @NotNull
  public SwitchStatementWithDefault setDiscriminant(@NotNull Expression discriminant) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @NotNull
  public SwitchStatementWithDefault setPreDefaultCases(@NotNull List<SwitchCase> preDefaultCases) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @NotNull
  public SwitchStatementWithDefault setDefaultCase(@NotNull SwitchDefault defaultCase) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @NotNull
  public SwitchStatementWithDefault setPostDefaultCases(@NotNull List<SwitchCase> postDefaultCases) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }
}
