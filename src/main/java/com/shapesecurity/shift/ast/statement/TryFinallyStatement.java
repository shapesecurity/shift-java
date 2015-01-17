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

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class TryFinallyStatement extends Statement {
  @NotNull
  public final Block body;
  @NotNull
  public final Maybe<CatchClause> catchClause;
  @NotNull
  public final Block finalizer;

  public TryFinallyStatement(@NotNull Block body, @NotNull Maybe<CatchClause> catchClause, @NotNull Block finalizer) {
    super();
    this.body = body;
    this.catchClause = catchClause;
    this.finalizer = finalizer;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.TryFinallyStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TryFinallyStatement &&
        this.body.equals(((TryFinallyStatement) object).body) &&
        this.catchClause.equals(((TryFinallyStatement) object).catchClause) &&
        this.finalizer.equals(((TryFinallyStatement) object).finalizer);
  }

  @NotNull
  public Block getBody() {
    return this.body;
  }

  @NotNull
  public Maybe<CatchClause> getCatchClause() {
    return this.catchClause;
  }

  @NotNull
  public Block getFinalizer() {
    return this.finalizer;
  }

  @NotNull
  public TryFinallyStatement setBody(@NotNull Block body) {
    return new TryFinallyStatement(body, this.catchClause, this.finalizer);
  }

  public TryFinallyStatement setCatchClause(@NotNull Maybe<CatchClause> catchClause) {
    return new TryFinallyStatement(this.body, catchClause, this.finalizer);
  }

  public TryFinallyStatement setFinalizer(@NotNull Block finalizer) {
    return new TryFinallyStatement(this.body, this.catchClause, finalizer);
  }
}
