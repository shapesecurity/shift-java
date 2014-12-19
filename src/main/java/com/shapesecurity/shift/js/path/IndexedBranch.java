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

package com.shapesecurity.shift.js.path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.ListNode;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.NonEmptyListNode;

public final class IndexedBranch implements Branch {
  public final int index;
  private final static IndexedBranch[] cache = new IndexedBranch[1024];

  private IndexedBranch(int index) {
    this.index = index;
  }

  @Nonnull
  public static IndexedBranch from(int i) {
    if (i < 1024) {
      if (cache[i] == null) {
        synchronized (cache) {
          if (cache[i] == null) {
            cache[i] = new IndexedBranch(i);
          }
        }
      } else {
        return cache[i];
      }
    }
    return new IndexedBranch(i);
  }

  @Override
  public int hashCode() {
    return index;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof IndexedBranch && ((IndexedBranch) obj).index == this.index;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public Node view(@Nonnull Node parent) {
    if (parent instanceof ListNode) {
      ListNode<Object> listNode = (ListNode<Object>) parent;
      Object node = listNode.list.index(this.index).toNullable();
      if (node == null) {
        return null;
      }
      return Branch.wrap(node, listNode.genType.elementType);
    } else if (parent instanceof NonEmptyListNode) {
      NonEmptyListNode<Object> listNode = (NonEmptyListNode<Object>) parent;
      Object node = listNode.list.index(this.index).toNullable();
      if (node == null) {
        return null;
      }
      return Branch.wrap(node, listNode.genType.elementType);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Node set(@Nonnull Node parent, @Nonnull Node child) {
    if (parent instanceof ListNode) {
      ListNode<Node> list = (ListNode<Node>) parent;
      if (list.genType.elementType.isAssignableFrom(child.genType())) {
        if (this.index <= list.list.length()) {
          return new ListNode<>(
              list.list.patch(this.index, 1, List.list(child)),
              list.genType);
        }
      }
    } else if (parent instanceof NonEmptyListNode) {
      NonEmptyListNode<Node> list = (NonEmptyListNode<Node>) parent;
      if (list.genType.elementType.isAssignableFrom(child.genType())) {
        if (this.index <= list.list.length()) {
          return new NonEmptyListNode<>(
              (NonEmptyList<Node>) list.list.patch(this.index, 1, List.list(child)),
              list.genType);
        }
      }
    }
    return parent;
  }

  @Override
  public String toString() {
    return "" + index;
  }
}