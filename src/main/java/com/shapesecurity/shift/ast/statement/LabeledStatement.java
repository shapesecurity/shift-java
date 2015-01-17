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

import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class LabeledStatement extends Statement {
  @NotNull
  public final Identifier label;
  @NotNull
  public final Statement body;

  public LabeledStatement(@NotNull Identifier label, @NotNull Statement body) {
    super();
    this.label = label;
    this.body = body;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.LabeledStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof LabeledStatement && this.label.equals(((LabeledStatement) object).label) &&
        this.body.equals(((LabeledStatement) object).body);
  }

  @NotNull
  public Identifier getLabel() {
    return this.label;
  }

  @NotNull
  public Statement getBody() {
    return this.body;
  }

  @NotNull
  public LabeledStatement setLabel(@NotNull Identifier label) {
    return new LabeledStatement(label, this.body);
  }

  @NotNull
  public LabeledStatement setBody(@NotNull Statement body) {
    return new LabeledStatement(this.label, body);
  }
}
