/**
 * Copyright 2016 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

'use strict';

exports.default = function findMaxSupers(nodes) {
  // Returns a map A -> B, which is:
  // for each type A, B is the largest type such that any A can be replaced with any B.

  let bounds = new Set(['Program']);

  function addBase(type) {
    switch (type.kind) {
      case 'node':
        bounds.add(type.argument);
        break;
      case 'nullable':
      case 'list':
        addBase(type.argument);
        break;
    }
  }

  for (let node of nodes.values()) {
    node.attributes.forEach(a => {
      addBase(a.type);
    });
  }


  function isEqualOrDescendent(child, parent) {
    if (child === parent) return true;
    let childNode = nodes.get(child);
    if (childNode.parents.indexOf(parent) !== -1) return true;
    return childNode.parents.some(c => isEqualOrDescendent(c, parent));
  }


  function findBoundFor(name) {
    if (bounds.has(name)) return name;
    let node = nodes.get(name);
    let parentBounds = Array.from(new Set(node.parents.map(findBoundFor).filter(x => x !== null)));
    if (parentBounds.length === 0) return name;
    if (parentBounds.length === 1) return parentBounds[0];
    for (let bound of parentBounds) {
      if (parentBounds.every(p => isEqualOrDescendent(bound, p))) {
        return bound;
      }
    }
    return name;
  }

  return new Map(Array.from(nodes.keys()).map(n => [n, findBoundFor(n)]));
};


