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
//package com.shapesecurity.shift.path;
//
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.EitherNode;
//import com.shapesecurity.shift.ast.ListNode;
//import com.shapesecurity.shift.ast.MaybeNode;
//import com.shapesecurity.shift.ast.Node;
//import com.shapesecurity.shift.ast.types.EitherType;
//import com.shapesecurity.shift.ast.types.GenType;
//import com.shapesecurity.shift.ast.types.ListType;
//import com.shapesecurity.shift.ast.types.MaybeType;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public interface Branch {
//  @Nullable
//  Node view(@NotNull Node parent);
//
//  @NotNull
//  Node set(@NotNull Node parent, @NotNull Node child);
//
//  @SuppressWarnings("unchecked")
//  @NotNull
//  static Node wrap(@NotNull Object obj, @NotNull GenType suggestedType) {
//    if (obj instanceof Either) {
//      return new EitherNode((Either) obj, (EitherType) suggestedType);
//    } else if (obj instanceof ImmutableList) {
//      return new ListNode((ImmutableList) obj, (ListType) suggestedType);
//    } else if (obj instanceof Maybe) {
//      return new MaybeNode<>((Maybe) obj, (MaybeType) suggestedType);
//    } else {
//      return (Node) obj;
//    }
//  }
//}
