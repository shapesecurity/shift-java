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

import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.types.Type;

import org.jetbrains.annotations.NotNull;

public class BlockStatement extends Statement {
  @NotNull
  public final Block block;

  public BlockStatement(@NotNull Block block) {
    super();
    this.block = block;
  }

  @NotNull
  @Override
  public Type type() {
    return Type.BlockStatement;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BlockStatement && this.block.equals(((BlockStatement) object).block);
  }

  @NotNull
  public Block getBlock() {
    return this.block;
  }

  public BlockStatement setBlock(@NotNull Block block) {
    return new BlockStatement(block);
  }
}
