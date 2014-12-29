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

package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.path.Branch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;

public class ProjectionTree<E> implements Iterable<E> {
  private static final ProjectionTree<Object> NIL = new Nil<>();
  private final Maybe<E> maybeNode;
  private List<Branch> path;
  private Map<Branch, ProjectionTree<E>> children = null; // leaves do not have a children map

  private ProjectionTree() {
    maybeNode = Maybe.nothing();
  }

  public ProjectionTree(@NotNull List<Branch> path) {
    maybeNode = Maybe.nothing();
    this.path = path;
  }

  public ProjectionTree(@NotNull E node, @NotNull List<Branch> path) {
    maybeNode = Maybe.just(node);
    this.path = path;
  }

  @SuppressWarnings("unchecked")
  @NotNull
  public static <T> ProjectionTree<T> nil() {
    return (ProjectionTree<T>) NIL;
  }

  public boolean isEmpty() {
    return false;
  }

  public final int length() {
    int myLength = (maybeNode.isJust() ? 1 : 0);
    if (children != null) {
      for (ProjectionTree<E> child : children.values()) {
        myLength += child.length();
      }
    }
    return myLength;
  }

  public boolean exists(@NotNull F<E, Boolean> f) {
    if (maybeNode.isJust()) {
      if (f.apply(maybeNode.just())) {
        return true;
      }
    }
    if (children != null) {
      for (ProjectionTree<E> child : children.values()) {
        if (child.exists(f)) {
          return true;
        }
      }
    }
    return false;
  }

  @NotNull
  public Maybe<E> find(@NotNull F<E, Boolean> f) {
    if (maybeNode.isJust()) {
      if (f.apply(maybeNode.just())) {
        return maybeNode;
      }
    }
    if (children != null) {
      for (ProjectionTree<E> child : children.values()) {
        Maybe<E> found = child.find(f);
        if (found.isJust()) {
          return found;
        }
      }
    }
    return Maybe.nothing();
  }

  @NotNull
  ProjectionTree<E> add(@NotNull E node, @NotNull List<Branch> path) {
    return append(new ProjectionTree<>(node, path));
  }

  @NotNull
  ProjectionTree<E> append(@NotNull ProjectionTree<E> other) {
    if (other.isEmpty()) {
      return this;
    }
    // first traverse paths to find least common ancestor path and the immediate branches from that lca
    List<Branch> myPath = path;
    int myLength = myPath.length;
    List<Branch> otherPath = other.path;
    int otherLength = otherPath.length;
    Maybe<Branch> maybeMyBranch = Maybe.nothing();
    while (myLength > otherLength) {
      maybeMyBranch = myPath.maybeHead();
      myPath = ((NonEmptyList<Branch>) myPath).tail();
      myLength--;
    }
    Maybe<Branch> maybeOtherBranch = Maybe.nothing();
    while (otherLength > myLength) {
      maybeOtherBranch = otherPath.maybeHead();
      otherPath = ((NonEmptyList<Branch>) otherPath).tail();
      otherLength--;
    }
    while (myPath.isNotEmpty() && myPath != otherPath) {
      maybeMyBranch = myPath.maybeHead();
      myPath = ((NonEmptyList<Branch>) myPath).tail();
      myLength--;
      maybeOtherBranch = otherPath.maybeHead();
      otherPath = ((NonEmptyList<Branch>) otherPath).tail();
      otherLength--;
    }
    // next handle all preDefaultCases of both, one, or none of the trees (this, other) are that lca
    ProjectionTree<E> result;
    if (maybeMyBranch.isNothing()) {
      if (maybeOtherBranch.isNothing()) {
        // two interior nodes; collapse into one
        if (other.maybeNode.isNothing()) {
          if (children == null) {
            children = other.children;
          } else {
            children.putAll(other.children);
          }
          result = this;
        } else {
          if (other.children == null) {
            other.children = children;
          } else {
            other.children.putAll(children);
          }
          result = other;
        }
      } else {
        // other becomes this one's child
        if (children == null) {
          children = new HashMap<>();
        }
        children.put(maybeOtherBranch.just(), other);
        result = this;
      }
    } else if (maybeOtherBranch.isNothing()) {
      // this becomes other's child
      if (other.children == null) {
        other.children = new HashMap<>();
      }
      other.children.put(maybeMyBranch.just(), this);
      result = other;
    } else {
      // create new interior node
      result = new ProjectionTree<>(myPath);
      result.children = new HashMap<>();
      result.children.put(maybeMyBranch.just(), this);
      result.children.put(maybeOtherBranch.just(), other);
    }
    return result;
  }

  @Override
  public Iterator<E> iterator() {

    return new Iterator<E>() {
      private final Stack<ProjectionTreeIterator> iteratorStack = new Stack<>();

      {
        iteratorStack.push(new ProjectionTreeIterator(ProjectionTree.this));
      }

      private E next = null;

      // fetch the next node, returns null if no more nodes are left
      private E fetchNext() {
        if (iteratorStack.empty()) {
          // no nodes left
          return null;
        }
        ProjectionTreeIterator pti = iteratorStack.peek();
        // iterator is only created after we've iterated over maybeNode
        if (pti.ptIterator == null) {
          // first look at this maybeNode
          if (pti.ptNode.maybeNode.isJust()) {
            if (pti.ptNode.children == null) {
              // no children, we're done with this subtree
              iteratorStack.pop();
            } else {
              // create iterator for children
              pti.ptIterator = pti.ptNode.children.values().iterator();
            }
            return pti.ptNode.maybeNode.just();
          }
        }
        // any preorder maybeNode already processed, do children
        if (pti.ptNode.children == null) {
          // no children, we're done with this subtree
          iteratorStack.pop();
        } else {
          if (pti.ptIterator == null) {
            // create iterator for children
            pti.ptIterator = pti.ptNode.children.values().iterator();
          }
          if (!pti.ptIterator.hasNext()) {
            // no more children, we're done with this subtree
            iteratorStack.pop();
          } else {
            // put next child on stack
            iteratorStack.push(new ProjectionTreeIterator(pti.ptIterator.next()));
          }
        }
        return fetchNext();
      }

      @Override
      public boolean hasNext() {
        if (next != null) {
          return true;
        }
        next = fetchNext();
        return (next != null);
      }

      @Override
      public E next() {
        if (next != null) {
          E lastNext = next;
          next = null;
          return lastNext;
        }
        return fetchNext();
      }

      @Override
      public void remove() {

      }
    };
  }

  private static class Nil<T> extends ProjectionTree<T> {
    private Nil() {
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean exists(@NotNull F<T, Boolean> f) {
      return false;
    }

    @NotNull
    @Override
    public Maybe<T> find(@NotNull F<T, Boolean> f) {
      return Maybe.nothing();
    }

    @NotNull
    @Override
    ProjectionTree<T> add(@NotNull T node, @NotNull List<Branch> path) {
      return new ProjectionTree<>(node, path);
    }

    @NotNull
    @Override
    ProjectionTree<T> append(@NotNull ProjectionTree<T> other) {
      return other;
    }
  }

  private class ProjectionTreeIterator {
    final ProjectionTree<E> ptNode;
    Iterator<ProjectionTree<E>> ptIterator;

    ProjectionTreeIterator(@NotNull ProjectionTree<E> ptNode) {
      this.ptNode = ptNode;
      this.ptIterator = null;
    }
  }
}
