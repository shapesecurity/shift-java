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

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class SwitchStatement extends Statement {
  @NotNull
  public final Expression discriminant;
  @NotNull
  public final ImmutableList<SwitchCase> cases;

  public SwitchStatement(@NotNull Expression discriminant, @NotNull ImmutableList<SwitchCase> cases) {
    super();
    this.discriminant = discriminant;
    this.cases = cases;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.SwitchStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SwitchStatement && this.discriminant.equals(((SwitchStatement) object).discriminant) &&
        this.cases.equals(((SwitchStatement) object).cases);
  }

  @NotNull
  public Expression getDiscriminant() {
    return this.discriminant;
  }

  @NotNull
  public ImmutableList<SwitchCase> getCases() {
    return this.cases;
  }

  @NotNull
  public SwitchStatement setDiscriminant(@NotNull Expression discriminant) {
    return new SwitchStatement(discriminant, this.cases);
  }

  @NotNull
  public SwitchStatement setCases(@NotNull ImmutableList<SwitchCase> cases) {
    return new SwitchStatement(this.discriminant, cases);
  }
}
