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

import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class WithStatement extends Statement {
  @NotNull
  public final Expression object;
  @NotNull
  public final Statement body;

  public WithStatement(@NotNull Expression object, @NotNull Statement body) {
    super();
    this.object = object;
    this.body = body;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.WithStatement;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof WithStatement && this.object.equals(((WithStatement) obj).object) &&
        this.body.equals(((WithStatement) obj).body);
  }

  @NotNull
  public Expression getObject() {
    return this.object;
  }

  @NotNull
  public Statement getBody() {
    return this.body;
  }

  public WithStatement setObject(@NotNull Expression object) {
    return new WithStatement(object, this.body);
  }

  public WithStatement setBody(@NotNull Statement body) {
    return new WithStatement(this.object, body);
  }
}
