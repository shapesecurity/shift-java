///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.ast;
//
//import java.io.IOException;
//import java.util.function.Consumer;
//
//import javax.annotation.Nonnull;
//
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.AstHelper;
//import com.shapesecurity.shift.TestReducer;
//import com.shapesecurity.shift.TestReducerWithPath;
//import JsError;
//import Parser;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//public class AstTest extends AstHelper {
//  private final static Script script;
//
//  static {
//    try {
//      script = Parser.parse(readLibrary("everything-0.0.4.js"));
//    } catch (JsError | IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  private void forBranches(@Nonnull Consumer<Branch> f) {
//    StaticBranch[] staticBranches = StaticBranch.values();
//    for (StaticBranch staticBranch : staticBranches) {
//      f.accept(staticBranch);
//    }
//  }
//
//  private void forASTs(@Nonnull Consumer<Node> f) {
//    script.reduce(new TestReducer() {
//      @Override
//      protected void accept(@Nonnull Node node) {
//        f.accept(node);
//      }
//    });
//  }
//
//  @Test
//  public void testReplication() throws IOException {
//    forASTs((node) -> forBranches((branch) -> {
//      assertEquals(node.getClass().getSimpleName(), node.type().name());
//      Maybe<? extends Node> maybe = node.get(branch);
//      if (maybe.isJust()) {
//        Node replicate = node.set(ImmutableList.of(new ReplacementChild(branch, maybe.fromJust())));
//        assertTrue(replicate != node);
//        assertEquals(node, replicate);
//      }
//    }));
//  }
//
//  private Node track(ImmutableList<Branch> path) {
//    Node node = script;
//    for (Branch branch : path.reverse()) {
//      node = node.get(branch).fromJust();
//    }
//    return node;
//  }
//
//  @Test
//  public void testPath() throws IOException {
//    script.reduce(new TestReducerWithPath() {
//      @Override
//      protected void accept(@Nonnull Node node, @Nonnull ImmutableList<Branch> path) {
//        assertTrue(node == track(path));
//      }
//    });
//  }
//}
