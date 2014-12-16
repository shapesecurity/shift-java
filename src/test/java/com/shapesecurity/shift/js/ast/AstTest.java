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

package com.shapesecurity.shift.js.ast;

import static org.junit.Assert.assertTrue;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.AstHelper;
import com.shapesecurity.shift.js.TestReducer;
import com.shapesecurity.shift.js.parser.JsError;
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.BranchType;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class AstTest extends AstHelper {
  private final static Script script;

  static {
    try {
      script = Parser.parse(readLibrary("everything.js"));
    } catch (JsError | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void forBranches(@Nonnull Consumer<Branch> f) {
    BranchType[] branchTypes = BranchType.values();
    for (BranchType branchType : branchTypes) {
      Branch branch = new Branch(branchType);
      f.accept(branch);
    }
  }

  private void forASTs(@Nonnull Consumer<Node> f) {
    script.reduce(new TestReducer() {
      @Override
      protected void accept(@Nonnull Node node) {
        f.accept(node);
      }
    });
  }

  @Test
  public void testReplication() throws IOException {
    forASTs((node) -> forBranches((branch) -> {
      Maybe<? extends Node> maybe = node.branchChild(branch);
      if (maybe.isJust()) {
        Node replicate = node.replicate(List.list(new ReplacementChild(branch, maybe.just())));
        assertTrue(replicate != node);
        assertEquals(node, replicate);
      }
    }));
  }
}
