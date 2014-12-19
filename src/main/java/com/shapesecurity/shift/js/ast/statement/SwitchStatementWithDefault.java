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

package com.shapesecurity.shift.js.ast.statement;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.types.Type;
import com.shapesecurity.shift.js.visitor.TransformerP;

public class SwitchStatementWithDefault extends Statement {
  @Nonnull
  public final Expression discriminant;
  @Nonnull
  public final List<SwitchCase> preDefaultCases;
  @Nonnull
  public final SwitchDefault defaultCase;
  @Nonnull
  public final List<SwitchCase> postDefaultCases;

  public SwitchStatementWithDefault(
      @Nonnull Expression discriminant,
      @Nonnull List<SwitchCase> preDefaultCases,
      @Nonnull SwitchDefault defaultCase,
      @Nonnull List<SwitchCase> postDefaultCases) {
    super();
    this.discriminant = discriminant;
    this.preDefaultCases = preDefaultCases;
    this.defaultCase = defaultCase;
    this.postDefaultCases = postDefaultCases;
  }

  @Nonnull
  @Override
  public <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> StatementState transform(
      @Nonnull TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> transformer) {
    return transformer.transform(this);
  }

  @Nonnull
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

  @Nonnull
  public Expression getDiscriminant() {
    return discriminant;
  }

  @Nonnull
  public List<SwitchCase> getPreDefaultCases() {
    return preDefaultCases;
  }

  @Nonnull
  public SwitchDefault getDefaultCase() {
    return defaultCase;
  }

  @Nonnull
  public List<SwitchCase> getPostDefaultCases() {
    return postDefaultCases;
  }

  @Nonnull
  public SwitchStatementWithDefault setDiscriminant(@Nonnull Expression discriminant) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @Nonnull
  public SwitchStatementWithDefault setPreDefaultCases(@Nonnull List<SwitchCase> preDefaultCases) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @Nonnull
  public SwitchStatementWithDefault setDefaultCase(@Nonnull SwitchDefault defaultCase) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @Nonnull
  public SwitchStatementWithDefault setPostDefaultCases(@Nonnull List<SwitchCase> postDefaultCases) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }
}
