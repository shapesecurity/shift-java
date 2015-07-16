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
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.NonEmptyImmutableList;
//import com.shapesecurity.shift.ast.ListNode;
//import com.shapesecurity.shift.ast.Node;
//import com.shapesecurity.shift.ast.NonEmptyListNode;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public final class IndexedBranch implements Branch {
//  public final int index;
//  private final static IndexedBranch[] cache = new IndexedBranch[1024];
//
//  private IndexedBranch(int index) {
//    this.index = index;
//  }
//
//  @NotNull
//  public static IndexedBranch from(int i) {
//    if (i < 1024) {
//      if (cache[i] == null) {
//        synchronized (cache) {
//          if (cache[i] == null) {
//            cache[i] = new IndexedBranch(i);
//          }
//        }
//      } else {
//        return cache[i];
//      }
//    }
//    return new IndexedBranch(i);
//  }
//
//  @Override
//  public int hashCode() {
//    return index;
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    return obj == this || obj instanceof IndexedBranch && ((IndexedBranch) obj).index == this.index;
//  }
//
//  @SuppressWarnings("unchecked")
//  @Nullable
//  @Override
//  public Node view(@NotNull Node parent) {
//    if (parent instanceof ListNode) {
//      ListNode<Object> listNode = (ListNode<Object>) parent;
//      Object node = listNode.list.index(this.index).toNullable();
//      if (node == null) {
//        return null;
//      }
//      return Branch.wrap(node, listNode.genType.elementType);
//    } else if (parent instanceof NonEmptyListNode) {
//      NonEmptyListNode<Object> listNode = (NonEmptyListNode<Object>) parent;
//      Object node = listNode.list.index(this.index).toNullable();
//      if (node == null) {
//        return null;
//      }
//      return Branch.wrap(node, listNode.genType.elementType);
//    }
//    return null;
//  }
//
//  @SuppressWarnings("unchecked")
//  @NotNull
//  @Override
//  public Node set(@NotNull Node parent, @NotNull Node child) {
//    if (parent instanceof ListNode) {
//      ListNode<Node> list = (ListNode<Node>) parent;
//      if (list.genType.elementType.isAssignableFrom(child.genType())) {
//        if (this.index <= list.list.length) {
//          return new ListNode<>(
//              list.list.patch(this.index, 1, ImmutableList.list(child)),
//              list.genType);
//        }
//      }
//    } else if (parent instanceof NonEmptyListNode) {
//      NonEmptyListNode<Node> list = (NonEmptyListNode<Node>) parent;
//      if (list.genType.elementType.isAssignableFrom(child.genType())) {
//        if (this.index <= list.list.length) {
//          return new NonEmptyListNode<>(
//              (NonEmptyImmutableList<Node>) list.list.patch(this.index, 1, ImmutableList.list(child)),
//              list.genType);
//        }
//      }
//    }
//    return parent;
//  }
//
//  @Override
//  public String toString() {
//    return "" + index;
//  }
//}
